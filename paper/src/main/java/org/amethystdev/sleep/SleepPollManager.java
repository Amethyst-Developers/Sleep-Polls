package org.amethystdev.sleep;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class SleepPollManager {
    private final Plugin plugin;
    private final Map<String, ActivePoll> polls = new HashMap<>();

    public SleepPollManager(Plugin plugin) {
        this.plugin = plugin;
    }

    public boolean isPollActive(String worldName) {
        ActivePoll p = polls.get(worldName);
        return p != null && !p.finished;
    }

    public ActivePoll startPoll(String worldName, Set<Player> voters) {
        ActivePoll p = new ActivePoll(worldName, voters);
        polls.put(worldName, p);
        p.start();
        return p;
    }

    public ActivePoll getPoll(String worldName) {
        return polls.get(worldName);
    }

    public class ActivePoll {
        private final String world;
        private final Set<UUID> eligible = new HashSet<>();
        private final Set<UUID> yes = new HashSet<>();
        private final Set<UUID> no = new HashSet<>();
        private boolean finished = false;
        private BukkitTask timeoutTask;

        public ActivePoll(String world, Set<Player> voters) {
            this.world = world;
            for (Player p : voters)
                eligible.add(p.getUniqueId());
        }

        public void start() {
            // broadcast poll message
            Bukkit.getScheduler().runTask(plugin, () -> {
                for (UUID id : eligible) {
                    Player p = Bukkit.getPlayer(id);
                    if (p == null)
                        continue;
                    p.sendMessage(Component.text("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━",
                            Style.style(NamedTextColor.DARK_GRAY, TextDecoration.STRIKETHROUGH)));
                    p.sendMessage(Component.text("[SleepPoll] ", Style.style(NamedTextColor.GOLD, TextDecoration.BOLD))
                            .append(Component.text("A player is sleeping!", NamedTextColor.YELLOW)));
                    p.sendMessage(Component.text("Type ", NamedTextColor.GRAY)
                            .append(Component.text("/sleeppoll yes ", NamedTextColor.GREEN))
                            .append(Component.text("or ", NamedTextColor.GRAY))
                            .append(Component.text("/sleeppoll no ", NamedTextColor.RED))
                            .append(Component.text("within ", NamedTextColor.GRAY))
                            .append(Component.text("20 seconds", NamedTextColor.AQUA))
                            .append(Component.text(".", NamedTextColor.GRAY)));
                    p.sendMessage(Component.text("Votes needed: ", NamedTextColor.GRAY)
                            .append(Component.text(String.valueOf((eligible.size() + 1) / 2), NamedTextColor.AQUA))
                            .append(Component.text("/", NamedTextColor.GRAY))
                            .append(Component.text(String.valueOf(eligible.size()), NamedTextColor.AQUA)));
                    p.sendMessage(Component.text("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━",
                            Style.style(NamedTextColor.DARK_GRAY, TextDecoration.STRIKETHROUGH)));
                    p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1f);
                }
            });

            timeoutTask = Bukkit.getScheduler().runTaskLater(plugin, () -> finish(), 20L * 20); // 20 seconds
        }

        public synchronized void vote(Player p, boolean accept) {
            if (finished)
                return;

            if (!eligible.contains(p.getUniqueId()))
                return;
            
                yes.remove(p.getUniqueId());
            no.remove(p.getUniqueId());

            if (accept)
                yes.add(p.getUniqueId());
            else
                no.add(p.getUniqueId());

            p.sendMessage(Component.text("[SleepPoll] ", Style.style(NamedTextColor.GOLD, TextDecoration.BOLD))
                    .append(Component.text("Vote recorded: ", NamedTextColor.GRAY))
                    .append(Component.text(accept ? "YES" : "NO",
                            Style.style(accept ? NamedTextColor.GREEN : NamedTextColor.RED, TextDecoration.BOLD)))
                    .append(Component.text(" (", NamedTextColor.GRAY))
                    .append(Component.text(String.valueOf(yes.size()), NamedTextColor.AQUA))
                    .append(Component.text("/", NamedTextColor.GRAY))
                    .append(Component.text(String.valueOf(eligible.size()), NamedTextColor.AQUA))
                    .append(Component.text(")", NamedTextColor.GRAY)));

            int totalEligible = eligible.size();
            int yesCount = yes.size();
            int noCount = no.size();

            int needed = (totalEligible + 1) / 2; // ceil half
            if (yesCount >= needed) {
                finish();
                return;
            }

            int remaining = totalEligible - (yesCount + noCount);
            if (yesCount + remaining < needed) {
                finish();
            }
        }

        public synchronized void finish() {
            if (finished)
                return;

            finished = true;

            if (timeoutTask != null)
                timeoutTask.cancel();

            int totalEligible = eligible.size();
            int yesCount = yes.size();
            int needed = (totalEligible + 1) / 2;
            boolean succeeded = yesCount >= needed;

            Bukkit.getScheduler().runTask(plugin, () -> {
                Component msg = succeeded
                        ? Component
                                .text("Sleep poll succeeded! ", Style.style(NamedTextColor.GREEN, TextDecoration.BOLD))
                                .append(Component.text("Skipping to day...", NamedTextColor.GRAY))
                        : Component.text("Sleep poll ended. ", Style.style(NamedTextColor.RED, TextDecoration.BOLD))
                                .append(Component.text("Not enough yes votes. Night will not be skipped.",
                                        NamedTextColor.GRAY));
                for (UUID id : eligible) {
                    Player p = Bukkit.getPlayer(id);
                    if (p == null)
                        continue;
                    p.sendMessage(Component.text("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━",
                            Style.style(NamedTextColor.DARK_GRAY, TextDecoration.STRIKETHROUGH)));
                    p.sendMessage(Component.text("[SleepPoll] ", Style.style(NamedTextColor.GOLD, TextDecoration.BOLD))
                            .append(msg));
                    p.sendMessage(Component.text("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━",
                            Style.style(NamedTextColor.DARK_GRAY, TextDecoration.STRIKETHROUGH)));
                }
                if (succeeded) {
                    if (Bukkit.getWorld(world) != null)
                        Bukkit.getWorld(world).setTime(1000L);
                }

                polls.remove(world);
            });
        }
    }
}
