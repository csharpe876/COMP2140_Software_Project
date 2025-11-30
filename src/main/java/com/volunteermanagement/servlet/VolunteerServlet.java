package com.volunteermanagement.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Volunteer management servlet for profiles and directory.
 * Enhanced with Java 21 patterns and proper error handling.
 */
public class VolunteerServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(VolunteerServlet.class);
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        logger.debug("Volunteer GET request");

        try {
            listVolunteers(req, resp);
        } catch (Exception e) {
            logger.error("Error in VolunteerServlet GET", e);
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "An error occurred");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        logger.debug("Volunteer POST request");
        
        try {
            // TODO: Handle volunteer profile updates
            resp.sendRedirect(req.getContextPath() + "/volunteers/");
        } catch (Exception e) {
            logger.error("Error in VolunteerServlet POST", e);
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "An error occurred");
        }
    }

    private void listVolunteers(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("text/html;charset=UTF-8");
        String contextPath = req.getContextPath();
        
        resp.getWriter().write("""
            <!DOCTYPE html>
            <html lang="en">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>Volunteers - Volunteer Management</title>
                <style>
                    body { font-family: system-ui, Arial, sans-serif; max-width: 1000px; margin: 20px auto; padding: 20px; }
                    .volunteer-card { border: 1px solid #ddd; padding: 15px; margin: 10px 0; border-radius: 5px; }
                    a { color: #0366d6; text-decoration: none; }
                    a:hover { text-decoration: underline; }
                </style>
            </head>
            <body>
                <h2>Volunteer Directory</h2>
                <p><a href="%s/">← Back to Home</a></p>
                
                <p><em>Volunteer directory will be populated from database.</em></p>
                <p>Features coming soon:</p>
                <ul>
                    <li>Volunteer profiles with skills and experience</li>
                    <li>Search and filter capabilities</li>
                    <li>Contact information for coordinators</li>
                </ul>
            </body>
            </html>
            """.formatted(contextPath));
    }
}
