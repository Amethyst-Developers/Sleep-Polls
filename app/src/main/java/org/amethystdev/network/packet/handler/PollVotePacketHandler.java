package org.amethystdev.network.packet.handler;

import org.amethystdev.Main;
import org.amethystdev.network.packet.PollVotePacket;
import org.amethystdev.sleep.Poll;

public final class PollVotePacketHandler
        implements PacketHandler<PollVotePacket> {

    private final Main plugin;

    public PollVotePacketHandler(
            Main plugin
    ) {

        this.plugin = plugin;
    }

    @Override
    public void handle(
            PollVotePacket packet
    ) {

        Poll poll =
                plugin.getPollManager()
                        .getPoll(
                                packet.getWorld()
                        );

        if (poll == null) {
            return;
        }

        poll.applyRemoteVote(
                packet.getPlayer(),
                packet.isVote()
        );

        plugin.getLogger().info(
                "Applied remote vote from "
                        + packet.getPlayer()
        );
    }
}