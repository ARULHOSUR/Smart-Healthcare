package com.hms.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;

public class LabOrderRequest {
    @JsonFormat(pattern = "dd-MM-yyyy")
    public LocalDate orderedDate;
    public String status;
    public String sampleId;   // optional
    public Long testId;
    public Long patientId;
    public Long doctorId;     // optional
}
