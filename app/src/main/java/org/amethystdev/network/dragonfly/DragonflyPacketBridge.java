package org.amethystdev.network.dragonfly;

import org.amethystdev.network.packet.PacketSerializer;
import org.amethystdev.network.packet.PollFinishPacket;
import org.amethystdev.network.packet.PollStartPacket;
import org.amethystdev.network.packet.PollVotePacket;
import org.amethystdev.sleep.event.PollEventBus;
import org.amethystdev.sleep.event.PollFinishEvent;
import org.amethystdev.sleep.event.PollStartEvent;
import org.amethystdev.sleep.event.PollVoteEvent;

public final class DragonflyPacketBridge {

    private DragonflyPacketBridge() {}

    public static void initialize(
            DragonflyManager dragonfly
    ) {

        String serverId =
                dragonfly.getPlugin()
                        .getConfig()
                        .getString(
                                "server-id",
                                "server-1"
                        );

        PollEventBus.register(event -> {

            String json = null;

            if (event instanceof PollStartEvent e) {

                json = PacketSerializer.toJson(
                        new PollStartPacket(
                                serverId,
                                e.getPoll().getWorld(),
                                e.getPoll().getRemainingSeconds(),
                                e.getPoll().getRequiredPercentage(),
                                e.getPoll().getEligible()
                        )
                );
            }

            if (event instanceof PollVoteEvent e) {

                json = PacketSerializer.toJson(
                        new PollVotePacket(
                                serverId,
                                e.getPoll().getWorld(),
                                e.getPlayer(),
                                e.isYesVote()
                        )
                );
            }

            if (event instanceof PollFinishEvent e) {

                json = PacketSerializer.toJson(
                        new PollFinishPacket(
                                serverId,
                                e.getPoll().getWorld(),
                                e.wasSuccessful()
                        )
                );
            }

            if (json != null) {

                dragonfly.publish(json);
            }
        });
    }
}