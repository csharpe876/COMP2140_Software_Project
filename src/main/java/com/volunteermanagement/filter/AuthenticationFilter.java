package com.volunteermanagement.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Authentication filter ensuring users are logged in for protected resources.
 * Enhanced with Java 21 patterns, configurable excluded paths, and proper session handling.
 */
public class AuthenticationFilter implements Filter {
    private static final Logger logger = LoggerFactory.getLogger(AuthenticationFilter.class);

    @Override
    public void init(FilterConfig filterConfig) {
        logger.info("AuthenticationFilter initialized");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        var req = (HttpServletRequest) request;
        var resp = (HttpServletResponse) response;
        
        String path = req.getServletPath();
        String pathInfo = req.getPathInfo();
        String fullPath = path + (pathInfo != null ? pathInfo : "");
        
        logger.debug("Authentication check for: {}", fullPath);
        
        // Check session (this filter only runs on protected paths per web.xml)
        HttpSession session = req.getSession(false);
        boolean isAuthenticated = session != null && session.getAttribute("userId") != null;
        
        if (isAuthenticated) {
            logger.debug("User authenticated, proceeding");
            chain.doFilter(request, response);
        } else {
            logger.debug("User not authenticated, redirecting to login");
            String contextPath = req.getContextPath();
            resp.sendRedirect(contextPath + "/auth/login");
        }
    }

    @Override
    public void destroy() {
        logger.debug("AuthenticationFilter destroyed");
    }
}
