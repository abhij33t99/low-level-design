package observer;

import model.Elevator;

public class ElevatorDisplay implements ElevatorObserver{
    @Override
    public void onElevatorStateChange(Elevator elevator) {
        System.out.printf("Elevator %s state changed to %s\n", elevator.getId(), elevator.getState().name());
    }

    @Override
    public void onElevatorFloorChange(Elevator elevator) {
        System.out.printf("Elevator %s floor changed to %d\n", elevator.getId(), elevator.getCurrentFloor());
    }
}
