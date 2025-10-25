package command;

import enums.Direction;
import service.ElevatorService;

import java.util.UUID;

public class ElevatorRequest implements ElevatorCommand{

    private final int elevatorId;
    private final int floor;
    private final Direction direction;
    private final ElevatorService elevatorService;
    private final boolean isInternalRequest;

    public ElevatorRequest(int elevatorId, int floor, Direction direction, ElevatorService elevatorService, boolean isInternalRequest) {
        this.elevatorId = elevatorId;
        this.floor = floor;
        this.direction = direction;
        this.elevatorService = elevatorService;
        this.isInternalRequest = isInternalRequest;
    }

    @Override
    public void execute() {
        if (isInternalRequest)
            elevatorService.requestFloor(elevatorId, floor);
        else
            elevatorService.requestElevator(elevatorId, floor);
    }

    public int getFloor() {
        return floor;
    }

    public Direction getDirection() {
        return direction;
    }

    public boolean isInternalRequest() {
        return isInternalRequest;
    }

    @Override
    public String toString() {
        return "ElevatorRequest{" +
                "elevatorId=" + elevatorId +
                ", floor=" + floor +
                ", direction=" + direction +
                ", isInternalRequest=" + isInternalRequest +
                '}';
    }
}
