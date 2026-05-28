package org.amethystdev.database;

import java.sql.Connection;
import java.sql.SQLException;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public final class MariaDBDatabaseManager
        implements DatabaseManager {

    private final Plugin plugin;

    private HikariDataSource dataSource;

    public MariaDBDatabaseManager(
            Plugin plugin
    ) {

        this.plugin = plugin;
    }

    @Override
    public synchronized void connect() {

        if (isConnected()) {
            return;
        }

        FileConfiguration config =
                plugin.getConfig();

        String host =
                config.getString(
                        "database.mariadb.host",
                        "localhost"
                );

        int port =
                config.getInt(
                        "database.mariadb.port",
                        3306
                );

        String database =
                config.getString(
                        "database.mariadb.database",
                        "sleeppolls"
                );

        String username =
                config.getString(
                        "database.mariadb.username",
                        "root"
                );

        String password =
                config.getString(
                        "database.mariadb.password",
                        "password"
                );

        int poolSize =
                config.getInt(
                        "database.mariadb.pool-size",
                        10
                );

        String jdbcUrl =
                "jdbc:mariadb://"
                        + host
                        + ":"
                        + port
                        + "/"
                        + database;

        try {

            HikariConfig hikariConfig =
                    new HikariConfig();

            hikariConfig.setJdbcUrl(
                    jdbcUrl
            );

            hikariConfig.setUsername(
                    username
            );

            hikariConfig.setPassword(
                    password
            );

            hikariConfig.setMaximumPoolSize(
                    poolSize
            );

            hikariConfig.setMinimumIdle(
                    2
            );

            hikariConfig.setPoolName(
                    "SleepPolls-Hikari"
            );

            hikariConfig.setAutoCommit(
                    true
            );

            hikariConfig.setConnectionTimeout(
                    10000
            );

            hikariConfig.setValidationTimeout(
                    5000
            );

            hikariConfig.setIdleTimeout(
                    600000
            );

            hikariConfig.setMaxLifetime(
                    1800000
            );

            hikariConfig.setKeepaliveTime(
                    30000
            );

            hikariConfig.setLeakDetectionThreshold(
                    15000
            );

            hikariConfig.setConnectionTestQuery(
                    "SELECT 1"
            );

            hikariConfig.setDriverClassName(
                    "org.mariadb.jdbc.Driver"
            );

            this.dataSource =
                    new HikariDataSource(
                            hikariConfig
                    );

            plugin.getLogger().info(
                    "Connected to MariaDB database."
            );

        } catch (Exception e) {

            plugin.getLogger().severe(
                    "Failed to connect to MariaDB."
            );

            e.printStackTrace();
        }
    }

    @Override
    public synchronized void disconnect() {

        if (dataSource == null) {
            return;
        }

        try {

            dataSource.close();

        } catch (Exception e) {

            e.printStackTrace();
        }

        dataSource = null;

        plugin.getLogger().info(
                "Disconnected from MariaDB."
        );
    }

    @Override
    public Connection getConnection() {

        try {

            if (!isConnected()) {

                connect();
            }

            Connection connection =
                    dataSource.getConnection();

            if (!connection.isValid(5)) {

                connection.close();

                disconnect();

                connect();

                return dataSource.getConnection();
            }

            return connection;

        } catch (SQLException e) {

            plugin.getLogger().severe(
                    "Failed to retrieve MariaDB connection."
            );

            e.printStackTrace();
        }

        return null;
    }

    @Override
    public boolean isConnected() {

        return dataSource != null
                && !dataSource.isClosed();
    }
}