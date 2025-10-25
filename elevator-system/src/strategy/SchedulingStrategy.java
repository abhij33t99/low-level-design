package strategy;

import model.Elevator;

public interface SchedulingStrategy {
    int getNextStop(Elevator elevator);
}
