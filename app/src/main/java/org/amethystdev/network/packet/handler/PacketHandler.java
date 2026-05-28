package org.amethystdev.network.packet.handler;

import org.amethystdev.network.packet.PollPacket;

public interface PacketHandler<T extends PollPacket> {

    void handle(T packet);
}