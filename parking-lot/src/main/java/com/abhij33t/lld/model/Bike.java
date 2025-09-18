package com.abhij33t.lld.model;

import com.abhij33t.lld.enums.VehicleType;

public class Bike extends Vehicle{
    public Bike(String number) {
        super(number, VehicleType.BIKE);
    }
}
