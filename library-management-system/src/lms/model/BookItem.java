package lms.model;

import lms.enums.BookStatus;

import java.time.LocalDate;
import java.util.Objects;

// R4: each physical copy is a distinct book item with unique ID
public class BookItem {
    private final String bookItemId; // unique per physical copy
    private final String bookId; // reference to logical Book
    private BookStatus status;
    private String currentHolderMemberId; // nullable
    private LocalDate dueDate; // nullable when not borrowed
    private int renewalsCount; // policy tracking

    public BookItem(String bookItemId, String bookId) {
        this.bookItemId = Objects.requireNonNull(bookItemId, "bookItemId");
        this.bookId = Objects.requireNonNull(bookId, "bookId");
        this.status = BookStatus.AVAILABLE;
        this.renewalsCount = 0;
    }

    public String getBookItemId() { return bookItemId; }
    public String getBookId() { return bookId; }
    public BookStatus getStatus() { return status; }
    public void setStatus(BookStatus status) { this.status = status; }
    public String getCurrentHolderMemberId() { return currentHolderMemberId; }
    public void setCurrentHolderMemberId(String id) { this.currentHolderMemberId = id; }
    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }
    public int getRenewalsCount() { return renewalsCount; }
    public void incrementRenewals() { this.renewalsCount++; }
    public void resetRenewals() { this.renewalsCount = 0; }
}
