package com.example.mmtapp.UI_classes;

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
import com.example.mmtapp.model_classes.User;
import com.example.mmtapp.patient_classes.PatientsNavigationScreen;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
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

public class ProfileUploadScreen extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 0;
    EditText firstNameEditText, surNameEditText, contactEditText, addressEditText;
    ProgressBar profileUploadProgress;
    LinearLayout profileImageView;
    LinearLayout uploadButton;
    String firstName, surName, address, profilePicUrl, myUID, contact;
    Uri imageUri;
    Task<Uri> getImageDownloadUrl;
    User user;
    DatabaseReference userRef;
    StorageReference pathReference;

    @Override
    protected void onCreate(@NonNull Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_upload_screen);
        firstNameEditText = findViewById(R.id.patient_first_name_edit_text);
        surNameEditText = findViewById(R.id.patient_surname_edit_text);
        contactEditText = findViewById(R.id.patient_contact_edit_text);
        addressEditText = findViewById(R.id.patient_address_edit_text);
        uploadButton = findViewById(R.id.upload_button);
        profileImageView = findViewById(R.id.patient_profile_pic_imageView);
        profileUploadProgress = findViewById(R.id.profile_upload_progressBar);


        FirebaseApp.initializeApp(getApplicationContext());
        myUID = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        user = new User();

        try {
            userRef = FirebaseDatabase.getInstance("https://mmt-services-default-rtdb.firebaseio.com/").getReference("Users").child(myUID);

        } catch (DatabaseException e) {
            Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
        }
        try{
            pathReference = FirebaseStorage.getInstance().getReference().child(myUID);
        }catch(Exception e){
            e.printStackTrace();
        }

        profileImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPermissions();
            }
        });
        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firstName = firstNameEditText.getText().toString().trim();
                surName = surNameEditText.getText().toString().trim();
                contact = contactEditText.getText().toString();
                address = addressEditText.getText().toString().trim();
                //perform validations

                if (firstName.isEmpty()) {
                    firstNameEditText.requestFocus();
                    firstNameEditText.setError("first name required");
                    return;
                }
                if (surName.isEmpty()) {
                    surNameEditText.requestFocus();
                    surNameEditText.setError("surname required");
                    return;
                }
                if (address.isEmpty()) {
                    addressEditText.requestFocus();
                    addressEditText.setError("address required");
                    return;
                }
                if (contactEditText.getText().toString().isEmpty()) {
                    contactEditText.requestFocus();
                    contactEditText.setError("contact required");
                    return;
                }
                if (imageUri == null) {
                    Toast.makeText(getApplicationContext(), "Please select an image", Toast.LENGTH_LONG).show();
                    return;
                }
                profileUploadProgress.setVisibility(View.VISIBLE);
                uploadImage();

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
                            profileImageView.setForeground(resource);
                        }
                    });
        }
    }

    private void imageChooser() {
        Intent imageIntent = new Intent();
        imageIntent.setType("image/*");
        imageIntent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(imageIntent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    void checkPermissions() {
        int permission = getApplicationContext().checkCallingPermission("Manifest.permission.READ_EXTERNAL_STORAGE");
        permission += getApplicationContext().checkCallingPermission("Manifest.permission.WRITE_EXTERNAL_STORAGE");
        if (permission != 0) {
            imageChooser();
        } else {
            Toast.makeText(this, "Allow this app to modify storage from settings", Toast.LENGTH_LONG).show();
        }
    }

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
                Toast.makeText(this, "Please select a profile image", Toast.LENGTH_LONG).show();
            }
    }

    void uploadProfileInfo() {
            user.setFirstName(firstName);
            user.setProfilePicUrl(profilePicUrl);
            user.setSurname(surName);
            user.setAddress(address);
            user.setUserUID(myUID);
            user.setContact(contact);

            try {

                userRef.setValue(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        profileUploadProgress.setVisibility(View.GONE);
                        startActivity(new Intent(getApplicationContext(), PatientsNavigationScreen.class));
                        Toast.makeText(getApplicationContext(), "Profile Uploaded successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        profileUploadProgress.setVisibility(View.GONE);
                        Toast.makeText(ProfileUploadScreen.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
            } catch (Exception e){
                e.printStackTrace();
            }
    }
}