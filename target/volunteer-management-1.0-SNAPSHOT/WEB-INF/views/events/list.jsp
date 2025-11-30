<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<c:set var="pageTitle" value="Volunteer Events" scope="request"/>
<c:set var="containerClass" value="container-fluid" scope="request"/>
<jsp:include page="../common/header.jsp"/>

<!-- Page Header -->
<div class="page-header">
    <h1 class="app-title">The Faculty of Science & Technology Volunteer Management System</h1>
    <h2 class="page-title">Volunteer Events</h2>
</div>

<!-- Search and Filter -->
<div class="mb-4">
    <div class="d-flex gap-2">
        <input 
            type="text" 
            id="searchEvents" 
            class="form-control" 
            placeholder="🔍 Search events by title, location, or category..."
            style="max-width: 500px;">
        <button class="btn btn-secondary" onclick="clearSearch()">Clear</button>
    </div>
</div>

<!-- No Events Message -->
<c:if test="${empty events}">
    <div class="alert alert-info">
        <p class="text-center mb-0">
            <strong>No active events at this time.</strong><br>
            Check back soon for new volunteer opportunities!
        </p>
    </div>
</c:if>

<!-- Events Grid -->
<div id="eventsContainer" class="row">
    <c:forEach items="${events}" var="event">
        <div class="col-md-6 mb-3 event-item">
            <div class="card event-card ${event.full ? 'full' : ''}">
                <div class="card-header">
                    <h3 class="card-title">${event.title}</h3>
                    <c:if test="${event.full}">
                        <span class="event-status full">FULL</span>
                    </c:if>
                    <c:if test="${!event.full}">
                        <span class="event-status available">Open</span>
                    </c:if>
                </div>
                <div class="card-body">
                    <div class="event-meta">
                        <p><strong>📅 Date:</strong> <fmt:formatDate value="${event.eventDate}" pattern="MMMM dd, yyyy"/></p>
                        <p><strong>🕐 Time:</strong> 
                            <c:choose>
                                <c:when test="${not empty event.startTime}">
                                    ${event.startTime} - ${event.endTime}
                                </c:when>
                                <c:otherwise>TBD</c:otherwise>
                            </c:choose>
                        </p>
                        <p><strong>📍 Location:</strong> ${event.location}</p>
                        <p><strong>🏷️ Category:</strong> ${event.category}</p>
                    </div>
                    
                    <p class="mt-3">${event.description}</p>
                    
                    <div class="mt-3">
                        <strong>Volunteers:</strong> 
                        <span class="${event.full ? 'text-danger' : 'text-success'}">
                            ${event.volunteersRegistered} / ${event.volunteersNeeded} registered
                            <c:if test="${event.full}"> (FULL)</c:if>
                        </span>
                    </div>
                </div>
                <div class="card-footer d-flex justify-content-between align-items-center">
                    <button 
                        class="btn btn-sm btn-primary view-details-btn" 
                        onclick="viewEventDetails(${event.id})"
                        data-event-id="${event.id}">
                        View Details
                    </button>
                    
                    <c:if test="${not empty sessionScope.userId && !event.full}">
                        <button 
                            class="btn btn-sm btn-success register-btn" 
                            onclick="registerForEvent(${event.id}, '${event.title}')"
                            data-event-id="${event.id}">
                            Register
                        </button>
                    </c:if>
                    
                    <c:if test="${event.full}">
                        <button class="btn btn-sm btn-secondary" disabled>
                            Event Full
                        </button>
                    </c:if>
                </div>
            </div>
        </div>
    </c:forEach>
</div>

<!-- Event Details Modal -->
<div id="eventDetailsModal" class="modal">
    <div class="modal-content">
        <div class="modal-header">
            <h3 class="modal-title" id="modalEventTitle">Event Details</h3>
            <button class="modal-close" onclick="Modal.close('eventDetailsModal')">&times;</button>
        </div>
        <div class="modal-body" id="modalEventBody">
            <!-- Content loaded dynamically -->
        </div>
        <div class="modal-footer">
            <button class="btn btn-secondary" onclick="Modal.close('eventDetailsModal')">Close</button>
            <button class="btn btn-primary" id="modalRegisterBtn" onclick="registerFromModal()">Register</button>
        </div>
    </div>
</div>

<!-- Page-specific JavaScript -->
<script>
    // Search functionality
    const searchInput = document.getElementById('searchEvents');
    const eventItems = document.querySelectorAll('.event-item');
    
    if (searchInput) {
        Search.setupSearch(searchInput, eventItems, ['.card-title', '.event-meta', '.card-body p']);
    }

    function clearSearch() {
        searchInput.value = '';
        eventItems.forEach(item => item.style.display = '');
    }

    // View event details in modal
    function viewEventDetails(eventId) {
        // In a real implementation, fetch event details via AJAX
        // For now, we'll extract from the card
        const card = document.querySelector(`[data-event-id="${eventId}"]`).closest('.card');
        const title = card.querySelector('.card-title').textContent;
        const content = card.querySelector('.card-body').innerHTML;
        
        document.getElementById('modalEventTitle').textContent = title;
        document.getElementById('modalEventBody').innerHTML = content;
        
        Modal.open('eventDetailsModal');
    }

    // Register for event
    function registerForEvent(eventId, eventTitle) {
        <c:choose>
            <c:when test="${empty sessionScope.userId}">
                Toast.warning('Please login to register for events');
                setTimeout(() => {
                    window.location.href = '${pageContext.request.contextPath}/auth/login';
                }, 1500);
            </c:when>
            <c:otherwise>
                if (confirm('Do you want to register for "' + eventTitle + '"?')) {
                    Loading.show('Registering...');
                    
                    Ajax.post('${pageContext.request.contextPath}/register/event', {
                        eventId: eventId
                    })
                    .then(response => {
                        Loading.hide();
                        if (response.success) {
                            Toast.success('Successfully registered for event!');
                            // Optionally reload page or update UI
                            setTimeout(() => location.reload(), 2000);
                        } else {
                            Toast.error(response.message || 'Registration failed');
                        }
                    })
                    .catch(error => {
                        Loading.hide();
                        Toast.error('An error occurred. Please try again.');
                        console.error('Registration error:', error);
                    });
                }
            </c:otherwise>
        </c:choose>
    }

    // Register from modal
    function registerFromModal() {
        const eventId = document.getElementById('modalEventBody')
            .querySelector('[data-event-id]')?.dataset.eventId;
        
        if (eventId) {
            Modal.close('eventDetailsModal');
            const card = document.querySelector(`[data-event-id="${eventId}"]`).closest('.card');
            const title = card.querySelector('.card-title').textContent;
            registerForEvent(eventId, title);
        }
    }

    // Show toast if there's a success message
    <c:if test="${not empty param.success}">
        Toast.success('${param.success}');
    </c:if>

    <c:if test="${not empty param.error}">
        Toast.error('${param.error}');
    </c:if>
</script>

<jsp:include page="../common/footer.jsp"/>
