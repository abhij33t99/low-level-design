package strategy;

import enums.NotificationChannelType;
import model.Notification;

public class EmailNotificationChannel implements NotificationChannel{
    @Override
    public void send(Notification notification) {
        System.out.println(notification.sendMessage());
    }

    @Override
    public NotificationChannelType getType() {
        return NotificationChannelType.EMAIL;
    }
}
