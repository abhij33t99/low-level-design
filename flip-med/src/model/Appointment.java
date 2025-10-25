package model;

import java.time.LocalTime;
import java.util.UUID;

public class Appointment {
    private final UUID id;
    private final UUID patientId;
    private final UUID doctorId;
    private final LocalTime startTime;

    public Appointment(UUID id, UUID patientId, UUID doctorId, LocalTime startTime) {
        this.id = id;
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.startTime = startTime;
    }

    public UUID getId() {
        return id;
    }

    public UUID getPatientId() {
        return patientId;
    }

    public UUID getDoctorId() {
        return doctorId;
    }

    public LocalTime getStartTime() {
        return startTime;
    }
}
