package com.hms.controller;

import com.hms.entity.*;
import com.hms.repository.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/lab")
public class LabController {

    private final LabOrderRepository orderRepo;
    private final PatientRepository patientRepo;
    private final DoctorRepository doctorRepo;
    private final LabTestRepository testRepo;

    public LabController(LabOrderRepository orderRepo,
                         PatientRepository patientRepo,
                         DoctorRepository doctorRepo,
                         LabTestRepository testRepo) {
        this.orderRepo = orderRepo;
        this.patientRepo = patientRepo;
        this.doctorRepo = doctorRepo;
        this.testRepo = testRepo;
    }

    // CREATE ORDER  (expects {"date":"dd-MM-yyyy","status":"...","sampleId":"...","testId":1,"patientId":2,"doctorId":1})
    @PostMapping("/orders")
    public ResponseEntity<?> createOrder(@RequestBody Map<String, String> body) {
        try {
            Long patientId = Long.valueOf(body.get("patientId"));
            Long doctorId  = Long.valueOf(body.get("doctorId"));
            Long testId    = Long.valueOf(body.get("testId"));

            Patient patient = patientRepo.findById(patientId).orElse(null);
            if (patient == null) return ResponseEntity.status(404).body(Map.of("error","Patient not found","id",patientId));

            Doctor doctor = doctorRepo.findById(doctorId).orElse(null);
            if (doctor == null) return ResponseEntity.status(404).body(Map.of("error","Doctor not found","id",doctorId));

            LabTest test = testRepo.findById(testId).orElse(null);
            if (test == null)   return ResponseEntity.status(404).body(Map.of("error","Test not found","id",testId));

            LabOrder o = new LabOrder();
            o.setOrderedDate(body.get("date"));   // store as String "dd-MM-yyyy"
            o.setStatus(body.get("status"));
            o.setSampleId(body.get("sampleId"));
            o.setPatient(patient);
            o.setDoctor(doctor);
            o.setTest(test);

            return ResponseEntity.ok(orderRepo.save(o));
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getClass().getSimpleName(), "message", ex.getMessage()));
        }
    }

    // LIST ORDERS
    @GetMapping("/orders")
    public ResponseEntity<?> listOrders() {
        try {
            return ResponseEntity.ok(orderRepo.findAll());
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getClass().getSimpleName(), "message", ex.getMessage()));
        }
    }
}
