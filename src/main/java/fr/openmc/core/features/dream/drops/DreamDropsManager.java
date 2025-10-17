package fr.openmc.core.features.dream.drops;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.dream.items.DreamItemRegister;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public class DreamDropsManager {

    private final static HashMap<Material, ItemStack> customDrops = new HashMap<>();

    public static void init() {
        OMCPlugin.registerEvents(
                new DreamDropsListener()
        );

        registerCustomDrop(Material.SCULK, DreamItemRegister.getByName("omc_dream:corrupted_sculk").getBest());
        registerCustomDrop(Material.PALE_OAK_WOOD, DreamItemRegister.getByName("omc_dream:old_pale_oak").getBest());
        registerCustomDrop(Material.ACACIA_WOOD, DreamItemRegister.getByName("omc_dream:old_pale_oak").getBest());
        registerCustomDrop(Material.CREAKING_HEART, DreamItemRegister.getByName("omc_dream:creaking_heart").getBest());
    }

    public static void registerCustomDrop(Material mat, ItemStack item) {
        customDrops.put(mat, item);
    }

    public static ItemStack getCustomDrop(Material mat) {
        return customDrops.get(mat);
    }
}
