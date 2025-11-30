package com.volunteermanagement.util;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseUtil {
    private static HikariDataSource dataSource;

    public static void init(Properties props) {
        if (dataSource != null) return;
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(props.getProperty("DB_URL"));
        config.setUsername(props.getProperty("DB_USER"));
        config.setPassword(props.getProperty("DB_PASSWORD"));
        config.setMaximumPoolSize(Integer.parseInt(props.getProperty("DB_POOL_SIZE", "10")));
        config.setIdleTimeout(Long.parseLong(props.getProperty("DB_POOL_IDLE_TIMEOUT", "600000")));
        config.setMaxLifetime(Long.parseLong(props.getProperty("DB_POOL_MAX_LIFETIME", "1800000")));
        config.setConnectionTimeout(Long.parseLong(props.getProperty("DB_POOL_CONNECTION_TIMEOUT", "30000")));
        dataSource = new HikariDataSource(config);
    }

    public static Connection getConnection() throws SQLException {
        if (dataSource == null) throw new IllegalStateException("DataSource not initialized");
        return dataSource.getConnection();
    }

    public static void close() {
        if (dataSource != null) {
            dataSource.close();
        }
    }
}