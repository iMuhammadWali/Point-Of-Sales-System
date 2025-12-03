package com.pos.models;


// This is a singleton class
public class UserSession {
    // Contain a self-object
    private static UserSession instance;

    private int userID;
    private String username;
    private String role;
    private String fullName;

    private UserSession() {} // Private constructor for singleton

    public static UserSession getInstance() {
        if (instance == null) {
            instance = new UserSession();
        }
        return instance;
    }

    // Getters and Setters
    public int getUserID() { return userID; }
    public void setUserID(int userID) { this.userID = userID; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getFullName() { return fullName; }
    public void setFullName(String firstName, String lastName) { this.fullName = firstName + ' ' + lastName; }

    public void clearSession() {
        this.userID = 0;
        this.username = null;
        this.role = null;
        this.fullName = null;
    }

    public boolean isLoggedIn() {
        return userID > 0 && username != null;
    }

    public boolean hasRole(String requiredRole) {
        return requiredRole.equalsIgnoreCase(this.role);
    }

    public boolean isAdmin() {
        return "admin".equalsIgnoreCase(this.role);
    }

    public boolean isManager() {
        return "manager".equalsIgnoreCase(this.role);
    }

    public boolean isCashier() {
        return "cashier".equalsIgnoreCase(this.role);
    }
}