package model;

import factory.NotificationMessageFactory;
import strategy.NotificationChannel;
import service.NotificationService;
import enums.OrderEventType;
import java.time.LocalDateTime;

import java.util.ArrayList;
import java.util.List;

public record Subscription(String subscriptionId, Member member, List<NotificationChannel> channels) {

    public Subscription(String subscriptionId, Member member, List<NotificationChannel> channels) {
        this.subscriptionId = subscriptionId;
        this.member = member;
        this.channels = new ArrayList<>(channels);
    }

    public void addChannel(NotificationChannel channel) {
        channels.add(channel);
    }

    public void removeChannel(NotificationChannel channel) {
        channels.remove(channel);
    }

    public void update(String orderId, OrderEventType type, LocalDateTime timestamp) {
        NotificationService service = NotificationService.getInstance();
        for (NotificationChannel channel : channels) {
            String message = NotificationMessageFactory.build(member, type);
            Notification notification = new Notification.Builder()
                    .setChannel(channel.getType())
                    .setEventType(type)
                    .setOrderId(orderId)
                    .setMemberId(member.id())
                    .setTimestamp(timestamp.toString())
                    .setMessage(message)
                    .build();

            service.sendNotification(channel, notification);
        }
    }
}
