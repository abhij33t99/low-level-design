package service;

import factory.NotificationChannelFactory;
import model.Notification;
import strategy.NotificationChannel;

import java.util.*;

public class NotificationService {

    private NotificationService() {}

    private static final class NotificationServiceHolder {
        public static final NotificationService INSTANCE = new NotificationService();
    }

    public static NotificationService getInstance() {
        return NotificationServiceHolder.INSTANCE;
    }

    // In-memory history: key = orderId|memberId
    private final Map<String, List<Notification>> history = new HashMap<>();

    private String key(String orderId, String memberId) {
        return orderId + "|" + memberId;
    }

    public void sendNotification(NotificationChannel channel, Notification notification) {
        // record to history before sending
        history.computeIfAbsent(key(notification.getOrderId(), notification.getMemberId()), k -> new ArrayList<>())
                .add(notification);
        channel.send(notification);
    }

    // Replay all notifications previously sent for given order and member
    public void replayNotifications(String orderId, String memberId) {
        List<Notification> list = history.getOrDefault(key(orderId, memberId), Collections.emptyList());
        if (list.isEmpty()) {
            System.out.println("[REPLAY] No notifications to replay for order=" + orderId + ", member=" + memberId);
            return;
        }
        for (Notification n : list) {
            NotificationChannel channel = NotificationChannelFactory.getNotificationChannel(n.getChannel());
            channel.send(n);
        }
    }
}
