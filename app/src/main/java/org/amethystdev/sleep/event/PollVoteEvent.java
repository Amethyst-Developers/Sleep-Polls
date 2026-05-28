package org.amethystdev.sleep.event;

import java.util.UUID;

import org.amethystdev.sleep.Poll;

public final class PollVoteEvent
        extends PollEvent {

    private final UUID player;

    private final boolean vote;

    public PollVoteEvent(
            Poll poll,
            UUID player,
            boolean vote
    ) {

        super(poll);

        this.player = player;

        this.vote = vote;
    }

    public UUID getPlayer() {

        return player;
    }

    public boolean isYesVote() {

        return vote;
    }
}