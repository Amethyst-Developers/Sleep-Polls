package org.amethystdev.sleep.event;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public final class PollEventBus {

    private static final List<Consumer<PollEvent>>
            listeners =
            new ArrayList<>();

    private PollEventBus() {}

    public static void register(
            Consumer<PollEvent> listener
    ) {

        listeners.add(listener);
    }

    public static void fire(
            PollEvent event
    ) {

        for (Consumer<PollEvent> listener
                : listeners) {

            listener.accept(event);
        }
    }
}