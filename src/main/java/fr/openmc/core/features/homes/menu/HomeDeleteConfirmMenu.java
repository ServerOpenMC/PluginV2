package fr.openmc.core.features.homes.menu;

import fr.openmc.api.menulib.Menu;
import fr.openmc.api.menulib.utils.InventorySize;
import fr.openmc.api.menulib.utils.ItemBuilder;
import fr.openmc.core.features.homes.models.Home;
import fr.openmc.core.features.homes.HomesManager;
import fr.openmc.core.utils.customitems.CustomItemRegistry;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class HomeDeleteConfirmMenu extends Menu {

    private final Home home;

    public HomeDeleteConfirmMenu(Player owner, Home home) {
        super(owner);
        this.home = home;
    }

    @Override
    public @NotNull String getName() {
        return PlaceholderAPI.setPlaceholders(this.getOwner(), "§r§f%img_offset_-8%%img_omc_homes_menus_home_delete%");
    }

    @Override
    public @NotNull InventorySize getInventorySize() {
        return InventorySize.SMALLEST;
    }

    @Override
    public @NotNull Map<Integer, ItemStack> getContent() {
        Map<Integer, ItemStack> content = new HashMap<>();
        Player player = getOwner();

            content.put(2, new ItemBuilder(
                            this,
                            Objects.requireNonNull(CustomItemRegistry.getByName("omc_homes:omc_homes_icon_bin_red")).getBest(),
                            itemMeta -> {
                                itemMeta.displayName(Component.text("§cConfirmer la suppression"));
                                itemMeta.lore(List.of(
                                        Component.text("§7■ §cClique §4gauche §cpour confirmer la suppression")
                                ));
                            }
                    ).setOnClick(event -> {
                        HomesManager.removeHome(home);
                        MessagesManager.sendMessage(player, Component.text("§aHome §e" + home.getName() + " §asupprimé avec succès !"), Prefix.HOME, MessageType.SUCCESS, true);
                        player.closeInventory();
                    })
            );

            content.put(4, new ItemBuilder(
                    this,
                    home.getIconItem(),
                    itemMeta -> itemMeta.displayName(Component.text("§a" + home.getName()))
            ));

            content.put(6, new ItemBuilder(
                    this,
                    Objects.requireNonNull(CustomItemRegistry.getByName("omc_homes:omc_homes_icon_bin")).getBest(),
                    itemMeta ->
                    itemMeta.displayName(Component.text("§aAnnuler la suppression"))).setBackButton()
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
