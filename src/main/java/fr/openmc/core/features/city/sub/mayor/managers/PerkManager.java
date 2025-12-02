package fr.openmc.core.features.city.sub.mayor.managers;

import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.sub.mayor.models.Mayor;
import fr.openmc.core.features.city.sub.mayor.perks.PerkType;
import fr.openmc.core.features.city.sub.mayor.perks.Perks;
import fr.openmc.core.features.city.sub.milestone.rewards.FeaturesRewards;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class PerkManager {
    private static final Random RANDOM = new Random();

    /**
     * Get a perk by its ID
     *
     * @param id the ID of the perk
     */
    public static Perks getPerkById(int id) {
        for (Perks perks : Perks.values()) {
            if (perks.getId() == id) return perks;
        }
        return null;
    }

    /**
     * Get a random list of perks
     */
    public static List<Perks> getRandomPerksAll(City city) {
        List<Perks> eventPerks = Arrays.stream(Perks.values())
                .filter(perk -> perk.getType() == PerkType.EVENT)
                .filter(perk -> isPerkUnlockedForCity(city, perk))
                .toList();

        List<Perks> basicPerks = Arrays.stream(Perks.values())
                .filter(perk -> perk.getType() == PerkType.BASIC)
                .filter(perk -> isPerkUnlockedForCity(city, perk))
                .toList();

        List<Perks> finalSelection = new ArrayList<>();
        if (!eventPerks.isEmpty()) {
            Perks selectedEventPerk = eventPerks.get(RANDOM.nextInt(eventPerks.size()));
            finalSelection.add(selectedEventPerk);
        }

        List<Perks> selectedBasicPerks = new ArrayList<>();
        while (!basicPerks.isEmpty() && selectedBasicPerks.size() < 2) {
            Perks randomPerk = basicPerks.get(RANDOM.nextInt(basicPerks.size()));
            if (!selectedBasicPerks.contains(randomPerk)) {
                selectedBasicPerks.add(randomPerk);
            }
        }

        finalSelection.addAll(selectedBasicPerks);

        return finalSelection;
    }

    /**
     * Get a random list of basic perks
     */
    public static List<Perks> getRandomPerksBasic(City city) {
        List<Perks> basicPerks = Arrays.stream(Perks.values())
                .filter(perk -> perk.getType() == PerkType.BASIC)
                .filter(perk -> isPerkUnlockedForCity(city, perk))
                .toList();

        List<Perks> selectedBasicPerks = new ArrayList<>();
        while (!basicPerks.isEmpty() && selectedBasicPerks.size() < 2) {
            Perks randomPerk = basicPerks.get(RANDOM.nextInt(basicPerks.size()));
            if (!selectedBasicPerks.contains(randomPerk)) {
                selectedBasicPerks.add(randomPerk);
            }
        }

        return new ArrayList<>(selectedBasicPerks);
    }

    /**
     * Get a random list of event perks
     */
    public static Perks getRandomPerkEvent(City city) {
        List<Perks> eventPerks = Arrays.stream(Perks.values())
                .filter(perk -> perk.getType() == PerkType.EVENT)
                .filter(perk -> isPerkUnlockedForCity(city, perk))
                .toList();

        if (eventPerks.isEmpty()) return null;

        return eventPerks.get(RANDOM.nextInt(eventPerks.size()));
    }

    /**
     * Check if a mayor has a perk by its ID
     *
     * @param mayor the mayor to check
     * @param idPerk the ID of the perk to check
     */
    public static boolean hasPerk(Mayor mayor, int idPerk) {
        if (mayor == null) return false;

        return mayor.getIdPerk1() == idPerk
                || mayor.getIdPerk2() == idPerk
                || mayor.getIdPerk3() == idPerk;
    }

    public static Perks getPerkEvent(Mayor mayor) {
        if (getPerkById(mayor.getIdPerk1()).getType() == PerkType.EVENT) return getPerkById(mayor.getIdPerk1());
        if (getPerkById(mayor.getIdPerk2()).getType() == PerkType.EVENT) return getPerkById(mayor.getIdPerk2());
        if (getPerkById(mayor.getIdPerk3()).getType() == PerkType.EVENT) return getPerkById(mayor.getIdPerk3());

        return null;
    }

    /**
    * Check if the perk is unlocked for the given city
    * Prevents automatic perk selection from choosing locked perks
    *
    * @param city the city to check
    * @param perk the perk to validate
     */
    private static boolean isPerkUnlockedForCity(City city, Perks perk) {
        if (city == null || perk == null) return false;

        return switch (perk.getCategory()) {
            case AGRICULTURAL -> FeaturesRewards.hasUnlockFeature(city, FeaturesRewards.Feature.PERK_AGRICULTURAL);
            case ECONOMIC -> FeaturesRewards.hasUnlockFeature(city, FeaturesRewards.Feature.PERK_ECONOMY);
            case MILITARY -> FeaturesRewards.hasUnlockFeature(city, FeaturesRewards.Feature.PERK_MILITARY);
            case STRATEGY -> FeaturesRewards.hasUnlockFeature(city, FeaturesRewards.Feature.PERK_STRATEGY);
        };
    }
}
