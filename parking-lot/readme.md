# Parking Lot LLD (Low-Level Design)

## Overview
This module implements a simple but extensible Parking Lot system in Java. It models floors, spots, gates, tickets, pricing, and payments with a focus on clean separation of concerns and the Strategy and Factory patterns for flexibility.

The core service exposes API methods to park and unpark vehicles, compute fees using a pluggable pricing strategy, and collect payments through interchangeable payment strategies.

## Goals and Features
- Multiple floors and typed parking spots (Bike, Car, Truck)
- Ticket issuance at entry and fee calculation at exit
- Pluggable pricing strategies (Time-based, Event-based)
- Pluggable payment strategies (Cash, Card) via a PaymentProcessor
- Dynamic configuration of floors/spots at runtime
- Simple, test-friendly design with minimal dependencies

## Architecture at a Glance
- Service (singleton):
  - ParkingLotService — orchestrates parking/unparking, holds floors, active tickets, and the selected PricingStrategy.
- Domain Models:
  - ParkingFloor, ParkingSpot, Vehicle (Car, Bike, Truck), Ticket, Gate (EntryGate, ExitGate)
- Strategies (Strategy pattern):
  - PricingStrategy: TimeBasedPricing, EventBasedPricing
  - PaymentStrategy: CashPayment, CardPayment
- Factories (Factory pattern):
  - VehicleFactory — constructs vehicles by type
  - PricingStrategyFactory — creates pricing strategies
  - PaymentStrategyFactory — creates payment strategies

## Key Classes
- com.abhij33t.lld.service.ParkingLotService
  - getInstance(): singleton accessor
  - addFloor(ParkingFloor): register floors
  - parkVehicle(Vehicle, LocalDateTime): allocate a suitable spot and issue a Ticket
  - unparkVehicle(ticketId, exitTime, PaymentMode): compute fee via PricingStrategy and pay via PaymentProcessor
  - setPricingStrategy(PricingStrategy): swap pricing at runtime
  - printStatus(): dump current occupancy of all spots

- com.abhij33t.lld.model
  - ParkingFloor: manages a map of ParkingSpot by id and finding an available spot for a VehicleType
  - ParkingSpot: has allowed VehicleType, occupancy state, id
  - Ticket: immutable builder-based ticket object carrying ids, vehicle, entryTime
  - Vehicle (abstract) with Car, Bike, Truck concrete types
  - Gate (abstract) with EntryGate (park) and ExitGate (unpark and payment)

- com.abhij33t.lld.strategy.pricing
  - PricingStrategy (interface)
  - TimeBasedPricing: computes fee based on duration and peak/off-peak windows
  - EventBasedPricing: alternate pricing for special events or periods

- com.abhij33t.lld.strategy.payment
  - PaymentStrategy (interface)
  - CashPayment, CardPayment (console-simulated)
  - PaymentProcessor: context that executes a PaymentStrategy against a Ticket and amount

- com.abhij33t.lld.factory
  - VehicleFactory, PricingStrategyFactory, PaymentStrategyFactory

## Typical Flow
1) EntryGate.parkVehicle(vehicle, entryTime)
   - Delegates to ParkingLotService.parkVehicle → finds a compatible free spot → issues Ticket → marks spot occupied.
2) ExitGate.unparkVehicle(ticketId, exitTime, paymentMode)
   - ParkingLotService computes fee via current PricingStrategy → processes payment via PaymentProcessor/PaymentStrategy → frees the spot on success.

## Build & Run
- Requirements: Java 17+ and Maven

Build module:
- mvn -q -f parking-lot/pom.xml clean package

Run example main class:
- java -cp parking-lot/target/classes com.abhij33t.lld.Main

## Usage Example (from Main.java)
```java
ParkingLotService lot = ParkingLotService.getInstance();
EntryGate entryGate = new EntryGate("EG1");
ExitGate exitGate = new ExitGate("XG1");

// Choose a pricing strategy at runtime
lot.setPricingStrategy(PricingStrategyFactory.get(PricingStrategyType.EVENT_BASED));

// Configure floors and spots
ParkingFloor floor1 = new ParkingFloor("Floor1");
floor1.addSpot(new ParkingSpot("F1S1", VehicleType.BIKE));
floor1.addSpot(new ParkingSpot("F1S2", VehicleType.CAR));
floor1.addSpot(new ParkingSpot("F1S3", VehicleType.TRUCK));
floor1.addSpot(new ParkingSpot("F1S4", VehicleType.CAR));
lot.addFloor(floor1);

// Vehicle arrives
Vehicle car = VehicleFactory.create("KA01AB1234", VehicleType.CAR);
LocalDateTime entryTime = LocalDateTime.of(2025, 5, 21, 7, 30);
Ticket ticket = entryGate.parkVehicle(car, entryTime);

// ... time passes ...
LocalDateTime exitTime = LocalDateTime.of(2025, 5, 21, 13, 15);
exitGate.unparkVehicle(ticket.getTicketId(), exitTime, PaymentMode.CARD);
```

## Extension Points
- Pricing
  - Add a new PricingStrategy implementation and register it in PricingStrategyFactory.
- Payments
  - Add a new PaymentStrategy (e.g., UPI, Wallet) and register in PaymentStrategyFactory.
- Spots & Vehicles
  - Add new VehicleType and adjust ParkingSpot matching logic accordingly.
- Concurrency & Scaling
  - Introduce proper synchronization and allocation policies if moving beyond single-threaded demos.

## Directory Layout
- src/main/java/com/abhij33t/lld/
  - Main.java — demo runner
  - enums/ — VehicleType, PricingStrategyType, PaymentMode, etc.
  - model/ — Gate, EntryGate, ExitGate, ParkingFloor, ParkingSpot, Ticket, Vehicle hierarchy
  - service/ — ParkingLotService
  - strategy/pricing — PricingStrategy, TimeBasedPricing, EventBasedPricing
  - strategy/payment — PaymentStrategy, CashPayment, CardPayment
  - factory/ — VehicleFactory, PricingStrategyFactory, PaymentStrategyFactory

## Notes
- The example uses console outputs to demonstrate the flow. Replace PaymentStrategy and pricing details as needed for production-like scenarios.
