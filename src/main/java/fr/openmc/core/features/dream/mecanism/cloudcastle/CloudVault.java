package fr.openmc.core.features.dream.mecanism.cloudcastle;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.OMCRegistry;
import fr.openmc.core.features.dream.DreamUtils;
import fr.openmc.core.features.dream.mecanism.rng.DreamRngLootEvent;
import fr.openmc.core.features.dream.registries.DreamItemRegistry;
import fr.openmc.core.registry.loottable.CustomLootTable;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Vault;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDispenseLootEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class CloudVault implements Listener {
    private final CustomLootTable CLOUD_VAULT_LOOT_TABLE = OMCRegistry.CUSTOM_LOOT_TABLES.get("omc_dream:cloud_vault");

    public static void replaceBlockWithVault(Block block) {
        block.setType(Material.VAULT);

        if (block.getState() instanceof Vault vault) {
            vault.setKeyItem(DreamItemRegistry.getByName("cloud_key").getBest());

            vault.setDisplayedItem(DreamItemRegistry.getByName("cloud_key").getBest());
            vault.update();
        }
    }

    @EventHandler
    public void onLootGenerate(BlockDispenseLootEvent event) {
        Player player = event.getPlayer();

        if (player == null) return;

        if (!DreamUtils.isInDreamWorld(player)) return;

        if (!(event.getBlock().getState() instanceof Vault)) return;
        if (CLOUD_VAULT_LOOT_TABLE == null) return;


        List<ItemStack> loot = CLOUD_VAULT_LOOT_TABLE.rollLootsWithAmount(3);
        event.setDispensedLoot(loot);

        for (ItemStack item : loot) {
            Bukkit.getScheduler().runTask(OMCPlugin.getInstance(), () ->
                    Bukkit.getServer().getPluginManager().callEvent(new DreamRngLootEvent(player, item, item.getAmount(), CLOUD_VAULT_LOOT_TABLE.getChanceOf(item)))
            );
        }
    }
}
