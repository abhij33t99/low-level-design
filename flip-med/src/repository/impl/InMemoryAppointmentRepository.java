package repository.impl;

import model.Appointment;
import model.Slot;
import repository.interfaces.AppointmentRepository;

import java.util.*;

public class InMemoryAppointmentRepository implements AppointmentRepository {
    private final Map<UUID, Appointment> appointments = new HashMap<>();

    @Override
    public Appointment book(UUID doctorId, UUID patientId, Slot slot) {
        UUID id = UUID.randomUUID();
        Appointment appt = new Appointment(id, patientId, doctorId, slot.getStartTime());
        appointments.put(id, appt);
        return appt;
    }

    @Override
    public boolean cancel(UUID appointmentId) {
        return appointments.remove(appointmentId) != null;
    }

    // helper for driver/tests
    public List<Appointment> findByPatient(UUID patientId) {
        List<Appointment> list = new ArrayList<>();
        for (Appointment a : appointments.values()) {
            if (a.getPatientId().equals(patientId)) list.add(a);
        }
        return list;
    }
}
