package lms.model;

import lms.enums.TransactionType;

import java.time.LocalDate;
import java.util.Objects;

// R1, R10: complete log of transactions with who and when
public class TransactionEntry {
    private final String entryId; // unique log id
    private final TransactionType type;
    private final String bookItemId;
    private final String actorUserId; // who performed
    private final LocalDate date; // date of action

    public TransactionEntry(String entryId, TransactionType type, String bookItemId, String actorUserId, LocalDate date) {
        this.entryId = Objects.requireNonNull(entryId, "entryId");
        this.type = Objects.requireNonNull(type, "type");
        this.bookItemId = Objects.requireNonNull(bookItemId, "bookItemId");
        this.actorUserId = Objects.requireNonNull(actorUserId, "actorUserId");
        this.date = Objects.requireNonNull(date, "date");
    }

    public String getEntryId() { return entryId; }
    public TransactionType getType() { return type; }
    public String getBookItemId() { return bookItemId; }
    public String getActorUserId() { return actorUserId; }
    public LocalDate getDate() { return date; }

    @Override
    public String toString() {
        return "TransactionEntry{" +
                "entryId='" + entryId + '\'' +
                ", type=" + type +
                ", bookItemId='" + bookItemId + '\'' +
                ", actorUserId='" + actorUserId + '\'' +
                ", date=" + date +
                '}';
    }
}
