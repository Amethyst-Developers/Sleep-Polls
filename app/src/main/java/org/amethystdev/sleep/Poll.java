package org.amethystdev.sleep;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public final class Poll {

    private final String world;

    /*
     * All eligible voters
     */
    private final Set<UUID> eligible =
            new HashSet<>();

    /*
     * YES votes
     */
    private final Set<UUID> yesVotes =
            new HashSet<>();

    /*
     * NO votes
     */
    private final Set<UUID> noVotes =
            new HashSet<>();

    /*
     * Poll creation timestamp
     */
    private final long createdAt;

    /*
     * Current poll state
     */
    private PollState state =
            PollState.ACTIVE;

    /*
     * Remaining duration
     */
    private int remainingSeconds;

    /*
     * Required vote percentage
     */
    private final int requiredPercentage;

    public Poll(
            String world,
            Set<UUID> eligible,
            int durationSeconds,
            int requiredPercentage
    ) {

        this.world = world;

        this.eligible.addAll(
                eligible
        );

        this.remainingSeconds =
                durationSeconds;

        this.requiredPercentage =
                requiredPercentage;

        this.createdAt =
                System.currentTimeMillis();
    }

    /*
     * Register vote
     */
    public synchronized void vote(
            UUID uuid,
            boolean yes
    ) {

        yesVotes.remove(uuid);

        noVotes.remove(uuid);

        if (yes) {

            yesVotes.add(uuid);

        } else {

            noVotes.add(uuid);
        }
    }

    /*
     * Has poll passed?
     */
    public synchronized boolean hasPassed() {

        return yesVotes.size()
                >= getNeededVotes();
    }

    /*
     * Can poll still mathematically pass?
     */
    public synchronized boolean canStillPass() {

        int remaining =
                eligible.size()
                        - (
                        yesVotes.size()
                                + noVotes.size()
                );

        return yesVotes.size()
                + remaining
                >= getNeededVotes();
    }

    /*
     * Needed YES votes
     */
    public int getNeededVotes() {

        return (int) Math.ceil(
                eligible.size()
                        * (
                        requiredPercentage
                                / 100.0
                )
        );
    }

    /*
     * Decrement timer
     */
    public void decrementTimer() {

        remainingSeconds--;
    }

    /*
     * World
     */
    public String getWorld() {

        return world;
    }

    /*
     * Eligible players
     */
    public Set<UUID> getEligible() {

        return eligible;
    }

    /*
     * YES votes
     */
    public Set<UUID> getYesVotes() {

        return yesVotes;
    }

    /*
     * NO votes
     */
    public Set<UUID> getNoVotes() {

        return noVotes;
    }

    /*
     * Creation timestamp
     */
    public long getCreatedAt() {

        return createdAt;
    }

    /*
     * Poll state
     */
    public PollState getState() {

        return state;
    }

    public void setState(
            PollState state
    ) {

        this.state = state;
    }

    /*
     * Remaining seconds
     */
    public int getRemainingSeconds() {

        return remainingSeconds;
    }

    public void setRemainingSeconds(
            int remainingSeconds
    ) {

        this.remainingSeconds =
                remainingSeconds;
    }

    /*
     * Required vote percentage
     */
    public int getRequiredPercentage() {

        return requiredPercentage;
    }

    /*
     * Vote counts
     */
    public int getYesVoteCount() {

        return yesVotes.size();
    }

    public int getNoVoteCount() {

        return noVotes.size();
    }

    public int getEligibleCount() {

        return eligible.size();
    }

    /*
     * Poll participation
     */
    public boolean hasVoted(
            UUID uuid
    ) {

        return yesVotes.contains(uuid)
                || noVotes.contains(uuid);
    }

    public boolean votedYes(
            UUID uuid
    ) {

        return yesVotes.contains(uuid);
    }

    public boolean votedNo(
            UUID uuid
    ) {

        return noVotes.contains(uuid);
    }

    /*
     * Remaining possible votes
     */
    public int getRemainingPossibleVotes() {

        return eligible.size()
                - (
                yesVotes.size()
                        + noVotes.size()
        );
    }

    /*
     * Vote percentage
     */
    public double getYesPercentage() {

        if (eligible.isEmpty()) {
            return 0;
        }

        return (
                yesVotes.size()
                        / (double) eligible.size()
        ) * 100.0;
    }

    /*
     * Expired?
     */
    public boolean isExpired() {

        return remainingSeconds <= 0;
    }

    /*
     * Active?
     */
    public boolean isActive() {

        return state
                == PollState.ACTIVE;
    }
    public void applyRemoteVote(
            UUID player,
            boolean vote
    ) {

        yesVotes.remove(player);

        noVotes.remove(player);

        if (vote) {

            yesVotes.add(player);

        } else {

            noVotes.add(player);
        }
    }
}