package org.amethystdev;

import org.amethystdev.commands.SleepPollsCommand;
import org.amethystdev.sleep.SleepListener;
import org.amethystdev.sleep.SleepPollManager;
import org.bukkit.plugin.java.JavaPlugin;
import revxrsal.commands.bukkit.BukkitLamp;

public class Main extends JavaPlugin {
    private SleepPollManager pollManager;

    @Override
    public void onEnable() {
        getLogger().info("Sleep-Polls enabling");
        this.pollManager = new SleepPollManager(this);
        getServer().getPluginManager().registerEvents(new SleepListener(this, pollManager), this);

        // Register command framework
        var lamp = BukkitLamp.builder(this).build();
        lamp.register(new SleepPollsCommand(this));
    }

    public SleepPollManager getPollManager() {
        return pollManager;
    }
}