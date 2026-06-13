package fr.openmc.core.features.homes.menu;

import dev.lone.itemsadder.api.FontImages.FontImageWrapper;
import fr.openmc.api.input.dialog.DialogInput;
import fr.openmc.api.menulib.Menu;
import fr.openmc.api.menulib.template.ItemMenuTemplate;
import fr.openmc.api.menulib.utils.InventorySize;
import fr.openmc.api.menulib.utils.ItemMenuBuilder;
import fr.openmc.api.menulib.utils.ItemUtils;
import fr.openmc.core.OMCRegistry;
import fr.openmc.core.features.homes.HomesManager;
import fr.openmc.core.features.homes.models.Home;
import fr.openmc.core.features.homes.utils.HomeUtil;
import fr.openmc.core.utils.text.messages.MessageType;
import fr.openmc.core.utils.text.messages.MessagesManager;
import fr.openmc.core.utils.text.messages.Prefix;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static fr.openmc.core.features.homes.utils.HomeUtil.MAX_LENGTH_HOME_NAME;

public class HomeConfigMenu extends Menu {

    private final Home home;

    public HomeConfigMenu(Player owner, Home home) {
        super(owner);
        this.home = home;
    }

    @Override
    public @NotNull Component getName() {
        return TranslationManager.translation("feature.homes.config.title");
    }

    @Override
    public String getTexture() {
        return FontImageWrapper.replaceFontImages("§r§f:offset_-8::omc_homes_menus_home_settings:");
    }

    @Override
    public @NotNull InventorySize getInventorySize() {
        return InventorySize.LARGER;
    }

    @Override
    public @NotNull Map<Integer, ItemMenuBuilder> getContent() {
        Map<Integer, ItemMenuBuilder> content = new HashMap<>();
        Player player = getOwner();

        content.put(4, new ItemMenuBuilder(this, home.getIconItem()).hide(ItemUtils.getDataComponentType()));

        content.put(20, new ItemMenuBuilder(this, home.getIcon().getItemStack(), itemMeta -> {
            itemMeta.displayName(TranslationManager.translation("feature.homes.config.change_icon.name"));
            itemMeta.lore(TranslationManager.translationLore("feature.homes.config.change_icon.lore"));
        }).hide(ItemUtils.getDataComponentType()).setOnClick(inventoryClickEvent -> new HomeChangeIconMenu(player, home).open()));

        content.put(22, new ItemMenuBuilder(this, Material.NAME_TAG, itemMeta -> {
            itemMeta.displayName(TranslationManager.translation("feature.homes.config.rename.name"));
            itemMeta.lore(TranslationManager.translationLore("feature.homes.config.rename.lore"));
        }).setOnClick(e -> DialogInput.send(getOwner(), TranslationManager.translation("feature.homes.config.rename.prompt"), MAX_LENGTH_HOME_NAME, input -> {
            if (input == null) return;

            if (!HomeUtil.isValidHomeName(input)) return;

            if (HomesManager.getHomesNames(getOwner().getUniqueId()).contains(input)) {
                MessagesManager.sendMessage(player, TranslationManager.translation("feature.homes.command.rename.already_has"), Prefix.HOME, MessageType.ERROR, true);
                return;
            }

            MessagesManager.sendMessage(player, TranslationManager.translation(
                    "feature.homes.config.rename.success",
                    Component.text(home.getName()).color(NamedTextColor.YELLOW),
                    Component.text(input).color(NamedTextColor.YELLOW)
            ), Prefix.HOME, MessageType.SUCCESS, true);
            HomesManager.renameHome(home, input);
        })));

        content.put(24, new ItemMenuBuilder(this, OMCRegistry.CUSTOM_ITEMS.HOMES_ICON_BIN_RED, itemMeta -> {
            itemMeta.displayName(TranslationManager.translation("feature.homes.config.delete.name"));
            itemMeta.lore(TranslationManager.translationLore("feature.homes.config.delete.lore"));
        }).setOnClick(_ -> new HomeDeleteConfirmMenu(getOwner(), home).open()));

        content.put(36, ItemMenuTemplate.BTN_PREVIOUS_PAGE_WHITE.apply(this).setBackButton());
        content.put(44, ItemMenuTemplate.BTN_CLOSE.apply(this));

        return content;
    }

    @Override
    public void onInventoryClick(InventoryClickEvent inventoryClickEvent) {}

    @Override
    public void onClose(InventoryCloseEvent event) {}

    @Override
    public List<Integer> getTakableSlot() {
        return List.of();
    }
}
