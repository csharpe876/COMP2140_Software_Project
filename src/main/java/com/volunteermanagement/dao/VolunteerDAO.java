package com.volunteermanagement.dao;

import com.volunteermanagement.model.Volunteer;
import com.volunteermanagement.util.DatabaseUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Data Access Object for managing Volunteer entities.
 * Provides CRUD operations and queries for volunteer profiles.
 */
public class VolunteerDAO {
    
    public Optional<Volunteer> findById(int id) {
        String sql = "SELECT * FROM volunteers WHERE id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return Optional.of(mapResultSetToVolunteer(rs));
            }
            return Optional.empty();
            
        } catch (SQLException e) {
            throw new RuntimeException("Error finding volunteer by ID: " + id, e);
        }
    }
    
    public Optional<Volunteer> findByUserId(int userId) {
        String sql = "SELECT * FROM volunteers WHERE user_id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return Optional.of(mapResultSetToVolunteer(rs));
            }
            return Optional.empty();
            
        } catch (SQLException e) {
            throw new RuntimeException("Error finding volunteer by user ID: " + userId, e);
        }
    }
    
    public List<Volunteer> findAllActive() {
        String sql = "SELECT * FROM volunteers WHERE status = 'ACTIVE' ORDER BY created_at DESC";
        List<Volunteer> volunteers = new ArrayList<>();
        
        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                volunteers.add(mapResultSetToVolunteer(rs));
            }
            
        } catch (SQLException e) {
            throw new RuntimeException("Error finding active volunteers", e);
        }
        
        return volunteers;
    }
    
    public List<Volunteer> findBySkill(String skill) {
        String sql = "SELECT * FROM volunteers WHERE skills LIKE ? AND status = 'ACTIVE' ORDER BY created_at DESC";
        List<Volunteer> volunteers = new ArrayList<>();
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, "%" + skill + "%");
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                volunteers.add(mapResultSetToVolunteer(rs));
            }
            
        } catch (SQLException e) {
            throw new RuntimeException("Error finding volunteers by skill: " + skill, e);
        }
        
        return volunteers;
    }
    
    public Volunteer create(Volunteer volunteer) {
        String sql = """
            INSERT INTO volunteers (user_id, skills, availability, emergency_contact, 
                                   emergency_phone, status)
            VALUES (?, ?, ?, ?, ?, ?)
            """;
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setInt(1, volunteer.getUserId());
            stmt.setString(2, volunteer.getSkills());
            stmt.setString(3, volunteer.getAvailability());
            stmt.setString(4, volunteer.getEmergencyContact());
            stmt.setString(5, volunteer.getEmergencyPhone());
            stmt.setString(6, volunteer.getStatus().name());
            
            int affected = stmt.executeUpdate();
            if (affected == 0) {
                throw new SQLException("Creating volunteer failed, no rows affected");
            }
            
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    volunteer.setId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Creating volunteer failed, no ID obtained");
                }
            }
            
            return volunteer;
            
        } catch (SQLException e) {
            throw new RuntimeException("Error creating volunteer", e);
        }
    }
    
    public boolean update(Volunteer volunteer) {
        String sql = """
            UPDATE volunteers 
            SET skills = ?, availability = ?, emergency_contact = ?, 
                emergency_phone = ?, status = ?
            WHERE id = ?
            """;
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, volunteer.getSkills());
            stmt.setString(2, volunteer.getAvailability());
            stmt.setString(3, volunteer.getEmergencyContact());
            stmt.setString(4, volunteer.getEmergencyPhone());
            stmt.setString(5, volunteer.getStatus().name());
            stmt.setInt(6, volunteer.getId());
            
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            throw new RuntimeException("Error updating volunteer: " + volunteer.getId(), e);
        }
    }
    
    // Note: hours_logged field not present in current Volunteer model
    // Can be added later if needed
    
    public boolean updateStatus(int volunteerId, Volunteer.VolunteerStatus status) {
        String sql = "UPDATE volunteers SET status = ? WHERE id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, status.name());
            stmt.setInt(2, volunteerId);
            
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            throw new RuntimeException("Error updating status for volunteer: " + volunteerId, e);
        }
    }
    
    public boolean delete(int id) {
        String sql = "DELETE FROM volunteers WHERE id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting volunteer: " + id, e);
        }
    }
    
    private Volunteer mapResultSetToVolunteer(ResultSet rs) throws SQLException {
        var volunteer = new Volunteer();
        volunteer.setId(rs.getInt("id"));
        volunteer.setUserId(rs.getInt("user_id"));
        volunteer.setSkills(rs.getString("skills"));
        volunteer.setAvailability(rs.getString("availability"));
        volunteer.setEmergencyContact(rs.getString("emergency_contact"));
        volunteer.setEmergencyPhone(rs.getString("emergency_phone"));
        volunteer.setStatus(Volunteer.VolunteerStatus.valueOf(rs.getString("status")));
        volunteer.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        
        Timestamp updatedAt = rs.getTimestamp("updated_at");
        if (updatedAt != null) {
            volunteer.setUpdatedAt(updatedAt.toLocalDateTime());
        }
        
        return volunteer;
    }
}
