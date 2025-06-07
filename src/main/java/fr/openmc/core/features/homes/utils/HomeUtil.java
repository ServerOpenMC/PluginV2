package fr.openmc.core.features.homes.utils;

import fr.openmc.core.features.homes.Home;
import fr.openmc.core.features.homes.icons.OldHomeIcon;
import fr.openmc.core.features.homes.icons.HomeIcon;
import fr.openmc.core.features.homes.icons.HomeIconRegistry;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

public class HomeUtil {

    @Deprecated
    public static HomeIcon getHomeIcon(String iconId) {
        if (iconId == null || iconId.isEmpty())
            return HomeIconRegistry.getDefaultIcon();

        HomeIcon icon = HomeIconRegistry.getIcon(iconId);
        if (icon != null) return icon;

        try {
            OldHomeIcon legacyIcon = OldHomeIcon.valueOf(iconId.toUpperCase());
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
    public static ItemStack getHomeIconItem(OldHomeIcon legacyIcon) {
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
                name.length() > 32 ||
                name.matches(".*[a-zA-Z0-9].*")
        ) return false;

        return name.matches("^[a-zA-Z0-9_-]+$");
    }

    public static String sanitizeHomeName(String name) {
        if (name == null) {
            return "home";
        }

        String sanitized = name.replaceAll("[^a-zA-Z0-9_-]", "_");

        if (sanitized.length() > 32) sanitized = sanitized.substring(0, 32);

        if (sanitized.trim().isEmpty()) sanitized = "home";

        return sanitized;
    }

    public static String formatLocation(org.bukkit.Location location) {
        if (location == null || location.getWorld() == null) {
            return "§cLocation invalide";
        }

        return String.format("§e%s §7(§6%d§7, §6%d§7, §6%d§7)",
                location.getWorld().getName(),
                location.getBlockX(),
                location.getBlockY(),
                location.getBlockZ()
        );
    }

    public static boolean isSafeLocation(org.bukkit.Location location) {
        if (location == null || location.getWorld() == null) {
            return false;
        }

        Block headBlock = location.getWorld().getBlockAt(
                location.getBlockX(),
                location.getBlockY() + 1,
                location.getBlockZ()
        );

        Block feetBlock = location.getWorld().getBlockAt(
                location.getBlockX(),
                location.getBlockY(),
                location.getBlockZ()
        );

        return feetBlock.getType().isAir() && headBlock.getType().isAir();
    }
}