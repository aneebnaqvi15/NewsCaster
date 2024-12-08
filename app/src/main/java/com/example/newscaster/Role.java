package com.example.newscaster;

import java.util.List;

public class Role {
    private int id;
    private String name;
    private String description;
    private String date;
    private List<String> permissions;

    public Role(int id, String name, String description, String date, List<String> permissions) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.date = date;
        this.permissions = permissions;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getDate() {
        return date;
    }

    public List<String> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<String> permissions) {
        this.permissions = permissions;
    }

    public boolean hasPermission(String permission) {
        return permissions.contains(permission);
    }

    // Add other necessary methods if needed
}
