package service.interfaces;

import model.Appointment;
import model.Slot;

import java.util.UUID;

public interface AppointmentService {
    Appointment book(UUID doctorId, UUID patientId, Slot slot);
    boolean cancel(UUID appointmentId );
}
