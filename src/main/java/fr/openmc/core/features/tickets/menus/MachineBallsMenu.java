package fr.openmc.core.features.tickets.menus;

import fr.openmc.api.menulib.Menu;
import fr.openmc.api.menulib.utils.InventorySize;
import fr.openmc.api.menulib.utils.ItemBuilder;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.tickets.PlayerStats;
import fr.openmc.core.features.tickets.TicketManager;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
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
    public @NotNull String getName() {
        return "Machine à boules";
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
    public @NotNull Map<Integer, ItemBuilder> getContent() {
        Map<Integer, ItemBuilder> items = new HashMap<>();

        items.put(2, new ItemBuilder(
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
                    if (TicketManager.getPlayerStats(getOwner().getUniqueId()) == null) {
                        MessagesManager.sendMessage(getOwner(), Component.text("§cVous n'avez pas de statistique pour récupérer des tickets."), Prefix.OPENMC, MessageType.ERROR, true);
                        return;
                    }
                    if (TicketManager.getPlayerStats(getOwner().getUniqueId()).isTicketGiven()) {
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

        items.put(6, new ItemBuilder(
                this,
                Material.NETHER_STAR,
                itemMeta -> {
                    itemMeta.displayName(Component.text("§eOuvrir un ticket"));
                    itemMeta.lore(
                        List.of(
                            Component.text("§7Ouvrir une box avec 1 ticket."),
                            Component.text("§7Vous avez actuellement §e%s §7tickets.".formatted(TicketManager.getPlayerStats(getOwner().getUniqueId()).getTicketRemaining()))
                    ));
                }
        ).setOnClick(
                e -> {
                    e.getWhoClicked().closeInventory();
                    if (TicketManager.getPlayerStats(getOwner().getUniqueId()).getTicketRemaining() <= 0) {
                        MessagesManager.sendMessage(getOwner(), Component.text("§cVous n'avez pas assez de tickets !"), Prefix.OPENMC, MessageType.ERROR, true);
                        return;
                    }
                    MachineBallsOpenMenu menu = new MachineBallsOpenMenu(getOwner());
                    menu.open();
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
