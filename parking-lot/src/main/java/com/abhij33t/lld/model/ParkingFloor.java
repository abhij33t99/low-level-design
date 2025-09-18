package com.abhij33t.lld.model;

import com.abhij33t.lld.enums.VehicleType;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public class ParkingFloor {
    private final String id;
    private final Map<String, ParkingSpot> spots = new HashMap<>();

    public ParkingFloor(String id) {
        this.id = id;
    }

    public void addSpot(ParkingSpot spot) {
        spots.put(spot.getId(), spot);
    }

    public ParkingSpot findAvailableSpot(VehicleType type) {
        for (ParkingSpot spot : spots.values()) {
            if (spot.getAllowedType() == type && spot.tryOccupy())
                return spot;
        }
        return null;
    }
}
