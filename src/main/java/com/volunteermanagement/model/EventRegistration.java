package com.volunteermanagement.model;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Event registration linking volunteers to events.
 * Enhanced with Java 21 patterns and status management.
 */
public class EventRegistration {
    private int id;
    private int eventId;
    private int volunteerId;
    private int userId;
    private RegistrationStatus status;
    private String notes;
    private final LocalDateTime registeredAt;
    private LocalDateTime updatedAt;

    public enum RegistrationStatus {
        CONFIRMED("confirmed"),
        PENDING("pending"),
        CANCELLED("cancelled"),
        ATTENDED("attended"),
        NO_SHOW("no_show");

        private final String value;

        RegistrationStatus(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public static RegistrationStatus fromString(String status) {
            if (status == null) return PENDING;
            return switch (status.toLowerCase()) {
                case "confirmed" -> CONFIRMED;
                case "cancelled" -> CANCELLED;
                case "attended" -> ATTENDED;
                case "no_show" -> NO_SHOW;
                default -> PENDING;
            };
        }
    }

    public EventRegistration() {
        this.registeredAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.status = RegistrationStatus.PENDING;
    }

    // Getters
    public int getId() { return id; }
    public int getEventId() { return eventId; }
    public int getVolunteerId() { return volunteerId; }
    public int getUserId() { return userId; }
    public RegistrationStatus getStatus() { return status; }
    public String getStatusString() { return status.getValue(); }
    public String getNotes() { return notes; }
    public LocalDateTime getRegisteredAt() { return registeredAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setEventId(int eventId) { this.eventId = eventId; }
    public void setVolunteerId(int volunteerId) { this.volunteerId = volunteerId; }
    public void setUserId(int userId) { this.userId = userId; }
    
    public void setStatus(RegistrationStatus status) {
        this.status = status != null ? status : RegistrationStatus.PENDING;
        touchUpdatedAt();
    }
    
    public void setStatus(String status) {
        this.status = RegistrationStatus.fromString(status);
        touchUpdatedAt();
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
        touchUpdatedAt();
    }
    
    public void setRegisteredAt(LocalDateTime registeredAt) { /* DB loading only */ }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    // Business logic
    private void touchUpdatedAt() {
        this.updatedAt = LocalDateTime.now();
    }

    public boolean isConfirmed() {
        return status == RegistrationStatus.CONFIRMED;
    }

    public boolean isPending() {
        return status == RegistrationStatus.PENDING;
    }

    public boolean isCancelled() {
        return status == RegistrationStatus.CANCELLED;
    }

    public boolean isActive() {
        return status == RegistrationStatus.CONFIRMED || status == RegistrationStatus.PENDING;
    }

    public void confirm() {
        setStatus(RegistrationStatus.CONFIRMED);
    }

    public void cancel() {
        setStatus(RegistrationStatus.CANCELLED);
    }

    public void markAttended() {
        setStatus(RegistrationStatus.ATTENDED);
    }

    public void markNoShow() {
        setStatus(RegistrationStatus.NO_SHOW);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EventRegistration that)) return false;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "EventRegistration{id=%d, eventId=%d, userId=%d, status=%s}"
            .formatted(id, eventId, userId, status);
    }
}