package com.pos.models;

import java.sql.Timestamp;

public class User {
    private Integer userID;
    private String firstName;
    private String lastName;
    private String username;
    private String passwordHash;
    private String role;
    private Timestamp createdAt;

    // Constructor for creating new user
    public User(Integer userID, String firstName, String lastName, String username, String role) {
        this.userID = userID;
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
//        this.passwordHash = passwordHash;
        this.role = role;
    }

    // Constructor for existing user from database
    public User(Integer userID, String firstName, String lastName, String username, String passwordHash, String role, Timestamp createdAt) {
        this.userID = userID;
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.passwordHash = passwordHash;
        this.role = role;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public Integer getUserID() { return userID; }
    public void setUserID(Integer userID) { this.userID = userID; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getFullName(){
        return firstName + " " + lastName;
    }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    // Utility method to print user details
    public void printUser() {
        System.out.println("UserID: " + userID);
        System.out.println("Name: " + firstName + " " + lastName);
        System.out.println("Username: " + username);
        System.out.println("Role: " + role);
        System.out.println("Created At: " + createdAt);
        System.out.println("----------------------------");
    }
}