package com.example.mmtapp.model_classes;

public class BookingResponse {
    String doctorName;
    String patientName;
    String patientUID;
    String doctorUID;
    String appointmentDate;
    String appointmentMonth;
    String responseDate;
    String responseTime;
    String doctorContact;
    String patientContact;
    String patientProfilePicUrl;
    String doctorProfilePicUrl;

    public BookingResponse() {
    }

    public String getDoctorName() {
        return doctorName;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public String getPatientUID() {
        return patientUID;
    }

    public void setPatientUID(String patientUID) {
        this.patientUID = patientUID;
    }

    public String getDoctorUID() {
        return doctorUID;
    }

    public void setDoctorUID(String doctorUID) {
        this.doctorUID = doctorUID;
    }

    public String getAppointmentDate() {
        return appointmentDate;
    }

    public void setAppointmentDate(String appointmentDate) {
        this.appointmentDate = appointmentDate;
    }

    public String getAppointmentMonth() {
        return appointmentMonth;
    }

    public void setAppointmentMonth(String appointmentMonth) {
        this.appointmentMonth = appointmentMonth;
    }

    public String getResponseDate() {
        return responseDate;
    }

    public void setResponseDate(String responseDate) {
        this.responseDate = responseDate;
    }

    public String getResponseTime() {
        return responseTime;
    }

    public void setResponseTime(String responseTime) {
        this.responseTime = responseTime;
    }

    public String getDoctorContact() {
        return doctorContact;
    }

    public void setDoctorContact(String doctorContact) {
        this.doctorContact = doctorContact;
    }

    public String getPatientContact() {
        return patientContact;
    }

    public void setPatientContact(String patientContact) {
        this.patientContact = patientContact;
    }

    public String getPatientProfilePicUrl() {
        return patientProfilePicUrl;
    }

    public void setPatientProfilePicUrl(String patientProfilePicUrl) {
        this.patientProfilePicUrl = patientProfilePicUrl;
    }

    public String getDoctorProfilePicUrl() {
        return doctorProfilePicUrl;
    }

    public void setDoctorProfilePicUrl(String doctorProfilePicUrl) {
        this.doctorProfilePicUrl = doctorProfilePicUrl;
    }

    public BookingResponse(String doctorName, String patientName, String patientUID, String doctorUID, String appointmentDate, String appointmentMonth, String responseDate, String responseTime, String doctorContact, String patientContact, String patientProfilePicUrl, String doctorProfilePicUrl) {
        this.doctorName = doctorName;
        this.patientName = patientName;
        this.patientUID = patientUID;
        this.doctorUID = doctorUID;
        this.appointmentDate = appointmentDate;
        this.appointmentMonth = appointmentMonth;
        this.responseDate = responseDate;
        this.responseTime = responseTime;
        this.doctorContact = doctorContact;
        this.patientContact = patientContact;
        this.patientProfilePicUrl = patientProfilePicUrl;
        this.doctorProfilePicUrl = doctorProfilePicUrl;
    }
}
