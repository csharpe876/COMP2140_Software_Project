/**
 * Events JavaScript
 * Handles event-specific functionality
 */

/**
 * Filter events by category
 */
function filterByCategory(category) {
    const url = new URL(window.location.href);
    if (category) {
        url.searchParams.set('category', category);
    } else {
        url.searchParams.delete('category');
    }
    url.searchParams.delete('status');
    window.location.href = url.toString();
}

/**
 * Filter events by status
 */
function filterByStatus(status) {
    const url = new URL(window.location.href);
    if (status) {
        url.searchParams.set('status', status);
    } else {
        url.searchParams.delete('status');
    }
    url.searchParams.delete('category');
    window.location.href = url.toString();
}

/**
 * Delete event with confirmation
 */
function deleteEvent(eventId) {
    if (confirm('Are you sure you want to delete this event? This action cannot be undone.')) {
        window.location.href = `${getBaseUrl()}controllers/EventController.php?action=delete&id=${eventId}`;
    }
}

/**
 * Cancel event registration
 */
function cancelRegistration(registrationId, eventTitle) {
    if (confirm(`Are you sure you want to cancel your registration for "${eventTitle}"?`)) {
        window.location.href = `${getBaseUrl()}controllers/EventController.php?action=cancel&id=${registrationId}`;
    }
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

// Search events in grid
document.addEventListener('DOMContentLoaded', function() {
    const searchInput = document.getElementById('searchInput');
    if (searchInput && document.querySelector('.events-grid')) {
        searchInput.addEventListener('keyup', function() {
            searchEvents(this.value);
        });
    }
});

/**
 * Search events
 */
function searchEvents(searchTerm) {
    const eventCards = document.querySelectorAll('.event-card');
    searchTerm = searchTerm.toLowerCase();

    eventCards.forEach(card => {
        const text = card.textContent.toLowerCase();
        card.style.display = text.includes(searchTerm) ? '' : 'none';
    });
}

/**
 * Validate event form
 */
function validateEventForm() {
    const startTime = document.getElementById('start_time').value;
    const endTime = document.getElementById('end_time').value;
    const eventDate = document.getElementById('event_date').value;
    
    // Check if end time is after start time
    if (startTime && endTime && endTime <= startTime) {
        alert('End time must be after start time');
        return false;
    }

    // Check if event date is not in the past
    const today = new Date();
    today.setHours(0, 0, 0, 0);
    const selectedDate = new Date(eventDate);
    
    if (selectedDate < today) {
        alert('Event date cannot be in the past');
        return false;
    }

    return true;
}

// Add event form validation
document.addEventListener('DOMContentLoaded', function() {
    const eventForm = document.querySelector('.event-form');
    if (eventForm) {
        eventForm.addEventListener('submit', function(e) {
            if (!validateEventForm()) {
                e.preventDefault();
            }
        });
    }
});
