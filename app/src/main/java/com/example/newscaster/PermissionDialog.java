package com.example.newscaster;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

public class PermissionDialog extends Dialog {

    private Role role;
    private OnPermissionsUpdatedListener listener;

    public PermissionDialog(@NonNull Context context, Role role, OnPermissionsUpdatedListener listener) {
        super(context);
        this.role = role;
        this.listener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_permissions);

        CheckBox submitNewsCheckBox = findViewById(R.id.checkbox_submit_news);
        CheckBox viewSubmittedNewsCheckBox = findViewById(R.id.checkbox_view_submitted_news);
        CheckBox viewOwnNewsStatusCheckBox = findViewById(R.id.checkbox_view_own_news_status);
        CheckBox approveNewsCheckBox = findViewById(R.id.checkbox_approve_news);
        CheckBox rejectNewsCheckBox = findViewById(R.id.checkbox_reject_news);
        CheckBox viewNewsForApprovalCheckBox = findViewById(R.id.checkbox_view_news_for_approval);
        CheckBox editNewsCheckBox = findViewById(R.id.checkbox_edit_news);
        CheckBox accessAllPagesCheckBox = findViewById(R.id.checkbox_access_all_pages);
        CheckBox manageRolesCheckBox = findViewById(R.id.checkbox_manage_roles);
        CheckBox viewAllNewsCheckBox = findViewById(R.id.checkbox_view_all_news);

        // Set the initial state of the checkboxes based on the role's permissions
        submitNewsCheckBox.setChecked(role.hasPermission("Submit News"));
        viewSubmittedNewsCheckBox.setChecked(role.hasPermission("View Submitted News"));
        viewOwnNewsStatusCheckBox.setChecked(role.hasPermission("View Own News Status"));
        approveNewsCheckBox.setChecked(role.hasPermission("Approve News"));
        rejectNewsCheckBox.setChecked(role.hasPermission("Reject News"));
        viewNewsForApprovalCheckBox.setChecked(role.hasPermission("View News for Approval"));
        editNewsCheckBox.setChecked(role.hasPermission("Edit News"));
        accessAllPagesCheckBox.setChecked(role.hasPermission("Access All Pages"));
        manageRolesCheckBox.setChecked(role.hasPermission("Manage Roles"));
        viewAllNewsCheckBox.setChecked(role.hasPermission("View All News"));

        Button saveButton = findViewById(R.id.button_save_permissions);
        saveButton.setOnClickListener(v -> {
            List<String> updatedPermissions = new ArrayList<>();
            if (submitNewsCheckBox.isChecked()) updatedPermissions.add("Submit News");
            if (viewSubmittedNewsCheckBox.isChecked()) updatedPermissions.add("View Submitted News");
            if (viewOwnNewsStatusCheckBox.isChecked()) updatedPermissions.add("View Own News Status");
            if (approveNewsCheckBox.isChecked()) updatedPermissions.add("Approve News");
            if (rejectNewsCheckBox.isChecked()) updatedPermissions.add("Reject News");
            if (viewNewsForApprovalCheckBox.isChecked()) updatedPermissions.add("View News for Approval");
            if (editNewsCheckBox.isChecked()) updatedPermissions.add("Edit News");
            if (accessAllPagesCheckBox.isChecked()) updatedPermissions.add("Access All Pages");
            if (manageRolesCheckBox.isChecked()) updatedPermissions.add("Manage Roles");
            if (viewAllNewsCheckBox.isChecked()) updatedPermissions.add("View All News");

            role.setPermissions(updatedPermissions);
            listener.onPermissionsUpdated(role);
            dismiss();
        });
    }

    public interface OnPermissionsUpdatedListener {
        void onPermissionsUpdated(Role updatedRole);
    }
}
