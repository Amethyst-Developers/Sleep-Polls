package org.amethystdev.sleep;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.plugin.Plugin;

import java.util.HashSet;
import java.util.Set;

public class SleepListener implements Listener {
    private final SleepPollManager manager;

    public SleepListener(Plugin plugin, SleepPollManager manager) {
        this.manager = manager;
    }

    @EventHandler
    public void onPlayerBedEnter(PlayerBedEnterEvent event) {
        Player sleeper = event.getPlayer();
        World world = sleeper.getWorld();
        String worldName = world.getName();

        long time = world.getTime();
        if (time < 13000 || time > 23000) {
            sleeper.sendMessage(Component.text("[SleepPoll] ", Style.style(NamedTextColor.GOLD, TextDecoration.BOLD))
                    .append(Component.text("Sleep polls are only available during night time!", NamedTextColor.RED)));
            return;
        }

        if (manager.isPollActive(worldName)) {
            event.setCancelled(true);
            sleeper.sendMessage(Component.text("[SleepPoll] ", Style.style(NamedTextColor.GOLD, TextDecoration.BOLD))
                    .append(Component.text("A poll is already active! ", NamedTextColor.YELLOW))
                    .append(Component.text("Please vote with ", NamedTextColor.GRAY))
                    .append(Component.text("/sleeppoll yes ", NamedTextColor.GREEN))
                    .append(Component.text("or ", NamedTextColor.GRAY))
                    .append(Component.text("/sleeppoll no", NamedTextColor.RED))
                    .append(Component.text(".", NamedTextColor.GRAY)));
            return;
        }

        Set<Player> voters = new HashSet<>(world.getPlayers());

        event.setCancelled(true);
        manager.startPoll(worldName, voters);
    }

}
