package org.amethystdev.network.packet;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public final class PacketSerializer {

    private static final Gson GSON =
            new GsonBuilder()
                    .setPrettyPrinting()
                    .create();

    private PacketSerializer() {}

    /*
     * Serialize packet → JSON
     */
    public static String toJson(
            PollPacket packet
    ) {

        return GSON.toJson(packet);
    }

    /*
     * Deserialize JSON → packet
     */
    public static PollPacket fromJson(
            String json
    ) {

        JsonObject object =
                JsonParser.parseString(json)
                        .getAsJsonObject();

        PacketType type =
                PacketType.valueOf(
                        object.get("type")
                                .getAsString()
                );

        return switch (type) {

            case POLL_START ->
                    GSON.fromJson(
                            json,
                            PollStartPacket.class
                    );

            case POLL_VOTE ->
                    GSON.fromJson(
                            json,
                            PollVotePacket.class
                    );

            case POLL_FINISH ->
                    GSON.fromJson(
                            json,
                            PollFinishPacket.class
                    );

            default ->
                    throw new IllegalStateException(
                            "Unknown packet type: "
                                    + type
                    );
        };
    }

    public static Gson getGson() {

        return GSON;
    }
}