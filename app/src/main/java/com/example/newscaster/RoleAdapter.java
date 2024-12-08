package com.example.newscaster;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RoleAdapter extends RecyclerView.Adapter<RoleAdapter.RoleViewHolder> {

    private List<Role> rolesList;
    private OnEditPermissionsClickListener onEditPermissionsClickListener;
    private OnDeleteClickListener onDeleteClickListener;

    public RoleAdapter(List<Role> rolesList, OnEditPermissionsClickListener onEditPermissionsClickListener, OnDeleteClickListener onDeleteClickListener) {
        this.rolesList = rolesList;
        this.onEditPermissionsClickListener = onEditPermissionsClickListener;
        this.onDeleteClickListener = onDeleteClickListener;
    }

    @NonNull
    @Override
    public RoleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_role, parent, false);
        return new RoleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RoleViewHolder holder, int position) {
        Role role = rolesList.get(position);
        holder.roleNameTextView.setText(role.getName());
        holder.roleDescriptionTextView.setText(role.getDescription());

        holder.editPermissionsButton.setOnClickListener(v -> onEditPermissionsClickListener.onEditPermissionsClicked(role));
        holder.deleteButton.setOnClickListener(v -> onDeleteClickListener.onDeleteClicked(role));
    }

    @Override
    public int getItemCount() {
        return rolesList.size();
    }

    public interface OnEditPermissionsClickListener {
        void onEditPermissionsClicked(Role role);
    }

    public interface OnDeleteClickListener {
        void onDeleteClicked(Role role);
    }

    public static class RoleViewHolder extends RecyclerView.ViewHolder {

        TextView roleNameTextView;
        TextView roleDescriptionTextView;
        Button editPermissionsButton;
        Button deleteButton;

        public RoleViewHolder(@NonNull View itemView) {
            super(itemView);
            roleNameTextView = itemView.findViewById(R.id.role_name_text_view);
            roleDescriptionTextView = itemView.findViewById(R.id.role_description_text_view);
            editPermissionsButton = itemView.findViewById(R.id.edit_permissions_button);
            deleteButton = itemView.findViewById(R.id.delete_button);
        }
    }
}
