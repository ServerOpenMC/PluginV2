package fr.openmc.core.features.dream.mecanism.blocksdrops;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.dream.registries.DreamItemRegistry;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public class DreamBlocksDropsManager {

    private final static HashMap<Material, ItemStack> customDrops = new HashMap<>();

    public static void init() {
        OMCPlugin.registerEvents(new ChangeBlockDropsListener());

        registerCustomDrop(Material.SCULK, DreamItemRegistry.getByName("corrupted_sculk").getBest());
        registerCustomDrop(Material.PALE_OAK_WOOD, DreamItemRegistry.getByName("old_pale_oak").getBest());
        registerCustomDrop(Material.ACACIA_WOOD, DreamItemRegistry.getByName("old_pale_oak").getBest());
        registerCustomDrop(Material.CREAKING_HEART, DreamItemRegistry.getByName("creaking_heart").getBest());
        registerCustomDrop(Material.BLUE_ICE, DreamItemRegistry.getByName("glacite").getBest());
        registerCustomDrop(Material.DEEPSLATE_COAL_ORE, DreamItemRegistry.getByName("coal_burn").getBest());
        registerCustomDrop(Material.DEEPSLATE, DreamItemRegistry.getByName("hard_stone").getBest());
        registerCustomDrop(Material.SMOOTH_BASALT, DreamItemRegistry.getByName("hard_stone").getBest());
        registerCustomDrop(Material.CRAFTING_TABLE, DreamItemRegistry.getByName("crafting_table").getBest());
        registerCustomDrop(Material.CAMPFIRE, DreamItemRegistry.getByName("eternal_campfire").getBest());
    }

    public static void registerCustomDrop(Material mat, ItemStack item) {
        customDrops.put(mat, item);
    }

    public static ItemStack getCustomDrop(Material mat) {
        return customDrops.get(mat);
    }
}
