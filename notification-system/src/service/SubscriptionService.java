package service;

import enums.NotificationChannelType;
import enums.OrderEventType;
import strategy.NotificationChannel;
import factory.NotificationChannelFactory;
import model.Member;
import model.Subscription;

import java.util.*;

public class SubscriptionService {

    private final Map<String, Map<OrderEventType, List<Subscription>>> subscriptions;

    private SubscriptionService() {
        subscriptions = new HashMap<>();
    }

    private static class SubscriptionServiceHolder {
        public static final SubscriptionService INSTANCE = new SubscriptionService();
    }

    public static SubscriptionService getInstance() {
        return SubscriptionServiceHolder.INSTANCE;
    }

    public void createSubscription(String orderId, Member member, List<OrderEventType> eventTypes, List<NotificationChannelType> channelTypes) {
        Map<OrderEventType, List<Subscription>> map = subscriptions.computeIfAbsent(orderId, k -> new HashMap<>());
        for (OrderEventType type : eventTypes) {
            List<Subscription> subscriptionListByEventType = map.computeIfAbsent(type, k -> new ArrayList<>());
            List<NotificationChannel> channels = channelTypes.stream()
                    .map(NotificationChannelFactory::getNotificationChannel)
                    .toList();
            String subId = "SUB" + UUID.randomUUID();
            Subscription subscription = new Subscription(subId, member, channels);
            subscriptionListByEventType.add(subscription);
        }

        subscriptions.put(orderId, map);
    }

    public List<Subscription> getSubscriptions(String orderId, OrderEventType type) {
        return subscriptions.get(orderId).get(type);
    }

    public void unsubscribe(String orderId, Member member, OrderEventType type) {
        Subscription subscriptionToRemove = subscriptions.get(orderId).get(type).stream()
                .filter(sub -> sub.member().equals(member))
                .findFirst().get();
        subscriptions.get(orderId).get(type).remove(subscriptionToRemove);
        System.out.println("[SUBSCRIPTION CHANGE] " + member.id() + " unsubscribed from " + type + " for " + orderId);
    }

    public void editChannelInSubscription(String orderId, Member member, NotificationChannelType channelType, boolean add) {
        Map<OrderEventType, List<Subscription>> eventSubsMap = subscriptions.get(orderId);
        for (List<Subscription> subscriptionList : eventSubsMap.values()) {
            for (Subscription subscription : subscriptionList) {
                if (subscription.member().equals(member)) {
                    if (add)
                        subscription.addChannel(NotificationChannelFactory.getNotificationChannel(channelType));
                    else {
                        subscription.removeChannel(NotificationChannelFactory.getNotificationChannel(channelType));
                    }
                }
            }
        }
    }


}
