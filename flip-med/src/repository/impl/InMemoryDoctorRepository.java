package repository.impl;

import model.Doctor;
import repository.interfaces.DoctorRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class InMemoryDoctorRepository implements DoctorRepository {

    private final Map<UUID, Doctor> doctorMap = new HashMap<>();

    @Override
    public void save(Doctor doctor) {
        doctorMap.put(doctor.getId(), doctor);
    }

    @Override
    public Doctor findById(UUID id) {
        return doctorMap.get(id);
    }

    @Override
    public List<Doctor> all() {
        return new ArrayList<>(doctorMap.values());
    }
}
