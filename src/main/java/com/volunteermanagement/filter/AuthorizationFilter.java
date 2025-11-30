package com.volunteermanagement.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Authorization filter enforcing role-based access control.
 * Enhanced with Java 21 patterns and proper permission checking.
 */
public class AuthorizationFilter implements Filter {
    private static final Logger logger = LoggerFactory.getLogger(AuthorizationFilter.class);
    private static final String ADMIN_ROLE = "admin";

    @Override
    public void init(FilterConfig filterConfig) {
        logger.info("AuthorizationFilter initialized");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        var req = (HttpServletRequest) request;
        var resp = (HttpServletResponse) response;
        
        HttpSession session = req.getSession(false);
        
        if (session == null) {
            logger.warn("Authorization check failed: no session");
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Access denied");
            return;
        }
        
        String userRole = (String) session.getAttribute("role");
        String userId = String.valueOf(session.getAttribute("userId"));
        
        logger.debug("Authorization check for user {} with role: {}", userId, userRole);
        
        if (ADMIN_ROLE.equalsIgnoreCase(userRole)) {
            logger.debug("Admin access granted");
            chain.doFilter(request, response);
        } else {
            logger.warn("Authorization failed for user {} with role: {}", userId, userRole);
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Admin access required");
        }
    }

    @Override
    public void destroy() {
        logger.debug("AuthorizationFilter destroyed");
    }
}
