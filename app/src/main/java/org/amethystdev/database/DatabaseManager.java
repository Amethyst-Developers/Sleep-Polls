package org.amethystdev.database;

import java.sql.Connection;

public interface DatabaseManager {

    void connect();

    void disconnect();

    Connection getConnection();

    boolean isConnected();
}