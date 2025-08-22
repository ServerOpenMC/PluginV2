package fr.openmc.core.features.leaderboards;

public record ContributorStats(int added, int removed) {

    public int getTotalLines() {
        return Math.abs(added) + Math.abs(removed);
    }
}