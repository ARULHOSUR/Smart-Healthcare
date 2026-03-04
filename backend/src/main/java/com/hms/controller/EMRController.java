package com.hms.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hms.entity.EMR;
import com.hms.entity.Patient;
import com.hms.repository.EMRRepository;
import com.hms.repository.PatientRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/emr")
public class EMRController {

    private final EMRRepository emrRepo;
    private final PatientRepository patientRepo;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final Logger logger = LoggerFactory.getLogger(EMRController.class);

    public EMRController(EMRRepository emrRepo, PatientRepository patientRepo) {
        this.emrRepo = emrRepo;
        this.patientRepo = patientRepo;
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody Map<String, Object> body) {
        try {
            logger.info("Received EMR request: {}", body);
            
            Object patientIdObj = body.get("patientId");
            if (patientIdObj == null) {
                logger.error("patientId is missing");
                return ResponseEntity.badRequest().body(Map.of("error", "patientId is required"));
            }
            
            Long patientId = Long.valueOf(patientIdObj.toString());
            logger.info("Looking for patient with id: {}", patientId);
            
            Patient p = patientRepo.findById(patientId).orElse(null);
            if (p == null) {
                logger.error("Patient not found with id: {}", patientId);
                return ResponseEntity.status(404).body(Map.of("error","Patient not found","patientId", String.valueOf(patientId)));
            }
            
            logger.info("Found patient: {}", p.getName());

            EMR e = new EMR();
            
            Object dateObj = body.get("date");
            if (dateObj != null) e.setVisitDate(dateObj.toString());
            
            Object diagObj = body.get("diagnosis");
            if (diagObj != null) e.setDiagnosis(diagObj.toString());
            
            Object treatObj = body.get("treatment");
            if (treatObj != null) e.setTreatmentPlan(treatObj.toString());
            
            Object notesObj = body.get("notes");
            if (notesObj != null) e.setNotes(notesObj.toString());
            
            e.setPatient(p);
            
            Object testsObj = body.get("tests");
            if (testsObj != null) {
                try {
                    if (testsObj instanceof List) {
                        e.setTests(objectMapper.writeValueAsString(testsObj));
                    } else {
                        e.setTests(testsObj.toString());
                    }
                } catch (JsonProcessingException ex) {
                    logger.warn("Failed to serialize tests", ex);
                }
            }

            EMR saved = emrRepo.save(e);
            logger.info("EMR saved with id: {}", saved.getId());
            return ResponseEntity.ok(saved);
        } catch (Exception ex) {
            logger.error("Error saving EMR", ex);
            return ResponseEntity.badRequest().body(Map.of("error", ex.getClass().getSimpleName(), "message", ex.getMessage()));
        }
    }

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
