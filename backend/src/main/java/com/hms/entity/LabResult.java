package com.hms.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity @Data @NoArgsConstructor @AllArgsConstructor @Builder
public class LabResult {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private Long orderId;
  private String resultValue;
  private String referenceRange;
  private String interpretation;
  private LocalDate resultDate;
}
