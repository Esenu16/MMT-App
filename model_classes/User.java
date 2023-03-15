package com.example.mmtapp.model_classes;

public class User {
    String firstName;
    String surname;

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

    public String getProfilePicUrl() {
        return profilePicUrl;
    }

    public void setProfilePicUrl(String profilePicUrl) {
        this.profilePicUrl = profilePicUrl;
    }

    public String getUserUID() {
        return userUID;
    }

    public void setUserUID(String userUID) {
        this.userUID = userUID;
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

    String address;
    String profilePicUrl;
    String userUID;
    String contact;
    String password;

    public User() {
    }

    public User(String firstName, String surname, String address, String profilePicUrl, String userUID, String contact, String password) {
        this.firstName = firstName;
        this.surname = surname;
        this.address = address;
        this.profilePicUrl = profilePicUrl;
        this.userUID = userUID;
        this.contact = contact;
        this.password = password;
    }
}
