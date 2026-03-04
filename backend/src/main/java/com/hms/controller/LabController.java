package com.hms.controller;

import com.hms.entity.*;
import com.hms.repository.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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

    @PostMapping("/orders")
    public ResponseEntity<?> createOrder(@RequestBody Map<String, String> body) {
        try {
            LabOrder o = new LabOrder();
            o.setOrderedDate(body.get("date") != null ? body.get("date") : java.time.LocalDate.now().toString());
            o.setStatus(body.get("status") != null ? body.get("status") : "ORDERED");
            o.setSampleId(body.get("sampleId"));

            // Check if patientId is provided (standard way)
            if (body.get("patientId") != null && !body.get("patientId").isEmpty()) {
                Long patientId = Long.valueOf(body.get("patientId"));
                Patient patient = patientRepo.findById(patientId).orElse(null);
                if (patient != null) {
                    o.setPatient(patient);
                }
            }

            // Check if doctorId is provided
            if (body.get("doctorId") != null && !body.get("doctorId").isEmpty()) {
                Long doctorId = Long.valueOf(body.get("doctorId"));
                Doctor doctor = doctorRepo.findById(doctorId).orElse(null);
                if (doctor != null) {
                    o.setDoctor(doctor);
                }
            }

            // Check if testId is provided (standard way)
            if (body.get("testId") != null && !body.get("testId").isEmpty()) {
                Long testId = Long.valueOf(body.get("testId"));
                LabTest test = testRepo.findById(testId).orElse(null);
                if (test != null) {
                    o.setTest(test);
                }
            }

            // Handle manual entry: store patient info directly as sampleId (for display purposes)
            if (body.get("patientName") != null) {
                String patientInfo = body.get("patientName");
                if (body.get("patientAge") != null && !body.get("patientAge").isEmpty()) {
                    patientInfo += "|" + body.get("patientAge");
                }
                if (body.get("patientPhone") != null && !body.get("patientPhone").isEmpty()) {
                    patientInfo += "|" + body.get("patientPhone");
                }
                o.setSampleId(patientInfo);
            }

            // Handle test name directly - store in status field for display
            if (body.get("testName") != null && o.getTest() == null) {
                String testName = body.get("testName");
                o.setStatus("TEST:" + testName);
            }

            return ResponseEntity.ok(orderRepo.save(o));
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getClass().getSimpleName(), "message", ex.getMessage()));
        }
    }

    @GetMapping("/orders")
    public ResponseEntity<?> listOrders() {
        try {
            return ResponseEntity.ok(orderRepo.findAll());
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getClass().getSimpleName(), "message", ex.getMessage()));
        }
    }
}
