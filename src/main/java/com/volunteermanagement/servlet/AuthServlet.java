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

    private void showLoginPage(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("text/html;charset=UTF-8");
        String contextPath = req.getContextPath();
        
        resp.getWriter().write("""
            <!DOCTYPE html>
            <html lang="en">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>Login - Volunteer Management</title>
                <style>
                    body { font-family: system-ui, Arial, sans-serif; max-width: 400px; margin: 50px auto; padding: 20px; }
                    input { width: 100%%; padding: 8px; margin: 8px 0; box-sizing: border-box; }
                    button { width: 100%%; padding: 10px; background: #0366d6; color: white; border: none; cursor: pointer; }
                    button:hover { background: #0256b7; }
                    .error { color: #b00020; padding: 10px; background: #fdecea; margin: 10px 0; }
                </style>
            </head>
            <body>
                <h2>Login</h2>
                <form method="post" action="%s/auth/login">
                    <input type="text" name="username" placeholder="Username" required>
                    <input type="password" name="password" placeholder="Password" required>
                    <button type="submit">Login</button>
                </form>
                <p><a href="%s/auth/register">Create new account</a></p>
                <p><a href="%s/">Back to Home</a></p>
            </body>
            </html>
            """.formatted(contextPath, contextPath, contextPath));
    }

    private void showRegisterPage(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("text/html;charset=UTF-8");
        String contextPath = req.getContextPath();
        
        resp.getWriter().write("""
            <!DOCTYPE html>
            <html lang="en">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>Register - Volunteer Management</title>
                <style>
                    body { font-family: system-ui, Arial, sans-serif; max-width: 400px; margin: 50px auto; padding: 20px; }
                    input { width: 100%%; padding: 8px; margin: 8px 0; box-sizing: border-box; }
                    button { width: 100%%; padding: 10px; background: #0366d6; color: white; border: none; cursor: pointer; }
                    button:hover { background: #0256b7; }
                </style>
            </head>
            <body>
                <h2>Register</h2>
                <form method="post" action="%s/auth/register">
                    <input type="text" name="username" placeholder="Username" required>
                    <input type="email" name="email" placeholder="Email" required>
                    <input type="password" name="password" placeholder="Password" required>
                    <input type="text" name="fullName" placeholder="Full Name" required>
                    <button type="submit">Register</button>
                </form>
                <p><a href="%s/auth/login">Already have an account?</a></p>
                <p><a href="%s/">Back to Home</a></p>
            </body>
            </html>
            """.formatted(contextPath, contextPath, contextPath));
    }

    private void handleLogin(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String username = ValidationUtil.cleanInput(req.getParameter("username"));
        String password = req.getParameter("password");

        if (ValidationUtil.isEmpty(username) || ValidationUtil.isEmpty(password)) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Username and password required");
            return;
        }

        // TODO: Implement actual authentication logic with database
        logger.info("Login attempt for user: {}", username);
        
        // Placeholder: create session
        HttpSession session = req.getSession(true);
        session.setAttribute("username", username);
        session.setAttribute("userId", 1); // TODO: get from database
        session.setMaxInactiveInterval(30 * 60); // 30 minutes

        resp.sendRedirect(req.getContextPath() + "/");
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

        // TODO: Implement actual registration logic with database
        logger.info("Registration attempt for user: {}", username);

        resp.sendRedirect(req.getContextPath() + "/auth/login");
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
