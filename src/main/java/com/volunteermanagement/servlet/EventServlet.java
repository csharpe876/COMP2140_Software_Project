package com.volunteermanagement.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Event management servlet for listing, creating, and managing events.
 * Enhanced with Java 21 patterns and proper error handling.
 */
public class EventServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(EventServlet.class);
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        logger.debug("Event GET request: {}", pathInfo);

        try {
            if (pathInfo == null || "/".equals(pathInfo)) {
                listEvents(req, resp);
            } else {
                // TODO: Handle specific event view by ID
                listEvents(req, resp);
            }
        } catch (Exception e) {
            logger.error("Error in EventServlet GET", e);
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "An error occurred");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        logger.debug("Event POST request");
        
        try {
            // TODO: Handle event creation/update
            resp.sendRedirect(req.getContextPath() + "/events/");
        } catch (Exception e) {
            logger.error("Error in EventServlet POST", e);
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "An error occurred");
        }
    }

    private void listEvents(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("text/html;charset=UTF-8");
        String contextPath = req.getContextPath();
        
        resp.getWriter().write("""
            <!DOCTYPE html>
            <html lang="en">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>Events - Volunteer Management</title>
                <style>
                    body { font-family: system-ui, Arial, sans-serif; max-width: 1000px; margin: 20px auto; padding: 20px; }
                    .event-card { border: 1px solid #ddd; padding: 15px; margin: 10px 0; border-radius: 5px; }
                    .event-card h3 { margin-top: 0; }
                    a { color: #0366d6; text-decoration: none; }
                    a:hover { text-decoration: underline; }
                </style>
            </head>
            <body>
                <h2>Volunteer Events</h2>
                <p><a href="%s/">← Back to Home</a></p>
                
                <div class="event-card">
                    <h3>Sample Event 1</h3>
                    <p><strong>Date:</strong> Coming Soon</p>
                    <p><strong>Location:</strong> TBD</p>
                    <p><strong>Description:</strong> Event management functionality will be implemented with database integration.</p>
                    <p><strong>Spots:</strong> TBD</p>
                </div>
                
                <div class="event-card">
                    <h3>Sample Event 2</h3>
                    <p><strong>Date:</strong> Coming Soon</p>
                    <p><strong>Location:</strong> TBD</p>
                    <p><strong>Description:</strong> Full CRUD operations for events coming soon.</p>
                    <p><strong>Spots:</strong> TBD</p>
                </div>
                
                <p><em>TODO: Connect to database to display real events</em></p>
            </body>
            </html>
            """.formatted(contextPath));
    }
}
