package com.volunteermanagement.listener;

import com.volunteermanagement.util.DatabaseUtil;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Application lifecycle listener for initializing and shutting down resources.
 * Initializes database connection pool on startup and cleans up on shutdown.
 */
@WebListener
public class AppContextListener implements ServletContextListener {
    private static final Logger logger = LoggerFactory.getLogger(AppContextListener.class);

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        logger.info("Application starting up...");
        
        try {
            // Load application properties
            Properties props = new Properties();
            try (InputStream input = getClass().getClassLoader().getResourceAsStream("application.properties")) {
                if (input == null) {
                    logger.error("Unable to find application.properties");
                    throw new RuntimeException("Configuration file not found");
                }
                props.load(input);
            }
            
            // Initialize database connection pool
            DatabaseUtil.init(props);
            logger.info("Database connection pool initialized successfully");
            
            // Store properties in servlet context for access by other components
            sce.getServletContext().setAttribute("appProperties", props);
            sce.getServletContext().setAttribute("appName", props.getProperty("APP_NAME", "Volunteer Management"));
            
            logger.info("Application initialized successfully");
            
        } catch (IOException e) {
            logger.error("Failed to load application properties", e);
            throw new RuntimeException("Application initialization failed", e);
        } catch (Exception e) {
            logger.error("Failed to initialize application", e);
            throw new RuntimeException("Application initialization failed", e);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        logger.info("Application shutting down...");
        
        try {
            // Shutdown database connection pool
            DatabaseUtil.shutdown();
            logger.info("Database connection pool shutdown successfully");
        } catch (Exception e) {
            logger.error("Error during application shutdown", e);
        }
        
        logger.info("Application shutdown complete");
    }
}
