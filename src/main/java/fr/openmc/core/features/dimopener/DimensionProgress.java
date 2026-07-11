package fr.openmc.core.features.dimopener;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class DimensionProgress {

    @Getter
    private final String dimensionId;
    @Setter @Getter
    private DimensionState state = DimensionState.LOCKED;
    @Getter @Setter
    private int currentStepIndex = 0;
    @Getter
    private double currentAmount = 0;

    @Getter
    private final Map<UUID, Double> contributions = new HashMap<>();

    @Getter
    private long cooldownEndTimestamp = -1;

    public DimensionProgress(String dimensionId) {
        this.dimensionId = dimensionId;
    }

    public void addAmount(UUID player, double amount) {
        this.currentAmount += amount;
        this.contributions.merge(player, amount, Double::sum);
    }

    public void resetStepProgress() {
        this.currentAmount =  0;
        this.contributions.clear();
    }

    public void startCooldown(long durationMillis) {
        this.cooldownEndTimestamp = System.currentTimeMillis() + durationMillis;
    }

    public boolean isCooldownOver() {
        return cooldownEndTimestamp != -1 && System.currentTimeMillis() >= this.cooldownEndTimestamp;
    }

    public void clearCooldown() {
        this.cooldownEndTimestamp = -1;
    }
}
