package fr.openmc.core.features.events.contents.dailyevents.contents.miraculousfishing;

import fr.openmc.core.features.events.contents.dailyevents.contents.miraculousfishing.minigame.FishingMiniGameResult;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FishingMiniGameResultTest {

    @Test
    void testRarityBonusValues() {
        assertEquals(0.0, FishingMiniGameResult.MISS.getRarityBonus());
        assertEquals(0.10, FishingMiniGameResult.GOOD.getRarityBonus());
        assertEquals(0.25, FishingMiniGameResult.PERFECT.getRarityBonus());
    }

    @Test
    void testMissKeepsOriginalWeights() {
        assertEquals(0.10, FishingAttributeManager.getRarityAdjustedChance(
                0.10, 0.70, FishingMiniGameResult.MISS
        ), 1e-9);
        assertEquals(0.70, FishingAttributeManager.getRarityAdjustedChance(
                0.70, 0.70, FishingMiniGameResult.MISS
        ), 1e-9);
    }

    @Test
    void testRareLootGetsMoreWeightThanCommonLoot() {
        double commonWeight = FishingAttributeManager.getRarityAdjustedChance(
                0.70, 0.70, FishingMiniGameResult.GOOD);
        double rareWeight = FishingAttributeManager.getRarityAdjustedChance(
                0.10, 0.70, FishingMiniGameResult.GOOD);

        assertEquals(0.70, commonWeight, 1e-9);
        assertTrue(rareWeight > 0.10);
        assertTrue((rareWeight / 0.10) > (commonWeight / 0.70));
    }

    @Test
    void testPerfectGivesMoreBonusThanGood() {
        double goodWeight = FishingAttributeManager.getRarityAdjustedChance(
                0.10, 0.70, FishingMiniGameResult.GOOD);
        double perfectWeight = FishingAttributeManager.getRarityAdjustedChance(
                0.10, 0.70, FishingMiniGameResult.PERFECT);

        assertTrue(perfectWeight > goodWeight);
    }

    @Test
    void testInvalidMaximumChanceDoesNotProduceWeight() {
        assertEquals(0.0, FishingAttributeManager.getRarityAdjustedChance(
                0.10, 0.0, FishingMiniGameResult.PERFECT
        ), 1e-9);
    }
}
