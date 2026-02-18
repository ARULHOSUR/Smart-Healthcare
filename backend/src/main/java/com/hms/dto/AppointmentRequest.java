package com.hms.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;

public class AppointmentRequest {
    @JsonFormat(pattern = "dd-MM-yyyy")   // <-- frontend sends 20-11-2025
    public LocalDate date;
    public String status;
    public Long doctorId;
    public Long patientId;
}
