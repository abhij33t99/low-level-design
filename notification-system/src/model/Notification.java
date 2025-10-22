package model;

import enums.NotificationChannelType;
import enums.OrderEventType;

public class Notification {
    private String timestamp, orderId, memberId, message;
    private OrderEventType eventType;
    private NotificationChannelType channel;

    private Notification() {
    }

    public static class Builder {
        Notification n = new Notification();

        public Builder setTimestamp(String s) {
            n.timestamp = s;
            return this;
        }

        public Builder setOrderId(String s) {
            n.orderId = s;
            return this;
        }

        public Builder setEventType(OrderEventType e) {
            n.eventType = e;
            return this;
        }

        public Builder setMemberId(String s) {
            n.memberId = s;
            return this;
        }

        public Builder setChannel(NotificationChannelType ch) {
            n.channel = ch;
            return this;
        }

        public Builder setMessage(String s) {
            n.message = s;
            return this;
        }

        public Notification build() {
            return n;
        }
    }

    public String sendMessage() {
        return String.format("[%s][NOTIFICATION][%s][%s][%s][%s] - \"%s\"", timestamp, orderId, eventType, memberId, channel, message);
    }

    // Getters added for replay functionality
    public String getOrderId() { return orderId; }
    public String getMemberId() { return memberId; }
    public NotificationChannelType getChannel() { return channel; }
}