package fr.openmc.core.features.dream.mecanism.blocksdrops;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.dream.registries.DreamItemRegistry;
import fr.openmc.core.registry.items.CustomItem;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public class DreamBlocksDropsManager {

    private final static HashMap<Material, ItemStack> customDrops = new HashMap<>();

    public static void init() {
        OMCPlugin.registerEvents(new ChangeBlockDropsListener());

        registerCustomDrop(Material.SCULK, DreamItemRegistry.CORRUPTED_SCULK);
        registerCustomDrop(Material.PALE_OAK_WOOD, DreamItemRegistry.OLD_PALE_OAK_WOOD);
        registerCustomDrop(Material.ACACIA_WOOD, DreamItemRegistry.OLD_PALE_OAK_WOOD);
        registerCustomDrop(Material.CREAKING_HEART, DreamItemRegistry.CREAKING_HEART);
        registerCustomDrop(Material.BLUE_ICE, DreamItemRegistry.GLACITE);
        registerCustomDrop(Material.DEEPSLATE_COAL_ORE, DreamItemRegistry.BURN_COAL);
        registerCustomDrop(Material.DEEPSLATE, DreamItemRegistry.HARD_STONE);
        registerCustomDrop(Material.SMOOTH_BASALT, DreamItemRegistry.HARD_STONE);
        registerCustomDrop(Material.CRAFTING_TABLE, DreamItemRegistry.CRAFTING_TABLE);
        registerCustomDrop(Material.CAMPFIRE, DreamItemRegistry.ETERNAL_CAMPFIRE);
    }

    public static void registerCustomDrop(Material mat, CustomItem item) {
        customDrops.put(mat, item.getBest());
    }

    public static ItemStack getCustomDrop(Material mat) {
        return customDrops.get(mat);
    }
}
