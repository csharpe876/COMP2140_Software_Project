package com.volunteermanagement.dao;

import com.volunteermanagement.model.Event;
import com.volunteermanagement.util.DatabaseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Data Access Object for Event operations
 * Handles all database interactions for volunteer events
 */
public class EventDAO {
    private static final Logger logger = LoggerFactory.getLogger(EventDAO.class);

    /**
     * Create a new event
     * 
     * @param event Event object with event information
     * @return Generated event ID, or 0 if creation failed
     */
    public int create(Event event) {
        String sql = "INSERT INTO events (title, description, location, event_date, start_time, " +
                    "end_time, volunteers_needed, category, status, created_by) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, event.getTitle());
            stmt.setString(2, event.getDescription());
            stmt.setString(3, event.getLocation());
            stmt.setDate(4, Date.valueOf(event.getEventDate()));
            stmt.setTime(5, Time.valueOf(event.getStartTime()));
            stmt.setTime(6, Time.valueOf(event.getEndTime()));
            stmt.setInt(7, event.getVolunteersNeeded());
            stmt.setString(8, event.getCategory());
            stmt.setString(9, event.getStatus());
            stmt.setInt(10, event.getCreatedBy());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        int id = rs.getInt(1);
                        logger.info("Created event with ID: {} - {}", id, event.getTitle());
                        return id;
                    }
                }
            }
            
        } catch (SQLException e) {
            logger.error("Error creating event: {}", event.getTitle(), e);
        }
        
        return 0;
    }

    /**
     * Get event by ID with registration count
     * 
     * @param id Event ID
     * @return Event object or null if not found
     */
    public Event getById(int id) {
        String sql = "SELECT e.*, u.full_name as creator_name, " +
                    "(SELECT COUNT(*) FROM event_registrations er " +
                    " WHERE er.event_id = e.id AND er.status IN ('confirmed', 'attended')) as volunteers_registered " +
                    "FROM events e " +
                    "INNER JOIN users u ON e.created_by = u.id " +
                    "WHERE e.id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToEvent(rs);
                }
            }
            
        } catch (SQLException e) {
            logger.error("Error getting event by ID: {}", id, e);
        }
        
        return null;
    }

    /**
     * Update event
     * 
     * @param event Event object with updated information
     * @return true if update successful, false otherwise
     */
    public boolean update(Event event) {
        String sql = "UPDATE events SET title = ?, description = ?, location = ?, " +
                    "event_date = ?, start_time = ?, end_time = ?, volunteers_needed = ?, " +
                    "category = ?, status = ?, updated_at = NOW() WHERE id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, event.getTitle());
            stmt.setString(2, event.getDescription());
            stmt.setString(3, event.getLocation());
            stmt.setDate(4, Date.valueOf(event.getEventDate()));
            stmt.setTime(5, Time.valueOf(event.getStartTime()));
            stmt.setTime(6, Time.valueOf(event.getEndTime()));
            stmt.setInt(7, event.getVolunteersNeeded());
            stmt.setString(8, event.getCategory());
            stmt.setString(9, event.getStatus());
            stmt.setInt(10, event.getId());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                logger.info("Updated event ID: {} - {}", event.getId(), event.getTitle());
                return true;
            }
            
        } catch (SQLException e) {
            logger.error("Error updating event ID: {}", event.getId(), e);
        }
        
        return false;
    }

    /**
     * Update event status
     * 
     * @param eventId Event ID
     * @param status New status (active, completed, cancelled)
     * @return true if update successful, false otherwise
     */
    public boolean updateStatus(int eventId, String status) {
        String sql = "UPDATE events SET status = ?, updated_at = NOW() WHERE id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, status);
            stmt.setInt(2, eventId);
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                logger.info("Updated event ID: {} status to: {}", eventId, status);
                return true;
            }
            
        } catch (SQLException e) {
            logger.error("Error updating event status for ID: {}", eventId, e);
        }
        
        return false;
    }

    /**
     * Delete event
     * 
     * @param id Event ID
     * @return true if deletion successful, false otherwise
     */
    public boolean delete(int id) {
        String sql = "DELETE FROM events WHERE id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                logger.info("Deleted event ID: {}", id);
                return true;
            }
            
        } catch (SQLException e) {
            logger.error("Error deleting event ID: {}", id, e);
        }
        
        return false;
    }

    /**
     * Get all events
     * 
     * @return List of all events
     */
    public List<Event> getAll() {
        return getAll(null, null, null, 0, 0);
    }

    /**
     * Get all events with filtering and pagination
     * 
     * @param status Filter by status (null for all)
     * @param category Filter by category (null for all)
     * @param orderBy Order by column (default: event_date ASC)
     * @param limit Number of records to return (0 for all)
     * @param offset Offset for pagination
     * @return List of events
     */
    public List<Event> getAll(String status, String category, String orderBy, int limit, int offset) {
        List<Event> events = new ArrayList<>();
        StringBuilder sql = new StringBuilder();
        
        sql.append("SELECT e.*, u.full_name as creator_name, ");
        sql.append("(SELECT COUNT(*) FROM event_registrations er ");
        sql.append(" WHERE er.event_id = e.id AND er.status IN ('confirmed', 'attended')) as volunteers_registered ");
        sql.append("FROM events e ");
        sql.append("INNER JOIN users u ON e.created_by = u.id ");
        sql.append("WHERE 1=1 ");
        
        if (status != null && !status.isEmpty()) {
            sql.append("AND e.status = ? ");
        }
        
        if (category != null && !category.isEmpty()) {
            sql.append("AND e.category = ? ");
        }
        
        if (orderBy != null && !orderBy.isEmpty()) {
            sql.append("ORDER BY ").append(orderBy).append(" ");
        } else {
            sql.append("ORDER BY e.event_date ASC, e.start_time ASC ");
        }
        
        if (limit > 0) {
            sql.append("LIMIT ? OFFSET ? ");
        }
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            
            int paramIndex = 1;
            
            if (status != null && !status.isEmpty()) {
                stmt.setString(paramIndex++, status);
            }
            
            if (category != null && !category.isEmpty()) {
                stmt.setString(paramIndex++, category);
            }
            
            if (limit > 0) {
                stmt.setInt(paramIndex++, limit);
                stmt.setInt(paramIndex++, offset);
            }
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    events.add(mapResultSetToEvent(rs));
                }
            }
            
            logger.info("Retrieved {} events", events.size());
            
        } catch (SQLException e) {
            logger.error("Error getting all events", e);
        }
        
        return events;
    }

    /**
     * Get upcoming events (future events with active status)
     * 
     * @param limit Number of events to return (0 for all)
     * @return List of upcoming events
     */
    public List<Event> getUpcoming(int limit) {
        List<Event> events = new ArrayList<>();
        StringBuilder sql = new StringBuilder();
        
        sql.append("SELECT e.*, u.full_name as creator_name, ");
        sql.append("(SELECT COUNT(*) FROM event_registrations er ");
        sql.append(" WHERE er.event_id = e.id AND er.status IN ('confirmed', 'attended')) as volunteers_registered ");
        sql.append("FROM events e ");
        sql.append("INNER JOIN users u ON e.created_by = u.id ");
        sql.append("WHERE e.event_date >= CURDATE() AND e.status = 'active' ");
        sql.append("ORDER BY e.event_date ASC, e.start_time ASC ");
        
        if (limit > 0) {
            sql.append("LIMIT ? ");
        }
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            
            if (limit > 0) {
                stmt.setInt(1, limit);
            }
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    events.add(mapResultSetToEvent(rs));
                }
            }
            
            logger.info("Retrieved {} upcoming events", events.size());
            
        } catch (SQLException e) {
            logger.error("Error getting upcoming events", e);
        }
        
        return events;
    }

    /**
     * Get past events
     * 
     * @param limit Number of events to return (0 for all)
     * @return List of past events
     */
    public List<Event> getPast(int limit) {
        List<Event> events = new ArrayList<>();
        StringBuilder sql = new StringBuilder();
        
        sql.append("SELECT e.*, u.full_name as creator_name, ");
        sql.append("(SELECT COUNT(*) FROM event_registrations er ");
        sql.append(" WHERE er.event_id = e.id AND er.status IN ('confirmed', 'attended')) as volunteers_registered ");
        sql.append("FROM events e ");
        sql.append("INNER JOIN users u ON e.created_by = u.id ");
        sql.append("WHERE e.event_date < CURDATE() ");
        sql.append("ORDER BY e.event_date DESC, e.start_time DESC ");
        
        if (limit > 0) {
            sql.append("LIMIT ? ");
        }
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            
            if (limit > 0) {
                stmt.setInt(1, limit);
            }
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    events.add(mapResultSetToEvent(rs));
                }
            }
            
            logger.info("Retrieved {} past events", events.size());
            
        } catch (SQLException e) {
            logger.error("Error getting past events", e);
        }
        
        return events;
    }

    /**
     * Get events by category
     * 
     * @param category Event category
     * @param limit Number of events to return (0 for all)
     * @return List of events in the category
     */
    public List<Event> getByCategory(String category, int limit) {
        List<Event> events = new ArrayList<>();
        StringBuilder sql = new StringBuilder();
        
        sql.append("SELECT e.*, u.full_name as creator_name, ");
        sql.append("(SELECT COUNT(*) FROM event_registrations er ");
        sql.append(" WHERE er.event_id = e.id AND er.status IN ('confirmed', 'attended')) as volunteers_registered ");
        sql.append("FROM events e ");
        sql.append("INNER JOIN users u ON e.created_by = u.id ");
        sql.append("WHERE e.category = ? AND e.status = 'active' ");
        sql.append("ORDER BY e.event_date ASC, e.start_time ASC ");
        
        if (limit > 0) {
            sql.append("LIMIT ? ");
        }
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            
            stmt.setString(1, category);
            
            if (limit > 0) {
                stmt.setInt(2, limit);
            }
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    events.add(mapResultSetToEvent(rs));
                }
            }
            
            logger.info("Retrieved {} events in category: {}", events.size(), category);
            
        } catch (SQLException e) {
            logger.error("Error getting events by category: {}", category, e);
        }
        
        return events;
    }

    /**
     * Get events created by a specific user
     * 
     * @param userId User ID
     * @return List of events created by the user
     */
    public List<Event> getByCreator(int userId) {
        List<Event> events = new ArrayList<>();
        String sql = "SELECT e.*, u.full_name as creator_name, " +
                    "(SELECT COUNT(*) FROM event_registrations er " +
                    " WHERE er.event_id = e.id AND er.status IN ('confirmed', 'attended')) as volunteers_registered " +
                    "FROM events e " +
                    "INNER JOIN users u ON e.created_by = u.id " +
                    "WHERE e.created_by = ? " +
                    "ORDER BY e.event_date DESC, e.start_time DESC";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    events.add(mapResultSetToEvent(rs));
                }
            }
            
            logger.info("Retrieved {} events created by user ID: {}", events.size(), userId);
            
        } catch (SQLException e) {
            logger.error("Error getting events by creator: {}", userId, e);
        }
        
        return events;
    }

    /**
     * Search events by keyword
     * 
     * @param keyword Search term for title, description, location
     * @param category Filter by category (null for all)
     * @param status Filter by status (null for all)
     * @return List of matching events
     */
    public List<Event> search(String keyword, String category, String status) {
        List<Event> events = new ArrayList<>();
        StringBuilder sql = new StringBuilder();
        
        sql.append("SELECT e.*, u.full_name as creator_name, ");
        sql.append("(SELECT COUNT(*) FROM event_registrations er ");
        sql.append(" WHERE er.event_id = e.id AND er.status IN ('confirmed', 'attended')) as volunteers_registered ");
        sql.append("FROM events e ");
        sql.append("INNER JOIN users u ON e.created_by = u.id ");
        sql.append("WHERE 1=1 ");
        
        if (keyword != null && !keyword.isEmpty()) {
            sql.append("AND (e.title LIKE ? OR e.description LIKE ? OR e.location LIKE ?) ");
        }
        
        if (category != null && !category.isEmpty()) {
            sql.append("AND e.category = ? ");
        }
        
        if (status != null && !status.isEmpty()) {
            sql.append("AND e.status = ? ");
        }
        
        sql.append("ORDER BY e.event_date ASC, e.start_time ASC");
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            
            int paramIndex = 1;
            
            if (keyword != null && !keyword.isEmpty()) {
                String searchPattern = "%" + keyword + "%";
                stmt.setString(paramIndex++, searchPattern);
                stmt.setString(paramIndex++, searchPattern);
                stmt.setString(paramIndex++, searchPattern);
            }
            
            if (category != null && !category.isEmpty()) {
                stmt.setString(paramIndex++, category);
            }
            
            if (status != null && !status.isEmpty()) {
                stmt.setString(paramIndex++, status);
            }
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    events.add(mapResultSetToEvent(rs));
                }
            }
            
            logger.info("Found {} events matching search criteria", events.size());
            
        } catch (SQLException e) {
            logger.error("Error searching events", e);
        }
        
        return events;
    }

    /**
     * Get events that a volunteer is registered for
     * 
     * @param volunteerId Volunteer ID
     * @return List of events the volunteer is registered for
     */
    public List<Event> getByVolunteer(int volunteerId) {
        List<Event> events = new ArrayList<>();
        String sql = "SELECT e.*, u.full_name as creator_name, " +
                    "(SELECT COUNT(*) FROM event_registrations er " +
                    " WHERE er.event_id = e.id AND er.status IN ('confirmed', 'attended')) as volunteers_registered " +
                    "FROM events e " +
                    "INNER JOIN users u ON e.created_by = u.id " +
                    "INNER JOIN event_registrations er ON e.id = er.event_id " +
                    "WHERE er.volunteer_id = ? AND er.status IN ('confirmed', 'attended') " +
                    "ORDER BY e.event_date ASC, e.start_time ASC";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, volunteerId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    events.add(mapResultSetToEvent(rs));
                }
            }
            
            logger.info("Retrieved {} events for volunteer ID: {}", events.size(), volunteerId);
            
        } catch (SQLException e) {
            logger.error("Error getting events by volunteer: {}", volunteerId, e);
        }
        
        return events;
    }

    /**
     * Get count of events by status
     * 
     * @return Map with status as key and count as value
     */
    public Map<String, Integer> getCountByStatus() {
        Map<String, Integer> counts = new HashMap<>();
        String sql = "SELECT status, COUNT(*) as count FROM events GROUP BY status";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                counts.put(rs.getString("status"), rs.getInt("count"));
            }
            
            logger.info("Retrieved event counts by status");
            
        } catch (SQLException e) {
            logger.error("Error getting event counts by status", e);
        }
        
        return counts;
    }

    /**
     * Get count of events by category
     * 
     * @return Map with category as key and count as value
     */
    public Map<String, Integer> getCountByCategory() {
        Map<String, Integer> counts = new HashMap<>();
        String sql = "SELECT category, COUNT(*) as count FROM events " +
                    "WHERE status = 'active' GROUP BY category";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                counts.put(rs.getString("category"), rs.getInt("count"));
            }
            
            logger.info("Retrieved event counts by category");
            
        } catch (SQLException e) {
            logger.error("Error getting event counts by category", e);
        }
        
        return counts;
    }

    /**
     * Get total count of events
     * 
     * @param status Filter by status (null for all)
     * @return Total count
     */
    public int getCount(String status) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM events");
        
        if (status != null && !status.isEmpty()) {
            sql.append(" WHERE status = ?");
        }
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            
            if (status != null && !status.isEmpty()) {
                stmt.setString(1, status);
            }
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
            
        } catch (SQLException e) {
            logger.error("Error getting event count", e);
        }
        
        return 0;
    }

    /**
     * Get event statistics
     * 
     * @return Map with various statistics
     */
    public Map<String, Object> getStats() {
        Map<String, Object> stats = new HashMap<>();
        
        try (Connection conn = DatabaseUtil.getConnection()) {
            
            // Total events
            try (PreparedStatement stmt = conn.prepareStatement("SELECT COUNT(*) FROM events")) {
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    stats.put("total", rs.getInt(1));
                }
            }
            
            // Active events
            try (PreparedStatement stmt = conn.prepareStatement(
                    "SELECT COUNT(*) FROM events WHERE status = 'active'")) {
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    stats.put("active", rs.getInt(1));
                }
            }
            
            // Upcoming events
            try (PreparedStatement stmt = conn.prepareStatement(
                    "SELECT COUNT(*) FROM events WHERE event_date >= CURDATE() AND status = 'active'")) {
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    stats.put("upcoming", rs.getInt(1));
                }
            }
            
            // Completed events
            try (PreparedStatement stmt = conn.prepareStatement(
                    "SELECT COUNT(*) FROM events WHERE status = 'completed'")) {
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    stats.put("completed", rs.getInt(1));
                }
            }
            
            // Events this month
            try (PreparedStatement stmt = conn.prepareStatement(
                    "SELECT COUNT(*) FROM events WHERE MONTH(event_date) = MONTH(CURDATE()) " +
                    "AND YEAR(event_date) = YEAR(CURDATE())")) {
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    stats.put("this_month", rs.getInt(1));
                }
            }
            
            // Total registrations
            try (PreparedStatement stmt = conn.prepareStatement(
                    "SELECT COUNT(*) FROM event_registrations")) {
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    stats.put("total_registrations", rs.getInt(1));
                }
            }
            
            logger.info("Retrieved event statistics");
            
        } catch (SQLException e) {
            logger.error("Error getting event statistics", e);
        }
        
        return stats;
    }

    /**
     * Check if event has available spots
     * 
     * @param eventId Event ID
     * @return true if spots available, false otherwise
     */
    public boolean hasAvailableSpots(int eventId) {
        String sql = "SELECT e.volunteers_needed, " +
                    "(SELECT COUNT(*) FROM event_registrations er " +
                    " WHERE er.event_id = e.id AND er.status IN ('confirmed', 'attended')) as volunteers_registered " +
                    "FROM events e WHERE e.id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, eventId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int needed = rs.getInt("volunteers_needed");
                    int registered = rs.getInt("volunteers_registered");
                    return registered < needed;
                }
            }
            
        } catch (SQLException e) {
            logger.error("Error checking available spots for event ID: {}", eventId, e);
        }
        
        return false;
    }

    /**
     * Map ResultSet to Event object
     * 
     * @param rs ResultSet
     * @return Event object
     * @throws SQLException if database access error occurs
     */
    private Event mapResultSetToEvent(ResultSet rs) throws SQLException {
        Event event = new Event();
        
        event.setId(rs.getInt("id"));
        event.setTitle(rs.getString("title"));
        event.setDescription(rs.getString("description"));
        event.setLocation(rs.getString("location"));
        event.setEventDate(rs.getDate("event_date").toLocalDate());
        event.setStartTime(rs.getTime("start_time").toLocalTime());
        event.setEndTime(rs.getTime("end_time").toLocalTime());
        event.setVolunteersNeeded(rs.getInt("volunteers_needed"));
        event.setVolunteersRegistered(rs.getInt("volunteers_registered"));
        event.setCategory(rs.getString("category"));
        event.setStatus(rs.getString("status"));
        event.setCreatedBy(rs.getInt("created_by"));
        event.setCreatorName(rs.getString("creator_name"));
        event.setCreatedAt(rs.getTimestamp("created_at"));
        event.setUpdatedAt(rs.getTimestamp("updated_at"));
        
        return event;
    }
}
