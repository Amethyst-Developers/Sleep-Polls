package org.amethystdev.network.dragonfly;

import io.lettuce.core.RedisClient;

import io.lettuce.core.pubsub.StatefulRedisPubSubConnection;

import io.lettuce.core.pubsub.RedisPubSubAdapter;

import io.lettuce.core.pubsub.api.sync.RedisPubSubCommands;

import org.amethystdev.Main;

import org.amethystdev.network.packet.PacketSerializer;
import org.amethystdev.network.packet.PollPacket;

import org.amethystdev.network.packet.handler.PacketHandlerRegistry;

public final class DragonflySubscriber {

    private final Main plugin;

    private final DragonflyManager dragonfly;

    private final PacketHandlerRegistry registry;

    private StatefulRedisPubSubConnection<String, String>
            pubSubConnection;

    public DragonflySubscriber(
            Main plugin,
            DragonflyManager dragonfly,
            PacketHandlerRegistry registry
    ) {

        this.plugin = plugin;

        this.dragonfly = dragonfly;

        this.registry = registry;
    }

    public void subscribe() {

        try {

            RedisClient client =
                    dragonfly.getClient();

            if (client == null) {
                return;
            }

            this.pubSubConnection =
                    client.connectPubSub();

            pubSubConnection.addListener(
                    new RedisPubSubAdapter<>() {

                        @Override
                        public void message(
                                String channel,
                                String message
                        ) {

                            try {

                                plugin.getLogger().info(
                                        "[Dragonfly] Received packet: "
                                                + message
                                );

                                PollPacket packet =
                                        PacketSerializer.fromJson(
                                                message
                                        );

                                /*
                                 * Ignore packets
                                 * from this server
                                 */
                                String serverId =
                                        plugin.getConfig()
                                                .getString(
                                                        "server-id",
                                                        "server-1"
                                                );

                                if (packet.getOrigin()
                                        .equalsIgnoreCase(
                                                serverId
                                        )) {

                                    return;
                                }

                                /*
                                 * Handle packet
                                 */
                                registry.handle(packet);

                            } catch (Exception e) {

                                plugin.getLogger().severe(
                                        "Failed to process Dragonfly packet."
                                );

                                e.printStackTrace();
                            }
                        }
                    }
            );

            RedisPubSubCommands<String, String>
                    sync =
                    pubSubConnection.sync();

            sync.subscribe(
                    dragonfly.getChannel()
            );

            plugin.getLogger().info(
                    "Subscribed to Dragonfly channel: "
                            + dragonfly.getChannel()
            );

        } catch (Exception e) {

            plugin.getLogger().severe(
                    "Failed to subscribe to Dragonfly."
            );

            e.printStackTrace();
        }
    }

    public void disconnect() {

        try {

            if (pubSubConnection != null) {

                pubSubConnection.close();
            }

            plugin.getLogger().info(
                    "Disconnected Dragonfly subscriber."
            );

        } catch (Exception e) {

            e.printStackTrace();
        }
    }
}