package org.amethystdev.sleep.event;

import org.amethystdev.sleep.Poll;

public final class PollFinishEvent
        extends PollEvent {

    private final boolean success;

    public PollFinishEvent(
            Poll poll,
            boolean success
    ) {

        super(poll);

        this.success = success;
    }

    public boolean wasSuccessful() {

        return success;
    }
}