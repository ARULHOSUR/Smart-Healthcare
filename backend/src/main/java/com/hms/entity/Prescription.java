package com.hms.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

@Entity
public class Prescription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String drug;
    private String dose;
    private Integer days;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties({"doctor","patient"})
    private Encounter encounter;

    // getters / setters
    public Long getId() { return id; }

    public String getDrug() { return drug; }
    public void setDrug(String drug) { this.drug = drug; }

    public String getDose() { return dose; }
    public void setDose(String dose) { this.dose = dose; }

    public Integer getDays() { return days; }
    public void setDays(Integer days) { this.days = days; }

    public Encounter getEncounter() { return encounter; }
    public void setEncounter(Encounter encounter) { this.encounter = encounter; }
}
