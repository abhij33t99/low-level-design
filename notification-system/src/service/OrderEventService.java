package service;

import enums.OrderEventType;
import enums.MemberType;
import model.Subscription;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

public class OrderEventService {

    private OrderEventService() {}

    private static class OrderEventServiceHolder {
        private static final OrderEventService INSTANCE = new OrderEventService();
    }

    public static OrderEventService getInstance() {
        return OrderEventServiceHolder.INSTANCE;
    }

    private final SubscriptionService subscriptionService = SubscriptionService.getInstance();

    public void publishEvent(String orderId, OrderEventType type) {
        List<Subscription> subscriptions = subscriptionService.getSubscriptions(orderId, type);
        if (subscriptions == null) {
            subscriptions = Collections.emptyList();
        }
        LocalDateTime timestamp = LocalDateTime.now();
        for (Subscription sub : subscriptions) {
            sub.update(orderId, type, timestamp);
        }
    }

    // Currently unused helper; kept for potential future use
    private List<OrderEventType> getStandardEventsForStakeholder(MemberType type) {
        return switch (type) {
            case SELLER ->  List.of(OrderEventType.ORDERED);
            case CUSTOMER -> List.of(OrderEventType.ORDERED, OrderEventType.SHIPPED, OrderEventType.DELIVERED);
            case DELIVERY -> null;
        };
    }
}
