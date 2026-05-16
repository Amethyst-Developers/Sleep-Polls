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

package org.amethystdev.sleep;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.amethystdev.Main;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextDecoration;

public class SleepPollManager {

    private final Plugin plugin;

    private final Map<String, ActivePoll> polls =
            new HashMap<>();

    public SleepPollManager(Plugin plugin) {
        this.plugin = plugin;
    }

    public boolean isPollActive(String worldName) {

        ActivePoll p = polls.get(worldName);

        return p != null && !p.finished;
    }

    public ActivePoll startPoll(
            String worldName,
            Set<Player> voters
    ) {

        ActivePoll p = new ActivePoll(
                worldName,
                voters
        );

        polls.put(worldName, p);

        p.start();

        return p;
    }

    public ActivePoll getPoll(String worldName) {
        return polls.get(worldName);
    }

    public class ActivePoll {

        private final String world;

        private final Set<UUID> eligible =
                new HashSet<>();

        private final Set<UUID> yes =
                new HashSet<>();

        private final Set<UUID> no =
                new HashSet<>();

        private boolean finished = false;

        private BukkitTask countdownTask;

        private BossBar bossBar;

        private int remainingSeconds;

        public ActivePoll(
                String world,
                Set<Player> voters
        ) {

            this.world = world;

            this.remainingSeconds =
                    ((Main) plugin)
                            .getPollDurationSeconds();

            for (Player p : voters) {
                eligible.add(p.getUniqueId());
            }
        }

        public void start() {

            Bukkit.getScheduler().runTask(plugin, () -> {

                int needed = getNeededVotes();

                for (UUID id : eligible) {

                    Player p = Bukkit.getPlayer(id);

                    if (p == null)
                        continue;

                    Component yesButton =
                            Component.text(
                                    "[✔ YES]",
                                    Style.style(
                                            NamedTextColor.GREEN,
                                            TextDecoration.BOLD
                                    )
                            )
                            .clickEvent(
                                    ClickEvent.runCommand("/sp yes")
                            )
                            .hoverEvent(
                                    HoverEvent.showText(
                                            Component.text(
                                                    "Vote YES",
                                                    NamedTextColor.GREEN
                                            )
                                    )
                            );

                    Component noButton =
                            Component.text(
                                    "[✖ NO]",
                                    Style.style(
                                            NamedTextColor.RED,
                                            TextDecoration.BOLD
                                    )
                            )
                            .clickEvent(
                                    ClickEvent.runCommand("/sp no")
                            )
                            .hoverEvent(
                                    HoverEvent.showText(
                                            Component.text(
                                                    "Vote NO",
                                                    NamedTextColor.RED
                                            )
                                    )
                            );

                    p.sendMessage(
                            Component.text(
                                    "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━",
                                    Style.style(
                                            NamedTextColor.DARK_GRAY,
                                            TextDecoration.STRIKETHROUGH
                                    )
                            )
                    );

                    p.sendMessage(
                            Component.text(
                                    "[SleepPoll] ",
                                    Style.style(
                                            NamedTextColor.GOLD,
                                            TextDecoration.BOLD
                                    )
                            ).append(
                                    Component.text(
                                            "A player is sleeping!",
                                            NamedTextColor.YELLOW
                                    )
                            )
                    );

                    p.sendMessage(
                            Component.text(
                                    "Vote within ",
                                    NamedTextColor.GRAY
                            ).append(
                                    Component.text(
                                            remainingSeconds + " seconds",
                                            NamedTextColor.AQUA
                                    )
                            ).append(
                                    Component.text(
                                            ": ",
                                            NamedTextColor.GRAY
                                    )
                            ).append(yesButton)
                            .append(
                                    Component.text(
                                            "   ",
                                            NamedTextColor.DARK_GRAY
                                    )
                            )
                            .append(noButton)
                    );

                    p.sendMessage(
                            Component.text(
                                    "Votes needed: ",
                                    NamedTextColor.GRAY
                            ).append(
                                    Component.text(
                                            String.valueOf(needed),
                                            NamedTextColor.AQUA
                                    )
                            ).append(
                                    Component.text(
                                            "/",
                                            NamedTextColor.GRAY
                                    )
                            ).append(
                                    Component.text(
                                            String.valueOf(
                                                    eligible.size()
                                            ),
                                            NamedTextColor.AQUA
                                    )
                            )
                    );

                    p.sendMessage(
                            Component.text(
                                    "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━",
                                    Style.style(
                                            NamedTextColor.DARK_GRAY,
                                            TextDecoration.STRIKETHROUGH
                                    )
                            )
                    );

                    if (((Main) plugin)
                            .areSoundsEnabled()) {

                        p.playSound(
                                p.getLocation(),
                                Sound.ENTITY_EXPERIENCE_ORB_PICKUP,
                                1f,
                                1f
                        );
                    }
                }

                if (((Main) plugin)
                        .isBossBarEnabled()) {

                    bossBar = Bukkit.createBossBar(
                            "§6🌙 Sleep Poll",
                            BarColor.GREEN,
                            BarStyle.SOLID
                    );

                    for (UUID id : eligible) {

                        Player p = Bukkit.getPlayer(id);

                        if (p == null)
                            continue;

                        if (((Main) plugin)
                                .hasBossBarEnabled(p)) {

                            bossBar.addPlayer(p);
                        }
                    }

                    updateBossBar();
                }

                updateActionBars();
            });

            countdownTask =
                    Bukkit.getScheduler().runTaskTimer(
                            plugin,
                            () -> {

                                if (finished)
                                    return;

                                remainingSeconds--;

                                updateBossBar();

                                updateActionBars();

                                if (remainingSeconds <= 5
                                        && remainingSeconds > 0) {

                                    for (UUID id : eligible) {

                                        Player p = Bukkit.getPlayer(id);

                                        if (p == null)
                                            continue;

                                        if (((Main) plugin)
                                                .areSoundsEnabled()) {

                                            p.playSound(
                                                    p.getLocation(),
                                                    Sound.BLOCK_NOTE_BLOCK_HAT,
                                                    1f,
                                                    1.8f
                                            );
                                        }
                                    }
                                }

                                if (remainingSeconds <= 0) {
                                    finish();
                                }

                            },
                            20L,
                            20L
                    );
        }

        private int getNeededVotes() {

            return (int) Math.ceil(
                    eligible.size()
                            * (((Main) plugin)
                            .getRequiredPercentage() / 100.0)
            );
        }

        private void updateBossBar() {

            if (bossBar == null)
                return;

            int needed = getNeededVotes();

            bossBar.setTitle(
                    "§6🌙 Sleep Poll §8• §e"
                            + remainingSeconds
                            + "s §7remaining §8| §aYES "
                            + yes.size()
                            + "/"
                            + needed
            );

            double maxDuration =
                    ((Main) plugin)
                            .getPollDurationSeconds();

            double progress =
                    remainingSeconds / maxDuration;

            bossBar.setProgress(
                    Math.max(
                            0.0,
                            Math.min(1.0, progress)
                    )
            );

            if (remainingSeconds <= 5) {

                bossBar.setColor(BarColor.RED);

            } else if (remainingSeconds <= 10) {

                bossBar.setColor(BarColor.YELLOW);

            } else {

                bossBar.setColor(BarColor.GREEN);
            }
        }

        private void updateActionBars() {

            int needed = getNeededVotes();

            Component actionBar =
                    Component.text(
                            "🌙 Sleep Poll ",
                            NamedTextColor.GOLD
                    ).append(
                            Component.text(
                                    "• ",
                                    NamedTextColor.DARK_GRAY
                            )
                    ).append(
                            Component.text(
                                    "YES ",
                                    NamedTextColor.GREEN
                            )
                    ).append(
                            Component.text(
                                    yes.size() + "/" + needed,
                                    NamedTextColor.AQUA
                            )
                    ).append(
                            Component.text(
                                    " • ",
                                    NamedTextColor.DARK_GRAY
                            )
                    ).append(
                            Component.text(
                                    remainingSeconds + "s",
                                    remainingSeconds <= 5
                                            ? NamedTextColor.RED
                                            : NamedTextColor.YELLOW
                            )
                    );

            for (UUID id : eligible) {

                Player p = Bukkit.getPlayer(id);

                if (p == null)
                    continue;

                p.sendActionBar(actionBar);
            }
        }

        public synchronized void vote(
                Player p,
                boolean accept
        ) {

            if (finished)
                return;

            if (!eligible.contains(
                    p.getUniqueId()
            ))
                return;

            yes.remove(p.getUniqueId());

            no.remove(p.getUniqueId());

            if (accept) {

                yes.add(p.getUniqueId());

            } else {

                no.add(p.getUniqueId());
            }

            updateBossBar();

            updateActionBars();

            p.sendMessage(
                    Component.text(
                            "[SleepPoll] ",
                            Style.style(
                                    NamedTextColor.GOLD,
                                    TextDecoration.BOLD
                            )
                    ).append(
                            Component.text(
                                    "Vote recorded: ",
                                    NamedTextColor.GRAY
                            )
                    ).append(
                            Component.text(
                                    accept ? "YES" : "NO",
                                    Style.style(
                                            accept
                                                    ? NamedTextColor.GREEN
                                                    : NamedTextColor.RED,
                                            TextDecoration.BOLD
                                    )
                            )
                    )
            );

            int totalEligible =
                    eligible.size();

            int yesCount =
                    yes.size();

            int noCount =
                    no.size();

            int needed =
                    getNeededVotes();

            if (yesCount >= needed) {

                finish();

                return;
            }

            int remaining =
                    totalEligible - (yesCount + noCount);

            if (yesCount + remaining < needed) {
                finish();
            }
        }

        public synchronized void finish() {

            if (finished)
                return;

            finished = true;

            if (countdownTask != null) {
                countdownTask.cancel();
            }

            if (bossBar != null) {
                bossBar.removeAll();
            }

            int yesCount =
                    yes.size();

            int needed =
                    getNeededVotes();

            boolean succeeded =
                    yesCount >= needed;

            Bukkit.getScheduler().runTask(plugin, () -> {

                Component msg =
                        succeeded
                                ? Component.text(
                                "Sleep poll succeeded! ",
                                Style.style(
                                        NamedTextColor.GREEN,
                                        TextDecoration.BOLD
                                )
                        ).append(
                                Component.text(
                                        "Skipping to day...",
                                        NamedTextColor.GRAY
                                )
                        )
                                : Component.text(
                                "Sleep poll ended. ",
                                Style.style(
                                        NamedTextColor.RED,
                                        TextDecoration.BOLD
                                )
                        ).append(
                                Component.text(
                                        "Not enough yes votes.",
                                        NamedTextColor.GRAY
                                )
                        );

                for (UUID id : eligible) {

                    Player p = Bukkit.getPlayer(id);

                    if (p == null)
                        continue;

                    p.sendMessage(
                            Component.text(
                                    "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━",
                                    Style.style(
                                            NamedTextColor.DARK_GRAY,
                                            TextDecoration.STRIKETHROUGH
                                    )
                            )
                    );

                    p.sendMessage(
                            Component.text(
                                    "[SleepPoll] ",
                                    Style.style(
                                            NamedTextColor.GOLD,
                                            TextDecoration.BOLD
                                    )
                            ).append(msg)
                    );

                    p.sendMessage(
                            Component.text(
                                    "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━",
                                    Style.style(
                                            NamedTextColor.DARK_GRAY,
                                            TextDecoration.STRIKETHROUGH
                                    )
                            )
                    );

                    if (((Main) plugin)
                            .areSoundsEnabled()) {

                        if (succeeded) {

                            p.playSound(
                                    p.getLocation(),
                                    Sound.ENTITY_PLAYER_LEVELUP,
                                    1f,
                                    1f
                            );

                        } else {

                            p.playSound(
                                    p.getLocation(),
                                    Sound.BLOCK_ANVIL_LAND,
                                    0.7f,
                                    1.5f
                            );
                        }
                    }
                }

                if (succeeded) {

                    if (Bukkit.getWorld(world) != null) {

                        Bukkit.getWorld(world)
                                .setTime(1000L);
                    }
                }

                polls.remove(world);
            });
        }

        public int getYesVotes() {
            return yes.size();
        }

        public int getNoVotes() {
            return no.size();
        }

        public int getEligibleVotes() {
            return eligible.size();
        }

        public int getRemainingSeconds() {
            return remainingSeconds;
        }

        public String getWorld() {
            return world;
        }
    }
}