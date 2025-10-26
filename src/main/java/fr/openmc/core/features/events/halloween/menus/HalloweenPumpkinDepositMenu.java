package fr.openmc.core.features.events.halloween.menus;

import fr.openmc.api.menulib.Menu;
import fr.openmc.api.menulib.utils.InventorySize;
import fr.openmc.api.menulib.utils.ItemBuilder;
import fr.openmc.core.features.events.halloween.HalloweenManager;
import fr.openmc.core.utils.ItemUtils;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
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
    public @NotNull String getName() {
        return "Déposer vos citrouilles";
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
                    meta.itemName(Component.text("Déposer vos citrouilles", TextColor.color(255, 107, 37)));
                    meta.lore(List.of(
                            Component.text("Cliquez ici pour déposer vos citrouilles et", NamedTextColor.GRAY),
                            Component.text("obtenir des récompenses !", NamedTextColor.GRAY)
                    ));
                    meta.setEnchantmentGlintOverride(true);
                }).setOnClick(event -> {
                    Player player = (Player) event.getWhoClicked();
                    int pumpkinCount = ItemUtils.removeItemsFromInventory(player, Material.PUMPKIN, Integer.MAX_VALUE);

                    HalloweenManager.depositPumpkins(player.getUniqueId(), pumpkinCount);
                    MessagesManager.sendMessage(
                            player,
                            Component.text("Vous avez déposé ", NamedTextColor.GOLD)
                                    .append(Component.text(pumpkinCount, TextColor.color(255, 107, 37), TextDecoration.BOLD))
                                    .append(Component.text(" citrouilles !", NamedTextColor.GOLD)),
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
