package fr.openmc.core.features.homes.menu;

import dev.lone.itemsadder.api.CustomStack;
import fr.openmc.api.menulib.PaginatedMenu;
import fr.openmc.api.menulib.utils.ItemBuilder;
import fr.openmc.core.features.homes.Home;
import fr.openmc.core.features.homes.HomesManager;
import fr.openmc.core.features.homes.utils.HomeUtil;
import fr.openmc.core.features.mailboxes.utils.MailboxMenuManager;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomeMenu extends PaginatedMenu {

    private final OfflinePlayer target;
    private boolean wasTarget = false;
    public HomeMenu(Player player, OfflinePlayer target) {
        super(player);
        this.target = target;
        this.wasTarget = true;
    }

    public HomeMenu(Player player) {
        super(player);
        this.target = player;
    }

    @Override
    public @NotNull String getName() {
        return PlaceholderAPI.setPlaceholders(this.getOwner(), "§r§f%img_offset_-8%%img_omc_homes_menus_home%");
    }

    @Override
    public @Nullable Material getBorderMaterial() {
        return Material.BLUE_STAINED_GLASS_PANE;
    }

    @Override
    public @NotNull List<Integer> getStaticSlots() {
        List<Integer> staticSlots = new ArrayList<>();
        staticSlots.add(45);
        staticSlots.add(48);
        staticSlots.add(49);
        staticSlots.add(50);
        staticSlots.add(53);

        return staticSlots;
    }

    @Override
    public @NotNull List<ItemStack> getItems() {
        List<ItemStack> items = new ArrayList<>();

        for(Home home : HomesManager.getHomes(target.getUniqueId())) {
            items.add(new ItemBuilder(this, HomeUtil.getHomeIconItem(home), itemMeta -> {
                itemMeta.setDisplayName("§e" + home.getName());
                itemMeta.setLore(List.of(
                        ChatColor.GRAY + "■ §aClique §2gauche pour vous téléporter",
                        ChatColor.GRAY + "■ §cCliquez §4droit §cpour configurer le home"
                ));
            }).setOnClick(event -> {
                if(event.isLeftClick()) {
                    this.getInventory().close();
                    MessagesManager.sendMessage(getOwner(), Component.text("§aVous avez été téléporté à votre home §e" + home.getName() + "§a."), Prefix.HOME, MessageType.SUCCESS, true);
                    getOwner().teleport(home.getLocation());
                } else if(event.isRightClick()) {
                    Player player = (Player) event.getWhoClicked();
                    new HomeConfigMenu(player, home).open();
                }
            }));
        }

        return items;
    }

    @Override
    public Map<Integer, ItemStack> getButtons() {
        Map<Integer, ItemStack> map = new HashMap<>();

        if(!wasTarget) {
            map.put(45, new ItemBuilder(this, CustomStack.getInstance("omc_homes:omc_homes_icon_information").getItemStack(),
                    itemMeta -> {
                        itemMeta.setDisplayName("§8(§bⓘ§8) §6Informations sur vos homes");
                        itemMeta.setLore(List.of(
                                "§8→ §6Chaque icon qui représente un home est lié au nom du home, par exemple, si vous appelé votre home 'maison', l'icône sera une maison",
                                "§7",
                                "§8› §6Vous pouvez configurer le home en effectuant un clique droit sur l'icône du home.",
                                "§8› §6Vous pouvez vous téléporter à votre home en effectuant un clique gauche sur l'icône du home."
                        ));
                    }
                )
            );

            map.put(53, new ItemBuilder(this, CustomStack.getInstance("omc_homes:omc_homes_icon_upgrade").getItemStack(), itemMeta -> {
                itemMeta.setDisplayName("§8● §6Améliorer les homes §8(Click ici)");
                itemMeta.setLore(List.of(
                    "§6Cliquez pour améliorer vos homes"
                ));
            }).setOnClick(event -> new HomeUpgradeMenu(getOwner()).open()));
        }

        map.put(48, new ItemBuilder(this, MailboxMenuManager.previousPageBtn()).setPreviousPageButton());
        map.put(49, new ItemBuilder(this, MailboxMenuManager.cancelBtn()).setCloseButton());
        map.put(50, new ItemBuilder(this, MailboxMenuManager.nextPageBtn()).setNextPageButton());

        return map;
    }

    @Override
    public void onInventoryClick(InventoryClickEvent inventoryClickEvent) {}
}
