package com.hms.controller;

import com.hms.entity.EMR;
import com.hms.entity.Patient;
import com.hms.repository.EMRRepository;
import com.hms.repository.PatientRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/emr")
public class EMRController {

    private final EMRRepository emrRepo;
    private final PatientRepository patientRepo;

    public EMRController(EMRRepository emrRepo, PatientRepository patientRepo) {
        this.emrRepo = emrRepo;
        this.patientRepo = patientRepo;
    }

    // CREATE  (expects {"date":"dd-MM-yyyy", "diagnosis":"...", "treatment":"...", "notes":"...", "patientId": 2})
    @PostMapping
    public ResponseEntity<?> create(@RequestBody Map<String, String> body) {
        try {
            Long patientId = Long.valueOf(body.get("patientId"));
            Patient p = patientRepo.findById(patientId).orElse(null);
            if (p == null) {
                return ResponseEntity.status(404).body(Map.of("error","Patient not found","patientId", String.valueOf(patientId)));
            }

            EMR e = new EMR();
            e.setVisitDate(body.get("date"));            // store as String "dd-MM-yyyy"
            e.setDiagnosis(body.get("diagnosis"));
            e.setTreatmentPlan(body.get("treatment"));
            e.setNotes(body.get("notes"));
            e.setPatient(p);

            return ResponseEntity.ok(emrRepo.save(e));
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getClass().getSimpleName(), "message", ex.getMessage()));
        }
    }

    // LIST (optionally filter by patientId)
    @GetMapping
    public ResponseEntity<?> list(@RequestParam(value = "patientId", required = false) Long patientId) {
        try {
            if (patientId == null) {
                return ResponseEntity.ok(emrRepo.findAll());
            }
            return ResponseEntity.ok(
                emrRepo.findAll().stream()
                       .filter(e -> e.getPatient() != null && e.getPatient().getId() != null
                             && e.getPatient().getId().equals(patientId))
                       .toList()
            );
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getClass().getSimpleName(), "message", ex.getMessage()));
        }
    }
}
