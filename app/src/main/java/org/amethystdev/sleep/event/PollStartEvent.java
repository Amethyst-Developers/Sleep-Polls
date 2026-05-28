package org.amethystdev.sleep.event;

import org.amethystdev.sleep.Poll;

public final class PollStartEvent
        extends PollEvent {

    public PollStartEvent(
            Poll poll
    ) {

        super(poll);
    }
}