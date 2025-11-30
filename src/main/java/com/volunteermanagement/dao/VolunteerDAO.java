package com.volunteermanagement.dao;

import com.volunteermanagement.model.Volunteer;
import com.volunteermanagement.util.DatabaseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Data Access Object for Volunteer operations
 * Handles all database interactions for volunteer profiles
 */
public class VolunteerDAO {
    private static final Logger logger = LoggerFactory.getLogger(VolunteerDAO.class);

    /**
     * Create a new volunteer profile
     * 
     * @param volunteer Volunteer object with profile information
     * @return Generated volunteer ID, or 0 if creation failed
     */
    public int create(Volunteer volunteer) {
        String sql = "INSERT INTO volunteers (user_id, skills, availability, experience, " +
                    "interests, emergency_contact, emergency_phone, status) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setInt(1, volunteer.getUserId());
            stmt.setString(2, volunteer.getSkills());
            stmt.setString(3, volunteer.getAvailability());
            stmt.setString(4, volunteer.getExperience());
            stmt.setString(5, volunteer.getInterests());
            stmt.setString(6, volunteer.getEmergencyContact());
            stmt.setString(7, volunteer.getEmergencyPhone());
            stmt.setString(8, volunteer.getStatus());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        int id = rs.getInt(1);
                        logger.info("Created volunteer profile with ID: {} for user ID: {}", id, volunteer.getUserId());
                        return id;
                    }
                }
            }
            
        } catch (SQLException e) {
            logger.error("Error creating volunteer profile for user ID: {}", volunteer.getUserId(), e);
        }
        
        return 0;
    }

    /**
     * Get volunteer profile by volunteer ID
     * 
     * @param id Volunteer ID
     * @return Volunteer object or null if not found
     */
    public Volunteer getById(int id) {
        String sql = "SELECT v.*, u.username, u.email, u.full_name, u.phone, u.role " +
                    "FROM volunteers v " +
                    "INNER JOIN users u ON v.user_id = u.id " +
                    "WHERE v.id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToVolunteer(rs);
                }
            }
            
        } catch (SQLException e) {
            logger.error("Error getting volunteer by ID: {}", id, e);
        }
        
        return null;
    }

    /**
     * Get volunteer profile by user ID
     * 
     * @param userId User ID
     * @return Volunteer object or null if not found
     */
    public Volunteer getByUserId(int userId) {
        String sql = "SELECT v.*, u.username, u.email, u.full_name, u.phone, u.role " +
                    "FROM volunteers v " +
                    "INNER JOIN users u ON v.user_id = u.id " +
                    "WHERE v.user_id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToVolunteer(rs);
                }
            }
            
        } catch (SQLException e) {
            logger.error("Error getting volunteer by user ID: {}", userId, e);
        }
        
        return null;
    }

    /**
     * Update volunteer profile
     * 
     * @param volunteer Volunteer object with updated information
     * @return true if update successful, false otherwise
     */
    public boolean update(Volunteer volunteer) {
        String sql = "UPDATE volunteers SET skills = ?, availability = ?, experience = ?, " +
                    "interests = ?, emergency_contact = ?, emergency_phone = ?, status = ?, " +
                    "updated_at = NOW() WHERE id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, volunteer.getSkills());
            stmt.setString(2, volunteer.getAvailability());
            stmt.setString(3, volunteer.getExperience());
            stmt.setString(4, volunteer.getInterests());
            stmt.setString(5, volunteer.getEmergencyContact());
            stmt.setString(6, volunteer.getEmergencyPhone());
            stmt.setString(7, volunteer.getStatus());
            stmt.setInt(8, volunteer.getId());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                logger.info("Updated volunteer profile ID: {}", volunteer.getId());
                return true;
            }
            
        } catch (SQLException e) {
            logger.error("Error updating volunteer profile ID: {}", volunteer.getId(), e);
        }
        
        return false;
    }

    /**
     * Update volunteer status
     * 
     * @param volunteerId Volunteer ID
     * @param status New status (active, inactive, suspended)
     * @return true if update successful, false otherwise
     */
    public boolean updateStatus(int volunteerId, String status) {
        String sql = "UPDATE volunteers SET status = ?, updated_at = NOW() WHERE id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, status);
            stmt.setInt(2, volunteerId);
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                logger.info("Updated volunteer ID: {} status to: {}", volunteerId, status);
                return true;
            }
            
        } catch (SQLException e) {
            logger.error("Error updating volunteer status for ID: {}", volunteerId, e);
        }
        
        return false;
    }

    /**
     * Delete volunteer profile
     * 
     * @param id Volunteer ID
     * @return true if deletion successful, false otherwise
     */
    public boolean delete(int id) {
        String sql = "DELETE FROM volunteers WHERE id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                logger.info("Deleted volunteer profile ID: {}", id);
                return true;
            }
            
        } catch (SQLException e) {
            logger.error("Error deleting volunteer profile ID: {}", id, e);
        }
        
        return false;
    }

    /**
     * Get all volunteer profiles
     * 
     * @return List of all volunteers
     */
    public List<Volunteer> getAll() {
        return getAll(null, null, 0, 0);
    }

    /**
     * Get all volunteer profiles with filtering and pagination
     * 
     * @param status Filter by status (null for all)
     * @param orderBy Order by column (default: created_at DESC)
     * @param limit Number of records to return (0 for all)
     * @param offset Offset for pagination
     * @return List of volunteers
     */
    public List<Volunteer> getAll(String status, String orderBy, int limit, int offset) {
        List<Volunteer> volunteers = new ArrayList<>();
        StringBuilder sql = new StringBuilder();
        
        sql.append("SELECT v.*, u.username, u.email, u.full_name, u.phone, u.role ");
        sql.append("FROM volunteers v ");
        sql.append("INNER JOIN users u ON v.user_id = u.id ");
        
        if (status != null && !status.isEmpty()) {
            sql.append("WHERE v.status = ? ");
        }
        
        if (orderBy != null && !orderBy.isEmpty()) {
            sql.append("ORDER BY ").append(orderBy).append(" ");
        } else {
            sql.append("ORDER BY v.created_at DESC ");
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
            
            if (limit > 0) {
                stmt.setInt(paramIndex++, limit);
                stmt.setInt(paramIndex++, offset);
            }
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    volunteers.add(mapResultSetToVolunteer(rs));
                }
            }
            
            logger.info("Retrieved {} volunteer profiles", volunteers.size());
            
        } catch (SQLException e) {
            logger.error("Error getting all volunteers", e);
        }
        
        return volunteers;
    }

    /**
     * Search volunteers by skill
     * 
     * @param skillKeyword Keyword to search in skills field
     * @return List of matching volunteers
     */
    public List<Volunteer> searchBySkill(String skillKeyword) {
        List<Volunteer> volunteers = new ArrayList<>();
        String sql = "SELECT v.*, u.username, u.email, u.full_name, u.phone, u.role " +
                    "FROM volunteers v " +
                    "INNER JOIN users u ON v.user_id = u.id " +
                    "WHERE v.skills LIKE ? AND v.status = 'active' " +
                    "ORDER BY v.created_at DESC";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, "%" + skillKeyword + "%");
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    volunteers.add(mapResultSetToVolunteer(rs));
                }
            }
            
            logger.info("Found {} volunteers with skill: {}", volunteers.size(), skillKeyword);
            
        } catch (SQLException e) {
            logger.error("Error searching volunteers by skill: {}", skillKeyword, e);
        }
        
        return volunteers;
    }

    /**
     * Search volunteers by multiple criteria
     * 
     * @param searchTerm Search term for name, email, skills
     * @param status Filter by status
     * @param availability Filter by availability
     * @return List of matching volunteers
     */
    public List<Volunteer> search(String searchTerm, String status, String availability) {
        List<Volunteer> volunteers = new ArrayList<>();
        StringBuilder sql = new StringBuilder();
        
        sql.append("SELECT v.*, u.username, u.email, u.full_name, u.phone, u.role ");
        sql.append("FROM volunteers v ");
        sql.append("INNER JOIN users u ON v.user_id = u.id ");
        sql.append("WHERE 1=1 ");
        
        if (searchTerm != null && !searchTerm.isEmpty()) {
            sql.append("AND (u.full_name LIKE ? OR u.email LIKE ? OR v.skills LIKE ? OR v.interests LIKE ?) ");
        }
        
        if (status != null && !status.isEmpty()) {
            sql.append("AND v.status = ? ");
        }
        
        if (availability != null && !availability.isEmpty()) {
            sql.append("AND v.availability LIKE ? ");
        }
        
        sql.append("ORDER BY v.created_at DESC");
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            
            int paramIndex = 1;
            
            if (searchTerm != null && !searchTerm.isEmpty()) {
                String searchPattern = "%" + searchTerm + "%";
                stmt.setString(paramIndex++, searchPattern);
                stmt.setString(paramIndex++, searchPattern);
                stmt.setString(paramIndex++, searchPattern);
                stmt.setString(paramIndex++, searchPattern);
            }
            
            if (status != null && !status.isEmpty()) {
                stmt.setString(paramIndex++, status);
            }
            
            if (availability != null && !availability.isEmpty()) {
                stmt.setString(paramIndex++, "%" + availability + "%");
            }
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    volunteers.add(mapResultSetToVolunteer(rs));
                }
            }
            
            logger.info("Found {} volunteers matching search criteria", volunteers.size());
            
        } catch (SQLException e) {
            logger.error("Error searching volunteers", e);
        }
        
        return volunteers;
    }

    /**
     * Get count of volunteers by status
     * 
     * @return Map with status as key and count as value
     */
    public Map<String, Integer> getCountByStatus() {
        Map<String, Integer> counts = new HashMap<>();
        String sql = "SELECT status, COUNT(*) as count FROM volunteers GROUP BY status";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                counts.put(rs.getString("status"), rs.getInt("count"));
            }
            
            logger.info("Retrieved volunteer counts by status");
            
        } catch (SQLException e) {
            logger.error("Error getting volunteer counts by status", e);
        }
        
        return counts;
    }

    /**
     * Get total count of volunteers
     * 
     * @param status Filter by status (null for all)
     * @return Total count
     */
    public int getCount(String status) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM volunteers");
        
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
            logger.error("Error getting volunteer count", e);
        }
        
        return 0;
    }

    /**
     * Get volunteer statistics
     * 
     * @return Map with various statistics
     */
    public Map<String, Object> getStats() {
        Map<String, Object> stats = new HashMap<>();
        
        try (Connection conn = DatabaseUtil.getConnection()) {
            
            // Total volunteers
            try (PreparedStatement stmt = conn.prepareStatement("SELECT COUNT(*) FROM volunteers")) {
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    stats.put("total", rs.getInt(1));
                }
            }
            
            // Active volunteers
            try (PreparedStatement stmt = conn.prepareStatement(
                    "SELECT COUNT(*) FROM volunteers WHERE status = 'active'")) {
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    stats.put("active", rs.getInt(1));
                }
            }
            
            // Inactive volunteers
            try (PreparedStatement stmt = conn.prepareStatement(
                    "SELECT COUNT(*) FROM volunteers WHERE status = 'inactive'")) {
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    stats.put("inactive", rs.getInt(1));
                }
            }
            
            // New volunteers this month
            try (PreparedStatement stmt = conn.prepareStatement(
                    "SELECT COUNT(*) FROM volunteers WHERE created_at >= DATE_SUB(NOW(), INTERVAL 1 MONTH)")) {
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    stats.put("new_this_month", rs.getInt(1));
                }
            }
            
            // Volunteers with event registrations
            try (PreparedStatement stmt = conn.prepareStatement(
                    "SELECT COUNT(DISTINCT volunteer_id) FROM event_registrations")) {
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    stats.put("with_registrations", rs.getInt(1));
                }
            }
            
            logger.info("Retrieved volunteer statistics");
            
        } catch (SQLException e) {
            logger.error("Error getting volunteer statistics", e);
        }
        
        return stats;
    }

    /**
     * Check if a user already has a volunteer profile
     * 
     * @param userId User ID
     * @return true if profile exists, false otherwise
     */
    public boolean hasProfile(int userId) {
        String sql = "SELECT COUNT(*) FROM volunteers WHERE user_id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
            
        } catch (SQLException e) {
            logger.error("Error checking if user has volunteer profile: {}", userId, e);
        }
        
        return false;
    }

    /**
     * Get volunteers registered for a specific event
     * 
     * @param eventId Event ID
     * @return List of volunteers registered for the event
     */
    public List<Volunteer> getByEvent(int eventId) {
        List<Volunteer> volunteers = new ArrayList<>();
        String sql = "SELECT v.*, u.username, u.email, u.full_name, u.phone, u.role " +
                    "FROM volunteers v " +
                    "INNER JOIN users u ON v.user_id = u.id " +
                    "INNER JOIN event_registrations er ON v.id = er.volunteer_id " +
                    "WHERE er.event_id = ? AND er.status IN ('confirmed', 'attended') " +
                    "ORDER BY er.registered_at DESC";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, eventId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    volunteers.add(mapResultSetToVolunteer(rs));
                }
            }
            
            logger.info("Retrieved {} volunteers for event ID: {}", volunteers.size(), eventId);
            
        } catch (SQLException e) {
            logger.error("Error getting volunteers by event ID: {}", eventId, e);
        }
        
        return volunteers;
    }

    /**
     * Map ResultSet to Volunteer object
     * 
     * @param rs ResultSet
     * @return Volunteer object
     * @throws SQLException if database access error occurs
     */
    private Volunteer mapResultSetToVolunteer(ResultSet rs) throws SQLException {
        Volunteer volunteer = new Volunteer();
        
        volunteer.setId(rs.getInt("id"));
        volunteer.setUserId(rs.getInt("user_id"));
        volunteer.setSkills(rs.getString("skills"));
        volunteer.setAvailability(rs.getString("availability"));
        volunteer.setExperience(rs.getString("experience"));
        volunteer.setInterests(rs.getString("interests"));
        volunteer.setEmergencyContact(rs.getString("emergency_contact"));
        volunteer.setEmergencyPhone(rs.getString("emergency_phone"));
        volunteer.setStatus(rs.getString("status"));
        volunteer.setCreatedAt(rs.getTimestamp("created_at"));
        volunteer.setUpdatedAt(rs.getTimestamp("updated_at"));
        
        // User information from join
        volunteer.setUsername(rs.getString("username"));
        volunteer.setEmail(rs.getString("email"));
        volunteer.setFullName(rs.getString("full_name"));
        volunteer.setPhone(rs.getString("phone"));
        volunteer.setRole(rs.getString("role"));
        
        return volunteer;
    }
}
