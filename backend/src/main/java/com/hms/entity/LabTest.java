package com.hms.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity @Data @NoArgsConstructor @AllArgsConstructor @Builder
public class LabTest {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  private String code;
  private String name;
  private String category;
  private Double price;
}
