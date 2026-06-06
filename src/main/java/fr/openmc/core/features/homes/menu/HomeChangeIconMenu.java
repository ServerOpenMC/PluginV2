package fr.openmc.core.features.homes.menu;

import dev.lone.itemsadder.api.FontImages.FontImageWrapper;
import fr.openmc.api.input.dialog.DialogInput;
import fr.openmc.api.menulib.PaginatedMenu;
import fr.openmc.api.menulib.utils.InventorySize;
import fr.openmc.api.menulib.utils.ItemMenuBuilder;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.OMCRegistry;
import fr.openmc.core.features.homes.icons.HomeIcon;
import fr.openmc.core.features.homes.icons.HomeIconCacheManager;
import fr.openmc.core.features.homes.models.Home;
import fr.openmc.core.features.mailboxes.utils.MailboxMenuManager;
import fr.openmc.core.utils.bukkit.ItemUtils;
import fr.openmc.core.utils.text.messages.MessageType;
import fr.openmc.core.utils.text.messages.MessagesManager;
import fr.openmc.core.utils.text.messages.Prefix;
import fr.openmc.core.utils.text.messages.TranslationManager;
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

import static fr.openmc.core.utils.text.InputUtils.MAX_LENGTH;

public class HomeChangeIconMenu extends PaginatedMenu {

    private final Home home;
    private HomeIcon.IconCategory currentCategory = HomeIcon.IconCategory.ALL;
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
    public @NotNull InventorySize getInventorySize() {
        return InventorySize.LARGEST;
    }

    @Override
    public int getSizeOfItems() {
        return getItems().size();
    }

    @Override
    public @NotNull Component getName() {
        return TranslationManager.translation("feature.homes.icon.menu.title");
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
        return List.of(45, 46, 47, 48, 49, 50, 51, 52, 53);
    }

    @Override
    public List<ItemStack> getItems() {
        Player player = getOwner();

        if (!searchQuery.isEmpty()) return HomeIconCacheManager.searchIcons(searchQuery, this, home, player);
        else return HomeIconCacheManager.getItemsForCategory(currentCategory, this, home, player);
    }

    @Override
    public List<Integer> getTakableSlot() {
        return List.of();
    }

    @Override
    public Map<Integer, ItemMenuBuilder> getButtons() {
        Map<Integer, ItemMenuBuilder> map = new HashMap<>();

        map.put(45, new ItemMenuBuilder(this,  OMCRegistry.CUSTOM_ITEMS.get("_iainternal:icon_back_orange").getBest(),
                itemMeta -> itemMeta.displayName(TranslationManager.translation("messages.menus.back")), true));

        map.put(48, new ItemMenuBuilder(this, MailboxMenuManager.previousPageBtn()).setPreviousPageButton());
        map.put(49, MailboxMenuManager.cancelBtn(this).setCloseButton());
        map.put(50, new ItemMenuBuilder(this, MailboxMenuManager.nextPageBtn()).setNextPageButton());

        // Search button
        map.put(51, new ItemMenuBuilder(this, Material.OAK_SIGN, meta -> {
            meta.displayName(TranslationManager.translation("feature.homes.icon.search.name"));
            List<Component> lore = new ArrayList<>();
            if (! searchQuery.isEmpty()) lore.add(TranslationManager.translation("feature.homes.icon.search.current", Component.text(searchQuery)));
            lore.add(Component.empty());
            lore.add(TranslationManager.translation("feature.homes.icon.search.left"));
            lore.add(TranslationManager.translation("feature.homes.icon.search.right"));
            meta.lore(lore);
        }).setOnClick(event -> {
            if (event.getClick().isLeftClick()) {
                getOwner().closeInventory();

                DialogInput.send(getOwner(), TranslationManager.translation("feature.homes.icon.search.prompt"), MAX_LENGTH, input -> {
                    if (input == null) return;

                    searchQuery = input;
                    currentCategory = HomeIcon.IconCategory.ALL;
                    setPage(0);
                    refresh();
                });
            } else if (event.getClick().isRightClick()) {
                searchQuery = "";
                refresh();
            }
        }));

        // Invisible items
        for (int slot : List.of(46, 47, 52)) {
            map.put(slot, new ItemMenuBuilder(this, ItemUtils.getInvisibleItem()));
        }

        // Category selector
        map.put(53, new ItemMenuBuilder(this, Material.COMPASS, meta -> {
            meta.displayName(TranslationManager.translation("feature.homes.icon.category.name"));

            List<Component> lore = new ArrayList<>();
            lore.add(TranslationManager.translation("feature.homes.icon.category.selection"));
            lore.add(Component.empty());
            lore.add(TranslationManager.translation("feature.homes.icon.category.available"));

            lore.add(formatCategoryLine(NamedTextColor.YELLOW, TranslationManager.translation("feature.homes.icon.category.all"), currentCategory == HomeIcon.IconCategory.ALL));
            lore.add(formatCategoryLine(NamedTextColor.GREEN, TranslationManager.translation("feature.homes.icon.category.vanilla"), currentCategory == HomeIcon.IconCategory.VANILLA));
            lore.add(formatCategoryLine(NamedTextColor.LIGHT_PURPLE, TranslationManager.translation("feature.homes.icon.category.custom"), currentCategory == HomeIcon.IconCategory.CUSTOM));

            lore.add(Component.empty());
            lore.add(TranslationManager.translation("feature.homes.icon.category.left"));
            lore.add(TranslationManager.translation("feature.homes.icon.category.right"));

            meta.lore(lore);
        }).setOnClick(event -> {
            // Cooldown to prevent spamming category changes
            long now = System.currentTimeMillis();
            long last = CATEGORY_COOLDOWNS.getOrDefault(getOwner().getUniqueId(), 0L);
            if (now - last < CATEGORY_COOLDOWN_TIME) {
                MessagesManager.sendMessage(getOwner(),
                        TranslationManager.translation("feature.homes.icon.category.spam"),
                        Prefix.OPENMC, MessageType.ERROR, true);
                return;
            }
            CATEGORY_COOLDOWNS.put(getOwner().getUniqueId(), now);

            getOwner().playSound(getOwner().getLocation(), Sound.UI_BUTTON_CLICK, 1.0f, 1.0f);
            HomeIcon.IconCategory[] CATEGORIES = HomeIcon.IconCategory.values();
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

    private Component formatCategoryLine(NamedTextColor color, Component name, boolean selected) {
        return Component.text(selected ? "• " : "  ", color)
                .append(name.color(NamedTextColor.DARK_GRAY));
    }

    @Override
    public void onInventoryClick(InventoryClickEvent inventoryClickEvent) {}

    @Override
    public void onClose(InventoryCloseEvent event) {}

    private void refresh() {
        Bukkit.getScheduler().runTask(OMCPlugin.getInstance(), this::open);
    }
}
