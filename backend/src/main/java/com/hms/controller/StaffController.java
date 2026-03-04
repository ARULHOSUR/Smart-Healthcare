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
    public ResponseEntity<?> create(@RequestBody Map<String, Object> body) {
        try {
            Staff s = new Staff();
            s.setName(readString(body, "name", "staffName"));
            s.setPhone(readString(body, "phone", "phoneNumber", "staffPhone"));
            s.setGender(readString(body, "gender", "staffGender"));
            s.setRole(defaultIfBlank(readString(body, "role", "staffRole"), "Staff"));
            s.setShift(readString(body, "assignedShift", "shift", "staffShift"));
            s.setAge(readInteger(body, "age", "staffAge"));

            return ResponseEntity.ok(staffRepo.save(s));
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getClass().getSimpleName(),
                                                           "message", ex.getMessage()));
        }
    }

    private String readString(Map<String, Object> body, String... keys) {
        for (String key : keys) {
            Object value = body.get(key);
            if (value != null) {
                String s = String.valueOf(value).trim();
                if (!s.isEmpty()) return s;
            }
        }
        return null;
    }

    private Integer readInteger(Map<String, Object> body, String... keys) {
        for (String key : keys) {
            Object value = body.get(key);
            if (value == null) continue;

            if (value instanceof Number num) {
                return num.intValue();
            }

            String s = String.valueOf(value).trim();
            if (s.isEmpty()) continue;

            try {
                return Integer.valueOf(s);
            } catch (NumberFormatException ignored) {
            }
        }
        return null;
    }

    private String defaultIfBlank(String value, String fallback) {
        return (value == null || value.isBlank()) ? fallback : value;
    }
}
