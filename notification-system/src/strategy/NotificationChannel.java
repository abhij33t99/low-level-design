package strategy;

import enums.NotificationChannelType;
import model.Notification;

public interface NotificationChannel {
    void send(Notification notification);
    NotificationChannelType getType();
}
