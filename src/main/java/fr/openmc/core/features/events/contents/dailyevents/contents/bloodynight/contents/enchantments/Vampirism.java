package fr.openmc.core.features.events.contents.dailyevents.contents.bloodynight.contents.enchantments;

import fr.openmc.core.features.dream.models.registry.DreamEnchantment;
import fr.openmc.core.utils.text.messages.TranslationManager;
import io.papermc.paper.registry.data.EnchantmentRegistryEntry;
import io.papermc.paper.registry.keys.tags.ItemTypeTagKeys;
import io.papermc.paper.registry.tag.TagKey;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ItemType;

@SuppressWarnings("UnstableApiUsage")
public class Vampirism extends DreamEnchantment implements Listener {
    private static final NamespacedKey MAX_HEALTH_MODIFIER_KEY =
            new NamespacedKey("omc_daily_events", "vampirism_max_health");

    @Override
    public Key getKey() {
        return Key.key("omc_daily_events:vampirism");
    }

    @Override
    public Component getName() {
        return TranslationManager.translation("feature.dailyevents.bloody_night.enchantment.vampirism.name");
    }

    @Override
    public TagKey<ItemType> getSupportedItems() {
        return ItemTypeTagKeys.SWORDS;
    }

    @Override
    public int getMaxLevel() {
        return 3;
    }

    @Override
    public int getWeight() {
        return 1;
    }

    @Override
    public int getAnvilCost() {
        return 4;
    }

    @Override
    public EnchantmentRegistryEntry.EnchantmentCost getMinimumCost() {
        return EnchantmentRegistryEntry.EnchantmentCost.of(1, 2);
    }

    @Override
    public EnchantmentRegistryEntry.EnchantmentCost getMaximalmCost() {
        return EnchantmentRegistryEntry.EnchantmentCost.of(4, 5);
    }

    @EventHandler
    public void onAttack(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player player)) return;
        if (!(event.getEntity() instanceof LivingEntity)) return;

        Enchantment enchant = this.getEnchantment();
        if (enchant == null) return;

        ItemStack item = player.getInventory().getItemInMainHand();
        if (!item.getEnchantments().containsKey(enchant)) return;
        int level = item.getEnchantmentLevel(enchant);

        double damageDealt = event.getFinalDamage();
        if (damageDealt <= 0) return;

        healPlayer(player, damageDealt);

        if (level >= 2) {
            double max = getMaxHealthForLevel(level);

            AttributeInstance maxHealthAttr = player.getAttribute(Attribute.MAX_HEALTH);
            if (maxHealthAttr == null) return;

            maxHealthAttr.addModifier(new AttributeModifier(
                    MAX_HEALTH_MODIFIER_KEY,
                    max - maxHealthAttr.getValue(),
                    AttributeModifier.Operation.ADD_NUMBER
            ));
        }
    }

    @EventHandler
    public void onItemHeldChange(PlayerItemHeldEvent event) {
        Player player = event.getPlayer();

        Enchantment enchant = this.getEnchantment();
        if (enchant == null) return;

        ItemStack item = player.getInventory().getItem(event.getPreviousSlot());
        if (item == null) return;
        if (!item.getEnchantments().containsKey(enchant)) return;

        int level = item.getEnchantmentLevel(enchant);

        if (level < 2) return;

        AttributeInstance maxHealthAttr = player.getAttribute(Attribute.MAX_HEALTH);
        if (maxHealthAttr == null) return;
        maxHealthAttr.removeModifier(MAX_HEALTH_MODIFIER_KEY);
    }

    /**
     * Donne 1/8 de vie en fonction des dégâts infligés à l'attaquant.
     * @param player le joueur ciblé
     * @param damageDealt le nombre de dégats mis
     */
    private void healPlayer(Player player, double damageDealt) {
        AttributeInstance maxHealthAttr = player.getAttribute(Attribute.MAX_HEALTH);
        if (maxHealthAttr == null) return;

        double maxHealth = maxHealthAttr.getValue();
        double heal = damageDealt * 0.125;
        double newHealth = Math.min(maxHealth, player.getHealth() + heal);

        player.setHealth(newHealth);
    }

    /**
     * Donne la vie maximale que le joueur peut avoir en fonction du niveau de l'enchantement.
     * @param level le niveau de l'enchantement
     * @return la vie maximale
     */
    private double getMaxHealthForLevel(int level) {
        return switch (level) {
            case 2 -> 25.0;
            case 3 -> 30.0;
            default -> 0.0;
        };
    }
}