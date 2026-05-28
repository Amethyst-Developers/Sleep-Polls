package org.amethystdev.network.packet.handler;

import org.amethystdev.Main;
import org.amethystdev.network.packet.PollStartPacket;
import org.bukkit.Bukkit;

public final class PollStartPacketHandler
        implements PacketHandler<PollStartPacket> {

    private final Main plugin;

    public PollStartPacketHandler(
            Main plugin
    ) {

        this.plugin = plugin;
    }

    @Override
    public void handle(
            PollStartPacket packet
    ) {

        Bukkit.getScheduler()
                .runTask(plugin, () -> {

                    /*
                     * Prevent duplicates
                     */
                    if (plugin.getPollManager()
                            .isPollActive(
                                    packet.getWorld()
                            )) {

                        return;
                    }

                    plugin.getLogger().info(
                            "Creating remote poll from packet."
                    );

                    plugin.getPollManager()
                            .createRemotePoll(
                                packet.getWorld(),
                                packet.getEligible(),
                                packet.getDurationSeconds(),
                                packet.getRequiredPercentage()
                            );
                });
    }
}