package fr.openmc.core.features.homes.icons;

import fr.openmc.core.utils.customitems.CustomItemRegistry;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Represents an icon used for homes, which can be either a vanilla Minecraft material or a custom item.
 */
public record HomeIcon(String id, String displayName, IconType type, String materialOrCustomId) {
    /**
     * Creates a custom HomeIcon.
     *
     * @param id          Unique identifier for the icon.
     * @param displayName Display name of the icon.
     * @param customId    Custom ID for the icon.
     * @return A HomeIcon of type CUSTOM.
     */
    public static HomeIcon custom(String id, String displayName, String customId) {
        return new HomeIcon(id, displayName, IconType.CUSTOM, customId);
    }

    /**
     * Creates a vanilla icon.
     *
     * @param material Material to represent the icon.
     * @return A HomeIcon of type VANILLA.
     */
    public static HomeIcon vanilla(Material material) {
        String name = formatMaterialName(material.name());
        return new HomeIcon(
                "vanilla:" + material.name().toLowerCase(),
                name,
                IconType.VANILLA,
                material.name()
        );
    }

    /**
     * Gets thye {@link ItemStack} associated with this icon.
     *
     * @return The ItemStack to display
     */
    public ItemStack getItemStack() {
        switch (type) {
            case CUSTOM:
                try {
                    return Objects.requireNonNull(CustomItemRegistry.getByName(materialOrCustomId)).getBest();
                } catch (Exception e) {
                    return new ItemStack(Material.GRASS_BLOCK);
                }
            case VANILLA:
                return new ItemStack(Material.valueOf(materialOrCustomId));
            default:
                return new ItemStack(Material.GRASS_BLOCK);
        }
    }

    /**
     * Converts a material name to a readable format (e.g. "STONE_BRICKS" -> "Stone Bricks").
     *
     * @param materialName The raw material name.
     * @return A human-readable string.
     */
    private static String formatMaterialName(String materialName) {
        Pattern pattern = Pattern.compile("\\b\\w");
        Matcher matcher = pattern.matcher(materialName.toLowerCase().replace("_", " "));

        StringBuilder result = new StringBuilder();
        int lastEnd = 0;

        while (matcher.find()) {
            result.append(materialName.substring(lastEnd, matcher.start()).toLowerCase());
            result.append(matcher.group().toUpperCase());
            lastEnd = matcher.end();
        }

        result.append(materialName.substring(lastEnd).toLowerCase());
        return result.toString();
    }

    /**
     * Checks if the icon is of type CUSTOM.
     *
     * @return True if custom, false otherwise.
     */
    public boolean isCustom() {
        return type == IconType.CUSTOM;
    }

    /**
     * Checks if the icon is of type VANILLA.
     *
     * @return True if vanilla, false otherwise.
     */
    public boolean isVanilla() {
        return type == IconType.VANILLA;
    }

    /**
     * Gets the material if this icon is of type VANILLA.
     *
     * @return The corresponding {@link Material} or null if it's a custom icon.
     */
    public Material getMaterial() {
        if (type == IconType.VANILLA)
            return Material.valueOf(materialOrCustomId);
        return null;
    }

    /**
     * Gets the formatted display name for vanilla icons.
     *
     * @return The display name for vanilla icons, or the set display name for custom ones.
     */
    public String getVanillaName() {
        if (type == IconType.VANILLA) {
            String displayName = PlainTextComponentSerializer.plainText().serialize(getItemStack().displayName());
            return displayName.replaceAll("[\\[\\]]", "").trim();
        }
        return displayName();
    }

    /**
     * Gets the save ID used for persistent storage.
     *
     * @return The unique ID of the icon.
     */
    public String getSaveId() {
        return id;
    }

    /**
     * Compares this icon to another for equality based on ID.
     *
     * @param obj The other object.
     * @return True if the IDs match.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        HomeIcon homeIcon = (HomeIcon) obj;
        return id.equals(homeIcon.id);
    }

    /**
     * Hashcode based on the icon ID.
     *
     * @return Hashcode of the ID.
     */
    @Override
    public int hashCode() {
        return id.hashCode();
    }

    /**
     * Returns a string representation of the icon.
     *
     * @return A string with icon details.
     */
    @Override
    public String toString() {
        return "HomeIcon{" +
                "id='" + id + '\'' +
                ", displayName='" + displayName + '\'' +
                ", type=" + type +
                ", materialOrCustomId='" + materialOrCustomId + '\'' +
                '}';
    }
}