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

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.amethystdev.commands.SleepPollsCommand;
import org.amethystdev.sleep.SleepListener;
import org.amethystdev.sleep.SleepPollManager;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import revxrsal.commands.bukkit.BukkitLamp;

public class Main extends JavaPlugin {
    private SleepPollManager pollManager;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        getLogger().info("Sleep-Polls enabling");
        this.pollManager = new SleepPollManager(this);
        getServer().getPluginManager().registerEvents(new SleepListener(this, pollManager), this);

        // Register command framework
        var lamp = BukkitLamp.builder(this).build();
        lamp.register(new SleepPollsCommand(this));
    }

    private final Set<UUID> bossBarDisabledPlayers = new HashSet<>();

    public boolean hasBossBarEnabled(Player player) {
    return !bossBarDisabledPlayers.contains(player.getUniqueId());
    }

    public void setBossBarEnabled(Player player, boolean enabled) {

    if (enabled) {

        bossBarDisabledPlayers.remove(player.getUniqueId());

    } else {

        bossBarDisabledPlayers.add(player.getUniqueId());
    }
    }

    public SleepPollManager getPollManager() {
        return pollManager;
    }

    public int getPollDurationSeconds() {
        return getConfig().getInt("poll-duration-seconds", 20);
    }

    public int getRequiredPercentage() {
        return getConfig().getInt("required-percentage", 50);
    }

    public boolean isBossBarEnabled() {
        return getConfig().getBoolean("bossbar.enabled", true);
    }

    public boolean areSoundsEnabled() {
        return getConfig().getBoolean("sounds.enabled", true);
    }

    public boolean shouldClearRain() {
        return getConfig().getBoolean("weather.clear-rain", true);
    }

    public boolean shouldClearThunder() {
        return getConfig().getBoolean("weather.clear-thunder", true);
    }

    public boolean isWorldBlocked(String worldName) {

    return getConfig()
            .getStringList("worlds.blacklist")
            .contains(worldName);
    }
    

}