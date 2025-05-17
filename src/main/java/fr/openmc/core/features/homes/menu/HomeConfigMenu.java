package fr.openmc.core.features.homes.menu;

import dev.lone.itemsadder.api.CustomStack;
import dev.lone.itemsadder.api.FontImages.FontImageWrapper;
import fr.openmc.api.menulib.Menu;
import fr.openmc.api.menulib.utils.InventorySize;
import fr.openmc.api.menulib.utils.ItemBuilder;
import fr.openmc.core.features.homes.Home;
import fr.openmc.core.features.homes.utils.HomeUtil;
import fr.openmc.core.features.mailboxes.utils.MailboxMenuManager;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import fr.openmc.core.utils.customitems.CustomItemRegistry;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomeConfigMenu extends Menu {

    private final Home home;

    public HomeConfigMenu(Player owner, Home home) {
        super(owner);
        this.home = home;
    }

    @Override
    public @NotNull String getName() {
        return PlaceholderAPI.setPlaceholders(this.getOwner(), "§r§f%img_offset_-8%%img_omc_homes_menus_home_settings%");
    }

    @Override
    public @NotNull InventorySize getInventorySize() {
        return InventorySize.LARGER;
    }

    @Override
    public @NotNull Map<Integer, ItemStack> getContent() {
        Map<Integer, ItemStack> content = new HashMap<>();
        Player player = getOwner();

        try {
            content.put(4, home.getIconItem());

            content.put(20, new ItemBuilder(this, HomeUtil.getRandomsIcons(),itemMeta -> {
                itemMeta.setDisplayName("§aChanger l'icône");
                itemMeta.setLore(List.of(ChatColor.GRAY + "■ §aClique §2gauche §apour changer l'icône de votre home"));
            }).setNextMenu(new HomeChangeIconMenu(player, home)));

            //TODO mettre un font de "omc_homes:bin" avant '§cSupprimer le home'
            content.put(24, new ItemBuilder(this, CustomItemRegistry.getByName("omc_homes:omc_homes_icon_bin_red").getBest(), itemMeta -> {
                itemMeta.setDisplayName("§cSupprimer le home");
                itemMeta.setLore(List.of(ChatColor.GRAY + "■ §cClique §4gauche §cpour supprimer votre home"));
            }).setNextMenu(new HomeDeleteConfirmMenu(getOwner(), home)));

            content.put(36, new ItemBuilder(this, MailboxMenuManager.previousPageBtn()).setNextMenu(new HomeMenu(player)));
            content.put(44, new ItemBuilder(this, MailboxMenuManager.cancelBtn()).setCloseButton());

            return content;
        } catch (Exception e) {
            MessagesManager.sendMessage(player, Component.text("§cUne Erreur est survenue, veuillez contacter le Staff"), Prefix.OPENMC, MessageType.ERROR, false);
            player.closeInventory();
            e.printStackTrace();
        }
        return content;
    }

    @Override
    public void onInventoryClick(InventoryClickEvent inventoryClickEvent) {}

    @Override
    public void onClose(InventoryCloseEvent event) {
        //empty
    }

    @Override
    public List<Integer> getTakableSlot() {
        return List.of();
    }
}
