package fr.openmc.core.features.homes.menu;

import dev.lone.itemsadder.api.FontImages.FontImageWrapper;
import fr.openmc.api.menulib.PaginatedMenu;
import fr.openmc.api.menulib.utils.InventorySize;
import fr.openmc.api.menulib.utils.ItemBuilder;
import fr.openmc.api.menulib.utils.ItemUtils;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.homes.HomesManager;
import fr.openmc.core.features.homes.events.HomeTpEvent;
import fr.openmc.core.features.homes.icons.HomeIcon;
import fr.openmc.core.features.homes.icons.HomeIconRegistry;
import fr.openmc.core.features.homes.models.Home;
import fr.openmc.core.features.mailboxes.utils.MailboxMenuManager;
import fr.openmc.core.registry.items.CustomItemRegistry;
import fr.openmc.core.utils.text.messages.MessageType;
import fr.openmc.core.utils.text.messages.MessagesManager;
import fr.openmc.core.utils.text.messages.Prefix;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

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
    public @NotNull InventorySize getInventorySize() {
        return InventorySize.LARGEST;
    }

    @Override
    public int getSizeOfItems() {
        return getItems().size();
    }

    @Override
    public @NotNull Component getName() {
        return TranslationManager.translation("feature.homes.menu.title");
    }

    @Override
    public String getTexture() {
        return FontImageWrapper.replaceFontImages("§r§f:offset_-8::omc_homes_menus_home:");
    }

    @Override
    public @Nullable Material getBorderMaterial() {
        return null;
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
    public List<ItemStack> getItems() {
        List<ItemStack> items = new ArrayList<>();
        for(Home home : HomesManager.getHomes(target.getUniqueId())) {
            HomeIcon homeIcon = home.getIcon();
            if (homeIcon == null) {
                homeIcon = HomeIconRegistry.getDefaultIcon();
                home.setIcon(homeIcon);
            }
            try {
                items.add(new ItemBuilder(this, HomeIconRegistry.getIconOrDefault(home.getIcon().id()).getItemStack(), itemMeta -> {
                    itemMeta.displayName(TranslationManager.translation(
                            "feature.homes.home.name",
                            Component.text(home.getName()).color(NamedTextColor.YELLOW)
                    ));
                    itemMeta.lore(TranslationManager.translationLore("feature.homes.menu.item.lore"));
                }).hide(ItemUtils.getDataComponentType()).setOnClick(event -> {
                    if(event.isLeftClick()) {
                        this.getInventory().close();
                        getOwner().teleportAsync(home.getLocation()).thenAccept(success -> {
                            new BukkitRunnable() {
                                @Override
                                public void run() {
                                    Bukkit.getPluginManager().callEvent(new HomeTpEvent(home, getOwner()));
                                }
                            }.runTask(OMCPlugin.getInstance());
                            MessagesManager.sendMessage(
                                    getOwner(),
                                    TranslationManager.translation(
                                            "feature.homes.menu.teleport.success",
                                            Component.text(home.getName()).color(NamedTextColor.YELLOW)
                                    ),
                                    Prefix.HOME,
                                    MessageType.SUCCESS,
                                    true
                            );
                        });
                    } else if(event.isRightClick()) {
                        Player player = (Player) event.getWhoClicked();
                        new HomeConfigMenu(player, home).open();
                    }
                }));
            } catch (Exception e) {
                MessagesManager.sendMessage(getOwner(), TranslationManager.translation("feature.homes.menu.error"), Prefix.OPENMC, MessageType.ERROR, false);
                getOwner().closeInventory();
                throw new RuntimeException("Failed to create HomeMenu item for home: " + home.getName(), e);
            }
        }
        return items;
    }

    @Override
    public List<Integer> getTakableSlot() {
        return List.of();
    }

    @Override
    public Map<Integer, ItemBuilder> getButtons() {
        Map<Integer, ItemBuilder> map = new HashMap<>();

        if(!wasTarget) {
            map.put(53, new ItemBuilder(this, Objects.requireNonNull(CustomItemRegistry.getByName("omc_homes:omc_homes_icon_upgrade")).getBest(), itemMeta -> {
                itemMeta.displayName(TranslationManager.translation("feature.homes.menu.upgrade.name"));
                itemMeta.lore(TranslationManager.translationLore("feature.homes.menu.upgrade.lore"));
            }).setOnClick(event -> new HomeUpgradeMenu(getOwner()).open()));
        }

        map.put(48, new ItemBuilder(this, MailboxMenuManager.previousPageBtn()).setPreviousPageButton());
        map.put(49, MailboxMenuManager.cancelBtn(this).setCloseButton());
        map.put(50, new ItemBuilder(this, MailboxMenuManager.nextPageBtn()).setNextPageButton());

        return map;
    }

    @Override
    public void onInventoryClick(InventoryClickEvent inventoryClickEvent) {}

    @Override
    public void onClose(InventoryCloseEvent event) {
        //empty
    }
}
