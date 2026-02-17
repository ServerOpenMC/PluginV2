package fr.openmc.core.features.dream.registries.enchantements;

import fr.openmc.api.cooldown.DynamicCooldownManager;
import fr.openmc.core.features.dream.models.registry.DreamEnchantment;
import fr.openmc.core.registry.enchantments.CustomEnchantmentRegistry;
import fr.openmc.core.utils.DateUtils;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import io.papermc.paper.registry.data.EnchantmentRegistryEntry;
import io.papermc.paper.registry.keys.tags.ItemTypeTagKeys;
import io.papermc.paper.registry.tag.TagKey;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ItemType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@SuppressWarnings("UnstableApiUsage")
public class Soulbound extends DreamEnchantment implements Listener {
    public static HashMap<UUID, List<ItemStack>> soulboundItemsOnRespawn = new HashMap<>();

    @Override
    public Key getKey() {
        return Key.key("omc_dream:soulbound");
    }

    @Override
    public Component getName() {
        return Component.text("Soulbound");
    }

    @Override
    public TagKey<ItemType> getSupportedItems() {
        return ItemTypeTagKeys.ENCHANTABLE_WEAPON;
    }

    @Override
    public int getMaxLevel() {
        return 5;
    }

    @Override
    public int getWeight() {
        return 1;
    }

    @Override
    public int getAnvilCost() {
        return 8;
    }

    @Override
    public EnchantmentRegistryEntry.EnchantmentCost getMinimumCost() {
        return EnchantmentRegistryEntry.EnchantmentCost.of(1, 1);
    }

    @Override
    public EnchantmentRegistryEntry.EnchantmentCost getMaximalmCost() {
        return EnchantmentRegistryEntry.EnchantmentCost.of(3, 4);
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        Enchantment enchant = CustomEnchantmentRegistry.getCustomEnchantmentByKey(this.getKey()).getEnchantment();
        if (enchant == null) return;

        int maxSoulboundLevel = 0;
        boolean hasEnchantment = false;

        for (ItemStack item : player.getInventory().getContents()) {
            if (item == null) continue;
            if (!item.getEnchantments().containsKey(enchant)) continue;

            if (DynamicCooldownManager.isReady(uuid, "player:soulbound")) {
                hasEnchantment = true;
                maxSoulboundLevel = Math.max(maxSoulboundLevel, item.getEnchantmentLevel(enchant));
                event.getItemsToKeep().add(item);
                event.getDrops().remove(item);
            }
        }

        if (hasEnchantment && DynamicCooldownManager.isReady(uuid, "player:soulbound")) {
            event.setShouldDropExperience(false);
            DynamicCooldownManager.use(uuid, "player:soulbound", getCooldown(maxSoulboundLevel));
            MessagesManager.sendMessage(player, Component.text("Votre enchantement Soulbound a fait effet ! Prochaine utilisation dans " + DateUtils.convertMillisToTime(getCooldown(maxSoulboundLevel))), Prefix.DREAM, MessageType.SUCCESS, false);
        }
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        player.give(soulboundItemsOnRespawn.getOrDefault(uuid, new ArrayList<>()));
        soulboundItemsOnRespawn.remove(uuid);
    }

    private long getCooldown(int level) {
        return switch (level) {
            case 2 -> 45 * 60 * 1000L;
            case 3 -> 30 * 60 * 1000L;
            case 4 -> 20 * 60 * 1000L;
            case 5 -> 15 * 60 * 1000L;
            default -> 60 * 60 * 1000L;
        };
    }
}
