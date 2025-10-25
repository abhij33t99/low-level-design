package lms.repository;

import lms.model.*;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class InMemoryRepositories {
    // Book and items
    private final Map<String, Book> books = new ConcurrentHashMap<>();
    private final Map<String, BookItem> bookItems = new ConcurrentHashMap<>();

    // Users and cards uniqueness
    private final Map<String, User> users = new ConcurrentHashMap<>();
    private final Set<String> cardNumbers = Collections.synchronizedSet(new HashSet<>());

    // Logs and other entities
    private final Map<String, TransactionEntry> transactions = new ConcurrentHashMap<>();
    private final Map<String, Reservation> reservations = new ConcurrentHashMap<>();
    private final Map<String, Notification> notifications = new ConcurrentHashMap<>();

    // Index: one active reservation per book item
    private final Map<String, String> activeReservationByItem = new ConcurrentHashMap<>(); // bookItemId -> reservationId

    public Optional<Book> getBook(String bookId) { return Optional.ofNullable(books.get(bookId)); }
    public void saveBook(Book book) { books.put(book.getBookId(), book); }

    public Optional<BookItem> getBookItem(String bookItemId) { return Optional.ofNullable(bookItems.get(bookItemId)); }
    public void saveBookItem(BookItem item) { bookItems.put(item.getBookItemId(), item); }

    public void saveUser(User user) {
        // ensure unique card number
        if (cardNumbers.contains(user.getCard().getCardNumber())) {
            throw new IllegalArgumentException("Duplicate library card number: " + user.getCard().getCardNumber());
        }
        cardNumbers.add(user.getCard().getCardNumber());
        users.put(user.getUserId(), user);
    }
    public Optional<User> getUser(String userId) { return Optional.ofNullable(users.get(userId)); }

    public void logTransaction(TransactionEntry entry) { transactions.put(entry.getEntryId(), entry); }
    public List<TransactionEntry> getAllTransactions() { return new ArrayList<>(transactions.values()); }

    public Optional<Reservation> getActiveReservationForItem(String bookItemId) {
        String rid = activeReservationByItem.get(bookItemId);
        if (rid == null) return Optional.empty();
        Reservation r = reservations.get(rid);
        if (r != null && r.isActive()) return Optional.of(r);
        return Optional.empty();
    }
    public void saveReservation(Reservation reservation) {
        reservations.put(reservation.getReservationId(), reservation);
        if (reservation.isActive()) {
            activeReservationByItem.put(reservation.getBookItemId(), reservation.getReservationId());
        }
    }
    public void cancelReservation(String reservationId) {
        Reservation r = reservations.get(reservationId);
        if (r != null && r.isActive()) {
            r.cancel();
            activeReservationByItem.remove(r.getBookItemId());
        }
    }

    public void saveNotification(Notification notification) { notifications.put(notification.getNotificationId(), notification); }
    public List<Notification> getNotificationsForMember(String memberId) {
        return notifications.values().stream().filter(n -> n.getMemberId().equals(memberId)).collect(Collectors.toList());
    }

    // Searches for R14
    public List<Book> findBooksByTitle(String title) {
        String t = title.toLowerCase();
        return books.values().stream().filter(b -> b.getTitle().toLowerCase().contains(t)).collect(Collectors.toList());
    }
    public List<Book> findBooksByAuthor(String author) {
        String a = author.toLowerCase();
        return books.values().stream().filter(b -> b.getAuthor().toLowerCase().contains(a)).collect(Collectors.toList());
    }
    public List<Book> findBooksBySubject(String subject) {
        String s = subject.toLowerCase();
        return books.values().stream().filter(b -> b.getSubject().toLowerCase().contains(s)).collect(Collectors.toList());
    }
    public List<Book> findBooksByPublicationDate(LocalDate date) {
        return books.values().stream().filter(b -> b.getPublicationDate().equals(date)).collect(Collectors.toList());
    }

    // Helpers
    public long countBorrowedByMember(String memberId) {
        return bookItems.values().stream().filter(i -> memberId.equals(i.getCurrentHolderMemberId())).count();
    }

    public List<BookItem> findItemsByBookId(String bookId) {
        return bookItems.values().stream().filter(i -> i.getBookId().equals(bookId)).collect(Collectors.toList());
    }

    public List<BookItem> getAllBookItems() {
        return new ArrayList<>(bookItems.values());
    }
}
