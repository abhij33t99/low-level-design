package service.impl;

import model.Patient;
import repository.interfaces.PatientRepository;
import service.interfaces.PatientService;

import java.util.UUID;

public class PatientServiceImpl implements PatientService {
    private final PatientRepository patientRepository;

    public PatientServiceImpl(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    @Override
    public void save(Patient patient) {
        patientRepository.save(patient);
    }

    @Override
    public Patient findById(UUID id) {
        return patientRepository.findById(id);
    }
}
