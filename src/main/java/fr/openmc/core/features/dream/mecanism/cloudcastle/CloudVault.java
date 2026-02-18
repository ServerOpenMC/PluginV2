package fr.openmc.core.features.dream.mecanism.cloudcastle;

import fr.openmc.core.features.dream.DreamUtils;
import fr.openmc.core.features.dream.registries.DreamItemRegistry;
import fr.openmc.core.registry.enchantments.CustomEnchantmentRegistry;
import net.kyori.adventure.key.Key;
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
            vault.setKeyItem(DreamItemRegistry.getByName("cloud_key").getBest());

            vault.setDisplayedItem(DreamItemRegistry.getByName("cloud_key").getBest());
            vault.update();
        }
    }

    public static List<ItemStack> getLootCloudVault() {
        Random random = new Random();

        List<ItemStack> loot = new ArrayList<>();

        for (int i = 0; i < 2; i++) {
            int luck = random.nextInt(100);

            if (luck < 50) {
                List<ItemStack> rolls = List.of(
                        DreamItemRegistry.getByName("cloud_helmet").getBest(),
                        DreamItemRegistry.getByName("cloud_chestplate").getBest(),
                        DreamItemRegistry.getByName("cloud_leggings").getBest(),
                        DreamItemRegistry.getByName("cloud_boots").getBest()
                );

                loot.add(rolls.get(random.nextInt(rolls.size())));
            } else if (luck < 75) {
                loot.add(DreamItemRegistry.getByName("somnifere").getBest());
            } else if (luck < 90) {
                loot.add(DreamItemRegistry.getByName("cloud_fishing_rod").getBest());
            } else {
                ItemStack bookEnchanted = CustomEnchantmentRegistry.getCustomEnchantmentByKey(
                        Key.key("omc_dream:dream_sleeper")
                ).getEnchantedBookItem(2).getBest();
                loot.add(bookEnchanted);
            }
        }

        return loot;
    }

    @EventHandler
    public void onLootGenerate(BlockDispenseLootEvent event) {
        Player player = event.getPlayer();

        if (player == null) return;

        if (!DreamUtils.isInDreamWorld(player)) return;

        if (!(event.getBlock().getState() instanceof Vault)) return;

        event.setDispensedLoot(getLootCloudVault());
    }
}
