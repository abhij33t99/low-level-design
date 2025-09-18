package com.abhij33t.lld.service;

import com.abhij33t.lld.enums.PaymentMode;
import com.abhij33t.lld.enums.PricingStrategyType;
import com.abhij33t.lld.factory.PaymentStrategyFactory;
import com.abhij33t.lld.factory.PricingStrategyFactory;
import com.abhij33t.lld.model.ParkingFloor;
import com.abhij33t.lld.model.ParkingSpot;
import com.abhij33t.lld.model.Ticket;
import com.abhij33t.lld.model.Vehicle;
import com.abhij33t.lld.strategy.payment.PaymentStrategy;
import com.abhij33t.lld.strategy.pricing.PricingStrategy;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ParkingLotService {
    private static final ParkingLotService INSTANCE = new ParkingLotService();

    private final Map<String, ParkingFloor> floors = new HashMap<>();
    private final Map<String, Ticket> activeTickets = new HashMap<>();
    @Setter
    private PricingStrategy pricingStrategy;

    private ParkingLotService(){
        this.pricingStrategy = PricingStrategyFactory.get(PricingStrategyType.TIME_BASED);
    }

    public static ParkingLotService getInstance(){
        return INSTANCE;
    }

    public void addFloor(ParkingFloor floor) {
        floors.put(floor.getId(), floor);
    }

    public Ticket parkVehicle(Vehicle vehicle, LocalDateTime entryTime) {
        for (ParkingFloor floor : floors.values()) {
            ParkingSpot spot = floor.findAvailableSpot(vehicle.getType());

            if (spot != null) {
                String ticketId = UUID.randomUUID().toString();
                Ticket ticket = Ticket.builder()
                        .ticketId(ticketId)
                        .vehicle(vehicle)
                        .entryTime(entryTime)
                        .floorId(floor.getId())
                        .spotId(spot.getId())
                        .build();

                activeTickets.put(ticketId, ticket);
                System.out.printf("Vehicle %s parked: Ticket: %s", vehicle.getNumber(), ticketId);
                return ticket;
            }
        }

        System.out.println("No spot available for vehicle type: "+vehicle.getType());
        return null;
    }

    public void unparkVehicle(String ticketId, LocalDateTime exitTime, PaymentMode paymentMode) {
        Ticket ticket = activeTickets.get(ticketId);
        if (ticket == null) {
            System.out.println("Invalid ticket id: "+ticketId);
            return;
        }

        double fee = pricingStrategy.calculatePrice(
                ticket.getVehicle().getType(),
                ticket.getEntryTime(),
                exitTime
        );

        PaymentStrategy paymentStrategy = PaymentStrategyFactory.get(paymentMode);
        PaymentProcessor paymentProcessor = new PaymentProcessor(paymentStrategy);
        boolean paid = paymentProcessor.pay(ticket, fee);

        if (!paid) {
            System.out.println("Payment unsuccessful! Vehicle can't exit");
            return;
        }

        ParkingSpot spot = floors.get(ticket.getFloorId()).getSpots().get(ticket.getSpotId());
        spot.vacate();
        activeTickets.remove(ticketId);

        System.out.println("Vehicle exited. Fee charged: "+fee);
    }

    public void printStatus() {
        floors.forEach((floorId, floor) -> {
            System.out.println("Floor: "+floorId);
            floor.getSpots().forEach((spotId, spot) -> {
                System.out.println(" Spot " + spotId + " [" + spot.getAllowedType() + "] - " + (spot.isOccupied() ? "Occupied" : "Free"));
            });
        });
    }

}
