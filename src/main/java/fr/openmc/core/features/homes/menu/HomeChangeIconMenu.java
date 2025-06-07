package fr.openmc.core.features.homes.menu;

import fr.openmc.api.input.signgui.SignGUI;
import fr.openmc.api.input.signgui.exception.SignGUIVersionException;
import fr.openmc.api.menulib.PaginatedMenu;
import fr.openmc.api.menulib.utils.ItemBuilder;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.homes.Home;
import fr.openmc.core.features.homes.icons.HomeIcon;
import fr.openmc.core.features.homes.icons.HomeIconRegistry;
import fr.openmc.core.features.homes.icons.IconCategory;
import fr.openmc.core.features.mailboxes.utils.MailboxMenuManager;
import fr.openmc.core.utils.ColorUtils;
import fr.openmc.core.utils.ItemUtils;
import fr.openmc.core.utils.customitems.CustomItemRegistry;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class HomeChangeIconMenu extends PaginatedMenu {

    private final Home home;
    private final Map<IconCategory, List<ItemStack>> CACHED_ITEMS = new ConcurrentHashMap<>();
    private final List<ItemStack> CACHED_SEARCH_ITEMS = new ArrayList<>();
    private IconCategory currentCategory = IconCategory.ALL;
    private String lastSearchQuery = "";
    private String searchQuery;

    public HomeChangeIconMenu(Player owner, Home home, String searchQuery) {
        super(owner);
        this.home = home;
        this.searchQuery = searchQuery;

        preloadVanillaItems();
    }

    public HomeChangeIconMenu(Player owner, Home home) {
        this(owner, home, "");
    }

    private void preloadVanillaItems() {
        if (!CACHED_ITEMS.containsKey(IconCategory.VANILLA)) {
            List<ItemStack> vanillaItems = createItemsForCategory(IconCategory.VANILLA);
            CACHED_ITEMS.put(IconCategory.VANILLA, vanillaItems);
        }
    }

    private List<ItemStack> createItemsForCategory(IconCategory category) {
        List<ItemStack> items = new ArrayList<>();
        Player player = getOwner();

        List<HomeIcon> iconsToShow = HomeIconRegistry.getIconsByCategory(category);

        for (HomeIcon homeIcon : iconsToShow) {
            items.add(new ItemBuilder(this, createItems(homeIcon))
                .setOnClick(event -> {
                    Bukkit.getScheduler().runTask(OMCPlugin.getInstance(), () -> {
                        home.setIcon(homeIcon);
                        MessagesManager.sendMessage(player,
                                Component.text("§aL'icône de votre home §2" + home.getName() + " §aa été changée avec succès !"),
                                Prefix.HOME, MessageType.SUCCESS, true);
                    });
                    player.closeInventory();
                }));
        }

        return items;
    }

    @Override
    public @NotNull String getName() {
        return PlaceholderAPI.setPlaceholders(getOwner(), "§r§f%img_offset_-8%%img_omc_homes_menus_home%");
    }

    @Override
    public @Nullable Material getBorderMaterial() {
        return Material.BLUE_STAINED_GLASS_PANE;
    }

    @Override
    public @NotNull List<Integer> getStaticSlots() {
        return List.of(45, 46, 47, 48, 49, 50, 51, 52, 53);
    }

    @Override
    public @NotNull List<ItemStack> getItems() {
        Player player = getOwner();

        try {
            if (!searchQuery.isEmpty()) {
                if (!searchQuery.equals(lastSearchQuery)) {
                    List<HomeIcon> iconsToShow = HomeIconRegistry.searchIcons(searchQuery);
                    CACHED_SEARCH_ITEMS.clear();

                    for (HomeIcon homeIcon : iconsToShow) {
                        CACHED_SEARCH_ITEMS.add(
                                new ItemBuilder(this, createItems(homeIcon))
                                    .setOnClick(event -> {
                                        Bukkit.getScheduler().runTask(OMCPlugin.getInstance(), () -> {
                                            home.setIcon(homeIcon);
                                            MessagesManager.sendMessage(player,
                                                    Component.text("§aL'icône de votre home §2" + home.getName() + " §aa été changée avec succès !"),
                                                    Prefix.HOME, MessageType.SUCCESS, true);
                                        });
                                        player.closeInventory();
                                }));
                    }
                    lastSearchQuery = searchQuery;
                }

                return new ArrayList<>(CACHED_SEARCH_ITEMS);
            }

            if (!CACHED_ITEMS.containsKey(currentCategory)) {
                CACHED_ITEMS.put(currentCategory, createItemsForCategory(currentCategory));
            }

            return new ArrayList<>(CACHED_ITEMS.get(currentCategory));
        } catch (Exception e) {
            MessagesManager.sendMessage(player,
                    Component.text("§cUne erreur est survenue, veuillez contacter le Staff"),
                    Prefix.OPENMC, MessageType.ERROR, false);
            player.closeInventory();
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Integer> getTakableSlot() {
        return List.of();
    }

    @Override
    public Map<Integer, ItemStack> getButtons() {
        Map<Integer, ItemStack> map = new HashMap<>();

        map.put(45, new ItemBuilder(this, Objects.requireNonNull(CustomItemRegistry.getByName("menu:previous_page")).getBest(),
                itemMeta -> itemMeta.setDisplayName("§7Retour")).setBackButton());

        ItemStack customFilter = new ItemStack(Material.EMERALD);
        ItemMeta customMeta = customFilter.getItemMeta();
        customMeta.setDisplayName("§aIcônes Custom");
        List<String> customLore = new ArrayList<>();
        customLore.add("§7Affiche uniquement les icônes custom");
        if (currentCategory == IconCategory.CUSTOM) {
            customLore.add("§a✓ Actif");
            customMeta.addEnchant(Enchantment.SHARPNESS, 1, true);
            customMeta.addItemFlags(org.bukkit.inventory.ItemFlag.HIDE_ENCHANTS);
        } else {
            customLore.add("§7Cliquez pour activer");
        }
        customMeta.setLore(customLore);
        customFilter.setItemMeta(customMeta);

        map.put(48, new ItemBuilder(this, MailboxMenuManager.previousPageBtn()).setPreviousPageButton());
        map.put(49, new ItemBuilder(this, MailboxMenuManager.cancelBtn()).setCloseButton());
        map.put(50, new ItemBuilder(this, MailboxMenuManager.nextPageBtn()).setNextPageButton());

        map.put(51, new ItemBuilder(this, Material.OAK_SIGN, meta -> {
            meta.setDisplayName("§eRecherche");
            List<String> lore = new ArrayList<>();
            if (!searchQuery.isEmpty()) lore.add("§7Recherche actuelle: §f" + searchQuery);
            lore.add("");
            lore.add("§7■ §aCliquez §2gauche §apour rechercher");
            lore.add("§7■ §cCliquez §4droit §cpour réinitialiser");
            meta.setLore(lore);
        }).setOnClick(event -> {
            if (event.getClick().isLeftClick()) {
                getOwner().closeInventory();
                String[] lines = {
                        "",
                        " ᐱᐱᐱᐱᐱᐱᐱ ",
                        "Entrez votre",
                        "nom ci dessus"
                };

                SignGUI gui;
                try {
                    gui = SignGUI.builder()
                            .setLines(lines)
                            .setType(ItemUtils.getSignType(getOwner()))
                            .setHandler((p, result) -> {
                                searchQuery = result.getLine(0);
                                currentCategory = IconCategory.ALL;
                                refresh();

                                return Collections.emptyList();
                            })
                            .build();

                    gui.open(getOwner());
                } catch (SignGUIVersionException e) {
                    MessagesManager.sendMessage(getOwner(),
                            Component.text("§cUne erreur est survenue, veuillez contacter le Staff"),
                            Prefix.OPENMC, MessageType.ERROR, false);
                }
            } else if (event.getClick().isRightClick()) {
                searchQuery = "";
                refresh();
            }
        }));

        for (int slot : List.of(46, 47, 52, 53)) {
            map.put(slot, new ItemBuilder(this, Objects.requireNonNull(CustomItemRegistry.getByName("omc_homes:omc_homes_invisible")).getBest(),
                    itemMeta -> itemMeta.setDisplayName("§7")));
        }

        map.put(53, new ItemBuilder(this, Material.COMPASS, meta -> {
            meta.displayName(Component.text("§aCatégorie"));

            List<Component> lore = new ArrayList<>();
            lore.add(Component.text("§7Sélection de catégorie"));
            lore.add(Component.empty());
            lore.add(Component.text("§7Catégories disponibles:"));

            lore.add(formatCategoryLine(NamedTextColor.YELLOW, "§eToutes", currentCategory == IconCategory.ALL));
            lore.add(formatCategoryLine(NamedTextColor.LIGHT_PURPLE, "§dPersonnalisés", currentCategory == IconCategory.CUSTOM));
            lore.add(formatCategoryLine(NamedTextColor.GREEN, "§aVanilla", currentCategory == IconCategory.VANILLA));

            lore.add(Component.empty());
            lore.add(Component.text("§7■ §aClique §2gauche §apour aller à la catégorie suivante"));
            lore.add(Component.text("§7■ §cClique §4droit §cpour aller à la catégorie précédente"));

            meta.lore(lore);
        }).setOnClick(event -> {
            getOwner().playSound(getOwner().getLocation(), Sound.UI_BUTTON_CLICK, 1.0f, 1.0f);

            if (event.getClick().isLeftClick()) {
                switch(currentCategory) {
                    case ALL:
                        currentCategory = IconCategory.CUSTOM;
                        break;
                    case CUSTOM:
                        currentCategory = IconCategory.VANILLA;
                        break;
                    case VANILLA:
                        currentCategory = IconCategory.ALL;
                        break;
                }
            } else if (event.getClick().isRightClick()) {
                switch(currentCategory) {
                    case ALL:
                        currentCategory = IconCategory.VANILLA;
                        break;
                    case VANILLA:
                        currentCategory = IconCategory.CUSTOM;
                        break;
                    case CUSTOM:
                        currentCategory = IconCategory.ALL;
                        break;
                }
            }

            refresh();
        }));

        return map;
    }

    private Component formatCategoryLine(NamedTextColor color, String name, boolean selected) {
        return Component.text((selected ? "§r• " : "§r  "), color)
                .append(Component.text(name, NamedTextColor.DARK_GRAY));
    }

    @Override
    public void onInventoryClick(InventoryClickEvent inventoryClickEvent) {}

    @Override
    public void onClose(InventoryCloseEvent event) {}

    private void refresh() {
        Bukkit.getScheduler().runTask(OMCPlugin.getInstance(), this::open);
    }

    private ItemStack createItems(HomeIcon homeIcon) {
        ItemStack iconItem = homeIcon.getItemStack().clone();
        ItemMeta meta = iconItem.getItemMeta();

        if (meta != null) {
            meta.setDisplayName("§a" + homeIcon.getVanillaName());

            List<String> lore = new ArrayList<>();

            if (home.getIcon().equals(homeIcon)) {
                meta.addEnchant(Enchantment.SHARPNESS, 5, true);
                meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                lore.add(ChatColor.GRAY + "§8[§a✔§8] §7Icône actuelle");
            } else {
                lore.add(ChatColor.GRAY + "■ §aClique §2gauche §apour changer l'icône");
            }

            meta.setLore(lore);
            iconItem.setItemMeta(meta);
        }

        return iconItem;
    }
}