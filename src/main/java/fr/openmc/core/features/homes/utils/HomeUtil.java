package fr.openmc.core.features.homes.utils;

import fr.openmc.core.features.homes.icons.HomeIcon;
import fr.openmc.core.features.homes.icons.HomeIconRegistry;

public class HomeUtil {
    private HomeUtil() {}

    public static final int MAX_LENGTH_HOME_NAME = 32;

    /**
     * Maps legacy custom icon IDs to the new HomeIcon system.
     * This method is used to maintain compatibility with older icon IDs.
     *
     * @param iconId The legacy custom icon ID.
     * @return The corresponding HomeIcon, or the default icon if not found.
     */
    public static HomeIcon mapLegacyCustomId(String iconId) {
        return switch (iconId.toLowerCase()) {
            case "omc_homes:omc_homes_icon_axenq" -> HomeIconRegistry.getIcon("custom:axenq");
            case "omc_homes:omc_homes_icon_bank" -> HomeIconRegistry.getIcon("custom:bank");
            case "omc_homes:omc_homes_icon_chateau" -> HomeIconRegistry.getIcon("custom:chateau");
            case "omc_homes:omc_homes_icon_chest" -> HomeIconRegistry.getIcon("custom:chest");
            case "omc_homes:omc_homes_icon_maison" -> HomeIconRegistry.getIcon("custom:home");
            case "omc_homes:omc_homes_icon_sandblock" -> HomeIconRegistry.getIcon("custom:sandblock");
            case "omc_homes:omc_homes_icon_shop" -> HomeIconRegistry.getIcon("custom:shop");
            case "omc_homes:omc_homes_icon_xernas" -> HomeIconRegistry.getIcon("custom:xernas");
            case "omc_homes:omc_homes_icon_zombie" -> HomeIconRegistry.getIcon("custom:farm");
            case "omc_homes:omc_homes_icon_grass" -> HomeIconRegistry.getIcon("custom:default");
            default -> HomeIconRegistry.getDefaultIcon();
        };
    }

    public static boolean isValidHomeName(String name) {
        if (
                name == null ||
                name.trim().isEmpty() ||
                name.length() < 3 ||
                name.length() > MAX_LENGTH_HOME_NAME
        ) return false;

        long alphanumericCount = name.chars().filter(Character::isLetterOrDigit).count();
        if (alphanumericCount < 3) return false;

        return name.matches("^[a-zA-Z0-9_-]+$");
    }
}
