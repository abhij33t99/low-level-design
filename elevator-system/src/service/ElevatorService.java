package service;

import command.ElevatorRequest;
import enums.Direction;
import model.Elevator;
import model.Floor;
import strategy.FCFSSchedulingStrategy;
import strategy.SchedulingStrategy;

import java.util.ArrayList;
import java.util.List;

public class ElevatorService {
    private List<Elevator> elevators;
    private List<Floor> floors;
    private SchedulingStrategy strategy;

    public ElevatorService() {
    }

    public ElevatorService(int noOfElevators, int noOfFloors) {
        this.elevators = new ArrayList<>();
        this.floors = new ArrayList<>();
        this.strategy = new FCFSSchedulingStrategy();

        for (int i = 1; i <= noOfElevators; i++)
            elevators.add(new Elevator(i));

        for (int i = 1; i <= noOfFloors; i++)
            floors.add(new Floor(i));
    }

    public void setStrategy(SchedulingStrategy strategy) {
        this.strategy = strategy;
    }

    public void requestElevator(int elevatorId, int floorNumber) {
        System.out.println(
                "External request: Floor " + floorNumber);
        Elevator selectedElevator = getElevatorById(elevatorId);
        Direction direction = floorNumber > selectedElevator.getCurrentFloor()
                ? Direction.UP
                : Direction.DOWN;
        if (selectedElevator != null) {
            selectedElevator.addRequest(
                    new ElevatorRequest(elevatorId, floorNumber, direction, this, false));
            System.out.println("Assigned elevator " + selectedElevator.getId()
                    + " to floor " + floorNumber);
        } else {
            System.out.println("No elevator available for floor " + floorNumber);
        }
    }

    public void requestFloor(int elevatorId, int floorNumber) {
        // Find the elevator by its ID
        Elevator elevator = getElevatorById(elevatorId);
        System.out.println("Internal request: Elevator " + elevator.getId()
                + " to floor " + floorNumber);
        // Determine the direction of the request
        Direction direction = floorNumber > elevator.getCurrentFloor()
                ? Direction.UP
                : Direction.DOWN;
        // Add the request to the elevator
        elevator.addRequest(
                new ElevatorRequest(elevatorId, floorNumber, direction, this, true));
    }

    private Elevator getElevatorById(int elevatorId) {
        for (Elevator elevator : elevators) {
            if (elevator.getId() == elevatorId)
                return elevator;
        }
        return null;
    }

    public List<Floor> getFloors() {
        return floors;
    }

    public List<Elevator> getElevators() {
        return elevators;
    }

    public void step() {
        for (Elevator elevator : elevators) {
            // Only process elevators with pending requests
            if (!elevator.getRequests().isEmpty()) {
                // Use the scheduling strategy to find the next stop
                int nextStop = strategy.getNextStop(elevator);


                // Move the elevator to the next stop if needed
                if (elevator.getCurrentFloor() != nextStop)
                    elevator.moveToNextStop(nextStop);
            }
        }
    }
}
