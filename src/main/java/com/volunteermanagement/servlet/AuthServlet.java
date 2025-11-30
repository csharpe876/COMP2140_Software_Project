package com.volunteermanagement.servlet;

import com.volunteermanagement.util.ValidationUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Authentication servlet handling login, logout, and registration.
 * Enhanced with Java 21 patterns, proper error handling, and security.
 */
public class AuthServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(AuthServlet.class);
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        logger.debug("Auth GET request: {}", pathInfo);

        try {
            switch (pathInfo != null ? pathInfo : "/") {
                case "/login" -> showLoginPage(req, resp);
                case "/logout" -> handleLogout(req, resp);
                case "/register" -> showRegisterPage(req, resp);
                default -> showLoginPage(req, resp);
            }
        } catch (Exception e) {
            logger.error("Error in AuthServlet GET", e);
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "An error occurred");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        logger.debug("Auth POST request: {}", pathInfo);

        try {
            switch (pathInfo != null ? pathInfo : "/") {
                case "/login" -> handleLogin(req, resp);
                case "/register" -> handleRegister(req, resp);
                default -> resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (Exception e) {
            logger.error("Error in AuthServlet POST", e);
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "An error occurred");
        }
    }

    private void showLoginPage(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        req.getRequestDispatcher("/WEB-INF/views/auth/login.jsp").forward(req, resp);
    }

    private void showRegisterPage(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        req.getRequestDispatcher("/WEB-INF/views/auth/register.jsp").forward(req, resp);
    }

    private void handleLogin(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String username = ValidationUtil.cleanInput(req.getParameter("username"));
        String password = req.getParameter("password");

        if (ValidationUtil.isEmpty(username) || ValidationUtil.isEmpty(password)) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Username and password required");
            return;
        }

        logger.info("Login attempt for user: {}", username);
        
        var userDAO = new com.volunteermanagement.dao.UserDAO();
        var userOpt = userDAO.authenticate(username, password);
        
        if (userOpt.isPresent()) {
            var user = userOpt.get();
            HttpSession session = req.getSession(true);
            session.setAttribute("userId", user.getId());
            session.setAttribute("username", user.getUsername());
            session.setAttribute("fullName", user.getFullName());
            session.setAttribute("role", user.getRoleString());
            session.setMaxInactiveInterval(30 * 60); // 30 minutes
            
            logger.info("User logged in successfully: {} (ID: {})", username, user.getId());
            resp.sendRedirect(req.getContextPath() + "/");
        } else {
            logger.warn("Failed login attempt for user: {}", username);
            resp.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid username or password");
        }
    }

    private void handleRegister(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String username = ValidationUtil.cleanInput(req.getParameter("username"));
        String email = ValidationUtil.cleanInput(req.getParameter("email"));
        String password = req.getParameter("password");
        String fullName = ValidationUtil.cleanInput(req.getParameter("fullName"));

        if (ValidationUtil.isEmpty(username) || ValidationUtil.isEmpty(email) ||
            ValidationUtil.isEmpty(password) || ValidationUtil.isEmpty(fullName)) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "All fields required");
            return;
        }

        if (!ValidationUtil.isValidEmail(email)) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid email format");
            return;
        }
        
        if (!ValidationUtil.isValidUsername(username)) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid username format (3-30 alphanumeric characters)");
            return;
        }

        var userDAO = new com.volunteermanagement.dao.UserDAO();
        
        // Check if username or email already exists
        if (userDAO.existsByUsername(username)) {
            resp.sendError(HttpServletResponse.SC_CONFLICT, "Username already exists");
            return;
        }
        
        if (userDAO.existsByEmail(email)) {
            resp.sendError(HttpServletResponse.SC_CONFLICT, "Email already registered");
            return;
        }
        
        try {
            var user = new com.volunteermanagement.model.User();
            user.setUsername(username);
            user.setEmail(email);
            user.setFullName(fullName);
            user.setRole(com.volunteermanagement.model.User.UserRole.VOLUNTEER);
            
            userDAO.create(user, password);
            logger.info("User registered successfully: {}", username);
            
            resp.sendRedirect(req.getContextPath() + "/auth/login?registered=true");
        } catch (Exception e) {
            logger.error("Registration failed for user: {}", username, e);
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Registration failed");
        }
    }

    private void handleLogout(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession(false);
        if (session != null) {
            logger.info("User logout: {}", session.getAttribute("username"));
            session.invalidate();
        }
        resp.sendRedirect(req.getContextPath() + "/");
    }
}
