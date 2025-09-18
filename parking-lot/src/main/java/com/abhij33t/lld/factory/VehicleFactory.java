package com.abhij33t.lld.factory;

import com.abhij33t.lld.enums.VehicleType;
import com.abhij33t.lld.model.Bike;
import com.abhij33t.lld.model.Car;
import com.abhij33t.lld.model.Truck;
import com.abhij33t.lld.model.Vehicle;

public class VehicleFactory {

    public static Vehicle create(String number, VehicleType type) {
        return switch (type) {
            case CAR -> new Car(number);
            case BIKE -> new Bike(number);
            case TRUCK -> new Truck(number);
        };
    }
}
