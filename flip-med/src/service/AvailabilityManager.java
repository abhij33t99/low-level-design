package service;

import dto.SlotWithDoctor;
import enums.RankingType;
import enums.Specialization;
import factory.RankingStrategyFactory;
import model.Doctor;
import model.Slot;
import repository.interfaces.DoctorRepository;
import strategy.SlotRankingStrategy;

import java.time.LocalTime;
import java.util.*;

public class AvailabilityManager {
    private final Map<UUID, List<Slot>> declaredSlots; // doctorId -> declared slots
    private final Map<UUID, List<Slot>> bookedSlots;   // doctorId -> booked slots
    private final Map<UUID, Deque<UUID>> waitlist;     // slotId -> queue of patientIds (not used in driver)

    private final DoctorRepository doctorRepository;

    public AvailabilityManager(DoctorRepository doctorRepository) {
        this.doctorRepository = doctorRepository;
        this.declaredSlots = new HashMap<>();
        this.bookedSlots = new HashMap<>();
        this.waitlist = new HashMap<>();
    }

    public void addAvailability(Doctor doctor, List<LocalTime> times) {
        List<Slot> slots = times.stream()
                .map(Slot::new)
                .toList();
        declaredSlots.put(doctor.getId(), slots);
        // ensure booked list exists
        bookedSlots.putIfAbsent(doctor.getId(), new ArrayList<>());
    }

    public List<SlotWithDoctor> getAvailableSlots(RankingType type) {
        return getAvailableSlotsBySpecialization(null, type);
    }

    public List<SlotWithDoctor> getAvailableSlotsBySpecialization(Specialization specialization, RankingType type) {
        List<Doctor> doctors = doctorRepository.all();
        if (specialization != null) {
            doctors = doctors.stream().filter(d -> d.getSpecialization() == specialization).toList();
        }
        List<SlotWithDoctor> availableSlots = new ArrayList<>();
        for (Doctor doctor : doctors) {
            List<Slot> declared = declaredSlots.getOrDefault(doctor.getId(), Collections.emptyList());
            List<Slot> booked = bookedSlots.getOrDefault(doctor.getId(), Collections.emptyList());
            declared.stream()
                    .filter(s -> !booked.contains(s))
                    .map(s -> new SlotWithDoctor(doctor, s))
                    .forEach(availableSlots::add);
        }
        SlotRankingStrategy strategy = RankingStrategyFactory.getRankingStrategy(type);
        return strategy.sort(availableSlots);
    }

    public void markSlotBooked(Doctor doctor, Slot slot) {
        var bookedSlotList = bookedSlots.getOrDefault(doctor.getId(), new ArrayList<>());
        bookedSlotList.add(slot);
        bookedSlots.put(doctor.getId(), bookedSlotList);
    }

    public void releaseSlot(Doctor doctor, Slot slot) {
        var booked = bookedSlots.getOrDefault(doctor.getId(), new ArrayList<>());
        booked.remove(slot);
        bookedSlots.put(doctor.getId(), booked);
    }
}
