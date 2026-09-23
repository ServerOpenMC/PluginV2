package fr.openmc.core.features.dream.mecanism.blocksdrops;

import fr.openmc.core.OMCRegistry;
import fr.openmc.core.lifecycle.interfaces.HasListeners;
import fr.openmc.core.lifecycle.listeners.ListenerFactory;
import fr.openmc.core.registry.features.Feature;
import fr.openmc.core.registry.items.CustomItem;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Set;

public class DreamBlocksDropsManager extends Feature implements HasListeners {

    private final HashMap<Material, ItemStack> customDrops = new HashMap<>();

    @Override
    public void init() {
        registerCustomDrop(Material.SCULK, OMCRegistry.DREAM_ITEM.CORRUPTED_SCULK);
        registerCustomDrop(Material.PALE_OAK_WOOD, OMCRegistry.DREAM_ITEM.OLD_PALE_OAK_WOOD);
        registerCustomDrop(Material.ACACIA_WOOD, OMCRegistry.DREAM_ITEM.OLD_PALE_OAK_WOOD);
        registerCustomDrop(Material.CREAKING_HEART, OMCRegistry.DREAM_ITEM.CREAKING_HEART);
        registerCustomDrop(Material.BLUE_ICE, OMCRegistry.DREAM_ITEM.GLACITE);
        registerCustomDrop(Material.DEEPSLATE_COAL_ORE, OMCRegistry.DREAM_ITEM.BURN_COAL);
        registerCustomDrop(Material.DEEPSLATE, OMCRegistry.DREAM_ITEM.HARD_STONE);
        registerCustomDrop(Material.SMOOTH_BASALT, OMCRegistry.DREAM_ITEM.HARD_STONE);
        registerCustomDrop(Material.CRAFTING_TABLE, OMCRegistry.DREAM_ITEM.CRAFTING_TABLE);
        registerCustomDrop(Material.CAMPFIRE, OMCRegistry.DREAM_ITEM.ETERNAL_CAMPFIRE);
    }
    @Override
    public Set<ListenerFactory> getListeners() {
        return Set.of(
                ChangeBlockDropsListener::new
        );
    }

    public void registerCustomDrop(Material mat, CustomItem item) {
        customDrops.put(mat, item.getBest());
    }

    public ItemStack getCustomDrop(Material mat) {
        return customDrops.get(mat);
    }
}
