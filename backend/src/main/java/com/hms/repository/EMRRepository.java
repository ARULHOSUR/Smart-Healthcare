package com.hms.repository;

import com.hms.entity.EMR;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface EMRRepository extends JpaRepository<EMR, Long> {
    List<EMR> findByPatientId(Long patientId);
}
