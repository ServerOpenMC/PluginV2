package fr.openmc.core.features.city.listeners.protections;

import lombok.Getter;

public class CityExplosionData {
    @Getter
    private int explosions;
    private long lastResetTimestamp;

    public CityExplosionData() {
        this.explosions = 0;
        this.lastResetTimestamp = System.currentTimeMillis();
    }

    public void resetIfNeeded() {
        long now = System.currentTimeMillis();
        if (now - lastResetTimestamp >= 86_400_000L) { // 24h
            explosions = 0;
            lastResetTimestamp = now;
        }
    }

    public boolean canExplode(int maxPerDay) {
        resetIfNeeded();
        return explosions < maxPerDay;
    }

    public void increment() {
        explosions++;
    }
}
