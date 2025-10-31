package fr.openmc.core.features.dream.models.registry;

public record DreamLoot(DreamItem item, double chance, int minAmount, int maxAmount) {
    public int getRandomAmount() {
        return minAmount + (int) (Math.random() * (maxAmount - minAmount + 1));
    }
}
