package fr.openmc.core.features.homes.icons;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.homes.Home;
import fr.openmc.core.features.homes.menu.HomeChangeIconMenu;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class HomeIconCacheManager {

    private static final ConcurrentHashMap<IconCategory, List<CachedIconItem>> CACHED_ITEMS = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<String, List<CachedIconItem>> CACHED_SEARCH_RESULTS = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<String, List<ItemStack>> RENDERED_ITEMS_CACHE = new ConcurrentHashMap<>();
    private static volatile boolean initialized = false;

    private static final Integer MAX_CACHE_SIZE = 50;

    /**
     * Initializes the HomeIconCacheManager and preloads all icons into the cache.
     * This method should be called once at the start of the plugin.
     */
    public static synchronized void initialize() {
        if (initialized) return;
        HomeIconRegistry.initializeIcons();

        Bukkit.getScheduler().runTaskAsynchronously(OMCPlugin.getInstance(), () -> {
            try {
                for (IconCategory category : IconCategory.values())
                    initializeCategoryCache(category);

                initialized = true;
                OMCPlugin.getInstance().getLogger().info("Initialized icon cache manager");
            } catch (Exception e) {
                throw new RuntimeException("Failed to initialize icon cache manager", e);
            }
        });
    }

    /**
     * Initializes the cache for a specific icon category.
     *
     * @param category The icon category to initialize.
     */
    private static void initializeCategoryCache(IconCategory category) {
        List<HomeIcon> icons = HomeIconRegistry.getIconsByCategory(category);
        List<CachedIconItem> cachedItems = new ArrayList<>();

        for (HomeIcon icon : icons) {
            try {
                ItemStack baseItem = icon.getItemStack().clone();
                cachedItems.add(new CachedIconItem(icon, baseItem));
            } catch (Exception e) {
                OMCPlugin.getInstance().getLogger().warning("Failed to create base item for icon: " + icon.getId() + " - " + e.getMessage());
            }
        }

        CACHED_ITEMS.put(category, cachedItems);
    }

    /**
     * Retrieves a list of ItemStacks for the specified icon category.
     *
     * @param category The icon category to retrieve items for.
     * @param menu     The HomeChangeIconMenu instance.
     * @param home     The Home instance.
     * @param player   The Player requesting the items.
     * @return A list of ItemStacks representing the icons in the specified category.
     */
    public static List<ItemStack> getItemsForCategory(IconCategory category, HomeChangeIconMenu menu, Home home, Player player) {
        if (!initialized) initializeCategoryCache(category);

        List<CachedIconItem> cachedItems = CACHED_ITEMS.get(category);
        if (cachedItems == null) {
            initializeCategoryCache(category);
            cachedItems = CACHED_ITEMS.get(category);
        }

        String cacheKey = category.name() + "_" + home.getIcon().getId();

        List<ItemStack> cachedRenderedItems = RENDERED_ITEMS_CACHE.get(cacheKey);
        if (cachedRenderedItems != null && cachedRenderedItems.size() == cachedItems.size()) {
            return cloneItemList(cachedRenderedItems);
        }

        List<ItemStack> items = new ArrayList<>();
        for (CachedIconItem cachedItem : cachedItems) {
            items.add(cachedItem.createItemForMenu(menu, home, player));
        }

        if (RENDERED_ITEMS_CACHE.size() > MAX_CACHE_SIZE) RENDERED_ITEMS_CACHE.clear(); // Limit cache size to avoid memory issues

        RENDERED_ITEMS_CACHE.put(cacheKey, cloneItemList(items));
        return items;
    }

    /**
     * Searches for icons based on the provided query and returns a list of ItemStacks.
     *
     * @param query  The search query to filter icons.
     * @param menu   The HomeChangeIconMenu instance.
     * @param home   The Home instance.
     * @param player The Player requesting the search.
     * @return A list of ItemStacks representing the icons that match the search query.
     */
    public static List<ItemStack> searchIcons(String query, HomeChangeIconMenu menu, Home home, Player player) {
        if (query == null || query.trim().isEmpty()) return new ArrayList<>();

        String normalizedQuery = query.toLowerCase().trim();
        String cacheKey = "search_" + normalizedQuery + "_" + home.getIcon().getId();

        List<ItemStack> cachedRenderedItems = RENDERED_ITEMS_CACHE.get(cacheKey);
        if (cachedRenderedItems != null) {
            return cloneItemList(cachedRenderedItems);
        }

        List<CachedIconItem> cachedItems = CACHED_SEARCH_RESULTS.get(normalizedQuery);

        if (cachedItems == null) {
            List<HomeIcon> searchResults = HomeIconRegistry.searchIcons(normalizedQuery);
            cachedItems = new ArrayList<>();
            for (HomeIcon icon : searchResults) {
                try {
                    ItemStack baseItem = icon.getItemStack().clone();
                    cachedItems.add(new CachedIconItem(icon, baseItem));
                } catch (Exception e) {
                    OMCPlugin.getInstance().getLogger().warning("Failed to create base item for icon: " + icon.getId() + " - " + e.getMessage());
                }
            }

            if (CACHED_SEARCH_RESULTS.size() > MAX_CACHE_SIZE) CACHED_SEARCH_RESULTS.clear(); // Limit cache size to avoid memory issues
            CACHED_SEARCH_RESULTS.put(normalizedQuery, cachedItems);
        }

        List<ItemStack> items = new ArrayList<>();
        for (CachedIconItem cachedIconItem : cachedItems) {
            items.add(cachedIconItem.createItemForMenu(menu, home, player));
        }

        if (RENDERED_ITEMS_CACHE.size() > MAX_CACHE_SIZE) RENDERED_ITEMS_CACHE.clear(); // Limit cache size to avoid memory issues
        RENDERED_ITEMS_CACHE.put(cacheKey, cloneItemList(items));

        return items;
    }

    /**
     * Clones a list of ItemStacks to avoid modifying the original items.
     *
     * @param items The list of ItemStacks to clone.
     * @return A new list containing cloned ItemStacks.
     */
    private static List<ItemStack> cloneItemList(List<ItemStack> items) {
        List<ItemStack> clonedItems = new ArrayList<>();
        for (ItemStack item : items) {
            clonedItems.add(item.clone());
        }
        return clonedItems;
    }

    /**
     * Clears the cache and resets the initialized state.
     * <p>
     * This method can be used to reset the cache if needed.
     */
    public static synchronized void clearCache() {
        CACHED_ITEMS.clear();
        CACHED_SEARCH_RESULTS.clear();
        RENDERED_ITEMS_CACHE.clear();
        initialized = false;
    }

    /**
     * Reloads the cache by clearing it and reinitializing.
     * <p>
     * This method can be used to refresh the cache without restarting the plugin.
     */
    public static void reload() {
        clearCache();
        initialize();
    }

    /**
     * Returns the current cache statistics.
     *
     * @return A string containing the cache statistics.
     */
    public static String getCacheStats() {
        StringBuilder stats = new StringBuilder();
        stats.append("HomeIconCacheManager Stats:\n");
        stats.append("- Initialisé: ").append(initialized).append("\n");
        stats.append("- Catégories en cache: ").append(CACHED_ITEMS.size()).append("\n");

        for (IconCategory category : CACHED_ITEMS.keySet()) {
            List<CachedIconItem> items = CACHED_ITEMS.get(category);
            stats.append("  - ").append(category).append(": ").append(items != null ? items.size() : 0).append(" items\n");
        }

        stats.append("- Recherches en cache: ").append(CACHED_SEARCH_RESULTS.size()).append("\n");
        stats.append("- Items rendus en cache: ").append(RENDERED_ITEMS_CACHE.size()).append("\n");

        return stats.toString();
    }

    /**
     * Invalidates the render cache for a specific icon ID.
     * <p>
     * This method removes all cached items that end with the specified icon ID.
     *
     * @param iconId The ID of the icon to invalidate from the render cache.
     */
    public static void invalidateRenderCache(String iconId) {
        RENDERED_ITEMS_CACHE.entrySet().removeIf(entry -> entry.getKey().endsWith("_" + iconId));
    }

    /**
     * Retrieves a paginated list of ItemStacks for the specified icon category.
     *
     * @param category       The icon category to retrieve items for.
     * @param menu           The HomeChangeIconMenu instance.
     * @param home           The Home instance.
     * @param player         The Player requesting the items.
     * @param page           The page number to retrieve (0-based).
     * @param itemsPerPage   The number of items per page.
     * @return A list of ItemStacks representing the icons in the specified category for the given page.
     */
    public static List<ItemStack> getItemsForCategoryPaginated(IconCategory category, HomeChangeIconMenu menu,
                                                               Home home, Player player, int page, int itemsPerPage) {
        if (!initialized) initializeCategoryCache(category);

        List<CachedIconItem> cachedItems = CACHED_ITEMS.get(category);
        if (cachedItems == null) {
            initializeCategoryCache(category);
            cachedItems = CACHED_ITEMS.get(category);
        }

        int startIndex = page * itemsPerPage;
        int endIndex = Math.min(startIndex + itemsPerPage, cachedItems.size());

        if (startIndex >= cachedItems.size()) return Collections.emptyList();

        String cacheKey = category.name() + "_" + home.getIcon().getId() + "_page_" + page;
        List<ItemStack> cachedPage = RENDERED_ITEMS_CACHE.get(cacheKey);

        if (cachedPage != null) return cloneItemList(cachedPage);

        List<ItemStack> pageItems = new ArrayList<>();
        for (int i = startIndex; i < endIndex; i++) {
            pageItems.add(cachedItems.get(i).createItemForMenu(menu, home, player));
        }

        if (RENDERED_ITEMS_CACHE.size() > 200) RENDERED_ITEMS_CACHE.clear();
        RENDERED_ITEMS_CACHE.put(cacheKey, cloneItemList(pageItems));

        return pageItems;
    }
}