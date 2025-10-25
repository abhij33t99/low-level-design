package lms.service;

import lms.enums.BookStatus;
import lms.enums.TransactionType;
import lms.model.*;
import lms.repository.InMemoryRepositories;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class LibraryService {
    public static final int MAX_BORROWED_BOOKS = 10; // R7
    public static final int LOAN_DAYS = 15; // R8
    public static final int MAX_RENEWALS = 1; // policy for R11

    private final InMemoryRepositories repo;
    private final NotificationService notificationService;
    private final SearchService searchService;

    public LibraryService(InMemoryRepositories repo, NotificationService notificationService) {
        this.repo = repo;
        this.notificationService = notificationService;
        this.searchService = new SearchService(repo);
    }

    // Expose delegates for explicit use if needed
    public SearchService getSearchService() { return searchService; }

    // Admin operations
    public void addBook(Book book) { repo.saveBook(book); }
    public void addBookItem(BookItem item) { repo.saveBookItem(item); }
    public void registerUser(User user) { repo.saveUser(user); }

    // R14 Search delegates to SearchService; kept here for convenience/back-compat
    public List<Book> searchByTitle(String title) { return searchService.searchByTitle(title); }
    public List<Book> searchByAuthor(String author) { return searchService.searchByAuthor(author); }
    public List<Book> searchBySubject(String subject) { return searchService.searchBySubject(subject); }
    public List<Book> searchByPublicationDate(LocalDate date) { return searchService.searchByPublicationDate(date); }

    public synchronized void reserveBookItem(String memberId, String bookItemId) {
        BookItem item = requireBookItem(bookItemId);

        // R9: only one member can reserve each book item at a time
        Optional<Reservation> existing = repo.getActiveReservationForItem(bookItemId);
        if (existing.isPresent()) {
            throw new IllegalStateException("Book item is already reserved by another member");
        }

        if (item.getStatus() == BookStatus.BORROWED) {
            System.out.println("Book is currently issued. You have been placed on hold.");
        } else if (item.getStatus() != BookStatus.AVAILABLE) {
            throw new IllegalStateException("Book item is not available for reservation.");
        }

        String reservationId = UUID.randomUUID().toString();
        Reservation r = new Reservation(reservationId, bookItemId, memberId, LocalDate.now());
        repo.saveReservation(r);

        if (item.getStatus() == BookStatus.AVAILABLE) {
            item.setStatus(BookStatus.RESERVED);
        }

        // R1, R10: log
        repo.logTransaction(new TransactionEntry(UUID.randomUUID().toString(), TransactionType.RESERVE, bookItemId, memberId, LocalDate.now()));
    }

    // R7, R8, R10, R11
    public synchronized void issueBookItem(String memberId, String bookItemId, String actorUserId) {
        User actor = requireUser(actorUserId);
        if (!(actor instanceof Librarian)) {
            throw new IllegalArgumentException("Only librarian can issue books");
        }
        BookItem item = requireBookItem(bookItemId);

        // If reserved, only the reserving member can borrow
        Optional<Reservation> activeRes = repo.getActiveReservationForItem(bookItemId);
        if (activeRes.isPresent() && !activeRes.get().getMemberId().equals(memberId)) {
            throw new IllegalStateException("This book item is reserved by another member");
        }

        if (item.getStatus() != BookStatus.AVAILABLE && item.getStatus() != BookStatus.RESERVED) {
            throw new IllegalStateException("Book item is not available");
        }
        long borrowedCount = repo.countBorrowedByMember(memberId);
        if (borrowedCount >= MAX_BORROWED_BOOKS) {
            throw new IllegalStateException("Borrowing limit exceeded (max " + MAX_BORROWED_BOOKS + ")");
        }
        item.setStatus(BookStatus.BORROWED);
        item.setCurrentHolderMemberId(memberId);
        item.setDueDate(LocalDate.now().plusDays(LOAN_DAYS));
        item.resetRenewals();
        repo.saveBookItem(item);

        // Clear reservation if any (member who reserved gets it now)
        activeRes.ifPresent(r -> repo.cancelReservation(r.getReservationId()));

        // Log borrow
        repo.logTransaction(new TransactionEntry(UUID.randomUUID().toString(), TransactionType.BORROW, bookItemId, actorUserId, LocalDate.now()));
    }

    public synchronized void returnBookItem(String memberId, String bookItemId, String actorUserId) {
        User actor = requireUser(actorUserId);
        if (!(actor instanceof Librarian)) {
            throw new IllegalArgumentException("Only librarian can accept returns");
        }
        BookItem item = requireBookItem(bookItemId);
        if (!memberId.equals(item.getCurrentHolderMemberId())) {
            throw new IllegalStateException("This member did not borrow this item");
        }

        // Log return
        repo.logTransaction(new TransactionEntry(UUID.randomUUID().toString(), TransactionType.RETURN, bookItemId, actorUserId, LocalDate.now()));

        // Make the book available first
        item.setCurrentHolderMemberId(null);
        item.setDueDate(null);
        item.resetRenewals();

        // Check for reservations
        Optional<Reservation> next = repo.getActiveReservationForItem(bookItemId);
        if (next.isPresent()) {
            Reservation reservation = next.get();
            String nextMemberId = reservation.getMemberId();

            // Set status to RESERVED for the next member
            item.setStatus(BookStatus.RESERVED);
            repo.saveBookItem(item);

            // Notify the member that the book is now available for them to issue
            notificationService.notifyMember(nextMemberId, "Reserved book item " + bookItemId + " is now available for you to issue.");
        } else {
            // If no reservations, make the book available
            item.setStatus(BookStatus.AVAILABLE);
            repo.saveBookItem(item);
        }
    }

    // R11 Renew (policy: max MAX_RENEWALS, cannot renew if another member has a reservation on that item)
    public synchronized void renewBookItem(String memberId, String bookItemId) {
        BookItem item = requireBookItem(bookItemId);
        if (!memberId.equals(item.getCurrentHolderMemberId())) {
            throw new IllegalStateException("Only the current holder can renew");
        }
        if (item.getRenewalsCount() >= MAX_RENEWALS) {
            throw new IllegalStateException("Renewal limit reached");
        }
        // cannot renew if someone reserved the item
        Optional<Reservation> activeRes = repo.getActiveReservationForItem(bookItemId);
        if (activeRes.isPresent()) {
            throw new IllegalStateException("Cannot renew: another member has reserved this item");
        }
        LocalDate newDue = (item.getDueDate() == null ? LocalDate.now() : item.getDueDate()).plusDays(LOAN_DAYS);
        item.setDueDate(newDue);
        item.incrementRenewals();
        repo.saveBookItem(item);

        // Log renew
        repo.logTransaction(new TransactionEntry(UUID.randomUUID().toString(), TransactionType.RENEW, bookItemId, memberId, LocalDate.now()));
    }

    // Overdue processing (R12): notify members if not returned by due date
    public void processOverdues() {
        LocalDate today = LocalDate.now();
        for (BookItem item : repo.getAllBookItems()) {
            if (item.getStatus() == BookStatus.BORROWED && item.getDueDate() != null && item.getDueDate().isBefore(today)) {
                String holder = item.getCurrentHolderMemberId();
                if (holder != null) {
                    notificationService.notifyMember(holder, "Book item " + item.getBookItemId() + " is overdue. Due date was " + item.getDueDate());
                }
            }
        }
    }

    private BookItem requireBookItem(String bookItemId) {
        Optional<BookItem> opt = repo.getBookItem(bookItemId);
        if (opt.isEmpty()) throw new IllegalArgumentException("Book item not found: " + bookItemId);
        return opt.get();
    }

    private User requireUser(String userId) {
        Optional<User> opt = repo.getUser(userId);
        if (opt.isEmpty()) throw new IllegalArgumentException("User not found: " + userId);
        return opt.get();
    }
}
