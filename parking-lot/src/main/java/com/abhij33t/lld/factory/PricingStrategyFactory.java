package com.abhij33t.lld.factory;

import com.abhij33t.lld.enums.PricingStrategyType;
import com.abhij33t.lld.strategy.pricing.EventBasedPricing;
import com.abhij33t.lld.strategy.pricing.PricingStrategy;
import com.abhij33t.lld.strategy.pricing.TimeBasedPricing;

public class PricingStrategyFactory {
    public static PricingStrategy get(PricingStrategyType type) {
        return switch (type) {
            case TIME_BASED -> new TimeBasedPricing();
            case EVENT_BASED -> new EventBasedPricing();
        };
    }
}
