package fr.openmc.core.features.events.contents.halloween.menus;

import fr.openmc.api.menulib.Menu;
import fr.openmc.api.menulib.utils.InventorySize;
import fr.openmc.api.menulib.utils.ItemBuilder;
import fr.openmc.core.features.events.contents.halloween.managers.HalloweenManager;
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

public class HalloweenPumpkinDepositMenu extends Menu {
    public HalloweenPumpkinDepositMenu(Player owner) {
        super(owner);
    }

    @Override
    public @NotNull Component getName() {
        return TranslationManager.translation("feature.events.halloween.menu.deposit.title");
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
    public @NotNull Map<Integer, ItemBuilder> getContent() {
        return Map.of(
                13,
                new ItemBuilder(this, Material.PUMPKIN, meta -> {
                    meta.itemName(TranslationManager.translation("feature.events.halloween.menu.deposit.button.name"));
                    meta.lore(TranslationManager.translationLore("feature.events.halloween.menu.deposit.button.lore"));
                    meta.setEnchantmentGlintOverride(true);
                }).setOnClick(event -> {
                    Player player = (Player) event.getWhoClicked();
                    int pumpkinCount = ItemUtils.removeItemsFromInventory(player, Material.PUMPKIN, Integer.MAX_VALUE);
                    if (pumpkinCount == 0) {
                        MessagesManager.sendMessage(
                                player,
                                TranslationManager.translation("feature.events.halloween.menu.deposit.no_pumpkin"),
                                Prefix.HALLOWEEN,
                                MessageType.ERROR,
                                false
                        );

                        player.closeInventory();
                        return;
                    }


                    HalloweenManager.depositPumpkins(player.getUniqueId(), pumpkinCount);
                    MessagesManager.sendMessage(
                            player,
                            TranslationManager.translation(
                                    "feature.events.halloween.menu.deposit.success",
                                    Component.text(pumpkinCount).color(TextColor.color(255, 107, 37)).decorate(TextDecoration.BOLD)
                            ),
                            Prefix.HALLOWEEN,
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
