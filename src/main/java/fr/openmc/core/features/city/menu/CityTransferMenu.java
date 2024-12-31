package fr.openmc.core.features.city.menu;


import dev.xernas.menulib.PaginatedMenu;
import dev.xernas.menulib.utils.ItemBuilder;
import dev.xernas.menulib.utils.ItemUtils;
import dev.xernas.menulib.utils.StaticSlots;
import fr.openmc.core.features.city.CPermission;
import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.utils.menu.ConfirmMenu;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class CityTransferMenu extends PaginatedMenu {

    public CityTransferMenu(Player owner) {
        super(owner);
    }

    @Override
    public @Nullable Material getBorderMaterial() {
        return Material.LIGHT_GRAY_STAINED_GLASS_PANE;
    }

    @Override
    public @NotNull List<Integer> getStaticSlots() {
        return StaticSlots.BOTTOM;
    }

    City cityTransfer;
    OfflinePlayer playerTransfer;
    Player playerWhoTransfer;

    @Override
    public @NotNull List<ItemStack> getItems() {
        Player player = getOwner();
        City city = CityManager.getPlayerCity(player.getUniqueId());
        assert city != null;

        boolean hasPermissionOwner = city.hasPermission(player.getUniqueId(), CPermission.OWNER);

        List<ItemStack> items = new ArrayList<>();
        for (UUID uuid : city.getMembers()) {
            if (uuid.equals(city.getPlayerWith(CPermission.OWNER))) {
                // Ignore l'owner et continue avec le prochain membre
                continue;
            }

            OfflinePlayer playerOffline = Bukkit.getOfflinePlayer(uuid);

            items.add(new ItemBuilder(this, ItemUtils.getPlayerSkull(uuid), itemMeta -> {
                itemMeta.displayName(Component.text("Membre " + playerOffline.getName()).decoration(TextDecoration.ITALIC, false));
                itemMeta.lore(List.of(
                        Component.text("§7Voulez-vous donner à §d" + playerOffline.getName() + " §7votre ville ?"),
                        Component.text("§e§lCLIQUEZ ICI POUR CONFIRMER")
                ));
            }).setOnClick(inventoryClickEvent -> {
                if (!hasPermissionOwner) {
                    MessagesManager.sendMessageType(player, MessagesManager.Message.PLAYERNOOWNER.getMessage(), Prefix.CITY, MessageType.ERROR, false);
                    return;
                }

                cityTransfer = city;
                playerTransfer = playerOffline;
                playerWhoTransfer = player;

                ConfirmMenu menu = new ConfirmMenu(player, null, this::acceptTransfer, this::refuseTransfer,
                        "§7Voulez-vous vraiment donner la ville à " + playerOffline.getName() + " ?",
                        "§7Vous allez garder la ville " + playerOffline.getName());
                menu.open();
            }));
        }
        return items;
    }

    @Override
    public Map<Integer, ItemStack> getButtons() {
        Map<Integer, ItemStack> map = new HashMap<>();
        map.put(49, new ItemBuilder(this, Material.PAPER, itemMeta -> {
            itemMeta.displayName(Component.text("§7Fermer"));
            itemMeta.setCustomModelData(10003);
        }).setCloseButton());
        map.put(48, new ItemBuilder(this, Material.PAPER, itemMeta -> {
            itemMeta.displayName(Component.text("§cPage précédente"));
            itemMeta.setCustomModelData(10005);
        }).setPreviousPageButton());
        map.put(50, new ItemBuilder(this, Material.PAPER, itemMeta -> {
            itemMeta.displayName(Component.text("§aPage suivante"));
            itemMeta.setCustomModelData(10006);
    }).setNextPageButton());
        return map;
    }

    @Override
    public @NotNull String getName() {
        return "Menu des Villes - Transferer";
    }

    @Override
    public void onInventoryClick(InventoryClickEvent inventoryClickEvent) {
        //empty
    }

    private void acceptTransfer() {
        cityTransfer.changeOwner(playerTransfer.getUniqueId());
        MessagesManager.sendMessageType(playerWhoTransfer, Component.text("Le nouveau maire est "+ playerTransfer.getName()), Prefix.CITY, MessageType.SUCCESS, false);

        if (playerTransfer.isOnline()) {
            MessagesManager.sendMessageType((Player) playerTransfer, Component.text("Vous êtes devenu le maire de la ville"), Prefix.CITY, MessageType.INFO, true);
        }
    }

    private void refuseTransfer() {
        //empty
    }
}
