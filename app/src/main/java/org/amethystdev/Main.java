/*
 * This file is part of Sleep-Polls - https://github.com/Amethyst-Developers/Sleep-Polls
 * Copyright (C) 2026  Monk (Monish), The Amethyst Team and contributors
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.amethystdev;

import org.amethystdev.commands.SleepPollsCommand;
import org.amethystdev.database.DatabaseExecutor;
import org.amethystdev.database.DatabaseInitializer;
import org.amethystdev.database.DatabaseManager;
import org.amethystdev.database.MariaDBDatabaseManager;
import org.amethystdev.database.SQLiteDatabaseManager;
import org.amethystdev.model.PlayerData;
import org.amethystdev.network.dragonfly.DragonflyManager;
import org.amethystdev.network.dragonfly.DragonflyPacketBridge;
import org.amethystdev.network.dragonfly.DragonflySubscriber;
import org.amethystdev.network.packet.PacketType;
import org.amethystdev.network.packet.handler.PacketHandlerRegistry;
import org.amethystdev.network.packet.handler.PollStartPacketHandler;
import org.amethystdev.network.packet.handler.PollVotePacketHandler;
import org.amethystdev.repository.PlayerDataRepository;
import org.amethystdev.sleep.SleepListener;
import org.amethystdev.sleep.SleepPollManager;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import revxrsal.commands.bukkit.BukkitLamp;

public final class Main extends JavaPlugin {

    private SleepPollManager pollManager;

    private DatabaseManager databaseManager;

    private PlayerDataRepository playerDataRepository;

    /*
     * Dragonfly
     */
    private DragonflyManager dragonflyManager;

    private DragonflySubscriber dragonflySubscriber;

    /*
     * Packet handlers
     */
    private PacketHandlerRegistry
            packetHandlerRegistry;

    @Override
    public void onEnable() {

        saveDefaultConfig();

        getLogger().info(
                "Sleep-Polls enabling..."
        );

        /*
         * Database selection
         */
        String databaseType =
                getConfig().getString(
                        "database.type",
                        "SQLITE"
                );

        if (databaseType.equalsIgnoreCase(
                "MARIADB"
        )) {

            this.databaseManager =
                    new MariaDBDatabaseManager(this);

            getLogger().info(
                    "Using MariaDB database backend."
            );

        } else {

            this.databaseManager =
                    new SQLiteDatabaseManager(this);

            getLogger().info(
                    "Using SQLite database backend."
            );
        }

        /*
         * Connect database
         */
        databaseManager.connect();

        /*
         * Repository
         */
        this.playerDataRepository =
                new PlayerDataRepository(
                        databaseManager
                );

        /*
         * Initialize tables
         */
        new DatabaseInitializer(
                databaseManager
        ).initialize();

        /*
         * Poll manager
         */
        this.pollManager =
                new SleepPollManager(this);

        /*
         * Packet registry
         */
        this.packetHandlerRegistry =
                new PacketHandlerRegistry();

        packetHandlerRegistry.register(
                PacketType.POLL_START,
                new PollStartPacketHandler(this)
        );

        packetHandlerRegistry.register(
                PacketType.POLL_VOTE,
                new PollVotePacketHandler(this)
        );

        /*
         * Dragonfly
         */
        this.dragonflyManager =
                new DragonflyManager(this);

        dragonflyManager.connect();

        this.dragonflySubscriber =
                new DragonflySubscriber(
                        this,
                        dragonflyManager,
                        packetHandlerRegistry
                );

        dragonflySubscriber.subscribe();

        DragonflyPacketBridge.initialize(
                dragonflyManager
        );

        /*
         * Events
         */
        getServer()
                .getPluginManager()
                .registerEvents(
                        new SleepListener(
                                this,
                                pollManager
                        ),
                        this
                );

        /*
         * Commands
         */
        var lamp =
                BukkitLamp.builder(this)
                        .build();

        lamp.register(
                new SleepPollsCommand(this)
        );

        getLogger().info(
                "Sleep-Polls enabled successfully."
        );
    }

    @Override
    public void onDisable() {

        DatabaseExecutor.shutdown();

        /*
         * Dragonfly shutdown
         */
        if (dragonflySubscriber != null) {

            dragonflySubscriber.disconnect();
        }

        if (dragonflyManager != null) {

            dragonflyManager.disconnect();
        }

        /*
         * Database shutdown
         */
        if (databaseManager != null) {

            databaseManager.disconnect();
        }

        getLogger().info(
                "Sleep-Polls disabled."
        );
    }

    public SleepPollManager getPollManager() {

        return pollManager;
    }

    public DatabaseManager getDatabaseManager() {

        return databaseManager;
    }

    public PlayerDataRepository
    getPlayerDataRepository() {

        return playerDataRepository;
    }

    public DragonflyManager
    getDragonflyManager() {

        return dragonflyManager;
    }

    public PacketHandlerRegistry
    getPacketHandlerRegistry() {

        return packetHandlerRegistry;
    }

    public boolean hasBossBarEnabled(
            Player player
    ) {

        PlayerData data =
                playerDataRepository
                        .getOrCreatePlayer(
                                player.getUniqueId()
                        );

        return data != null
                && data.isBossBarEnabled();
    }

    public void setBossBarEnabled(
            Player player,
            boolean enabled
    ) {

        PlayerData data =
                playerDataRepository
                        .getOrCreatePlayer(
                                player.getUniqueId()
                        );

        if (data == null) {
            return;
        }

        data.setBossBarEnabled(enabled);

        playerDataRepository.savePlayer(data);
    }

    public int getPollDurationSeconds() {

        return getConfig().getInt(
                "poll-duration-seconds",
                20
        );
    }

    public int getRequiredPercentage() {

        return getConfig().getInt(
                "required-percentage",
                50
        );
    }

    public boolean isBossBarEnabled() {

        return getConfig().getBoolean(
                "bossbar.enabled",
                true
        );
    }

    public boolean areSoundsEnabled() {

        return getConfig().getBoolean(
                "sounds.enabled",
                true
        );
    }

    public boolean shouldClearRain() {

        return getConfig().getBoolean(
                "weather.clear-rain",
                true
        );
    }

    public boolean shouldClearThunder() {

        return getConfig().getBoolean(
                "weather.clear-thunder",
                true
        );
    }

    public boolean isWorldBlocked(
            String worldName
    ) {

        return getConfig()
                .getStringList(
                        "worlds.blacklist"
                )
                .contains(worldName);
    }

    public void reloadPlugin() {

        getLogger().info(
                "Reloading Sleep-Polls..."
        );

        /*
         * Clear caches
         */
        if (playerDataRepository != null) {

            playerDataRepository.clearCache();
        }

        /*
         * Disconnect Dragonfly
         */
        if (dragonflySubscriber != null) {

            dragonflySubscriber.disconnect();
        }

        if (dragonflyManager != null) {

            dragonflyManager.disconnect();
        }

        /*
         * Disconnect database
         */
        if (databaseManager != null) {

            databaseManager.disconnect();
        }

        /*
         * Reload config
         */
        reloadConfig();

        /*
         * Recreate database manager
         */
        String databaseType =
                getConfig().getString(
                        "database.type",
                        "SQLITE"
                );

        if (databaseType.equalsIgnoreCase(
                "MARIADB"
        )) {

            this.databaseManager =
                    new MariaDBDatabaseManager(this);

        } else {

            this.databaseManager =
                    new SQLiteDatabaseManager(this);
        }

        /*
         * Reconnect database
         */
        databaseManager.connect();

        /*
         * Recreate repository
         */
        this.playerDataRepository =
                new PlayerDataRepository(
                        databaseManager
                );

        /*
         * Run migrations
         */
        new DatabaseInitializer(
                databaseManager
        ).initialize();

        /*
         * Recreate packet registry
         */
        this.packetHandlerRegistry =
                new PacketHandlerRegistry();

        packetHandlerRegistry.register(
                PacketType.POLL_START,
                new PollStartPacketHandler(this)
        );

        packetHandlerRegistry.register(
                PacketType.POLL_VOTE,
                new PollVotePacketHandler(this)
        );

        /*
         * Reconnect Dragonfly
         */
        this.dragonflyManager =
                new DragonflyManager(this);

        dragonflyManager.connect();

        this.dragonflySubscriber =
                new DragonflySubscriber(
                        this,
                        dragonflyManager,
                        packetHandlerRegistry
                );

        dragonflySubscriber.subscribe();

        DragonflyPacketBridge.initialize(
                dragonflyManager
        );

        getLogger().info(
                "Sleep-Polls reload complete."
        );
    }
}