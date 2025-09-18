package com.abhij33t.lld.strategy.pricing;

import com.abhij33t.lld.enums.VehicleType;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDateTime;

public class EventBasedPricing implements PricingStrategy{

    @Override
    public double calculatePrice(VehicleType type, LocalDateTime entryTime, LocalDateTime exitTime) {
        double base = getBasePrice(type, entryTime);
        long minutes = Duration.between(entryTime, exitTime).toMinutes();
        return base * ((double) minutes /60);
    }

    private double getBasePrice(VehicleType type, LocalDateTime entryTime) {
        double base = 1;
        switch (type) {
            case BIKE -> base = 10;
            case CAR -> base = 20;
            case TRUCK -> base = 30;
        }

        //increased price for weekends
        DayOfWeek dayOfWeek = entryTime.getDayOfWeek();
        if (dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY)
            base = base * 2;

        return base;
    }
}
