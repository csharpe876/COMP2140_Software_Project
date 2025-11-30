package com.volunteermanagement.util;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Thread-safe database connection pool manager using HikariCP.
 * Optimized for Java 21 with modern patterns and proper resource management.
 */
public final class DatabaseUtil {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseUtil.class);
    private static volatile HikariDataSource dataSource;
    private static final Lock initLock = new ReentrantLock();

    private DatabaseUtil() {
        throw new UnsupportedOperationException("Utility class");
    }

    /**
     * Initialize the connection pool with thread-safe double-checked locking.
     */
    public static void init(Properties props) {
        if (dataSource != null) return;
        
        initLock.lock();
        try {
            if (dataSource != null) return; // double-check
            
            validateProperties(props);
            
            var config = new HikariConfig();
            config.setJdbcUrl(props.getProperty("DB_URL"));
            config.setUsername(props.getProperty("DB_USER"));
            config.setPassword(props.getProperty("DB_PASSWORD"));
            config.setMaximumPoolSize(parseIntProperty(props, "DB_POOL_SIZE", 10));
            config.setMinimumIdle(parseIntProperty(props, "DB_POOL_MIN_IDLE", 2));
            config.setIdleTimeout(parseLongProperty(props, "DB_POOL_IDLE_TIMEOUT", 600000L));
            config.setMaxLifetime(parseLongProperty(props, "DB_POOL_MAX_LIFETIME", 1800000L));
            config.setConnectionTimeout(parseLongProperty(props, "DB_POOL_CONNECTION_TIMEOUT", 30000L));
            
            // Performance optimizations
            config.addDataSourceProperty("cachePrepStmts", "true");
            config.addDataSourceProperty("prepStmtCacheSize", "250");
            config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
            config.addDataSourceProperty("useServerPrepStmts", "true");
            
            dataSource = new HikariDataSource(config);
            logger.info("Database connection pool initialized successfully");
        } catch (Exception e) {
            logger.error("Failed to initialize database connection pool", e);
            throw new IllegalStateException("Database initialization failed", e);
        } finally {
            initLock.unlock();
        }
    }

    /**
     * Get a connection from the pool. Connection must be closed by caller.
     */
    public static Connection getConnection() throws SQLException {
        if (dataSource == null || dataSource.isClosed()) {
            throw new IllegalStateException("DataSource not initialized or already closed");
        }
        return dataSource.getConnection();
    }

    /**
     * Check if the pool is initialized and healthy.
     */
    public static boolean isInitialized() {
        return dataSource != null && !dataSource.isClosed();
    }

    /**
     * Shutdown the connection pool gracefully.
     */
    public static void shutdown() {
        if (dataSource != null && !dataSource.isClosed()) {
            logger.info("Shutting down database connection pool");
            dataSource.close();
        }
    }

    private static void validateProperties(Properties props) {
        if (props == null) throw new IllegalArgumentException("Properties cannot be null");
        
        var required = new String[]{"DB_URL", "DB_USER", "DB_PASSWORD"};
        for (String key : required) {
            if (props.getProperty(key) == null) {
                throw new IllegalArgumentException("Required property missing: " + key);
            }
        }
    }

    private static int parseIntProperty(Properties props, String key, int defaultValue) {
        String value = props.getProperty(key);
        if (value == null || value.isBlank()) return defaultValue;
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            logger.warn("Invalid integer for {}: {}, using default: {}", key, value, defaultValue);
            return defaultValue;
        }
    }

    private static long parseLongProperty(Properties props, String key, long defaultValue) {
        String value = props.getProperty(key);
        if (value == null || value.isBlank()) return defaultValue;
        try {
            return Long.parseLong(value.trim());
        } catch (NumberFormatException e) {
            logger.warn("Invalid long for {}: {}, using default: {}", key, value, defaultValue);
            return defaultValue;
        }
    }
}