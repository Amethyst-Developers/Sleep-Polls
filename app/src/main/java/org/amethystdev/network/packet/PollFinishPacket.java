package org.amethystdev.network.packet;

public final class PollFinishPacket
        extends PollPacket {

    private final String world;

    private final boolean success;

    public PollFinishPacket(
            String origin,
            String world,
            boolean success
    ) {

        super(
                PacketType.POLL_FINISH,
                origin
        );

        this.world = world;

        this.success = success;
    }

    public String getWorld() {

        return world;
    }

    public boolean isSuccess() {

        return success;
    }
}