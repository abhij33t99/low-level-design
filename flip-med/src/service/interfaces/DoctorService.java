package service.interfaces;

import model.Doctor;

import java.util.UUID;

public interface DoctorService {
    void save(Doctor doctor);
    Doctor findById(UUID id);
}
