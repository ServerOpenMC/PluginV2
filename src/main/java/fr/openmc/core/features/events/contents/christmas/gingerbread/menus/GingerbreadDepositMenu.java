package fr.openmc.core.features.events.contents.christmas.gingerbread.menus;

import fr.openmc.api.menulib.Menu;
import fr.openmc.api.menulib.utils.InventorySize;
import fr.openmc.api.menulib.utils.ItemMenuBuilder;
import fr.openmc.core.features.events.contents.christmas.gingerbread.managers.GingerBreadManager;
import fr.openmc.core.features.events.contents.halloween.pumpkin.managers.HalloweenManager;
import fr.openmc.core.utils.bukkit.ItemUtils;
import fr.openmc.core.utils.text.messages.MessageType;
import fr.openmc.core.utils.text.messages.MessagesManager;
import fr.openmc.core.utils.text.messages.Prefix;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class GingerbreadDepositMenu extends Menu {
    public GingerbreadDepositMenu(Player owner) {
        super(owner);
    }

    @Override
    public @NotNull Component getName() {
        return TranslationManager.translation("feature.events.gingerbread.menu.deposit.title");
    }

    @Override
    public String getTexture() {
        return null;
    }

    @Override
    public @NotNull InventorySize getInventorySize() {
        return InventorySize.NORMAL;
    }

    @Override
    public @NotNull Map<Integer, ItemMenuBuilder> getContent() {
        return Map.of(
                13,
                new ItemMenuBuilder(this, Material.PUMPKIN, meta -> {
                    meta.itemName(TranslationManager.translation("feature.events.gingerbread.menu.deposit.button.name"));
                    meta.lore(TranslationManager.translationLore("feature.events.gingerbread.menu.deposit.button.lore"));
                    meta.setEnchantmentGlintOverride(true);
                }).setOnClick(event -> {
                    Player player = (Player) event.getWhoClicked();
                    int gingerbreadCount = ItemUtils.removeItemsFromPlayerInventory(player, Material.PUMPKIN, Integer.MAX_VALUE);
                    if (gingerbreadCount == 0) {
                        MessagesManager.sendMessage(
                                player,
                                TranslationManager.translation("feature.events.gingerbread.menu.deposit.no_pumpkin"),
                                Prefix.CHRISTMAS,
                                MessageType.ERROR,
                                false
                        );

                        player.closeInventory();
                        return;
                    }

                    GingerBreadManager.depositGingerbread(player.getUniqueId(), gingerbreadCount);
                    MessagesManager.sendMessage(
                            player,
                            TranslationManager.translation(
                                    "feature.events.gingerbread.menu.deposit.success",
                                    Component.text(gingerbreadCount).color(TextColor.color(255, 107, 37)).decorate(TextDecoration.BOLD)
                            ),
                            Prefix.CHRISTMAS,
                            MessageType.SUCCESS,
                            false
                    );

                    player.closeInventory();
                })
        );
    }

    @Override
    public List<Integer> getTakableSlot() {
        return Collections.emptyList();
    }

    @Override
    public void onInventoryClick(InventoryClickEvent e) {
        // Not used
    }

    @Override
    public void onClose(InventoryCloseEvent event) {
        // Not used
    }
}
