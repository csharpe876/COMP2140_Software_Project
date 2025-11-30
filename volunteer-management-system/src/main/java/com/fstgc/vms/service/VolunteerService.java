package com.fstgc.vms.service;

import com.fstgc.vms.model.Volunteer;
import com.fstgc.vms.repository.VolunteerRepository;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.SQLException;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Volunteer Service Layer
 * Contains business logic for volunteer operations
 */
public class VolunteerService {
    private final VolunteerRepository volunteerRepository;
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$"
    );
    private static final Pattern PHONE_PATTERN = Pattern.compile("^[+]?[0-9]{10,15}$");

    public VolunteerService() {
        this.volunteerRepository = new VolunteerRepository();
    }

    /**
     * Register a new volunteer
     * @param volunteer Volunteer object to register
     * @return Registered volunteer with ID
     * @throws IllegalArgumentException if validation fails
     * @throws SQLException if database error occurs
     */
    public Volunteer registerVolunteer(Volunteer volunteer) throws IllegalArgumentException, SQLException {
        // Validate volunteer data
        validateVolunteer(volunteer);
        
        // Check if email already exists
        Volunteer existingVolunteer = volunteerRepository.findByEmail(volunteer.getEmail());
        if (existingVolunteer != null) {
            throw new IllegalArgumentException("Email already registered");
        }
        
        // Hash password if provided
        if (volunteer.getPasswordHash() != null && !volunteer.getPasswordHash().isEmpty()) {
            String hashedPassword = BCrypt.hashpw(volunteer.getPasswordHash(), BCrypt.gensalt());
            volunteer.setPasswordHash(hashedPassword);
        }
        
        // Save volunteer
        return volunteerRepository.save(volunteer);
    }

    /**
     * Get all active volunteers
     * @return List of active volunteers
     * @throws SQLException if database error occurs
     */
    public List<Volunteer> getAllActiveVolunteers() throws SQLException {
        return volunteerRepository.findAll();
    }

    /**
     * Get volunteer by ID
     * @param volunteerId Volunteer ID
     * @return Volunteer object
     * @throws SQLException if database error occurs
     */
    public Volunteer getVolunteerById(int volunteerId) throws SQLException {
        Volunteer volunteer = volunteerRepository.findById(volunteerId);
        if (volunteer == null) {
            throw new IllegalArgumentException("Volunteer not found with ID: " + volunteerId);
        }
        return volunteer;
    }

    /**
     * Get volunteer by email
     * @param email Volunteer email
     * @return Volunteer object
     * @throws SQLException if database error occurs
     */
    public Volunteer getVolunteerByEmail(String email) throws SQLException {
        Volunteer volunteer = volunteerRepository.findByEmail(email);
        if (volunteer == null) {
            throw new IllegalArgumentException("Volunteer not found with email: " + email);
        }
        return volunteer;
    }

    /**
     * Update volunteer information
     * @param volunteer Volunteer object with updated information
     * @return Updated volunteer
     * @throws SQLException if database error occurs
     */
    public Volunteer updateVolunteer(Volunteer volunteer) throws SQLException {
        // Validate volunteer data
        validateVolunteer(volunteer);
        
        // Check if volunteer exists
        Volunteer existingVolunteer = volunteerRepository.findById(volunteer.getVolunteerId());
        if (existingVolunteer == null) {
            throw new IllegalArgumentException("Volunteer not found");
        }
        
        // Check if new email is already taken by another volunteer
        if (!existingVolunteer.getEmail().equals(volunteer.getEmail())) {
            Volunteer emailCheck = volunteerRepository.findByEmail(volunteer.getEmail());
            if (emailCheck != null) {
                throw new IllegalArgumentException("Email already in use by another volunteer");
            }
        }
        
        return volunteerRepository.update(volunteer);
    }

    /**
     * Deactivate a volunteer
     * @param volunteerId Volunteer ID to deactivate
     * @return true if deactivated successfully
     * @throws SQLException if database error occurs
     */
    public boolean deactivateVolunteer(int volunteerId) throws SQLException {
        Volunteer volunteer = volunteerRepository.findById(volunteerId);
        if (volunteer == null) {
            throw new IllegalArgumentException("Volunteer not found");
        }
        return volunteerRepository.delete(volunteerId);
    }

    /**
     * Search volunteers by name
     * @param searchTerm Search term
     * @return List of matching volunteers
     * @throws SQLException if database error occurs
     */
    public List<Volunteer> searchVolunteers(String searchTerm) throws SQLException {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return getAllActiveVolunteers();
        }
        return volunteerRepository.searchByName(searchTerm.trim());
    }

    /**
     * Authenticate volunteer
     * @param email Volunteer email
     * @param password Plain text password
     * @return Volunteer object if authentication successful
     * @throws SQLException if database error occurs
     */
    public Volunteer authenticateVolunteer(String email, String password) throws SQLException {
        Volunteer volunteer = volunteerRepository.findByEmail(email);
        if (volunteer == null) {
            throw new IllegalArgumentException("Invalid email or password");
        }
        
        if (!volunteer.isActive()) {
            throw new IllegalArgumentException("Account is inactive");
        }
        
        if (volunteer.getPasswordHash() == null || !BCrypt.checkpw(password, volunteer.getPasswordHash())) {
            throw new IllegalArgumentException("Invalid email or password");
        }
        
        return volunteer;
    }

    /**
     * Get total volunteer count
     * @return Number of active volunteers
     * @throws SQLException if database error occurs
     */
    public int getTotalVolunteerCount() throws SQLException {
        return volunteerRepository.getVolunteerCount();
    }

    /**
     * Validate volunteer data
     * @param volunteer Volunteer to validate
     * @throws IllegalArgumentException if validation fails
     */
    private void validateVolunteer(Volunteer volunteer) throws IllegalArgumentException {
        if (volunteer == null) {
            throw new IllegalArgumentException("Volunteer object cannot be null");
        }
        
        if (volunteer.getFirstName() == null || volunteer.getFirstName().trim().isEmpty()) {
            throw new IllegalArgumentException("First name is required");
        }
        
        if (volunteer.getLastName() == null || volunteer.getLastName().trim().isEmpty()) {
            throw new IllegalArgumentException("Last name is required");
        }
        
        if (volunteer.getEmail() == null || !isValidEmail(volunteer.getEmail())) {
            throw new IllegalArgumentException("Valid email is required");
        }
        
        if (volunteer.getPhone() != null && !volunteer.getPhone().isEmpty() && !isValidPhone(volunteer.getPhone())) {
            throw new IllegalArgumentException("Invalid phone number format");
        }
    }

    /**
     * Validate email format
     * @param email Email to validate
     * @return true if valid
     */
    private boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }

    /**
     * Validate phone format
     * @param phone Phone to validate
     * @return true if valid
     */
    private boolean isValidPhone(String phone) {
        return phone != null && PHONE_PATTERN.matcher(phone).matches();
    }
}package com.fstgc.vms.service;

import com.fstgc.vms.model.Volunteer;
import com.fstgc.vms.repository.VolunteerRepository;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.SQLException;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Volunteer Service Layer
 * Contains business logic for volunteer operations
 */
public class VolunteerService {
    private final VolunteerRepository volunteerRepository;
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$"
    );
    private static final Pattern PHONE_PATTERN = Pattern.compile("^[+]?[0-9]{10,15}$");

    public VolunteerService() {
        this.volunteerRepository = new VolunteerRepository();
    }

    /**
     * Register a new volunteer
     * @param volunteer Volunteer object to register
     * @return Registered volunteer with ID
     * @throws IllegalArgumentException if validation fails
     * @throws SQLException if database error occurs
     */
    public Volunteer registerVolunteer(Volunteer volunteer) throws IllegalArgumentException, SQLException {
        // Validate volunteer data
        validateVolunteer(volunteer);
        
        // Check if email already exists
        Volunteer existingVolunteer = volunteerRepository.findByEmail(volunteer.getEmail());
        if (existingVolunteer != null) {
            throw new IllegalArgumentException("Email already registered");
        }
        
        // Hash password if provided
        if (volunteer.getPasswordHash() != null && !volunteer.getPasswordHash().isEmpty()) {
            String hashedPassword = BCrypt.hashpw(volunteer.getPasswordHash(), BCrypt.gensalt());
            volunteer.setPasswordHash(hashedPassword);
        }
        
        // Save volunteer
        return volunteerRepository.save(volunteer);
    }

    /**
     * Get all active volunteers
     * @return List of active volunteers
     * @throws SQLException if database error occurs
     */
    public List<Volunteer> getAllActiveVolunteers() throws SQLException {
        return volunteerRepository.findAll();
    }

    /**
     * Get volunteer by ID
     * @param volunteerId Volunteer ID
     * @return Volunteer object
     * @throws SQLException if database error occurs
     */
    public Volunteer getVolunteerById(int volunteerId) throws SQLException {
        Volunteer volunteer = volunteerRepository.findById(volunteerId);
        if (volunteer == null) {
            throw new IllegalArgumentException("Volunteer not found with ID: " + volunteerId);
        }
        return volunteer;
    }

    /**
     * Get volunteer by email
     * @param email Volunteer email
     * @return Volunteer object
     * @throws SQLException if database error occurs
     */
    public Volunteer getVolunteerByEmail(String email) throws SQLException {
        Volunteer volunteer = volunteerRepository.findByEmail(email);
        if (volunteer == null) {
            throw new IllegalArgumentException("Volunteer not found with email: " + email);
        }
        return volunteer;
    }

    /**
     * Update volunteer information
     * @param volunteer Volunteer object with updated information
     * @return Updated volunteer
     * @throws SQLException if database error occurs
     */
    public Volunteer updateVolunteer(Volunteer volunteer) throws SQLException {
        // Validate volunteer data
        validateVolunteer(volunteer);
        
        // Check if volunteer exists
        Volunteer existingVolunteer = volunteerRepository.findById(volunteer.getVolunteerId());
        if (existingVolunteer == null) {
            throw new IllegalArgumentException("Volunteer not found");
        }
        
        // Check if new email is already taken by another volunteer
        if (!existingVolunteer.getEmail().equals(volunteer.getEmail())) {
            Volunteer emailCheck = volunteerRepository.findByEmail(volunteer.getEmail());
            if (emailCheck != null) {
                throw new IllegalArgumentException("Email already in use by another volunteer");
            }
        }
        
        return volunteerRepository.update(volunteer);
    }

    /**
     * Deactivate a volunteer
     * @param volunteerId Volunteer ID to deactivate
     * @return true if deactivated successfully
     * @throws SQLException if database error occurs
     */
    public boolean deactivateVolunteer(int volunteerId) throws SQLException {
        Volunteer volunteer = volunteerRepository.findById(volunteerId);
        if (volunteer == null) {
            throw new IllegalArgumentException("Volunteer not found");
        }
        return volunteerRepository.delete(volunteerId);
    }

    /**
     * Search volunteers by name
     * @param searchTerm Search term
     * @return List of matching volunteers
     * @throws SQLException if database error occurs
     */
    public List<Volunteer> searchVolunteers(String searchTerm) throws SQLException {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return getAllActiveVolunteers();
        }
        return volunteerRepository.searchByName(searchTerm.trim());
    }

    /**
     * Authenticate volunteer
     * @param email Volunteer email
     * @param password Plain text password
     * @return Volunteer object if authentication successful
     * @throws SQLException if database error occurs
     */
    public Volunteer authenticateVolunteer(String email, String password) throws SQLException {
        Volunteer volunteer = volunteerRepository.findByEmail(email);
        if (volunteer == null) {
            throw new IllegalArgumentException("Invalid email or password");
        }
        
        if (!volunteer.isActive()) {
            throw new IllegalArgumentException("Account is inactive");
        }
        
        if (volunteer.getPasswordHash() == null || !BCrypt.checkpw(password, volunteer.getPasswordHash())) {
            throw new IllegalArgumentException("Invalid email or password");
        }
        
        return volunteer;
    }

    /**
     * Get total volunteer count
     * @return Number of active volunteers
     * @throws SQLException if database error occurs
     */
    public int getTotalVolunteerCount() throws SQLException {
        return volunteerRepository.getVolunteerCount();
    }

    /**
     * Validate volunteer data
     * @param volunteer Volunteer to validate
     * @throws IllegalArgumentException if validation fails
     */
    private void validateVolunteer(Volunteer volunteer) throws IllegalArgumentException {
        if (volunteer == null) {
            throw new IllegalArgumentException("Volunteer object cannot be null");
        }
        
        if (volunteer.getFirstName() == null || volunteer.getFirstName().trim().isEmpty()) {
            throw new IllegalArgumentException("First name is required");
        }
        
        if (volunteer.getLastName() == null || volunteer.getLastName().trim().isEmpty()) {
            throw new IllegalArgumentException("Last name is required");
        }
        
        if (volunteer.getEmail() == null || !isValidEmail(volunteer.getEmail())) {
            throw new IllegalArgumentException("Valid email is required");
        }
        
        if (volunteer.getPhone() != null && !volunteer.getPhone().isEmpty() && !isValidPhone(volunteer.getPhone())) {
            throw new IllegalArgumentException("Invalid phone number format");
        }
    }

    /**
     * Validate email format
     * @param email Email to validate
     * @return true if valid
     */
    private boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }

    /**
     * Validate phone format
     * @param phone Phone to validate
     * @return true if valid
     */
    private boolean isValidPhone(String phone) {
        return phone != null && PHONE_PATTERN.matcher(phone).matches();
    }
}