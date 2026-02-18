package com.hms.controller;

import com.hms.entity.Staff;
import com.hms.repository.StaffRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/staff")
public class StaffController {

    private final StaffRepository staffRepo;

    public StaffController(StaffRepository staffRepo) {
        this.staffRepo = staffRepo;
    }

    @GetMapping
    public List<Staff> all() { return staffRepo.findAll(); }

    // Expecting JSON: { "name":"Rahim", "role":"Nurse", "shift":"Day" }
    @PostMapping
    public ResponseEntity<?> create(@RequestBody Map<String, String> body) {
        try {
            Staff s = new Staff();
            s.setName(body.get("name"));
            s.setRole(body.get("role"));
            s.setShift(body.get("shift"));
            return ResponseEntity.ok(staffRepo.save(s));
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getClass().getSimpleName(),
                                                           "message", ex.getMessage()));
        }
    }
}
