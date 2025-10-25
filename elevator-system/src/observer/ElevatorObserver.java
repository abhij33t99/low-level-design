package observer;

import enums.ElevatorState;
import model.Elevator;

public interface ElevatorObserver {
    void onElevatorStateChange(Elevator elevator);
    void onElevatorFloorChange(Elevator elevator);
}
