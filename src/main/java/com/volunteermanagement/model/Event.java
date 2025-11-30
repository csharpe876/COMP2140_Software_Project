package com.volunteermanagement.model;

import java.io.Serializable;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Event entity class
 */
public class Event implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer id;
    private String title;
    private String description;
    private String location;
    private LocalDate eventDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private Integer volunteersNeeded;
    private Integer volunteersRegistered;
    private String category;
    private String status;
    private Integer createdBy;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    // Extended fields
    private String creatorName;

    // Constructors
    public Event() {
    }

    public Event(String title, String description, String location, LocalDate eventDate,
                LocalTime startTime, LocalTime endTime, Integer volunteersNeeded, String category,
                String status, Integer createdBy) {
        this.title = title;
        this.description = description;
        this.location = location;
        this.eventDate = eventDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.volunteersNeeded = volunteersNeeded;
        this.category = category;
        this.status = status;
        this.createdBy = createdBy;
    }

    // Getters and Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public LocalDate getEventDate() {
        return eventDate;
    }

    public void setEventDate(LocalDate eventDate) {
        this.eventDate = eventDate;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public Integer getVolunteersNeeded() {
        return volunteersNeeded;
    }

    public void setVolunteersNeeded(Integer volunteersNeeded) {
        this.volunteersNeeded = volunteersNeeded;
    }

    public Integer getVolunteersRegistered() {
        return volunteersRegistered != null ? volunteersRegistered : 0;
    }

    public void setVolunteersRegistered(Integer volunteersRegistered) {
        this.volunteersRegistered = volunteersRegistered;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Integer createdBy) {
        this.createdBy = createdBy;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getCreatorName() {
        return creatorName;
    }

    public void setCreatorName(String creatorName) {
        this.creatorName = creatorName;
    }

    // Helper methods
    public boolean isFull() {
        return getVolunteersRegistered() >= volunteersNeeded;
    }

    public boolean isActive() {
        return "active".equalsIgnoreCase(this.status);
    }

    public boolean isPast() {
        return eventDate != null && eventDate.isBefore(LocalDate.now());
    }

    @Override
    public String toString() {
        return "Event{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", eventDate=" + eventDate +
                ", status='" + status + '\'' +
                '}';
    }
}
