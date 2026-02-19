package com.hms.dto;

public class AppointmentRequest {

    private Long doctorId;
    private Long patientId;
    private String date;

    public Long getDoctorId() { return doctorId; }
    public void setDoctorId(Long doctorId) { this.doctorId = doctorId; }

    public Long getPatientId() { return patientId; }
    public void setPatientId(Long patientId) { this.patientId = patientId; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
}
