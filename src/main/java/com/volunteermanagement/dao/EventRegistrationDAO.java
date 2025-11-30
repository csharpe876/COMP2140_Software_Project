package com.volunteermanagement.dao;

import com.volunteermanagement.model.EventRegistration;
import com.volunteermanagement.util.DatabaseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Data Access Object for EventRegistration operations
 * Handles all database interactions for volunteer event registrations
 */
public class EventRegistrationDAO {
    private static final Logger logger = LoggerFactory.getLogger(EventRegistrationDAO.class);

    /**
     * Create a new event registration
     * 
     * @param registration EventRegistration object
     * @return Generated registration ID, or 0 if creation failed
     */
    public int create(EventRegistration registration) {
        String sql = "INSERT INTO event_registrations (event_id, volunteer_id, status, notes) " +
                    "VALUES (?, ?, ?, ?)";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setInt(1, registration.getEventId());
            stmt.setInt(2, registration.getVolunteerId());
            stmt.setString(3, registration.getStatus());
            stmt.setString(4, registration.getNotes());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        int id = rs.getInt(1);
                        logger.info("Created event registration ID: {} - Event: {}, Volunteer: {}", 
                                  id, registration.getEventId(), registration.getVolunteerId());
                        return id;
                    }
                }
            }
            
        } catch (SQLException e) {
            logger.error("Error creating event registration for event: {}, volunteer: {}", 
                        registration.getEventId(), registration.getVolunteerId(), e);
        }
        
        return 0;
    }

    /**
     * Get registration by ID
     * 
     * @param id Registration ID
     * @return EventRegistration object or null if not found
     */
    public EventRegistration getById(int id) {
        String sql = "SELECT er.*, e.title as event_title, e.event_date, e.start_time, e.end_time, " +
                    "e.location, v.user_id, u.full_name as volunteer_name, u.email as volunteer_email, u.phone as volunteer_phone " +
                    "FROM event_registrations er " +
                    "INNER JOIN events e ON er.event_id = e.id " +
                    "INNER JOIN volunteers v ON er.volunteer_id = v.id " +
                    "INNER JOIN users u ON v.user_id = u.id " +
                    "WHERE er.id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToRegistration(rs);
                }
            }
            
        } catch (SQLException e) {
            logger.error("Error getting registration by ID: {}", id, e);
        }
        
        return null;
    }

    /**
     * Check if a volunteer is already registered for an event
     * 
     * @param eventId Event ID
     * @param volunteerId Volunteer ID
     * @return true if already registered, false otherwise
     */
    public boolean isRegistered(int eventId, int volunteerId) {
        String sql = "SELECT COUNT(*) FROM event_registrations " +
                    "WHERE event_id = ? AND volunteer_id = ? " +
                    "AND status IN ('confirmed', 'attended')";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, eventId);
            stmt.setInt(2, volunteerId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
            
        } catch (SQLException e) {
            logger.error("Error checking registration for event: {}, volunteer: {}", eventId, volunteerId, e);
        }
        
        return false;
    }

    /**
     * Get all registrations for a specific event
     * 
     * @param eventId Event ID
     * @return List of registrations for the event
     */
    public List<EventRegistration> getByEvent(int eventId) {
        return getByEvent(eventId, null);
    }

    /**
     * Get registrations for a specific event with status filter
     * 
     * @param eventId Event ID
     * @param status Filter by status (null for all)
     * @return List of registrations for the event
     */
    public List<EventRegistration> getByEvent(int eventId, String status) {
        List<EventRegistration> registrations = new ArrayList<>();
        StringBuilder sql = new StringBuilder();
        
        sql.append("SELECT er.*, e.title as event_title, e.event_date, e.start_time, e.end_time, ");
        sql.append("e.location, v.user_id, u.full_name as volunteer_name, u.email as volunteer_email, u.phone as volunteer_phone ");
        sql.append("FROM event_registrations er ");
        sql.append("INNER JOIN events e ON er.event_id = e.id ");
        sql.append("INNER JOIN volunteers v ON er.volunteer_id = v.id ");
        sql.append("INNER JOIN users u ON v.user_id = u.id ");
        sql.append("WHERE er.event_id = ? ");
        
        if (status != null && !status.isEmpty()) {
            sql.append("AND er.status = ? ");
        }
        
        sql.append("ORDER BY er.registered_at DESC");
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            
            int paramIndex = 1;
            stmt.setInt(paramIndex++, eventId);
            
            if (status != null && !status.isEmpty()) {
                stmt.setString(paramIndex++, status);
            }
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    registrations.add(mapResultSetToRegistration(rs));
                }
            }
            
            logger.info("Retrieved {} registrations for event ID: {}", registrations.size(), eventId);
            
        } catch (SQLException e) {
            logger.error("Error getting registrations by event: {}", eventId, e);
        }
        
        return registrations;
    }

    /**
     * Get all registrations for a specific volunteer
     * 
     * @param volunteerId Volunteer ID
     * @return List of registrations for the volunteer
     */
    public List<EventRegistration> getByVolunteer(int volunteerId) {
        return getByVolunteer(volunteerId, null);
    }

    /**
     * Get registrations for a specific volunteer with status filter
     * 
     * @param volunteerId Volunteer ID
     * @param status Filter by status (null for all)
     * @return List of registrations for the volunteer
     */
    public List<EventRegistration> getByVolunteer(int volunteerId, String status) {
        List<EventRegistration> registrations = new ArrayList<>();
        StringBuilder sql = new StringBuilder();
        
        sql.append("SELECT er.*, e.title as event_title, e.event_date, e.start_time, e.end_time, ");
        sql.append("e.location, v.user_id, u.full_name as volunteer_name, u.email as volunteer_email, u.phone as volunteer_phone ");
        sql.append("FROM event_registrations er ");
        sql.append("INNER JOIN events e ON er.event_id = e.id ");
        sql.append("INNER JOIN volunteers v ON er.volunteer_id = v.id ");
        sql.append("INNER JOIN users u ON v.user_id = u.id ");
        sql.append("WHERE er.volunteer_id = ? ");
        
        if (status != null && !status.isEmpty()) {
            sql.append("AND er.status = ? ");
        }
        
        sql.append("ORDER BY e.event_date DESC, e.start_time DESC");
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            
            int paramIndex = 1;
            stmt.setInt(paramIndex++, volunteerId);
            
            if (status != null && !status.isEmpty()) {
                stmt.setString(paramIndex++, status);
            }
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    registrations.add(mapResultSetToRegistration(rs));
                }
            }
            
            logger.info("Retrieved {} registrations for volunteer ID: {}", registrations.size(), volunteerId);
            
        } catch (SQLException e) {
            logger.error("Error getting registrations by volunteer: {}", volunteerId, e);
        }
        
        return registrations;
    }

    /**
     * Get upcoming event registrations for a volunteer
     * 
     * @param volunteerId Volunteer ID
     * @return List of upcoming event registrations
     */
    public List<EventRegistration> getUpcomingByVolunteer(int volunteerId) {
        List<EventRegistration> registrations = new ArrayList<>();
        String sql = "SELECT er.*, e.title as event_title, e.event_date, e.start_time, e.end_time, " +
                    "e.location, v.user_id, u.full_name as volunteer_name, u.email as volunteer_email, u.phone as volunteer_phone " +
                    "FROM event_registrations er " +
                    "INNER JOIN events e ON er.event_id = e.id " +
                    "INNER JOIN volunteers v ON er.volunteer_id = v.id " +
                    "INNER JOIN users u ON v.user_id = u.id " +
                    "WHERE er.volunteer_id = ? " +
                    "AND e.event_date >= CURDATE() " +
                    "AND er.status IN ('confirmed', 'attended') " +
                    "ORDER BY e.event_date ASC, e.start_time ASC";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, volunteerId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    registrations.add(mapResultSetToRegistration(rs));
                }
            }
            
            logger.info("Retrieved {} upcoming registrations for volunteer ID: {}", registrations.size(), volunteerId);
            
        } catch (SQLException e) {
            logger.error("Error getting upcoming registrations for volunteer: {}", volunteerId, e);
        }
        
        return registrations;
    }

    /**
     * Update registration status
     * 
     * @param registrationId Registration ID
     * @param status New status
     * @return true if update successful, false otherwise
     */
    public boolean updateStatus(int registrationId, String status) {
        String sql = "UPDATE event_registrations SET status = ?, updated_at = NOW() WHERE id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, status);
            stmt.setInt(2, registrationId);
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                logger.info("Updated registration ID: {} status to: {}", registrationId, status);
                return true;
            }
            
        } catch (SQLException e) {
            logger.error("Error updating registration status for ID: {}", registrationId, e);
        }
        
        return false;
    }

    /**
     * Update registration notes
     * 
     * @param registrationId Registration ID
     * @param notes New notes
     * @return true if update successful, false otherwise
     */
    public boolean updateNotes(int registrationId, String notes) {
        String sql = "UPDATE event_registrations SET notes = ?, updated_at = NOW() WHERE id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, notes);
            stmt.setInt(2, registrationId);
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                logger.info("Updated registration ID: {} notes", registrationId);
                return true;
            }
            
        } catch (SQLException e) {
            logger.error("Error updating registration notes for ID: {}", registrationId, e);
        }
        
        return false;
    }

    /**
     * Cancel a registration (set status to cancelled)
     * 
     * @param registrationId Registration ID
     * @return true if cancellation successful, false otherwise
     */
    public boolean cancel(int registrationId) {
        return updateStatus(registrationId, "cancelled");
    }

    /**
     * Cancel registration by event and volunteer
     * 
     * @param eventId Event ID
     * @param volunteerId Volunteer ID
     * @return true if cancellation successful, false otherwise
     */
    public boolean cancelByEventAndVolunteer(int eventId, int volunteerId) {
        String sql = "UPDATE event_registrations SET status = 'cancelled', updated_at = NOW() " +
                    "WHERE event_id = ? AND volunteer_id = ? AND status = 'confirmed'";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, eventId);
            stmt.setInt(2, volunteerId);
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                logger.info("Cancelled registration for event: {}, volunteer: {}", eventId, volunteerId);
                return true;
            }
            
        } catch (SQLException e) {
            logger.error("Error cancelling registration for event: {}, volunteer: {}", eventId, volunteerId, e);
        }
        
        return false;
    }

    /**
     * Delete a registration
     * 
     * @param id Registration ID
     * @return true if deletion successful, false otherwise
     */
    public boolean delete(int id) {
        String sql = "DELETE FROM event_registrations WHERE id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                logger.info("Deleted registration ID: {}", id);
                return true;
            }
            
        } catch (SQLException e) {
            logger.error("Error deleting registration ID: {}", id, e);
        }
        
        return false;
    }

    /**
     * Get count of registrations for an event by status
     * 
     * @param eventId Event ID
     * @param status Filter by status (null for all active statuses)
     * @return Count of registrations
     */
    public int getEventRegistrationCount(int eventId, String status) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM event_registrations WHERE event_id = ?");
        
        if (status != null && !status.isEmpty()) {
            sql.append(" AND status = ?");
        } else {
            sql.append(" AND status IN ('confirmed', 'attended')");
        }
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            
            stmt.setInt(1, eventId);
            
            if (status != null && !status.isEmpty()) {
                stmt.setString(2, status);
            }
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
            
        } catch (SQLException e) {
            logger.error("Error getting registration count for event: {}", eventId, e);
        }
        
        return 0;
    }

    /**
     * Get count of registrations for a volunteer
     * 
     * @param volunteerId Volunteer ID
     * @param status Filter by status (null for all)
     * @return Count of registrations
     */
    public int getVolunteerRegistrationCount(int volunteerId, String status) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM event_registrations WHERE volunteer_id = ?");
        
        if (status != null && !status.isEmpty()) {
            sql.append(" AND status = ?");
        }
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            
            stmt.setInt(1, volunteerId);
            
            if (status != null && !status.isEmpty()) {
                stmt.setString(2, status);
            }
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
            
        } catch (SQLException e) {
            logger.error("Error getting registration count for volunteer: {}", volunteerId, e);
        }
        
        return 0;
    }

    /**
     * Get registration statistics by status
     * 
     * @return Map with status as key and count as value
     */
    public Map<String, Integer> getCountByStatus() {
        Map<String, Integer> counts = new HashMap<>();
        String sql = "SELECT status, COUNT(*) as count FROM event_registrations GROUP BY status";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                counts.put(rs.getString("status"), rs.getInt("count"));
            }
            
            logger.info("Retrieved registration counts by status");
            
        } catch (SQLException e) {
            logger.error("Error getting registration counts by status", e);
        }
        
        return counts;
    }

    /**
     * Get overall registration statistics
     * 
     * @return Map with various statistics
     */
    public Map<String, Object> getStats() {
        Map<String, Object> stats = new HashMap<>();
        
        try (Connection conn = DatabaseUtil.getConnection()) {
            
            // Total registrations
            try (PreparedStatement stmt = conn.prepareStatement(
                    "SELECT COUNT(*) FROM event_registrations")) {
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    stats.put("total", rs.getInt(1));
                }
            }
            
            // Confirmed registrations
            try (PreparedStatement stmt = conn.prepareStatement(
                    "SELECT COUNT(*) FROM event_registrations WHERE status = 'confirmed'")) {
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    stats.put("confirmed", rs.getInt(1));
                }
            }
            
            // Attended registrations
            try (PreparedStatement stmt = conn.prepareStatement(
                    "SELECT COUNT(*) FROM event_registrations WHERE status = 'attended'")) {
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    stats.put("attended", rs.getInt(1));
                }
            }
            
            // Cancelled registrations
            try (PreparedStatement stmt = conn.prepareStatement(
                    "SELECT COUNT(*) FROM event_registrations WHERE status = 'cancelled'")) {
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    stats.put("cancelled", rs.getInt(1));
                }
            }
            
            // Registrations this month
            try (PreparedStatement stmt = conn.prepareStatement(
                    "SELECT COUNT(*) FROM event_registrations " +
                    "WHERE MONTH(registered_at) = MONTH(CURDATE()) AND YEAR(registered_at) = YEAR(CURDATE())")) {
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    stats.put("this_month", rs.getInt(1));
                }
            }
            
            logger.info("Retrieved registration statistics");
            
        } catch (SQLException e) {
            logger.error("Error getting registration statistics", e);
        }
        
        return stats;
    }

    /**
     * Get all registrations (for admin purposes)
     * 
     * @param limit Number of records to return (0 for all)
     * @param offset Offset for pagination
     * @return List of all registrations
     */
    public List<EventRegistration> getAll(int limit, int offset) {
        List<EventRegistration> registrations = new ArrayList<>();
        StringBuilder sql = new StringBuilder();
        
        sql.append("SELECT er.*, e.title as event_title, e.event_date, e.start_time, e.end_time, ");
        sql.append("e.location, v.user_id, u.full_name as volunteer_name, u.email as volunteer_email, u.phone as volunteer_phone ");
        sql.append("FROM event_registrations er ");
        sql.append("INNER JOIN events e ON er.event_id = e.id ");
        sql.append("INNER JOIN volunteers v ON er.volunteer_id = v.id ");
        sql.append("INNER JOIN users u ON v.user_id = u.id ");
        sql.append("ORDER BY er.registered_at DESC ");
        
        if (limit > 0) {
            sql.append("LIMIT ? OFFSET ?");
        }
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            
            if (limit > 0) {
                stmt.setInt(1, limit);
                stmt.setInt(2, offset);
            }
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    registrations.add(mapResultSetToRegistration(rs));
                }
            }
            
            logger.info("Retrieved {} registrations", registrations.size());
            
        } catch (SQLException e) {
            logger.error("Error getting all registrations", e);
        }
        
        return registrations;
    }

    /**
     * Map ResultSet to EventRegistration object
     * 
     * @param rs ResultSet
     * @return EventRegistration object
     * @throws SQLException if database access error occurs
     */
    private EventRegistration mapResultSetToRegistration(ResultSet rs) throws SQLException {
        EventRegistration registration = new EventRegistration();
        
        registration.setId(rs.getInt("id"));
        registration.setEventId(rs.getInt("event_id"));
        registration.setVolunteerId(rs.getInt("volunteer_id"));
        registration.setStatus(rs.getString("status"));
        registration.setNotes(rs.getString("notes"));
        registration.setRegisteredAt(rs.getTimestamp("registered_at"));
        registration.setUpdatedAt(rs.getTimestamp("updated_at"));
        
        // Additional information from joins
        registration.setEventTitle(rs.getString("event_title"));
        registration.setEventDate(rs.getDate("event_date").toLocalDate());
        registration.setEventStartTime(rs.getTime("start_time").toLocalTime());
        registration.setEventEndTime(rs.getTime("end_time").toLocalTime());
        registration.setEventLocation(rs.getString("location"));
        registration.setVolunteerName(rs.getString("volunteer_name"));
        registration.setVolunteerEmail(rs.getString("volunteer_email"));
        registration.setVolunteerPhone(rs.getString("volunteer_phone"));
        
        return registration;
    }
}
