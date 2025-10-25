package lms.model;

import java.time.LocalDate;
import java.util.Objects;

// R9, R13: reservation per book item by a member
public class Reservation {
    private final String reservationId; // unique
    private final String bookItemId; // one member can reserve a given item at a time
    private final String memberId;
    private final LocalDate date;
    private boolean active;

    public Reservation(String reservationId, String bookItemId, String memberId, LocalDate date) {
        this.reservationId = Objects.requireNonNull(reservationId, "reservationId");
        this.bookItemId = Objects.requireNonNull(bookItemId, "bookItemId");
        this.memberId = Objects.requireNonNull(memberId, "memberId");
        this.date = Objects.requireNonNull(date, "date");
        this.active = true;
    }

    public String getReservationId() { return reservationId; }
    public String getBookItemId() { return bookItemId; }
    public String getMemberId() { return memberId; }
    public LocalDate getDate() { return date; }
    public boolean isActive() { return active; }
    public void cancel() { this.active = false; }
}
