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
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
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
        return Component.text("Machine à boules");
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
                    itemMeta.displayName(Component.text("§eRécupérer mes tickets"));
                    itemMeta.lore(
                        List.of(
                            Component.text("§7Récupérer les tickets que"),
                            Component.text("§7vous avez récolté grâce à votre"),
                            Component.text("§7temps de jeu sur OpenMC V1.")
                    ));
                }
        ).setOnClick(
                e -> {
                    e.getWhoClicked().closeInventory();
                    if (stats == null) {
                        MessagesManager.sendMessage(getOwner(), Component.text("§cVous n'avez pas de statistique pour récupérer des tickets."), Prefix.OPENMC, MessageType.ERROR, true);
                        return;
                    }
                    if (stats.isTicketGiven()) {
                        MessagesManager.sendMessage(getOwner(), Component.text("§cVous avez déjà récupéré vos tickets !"), Prefix.OPENMC, MessageType.ERROR, true);
                        return;
                    }
                    int ticketsToGive = TicketManager.giveTicket(getOwner().getUniqueId());
                    if (ticketsToGive <= 0) {
                        MessagesManager.sendMessage(getOwner(), Component.text("§cVous n'avez pas de tickets à récupérer !"), Prefix.OPENMC, MessageType.ERROR, true);
                    } else {
                        MessagesManager.sendMessage(getOwner(), Component.text("§aVous avez reçu §e%s §atickets !".formatted(ticketsToGive)), Prefix.OPENMC, MessageType.SUCCESS, true);
                    }
                }
        ));

        items.put(6, new ItemMenuBuilder(
                this,
                Material.NETHER_STAR,
                itemMeta -> {
                    itemMeta.displayName(Component.text("§eOuvrir un ticket"));
                    itemMeta.lore(
                        List.of(
                            Component.text("§7Ouvrir une box avec 1 ticket."),
                            Component.text("§7Vous avez actuellement §e%s §7tickets.".formatted(tickets))
                    ));
                }
        ).setOnClick(
                e -> {
                    e.getWhoClicked().closeInventory();
                    if (tickets <= 0) {
                        MessagesManager.sendMessage(getOwner(), Component.text("§cVous n'avez pas assez de tickets !"), Prefix.OPENMC, MessageType.ERROR, true);
                        return;
                    }

                    if (TicketManager.useTicket(getOwner().getUniqueId())) {
                        OMCRegistry.CUSTOM_LOOTBOXES.get("omc:machine_ball").open(getOwner());
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
