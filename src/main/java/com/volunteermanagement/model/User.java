package com.volunteermanagement.model;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * User entity representing system users (volunteers and admins).
 * Enhanced with Java 21 patterns, validation, and immutability where appropriate.
 */
public class User {
    private int id;
    private String username;
    private String email;
    private String passwordHash;
    private String fullName;
    private String phone;
    private UserRole role;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public enum UserRole {
        VOLUNTEER("volunteer"),
        ADMIN("admin");

        private final String value;

        UserRole(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public static UserRole fromString(String role) {
            if (role == null) return VOLUNTEER;
            return switch (role.toLowerCase()) {
                case "admin" -> ADMIN;
                default -> VOLUNTEER;
            };
        }
    }

    public User() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.role = UserRole.VOLUNTEER;
    }

    // Getters
    public int getId() { return id; }
    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public String getPasswordHash() { return passwordHash; }
    public String getFullName() { return fullName; }
    public String getPhone() { return phone; }
    public UserRole getRole() { return role; }
    public String getRoleString() { return role.getValue(); }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }

    // Setters with touch update timestamp
    public void setId(int id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
        touchUpdatedAt();
    }

    public void setEmail(String email) {
        this.email = email;
        touchUpdatedAt();
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
        touchUpdatedAt();
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
        touchUpdatedAt();
    }

    public void setPhone(String phone) {
        this.phone = phone;
        touchUpdatedAt();
    }

    public void setRole(UserRole role) {
        this.role = role != null ? role : UserRole.VOLUNTEER;
        touchUpdatedAt();
    }

    public void setRole(String role) {
        this.role = UserRole.fromString(role);
        touchUpdatedAt();
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        // Allowed only for database loading
    }

    // Utility methods
    private void touchUpdatedAt() {
        this.updatedAt = LocalDateTime.now();
    }

    public boolean isAdmin() {
        return role == UserRole.ADMIN;
    }

    public boolean isVolunteer() {
        return role == UserRole.VOLUNTEER;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User user)) return false;
        return id == user.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "User{id=%d, username='%s', email='%s', role=%s}"
            .formatted(id, username, email, role);
    }
}