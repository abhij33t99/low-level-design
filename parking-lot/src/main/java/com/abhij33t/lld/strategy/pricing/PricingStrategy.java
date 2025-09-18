package com.abhij33t.lld.strategy.pricing;

import com.abhij33t.lld.enums.VehicleType;

import java.time.LocalDateTime;

public interface PricingStrategy {
    double calculatePrice(VehicleType type, LocalDateTime entryTime, LocalDateTime exitTime);
}
