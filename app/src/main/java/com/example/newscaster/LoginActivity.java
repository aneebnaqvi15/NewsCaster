package com.example.newscaster;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    EditText loginUsername, loginPassword;
    Button loginButton;
    TextView signupRedirectText, forgotPassword, guestAccessText;
    RadioButton loginAsUser, loginAsAdmin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginUsername = findViewById(R.id.login_username);
        loginPassword = findViewById(R.id.login_password);
        signupRedirectText = findViewById(R.id.signupRedirectText);
        forgotPassword = findViewById(R.id.forgot_password);
        loginButton = findViewById(R.id.login_button);
        loginAsUser = findViewById(R.id.user);
        loginAsAdmin = findViewById(R.id.admin);
        guestAccessText = findViewById(R.id.guestAccessText);  // Find the guest access TextView

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!validateUsername() | !validatePassword()) {
                    return;
                }
                if (loginAsAdmin.isChecked()) {
                    checkAdmin();
                } else {
                    checkUser();
                }
            }
        });

        signupRedirectText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (loginAsAdmin.isChecked()) {
                    Intent intent = new Intent(LoginActivity.this, AdminSignUpActivity.class);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
                    startActivity(intent);
                }
            }
        });

        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showForgotPasswordDialog();
            }
        });

        guestAccessText.setOnClickListener(new View.OnClickListener() {  // Add OnClickListener for guest access
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    public Boolean validateUsername() {
        String val = loginUsername.getText().toString();
        if (val.isEmpty()) {
            loginUsername.setError("Username cannot be empty");
            return false;
        } else {
            loginUsername.setError(null);
            return true;
        }
    }

    public Boolean validatePassword() {
        String val = loginPassword.getText().toString();
        if (val.isEmpty()) {
            loginPassword.setError("Password cannot be empty");
            return false;
        } else {
            loginPassword.setError(null);
            return true;
        }
    }

    public void checkUser() {
        String userUsername = loginUsername.getText().toString().trim();
        String userPassword = loginPassword.getText().toString().trim();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users").child(userUsername);

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String passwordFromDB = snapshot.child("password").getValue(String.class);
                    if (passwordFromDB != null && passwordFromDB.equals(userPassword)) {
                        String nameFromDB = snapshot.child("name").getValue(String.class);
                        String emailFromDB = snapshot.child("email").getValue(String.class);
                        String usernameFromDB = snapshot.child("username").getValue(String.class);

                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);

                        intent.putExtra("name", nameFromDB);
                        intent.putExtra("email", emailFromDB);
                        intent.putExtra("username", usernameFromDB);
                        intent.putExtra("password", passwordFromDB);

                        startActivity(intent);
                        finish();
                    } else {
                        loginPassword.setError("Invalid Credentials");
                        loginPassword.requestFocus();
                    }
                } else {
                    loginUsername.setError("User does not exist");
                    loginUsername.requestFocus();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("LoginActivity", "Database error: " + error.getMessage());
            }
        });
    }

    public void checkAdmin() {
        String adminUsername = loginUsername.getText().toString().trim();
        String adminPassword = loginPassword.getText().toString().trim();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("adminPanel");

        reference.orderByChild("username").equalTo(adminUsername).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot adminSnapshot : snapshot.getChildren()) {
                        String passwordFromDB = adminSnapshot.child("password").getValue(String.class);
                        if (passwordFromDB != null && passwordFromDB.equals(adminPassword)) {
                            String nameFromDB = adminSnapshot.child("username").getValue(String.class);
                            String emailFromDB = adminSnapshot.child("email").getValue(String.class);

                            Intent intent = new Intent(LoginActivity.this, RoleSelectionActivity.class);
                            intent.putExtra("username", nameFromDB);
                            intent.putExtra("email", emailFromDB);

                            startActivity(intent);
                            finish();
                        } else {
                            loginPassword.setError("Invalid Credentials");
                            loginPassword.requestFocus();
                        }
                    }
                } else {
                    loginUsername.setError("Admin does not exist");
                    loginUsername.requestFocus();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("LoginActivity", "Database error: " + error.getMessage());
            }
        });
    }

    private void showForgotPasswordDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.activity_forget_password2);

        EditText emailBox = dialog.findViewById(R.id.emailBox);
        Button resetButton = dialog.findViewById(R.id.btnReset);
        Button cancelButton = dialog.findViewById(R.id.btnCancel);

        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = emailBox.getText().toString().trim();
                if (!email.isEmpty()) {
                    // Handle password reset logic here
                    sendPasswordResetEmail(email);
                    dialog.dismiss();
                } else {
                    emailBox.setError("Email cannot be empty");
                }
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void sendPasswordResetEmail(String email) {
        // Implement the logic to send a password reset email
        Toast.makeText(LoginActivity.this, "Password reset email sent to " + email, Toast.LENGTH_SHORT).show();
    }
}