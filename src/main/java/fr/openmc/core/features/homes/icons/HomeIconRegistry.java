package fr.openmc.core.features.homes.icons;

import org.bukkit.Material;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * The HomeIconRegistry manages the registration and retrieval of {@link HomeIcon} objects,
 * both custom and vanilla (based on Minecraft {@link Material}).
 * It allows initialization, querying, and conversion between legacy icons.
 */
public class HomeIconRegistry {

    private static final Map<String, HomeIcon> icons = new LinkedHashMap<>();
    private static final List<HomeIcon> customIcons = new ArrayList<>();
    private static final List<HomeIcon> vanillaIcons = new ArrayList<>();

    /**
     * Initializes the icon registry by registering predefined custom icons
     * and generating vanilla icons from all available {@link Material}s.
     */
    public static void initializeIcons() {
        // Custom icons registration
        registerCustomIcon("axeno", "Axeno", "omc_homes:omc_homes_icon_axenq");
        registerCustomIcon("bank", "Bank", "omc_homes:omc_homes_icon_bank");
        registerCustomIcon("chateau", "Château", "omc_homes:omc_homes_icon_chateau");
        registerCustomIcon("chest", "Chest", "omc_homes:omc_homes_icon_chest");
        registerCustomIcon("home", "Home", "omc_homes:omc_homes_icon_maison");
        registerCustomIcon("sandblock", "Sandblock", "omc_homes:omc_homes_icon_sandblock");
        registerCustomIcon("shop", "Shop", "omc_homes:omc_homes_icon_shop");
        registerCustomIcon("xernas", "Xernas", "omc_homes:omc_homes_icon_xernas");
        registerCustomIcon("farm", "Farm", "omc_homes:omc_homes_icon_zombie");
        registerCustomIcon("grass", "Grass", "omc_homes:omc_homes_icon_grass");

        // Vanilla icons registration
        for (Material material : Material.values()) {
            if (material.isItem() && !material.isAir()) {
                HomeIcon icon = HomeIcon.vanilla(material);
                icons.put(icon.id(), icon);
                vanillaIcons.add(icon);
            }
        }
    }

    /**
     * Registers a custom icon into the registry.
     *
     * @param id          Unique identifier for the icon.
     * @param displayName Display name of the icon.
     * @param customId    Custom ID for the icon.
     */
    private static void registerCustomIcon(String id, String displayName, String customId) {
        HomeIcon icon = HomeIcon.custom(id, displayName, customId);
        icons.put(icon.id(), icon);
        customIcons.add(icon);
    }

    /**
     * Retrieves an icon by its ID.
     *
     * @param id ID of the icon.
     * @return Corresponding {@link HomeIcon}, or null if not found.
     */
    public static HomeIcon getIcon(String id) {
        return icons.get(id);
    }

    /**
     * Retrieves an icon by its ID or returns the default icon if not found.
     *
     * @param id ID of the icon.
     * @return The found {@link HomeIcon}, or a default icon if missing.
     */
    public static HomeIcon getIconOrDefault(String id) {
        HomeIcon icon = icons.get(id);
        return icon != null ? icon : getDefaultIcon();
    }

    /**
     * Returns the default icon (vanilla grass block).
     *
     * @return Default {@link HomeIcon}.
     */
    public static HomeIcon getDefaultIcon() {
        return icons.get("vanilla:grass_block");
    }

    /**
     * Retrieves a list of all registered icons.
     *
     * @return List of all {@link HomeIcon} objects.
     */
    public static List<HomeIcon> getAllIcons() {
        return new ArrayList<>(icons.values());
    }

    /**
     * Retrieves a list of all custom icons.
     *
     * @return List of custom {@link HomeIcon} objects.
     */
    public static List<HomeIcon> getCustomIcons() {
        return new ArrayList<>(customIcons);
    }

    /**
     * Retrieves a list of all vanilla icons.
     *
     * @return List of vanilla {@link HomeIcon} objects.
     */
    public static List<HomeIcon> getVanillaIcons() {
        return new ArrayList<>(vanillaIcons);
    }

    /**
     * Filters icons by their category.
     *
     * @param category The {@link HomeIcon.IconCategory} to filter by.
     * @return List of matching {@link HomeIcon} objects.
     */
    public static List<HomeIcon> getIconsByCategory(HomeIcon.IconCategory category) {
        return switch (category) {
            case CUSTOM -> getCustomIcons();
            case VANILLA -> getVanillaIcons();
            default -> getAllIcons();
        };
    }

    /**
     * Searches for icons that match a given query in their ID, display name, or material.
     *
     * @param query Search query (case-insensitive).
     * @return List of matching {@link HomeIcon} objects.
     */
    public static List<HomeIcon> searchIcons(String query) {
        String lowerQuery = query.toLowerCase();
        return icons.values().stream()
                .filter(icon ->
                        icon.displayName().toLowerCase().contains(lowerQuery) ||
                                icon.id().toLowerCase().contains(lowerQuery) ||
                                (icon.isVanilla() && icon.getMaterial().name().toLowerCase().contains(lowerQuery))
                )
                .collect(Collectors.toList());
    }

    /**
     * Converts a legacy {@link LegacyHomeIcon} to a modern {@link HomeIcon}.
     *
     * @param legacyIcon The legacy icon.
     * @return A matching {@link HomeIcon}, or default if none matches.
     */
    public static HomeIcon fromLegacyHomeIcon(LegacyHomeIcon legacyIcon) {
        String customId = "custom:" + legacyIcon.getName().toLowerCase();
        return getIconOrDefault(customId);
    }

    /**
     * Converts a modern {@link HomeIcon} back to a legacy {@link LegacyHomeIcon} enum.
     *
     * @param icon The current icon.
     * @return The corresponding {@link LegacyHomeIcon}, or {@code DEFAULT} if not found.
     */
    public static LegacyHomeIcon toLegacyHomeIcon(HomeIcon icon) {
        if (icon.isCustom()) {
            String iconName = icon.id().replace("custom:", "").toUpperCase();
            try {
                return LegacyHomeIcon.valueOf(iconName);
            } catch (IllegalArgumentException e) {
                return LegacyHomeIcon.DEFAULT;
            }
        }
        return LegacyHomeIcon.DEFAULT;
    }
}
