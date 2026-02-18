package com.hms.repository;

import com.hms.entity.Encounter;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface EncounterRepository extends JpaRepository<Encounter, Long> {
    List<Encounter> findByPatientId(Long patientId);
}
