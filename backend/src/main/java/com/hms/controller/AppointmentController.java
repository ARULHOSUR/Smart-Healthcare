package com.hms.controller;

import com.hms.service.SmsService;
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
    private final SmsService smsService;   // ✅ Injected

    public AppointmentController(
            AppointmentRepository appointmentRepo,
            DoctorRepository doctorRepo,
            PatientRepository patientRepo,
            SmsService smsService   // ✅ Added in constructor
    ) {
        this.appointmentRepo = appointmentRepo;
        this.doctorRepo = doctorRepo;
        this.patientRepo = patientRepo;
        this.smsService = smsService;   // ✅ Assign
    }

    @GetMapping
    public List<Appointment> all() {
        return appointmentRepo.findAll();
    }

    @PostMapping
    public Appointment create(@RequestBody AppointmentRequest in) {

        // Create and save patient
        Patient p = new Patient();
        p.setName(in.getPatientName());
        p.setAge(in.getPatientAge());
        p.setPhone(in.getPatientPhone());

        patientRepo.save(p);

        // Get doctor
        Doctor d = doctorRepo.findById(in.getDoctorId())
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

        // Create appointment
        Appointment a = new Appointment();
        a.setDate(in.getDate());
        a.setSlot(in.getSlot());
        a.setStatus("CONFIRMED");
        a.setDoctor(d);
        a.setPatient(p);

        Appointment saved = appointmentRepo.save(a);

        // ✅ REAL SMS USING TWILIO
        smsService.sendAppointmentSMS(
                p.getPhone(),
                d.getName(),
                a.getDate(),
                a.getSlot()
        );

        return saved;
    }
}