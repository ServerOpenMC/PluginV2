package fr.openmc.core.hooks.github.models;

public record ContributorStats(int totalAddLines, int totalRemoveLines) {

    public int getTotalLines() {
        return totalAddLines - totalRemoveLines;
    }
}