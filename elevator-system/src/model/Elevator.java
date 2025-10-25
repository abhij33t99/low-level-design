package model;

import enums.Direction;
import enums.ElevatorState;
import observer.ElevatorObserver;
import command.ElevatorRequest;

import java.util.*;

public class Elevator {
    private final int id;
    private int currentFloor;
    private Direction direction;
    private ElevatorState state;
    private Queue<ElevatorRequest> requests;
    private Set<ElevatorObserver> observers;

    public Elevator(int id) {
        this.id = id;
        this.currentFloor = 1;
        this.requests = new LinkedList<>();
        this.observers = new HashSet<>();
        this.state = ElevatorState.IDLE;
        this.direction = Direction.IDLE;
    }

    public void addObserver(ElevatorObserver observer) {
        observers.add(observer);
    }

    void removeObserver(ElevatorObserver observer) {
        observers.remove(observer);
    }

    private void notifyStateChange() {
        for (ElevatorObserver observer : observers)
            observer.onElevatorStateChange(this);
    }

    private void notifyFloorChange() {
        for (ElevatorObserver observer : observers)
            observer.onElevatorFloorChange(this);
    }

    public void addRequest(ElevatorRequest request) {
        if (!requests.contains(request)) {
            requests.add(request);
        }

        int requestedFloor = request.getFloor();
        if (state == ElevatorState.IDLE && !requests.isEmpty()) {
            if (requestedFloor > currentFloor){
                direction = Direction.UP;
            } else if (requestedFloor < currentFloor){
                direction = Direction.DOWN;
            }
            setState(ElevatorState.MOVING);
        }
    }

    public void moveToNextStop(int nextStop) {
        if (state != ElevatorState.MOVING)
            return;
        while (currentFloor != nextStop) {
            if (direction == Direction.UP)
                currentFloor++;
            else
                currentFloor--;
            notifyFloorChange();
        }
        completeArrival();
    }

    private void completeArrival() {
        setState(ElevatorState.STOPPED);
        requests.removeIf(request -> request.getFloor() == currentFloor);

        if (requests.isEmpty()) {
            setDirection(Direction.IDLE);
            setState(ElevatorState.IDLE);
        } else {
            setState(ElevatorState.MOVING);
        }
    }

    public int getId() {
        return id;
    }

    public int getCurrentFloor() {
        return currentFloor;
    }

    public void setCurrentFloor(int currentFloor) {
        this.currentFloor = currentFloor;
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public ElevatorState getState() {
        return state;
    }

    public void setState(ElevatorState state) {
        this.state = state;
    }

    public Queue<ElevatorRequest> getRequests() {
        return new LinkedList<>(requests);
    }

    public List<ElevatorRequest> getDestinationFloors() {
        return new ArrayList<>(requests);
    }
}
