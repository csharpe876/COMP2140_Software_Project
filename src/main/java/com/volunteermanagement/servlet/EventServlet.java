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

    private void listEvents(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        var eventDAO = new com.volunteermanagement.dao.EventDAO();
        var events = eventDAO.findActiveEvents();
        
        req.setAttribute("events", events);
        req.getRequestDispatcher("/WEB-INF/views/events/list.jsp").forward(req, resp);
    }
}
