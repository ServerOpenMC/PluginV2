package fr.openmc.core.features.dream.mecanism.cloudcastle;

import fr.openmc.core.features.dream.DreamUtils;
import fr.openmc.core.features.dream.registries.DreamItemRegistry;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import net.kyori.adventure.key.Key;
import org.bukkit.Material;
import org.bukkit.Registry;
import org.bukkit.block.Block;
import org.bukkit.block.Vault;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDispenseLootEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CloudVault implements Listener {
    public static void replaceBlockWithVault(Block block) {
        block.setType(Material.VAULT);

        if (block.getState() instanceof Vault vault) {
            vault.setKeyItem(DreamItemRegistry.getByName("omc_dream:cloud_key").getBest());

            vault.setDisplayedItem(DreamItemRegistry.getByName("omc_dream:cloud_key").getBest());
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
                        DreamItemRegistry.getByName("omc_dream:cloud_helmet").getBest(),
                        DreamItemRegistry.getByName("omc_dream:cloud_chestplate").getBest(),
                        DreamItemRegistry.getByName("omc_dream:cloud_leggings").getBest(),
                        DreamItemRegistry.getByName("omc_dream:cloud_boots").getBest()
                );

                loot.add(rolls.get(random.nextInt(rolls.size())));
            } else if (luck < 75) {
                loot.add(DreamItemRegistry.getByName("omc_dream:somnifere").getBest());
            } else if (luck < 90) {
                loot.add(DreamItemRegistry.getByName("omc_dream:cloud_fishing_rod").getBest());
            } else {
                ItemStack bookEnchanted = new ItemStack(Material.ENCHANTED_BOOK);
                EnchantmentStorageMeta meta = (EnchantmentStorageMeta) bookEnchanted.getItemMeta();

                Registry<@NotNull Enchantment> enchantmentRegistry = RegistryAccess
                        .registryAccess()
                        .getRegistry(RegistryKey.ENCHANTMENT);

                Enchantment enchantment = enchantmentRegistry.getOrThrow(
                        RegistryKey.ENCHANTMENT.typedKey(Key.key("dream:dream_sleeper"))
                );

                meta.addStoredEnchant(enchantment, 2, false);
                bookEnchanted.setItemMeta(meta);
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
