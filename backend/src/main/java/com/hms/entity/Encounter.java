package com.hms.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
public class Encounter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate date;   // <-- controller expects this
    private String notes;
    private String bp;
    private Double temp;
    private Integer pulse;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties({"appointments"})  // avoid recursion when serializing
    private Doctor doctor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties({"appointments"})
    private Patient patient;

    // --- getters & setters ---
    public Long getId() { return id; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public String getBp() { return bp; }
    public void setBp(String bp) { this.bp = bp; }

    public Double getTemp() { return temp; }
    public void setTemp(Double temp) { this.temp = temp; }

    public Integer getPulse() { return pulse; }
    public void setPulse(Integer pulse) { this.pulse = pulse; }

    public Doctor getDoctor() { return doctor; }
    public void setDoctor(Doctor doctor) { this.doctor = doctor; }

    public Patient getPatient() { return patient; }
    public void setPatient(Patient patient) { this.patient = patient; }
}
