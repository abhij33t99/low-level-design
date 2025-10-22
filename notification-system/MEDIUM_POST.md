Title: Designing a Real-Time, Extensible Order Notification System (LLD + Code)

TL;DR
- We’ll design and implement a real-time, extensible order notification system for an e-commerce platform.
- Stakeholders subscribe to order events (ORDERED, SHIPPED, DELIVERED) and choose channels (Email, SMS). 
- The system supports subscribe/unsubscribe, channel management, and event replay using latest preferences.
- In-memory data structures keep the solution simple and testable.

1) Problem Statement
Build a notification system that alerts different stakeholders (customers, sellers, logistics partners) about order lifecycle events. The solution must be:
- Extensible: easily add new stakeholder types or channels.
- Configurable: per-user subscriptions and channel preferences.
- Non-blocking capable: designed so async sending can be plugged in.
- Safe for concurrent preference changes (discussed under improvements).

2) Requirements Recap
Event Types
- ORDERED: customer places an order
- SHIPPED: seller ships the order
- DELIVERED: logistics confirms delivery

Stakeholders & Who Gets Notified
- Customer: ORDERED, SHIPPED, DELIVERED
- Seller: ORDERED
- Logistics/Delivery: SHIPPED
- Only stakeholders linked to a specific order are notified.

Notification Channels
- Email
- SMS
- (Easily extendable for Push/App, WhatsApp, etc.)

Functional Requirements
- Subscribe to event types with preferred channels
- Manage subscriptions: add/remove channels, unsubscribe
- Event replay: re-send notifications for a given order and member with latest preferences
- Concurrency-safe and async-friendly design goals

Notification Format
[Timestamp][NOTIFICATION][Order ID][Event Type][Stakeholder ID][Channel] - "<Message>"

Example
[18-09-2025T00:00:01][NOTIFICATION][ORDER-001][ORDER_DELIVERED][CUSTOMER-1][SMS] - "Your package with ID - ORDER-001 has been delivered"

3) Architecture Overview
We use the Observer/Pub-Sub pattern with a thin domain model and strategy-based channels.
- SubscriptionService: manages in-memory subscriptions per order and event type.
- OrderEventService: publishes order events; looks up subscriptions and notifies via Subscription objects.
- Subscription: represents a stakeholder’s subscription; fans out notifications across their configured channels.
- NotificationService: central point to send notifications (via channel strategy) and to store history for replay.
- NotificationChannel (Strategy): interface for different channels (Email, SMS, later Push, etc.).
- Factories: 
  - NotificationChannelFactory: returns singleton-ish channel instances by type.
  - NotificationMessageFactory: builds stakeholder- and event-specific messages.

4) Class Diagram
A PlantUML diagram is included in: notification-system/diagrams/notification-system.puml
Render it via your favorite PlantUML renderer.

5) Code Walkthrough (Key Pieces)

Enums
- OrderEventType
```java
public enum OrderEventType { ORDERED, SHIPPED, DELIVERED }
```
- MemberType
```java
public enum MemberType { CUSTOMER, SELLER, DELIVERY }
```
- NotificationChannelType
```java
public enum NotificationChannelType { EMAIL, SMS }
```

Models
- Member
```java
public record Member(String id, MemberType type, String name) {}
```
- Notification (builder + formatting)
```java
public class Notification {
    private String timestamp, orderId, memberId, message;
    private OrderEventType eventType;
    private NotificationChannelType channel;

    public static class Builder {
        Notification n = new Notification();
        public Builder setTimestamp(String s) { n.timestamp = s; return this; }
        public Builder setOrderId(String s) { n.orderId = s; return this; }
        public Builder setEventType(OrderEventType e) { n.eventType = e; return this; }
        public Builder setMemberId(String s) { n.memberId = s; return this; }
        public Builder setChannel(NotificationChannelType ch) { n.channel = ch; return this; }
        public Builder setMessage(String s) { n.message = s; return this; }
        public Notification build() { return n; }
    }

    public String sendMessage() {
        return String.format("[%s][NOTIFICATION][%s][%s][%s][%s] - \"%s\"",
                timestamp, orderId, eventType, memberId, channel, message);
    }

    // getters used for replay
    public String getOrderId() { return orderId; }
    public String getMemberId() { return memberId; }
    public NotificationChannelType getChannel() { return channel; }
}
```
- Subscription (fan-out across channels)
```java
public record Subscription(String subscriptionId, Member member, List<NotificationChannel> channels) {
    public Subscription(String subscriptionId, Member member, List<NotificationChannel> channels) {
        this.subscriptionId = subscriptionId;
        this.member = member;
        this.channels = new ArrayList<>(channels);
    }

    public void addChannel(NotificationChannel channel) { channels.add(channel); }
    public void removeChannel(NotificationChannel channel) { channels.remove(channel); }

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
```

Factories
- NotificationChannelFactory
```java
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
```
- NotificationMessageFactory (stakeholder-aware messages)
```java
public class NotificationMessageFactory {
    private static final Map<OrderEventType, Map<MemberType, String>> BASE_MESSAGES = new EnumMap<>(OrderEventType.class);
    private static final Map<OrderEventType, String> EVENT_DEFAULTS = new EnumMap<>(OrderEventType.class);

    static {
        EVENT_DEFAULTS.put(OrderEventType.ORDERED, "order has been placed");
        EVENT_DEFAULTS.put(OrderEventType.SHIPPED, "order has been shipped");
        EVENT_DEFAULTS.put(OrderEventType.DELIVERED, "order has been delivered");

        Map<MemberType, String> ordered = new EnumMap<>(MemberType.class);
        ordered.put(MemberType.SELLER, "an item has been placed from your shop");
        ordered.put(MemberType.CUSTOMER, "congrats! your order has been placed");
        ordered.put(MemberType.DELIVERY, "an order has been placed for your pickup");
        BASE_MESSAGES.put(OrderEventType.ORDERED, ordered);

        Map<MemberType, String> shipped = new EnumMap<>(MemberType.class);
        shipped.put(MemberType.SELLER, "order has been shipped to the customer");
        shipped.put(MemberType.CUSTOMER, "good news! your order has been shipped");
        shipped.put(MemberType.DELIVERY, "order is ready for your transit/shipment");
        BASE_MESSAGES.put(OrderEventType.SHIPPED, shipped);

        Map<MemberType, String> delivered = new EnumMap<>(MemberType.class);
        delivered.put(MemberType.SELLER, "order has been delivered to the customer");
        delivered.put(MemberType.CUSTOMER, "hooray! your order has been delivered");
        delivered.put(MemberType.DELIVERY, "order has been delivered; please confirm completion");
        BASE_MESSAGES.put(OrderEventType.DELIVERED, delivered);
    }

    public static String build(Member member, OrderEventType type) {
        MemberType mType = member != null ? member.type() : null;
        return resolveBaseMessage(type, mType);
    }

    private static String resolveBaseMessage(OrderEventType type, MemberType memberType) {
        if (type == null) return "order update";
        Map<MemberType, String> byMember = BASE_MESSAGES.get(type);
        String eventDefault = EVENT_DEFAULTS.getOrDefault(type, "order update");
        if (byMember == null || memberType == null) return eventDefault;
        return byMember.getOrDefault(memberType, eventDefault);
    }
}
```

Channel Strategy
```java
public interface NotificationChannel {
    void send(Notification notification);
    NotificationChannelType getType();
}

public class EmailNotificationChannel implements NotificationChannel {
    public void send(Notification notification) { System.out.println(notification.sendMessage()); }
    public NotificationChannelType getType() { return NotificationChannelType.EMAIL; }
}

public class SMSNotificationChannel implements NotificationChannel {
    public void send(Notification notification) { System.out.println(notification.sendMessage()); }
    public NotificationChannelType getType() { return NotificationChannelType.SMS; }
}
```

Services
- SubscriptionService (manage subs and channels)
```java
public class SubscriptionService {
    private final Map<String, Map<OrderEventType, List<Subscription>>> subscriptions = new HashMap<>();

    public void createSubscription(String orderId, Member member, List<OrderEventType> eventTypes, List<NotificationChannelType> channelTypes) {
        Map<OrderEventType, List<Subscription>> map = subscriptions.computeIfAbsent(orderId, k -> new HashMap<>());
        for (OrderEventType type : eventTypes) {
            List<Subscription> list = map.computeIfAbsent(type, k -> new ArrayList<>());
            List<NotificationChannel> channels = channelTypes.stream()
                    .map(NotificationChannelFactory::getNotificationChannel)
                    .toList();
            String subId = "SUB" + UUID.randomUUID();
            list.add(new Subscription(subId, member, channels));
        }
        subscriptions.put(orderId, map);
    }

    public List<Subscription> getSubscriptions(String orderId, OrderEventType type) {
        return subscriptions.get(orderId).get(type);
    }

    public void unsubscribe(String orderId, Member member, OrderEventType type) {
        Subscription sub = subscriptions.get(orderId).get(type).stream()
                .filter(s -> s.member().equals(member))
                .findFirst().get();
        subscriptions.get(orderId).get(type).remove(sub);
        System.out.println("[SUBSCRIPTION CHANGE] " + member.id() + " unsubscribed from " + type + " for " + orderId);
    }

    public void editChannelInSubscription(String orderId, Member member, NotificationChannelType channelType, boolean add) {
        Map<OrderEventType, List<Subscription>> eventSubsMap = subscriptions.get(orderId);
        for (List<Subscription> list : eventSubsMap.values()) {
            for (Subscription subscription : list) {
                if (subscription.member().equals(member)) {
                    if (add) subscription.addChannel(NotificationChannelFactory.getNotificationChannel(channelType));
                    else subscription.removeChannel(NotificationChannelFactory.getNotificationChannel(channelType));
                }
            }
        }
    }
}
```
- OrderEventService (publish events to subs)
```java
public class OrderEventService {
    private final SubscriptionService subscriptionService = SubscriptionService.getInstance();

    public void publishEvent(String orderId, OrderEventType type) {
        List<Subscription> subscriptions = subscriptionService.getSubscriptions(orderId, type);
        if (subscriptions == null) subscriptions = Collections.emptyList();
        LocalDateTime timestamp = LocalDateTime.now();
        for (Subscription sub : subscriptions) {
            sub.update(orderId, type, timestamp);
        }
    }
}
```
- NotificationService (send + replay)
```java
public class NotificationService {
    // In-memory history: key = orderId|memberId
    private final Map<String, List<Notification>> history = new HashMap<>();
    private String key(String orderId, String memberId) { return orderId + "|" + memberId; }

    public void sendNotification(NotificationChannel channel, Notification notification) {
        history.computeIfAbsent(key(notification.getOrderId(), notification.getMemberId()), k -> new ArrayList<>())
               .add(notification);
        channel.send(notification);
    }

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
```

6) End-to-End Flow
- Subscribe: stakeholders call SubscriptionService.createSubscription(orderId, member, eventTypes, channelTypes).
- Publish Event: OrderEventService.publishEvent(orderId, type) looks up subs and delegates to Subscription.update to fan-out notifications across channels.
- Unsubscribe: SubscriptionService.unsubscribe removes a member’s subscription for an event on an order.
- Edit Channels: SubscriptionService.editChannelInSubscription adds or removes channel preferences.
- Replay: NotificationService.replayNotifications(orderId, memberId) re-sends previously sent notifications for that pair.

7) Running the Demo
A simple client demonstrates the flow (NotificationClient.java):
```java
public static void main(String... args) {
    SubscriptionService subscriptionService = SubscriptionService.getInstance();
    OrderEventService orderEventService = OrderEventService.getInstance();
    NotificationService notificationService = NotificationService.getInstance();

    Member alice = new Member("customer1", MemberType.CUSTOMER, "Alice");
    Member seller = new Member("seller1", MemberType.SELLER, "Book Seller");

    subscriptionService.createSubscription("order1", alice,
        List.of(OrderEventType.ORDERED, OrderEventType.SHIPPED, OrderEventType.DELIVERED),
        List.of(NotificationChannelType.EMAIL));

    subscriptionService.createSubscription("order1", seller,
        List.of(OrderEventType.ORDERED), List.of(NotificationChannelType.EMAIL));

    orderEventService.publishEvent("order1", OrderEventType.ORDERED);
    subscriptionService.unsubscribe("order1", alice, OrderEventType.SHIPPED);
    orderEventService.publishEvent("order1", OrderEventType.SHIPPED);
    subscriptionService.editChannelInSubscription("order1", alice, NotificationChannelType.SMS, true);
    orderEventService.publishEvent("order1", OrderEventType.DELIVERED);

    System.out.println("Replaying notifications for order 1 and alice");
    notificationService.replayNotifications("order1", "customer1");
}
```
Expected console output will look like (timestamps and exact messages may vary):
- [..][NOTIFICATION][order1][ORDERED][customer1][EMAIL] - "congrats! your order has been placed"
- [..][NOTIFICATION][order1][ORDERED][seller1][EMAIL] - "an item has been placed from your shop"
- [SUBSCRIPTION CHANGE] customer1 unsubscribed from SHIPPED for order1
- [..][NOTIFICATION][order1][DELIVERED][customer1][EMAIL] - "hooray! your order has been delivered"
- [..][NOTIFICATION][order1][DELIVERED][customer1][SMS] - "hooray! your order has been delivered"
- Replaying notifications for order 1 and alice
- [..][NOTIFICATION][order1][ORDERED][customer1][EMAIL] - "congrats! your order has been placed"
- [..][NOTIFICATION][order1][ORDERED][seller1][EMAIL] - "an item has been placed from your shop"
- [..][NOTIFICATION][order1][DELIVERED][customer1][EMAIL] - "hooray! your order has been delivered"
- [..][NOTIFICATION][order1][DELIVERED][customer1][SMS] - "hooray! your order has been delivered"

8) Extensibility
Add a new channel
- Create a class PushNotificationChannel implementing NotificationChannel.
- Add enum value in NotificationChannelType (e.g., PUSH).
- Extend NotificationChannelFactory.switch to return the new channel instance.

Add a new stakeholder type
- Add enum value in MemberType.
- Update NotificationMessageFactory BASE_MESSAGES for each event (optional default otherwise).

Add a new event type
- Add in OrderEventType.
- Update NotificationMessageFactory defaults and BASE_MESSAGES.
- Start publishing it via OrderEventService.publishEvent.

9) Concurrency and Async Notes
- Current in-memory maps are not thread-safe if mutated concurrently. For true concurrency:
  - Use ConcurrentHashMap and copy-on-write lists or explicit synchronization around subscription changes.
  - Consider an immutable snapshot approach per publish to ensure consistent fan-out.
- Async sending:
  - Wrap NotificationService.sendNotification with a thread pool / executor to dispatch channel.send(notification) asynchronously.
  - For production scale, a message queue (Kafka/RabbitMQ) + consumer workers would decouple event publishing from sending.

10) Testing Ideas
- Unit test SubscriptionService.createSubscription/unsubscribe/editChannelInSubscription behaviors.
- Unit test NotificationService.replayNotifications to ensure it re-sends the correct history.
- Contract tests per channel implementation to validate output format.

11) What’s Next
- Introduce persistence (optional) for durable history and replay across restarts.
- Implement idempotency keys to avoid duplicate sends on retries.
- Add rate limiting and backoff strategies per channel.
- Add templating engine for richer messages and localization.

Credits
This article corresponds to the code in this repository under notification-system/. The README.md in the same folder also contains the raw requirements and diagram notes.