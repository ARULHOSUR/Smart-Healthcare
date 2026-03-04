package com.hms.controller;

import com.hms.dto.ReportSummaryDto;
import com.hms.repository.AppointmentRepository;
import com.hms.repository.BillingRepository;
import com.hms.repository.DoctorRepository;
import com.hms.repository.PatientRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final PatientRepository patientRepo;
    private final DoctorRepository doctorRepo;
    private final AppointmentRepository appointmentRepo;
    private final BillingRepository billingRepo;

    public ReportController(PatientRepository patientRepo,
                            DoctorRepository doctorRepo,
                            AppointmentRepository appointmentRepo,
                            BillingRepository billingRepo) {
        this.patientRepo = patientRepo;
        this.doctorRepo = doctorRepo;
        this.appointmentRepo = appointmentRepo;
        this.billingRepo = billingRepo;
    }

    @GetMapping("/summary")
    public ReportSummaryDto summary() {
        return new ReportSummaryDto(
                patientRepo.count(),
                doctorRepo.count(),
                appointmentRepo.count(),
                billingRepo.count()
        );
    }
}
