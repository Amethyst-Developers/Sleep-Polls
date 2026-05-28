package org.amethystdev.sleep.event;

import org.amethystdev.sleep.Poll;

public abstract class PollEvent {

    private final Poll poll;

    protected PollEvent(
            Poll poll
    ) {

        this.poll = poll;
    }

    public Poll getPoll() {

        return poll;
    }
}