package fr.openmc.core.features.dream.registries;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.dream.DreamUtils;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public class DreamBlocksDropsRegistry implements Listener {

    private final static HashMap<Material, ItemStack> customDrops = new HashMap<>();

    public static void init() {
        OMCPlugin.registerEvents(new DreamBlocksDropsRegistry());

        registerCustomDrop(Material.SCULK, DreamItemRegistry.getByName("omc_dream:corrupted_sculk").getBest());
        registerCustomDrop(Material.PALE_OAK_WOOD, DreamItemRegistry.getByName("omc_dream:old_pale_oak").getBest());
        registerCustomDrop(Material.ACACIA_WOOD, DreamItemRegistry.getByName("omc_dream:old_pale_oak").getBest());
        registerCustomDrop(Material.CREAKING_HEART, DreamItemRegistry.getByName("omc_dream:creaking_heart").getBest());
        registerCustomDrop(Material.BLUE_ICE, DreamItemRegistry.getByName("omc_dream:glacite").getBest());
        registerCustomDrop(Material.DEEPSLATE_COAL_ORE, DreamItemRegistry.getByName("omc_dream:coal_burn").getBest());
        registerCustomDrop(Material.DEEPSLATE, DreamItemRegistry.getByName("omc_dream:hard_stone").getBest());
        registerCustomDrop(Material.SMOOTH_BASALT, DreamItemRegistry.getByName("omc_dream:hard_stone").getBest());
        registerCustomDrop(Material.CRAFTING_TABLE, DreamItemRegistry.getByName("omc_dream:crafting_table").getBest());
        registerCustomDrop(Material.CAMPFIRE, DreamItemRegistry.getByName("omc_dream:eternal_campfire").getBest());
    }

    public static void registerCustomDrop(Material mat, ItemStack item) {
        customDrops.put(mat, item);
    }

    public static ItemStack getCustomDrop(Material mat) {
        return customDrops.get(mat);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();

        if (!DreamUtils.isDreamWorld(block.getLocation())) return;

        Material type = block.getType();

        ItemStack customDrop = DreamBlocksDropsRegistry.getCustomDrop(type);

        event.setDropItems(false);
        event.setExpToDrop(0);

        if (customDrop != null && !event.getPlayer().getGameMode().equals(GameMode.CREATIVE)) {
            block.getWorld().dropItemNaturally(block.getLocation(), customDrop);
        }
    }
}
