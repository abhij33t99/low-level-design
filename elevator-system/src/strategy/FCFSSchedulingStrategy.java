package strategy;

import enums.Direction;
import model.Elevator;

public class FCFSSchedulingStrategy implements SchedulingStrategy{
    @Override
    public int getNextStop(Elevator elevator) {
        int currentFloor = elevator.getCurrentFloor();
        Direction elevatorDirection = elevator.getDirection();

        var requests = elevator.getRequests();
        if (requests.isEmpty())
            return currentFloor;

        int nextRequestedFloor = requests.poll().getFloor();

        if (nextRequestedFloor == currentFloor)
            return currentFloor;

        if (elevatorDirection == Direction.IDLE) {
            elevator.setDirection(
                    nextRequestedFloor > currentFloor ? Direction.UP : Direction.DOWN);
        } else if (elevatorDirection == Direction.UP
                && nextRequestedFloor < currentFloor) {
            elevator.setDirection(Direction.DOWN);
        } else if (nextRequestedFloor > currentFloor) {
            elevator.setDirection(Direction.UP);
        }

        return nextRequestedFloor;
    }
}
