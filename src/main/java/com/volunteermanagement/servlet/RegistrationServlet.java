package com.volunteermanagement.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Event registration servlet for managing volunteer sign-ups.
 * Enhanced with Java 21 patterns and proper error handling.
 */
public class RegistrationServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(RegistrationServlet.class);
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        logger.debug("Registration GET request");

        try {
            showRegistrations(req, resp);
        } catch (Exception e) {
            logger.error("Error in RegistrationServlet GET", e);
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "An error occurred");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        logger.debug("Registration POST request");
        
        try {
            // TODO: Handle event registration
            String eventId = req.getParameter("eventId");
            logger.info("Registration request for event: {}", eventId);
            
            resp.sendRedirect(req.getContextPath() + "/events/");
        } catch (Exception e) {
            logger.error("Error in RegistrationServlet POST", e);
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "An error occurred");
        }
    }

    private void showRegistrations(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("text/html;charset=UTF-8");
        String contextPath = req.getContextPath();
        
        resp.getWriter().write("""
            <!DOCTYPE html>
            <html lang="en">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>My Registrations - Volunteer Management</title>
                <style>
                    body { font-family: system-ui, Arial, sans-serif; max-width: 1000px; margin: 20px auto; padding: 20px; }
                    .reg-card { border: 1px solid #ddd; padding: 15px; margin: 10px 0; border-radius: 5px; }
                    .status { padding: 3px 8px; border-radius: 3px; font-size: 0.9em; }
                    .confirmed { background: #d4edda; color: #155724; }
                    a { color: #0366d6; text-decoration: none; }
                    a:hover { text-decoration: underline; }
                </style>
            </head>
            <body>
                <h2>My Event Registrations</h2>
                <p><a href="%s/">← Back to Home</a> | <a href="%s/events/">Browse Events</a></p>
                
                <p><em>Your event registrations will appear here once you sign up for events.</em></p>
                <p>Features coming soon:</p>
                <ul>
                    <li>View all your registered events</li>
                    <li>Cancel registrations</li>
                    <li>Check-in status tracking</li>
                    <li>Volunteer hours tracking</li>
                </ul>
            </body>
            </html>
            """.formatted(contextPath, contextPath));
    }
}
