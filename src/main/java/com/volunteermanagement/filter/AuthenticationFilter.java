package com.volunteermanagement.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Set;

/**
 * Authentication filter ensuring users are logged in for protected resources.
 * Enhanced with Java 21 patterns, configurable excluded paths, and proper session handling.
 */
public class AuthenticationFilter implements Filter {
    private static final Logger logger = LoggerFactory.getLogger(AuthenticationFilter.class);
    private static final Set<String> EXCLUDED_PATHS = Set.of(
        "/auth/login",
        "/auth/register",
        "/",
        "/index.jsp"
    );

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
        
        // Allow excluded paths
        if (isExcludedPath(fullPath)) {
            chain.doFilter(request, response);
            return;
        }
        
        // Check session
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

    private boolean isExcludedPath(String path) {
        return EXCLUDED_PATHS.stream().anyMatch(path::startsWith);
    }

    @Override
    public void destroy() {
        logger.debug("AuthenticationFilter destroyed");
    }
}
