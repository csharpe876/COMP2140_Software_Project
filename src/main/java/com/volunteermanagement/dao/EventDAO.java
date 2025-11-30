package com.volunteermanagement.dao;

import com.volunteermanagement.model.Event;
import com.volunteermanagement.util.DatabaseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Data Access Object for Event entity.
 * Handles all database operations for volunteer events.
 */
public class EventDAO {
    private static final Logger logger = LoggerFactory.getLogger(EventDAO.class);

    public Optional<Event> findById(int id) {
        String sql = "SELECT * FROM events WHERE id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToEvent(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Error finding event by ID: {}", id, e);
        }
        return Optional.empty();
    }

    public Event create(Event event) throws SQLException {
        String sql = "INSERT INTO events (title, description, location, event_date, start_time, end_time, " +
                    "volunteers_needed, category, status, created_by) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, event.getTitle());
            stmt.setString(2, event.getDescription());
            stmt.setString(3, event.getLocation());
            stmt.setDate(4, Date.valueOf(event.getEventDate()));
            stmt.setTime(5, event.getStartTime() != null ? Time.valueOf(event.getStartTime()) : null);
            stmt.setTime(6, event.getEndTime() != null ? Time.valueOf(event.getEndTime()) : null);
            stmt.setInt(7, event.getVolunteersNeeded());
            stmt.setString(8, event.getCategory());
            stmt.setString(9, event.getStatusString());
            stmt.setInt(10, event.getCreatedBy());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("Creating event failed, no rows affected.");
            }
            
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    event.setId(generatedKeys.getInt(1));
                    logger.info("Event created successfully: {}", event.getTitle());
                    return event;
                } else {
                    throw new SQLException("Creating event failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            logger.error("Error creating event: {}", event.getTitle(), e);
            throw e;
        }
    }

    public void update(Event event) throws SQLException {
        String sql = "UPDATE events SET title = ?, description = ?, location = ?, event_date = ?, " +
                    "start_time = ?, end_time = ?, volunteers_needed = ?, category = ?, status = ?, " +
                    "updated_at = CURRENT_TIMESTAMP WHERE id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, event.getTitle());
            stmt.setString(2, event.getDescription());
            stmt.setString(3, event.getLocation());
            stmt.setDate(4, Date.valueOf(event.getEventDate()));
            stmt.setTime(5, event.getStartTime() != null ? Time.valueOf(event.getStartTime()) : null);
            stmt.setTime(6, event.getEndTime() != null ? Time.valueOf(event.getEndTime()) : null);
            stmt.setInt(7, event.getVolunteersNeeded());
            stmt.setString(8, event.getCategory());
            stmt.setString(9, event.getStatusString());
            stmt.setInt(10, event.getId());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("Updating event failed, no rows affected.");
            }
            
            logger.info("Event updated successfully: {}", event.getId());
        } catch (SQLException e) {
            logger.error("Error updating event: {}", event.getId(), e);
            throw e;
        }
    }

    public void updateRegistrationCount(int eventId, int count) throws SQLException {
        String sql = "UPDATE events SET volunteers_registered = ?, updated_at = CURRENT_TIMESTAMP WHERE id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, count);
            stmt.setInt(2, eventId);
            
            stmt.executeUpdate();
            logger.debug("Updated registration count for event {}: {}", eventId, count);
        } catch (SQLException e) {
            logger.error("Error updating registration count for event: {}", eventId, e);
            throw e;
        }
    }

    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM events WHERE id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("Deleting event failed, no rows affected.");
            }
            
            logger.info("Event deleted successfully: {}", id);
        } catch (SQLException e) {
            logger.error("Error deleting event: {}", id, e);
            throw e;
        }
    }

    public List<Event> findAll() {
        List<Event> events = new ArrayList<>();
        String sql = "SELECT * FROM events ORDER BY event_date DESC, created_at DESC";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                events.add(mapResultSetToEvent(rs));
            }
        } catch (SQLException e) {
            logger.error("Error finding all events", e);
        }
        return events;
    }

    public List<Event> findActiveEvents() {
        List<Event> events = new ArrayList<>();
        String sql = "SELECT * FROM events WHERE status = 'active' AND event_date >= CURDATE() " +
                    "ORDER BY event_date ASC";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                events.add(mapResultSetToEvent(rs));
            }
        } catch (SQLException e) {
            logger.error("Error finding active events", e);
        }
        return events;
    }

    public List<Event> findByCategory(String category) {
        List<Event> events = new ArrayList<>();
        String sql = "SELECT * FROM events WHERE category = ? ORDER BY event_date DESC";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, category);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    events.add(mapResultSetToEvent(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Error finding events by category: {}", category, e);
        }
        return events;
    }

    public List<Event> findUpcomingEvents(int limit) {
        List<Event> events = new ArrayList<>();
        String sql = "SELECT * FROM events WHERE status = 'active' AND event_date >= CURDATE() " +
                    "ORDER BY event_date ASC LIMIT ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, limit);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    events.add(mapResultSetToEvent(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Error finding upcoming events", e);
        }
        return events;
    }

    public List<Event> findByDateRange(LocalDate startDate, LocalDate endDate) {
        List<Event> events = new ArrayList<>();
        String sql = "SELECT * FROM events WHERE event_date BETWEEN ? AND ? ORDER BY event_date ASC";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setDate(1, Date.valueOf(startDate));
            stmt.setDate(2, Date.valueOf(endDate));
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    events.add(mapResultSetToEvent(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Error finding events by date range", e);
        }
        return events;
    }

    private Event mapResultSetToEvent(ResultSet rs) throws SQLException {
        Event event = new Event();
        event.setId(rs.getInt("id"));
        event.setTitle(rs.getString("title"));
        event.setDescription(rs.getString("description"));
        event.setLocation(rs.getString("location"));
        
        Date eventDate = rs.getDate("event_date");
        if (eventDate != null) {
            event.setEventDate(eventDate.toLocalDate());
        }
        
        Time startTime = rs.getTime("start_time");
        if (startTime != null) {
            event.setStartTime(startTime.toLocalTime());
        }
        
        Time endTime = rs.getTime("end_time");
        if (endTime != null) {
            event.setEndTime(endTime.toLocalTime());
        }
        
        event.setVolunteersNeeded(rs.getInt("volunteers_needed"));
        event.setVolunteersRegistered(rs.getInt("volunteers_registered"));
        event.setCategory(rs.getString("category"));
        event.setStatus(rs.getString("status"));
        event.setCreatedBy(rs.getInt("created_by"));
        event.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        event.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
        
        return event;
    }
}
