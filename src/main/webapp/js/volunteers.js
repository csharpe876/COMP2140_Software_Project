/**
 * Volunteers JavaScript
 * Handles volunteer-specific functionality
 */

/**
 * View volunteer details in modal
 */
function viewVolunteer(volunteerId) {
    // In a real implementation, this would fetch volunteer details via AJAX
    // For now, redirect to a details page or show basic info
    alert('Viewing volunteer details for ID: ' + volunteerId);
}

/**
 * Toggle volunteer status
 */
function toggleStatus(volunteerId, currentStatus) {
    const newStatus = currentStatus === 'active' ? 'inactive' : 'active';
    const action = newStatus === 'active' ? 'activate' : 'deactivate';
    
    if (confirm(`Are you sure you want to ${action} this volunteer?`)) {
        document.getElementById('statusVolunteerId').value = volunteerId;
        document.getElementById('statusValue').value = newStatus;
        document.getElementById('statusForm').submit();
    }
}

/**
 * Delete volunteer with confirmation
 */
function deleteVolunteer(volunteerId, volunteerName) {
    if (confirm(`Are you sure you want to delete volunteer "${volunteerName}"? This will also delete their user account and cannot be undone.`)) {
        window.location.href = `${getBaseUrl()}controllers/VolunteerController.php?action=delete&id=${volunteerId}`;
    }
}

/**
 * Filter volunteers by status
 */
document.addEventListener('DOMContentLoaded', function() {
    const statusFilter = document.getElementById('statusFilter');
    if (statusFilter) {
        statusFilter.addEventListener('change', function() {
            filterVolunteersByStatus(this.value);
        });
    }
});

/**
 * Filter table by status
 */
function filterVolunteersByStatus(status) {
    const rows = document.querySelectorAll('#volunteersTable tbody tr');
    
    rows.forEach(row => {
        if (!status) {
            row.style.display = '';
        } else {
            const statusBadge = row.querySelector('.badge');
            const rowStatus = statusBadge.textContent.toLowerCase().trim();
            row.style.display = rowStatus === status.toLowerCase() ? '' : 'none';
        }
    });
}

/**
 * Get base URL
 */
function getBaseUrl() {
    const path = window.location.pathname;
    const parts = path.split('/');
    const index = parts.indexOf('COMP2140_Software_Project');
    if (index !== -1) {
        return parts.slice(0, index + 2).join('/') + '/';
    }
    return '/COMP2140_Software_Project/';
}

/**
 * Validate volunteer profile form
 */
document.addEventListener('DOMContentLoaded', function() {
    const profileForm = document.querySelector('.profile-form');
    if (profileForm) {
        profileForm.addEventListener('submit', function(e) {
            const skills = document.getElementById('skills').value.trim();
            const availability = document.getElementById('availability').value.trim();
            const emergencyContact = document.getElementById('emergency_contact').value.trim();
            const emergencyPhone = document.getElementById('emergency_phone').value.trim();

            if (!skills) {
                alert('Please enter your skills');
                e.preventDefault();
                return false;
            }

            if (!availability) {
                alert('Please enter your availability');
                e.preventDefault();
                return false;
            }

            if (!emergencyContact) {
                alert('Please enter an emergency contact name');
                e.preventDefault();
                return false;
            }

            if (!emergencyPhone) {
                alert('Please enter an emergency contact phone');
                e.preventDefault();
                return false;
            }
        });
    }
});
