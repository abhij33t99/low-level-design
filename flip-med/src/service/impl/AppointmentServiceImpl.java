package service.impl;

import model.Appointment;
import model.Slot;
import repository.interfaces.AppointmentRepository;
import service.AvailabilityManager;
import service.interfaces.AppointmentService;

import java.time.LocalTime;
import java.util.*;

public class AppointmentServiceImpl implements AppointmentService {
    private final AppointmentRepository appointmentRepository;
    private final AvailabilityManager availabilityManager;
    // Track patient appointments by start time to prevent double booking
    private final Map<UUID, List<Appointment>> patientAppointments = new HashMap<>();

    public AppointmentServiceImpl(AppointmentRepository appointmentRepository, AvailabilityManager availabilityManager) {
        this.appointmentRepository = appointmentRepository;
        this.availabilityManager = availabilityManager;
    }

    @Override
    public Appointment book(UUID doctorId, UUID patientId, Slot slot) {
        // Prevent a patient from booking multiple appointments at the same time
        List<Appointment> appts = patientAppointments.getOrDefault(patientId, new ArrayList<>());
        LocalTime start = slot.getStartTime();
        boolean conflict = appts.stream().anyMatch(a -> a.getStartTime().equals(start));
        if (conflict) {
            return null; // indicate failure due to double booking at same time
        }
        Appointment appt = appointmentRepository.book(doctorId, patientId, slot);
        appts.add(appt);
        patientAppointments.put(patientId, appts);
        // Mark slot booked for the doctor
        // We don't have doctor object here; driver will call availabilityManager.markSlotBooked directly as needed.
        return appt;
    }

    @Override
    public boolean cancel(UUID appointmentId) {
        return appointmentRepository.cancel(appointmentId);
    }

    // helper for driver
    public List<Appointment> getAppointmentsForPatient(UUID patientId) {
        return new ArrayList<>(patientAppointments.getOrDefault(patientId, Collections.emptyList()));
    }
}
