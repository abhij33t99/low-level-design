package com.abhij33t.lld.model;

import com.abhij33t.lld.enums.VehicleType;

public class Car extends Vehicle{
    public Car(String number) {
        super(number, VehicleType.CAR);
    }
}
