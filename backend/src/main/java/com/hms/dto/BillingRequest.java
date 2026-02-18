package com.hms.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;

public class BillingRequest {
    @JsonFormat(pattern = "dd-MM-yyyy")
    public LocalDate date;
    public Double amount;
    public String paymentMethod;
    public String insuranceProvider;
    public String claimStatus;
    public String remarks;
    public Long patientId;
}
