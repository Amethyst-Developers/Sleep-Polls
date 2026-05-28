package org.amethystdev.model;

import java.util.UUID;

public final class PlayerData {

    private final UUID uuid;

    private boolean bossBarEnabled;

    private int totalVotes;

    private int successfulVotes;

    private int failedVotes;

    private int pollsStarted;

    private int nightsSkipped;

    public PlayerData(
            UUID uuid,
            boolean bossBarEnabled,
            int totalVotes,
            int successfulVotes,
            int failedVotes,
            int pollsStarted,
            int nightsSkipped
    ) {

        this.uuid = uuid;

        this.bossBarEnabled = bossBarEnabled;

        this.totalVotes = totalVotes;

        this.successfulVotes = successfulVotes;

        this.failedVotes = failedVotes;

        this.pollsStarted = pollsStarted;

        this.nightsSkipped = nightsSkipped;
    }

    public UUID getUuid() {
        return uuid;
    }

    public boolean isBossBarEnabled() {
        return bossBarEnabled;
    }

    public void setBossBarEnabled(
            boolean bossBarEnabled
    ) {

        this.bossBarEnabled = bossBarEnabled;
    }

    public int getTotalVotes() {
        return totalVotes;
    }

    public void setTotalVotes(
            int totalVotes
    ) {

        this.totalVotes = totalVotes;
    }

    public int getSuccessfulVotes() {
        return successfulVotes;
    }

    public void setSuccessfulVotes(
            int successfulVotes
    ) {

        this.successfulVotes = successfulVotes;
    }

    public int getFailedVotes() {
        return failedVotes;
    }

    public void setFailedVotes(
            int failedVotes
    ) {

        this.failedVotes = failedVotes;
    }

    public int getPollsStarted() {
        return pollsStarted;
    }

    public void setPollsStarted(
            int pollsStarted
    ) {

        this.pollsStarted = pollsStarted;
    }

    public int getNightsSkipped() {
        return nightsSkipped;
    }

    public void setNightsSkipped(
            int nightsSkipped
    ) {

        this.nightsSkipped = nightsSkipped;
    }

    public void incrementVotes() {
        this.totalVotes++;
    }

    public void incrementSuccessfulVotes() {
        this.successfulVotes++;
    }

    public void incrementFailedVotes() {
        this.failedVotes++;
    }

    public void incrementPollsStarted() {
        this.pollsStarted++;
    }

    public void incrementNightsSkipped() {
        this.nightsSkipped++;
    }
}