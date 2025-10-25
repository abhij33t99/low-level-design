package repository.impl;

import model.Patient;
import repository.interfaces.PatientRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class InMemoryPatientRepository implements PatientRepository {
    private final Map<UUID, Patient> patientMap = new HashMap<>();

    @Override
    public void save(Patient patient) {
        patientMap.put(patient.getId(), patient);
    }

    @Override
    public Patient findById(UUID id){
        return patientMap.getOrDefault(id, null);
    }
}
