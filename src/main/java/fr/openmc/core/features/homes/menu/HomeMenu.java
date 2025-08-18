package fr.openmc.core.features.homes.menu;

import dev.lone.itemsadder.api.FontImages.FontImageWrapper;
import fr.openmc.api.menulib.PaginatedMenu;
import fr.openmc.api.menulib.utils.InventorySize;
import fr.openmc.api.menulib.utils.ItemBuilder;
import fr.openmc.core.features.homes.HomesManager;
import fr.openmc.core.features.homes.icons.HomeIcon;
import fr.openmc.core.features.homes.icons.HomeIconRegistry;
import fr.openmc.core.features.homes.models.Home;
import fr.openmc.core.features.mailboxes.utils.MailboxMenuManager;
import fr.openmc.core.items.CustomItemRegistry;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
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
    public @NotNull String getName() {
        return "Menu des Homes";
    }

    @Override
    public String getTexture() {
        return FontImageWrapper.replaceFontImages("§r§f:offset_-8::omc_homes_menus_home:");
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
                    itemMeta.displayName(Component.text("§e" + home.getName()));
                    itemMeta.lore(List.of(
                            Component.text("§7■ §aClique §2gauche pour vous téléporter"),
                            Component.text("§7■ §cCliquez §4droit §cpour configurer le home")
                    ));
                }).setOnClick(event -> {
                    if(event.isLeftClick()) {
                        this.getInventory().close();
                        getOwner().teleportAsync(home.getLocation()).thenAccept(success -> {
                            MessagesManager.sendMessage(getOwner(), Component.text("§aVous avez été téléporté à votre home §e" + home.getName() + "§a."), Prefix.HOME, MessageType.SUCCESS, true);
                        });
                    } else if(event.isRightClick()) {
                        Player player = (Player) event.getWhoClicked();
                        new HomeConfigMenu(player, home).open();
                    }
                }));
            } catch (Exception e) {
                MessagesManager.sendMessage(getOwner(), Component.text("§cUne Erreur est survenue, veuillez contacter le Staff"), Prefix.OPENMC, MessageType.ERROR, false);
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
                map.put(45, new ItemBuilder(this, Objects.requireNonNull(CustomItemRegistry.getByName("omc_homes:omc_homes_icon_information")).getBest(),
                        itemMeta -> {
                            itemMeta.displayName(Component.text("§8(§bⓘ§8) §6Informations sur vos homes"));
                            itemMeta.lore(List.of(
                                    Component.text("§8→ §6Chaque icon qui représente un home est lié au nom du home, par exemple, si vous appelé votre home 'maison', l'icône sera une maison"),
                                    Component.empty(),
                                    Component.text("§8› §6Vous pouvez configurer le home en effectuant un clique droit sur l'icône du home."),
                                    Component.text("§8› §6Vous pouvez vous téléporter à votre home en effectuant un clique gauche sur l'icône du home.")
                            ));
                        }
                    )
            );

                map.put(53, new ItemBuilder(this, Objects.requireNonNull(CustomItemRegistry.getByName("omc_homes:omc_homes_icon_upgrade")).getBest(), itemMeta -> {
                    itemMeta.displayName(Component.text("§8● §6Améliorer les homes §8(Click ici)"));
                    itemMeta.lore(List.of(
                        Component.text("§6Cliquez pour améliorer vos homes")
                    ));
                }).setOnClick(event -> new HomeUpgradeMenu(getOwner()).open()));
            }

        map.put(48, new ItemBuilder(this, MailboxMenuManager.previousPageBtn()).setPreviousPageButton());
        map.put(49, new ItemBuilder(this, MailboxMenuManager.cancelBtn()).setCloseButton());
        map.put(50, new ItemBuilder(this, MailboxMenuManager.nextPageBtn()).setNextPageButton());

        return map;
    }

    @Override
    public void onInventoryClick(InventoryClickEvent inventoryClickEvent) {
    }

    @Override
    public void onClose(InventoryCloseEvent event) {
        //empty
    }
}
