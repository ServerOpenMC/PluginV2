package fr.openmc.core.features.homes.menu;

import fr.openmc.api.input.signgui.SignGUI;
import fr.openmc.api.menulib.PaginatedMenu;
import fr.openmc.api.menulib.utils.ItemBuilder;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.homes.icons.HomeIconCacheManager;
import fr.openmc.core.features.homes.icons.IconCategory;
import fr.openmc.core.features.homes.models.Home;
import fr.openmc.core.features.mailboxes.utils.MailboxMenuManager;
import fr.openmc.core.utils.ItemUtils;
import fr.openmc.core.utils.customitems.CustomItemRegistry;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class HomeChangeIconMenu extends PaginatedMenu {

    private final Home home;
    private IconCategory currentCategory = IconCategory.ALL;
    private String searchQuery;

    private static final Map<UUID, Long> CATEGORY_COOLDOWNS = new ConcurrentHashMap<>();
    private static final long CATEGORY_COOLDOWN_TIME = 500; // 2 seconds cooldown

    public HomeChangeIconMenu(Player owner, Home home, String searchQuery) {
        super(owner);
        this.home = home;
        this.searchQuery = searchQuery != null ? searchQuery : "";
    }

    public HomeChangeIconMenu(Player owner, Home home) {
        this(owner, home, "");
    }

    @Override
    public @NotNull String getName() {
        return "§r§f:offset_-8::omc_homes_menus_home:";
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

        if (!searchQuery.isEmpty()) return HomeIconCacheManager.searchIcons(searchQuery, this, home, player);
        else return HomeIconCacheManager.getItemsForCategory(currentCategory, this, home, player);
    }

    @Override
    public List<Integer> getTakableSlot() {
        return List.of();
    }

    @Override
    public Map<Integer, ItemStack> getButtons() {
        Map<Integer, ItemStack> map = new HashMap<>();

        map.put(45, new ItemBuilder(this, Objects.requireNonNull(CustomItemRegistry.getByName("menu:previous_page")).getBest(),
                itemMeta -> itemMeta.displayName(Component.text("§7Retour"))).setBackButton());

        map.put(48, new ItemBuilder(this, MailboxMenuManager.previousPageBtn()).setPreviousPageButton());
        map.put(49, new ItemBuilder(this, MailboxMenuManager.cancelBtn()).setCloseButton());
        map.put(50, new ItemBuilder(this, MailboxMenuManager.nextPageBtn()).setNextPageButton());

        // Search button
        map.put(51, new ItemBuilder(this, Material.OAK_SIGN, meta -> {
            meta.displayName(Component.text("§eRecherche"));
            List<Component> lore = new ArrayList<>();
            if (!searchQuery.isEmpty()) lore.add(Component.text("§7Recherche actuelle: §f" + searchQuery));
            lore.add(Component.empty());
            lore.add(Component.text("§7■ §aCliquez §2gauche §apour rechercher"));
            lore.add(Component.text("§7■ §cCliquez §4droit §cpour réinitialiser"));
            meta.lore(lore);
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
                gui = SignGUI.builder()
                        .setLines(lines)
                        .setType(ItemUtils.getSignType(getOwner()))
                        .setHandler((p, result) -> {
                            searchQuery = result.getLine(0);
                            currentCategory = IconCategory.ALL;
                            setPage(0);
                            refresh();

                            return Collections.emptyList();
                        })
                        .build();

                gui.open(getOwner());
            } else if (event.getClick().isRightClick()) {
                searchQuery = "";
                refresh();
            }
        }));

        // Invisible items
        for (int slot : List.of(46, 47, 52)) {
            map.put(slot, new ItemBuilder(this, Objects.requireNonNull(CustomItemRegistry.getByName("omc_homes:omc_homes_invisible")).getBest(),
                    itemMeta -> itemMeta.displayName(Component.empty())));
        }

        // Category selector
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
            // Cooldown to prevent spamming category changes
            long now = System.currentTimeMillis();
            long last = CATEGORY_COOLDOWNS.getOrDefault(getOwner().getUniqueId(), 0L);
            if (now - last < CATEGORY_COOLDOWN_TIME) {
                MessagesManager.sendMessage(getOwner(),
                        Component.text("§cMerci de ne pas spammer le changement de catégorie."),
                        Prefix.OPENMC, MessageType.ERROR, true);
                return;
            }
            CATEGORY_COOLDOWNS.put(getOwner().getUniqueId(), now);

            getOwner().playSound(getOwner().getLocation(), Sound.UI_BUTTON_CLICK, 1.0f, 1.0f);
            IconCategory[] CATEGORIES = IconCategory.values();
            if (event.getClick().isLeftClick()) {
                currentCategory = CATEGORIES[(currentCategory.ordinal() + 1) % CATEGORIES.length];
            } else if (event.getClick().isRightClick()) {
                currentCategory = CATEGORIES[(currentCategory.ordinal() - 1 + CATEGORIES.length) % CATEGORIES.length];
            }

            searchQuery = "";
            refresh();
        }));

        return map;
    }

    private Component formatCategoryLine(NamedTextColor color, String name, boolean selected) {
        return Component.text((selected ? "§r• " : "§r  "), color)
                .append(Component.text(name, NamedTextColor.DARK_GRAY));
    }

    @Override
    public void onInventoryClick(InventoryClickEvent inventoryClickEvent) {
    }

    @Override
    public void onClose(InventoryCloseEvent event) {}

    private void refresh() {
        Bukkit.getScheduler().runTask(OMCPlugin.getInstance(), this::open);
    }
}
