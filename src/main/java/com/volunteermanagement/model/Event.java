package com.volunteermanagement.model;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Event entity representing volunteer opportunities.
 * Enhanced with Java 21 patterns, enums, and business logic validation.
 */
public class Event {
    private int id;
    private String title;
    private String description;
    private String location;
    private LocalDate eventDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private int volunteersNeeded;
    private int volunteersRegistered;
    private String category;
    private EventStatus status;
    private int createdBy;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public enum EventStatus {
        ACTIVE("active"),
        COMPLETED("completed"),
        CANCELLED("cancelled");

        private final String value;

        EventStatus(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public static EventStatus fromString(String status) {
            if (status == null) return ACTIVE;
            return switch (status.toLowerCase()) {
                case "completed" -> COMPLETED;
                case "cancelled" -> CANCELLED;
                default -> ACTIVE;
            };
        }
    }

    public Event() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.status = EventStatus.ACTIVE;
        this.volunteersRegistered = 0;
    }

    // Getters
    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getLocation() { return location; }
    public LocalDate getEventDate() { return eventDate; }
    public LocalTime getStartTime() { return startTime; }
    public LocalTime getEndTime() { return endTime; }
    public int getVolunteersNeeded() { return volunteersNeeded; }
    public int getVolunteersRegistered() { return volunteersRegistered; }
    public String getCategory() { return category; }
    public EventStatus getStatus() { return status; }
    public String getStatusString() { return status.getValue(); }
    public int getCreatedBy() { return createdBy; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }

    // Setters
    public void setId(int id) { this.id = id; }
    
    public void setTitle(String title) {
        this.title = title;
        touchUpdatedAt();
    }
    
    public void setDescription(String description) {
        this.description = description;
        touchUpdatedAt();
    }
    
    public void setLocation(String location) {
        this.location = location;
        touchUpdatedAt();
    }
    
    public void setEventDate(LocalDate eventDate) {
        this.eventDate = eventDate;
        touchUpdatedAt();
    }
    
    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
        touchUpdatedAt();
    }
    
    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
        touchUpdatedAt();
    }
    
    public void setVolunteersNeeded(int volunteersNeeded) {
        this.volunteersNeeded = Math.max(0, volunteersNeeded);
        touchUpdatedAt();
    }
    
    public void setVolunteersRegistered(int volunteersRegistered) {
        this.volunteersRegistered = Math.max(0, volunteersRegistered);
        touchUpdatedAt();
    }
    
    public void setCategory(String category) {
        this.category = category;
        touchUpdatedAt();
    }
    
    public void setStatus(EventStatus status) {
        this.status = status != null ? status : EventStatus.ACTIVE;
        touchUpdatedAt();
    }
    
    public void setStatus(String status) {
        this.status = EventStatus.fromString(status);
        touchUpdatedAt();
    }
    
    public void setCreatedBy(int createdBy) { this.createdBy = createdBy; }
    public void setCreatedAt(LocalDateTime createdAt) { /* DB loading only */ }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    // Business logic methods
    private void touchUpdatedAt() {
        this.updatedAt = LocalDateTime.now();
    }

    public boolean isFull() {
        return volunteersRegistered >= volunteersNeeded;
    }

    public boolean hasSpots() {
        return volunteersRegistered < volunteersNeeded;
    }

    public int getAvailableSpots() {
        return Math.max(0, volunteersNeeded - volunteersRegistered);
    }

    public boolean isActive() {
        return status == EventStatus.ACTIVE;
    }

    public boolean isCompleted() {
        return status == EventStatus.COMPLETED;
    }

    public boolean isCancelled() {
        return status == EventStatus.CANCELLED;
    }

    public boolean isPast() {
        return eventDate != null && eventDate.isBefore(LocalDate.now());
    }

    public boolean isFuture() {
        return eventDate != null && eventDate.isAfter(LocalDate.now());
    }

    public boolean isToday() {
        return eventDate != null && eventDate.isEqual(LocalDate.now());
    }

    public boolean canRegister() {
        return isActive() && hasSpots() && !isPast();
    }

    public void incrementRegistrations() {
        if (hasSpots()) {
            volunteersRegistered++;
            touchUpdatedAt();
        }
    }

    public void decrementRegistrations() {
        if (volunteersRegistered > 0) {
            volunteersRegistered--;
            touchUpdatedAt();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Event event)) return false;
        return id == event.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Event{id=%d, title='%s', date=%s, status=%s, spots=%d/%d}"
            .formatted(id, title, eventDate, status, volunteersRegistered, volunteersNeeded);
    }
}