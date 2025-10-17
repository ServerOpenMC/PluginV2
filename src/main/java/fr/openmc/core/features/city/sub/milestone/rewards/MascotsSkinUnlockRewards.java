package fr.openmc.core.features.city.sub.milestone.rewards;

import fr.openmc.core.features.city.sub.mascots.models.MascotType;
import fr.openmc.core.features.city.sub.milestone.CityRewards;
import lombok.Getter;
import net.kyori.adventure.text.Component;

import java.util.Arrays;
import java.util.List;

/**
 * Enumération représentant les récompenses de skins pour la Mascotte.
 * Chaque niveau débloque un ou plusieurs skins représentés par un MascotType.
 */
@Getter
public enum MascotsSkinUnlockRewards implements CityRewards {

    LEVEL_1(MascotType.ZOMBIE),
    LEVEL_2(MascotType.COW, MascotType.MOOSHROOM),
    LEVEL_3(MascotType.SPIDER, MascotType.SKELETON),
    LEVEL_4(MascotType.VILLAGER),
    LEVEL_5(MascotType.SHEEP),
    LEVEL_6(MascotType.PANDA),
    LEVEL_7(MascotType.PIG),
    LEVEL_8(MascotType.WOLF, MascotType.GOAT),
    LEVEL_9(MascotType.CHICKEN),
    LEVEL_10(MascotType.AXOLOTL);

    /**
     * Skins débloqués à ce niveau.
     */
    private final MascotType[] mascotsSkin;

    /**
     * Constructeur de l'énumération.
     *
     * @param mascotsSkin un ou plusieurs MascotType débloqués à ce niveau
     */
    MascotsSkinUnlockRewards(MascotType... mascotsSkin) {
        this.mascotsSkin = mascotsSkin;
    }

    /**
     * Retourne le niveau requis pour débloquer un skin spécifique.
     *
     * @param type le type de MascotType recherché
     * @return le niveau requis, ou -1 si le type n'est pas trouvé
     */
    public static int getLevelRequiredSkin(MascotType type) {
        for (MascotsSkinUnlockRewards reward : MascotsSkinUnlockRewards.values()) {
            for (MascotType mascot : reward.mascotsSkin) {
                if (mascot.equals(type)) {
                    String name = reward.name();
                    return Integer.parseInt(name.split("_")[1]);
                }
            }
        }
        return -1;
    }

    /**
     * Retourne un composant texte décrivant les skins débloqués.
     *
     * @return un composant texte indiquant les skins débloqués
     */
    @Override
    public Component getName() {
        MascotType[] unlocked = this.getMascotsSkin();
        List<String> names = Arrays.stream(unlocked)
                .map(MascotType::getDisplayName)
                .toList();
        String skins;
        if (names.size() == 1) {
            skins = "la mascotte " + names.getFirst();
        } else {
            skins = "les mascottes " + String.join(", ", names.subList(0, names.size() - 1))
                    + " et " + names.get(names.size() - 1);
        }
        return Component.text("§7Débloque §c" + skins);
    }
}