package org.amethystdev.commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextDecoration;
import org.amethystdev.Main;
import org.amethystdev.sleep.SleepPollManager;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Subcommand;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;

@Command({ "sleeppoll", "sp" })
public class SleepPollsCommand {
    private final Main plugin;

    public SleepPollsCommand(Main plugin) {
        this.plugin = plugin;
    }

    @Subcommand("version")
    public void version(BukkitCommandActor actor) {
        actor.reply("The plugin is running on 0.0.1-ALPHA");
    }

    @Subcommand("yes")
    public void voteYes(BukkitCommandActor actor) {
        if (!actor.isPlayer()) {
            actor.reply("This command can only be used by players!");
            return;
        }

        Player player = actor.asPlayer();
        SleepPollManager manager = plugin.getPollManager();
        String worldName = player.getWorld().getName();
        var poll = manager.getPoll(worldName);

        if (poll == null) {
            player.sendMessage(Component.text("[SleepPoll] ", Style.style(NamedTextColor.GOLD, TextDecoration.BOLD))
                    .append(Component.text("There is no active poll in your world.", NamedTextColor.RED)));
            return;
        }

        poll.vote(player, true);
    }

    @Subcommand("no")
    public void voteNo(BukkitCommandActor actor) {
        if (!actor.isPlayer()) {
            actor.reply("This command can only be used by players!");
            return;
        }

        Player player = actor.asPlayer();
        SleepPollManager manager = plugin.getPollManager();
        String worldName = player.getWorld().getName();
        var poll = manager.getPoll(worldName);

        if (poll == null) {
            player.sendMessage(Component.text("[SleepPoll] ", Style.style(NamedTextColor.GOLD, TextDecoration.BOLD))
                    .append(Component.text("There is no active poll in your world.", NamedTextColor.RED)));
            return;
        }

        poll.vote(player, false);
    }

}
