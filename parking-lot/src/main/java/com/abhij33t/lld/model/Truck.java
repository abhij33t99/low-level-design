package com.abhij33t.lld.model;

import com.abhij33t.lld.enums.VehicleType;

public class Truck extends Vehicle{
    public Truck(String number) {
        super(number, VehicleType.TRUCK);
    }
}
