package model;

import java.time.Duration;
import java.time.LocalTime;
import java.util.Objects;
import java.util.UUID;

public class Slot {
    private final UUID id;
    private final LocalTime startTime;
    private final LocalTime endTime;

    public Slot(LocalTime startTime) {
        this.startTime = startTime;
        this.endTime = startTime.plus(Duration.ofMinutes(60));
        this.id = UUID.randomUUID();
    }

    public UUID getId() {
        return id;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Slot slot = (Slot) o;
        return Objects.equals(startTime, slot.startTime) && Objects.equals(endTime, slot.endTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(startTime, endTime);
    }

    @Override
    public String toString() {
        return String.format("%s-%s", startTime, endTime);
    }
}
