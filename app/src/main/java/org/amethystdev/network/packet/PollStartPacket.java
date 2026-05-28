package org.amethystdev.network.packet;

import java.util.Set;
import java.util.UUID;

public final class PollStartPacket
        extends PollPacket {

    private final String world;

    private final int durationSeconds;

    private final int requiredPercentage;

    private final Set<UUID> eligible;

    public PollStartPacket(
            String origin,
            String world,
            int durationSeconds,
            int requiredPercentage,
            Set<UUID> eligible
    ) {

        super(
                PacketType.POLL_START,
                origin
        );

        this.world = world;

        this.durationSeconds =
                durationSeconds;

        this.requiredPercentage =
                requiredPercentage;

        this.eligible = eligible;
    }

    public String getWorld() {

        return world;
    }

    public int getDurationSeconds() {

        return durationSeconds;
    }

    public int getRequiredPercentage() {

        return requiredPercentage;
    }

    public Set<UUID> getEligible() {

        return eligible;
    }
}