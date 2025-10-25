package service.impl;

import model.Doctor;
import repository.interfaces.DoctorRepository;
import service.interfaces.DoctorService;

import java.util.UUID;

public class DoctorServiceImpl implements DoctorService {
    private final DoctorRepository doctorRepository;

    public DoctorServiceImpl(DoctorRepository doctorRepository) {
        this.doctorRepository = doctorRepository;
    }

    @Override
    public void save(Doctor doctor) {
        doctorRepository.save(doctor);
    }

    @Override
    public Doctor findById(UUID id) {
        return doctorRepository.findById(id);
    }
}
