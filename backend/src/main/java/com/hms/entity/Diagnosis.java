package com.hms.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

@Entity
public class Diagnosis {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String text; // simple free text diagnosis

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties({"doctor","patient"}) // keep payload slim
    private Encounter encounter;

    // getters / setters
    public Long getId() { return id; }

    public String getText() { return text; }
    public void setText(String text) { this.text = text; }

    public Encounter getEncounter() { return encounter; }
    public void setEncounter(Encounter encounter) { this.encounter = encounter; }
}
