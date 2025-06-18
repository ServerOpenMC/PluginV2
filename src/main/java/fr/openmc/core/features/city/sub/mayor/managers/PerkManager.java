package fr.openmc.core.features.city.sub.mayor.managers;

import fr.openmc.core.features.city.sub.mayor.Mayor;
import fr.openmc.core.features.city.sub.mayor.perks.PerkType;
import fr.openmc.core.features.city.sub.mayor.perks.Perks;
import fr.openmc.core.features.city.models.Mayor;
import fr.openmc.core.features.city.mayor.perks.PerkType;
import fr.openmc.core.features.city.mayor.perks.Perks;

import java.util.ArrayList;
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
    public static List<Perks> getRandomPerksAll() {
        List<Perks> eventPerks = List.of(Perks.values()).stream()
                .filter(perk -> perk.getType() == PerkType.EVENT)
                .toList();

        List<Perks> basicPerks = List.of(Perks.values()).stream()
                .filter(perk -> perk.getType() == PerkType.BASIC)
                .toList();

        Perks selectedEventPerk = eventPerks.get(RANDOM.nextInt(eventPerks.size()));

        List<Perks> selectedBasicPerks = new ArrayList<>();
        while (selectedBasicPerks.size() < 2) {
            Perks randomPerk = basicPerks.get(RANDOM.nextInt(basicPerks.size()));
            if (!selectedBasicPerks.contains(randomPerk)) {
                selectedBasicPerks.add(randomPerk);
            }
        }

        List<Perks> finalSelection = new ArrayList<>();
        finalSelection.add(selectedEventPerk);
        finalSelection.addAll(selectedBasicPerks);

        return finalSelection;
    }

    /**
     * Get a random list of basic perks
     */
    public static List<Perks> getRandomPerksBasic() {
        List<Perks> basicPerks = List.of(Perks.values()).stream()
                .filter(perk -> perk.getType() == PerkType.BASIC)
                .toList();

        List<Perks> selectedBasicPerks = new ArrayList<>();
        while (selectedBasicPerks.size() < 2) {
            Perks randomPerk = basicPerks.get(RANDOM.nextInt(basicPerks.size()));
            if (!selectedBasicPerks.contains(randomPerk)) {
                selectedBasicPerks.add(randomPerk);
            }
        }

        List<Perks> finalSelection = new ArrayList<>();
        finalSelection.addAll(selectedBasicPerks);

        return finalSelection;
    }

    /**
     * Get a random list of event perks
     */
    public static Perks getRandomPerkEvent() {
        List<Perks> eventPerks = List.of(Perks.values()).stream()
                .filter(perk -> perk.getType() == PerkType.EVENT)
                .toList();

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
        if ((mayor.getIdPerk1() == idPerk) || (mayor.getIdPerk2() == idPerk) || (mayor.getIdPerk3() == idPerk)) {
            return true;
        }
        return false;
    }

    public static Perks getPerkEvent(Mayor mayor) {
        if (getPerkById(mayor.getIdPerk1()).getType() == PerkType.EVENT) return getPerkById(mayor.getIdPerk1());
        if (getPerkById(mayor.getIdPerk2()).getType() == PerkType.EVENT) return getPerkById(mayor.getIdPerk2());
        if (getPerkById(mayor.getIdPerk3()).getType() == PerkType.EVENT) return getPerkById(mayor.getIdPerk3());

        return null;
    }
}
