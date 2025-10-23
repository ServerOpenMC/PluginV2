package fr.openmc.core.features.dream.blocks.cloudvault;

import fr.openmc.core.features.dream.generation.DreamDimensionManager;
import fr.openmc.core.features.dream.items.DreamItemRegister;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Vault;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDispenseLootEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CloudVault implements Listener {
    public static void replaceBlockWithVault(Block block) {
        block.setType(Material.VAULT);

        if (block.getState() instanceof Vault vault) {
            vault.setKeyItem(DreamItemRegister.getByName("omc_dream:cloud_key").getBest());

            vault.setDisplayedItem(DreamItemRegister.getByName("omc_dream:cloud_key").getBest());
            vault.update();
        }
    }

    public static List<ItemStack> getLootCloudVault() {
        Random random = new Random();

        List<ItemStack> loot = new ArrayList<>();

        int luck = random.nextInt(100);

        if (luck < 65) {
            List<ItemStack> rolls = List.of(
                    DreamItemRegister.getByName("omc_dream:cloud_helmet").getBest(),
                    DreamItemRegister.getByName("omc_dream:cloud_chestplate").getBest(),
                    DreamItemRegister.getByName("omc_dream:cloud_leggings").getBest(),
                    DreamItemRegister.getByName("omc_dream:cloud_boots").getBest()
            );

            loot.add(rolls.get(random.nextInt(rolls.size())));
        } else if (luck < 80) {
            loot.add(DreamItemRegister.getByName("omc_dream:somnifere").getBest());
        } else if (luck < 90) {
            loot.add(DreamItemRegister.getByName("omc_dream:cloud_fishing_rod").getBest());
        }

        return loot;
    }

    @EventHandler
    public void onLootGenerate(BlockDispenseLootEvent event) {
        Player player = event.getPlayer();

        if (player == null) return;

        if (!player.getWorld().getName().equals(DreamDimensionManager.DIMENSION_NAME)) return;

        if (!(event.getBlock().getState() instanceof Vault)) return;

        event.setDispensedLoot(getLootCloudVault());
    }
}
