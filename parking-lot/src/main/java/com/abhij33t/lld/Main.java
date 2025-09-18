package com.abhij33t.lld;

import com.abhij33t.lld.enums.PaymentMode;
import com.abhij33t.lld.enums.PricingStrategyType;
import com.abhij33t.lld.enums.VehicleType;
import com.abhij33t.lld.factory.PricingStrategyFactory;
import com.abhij33t.lld.factory.VehicleFactory;
import com.abhij33t.lld.model.*;
import com.abhij33t.lld.service.ParkingLotService;

import java.time.LocalDateTime;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        ParkingLotService lot = ParkingLotService.getInstance();
        EntryGate entryGate = new EntryGate("EG1");
        ExitGate exitGate = new ExitGate("XG1");

        lot.setPricingStrategy(PricingStrategyFactory.get(PricingStrategyType.EVENT_BASED));

        ParkingFloor floor1 = new ParkingFloor("Floor1");
        floor1.addSpot(new ParkingSpot("F1S1", VehicleType.BIKE));
        floor1.addSpot(new ParkingSpot("F1S2", VehicleType.CAR));
        floor1.addSpot(new ParkingSpot("F1S3", VehicleType.TRUCK));
        floor1.addSpot(new ParkingSpot("F1S4", VehicleType.CAR));
        lot.addFloor(floor1);

        Vehicle car = VehicleFactory.create("KA01AB1234", VehicleType.CAR);

        LocalDateTime entryTime = LocalDateTime.of(2025, 5, 21, 7, 30);
        Ticket ticket = entryGate.parkVehicle(car, entryTime);

        System.out.println("--------------------------");

        lot.printStatus();

        System.out.println("--------------------------");

        LocalDateTime exitTime = LocalDateTime.of(2025, 5, 21, 13, 15);
        exitGate.unparkVehicle(ticket.getTicketId(), exitTime, PaymentMode.CARD);

        System.out.println("--------------------------");

        lot.printStatus();
    }
}