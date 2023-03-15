package com.example.mmtapp.model_classes;

public class BookingRequest {
    String patientUID;
    String doctorUID;
    String patientName;
    String profilePicUrl;
    String patientContact;

    public BookingRequest() {
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

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public String getProfilePicUrl() {
        return profilePicUrl;
    }

    public void setProfilePicUrl(String profilePicUrl) {
        this.profilePicUrl = profilePicUrl;
    }

    public String getPatientContact() {
        return patientContact;
    }

    public void setPatientContact(String patientContact) {
        this.patientContact = patientContact;
    }

    public BookingRequest(String patientUID, String doctorUID, String patientName, String profilePicUrl, String patientContact) {
        this.patientUID = patientUID;
        this.doctorUID = doctorUID;
        this.patientName = patientName;
        this.profilePicUrl = profilePicUrl;
        this.patientContact = patientContact;
    }
}
