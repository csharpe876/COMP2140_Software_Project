package com.volunteermanagement.model;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Volunteer profile entity with extended user information.
 * Enhanced with Java 21 patterns and status management.
 */
public class Volunteer {
    private int id;
    private int userId;
    private String skills;
    private String availability;
    private String experience;
    private String interests;
    private String emergencyContact;
    private String emergencyPhone;
    private VolunteerStatus status;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Joined user fields (read-only, populated by queries)
    private String username;
    private String email;
    private String fullName;
    private String phone;
    private String role;

    public enum VolunteerStatus {
        ACTIVE("active"),
        INACTIVE("inactive"),
        SUSPENDED("suspended");

        private final String value;

        VolunteerStatus(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public static VolunteerStatus fromString(String status) {
            if (status == null) return ACTIVE;
            return switch (status.toLowerCase()) {
                case "inactive" -> INACTIVE;
                case "suspended" -> SUSPENDED;
                default -> ACTIVE;
            };
        }
    }

    public Volunteer() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.status = VolunteerStatus.ACTIVE;
    }

    // Core field getters
    public int getId() { return id; }
    public int getUserId() { return userId; }
    public String getSkills() { return skills; }
    public String getAvailability() { return availability; }
    public String getExperience() { return experience; }
    public String getInterests() { return interests; }
    public String getEmergencyContact() { return emergencyContact; }
    public String getEmergencyPhone() { return emergencyPhone; }
    public VolunteerStatus getStatus() { return status; }
    public String getStatusString() { return status.getValue(); }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }

    // Joined field getters (read-only)
    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public String getFullName() { return fullName; }
    public String getPhone() { return phone; }
    public String getRole() { return role; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setUserId(int userId) { this.userId = userId; }
    
    public void setSkills(String skills) {
        this.skills = skills;
        touchUpdatedAt();
    }
    
    public void setAvailability(String availability) {
        this.availability = availability;
        touchUpdatedAt();
    }
    
    public void setExperience(String experience) {
        this.experience = experience;
        touchUpdatedAt();
    }
    
    public void setInterests(String interests) {
        this.interests = interests;
        touchUpdatedAt();
    }
    
    public void setEmergencyContact(String emergencyContact) {
        this.emergencyContact = emergencyContact;
        touchUpdatedAt();
    }
    
    public void setEmergencyPhone(String emergencyPhone) {
        this.emergencyPhone = emergencyPhone;
        touchUpdatedAt();
    }
    
    public void setStatus(VolunteerStatus status) {
        this.status = status != null ? status : VolunteerStatus.ACTIVE;
        touchUpdatedAt();
    }
    
    public void setStatus(String status) {
        this.status = VolunteerStatus.fromString(status);
        touchUpdatedAt();
    }
    
    public void setCreatedAt(LocalDateTime createdAt) { /* DB loading only */ }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    // Joined field setters (for query result population)
    public void setUsername(String username) { this.username = username; }
    public void setEmail(String email) { this.email = email; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setRole(String role) { this.role = role; }

    // Business logic
    private void touchUpdatedAt() {
        this.updatedAt = LocalDateTime.now();
    }

    public boolean isActive() {
        return status == VolunteerStatus.ACTIVE;
    }

    public boolean canVolunteer() {
        return isActive();
    }

    public void activate() {
        setStatus(VolunteerStatus.ACTIVE);
    }

    public void deactivate() {
        setStatus(VolunteerStatus.INACTIVE);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Volunteer volunteer)) return false;
        return id == volunteer.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Volunteer{id=%d, userId=%d, status=%s}"
            .formatted(id, userId, status);
    }
}