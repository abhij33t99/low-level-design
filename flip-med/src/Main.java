import dto.SlotWithDoctor;
import enums.RankingType;
import enums.Specialization;
import model.Doctor;
import model.Patient;
import model.Slot;
import repository.impl.InMemoryAppointmentRepository;
import repository.impl.InMemoryDoctorRepository;
import repository.impl.InMemoryPatientRepository;
import service.AvailabilityManager;
import service.impl.AppointmentServiceImpl;
import service.impl.DoctorServiceImpl;
import service.impl.PatientServiceImpl;

import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        // Setup repositories and services
        var doctorRepo = new InMemoryDoctorRepository();
        var patientRepo = new InMemoryPatientRepository();
        var apptRepo = new InMemoryAppointmentRepository();

        var doctorService = new DoctorServiceImpl(doctorRepo);
        var patientService = new PatientServiceImpl(patientRepo);
        var availability = new AvailabilityManager(doctorRepo);
        var appointmentService = new AppointmentServiceImpl(apptRepo, availability);

        // Register doctors
        var drCurious = new Doctor("Curious", Specialization.CARDIOLOGIST, 4.8);
        doctorService.save(drCurious);
        System.out.println("Welcome Dr. Curious !!");

        var drDreadful = new Doctor("Dreadful", Specialization.DERMATOLOGIST, 4.2);
        doctorService.save(drDreadful);
        System.out.println("Welcome Dr. Dreadful !!");

        var drDaring = new Doctor("Daring", Specialization.DERMATOLOGIST, 4.9);
        doctorService.save(drDaring);
        System.out.println("Welcome Dr. Daring !!");

        // Declare availability (slots are 60 mins)
        availability.addAvailability(drCurious, Arrays.asList(LocalTime.of(9,0), LocalTime.NOON, LocalTime.of(16,0)));
        System.out.println("Done Doc!");
        availability.addAvailability(drDreadful, Arrays.asList(LocalTime.of(9,0), LocalTime.of(11,0), LocalTime.of(13,0)));
        System.out.println("Done Doc!");
        availability.addAvailability(drDaring, Arrays.asList(LocalTime.of(11,0), LocalTime.of(14,0)));
        System.out.println("Done Doc!");

        // Register patients
        var patientA = new Patient("PatientA", List.of());
        var patientB = new Patient("PatientB", List.of());
        var patientC = new Patient("PatientC", List.of());
        var patientD = new Patient("PatientD", List.of());
        var patientF = new Patient("PatientF", List.of());
        patientService.save(patientA);
        System.out.println("PatientA registered successfully.");
        patientService.save(patientB);
        patientService.save(patientC);
        patientService.save(patientD);
        patientService.save(patientF);

        // Show available slots by specialization (Cardiologist) - Time based
        System.out.println("Available Cardiologists (TIME ranking):");
        printSlots(availability.getAvailableSlotsBySpecialization(Specialization.CARDIOLOGIST, RankingType.TIME));

        // Book appointments
        // PatientA books Dr Curious 12:00
        Slot slotCurious1200 = new Slot(LocalTime.NOON);
        var appt1 = appointmentService.book(drCurious.getId(), patientA.getId(), slotCurious1200);
        if (appt1 != null) {
            availability.markSlotBooked(drCurious, slotCurious1200);
            System.out.println("Booked. Booking id: " + appt1.getId());
        }

        // Show available Cardiologist after booking
        System.out.println("Available Cardiologists after booking (TIME):");
        printSlots(availability.getAvailableSlotsBySpecialization(Specialization.CARDIOLOGIST, RankingType.TIME));

        // Dermatologists by TIME ranking
        System.out.println("Available Dermatologists (TIME ranking):");
        printSlots(availability.getAvailableSlotsBySpecialization(Specialization.DERMATOLOGIST, RankingType.TIME));

        // Dermatologists by RATING ranking
        System.out.println("Available Dermatologists (RATING ranking):");
        printSlots(availability.getAvailableSlotsBySpecialization(Specialization.DERMATOLOGIST, RankingType.RATING));

        // More bookings
        var appt2 = appointmentService.book(drDaring.getId(), patientF.getId(), new Slot(LocalTime.of(11,0)));
        if (appt2 != null) {
            availability.markSlotBooked(drDaring, new Slot(LocalTime.of(11,0)));
            System.out.println("Booked. Booking id: " + appt2.getId());
        }

        var appt3 = appointmentService.book(drCurious.getId(), patientB.getId(), new Slot(LocalTime.NOON));
        if (appt3 != null) {
            availability.markSlotBooked(drCurious, new Slot(LocalTime.NOON));
            System.out.println("Booked. Booking id: " + appt3.getId());
        } else {
            System.out.println("Booking failed (slot not available or conflict).");
        }

        var appt4 = appointmentService.book(drCurious.getId(), patientF.getId(), new Slot(LocalTime.of(9,0)));
        if (appt4 != null) {
            availability.markSlotBooked(drCurious, new Slot(LocalTime.of(9,0)));
            System.out.println("Booked. Booking id: " + appt4.getId());
        }

        var appt5 = appointmentService.book(drCurious.getId(), patientC.getId(), new Slot(LocalTime.of(16,0)));
        if (appt5 != null) {
            availability.markSlotBooked(drCurious, new Slot(LocalTime.of(16,0)));
            System.out.println("Booked. Booking id: " + appt5.getId());
        }

        // Show cardiologist availability again
        System.out.println("Cardiologists availability after more bookings:");
        printSlots(availability.getAvailableSlotsBySpecialization(Specialization.CARDIOLOGIST, RankingType.TIME));

        // Cancel one appointment
        if (appt5 != null) {
            boolean cancelled = apptRepo.cancel(appt5.getId());
            if (cancelled) {
                availability.releaseSlot(drCurious, new Slot(LocalTime.of(16,0)));
                System.out.println("Booking Cancelled");
            }
        }

        System.out.println("Cardiologists availability after cancellation:");
        printSlots(availability.getAvailableSlotsBySpecialization(Specialization.CARDIOLOGIST, RankingType.TIME));

        // Show PatientF appointments tracked locally
        System.out.println("Appointments booked by PatientF:");
        for (var a : appointmentService.getAppointmentsForPatient(patientF.getId())) {
            System.out.println("Booking id: " + a.getId() + ", Start: " + a.getStartTime());
        }
    }

    private static void printSlots(List<SlotWithDoctor> list) {
        if (list.isEmpty()) {
            System.out.println("No slots available");
            return;
        }
        for (SlotWithDoctor swd : list) {
            System.out.println("Dr." + swd.getDoctor().getName() + ": (" + swd.getSlot().getStartTime() + ")");
        }
    }
}