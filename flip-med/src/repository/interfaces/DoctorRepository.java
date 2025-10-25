package repository.interfaces;

import model.Doctor;

import java.util.List;
import java.util.UUID;

public interface DoctorRepository {
    void save(Doctor doctor);
    Doctor findById(UUID id);
    List<Doctor> all();
}
