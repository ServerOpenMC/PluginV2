package fr.openmc.core.features.events.contents.dailyevents.contents.miraculousfishing.minigame;

public enum FishingMiniGameResult {
    MISS(0.0),
    GOOD(0.10),
    PERFECT(0.25);

    private final double rarityBonus;

    FishingMiniGameResult(double rarityBonus) {
        this.rarityBonus = rarityBonus;
    }

    public double getRarityBonus() {
        return rarityBonus;
    }
}
