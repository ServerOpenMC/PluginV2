package fr.openmc.core.features.homes.menu;

import dev.lone.itemsadder.api.FontImages.FontImageWrapper;
import fr.openmc.api.menulib.Menu;
import fr.openmc.api.menulib.utils.InventorySize;
import fr.openmc.api.menulib.utils.ItemMenuBuilder;
import fr.openmc.api.menulib.utils.ItemUtils;
import fr.openmc.core.OMCRegistry;
import fr.openmc.core.features.homes.HomesManager;
import fr.openmc.core.features.homes.models.Home;
import fr.openmc.core.utils.text.messages.MessageType;
import fr.openmc.core.utils.text.messages.MessagesManager;
import fr.openmc.core.utils.text.messages.Prefix;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomeDeleteConfirmMenu extends Menu {

    private final Home home;

    public HomeDeleteConfirmMenu(Player owner, Home home) {
        super(owner);
        this.home = home;
    }

    @Override
    public @NotNull Component getName() {
        return TranslationManager.translation("feature.homes.delete.menu.title");
    }

    @Override
    public String getTexture() {
        return FontImageWrapper.replaceFontImages("§r§f:offset_-8::omc_homes_menus_home_delete:");
    }

    @Override
    public @NotNull InventorySize getInventorySize() {
        return InventorySize.SMALLEST;
    }

    @Override
    public @NotNull Map<Integer, ItemMenuBuilder> getContent() {
        Map<Integer, ItemMenuBuilder> content = new HashMap<>();
        Player player = getOwner();

            content.put(2, new ItemMenuBuilder(
                            this,
                            OMCRegistry.CUSTOM_ITEMS.HOMES_ICON_BIN_RED,
                            itemMeta -> {
                                itemMeta.displayName(TranslationManager.translation("feature.homes.delete.confirm.name"));
                                itemMeta.lore(TranslationManager.translationLore("feature.homes.delete.confirm.lore"));
                            }
                    ).setOnClick(event -> {
                        HomesManager.removeHome(home);
                        MessagesManager.sendMessage(
                                player,
                                TranslationManager.translation(
                                        "feature.homes.delete.success",
                                        Component.text(home.getName()).color(NamedTextColor.YELLOW)
                                ),
                                Prefix.HOME,
                                MessageType.SUCCESS,
                                true
                        );
                        player.closeInventory();
                    })
            );

            content.put(4, new ItemMenuBuilder(
                    this,
                    home.getIconItem(),
                    itemMeta -> itemMeta.displayName(TranslationManager.translation(
                            "feature.homes.home.name",
                            Component.text(home.getName()).color(NamedTextColor.GREEN)
                    ))
            ).hide(ItemUtils.getDataComponentType()));

            content.put(6, new ItemMenuBuilder(
                    this,
                    OMCRegistry.CUSTOM_ITEMS.HOMES_ICON_BIN, true)
            );

            return content;
    }

    @Override
    public void onInventoryClick(InventoryClickEvent inventoryClickEvent) {
    }

    @Override
    public void onClose(InventoryCloseEvent event) {
        //empty
    }

    @Override
    public List<Integer> getTakableSlot() {
        return List.of();
    }
}
