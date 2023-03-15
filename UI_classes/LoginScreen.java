package com.example.mmtapp.UI_classes;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mmtapp.R;
import com.example.mmtapp.doctor_classes.DoctorsNavigationScreen;
import com.example.mmtapp.patient_classes.PatientsNavigationScreen;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

@SuppressLint("Registered")
public class LoginScreen extends AppCompatActivity {
    private static final int RC_SIGN_IN =23 ;
    EditText login_emailText, login_passwordText;
    TextView passwordText, signUpText,emailVerificationView;
    ProgressBar login_progress;
    LinearLayout loginButton;
    private String userEmail, userPassword;
    LinearLayout googleSignInButton;
    GoogleSignInAccount signInAccount;
    GoogleSignInClient mmtClient;


    @Override
    protected void onCreate(@NonNull Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_screen);
        //Initialize views
        login_emailText = findViewById(R.id.login_email_edit_text);
        login_passwordText = findViewById(R.id.login_password_edit_text);
        passwordText = findViewById(R.id.forgot_password_text);
        signUpText = findViewById(R.id.sign_up_text);
        login_progress = findViewById(R.id.login_progressBar);
        loginButton = findViewById(R.id.login_button);
        googleSignInButton=findViewById(R.id.google_signIn_button);
        emailVerificationView=findViewById(R.id.email_verificationTextView);

        FirebaseApp.initializeApp(getApplicationContext());

        signInAccount=GoogleSignIn.getLastSignedInAccount(getApplicationContext());
        createGoogleSignInRequest();

        //Set onclick listeners
        passwordText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), PasswordResetScreen.class));
            }
        });
        //set on click listener on sign up text
        signUpText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), SignUpScreen.class));
            }
        });
        //Set on click listener on login button
        loginButton.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SyntheticAccessor")
            @Override
            public void onClick(View v) {
                userEmail = login_emailText.getText().toString().trim();
                userPassword = login_passwordText.getText().toString().trim();

                if (userEmail.isEmpty()) {
                    login_emailText.setError("email required");
                    login_emailText.requestFocus();
                    return;
                }
                if (!Patterns.EMAIL_ADDRESS.matcher(userEmail).matches()) {
                    login_emailText.setError("please enter a valid email");
                    login_emailText.requestFocus();
                    return;
                }
                if (userPassword.isEmpty()) {
                    login_passwordText.setError("password required");
                    login_passwordText.requestFocus();
                    return;
                }
                if (userPassword.length() < 8) {
                    login_passwordText.setError("password must be at least 8 characters");
                    login_passwordText.requestFocus();
                    return;
                }

                loginUser(userEmail, userPassword);
            }
        });

        googleSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

           if(requestCode==RC_SIGN_IN){
               Task<GoogleSignInAccount>task=GoogleSignIn.getSignedInAccountFromIntent(data);
               try{
                   GoogleSignInAccount account=task.getResult(ApiException.class);
                   assert account != null;
                   firebaseAuthWithGoogle(account);
               } catch (ApiException e) {
                   Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
               }
           }
        }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        AuthCredential firebaseCredential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        FirebaseAuth mAuth=FirebaseAuth.getInstance();
        mAuth.signInWithCredential(firebaseCredential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            startActivity(new Intent(getApplicationContext(),PatientsNavigationScreen.class));
                        } else {
                            Toast.makeText(getApplicationContext(), "Failed to login", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @SuppressLint("SetTextI18n")
    private void loginUser(final String email, final String password) {

        login_progress.setVisibility(View.VISIBLE);
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();
                        assert user != null;
                        if(!user.isEmailVerified()){
                            login_progress.setVisibility(View.GONE);
                            emailVerificationView.setVisibility(View.VISIBLE);
                            emailVerificationView.setText("Email account is not verified!");
                            emailVerificationView.setTextColor(Color.parseColor("#E30929"));
                        } else{
                            login_progress.setVisibility(View.GONE);
                            if (!userPassword.equals("@dmin1234")) {
                                startActivity(new Intent(getApplicationContext(), PatientsNavigationScreen.class));
                                finish();
                            } else {
                                startActivity(new Intent(getApplicationContext(), DoctorsNavigationScreen.class));
                                finish();
                            }
                        }

                    } else {
                        login_progress.setVisibility(View.GONE);
                      Toast.makeText(LoginScreen.this, "Unable to login,check your password or internet connection", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(e -> {
                    login_progress.setVisibility(View.GONE);
                    Toast.makeText(getApplicationContext(), "If you are not registered please Register", Toast.LENGTH_LONG).show();
                });
    }
   void createGoogleSignInRequest(){
       GoogleSignInOptions gso=new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
               .requestIdToken(String.valueOf(R.string.default_web_client_id))
               .requestEmail()
               .build();
      mmtClient= GoogleSignIn.getClient(getApplicationContext(),gso);

   }
   void signIn(){
       Intent signInIntent=mmtClient.getSignInIntent();
       startActivityForResult(signInIntent,RC_SIGN_IN);
   }
}
