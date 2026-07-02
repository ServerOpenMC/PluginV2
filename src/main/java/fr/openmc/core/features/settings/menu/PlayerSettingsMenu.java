package fr.openmc.core.features.settings.menu;

import dev.lone.itemsadder.api.FontImages.FontImageWrapper;
import fr.openmc.api.menulib.PaginatedMenu;
import fr.openmc.api.menulib.template.ConfirmMenu;
import fr.openmc.api.menulib.template.ItemMenuTemplate;
import fr.openmc.api.menulib.utils.InventorySize;
import fr.openmc.api.menulib.utils.ItemMenuBuilder;
import fr.openmc.core.OMCRegistry;
import fr.openmc.core.features.settings.PlayerSettings;
import fr.openmc.core.features.settings.PlayerSettingsManager;
import fr.openmc.core.features.settings.SettingType;
import fr.openmc.core.features.settings.policy.Policy;
import fr.openmc.core.utils.text.messages.MessageType;
import fr.openmc.core.utils.text.messages.MessagesManager;
import fr.openmc.core.utils.text.messages.Prefix;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

public class PlayerSettingsMenu extends PaginatedMenu {

    private final PlayerSettings settings;

    public PlayerSettingsMenu(Player player) {
        super(player);
        this.settings = PlayerSettingsManager.getPlayerSettings(player);
    }

    @Override
    public @NotNull InventorySize getInventorySize() {
        return InventorySize.LARGEST;
    }

    @Override
    public int getSizeOfItems() {
        return getItems().size();
    }

    @Override
    public @Nullable Material getBorderMaterial() {
        return null;
    }

    @Override
    public @NotNull List<Integer> getStaticSlots() {
        return IntStream.rangeClosed(45, 53).boxed().toList();
    }

    @Override
    public Map<Integer, ItemMenuBuilder> getButtons() {
        Map<Integer, ItemMenuBuilder> buttons = new HashMap<>();

        buttons.put(45, new ItemMenuBuilder(this, OMCRegistry.CUSTOM_ITEMS.HOMES_ICON_BIN_RED, meta -> {
            meta.displayName(TranslationManager.translation("feature.settings.reset.title")
                    .decoration(TextDecoration.ITALIC, false));
        }).setOnClick(_ ->
                new ConfirmMenu(getOwner(), () -> {
                    settings.resetAllSettings();
                    this.refresh();
                    MessagesManager.sendMessage(getOwner(),
                            TranslationManager.translation("feature.settings.reset.success")
                                    .decoration(TextDecoration.ITALIC, false),
                            Prefix.SETTINGS, MessageType.SUCCESS, true);
                    },
                    this::open,
                    List.of(
                        TranslationManager.translation("feature.settings.reset.confirm_message")
                                .decoration(TextDecoration.ITALIC, false),
                        TranslationManager.translation("feature.settings.reset.warning")
                                .decoration(TextDecoration.ITALIC, false)),
                    List.of(
                        TranslationManager.translation("feature.settings.reset.cancel")
                                .decoration(TextDecoration.ITALIC, false))
                ).open()
        ));

        buttons.put(48, ItemMenuTemplate.BTN_PREVIOUS_PAGE_WHITE.apply(this));
        buttons.put(49, ItemMenuTemplate.BTN_CLOSE.apply(this));
        buttons.put(50, ItemMenuTemplate.BTN_NEXT_PAGE_WHITE.apply(this));

        return buttons;
    }

    @Override
    public @NotNull Component getName() {
        return TranslationManager.translation("feature.settings.menu.title");
    }

    @Override
    public String getTexture() {
        return FontImageWrapper.replaceFontImages("§r§f:offset_-8::settings:");
    }

    @Override
    public List<ItemStack> getItems() {
        List<ItemStack> content = new ArrayList<>();

        for (SettingType settingType : SettingType.values()) {
            content.add(createSettingItem(settingType));
        }

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

        return new ItemMenuBuilder(this, material, meta -> {
            meta.displayName(TranslationManager.translation(settingType.getName()).color(NamedTextColor.AQUA)
                    .decoration(TextDecoration.ITALIC, false));

            List<Component> lore = new ArrayList<>();
            lore.add(TranslationManager.translation(currentValue ? "feature.settings.item.status_enabled" : "feature.settings.item.status_disabled")
                    .decoration(TextDecoration.ITALIC, false));
            lore.add(Component.empty());
            lore.add(TranslationManager.translation("feature.settings.item.change")
                    .decoration(TextDecoration.ITALIC, false));
            meta.lore(lore);
        }).hide(settingType.getDataComponentType()).setOnClick(e -> {
            settings.setSetting(settingType, !currentValue);
            this.refresh();

            MessagesManager.sendMessage(getOwner(),
                    TranslationManager.translation("feature.settings.item.changed",
                            TranslationManager.translation(settingType.getName()),
                            TranslationManager.translation(currentValue ? "feature.settings.item.status_disabled" : "feature.settings.item.status_enabled"))
                            .decoration(TextDecoration.ITALIC, false),
                    Prefix.SETTINGS, MessageType.SUCCESS, true);
        });
    }

    private ItemStack createEnumItem(SettingType settingType, Object currentValue) {
        return new ItemMenuBuilder(this, settingType.getEnabledMaterial(), meta -> {
            meta.displayName(TranslationManager.translation(settingType.getName()).color(NamedTextColor.AQUA)
                    .decoration(TextDecoration.ITALIC, false));

            List<Component> lore = new ArrayList<>();
            if (settingType.getEnumDescription() != null) {
                lore.add(TranslationManager.translation(settingType.getEnumDescription()).color(NamedTextColor.YELLOW)
                        .decoration(TextDecoration.ITALIC, false));
            }

            addEnumOptions(lore, settingType, currentValue);

            lore.add(Component.empty());
            lore.add(getEnumDescription(currentValue).color(NamedTextColor.GRAY)
                    .decoration(TextDecoration.ITALIC, false));
            lore.add(Component.empty());
            lore.add(TranslationManager.translation("feature.settings.item.change")
                    .decoration(TextDecoration.ITALIC, false));
            meta.lore(lore);
        }).setOnClick(e -> {
            Object nextValue = getNextEnumValue(settingType, currentValue);
            settings.setSetting(settingType, nextValue);
            this.refresh();

            MessagesManager.sendMessage(getOwner(),
                    TranslationManager.translation("feature.settings.item.updated", TranslationManager.translation(settingType.getName()))
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

            lore.add(prefix.append(getEnumDisplayName(value)
                            .color(value.equals(currentValue) ? NamedTextColor.WHITE : NamedTextColor.GRAY)
                    .decoration(TextDecoration.ITALIC, false)));
        }
    }

    private Object[] getEnumValues(SettingType settingType) {
        return settingType.getDefaultValue().getClass().getEnumConstants();
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

    private Component getEnumDisplayName(Object enumValue) {
        if (enumValue instanceof Policy policy) {
            return TranslationManager.translation(policy.getDisplayName());
        }
        return Component.text(enumValue.toString());
    }

    private Component getEnumDescription(Object enumValue) {
        if (enumValue instanceof Policy policy) {
            return TranslationManager.translation(policy.getDescription());
        }
        return Component.empty();
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
}
