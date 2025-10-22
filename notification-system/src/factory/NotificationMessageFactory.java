package factory;

import enums.MemberType;
import enums.OrderEventType;
import model.Member;

import java.util.EnumMap;
import java.util.Map;

public class NotificationMessageFactory {
    private static final Map<OrderEventType, Map<MemberType, String>> BASE_MESSAGES = new EnumMap<>(OrderEventType.class);
    private static final Map<OrderEventType, String> EVENT_DEFAULTS = new EnumMap<>(OrderEventType.class);

    static {
        // Defaults per event
        EVENT_DEFAULTS.put(OrderEventType.ORDERED, "order has been placed");
        EVENT_DEFAULTS.put(OrderEventType.SHIPPED, "order has been shipped");
        EVENT_DEFAULTS.put(OrderEventType.DELIVERED, "order has been delivered");

        // ORDERED messages
        Map<MemberType, String> ordered = new EnumMap<>(MemberType.class);
        ordered.put(MemberType.SELLER, "an item has been placed from your shop");
        ordered.put(MemberType.CUSTOMER, "congrats! your order has been placed");
        ordered.put(MemberType.DELIVERY, "an order has been placed for your pickup");
        BASE_MESSAGES.put(OrderEventType.ORDERED, ordered);

        // SHIPPED messages
        Map<MemberType, String> shipped = new EnumMap<>(MemberType.class);
        shipped.put(MemberType.SELLER, "order has been shipped to the customer");
        shipped.put(MemberType.CUSTOMER, "good news! your order has been shipped");
        shipped.put(MemberType.DELIVERY, "order is ready for your transit/shipment");
        BASE_MESSAGES.put(OrderEventType.SHIPPED, shipped);

        // DELIVERED messages
        Map<MemberType, String> delivered = new EnumMap<>(MemberType.class);
        delivered.put(MemberType.SELLER, "order has been delivered to the customer");
        delivered.put(MemberType.CUSTOMER, "hooray! your order has been delivered");
        delivered.put(MemberType.DELIVERY, "order has been delivered; please confirm completion");
        BASE_MESSAGES.put(OrderEventType.DELIVERED, delivered);
    }

    private NotificationMessageFactory() {}

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
