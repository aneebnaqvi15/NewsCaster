package com.example.newscaster;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AddRoleDialog extends Dialog {
    private EditText roleNameEditText;
    private EditText roleDescriptionEditText;
    private Button saveRoleButton;
    private Button cancelButton;
    private RoleAddedListener roleAddedListener;

    public AddRoleDialog(Context context, RoleAddedListener listener) {
        super(context);
        setContentView(R.layout.dialog_add_role);
        roleAddedListener = listener;

        roleNameEditText = findViewById(R.id.role_name_edit_text);
        roleDescriptionEditText = findViewById(R.id.role_description_edit_text);
        saveRoleButton = findViewById(R.id.save_role_button);
        cancelButton = findViewById(R.id.cancel_button);

        saveRoleButton.setOnClickListener(v -> {
            String roleName = roleNameEditText.getText().toString().trim();
            String roleDescription = roleDescriptionEditText.getText().toString().trim();

            if (roleName.isEmpty() || roleDescription.isEmpty()) {
                Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show();
            } else {
                List<String> defaultPermissions = new ArrayList<>();
                Role newRole = new Role(0, roleName, roleDescription, getCurrentDateTime(), defaultPermissions);
                roleAddedListener.onRoleAdded(newRole);
                dismiss();
            }
        });

        cancelButton.setOnClickListener(v -> dismiss());
    }

    private String getCurrentDateTime() {
        // Method to get the current date and time in the desired format
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
    }

    public interface RoleAddedListener {
        void onRoleAdded(Role role);
    }
}
