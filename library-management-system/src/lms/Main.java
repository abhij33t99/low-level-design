package lms;

import lms.model.*;
import lms.repository.InMemoryRepositories;
import lms.service.LibraryService;
import lms.service.NotificationService;

import java.time.LocalDate;

public class Main {
    public static void main(String[] args) {
        // Setup
        InMemoryRepositories repo = new InMemoryRepositories();
        NotificationService notificationService = new NotificationService(repo);
        LibraryService library = new LibraryService(repo, notificationService);

        // Create users
        Librarian librarian = new Librarian("lib-1", "Alice", new LibraryCard("CARD-L-001"));
        Member member1 = new Member("mem-1", "Bob", new LibraryCard("CARD-M-001"));
        Member member2 = new Member("mem-2", "Charlie", new LibraryCard("CARD-M-002"));
        library.registerUser(librarian);
        library.registerUser(member1);
        library.registerUser(member2);

        // Add a book and a single copy
        Book book = new Book("book-1", "978-0321765723", "The C++ Programming Language", "Bjarne Stroustrup", "Programming", LocalDate.of(2013, 5, 9), "Rack B2-Shelf 3");
        library.addBook(book);
        BookItem bookItem = new BookItem("item-1", book.getBookId());
        library.addBookItem(bookItem);

        System.out.println("--- SCENARIO START ---");

        // 1. Member1 issues the book
        System.out.println("\n1. Member1 issues the book.");
        library.issueBookItem(member1.getUserId(), bookItem.getBookItemId(), librarian.getUserId());
        System.out.println("Book status: " + repo.getBookItem(bookItem.getBookItemId()).get().getStatus());

        // 2. Member2 tries to reserve the same book (and is put on hold)
        System.out.println("\n2. Member2 reserves the same book and is put on hold.");
        library.reserveBookItem(member2.getUserId(), bookItem.getBookItemId());
        System.out.println("Book status: " + repo.getBookItem(bookItem.getBookItemId()).get().getStatus());

        // 3. Member1 returns the book
        System.out.println("\n3. Member1 returns the book.");
        library.returnBookItem(member1.getUserId(), bookItem.getBookItemId(), librarian.getUserId());
        System.out.println("Book status: " + repo.getBookItem(bookItem.getBookItemId()).get().getStatus());

        // 4. Check notifications for Member2
        System.out.println("\n4. Checking notifications for Member2.");
        notificationService.getNotificationsForMember(member2.getUserId())
                .forEach(n -> System.out.println("Notification for Member2: " + n.getMessage()));

        // 5. Member2 issues the reserved book
        System.out.println("\n5. Member2 issues the reserved book.");
        library.issueBookItem(member2.getUserId(), bookItem.getBookItemId(), librarian.getUserId());
        System.out.println("Book status: " + repo.getBookItem(bookItem.getBookItemId()).get().getStatus());

        // 6. Member2 tries to renew the book
        System.out.println("\n6. Member2 tries to renew the book.");
        library.renewBookItem(member2.getUserId(), bookItem.getBookItemId());
        System.out.println("Due date after renewal: " + repo.getBookItem(bookItem.getBookItemId()).get().getDueDate());

        System.out.println("\n--- SCENARIO END ---");

        // Print all transactions
        System.out.println("\n--- ALL TRANSACTIONS ---");
        repo.getAllTransactions().forEach(t -> System.out.println(t));
    }
}
