package com.example.newscaster;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.regex.Pattern;

public class AdminSignUpActivity extends AppCompatActivity {

    private EditText editTextUsername, editTextEmail, editTextCnic, editTextPhone, editTextAddress, editTextPassword, editTextConfirmPassword;
    private Button buttonSignUp;
    private TextView loginRedirectText;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    // Password pattern to enforce: at least 1 uppercase, 1 lowercase, 1 digit, 1 special character, and 8 characters long
    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_signup);

        // Initialize Firebase Auth and Database Reference
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference("adminPanel");

        // Initialize views
        editTextUsername = findViewById(R.id.signup_username);
        editTextEmail = findViewById(R.id.signup_email);
        editTextCnic = findViewById(R.id.signup_cnic);
        editTextPhone = findViewById(R.id.signup_phone);
        editTextAddress = findViewById(R.id.signup_address);
        editTextPassword = findViewById(R.id.signup_password);
        editTextConfirmPassword = findViewById(R.id.signup_confirm_password);
        buttonSignUp = findViewById(R.id.signup_button);
        loginRedirectText = findViewById(R.id.loginRedirectText);

        // Set button click listener
        buttonSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleSignUp();
            }
        });

        // Set login redirect click listener
        loginRedirectText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                redirectToLogin();
            }
        });
    }

    private void handleSignUp() {
        String username = editTextUsername.getText().toString().trim();
        String email = editTextEmail.getText().toString().trim();
        String cnic = editTextCnic.getText().toString().trim();
        String phone = editTextPhone.getText().toString().trim();
        String address = editTextAddress.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        String confirmPassword = editTextConfirmPassword.getText().toString().trim();

        // Validate inputs
        if (TextUtils.isEmpty(username)) {
            editTextUsername.setError("Username is required");
            return;
        }

        if (TextUtils.isEmpty(email)) {
            editTextEmail.setError("Email is required");
            return;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextEmail.setError("Please enter a valid email address");
            return;
        }

        if (TextUtils.isEmpty(cnic)) {
            editTextCnic.setError("CNIC is required");
            return;
        }

        if (TextUtils.isEmpty(phone)) {
            editTextPhone.setError("Phone number is required");
            return;
        }

        if (TextUtils.isEmpty(address)) {
            editTextAddress.setError("Address is required");
            return;
        }

        if (TextUtils.isEmpty(password)) {
            editTextPassword.setError("Password is required");
            return;
        } else if (!PASSWORD_PATTERN.matcher(password).matches()) {
            editTextPassword.setError("Password must be at least 8 characters long, contain at least one uppercase letter, one lowercase letter, one number, and one special character");
            return;
        }

        if (TextUtils.isEmpty(confirmPassword)) {
            editTextConfirmPassword.setError("Please confirm your password");
            return;
        } else if (!password.equals(confirmPassword)) {
            editTextConfirmPassword.setError("Passwords do not match");
            return;
        }

        // Create admin user with Firebase Authentication
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Send email verification
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null) {
                                user.sendEmailVerification()
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Toast.makeText(AdminSignUpActivity.this, "Verification email sent. Please check your email.", Toast.LENGTH_LONG).show();

                                                    // Get user ID
                                                    String userId = user.getUid();

                                                    // Create an Admin object
                                                    Admin admin = new Admin(userId, username, email, cnic, phone, address, password);

                                                    // Save admin details in the database
                                                    mDatabase.child(userId).setValue(admin)
                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    if (task.isSuccessful()) {
                                                                        // Redirect to login screen
                                                                        redirectToLogin();
                                                                    } else {
                                                                        Toast.makeText(AdminSignUpActivity.this, "Failed to save admin details: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                                                    }
                                                                }
                                                            });
                                                } else {
                                                    Toast.makeText(AdminSignUpActivity.this, "Failed to send verification email: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                                }
                                            }
                                        });
                            }
                        } else {
                            Toast.makeText(AdminSignUpActivity.this, "Sign Up Failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void redirectToLogin() {
        Intent intent = new Intent(AdminSignUpActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    // Admin class to represent admin details
    public static class Admin {
        public String userId;
        public String username;
        public String email;
        public String cnic;
        public String phone;
        public String address;
        public String password;

        public Admin(String userId, String username, String email, String cnic, String phone, String address, String password) {
            this.userId = userId;
            this.username = username;
            this.email = email;
            this.cnic = cnic;
            this.phone = phone;
            this.address = address;
            this.password = password;
        }
    }
}
