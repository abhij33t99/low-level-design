package repository.interfaces;

import model.Patient;

import java.util.UUID;

public interface PatientRepository {
    void save(Patient patient);
    Patient findById(UUID id);
}
