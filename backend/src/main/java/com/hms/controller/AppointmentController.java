package com.hms.controller;

import com.hms.dto.AppointmentRequest;
import com.hms.entity.Appointment;
import com.hms.entity.Doctor;
import com.hms.entity.Patient;
import com.hms.repository.AppointmentRepository;
import com.hms.repository.DoctorRepository;
import com.hms.repository.PatientRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {

    private final AppointmentRepository appointmentRepo;
    private final DoctorRepository doctorRepo;
    private final PatientRepository patientRepo;

    public AppointmentController(
            AppointmentRepository appointmentRepo,
            DoctorRepository doctorRepo,
            PatientRepository patientRepo
    ) {
        this.appointmentRepo = appointmentRepo;
        this.doctorRepo = doctorRepo;
        this.patientRepo = patientRepo;
    }

    @GetMapping
    public List<Appointment> all() {
        return appointmentRepo.findAll();
    }

    @PostMapping
    public Appointment create(@RequestBody AppointmentRequest in) {

        Appointment a = new Appointment();

        // Directly store date string (no LocalDate parsing)
        a.setDate(in.getDate());

        // Set default status
        a.setStatus("PENDING");

        Doctor d = doctorRepo.findById(in.getDoctorId()).orElseThrow();
        Patient p = patientRepo.findById(in.getPatientId()).orElseThrow();

        a.setDoctor(d);
        a.setPatient(p);

        return appointmentRepo.save(a);
    }
}
