package org.amethystdev.network.packet;
import java.util.UUID;

public final class PollVotePacket
        extends PollPacket {

    private final String world;

    private final UUID player;

    private final boolean vote;

    public PollVotePacket(
            String origin,
            String world,
            UUID player,
            boolean vote
    ) {

        super(
                PacketType.POLL_VOTE,
                origin
        );

        this.world = world;

        this.player = player;

        this.vote = vote;
    }

    public String getWorld() {

        return world;
    }

    public UUID getPlayer() {

        return player;
    }

    public boolean isVote() {

        return vote;
    }
}