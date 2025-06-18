package fr.openmc.core.features.settings.menu;

import fr.openmc.api.menulib.Menu;
import fr.openmc.api.menulib.utils.InventorySize;
import fr.openmc.api.menulib.utils.ItemBuilder;
import fr.openmc.core.features.settings.PlayerSettings;
import fr.openmc.core.features.settings.PlayerSettingsManager;
import fr.openmc.core.features.settings.SettingType;
import fr.openmc.core.features.settings.policy.CityPolicy;
import fr.openmc.core.features.settings.policy.FriendPolicy;
import fr.openmc.core.features.settings.policy.GlobalPolicy;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlayerSettingsMenu extends Menu {

    private final PlayerSettings settings;

    public PlayerSettingsMenu(Player player) {
        super(player);
        try {
            this.settings = PlayerSettingsManager.getInstance().getPlayerSettings(player);
        } catch (Exception e) {
            player.sendMessage(Component.text("Erreur lors de l'ouverture des paramètres. Veuillez réessayer plus tard.", NamedTextColor.RED));
            throw new RuntimeException("Failed to initialize PlayerSettingsMenu", e);
        }
    }

    @Override
    public @NotNull String getName() {
        return "§r§fParamètres de " + getOwner().getName();
    }

    @Override
    public @NotNull InventorySize getInventorySize() {
        return InventorySize.LARGEST;
    }

    @Override
    public @NotNull Map<Integer, ItemStack> getContent() {
        Map<Integer, ItemStack> content = new HashMap<>();

        // Slots de placement automatique (ligne par ligne)
        int[] slots = {10, 11, 12, 13, 14, 15, 16, 19, 20, 21, 22, 23, 24, 25, 28, 29, 30, 31, 32, 33, 34};
        int slotIndex = 0;

        // Génération automatique des items pour chaque paramètre
        for (SettingType settingType : SettingType.values()) {
            if (slotIndex >= slots.length) break; // Éviter de dépasser les slots disponibles

            content.put(slots[slotIndex], createSettingItem(settingType));
            slotIndex++;
        }

        // Bouton de fermeture
        content.put(40, new ItemBuilder(this, Material.BARRIER, meta -> {
            meta.displayName(Component.text("Fermer", NamedTextColor.RED)
                    .decoration(TextDecoration.ITALIC, false)
                    .decoration(TextDecoration.BOLD, true));
        }).setCloseButton());

        return content;
    }

    private ItemStack createSettingItem(SettingType settingType) {
        Object currentValue = settings.getSetting(settingType);

        return switch (settingType.getValueType()) {
            case BOOLEAN -> createBooleanItem(settingType, (Boolean) currentValue);
            case ENUM -> createEnumItem(settingType, currentValue);
            default -> throw new UnsupportedOperationException("Type de valeur non supporté: " + settingType.getValueType());
        };
    }

    private ItemStack createBooleanItem(SettingType settingType, boolean currentValue) {
        Material material = currentValue ? settingType.getEnabledMaterial() : settingType.getDisabledMaterial();

        return new ItemBuilder(this, material, meta -> {
            meta.displayName(Component.text(settingType.getName(), NamedTextColor.AQUA)
                    .decoration(TextDecoration.ITALIC, false));

            List<Component> lore = new ArrayList<>();
            lore.add(Component.text(currentValue ? "Activé" : "Désactivé",
                            currentValue ? NamedTextColor.GREEN : NamedTextColor.RED)
                    .decoration(TextDecoration.ITALIC, false));
            lore.add(Component.empty());
            lore.add(Component.text("Clique pour changer", NamedTextColor.GRAY)
                    .decoration(TextDecoration.ITALIC, false));
            meta.lore(lore);
        }).setOnClick(e -> {
            settings.setSetting(settingType, !currentValue);
            this.refresh();

            String statusText = currentValue ? "désactivé" : "activé";
            MessagesManager.sendMessage(getOwner(),
                    Component.text(settingType.getName() + " " + statusText, NamedTextColor.GREEN)
                            .decoration(TextDecoration.ITALIC, false),
                    Prefix.SETTINGS, MessageType.SUCCESS, true);
        });
    }

    private ItemStack createEnumItem(SettingType settingType, Object currentValue) {
        return new ItemBuilder(this, settingType.getEnabledMaterial(), meta -> {
            meta.displayName(Component.text(settingType.getName(), NamedTextColor.AQUA)
                    .decoration(TextDecoration.ITALIC, false));

            List<Component> lore = new ArrayList<>();
            if (settingType.getEnumDescription() != null) {
                lore.add(Component.text(settingType.getEnumDescription(), NamedTextColor.YELLOW)
                        .decoration(TextDecoration.ITALIC, false));
            }

            // Ajout des options avec indicateur de sélection
            addEnumOptions(lore, settingType, currentValue);

            lore.add(Component.empty());
            lore.add(Component.text(getEnumDescription(currentValue), NamedTextColor.GRAY)
                    .decoration(TextDecoration.ITALIC, false));
            lore.add(Component.empty());
            lore.add(Component.text("Clique pour changer", NamedTextColor.GRAY)
                    .decoration(TextDecoration.ITALIC, false));
            meta.lore(lore);
        }).setOnClick(e -> {
            Object nextValue = getNextEnumValue(settingType, currentValue);
            settings.setSetting(settingType, nextValue);
            this.refresh();

            MessagesManager.sendMessage(getOwner(),
                    Component.text(settingType.getName() + " mis à jour.", NamedTextColor.GREEN)
                            .decoration(TextDecoration.ITALIC, false),
                    Prefix.SETTINGS, MessageType.SUCCESS, true);
        });
    }

    private void addEnumOptions(List<Component> lore, SettingType settingType, Object currentValue) {
        Object[] values = getEnumValues(settingType);
        for (Object value : values) {
            Component prefix = value.equals(currentValue)
                    ? Component.text(" → ", NamedTextColor.GOLD).decoration(TextDecoration.ITALIC, false)
                    : Component.text("    ", NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false);

            lore.add(prefix.append(Component.text(getEnumDisplayName(value),
                            value.equals(currentValue) ? NamedTextColor.WHITE : NamedTextColor.GRAY)
                    .decoration(TextDecoration.ITALIC, false)));
        }
    }

    private Object[] getEnumValues(SettingType settingType) {
        return switch (settingType) {
            case FRIEND_REQUESTS_POLICY -> FriendPolicy.values();
            case CITY_JOIN_REQUESTS_POLICY -> CityPolicy.values();
            case PRIVATE_MESSAGE_POLICY -> GlobalPolicy.values();
            default -> new Object[0];
        };
    }

    private Object getNextEnumValue(SettingType settingType, Object currentValue) {
        Object[] values = getEnumValues(settingType);
        for (int i = 0; i < values.length; i++) {
            if (values[i].equals(currentValue)) {
                return values[(i + 1) % values.length];
            }
        }
        return values[0]; // Fallback
    }

    private String getEnumDisplayName(Object enumValue) {
        if (enumValue instanceof FriendPolicy policy) {
            return policy.getDisplayName();
        } else if (enumValue instanceof CityPolicy policy) {
            return policy.getDisplayName();
        } else if (enumValue instanceof GlobalPolicy level) {
            return level.getDisplayName();
        }
        return enumValue.toString();
    }

    private String getEnumDescription(Object enumValue) {
        if (enumValue instanceof FriendPolicy policy) {
            return policy.getDescription();
        } else if (enumValue instanceof CityPolicy policy) {
            return policy.getDescription();
        } else if (enumValue instanceof GlobalPolicy level) {
            return level.getDescription();
        }
        return "";
    }

    private void refresh() {
        new PlayerSettingsMenu(getOwner()).open();
    }

    @Override
    public void onInventoryClick(InventoryClickEvent e) {}

    @Override
    public void onClose(InventoryCloseEvent event) {}

    @Override
    public List<Integer> getTakableSlot() {
        return List.of();
    }

    // Classe pour stocker les informations d'affichage
    private static class SettingDisplayInfo {
        final Material enabledMaterial;
        final Material disabledMaterial;
        final String displayName;
        final String enumDescription;

        // Constructeur pour les paramètres booléens
        SettingDisplayInfo(Material enabledMaterial, Material disabledMaterial, String displayName) {
            this.enabledMaterial = enabledMaterial;
            this.disabledMaterial = disabledMaterial;
            this.displayName = displayName;
            this.enumDescription = null;
        }

        // Constructeur pour les paramètres enum
        SettingDisplayInfo(Material material, String displayName, String enumDescription) {
            this.enabledMaterial = material;
            this.disabledMaterial = material;
            this.displayName = displayName;
            this.enumDescription = enumDescription;
        }
    }
}