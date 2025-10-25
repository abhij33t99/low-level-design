package lms.model;

import java.time.LocalDate;
import java.util.Objects;

// R2, R3: unique id, detailed info incl. rack/location
public class Book {
    private final String bookId; // logical book id (e.g., work/title level)
    private final String isbn;
    private final String title;
    private final String author;
    private final String subject;
    private final LocalDate publicationDate;
    private final String rackLocation; // e.g., Rack A3, Shelf 2

    public Book(String bookId, String isbn, String title, String author, String subject, LocalDate publicationDate, String rackLocation) {
        this.bookId = Objects.requireNonNull(bookId, "bookId");
        this.isbn = Objects.requireNonNull(isbn, "isbn");
        this.title = Objects.requireNonNull(title, "title");
        this.author = Objects.requireNonNull(author, "author");
        this.subject = Objects.requireNonNull(subject, "subject");
        this.publicationDate = Objects.requireNonNull(publicationDate, "publicationDate");
        this.rackLocation = Objects.requireNonNull(rackLocation, "rackLocation");
    }

    public String getBookId() { return bookId; }
    public String getIsbn() { return isbn; }
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public String getSubject() { return subject; }
    public LocalDate getPublicationDate() { return publicationDate; }
    public String getRackLocation() { return rackLocation; }
}
