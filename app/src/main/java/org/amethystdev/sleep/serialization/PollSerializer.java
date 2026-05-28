package org.amethystdev.sleep.serialization;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.amethystdev.sleep.Poll;

public final class PollSerializer {

    private static final Gson GSON =
            new GsonBuilder()
                    .setPrettyPrinting()
                    .create();

    private PollSerializer() {}

    /*
     * Serialize poll → JSON
     */
    public static String toJson(
            Poll poll
    ) {

        return GSON.toJson(poll);
    }

    /*
     * Deserialize JSON → poll
     */
    public static Poll fromJson(
            String json
    ) {

        return GSON.fromJson(
                json,
                Poll.class
        );
    }

    /*
     * Access raw Gson instance
     */
    public static Gson getGson() {

        return GSON;
    }
}