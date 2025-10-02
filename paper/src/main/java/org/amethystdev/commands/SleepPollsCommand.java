package org.amethystdev.commands;

import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Subcommand;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;

@Command({"Sleeppoll","sp"})
public class SleepPollsCommand {
    
    @Subcommand("version")
    public void version(BukkitCommandActor actor) {
        actor.reply("The plugin is running on 0.0.1-ALPHA");
    }

}
