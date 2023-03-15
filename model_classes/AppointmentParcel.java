package com.example.mmtapp.model_classes;

import androidx.annotation.NonNull;

public class AppointmentParcel {
    String bundledUserName;
    String bundledUserUID;

    public AppointmentParcel() {
    }

    public AppointmentParcel(@NonNull String bundledUserName, @NonNull String bundledUserUID) {
        this.bundledUserName = bundledUserName;
        this.bundledUserUID = bundledUserUID;
    }

    @NonNull
    public String getBundledUserName() {
        return bundledUserName;
    }

    public void setBundledUserName(@NonNull String bundledUserName) {
        this.bundledUserName = bundledUserName;
    }

    @NonNull
    public String getBundledUserUID() {
        return bundledUserUID;
    }

    public void setBundledUserUID(@NonNull String bundledUserUID) {
        this.bundledUserUID = bundledUserUID;
    }
}
