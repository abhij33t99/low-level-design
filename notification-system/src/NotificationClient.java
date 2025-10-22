import enums.NotificationChannelType;
import enums.OrderEventType;
import enums.MemberType;
import model.Member;
import service.OrderEventService;
import service.SubscriptionService;
import service.NotificationService;
import java.util.List;

public static void main(String... args) {

    SubscriptionService subscriptionService = SubscriptionService.getInstance();
    OrderEventService orderEventService = OrderEventService.getInstance();
    NotificationService notificationService = NotificationService.getInstance();

    Member alice = new Member(
            "customer1",
            MemberType.CUSTOMER,
            "Alice"
    );

    Member seller = new Member(
            "seller1",
            MemberType.SELLER,
            "Book Seller"
    );

    subscriptionService.createSubscription(
            "order1",
            alice,
            List.of(OrderEventType.ORDERED, OrderEventType.SHIPPED, OrderEventType.DELIVERED),
            List.of(NotificationChannelType.EMAIL)
    );

    subscriptionService.createSubscription(
            "order1",
            seller,
            List.of(OrderEventType.ORDERED),
            List.of(NotificationChannelType.EMAIL)
    );

    orderEventService.publishEvent("order1", OrderEventType.ORDERED);
    subscriptionService.unsubscribe("order1", alice, OrderEventType.SHIPPED);
    orderEventService.publishEvent("order1", OrderEventType.SHIPPED);
    subscriptionService.editChannelInSubscription("order1", alice, NotificationChannelType.SMS, true);
    orderEventService.publishEvent("order1", OrderEventType.DELIVERED);

    // Replay notifications for order1 and customer1 (Alice)
    System.out.println("Replaying notifications for order 1 and alice");
    notificationService.replayNotifications("order1", "customer1");
}