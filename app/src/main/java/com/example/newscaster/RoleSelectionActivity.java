package com.example.newscaster;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class RoleSelectionActivity extends AppCompatActivity {

    private RadioGroup roleRadioGroup;
    private Button nextButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_role_selection);

        // Initialize views
        roleRadioGroup = findViewById(R.id.role_radio_group);
        nextButton = findViewById(R.id.next_button);

        // Set onClick listener for the next button
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the selected radio button
                int selectedId = roleRadioGroup.getCheckedRadioButtonId();

                if (selectedId != -1) {
                    RadioButton selectedRadioButton = findViewById(selectedId);
                    String selectedRole = selectedRadioButton.getText().toString();

                    // Redirect based on the selected role
                    switch (selectedRole) {
                        case "Reporter":
                            navigateToActivity(ReporterActivity.class);
                            break;
                        case "Sub Editor":
                            navigateToActivity(SubEditorActivity.class);
                            break;
                        case "Chief Editor":
                            navigateToActivity(ChiefEditorActivity.class);
                            break;
                        case "Section Editor":
                            navigateToActivity(SectionEditorActivity.class);
                            break;
                        default:
                            Toast.makeText(RoleSelectionActivity.this, "Please select a valid role.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(RoleSelectionActivity.this, "Please select a role.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // Navigate to the specified activity
    private void navigateToActivity(Class<?> targetActivity) {
        Intent intent = new Intent(RoleSelectionActivity.this, targetActivity);
        startActivity(intent);
    }
}

