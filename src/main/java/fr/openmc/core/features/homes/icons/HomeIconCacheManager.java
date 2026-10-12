package fr.openmc.core.features.homes.icons;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.homes.menu.HomeChangeIconMenu;
import fr.openmc.core.features.homes.models.Home;
import fr.openmc.core.lifecycle.integration.OMCLogger;
import fr.openmc.core.registry.features.Feature;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class HomeIconCacheManager extends Feature {

    private final ConcurrentHashMap<HomeIcon.IconCategory, List<CachedIconItem>> cachedItems = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, List<CachedIconItem>> cachedSearchResults = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, List<ItemStack>> renderedItemsCache = new ConcurrentHashMap<>();
    private volatile boolean initialized = false;

    private static final Integer MAX_CACHE_SIZE = 50;

    @Override
    public void onEnable() {
        this.initialize();
    }

    @Override
    public void onDisable() {
        this.clearCache();
    }

    /**
     * Initializes the HomeIconCacheManager and preloads all icons into the cache.
     * This method should be called once at the start of the plugin.
     */
    public synchronized void initialize() {
        if (initialized) return;
        HomeIconRegistry.initializeIcons();

        Bukkit.getScheduler().runTaskAsynchronously(OMCPlugin.getInstance(), () -> {
            try {
                for (HomeIcon.IconCategory category : HomeIcon.IconCategory.values())
                    initializeCategoryCache(category);

                initialized = true;
                OMCLogger.info("Initialized icon cache manager");
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
    private void initializeCategoryCache(HomeIcon.IconCategory category) {
        List<HomeIcon> icons = HomeIconRegistry.getIconsByCategory(category);
        List<CachedIconItem> cachedItems = new ArrayList<>();

        for (HomeIcon icon : icons) {
            try {
                ItemStack baseItem = icon.getItemStack().clone();
                cachedItems.add(new CachedIconItem(icon, baseItem));
            } catch (Exception e) {
                OMCLogger.warn("Failed to create base item for icon: {} - {}", icon.id(), e.getMessage(), e);
            }
        }

        this.cachedItems.put(category, cachedItems);
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
    public List<ItemStack> getItemsForCategory(HomeIcon.IconCategory category, HomeChangeIconMenu menu, Home home, Player player) {
        if (!initialized) initializeCategoryCache(category);

        List<CachedIconItem> cachedItems = this.cachedItems.get(category);
        if (cachedItems == null) {
            initializeCategoryCache(category);
            cachedItems = this.cachedItems.get(category);
        }

        String cacheKey = category.name() + "_" + home.getIcon().id();

        List<ItemStack> cachedRenderedItems = renderedItemsCache.get(cacheKey);
        if (cachedRenderedItems != null && cachedRenderedItems.size() == cachedItems.size()) return cloneItemList(cachedRenderedItems);

        List<ItemStack> items = new ArrayList<>();
        cachedItems.forEach(cachedItem ->
                items.add(cachedItem.createItemForMenu(menu, home, player))
        );

        if (renderedItemsCache.size() > MAX_CACHE_SIZE) renderedItemsCache.clear(); // Limit cache size to avoid memory issues

        renderedItemsCache.put(cacheKey, cloneItemList(items));
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
    public List<ItemStack> searchIcons(String query, HomeChangeIconMenu menu, Home home, Player player) {
        if (query == null || query.trim().isEmpty()) return new ArrayList<>();

        String normalizedQuery = query.toLowerCase().trim();
        String cacheKey = "search_" + normalizedQuery + "_" + home.getIcon().id();

        List<ItemStack> cachedRenderedItems = renderedItemsCache.get(cacheKey);
        if (cachedRenderedItems != null) {
            return cloneItemList(cachedRenderedItems);
        }

        List<CachedIconItem> cachedItems = cachedSearchResults.get(normalizedQuery);

        if (cachedItems == null) {
            List<HomeIcon> searchResults = HomeIconRegistry.searchIcons(normalizedQuery);
            cachedItems = new ArrayList<>();
            for (HomeIcon icon : searchResults) {
                try {
                    ItemStack baseItem = icon.getItemStack().clone();
                    cachedItems.add(new CachedIconItem(icon, baseItem));
                } catch (Exception e) {
                    OMCLogger.warn("Failed to create base item for icon: {} - {}", icon.id(), e.getMessage(), e);
                }
            }

            if (cachedSearchResults.size() > MAX_CACHE_SIZE) cachedSearchResults.clear(); // Limit cache size to avoid memory issues
            cachedSearchResults.put(normalizedQuery, cachedItems);
        }

        List<ItemStack> items = new ArrayList<>();
        cachedItems.forEach(cachedItem -> items.add(cachedItem.createItemForMenu(menu, home, player)));

        if (renderedItemsCache.size() > MAX_CACHE_SIZE) renderedItemsCache.clear(); // Limit cache size to avoid memory issues
        renderedItemsCache.put(cacheKey, cloneItemList(items));

        return items;
    }

    /**
     * Clones a list of ItemStacks to avoid modifying the original items.
     *
     * @param items The list of ItemStacks to clone.
     * @return A new list containing cloned ItemStacks.
     */
    private List<ItemStack> cloneItemList(List<ItemStack> items) {
        List<ItemStack> clonedItems = new ArrayList<>();
        items.forEach(item -> clonedItems.add(item.clone()));
        return clonedItems;
    }

    /**
     * Clears the cache and resets the initialized state.
     * <p>
     * This method can be used to reset the cache if needed.
     */
    public synchronized void clearCache() {
        cachedItems.clear();
        cachedSearchResults.clear();
        renderedItemsCache.clear();
        initialized = false;
    }

    public void clearRenderedCache() {
        renderedItemsCache.clear();
    }

    /**
     * Reloads the cache by clearing it and reinitializing.
     * <p>
     * This method can be used to refresh the cache without restarting the plugin.
     */
    public void reload() {
        clearCache();
        initialize();
    }

    /**
     * Returns the current cache statistics.
     *
     * @return A string containing the cache statistics.
     */
    public String getCacheStats() {
        StringBuilder stats = new StringBuilder();
        stats.append("HomeIconCacheManager Stats:\n");
        stats.append("- Initialisé: ").append(initialized).append("\n");
        stats.append("- Catégories en cache: ").append(cachedItems.size()).append("\n");

        for (HomeIcon.IconCategory category : cachedItems.keySet()) {
            List<CachedIconItem> items = cachedItems.get(category);
            stats.append("  - ").append(category).append(": ").append(items != null ? items.size() : 0).append(" items\n");
        }

        stats.append("- Recherches en cache: ").append(cachedSearchResults.size()).append("\n");
        stats.append("- Items rendus en cache: ").append(renderedItemsCache.size()).append("\n");

        return stats.toString();
    }
}
