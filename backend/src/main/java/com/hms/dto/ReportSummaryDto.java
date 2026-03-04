package com.hms.dto;

public class ReportSummaryDto {
    private long totalPatients;
    private long totalDoctors;
    private long totalAppointments;
    private long totalBills;

    public ReportSummaryDto() { }

    public ReportSummaryDto(long totalPatients, long totalDoctors, long totalAppointments, long totalBills) {
        this.totalPatients = totalPatients;
        this.totalDoctors = totalDoctors;
        this.totalAppointments = totalAppointments;
        this.totalBills = totalBills;
    }

    public long getTotalPatients() { return totalPatients; }
    public void setTotalPatients(long totalPatients) { this.totalPatients = totalPatients; }

    public long getTotalDoctors() { return totalDoctors; }
    public void setTotalDoctors(long totalDoctors) { this.totalDoctors = totalDoctors; }

    public long getTotalAppointments() { return totalAppointments; }
    public void setTotalAppointments(long totalAppointments) { this.totalAppointments = totalAppointments; }

    public long getTotalBills() { return totalBills; }
    public void setTotalBills(long totalBills) { this.totalBills = totalBills; }
}
