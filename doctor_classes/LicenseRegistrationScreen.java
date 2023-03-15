package com.example.mmtapp.doctor_classes;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.mmtapp.R;
import com.example.mmtapp.model_classes.Doctor;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Objects;

public class LicenseRegistrationScreen extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 0;
    EditText doctorFirstNameEditText;
    EditText doctorSurnameEditText;
    EditText doctorContactEditText;
    EditText doctorAddressEditText;
    EditText doctorLicenseEditText;
    ProgressBar doctorLicenseRegistrationProgressBar;
    LinearLayout doctorProfilePicLayout;
    LinearLayout submitButton;
    String doctorFirstName, doctorSurname, profilePicUrl, doctorAddress, licenseNumber, myUID, doctorContact;
    Uri imageUri;
    Task<Uri> getImageDownloadUrl;
    Doctor doctor;
    DatabaseReference docRef;
    StorageReference pathReference;

    @Override
    protected void onCreate(@NonNull Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.license_registration_screen);

        //initialize views
        doctorFirstNameEditText = findViewById(R.id.doctor_first_name_edit_text);
        doctorSurnameEditText = findViewById(R.id.doctor_surname_edit_text);
        doctorContactEditText = findViewById(R.id.doctor_contact_edit_text);
        doctorAddressEditText = findViewById(R.id.doctor_address_edit_text);
        doctorLicenseEditText = findViewById(R.id.doctor_license_no_edit_text);
        doctorLicenseRegistrationProgressBar = findViewById(R.id.license_registration_progressBar);
        submitButton = findViewById(R.id.submit_button);
        doctorProfilePicLayout = findViewById(R.id.doctor_profile_pic_linearLayout);

        //initialize firebase and the real time database
        FirebaseApp.initializeApp(getApplicationContext());
        myUID = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        doctor = new Doctor();


        try{
           docRef = FirebaseDatabase.getInstance("https://mmt-services-default-rtdb.firebaseio.com/").getReference("Doctors");;
        } catch (DatabaseException e){
            e.printStackTrace();
        }

        try {
            pathReference = FirebaseStorage.getInstance().getReference().child(myUID);
        } catch (Exception e){
            e.printStackTrace();
        }

        //Set on click listener to submit button
        //perform some validations
        //upload data to firebase realtime database
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doctorFirstName = doctorFirstNameEditText.getText().toString().trim();
                doctorSurname = doctorSurnameEditText.getText().toString().trim();
                doctorAddress = doctorAddressEditText.getText().toString().trim();
                licenseNumber = doctorLicenseEditText.getText().toString().trim();
                doctorContact = doctorContactEditText.getText().toString().trim();

                if (doctorFirstName.isEmpty()) {
                    doctorFirstNameEditText.setError("name required");
                    doctorFirstNameEditText.requestFocus();
                    return;
                }
                if (doctorSurname.isEmpty()) {
                    doctorSurnameEditText.setError("name required");
                    doctorSurnameEditText.requestFocus();
                    return;
                }
                if (doctorAddress.isEmpty()) {
                    doctorAddressEditText.setError("address required");
                    doctorAddressEditText.requestFocus();
                    return;
                }
                if (licenseNumber.isEmpty()) {
                    doctorLicenseEditText.setError("Licence number required");
                    doctorLicenseEditText.requestFocus();
                    return;
                }
                if (doctorContact.isEmpty()) {
                    doctorContactEditText.setError("Enter your contact please");
                    doctorContactEditText.requestFocus();
                    return;
                }
                if (imageUri == null) {
                    Toast.makeText(LicenseRegistrationScreen.this, "Please select an image", Toast.LENGTH_LONG).show();
                    return;
                }
                doctorLicenseRegistrationProgressBar.setVisibility(View.VISIBLE);
                uploadImage();
            }
        });

        //Choose profile image
        doctorProfilePicLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPermissions();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            Glide.with(getApplicationContext())
                    .load(imageUri)
                    .into(new SimpleTarget<Drawable>() {
                        @Override
                        public void onResourceReady(Drawable resource, Transition<? super Drawable> transition) {
                            doctorProfilePicLayout.setForeground(resource);
                        }
                    });
        }
    }

    //uploads image to firebase storage
    void uploadImage() {
        if (imageUri != null) {

                pathReference.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {
                            getImageDownloadUrl=pathReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    profilePicUrl=uri.toString();
                                    uploadProfileInfo();
                                }
                            });
                        } else {
                            profilePicUrl = "null";
                        }
                    }
                });

        } else {
            Toast.makeText(this, "Please select a profile image", Toast.LENGTH_SHORT).show();
        }
    }

    void chooseProfilePic() {
        Intent imageIntent = new Intent();
        imageIntent.setType("image/*");
        imageIntent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(imageIntent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    void checkPermissions() {
        int permission = getApplicationContext().checkCallingPermission("Manifest.permission.READ_EXTERNAL_STORAGE");
        permission += getApplicationContext().checkCallingPermission("Manifest.permission.WRITE_EXTERNAL_STORAGE");
        if (permission != 0) {
            chooseProfilePic();
        } else {
            Toast.makeText(this, "Allow this app to modify storage from settings", Toast.LENGTH_LONG).show();
        }
    }

    void uploadProfileInfo() {

        doctor.setFirstName(doctorFirstName);
        doctor.setSurname(doctorSurname);
        doctor.setAddress(doctorAddress);
        doctor.setProfilePicUrl(profilePicUrl);
        doctor.setLicenseNumber(licenseNumber);
        doctor.setUserUID(myUID);
        doctor.setContact(doctorContact);

        try {
            docRef.child(myUID).setValue(doctor).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        doctorLicenseRegistrationProgressBar.setVisibility(View.GONE);
                        Toast.makeText(getApplicationContext(), "Profile uploaded successfully", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(), DoctorsNavigationScreen.class));
                        finish();
                    } else {
                        Toast.makeText(getApplicationContext(), "Failed to upload image", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}