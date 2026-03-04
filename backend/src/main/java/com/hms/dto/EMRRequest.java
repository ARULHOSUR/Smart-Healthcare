
package com.hms.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;

public class EMRRequest {
    @JsonFormat(pattern = "dd-MM-yyyy")
    public LocalDate visitDate;
    public String diagnosis;
    public String treatmentPlan;
    public String notes;
    public Long patientId;
}
