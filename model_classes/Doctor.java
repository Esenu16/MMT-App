package com.example.mmtapp.model_classes;

public class Doctor {
    String firstName;
    String surname;
    String address;
    String licenseNumber;
    String profilePicUrl;
    String UserUID;
    String contact;
    String password;

    public Doctor() {
    }

    public Doctor(String firstName, String surname, String address, String licenseNumber, String profilePicUrl, String userUID, String contact, String password) {
        this.firstName = firstName;
        this.surname = surname;
        this.address = address;
        this.licenseNumber = licenseNumber;
        this.profilePicUrl = profilePicUrl;
        UserUID = userUID;
        this.contact = contact;
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getLicenseNumber() {
        return licenseNumber;
    }

    public void setLicenseNumber(String licenseNumber) {
        this.licenseNumber = licenseNumber;
    }

    public String getProfilePicUrl() {
        return profilePicUrl;
    }

    public void setProfilePicUrl(String profilePicUrl) {
        this.profilePicUrl = profilePicUrl;
    }

    public String getUserUID() {
        return UserUID;
    }

    public void setUserUID(String userUID) {
        UserUID = userUID;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}