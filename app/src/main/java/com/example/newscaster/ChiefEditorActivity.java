package com.example.newscaster;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.annotation.NonNull;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ChiefEditorActivity extends AppCompatActivity {

    private Button addRoleButton;
    private RecyclerView rolesRecyclerView, newsForApprovalRecyclerView, newsOnDashboardRecyclerView;
    private RoleAdapter rolesAdapter;
    private NewsAdapter newsForApprovalAdapter, newsOnDashboardAdapter;
    private List<Role> rolesList;
    private List<News> newsForApprovalList, newsOnDashboardList;
    private CardView manageRolesCard, newsForApprovalCard, newsOnDashboardCard;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chief_editor);

        // Initialize card views
        manageRolesCard = findViewById(R.id.manage_roles_card);
        newsForApprovalCard = findViewById(R.id.news_for_approval_card);
        newsOnDashboardCard = findViewById(R.id.news_on_dashboard_card);

        // Initialize RecyclerViews and buttons
        rolesRecyclerView = findViewById(R.id.roles_recycler_view);
        newsForApprovalRecyclerView = findViewById(R.id.news_for_approval_recycler_view);
        newsOnDashboardRecyclerView = findViewById(R.id.news_on_dashboard_recycler_view);
        addRoleButton = findViewById(R.id.add_role_button);

        // Initialize lists and adapters
        rolesList = new ArrayList<>();
        newsForApprovalList = new ArrayList<>();
        newsOnDashboardList = new ArrayList<>();

        rolesAdapter = new RoleAdapter(rolesList, this::onEditPermissionsClicked, this::onDeleteClicked);
        newsForApprovalAdapter = new NewsAdapter(this, newsForApprovalList);
        newsOnDashboardAdapter = new NewsAdapter(this, newsOnDashboardList);

        // Set up RecyclerViews
        rolesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        rolesRecyclerView.setAdapter(rolesAdapter);

        newsForApprovalRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        newsForApprovalRecyclerView.setAdapter(newsForApprovalAdapter);

        newsOnDashboardRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        newsOnDashboardRecyclerView.setAdapter(newsOnDashboardAdapter);

        // Load data
        loadRoles();
        loadNewsForApproval();
        loadNewsOnDashboard();

        // Set up Add Role button click listener
        addRoleButton.setOnClickListener(v -> showAddRoleDialog());

        // Set up card click listeners
        manageRolesCard.setOnClickListener(v -> toggleCardVisibility(rolesRecyclerView, addRoleButton));
        newsForApprovalCard.setOnClickListener(v -> toggleCardVisibility(newsForApprovalRecyclerView));
        newsOnDashboardCard.setOnClickListener(v -> toggleCardVisibility(newsOnDashboardRecyclerView));
    }

    private void loadRoles() {
        // Dummy data for roles (replace with real data)
        rolesList.add(new Role(1, "Chief Editor", "Chief Editor can access all pages.", "2023-05-25 23:03:56.91", getChiefEditorPermissions()));
        rolesList.add(new Role(2, "Section Editor", "Section Editor can access some pages.", "2023-05-25 23:03:56.91", getSectionEditorPermissions()));
        rolesList.add(new Role(3, "Sub Editor", "Sub Editor can access few pages.", "2023-05-25 23:03:56.91", getSubEditorPermissions()));
        rolesList.add(new Role(4, "Reporter", "Can see and post news.", "2023-05-25 23:03:56.91", getReporterPermissions()));

        rolesAdapter.notifyDataSetChanged();
    }

    private void loadNewsForApproval() {
        // Dummy data for news for approval (replace with real data)
        newsForApprovalList.add(new News("1", "Headline 1", "Article 1", "Category 1", "Location 1", "2023-07-25", "10:00:00", "", "Pending", System.currentTimeMillis()));
        newsForApprovalList.add(new News("2", "Headline 2", "Article 2", "Category 2", "Location 2", "2023-07-25", "11:00:00", "", "Pending", System.currentTimeMillis()));

        newsForApprovalAdapter.notifyDataSetChanged();
    }

    private void loadNewsOnDashboard() {
        // Fetch news from the sectionEditor reference in Firebase
        databaseReference = FirebaseDatabase.getInstance().getReference("subEditor");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                newsOnDashboardList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    News news = snapshot.getValue(News.class);
                    if ("Published".equals(news.getStatus())) {
                        newsOnDashboardList.add(news);
                    }
                }
                newsOnDashboardAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ChiefEditorActivity.this, "Failed to load news.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showAddRoleDialog() {
        AddRoleDialog dialog = new AddRoleDialog(this, newRole -> {
            rolesList.add(newRole);
            rolesAdapter.notifyDataSetChanged();
            Toast.makeText(this, "New role added.", Toast.LENGTH_SHORT).show();
        });
        dialog.show();
    }

    private void onEditPermissionsClicked(Role role) {
        PermissionDialog dialog = new PermissionDialog(this, role, updatedRole -> {
            int index = rolesList.indexOf(role);
            if (index != -1) {
                rolesList.set(index, updatedRole);
                rolesAdapter.notifyDataSetChanged();
                Toast.makeText(this, "Permissions updated.", Toast.LENGTH_SHORT).show();
            }
        });
        dialog.show();
    }

    private void onDeleteClicked(Role role) {
        rolesList.remove(role);
        rolesAdapter.notifyDataSetChanged();
        Toast.makeText(this, "Role deleted.", Toast.LENGTH_SHORT).show();
    }

    private void toggleCardVisibility(View... views) {
        for (View view : views) {
            view.setVisibility(view.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
        }
    }

    private List<String> getChiefEditorPermissions() {
        List<String> permissions = new ArrayList<>();
        permissions.add("Submit News");
        permissions.add("View Submitted News");
        permissions.add("View Own News Status");
        permissions.add("Approve News");
        permissions.add("Reject News");
        permissions.add("View News for Approval");
        permissions.add("Edit News");
        permissions.add("Access All Pages");
        permissions.add("Manage Roles");
        permissions.add("View All News");
        return permissions;
    }

    private List<String> getSectionEditorPermissions() {
        List<String> permissions = new ArrayList<>();
        permissions.add("Submit News");
        permissions.add("View Submitted News");
        permissions.add("View Own News Status");
        permissions.add("Approve News");
        permissions.add("Reject News");
        permissions.add("View News for Approval");
        permissions.add("Edit News");
        permissions.add("View All News");
        return permissions;
    }

    private List<String> getSubEditorPermissions() {
        List<String> permissions = new ArrayList<>();
        permissions.add("Submit News");
        permissions.add("View Submitted News");
        permissions.add("View Own News Status");
        permissions.add("Approve News");
        permissions.add("Reject News");
        permissions.add("View News for Approval");
        return permissions;
    }

    private List<String> getPublisherPermissions() {
        List<String> permissions = new ArrayList<>();
        permissions.add("Submit News");
        permissions.add("View Submitted News");
        permissions.add("View Own News Status");
        return permissions;
    }

    private List<String> getReporterPermissions() {
        List<String> permissions = new ArrayList<>();
        permissions.add("Submit News");
        return permissions;
    }
}
