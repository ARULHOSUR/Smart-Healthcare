package com.hms.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "lab_order")
public class LabOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Controllers expect: setOrderedDate(String)
    @Column(name = "ordered_date")
    private String orderedDate;

    // Controllers expect: setStatus(String)
    private String status;

    // Controllers expect: setSampleId(String)
    @Column(name = "sample_id")
    private String sampleId;

    // Controllers expect: setTest(LabTest)
    @ManyToOne
    @JoinColumn(name = "test_id")
    private LabTest test;

    // Controllers expect: setPatient(Patient)
    @ManyToOne
    @JoinColumn(name = "patient_id")
    private Patient patient;

    // Controllers expect: setDoctor(Doctor)
    @ManyToOne
    @JoinColumn(name = "doctor_id")
    private Doctor doctor;

    // ----- getters & setters (no Lombok) -----
    public LabOrder() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getOrderedDate() { return orderedDate; }
    public void setOrderedDate(String orderedDate) { this.orderedDate = orderedDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getSampleId() { return sampleId; }
    public void setSampleId(String sampleId) { this.sampleId = sampleId; }

    public LabTest getTest() { return test; }
    public void setTest(LabTest test) { this.test = test; }

    public Patient getPatient() { return patient; }
    public void setPatient(Patient patient) { this.patient = patient; }

    public Doctor getDoctor() { return doctor; }
    public void setDoctor(Doctor doctor) { this.doctor = doctor; }
}
