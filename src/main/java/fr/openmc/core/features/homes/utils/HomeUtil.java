package fr.openmc.core.features.homes.utils;

import fr.openmc.core.features.homes.icons.HomeIcon;
import fr.openmc.core.features.homes.icons.HomeIconRegistry;
import fr.openmc.core.features.homes.icons.LegacyHomeIcon;
import fr.openmc.core.features.homes.models.Home;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

public class HomeUtil {

    public static final int MAX_LENGTH_HOME_NAME = 32;
    @Deprecated
    public static HomeIcon getHomeIcon(String iconId) {
        if (iconId == null || iconId.isEmpty())
            return HomeIconRegistry.getDefaultIcon();

        HomeIcon icon = HomeIconRegistry.getIcon(iconId);
        if (icon != null) return icon;

        try {
            LegacyHomeIcon legacyIcon = LegacyHomeIcon.valueOf(iconId.toUpperCase());
            return HomeIconRegistry.fromLegacyHomeIcon(legacyIcon);
        } catch (IllegalArgumentException e) {
            return mapLegacyCustomId(iconId);
        }
    }

    @Deprecated
    public static ItemStack getHomeIconItem(Home home) {
        return home.getIcon().getItemStack();
    }

    @Deprecated
    public static ItemStack getHomeIconItem(LegacyHomeIcon legacyIcon) {
        HomeIcon icon = HomeIconRegistry.fromLegacyHomeIcon(legacyIcon);
        return icon.getItemStack();
    }

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
