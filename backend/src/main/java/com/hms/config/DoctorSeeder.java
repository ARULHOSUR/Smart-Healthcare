package com.hms.config;

import com.hms.entity.Doctor;
import com.hms.repository.DoctorRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DoctorSeeder implements CommandLineRunner {

    private final DoctorRepository doctorRepository;

    public DoctorSeeder(DoctorRepository doctorRepository) {
        this.doctorRepository = doctorRepository;
    }

    @Override
    public void run(String... args) {
        List<Doctor> defaults = List.of(
                makeDoctor("Sashank Jonnavithula", "Optamalogist"),
                makeDoctor("Ananya Reddy", "Cardiologist"),
                makeDoctor("Rahul Varma", "Orthopedic"),
                makeDoctor("Meera Nair", "Dermatologist"),
                makeDoctor("Vikram Iyer", "Neurologist"),
                makeDoctor("Priya Sharma", "Pediatrician")
        );

        for (Doctor doctor : defaults) {
            if (!doctorRepository.existsByNameIgnoreCase(doctor.getName())) {
                doctorRepository.save(doctor);
            }
        }
    }

    private Doctor makeDoctor(String name, String specialization) {
        Doctor doctor = new Doctor();
        doctor.setName(name);
        doctor.setSpecialization(specialization);
        return doctor;
    }
}
