package factory;

import enums.NotificationChannelType;
import strategy.EmailNotificationChannel;
import strategy.NotificationChannel;
import strategy.SMSNotificationChannel;

import java.util.HashMap;
import java.util.Map;

public class NotificationChannelFactory {
    private static final Map<NotificationChannelType, NotificationChannel> notificationChannelMap = new HashMap<>();

    public static NotificationChannel getNotificationChannel(NotificationChannelType type) {
        notificationChannelMap.computeIfAbsent(type, k -> createNotificationChannel(type));
        return notificationChannelMap.get(type);
    }

    private static NotificationChannel createNotificationChannel(NotificationChannelType type) {
        return switch (type){
            case SMS -> new SMSNotificationChannel();
            case EMAIL -> new EmailNotificationChannel();
        };
    }
}
