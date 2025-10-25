package repository.interfaces;

import model.Appointment;
import model.Slot;

import java.util.UUID;

public interface AppointmentRepository {
    Appointment book(UUID doctorId, UUID patientId, Slot slot);
    boolean cancel(UUID appointmentId );
}
