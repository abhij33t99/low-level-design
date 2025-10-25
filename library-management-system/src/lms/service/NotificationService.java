package lms.service;

import lms.model.Notification;
import lms.repository.InMemoryRepositories;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public class NotificationService {
    private final InMemoryRepositories repo;

    public NotificationService(InMemoryRepositories repo) {
        this.repo = repo;
    }

    public void notifyMember(String memberId, String message) {
        Notification n = new Notification(UUID.randomUUID().toString(), memberId, message, LocalDate.now());
        repo.saveNotification(n);
    }

    public List<Notification> getNotificationsForMember(String memberId) {
        return repo.getNotificationsForMember(memberId);
    }
}
