package com.hms.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "emr")
public class EMR {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Controllers expect: setVisitDate(String)
    @Column(name = "visit_date")
    private String visitDate;

    // Controllers expect: setDiagnosis(String)
    private String diagnosis;

    // Controllers expect: setTreatmentPlan(String)
    @Column(name = "treatment_plan")
    private String treatmentPlan;

    // Controllers expect: setNotes(String)
    private String notes;

    // Controllers expect: setPatient(Patient)
    @ManyToOne
    @JoinColumn(name = "patient_id")
    private Patient patient;

    // ----- getters & setters (no Lombok) -----
    public EMR() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getVisitDate() { return visitDate; }
    public void setVisitDate(String visitDate) { this.visitDate = visitDate; }

    public String getDiagnosis() { return diagnosis; }
    public void setDiagnosis(String diagnosis) { this.diagnosis = diagnosis; }

    public String getTreatmentPlan() { return treatmentPlan; }
    public void setTreatmentPlan(String treatmentPlan) { this.treatmentPlan = treatmentPlan; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public Patient getPatient() { return patient; }
    public void setPatient(Patient patient) { this.patient = patient; }
}
