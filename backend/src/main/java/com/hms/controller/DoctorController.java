package com.hms.controller;

import com.hms.entity.Doctor;
import com.hms.repository.DoctorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/doctors")
@CrossOrigin(origins = "*")
public class DoctorController {

    @Autowired
    private DoctorRepository doctorRepository;

    @GetMapping
    public List<Doctor> getAllDoctors() {
        return doctorRepository.findAll();
    }
    @GetMapping("/{doctorId}/slots")
public List<String> getDoctorSlots(@PathVariable Long doctorId) {
    return List.of(
        "10:00 AM - 10:30 AM",
        "11:00 AM - 11:30 AM",
        "02:00 PM - 02:30 PM"
    );
}


    @PostMapping
    public Doctor createDoctor(@RequestBody Doctor doctor) {
        return doctorRepository.save(doctor);
    }
}
