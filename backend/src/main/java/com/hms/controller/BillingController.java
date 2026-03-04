package com.hms.controller;

import com.hms.dto.BillingRequest;
import com.hms.entity.Billing;
import com.hms.entity.Patient;
import com.hms.repository.BillingRepository;
import com.hms.repository.PatientRepository;
import org.springframework.web.bind.annotation.*;

import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping("/api/billing")
public class BillingController {

    private final BillingRepository billingRepo;
    private final PatientRepository patientRepo;

    public BillingController(BillingRepository billingRepo, PatientRepository patientRepo) {
        this.billingRepo = billingRepo;
        this.patientRepo = patientRepo;
    }

    @GetMapping
    public List<Billing> all() {
        return billingRepo.findAll();
    }

    @GetMapping("/patient/{id}")
    public List<Billing> byPatient(@PathVariable Long id) {
        return billingRepo.findByPatientId(id);
    }

    @PostMapping
    public Billing create(@RequestBody BillingRequest in) {
        Billing b = new Billing();

        String iso = (in.date != null) ? in.date.format(DateTimeFormatter.ISO_DATE) : null;
        b.setDate(iso);
        b.setAmount(in.amount);
        b.setPaymentMethod(in.paymentMethod);
        b.setInsuranceProvider(in.insuranceProvider);
        b.setClaimStatus(in.claimStatus);
        b.setRemarks(in.remarks);

        Patient p = patientRepo.findById(in.patientId).orElseThrow();
        b.setPatient(p);

        return billingRepo.save(b);
    }
}
