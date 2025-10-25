package dto;

import model.Doctor;
import model.Slot;

import java.util.UUID;

public class SlotWithDoctor {
    private Doctor doctor;
    private Slot slot;

    public SlotWithDoctor(Doctor doctor, Slot slot) {
        this.doctor = doctor;
        this.slot = slot;
    }

    public Doctor getDoctor() {
        return doctor;
    }

    public Slot getSlot() {
        return slot;
    }
}
