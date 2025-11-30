package com.volunteermanagement.dao;

import com.volunteermanagement.model.EventRegistration;
import com.volunteermanagement.util.DatabaseUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Data Access Object for managing EventRegistration entities.
 * Provides operations for event sign-up, cancellation, and registration queries.
 */
public class EventRegistrationDAO {
    
    public Optional<EventRegistration> findById(int id) {
        String sql = "SELECT * FROM event_registrations WHERE id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return Optional.of(mapResultSetToRegistration(rs));
            }
            return Optional.empty();
            
        } catch (SQLException e) {
            throw new RuntimeException("Error finding registration by ID: " + id, e);
        }
    }
    
    public Optional<EventRegistration> findByEventAndVolunteer(int eventId, int volunteerId) {
        String sql = "SELECT * FROM event_registrations WHERE event_id = ? AND volunteer_id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, eventId);
            stmt.setInt(2, volunteerId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return Optional.of(mapResultSetToRegistration(rs));
            }
            return Optional.empty();
            
        } catch (SQLException e) {
            throw new RuntimeException("Error finding registration by event and volunteer", e);
        }
    }
    
    public List<EventRegistration> findByEventId(int eventId) {
        String sql = "SELECT * FROM event_registrations WHERE event_id = ? ORDER BY registered_at";
        List<EventRegistration> registrations = new ArrayList<>();
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, eventId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                registrations.add(mapResultSetToRegistration(rs));
            }
            
        } catch (SQLException e) {
            throw new RuntimeException("Error finding registrations by event ID: " + eventId, e);
        }
        
        return registrations;
    }
    
    public List<EventRegistration> findByVolunteerId(int volunteerId) {
        String sql = "SELECT * FROM event_registrations WHERE volunteer_id = ? ORDER BY registered_at DESC";
        List<EventRegistration> registrations = new ArrayList<>();
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, volunteerId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                registrations.add(mapResultSetToRegistration(rs));
            }
            
        } catch (SQLException e) {
            throw new RuntimeException("Error finding registrations by volunteer ID: " + volunteerId, e);
        }
        
        return registrations;
    }
    
    public List<EventRegistration> findConfirmedByEventId(int eventId) {
        String sql = "SELECT * FROM event_registrations WHERE event_id = ? AND status = 'CONFIRMED' ORDER BY registered_at";
        List<EventRegistration> registrations = new ArrayList<>();
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, eventId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                registrations.add(mapResultSetToRegistration(rs));
            }
            
        } catch (SQLException e) {
            throw new RuntimeException("Error finding confirmed registrations by event ID: " + eventId, e);
        }
        
        return registrations;
    }
    
    public EventRegistration register(int eventId, int volunteerId) {
        String sql = """
            INSERT INTO event_registrations (event_id, volunteer_id, status)
            VALUES (?, ?, 'CONFIRMED')
            """;
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setInt(1, eventId);
            stmt.setInt(2, volunteerId);
            
            int affected = stmt.executeUpdate();
            if (affected == 0) {
                throw new SQLException("Registering volunteer failed, no rows affected");
            }
            
            var registration = new EventRegistration();
            registration.setEventId(eventId);
            registration.setVolunteerId(volunteerId);
            registration.setStatus(EventRegistration.RegistrationStatus.CONFIRMED);
            
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    registration.setId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Registering volunteer failed, no ID obtained");
                }
            }
            
            return registration;
            
        } catch (SQLException e) {
            throw new RuntimeException("Error registering volunteer for event", e);
        }
    }
    
    public boolean updateStatus(int registrationId, EventRegistration.RegistrationStatus status) {
        String sql = "UPDATE event_registrations SET status = ? WHERE id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, status.name());
            stmt.setInt(2, registrationId);
            
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            throw new RuntimeException("Error updating registration status: " + registrationId, e);
        }
    }
    
    public boolean cancel(int registrationId) {
        return updateStatus(registrationId, EventRegistration.RegistrationStatus.CANCELLED);
    }
    
    public boolean delete(int id) {
        String sql = "DELETE FROM event_registrations WHERE id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting registration: " + id, e);
        }
    }
    
    public int countConfirmedByEventId(int eventId) {
        String sql = "SELECT COUNT(*) FROM event_registrations WHERE event_id = ? AND status = 'CONFIRMED'";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, eventId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1);
            }
            return 0;
            
        } catch (SQLException e) {
            throw new RuntimeException("Error counting confirmed registrations for event: " + eventId, e);
        }
    }
    
    public boolean isVolunteerRegistered(int eventId, int volunteerId) {
        String sql = "SELECT COUNT(*) FROM event_registrations WHERE event_id = ? AND volunteer_id = ? AND status != 'CANCELLED'";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, eventId);
            stmt.setInt(2, volunteerId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            return false;
            
        } catch (SQLException e) {
            throw new RuntimeException("Error checking registration status", e);
        }
    }
    
    private EventRegistration mapResultSetToRegistration(ResultSet rs) throws SQLException {
        var registration = new EventRegistration();
        registration.setId(rs.getInt("id"));
        registration.setEventId(rs.getInt("event_id"));
        registration.setVolunteerId(rs.getInt("volunteer_id"));
        registration.setStatus(EventRegistration.RegistrationStatus.valueOf(rs.getString("status")));
        registration.setRegisteredAt(rs.getTimestamp("registered_at").toLocalDateTime());
        
        Timestamp updatedAt = rs.getTimestamp("updated_at");
        if (updatedAt != null) {
            registration.setUpdatedAt(updatedAt.toLocalDateTime());
        }
        
        return registration;
    }
}
