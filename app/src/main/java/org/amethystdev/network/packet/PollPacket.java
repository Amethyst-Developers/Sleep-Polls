package org.amethystdev.network.packet;

public abstract class PollPacket {

    private final PacketType type;

    private final String origin;

    protected PollPacket(
            PacketType type,
            String origin
    ) {

        this.type = type;

        this.origin = origin;
    }

    public PacketType getType() {

        return type;
    }

    public String getOrigin() {

        return origin;
    }
}