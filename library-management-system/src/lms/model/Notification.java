package lms.model;

import java.time.LocalDate;

public class Notification {
    private final String notificationId;
    private final String memberId;
    private final String message;
    private final LocalDate date;

    public Notification(String notificationId, String memberId, String message, LocalDate date) {
        this.notificationId = notificationId;
        this.memberId = memberId;
        this.message = message;
        this.date = date;
    }

    public String getNotificationId() { return notificationId; }
    public String getMemberId() { return memberId; }
    public String getMessage() { return message; }
    public LocalDate getDate() { return date; }
}
