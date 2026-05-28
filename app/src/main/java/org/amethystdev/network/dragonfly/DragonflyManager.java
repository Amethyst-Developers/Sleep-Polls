package org.amethystdev.network.dragonfly;

import org.amethystdev.Main;
import org.bukkit.configuration.file.FileConfiguration;

import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;

public final class DragonflyManager {

    private final Main plugin;

    private RedisClient client;

    private StatefulRedisConnection<String, String>
            connection;

    private RedisCommands<String, String>
            commands;

    private String channel;

    public DragonflyManager(
            Main plugin
    ) {

        this.plugin = plugin;
    }

    public void connect() {

        FileConfiguration config =
                plugin.getConfig();

        boolean enabled =
                config.getBoolean(
                        "dragonfly.enabled",
                        false
                );

        if (!enabled) {

            plugin.getLogger().info(
                    "Dragonfly disabled."
            );

            return;
        }

        try {

            String host =
                    config.getString(
                            "dragonfly.host",
                            "localhost"
                    );

            int port =
                    config.getInt(
                            "dragonfly.port",
                            6379
                    );

            String password =
                    config.getString(
                            "dragonfly.password",
                            ""
                    );

            this.channel =
                    config.getString(
                            "dragonfly.channel",
                            "sleeppolls"
                    );

            RedisURI.Builder builder =
                    RedisURI.builder()
                            .withHost(host)
                            .withPort(port);

            if (!password.isBlank()) {

                builder.withPassword(
                        password.toCharArray()
                );
            }

            RedisURI uri =
                    builder.build();

            this.client =
                    RedisClient.create(uri);

            this.connection =
                    client.connect();

            this.commands =
                    connection.sync();

            plugin.getLogger().info(
                    "Connected to DragonflyDB."
            );

        } catch (Exception e) {

            plugin.getLogger().severe(
                    "Failed to connect to DragonflyDB."
            );

            e.printStackTrace();
        }
    }

    public void disconnect() {

        try {

            if (connection != null) {

                connection.close();
            }

            if (client != null) {

                client.shutdown();
            }

            plugin.getLogger().info(
                    "Disconnected from DragonflyDB."
            );

        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    public void publish(
            String message
    ) {

        if (commands == null)
            return;

        commands.publish(
                channel,
                message
        );
    }

    public String getChannel() {

        return channel;
    }

    public RedisClient getClient() {

        return client;
    }
	
	public Main getPlugin() {

	return plugin;
	}
}