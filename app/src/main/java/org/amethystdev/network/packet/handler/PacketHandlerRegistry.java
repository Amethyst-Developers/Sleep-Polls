package org.amethystdev.network.packet.handler;

import java.util.HashMap;
import java.util.Map;

import org.amethystdev.network.packet.PacketType;
import org.amethystdev.network.packet.PollPacket;

public final class PacketHandlerRegistry {

    private final Map<
            PacketType,
            PacketHandler<? extends PollPacket>
            > handlers =
            new HashMap<>();

    public <T extends PollPacket> void register(
            PacketType type,
            PacketHandler<T> handler
    ) {

        handlers.put(
                type,
                handler
        );
    }

    @SuppressWarnings("unchecked")
    public <T extends PollPacket> void handle(
            T packet
    ) {

        PacketHandler<T> handler =
                (PacketHandler<T>) handlers.get(
                        packet.getType()
                );

        if (handler == null) {
            return;
        }

        handler.handle(packet);
    }
}