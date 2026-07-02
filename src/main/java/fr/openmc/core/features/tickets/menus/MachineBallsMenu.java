package fr.openmc.core.features.tickets.menus;

import fr.openmc.api.menulib.Menu;
import fr.openmc.api.menulib.utils.InventorySize;
import fr.openmc.api.menulib.utils.ItemMenuBuilder;
import fr.openmc.core.OMCRegistry;
import fr.openmc.core.features.tickets.PlayerStats;
import fr.openmc.core.features.tickets.TicketManager;
import fr.openmc.core.utils.text.messages.MessageType;
import fr.openmc.core.utils.text.messages.MessagesManager;
import fr.openmc.core.utils.text.messages.Prefix;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
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

public class MachineBallsMenu extends Menu {

    public MachineBallsMenu(Player owner) {
        super(owner);
    }

    @Override
    public @NotNull Component getName() {
        return TranslationManager.translation("feature.tickets.machine.title");
    }

    @Override
    public String getTexture() {
        return null;
    }

    @Override
    public @NotNull InventorySize getInventorySize() {
        return InventorySize.SMALLEST;
    }

    @Override
    public @NotNull Map<Integer, ItemMenuBuilder> getContent() {
        Map<Integer, ItemMenuBuilder> items = new HashMap<>();

        PlayerStats stats = TicketManager.getPlayerStats(getOwner().getUniqueId());
        int tickets = stats != null ? stats.getTicketRemaining() : 0;

        items.put(2, new ItemMenuBuilder(
                this,
                Material.PAPER,
                itemMeta -> {
                    itemMeta.displayName(TranslationManager.translation("feature.tickets.menu.get_tickets.title"));
                    itemMeta.lore(
                        List.of(
                            TranslationManager.translation("feature.tickets.menu.get_tickets.lore1"),
                            TranslationManager.translation("feature.tickets.menu.get_tickets.lore2"),
                            TranslationManager.translation("feature.tickets.menu.get_tickets.lore3")
                    ));
                }
        ).setOnClick(
                e -> {
                    e.getWhoClicked().closeInventory();
                    if (stats == null) {
                        MessagesManager.sendMessage(getOwner(), TranslationManager.translation("feature.tickets.menu.no_stats"), Prefix.OPENMC, MessageType.ERROR, true);
                        return;
                    }
                    if (stats.isTicketGiven()) {
                        MessagesManager.sendMessage(getOwner(), TranslationManager.translation("feature.tickets.menu.already_claimed"), Prefix.OPENMC, MessageType.ERROR, true);
                        return;
                    }
                    int ticketsToGive = TicketManager.giveTicket(getOwner().getUniqueId());
                    if (ticketsToGive <= 0) {
                        MessagesManager.sendMessage(getOwner(), TranslationManager.translation("feature.tickets.menu.no_tickets"), Prefix.OPENMC, MessageType.ERROR, true);
                    } else {
                        MessagesManager.sendMessage(getOwner(), TranslationManager.translation("feature.tickets.menu.claimed",
                                Component.text(ticketsToGive, NamedTextColor.YELLOW)), Prefix.OPENMC, MessageType.SUCCESS, true);
                    }
                }
        ));

        items.put(6, new ItemMenuBuilder(
                this,
                Material.NETHER_STAR,
                itemMeta -> {
                    itemMeta.displayName(TranslationManager.translation("feature.tickets.menu.open_ticket.title"));
                    itemMeta.lore(
                        List.of(
                            TranslationManager.translation("feature.tickets.menu.open_ticket.lore1"),
                            TranslationManager.translation("feature.tickets.menu.open_ticket.lore2", Component.text(tickets))
                    ));
                }
        ).setOnClick(
                e -> {
                    e.getWhoClicked().closeInventory();
                    if (tickets <= 0) {
                        MessagesManager.sendMessage(getOwner(), TranslationManager.translation("feature.tickets.menu.not_enough_tickets"), Prefix.OPENMC, MessageType.ERROR, true);
                        return;
                    }

                    if (TicketManager.useTicket(getOwner().getUniqueId())) {
                        OMCRegistry.CUSTOM_LOOTBOXES.MACHINE_BALL.open(getOwner());
                    }
                }
        ));

        return items;
    }

    @Override
    public void onInventoryClick(InventoryClickEvent e) {}

    @Override
    public void onClose(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();
        player.playSound(Sound.sound(Key.key("minecraft", "block.barrel.close"), Sound.Source.BLOCK, 1f, 1f));
    }

    @Override
    public List<Integer> getTakableSlot() {
        return List.of();
    }
}
