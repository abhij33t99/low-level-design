package service.interfaces;

import model.Patient;

import java.util.UUID;

public interface PatientService {
    void save(Patient patient);
    Patient findById(UUID id);
}
