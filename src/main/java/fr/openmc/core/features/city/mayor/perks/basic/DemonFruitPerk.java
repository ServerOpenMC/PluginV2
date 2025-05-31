package fr.openmc.core.features.city.mayor.perks.basic;

import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.mayor.managers.MayorManager;
import fr.openmc.core.features.city.mayor.managers.PerkManager;
import fr.openmc.core.features.city.mayor.perks.Perks;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class DemonFruitPerk implements Listener {
    private static final NamespacedKey RANGE_MODIFIER_KEY = new NamespacedKey("mayor_perks","demon_fruit");
    private static final double BONUS_VALUE = 1.0;

    /**
     * Applies the reach bonus to the player.
     *
     * @param player The player to apply the bonus to.
     */
    public static void applyReachBonus(Player player) {
        if (player.getAttribute(Attribute.ENTITY_INTERACTION_RANGE) == null && player.getAttribute(Attribute.BLOCK_INTERACTION_RANGE) == null) {
            return;
        }

        player.getAttribute(Attribute.ENTITY_INTERACTION_RANGE)
                .getModifiers()
                .forEach(modifierEntity -> {
                    if (modifierEntity.getKey().equals(RANGE_MODIFIER_KEY)) {
                        player.getAttribute(Attribute.ENTITY_INTERACTION_RANGE).removeModifier(modifierEntity);
                    }
                });

        AttributeModifier modifierEntity = new AttributeModifier(RANGE_MODIFIER_KEY, BONUS_VALUE, AttributeModifier.Operation.ADD_NUMBER);
        player.getAttribute(Attribute.ENTITY_INTERACTION_RANGE).addModifier(modifierEntity);

        player.getAttribute(Attribute.BLOCK_INTERACTION_RANGE)
                .getModifiers()
                .forEach(modifierBlock -> {
                    if (modifierBlock.getKey().equals(RANGE_MODIFIER_KEY)) {
                        player.getAttribute(Attribute.BLOCK_INTERACTION_RANGE).removeModifier(modifierBlock);
                    }
                });

        AttributeModifier modifierBlock = new AttributeModifier(RANGE_MODIFIER_KEY, BONUS_VALUE, AttributeModifier.Operation.ADD_NUMBER);
        player.getAttribute(Attribute.BLOCK_INTERACTION_RANGE).addModifier(modifierBlock);
    }

    /**
     * Removes the reach bonus from the player.
     *
     * @param player The player to remove the bonus from.
     */
    public static void removeReachBonus(Player player) {
        if (player.getAttribute(Attribute.ENTITY_INTERACTION_RANGE) == null && player.getAttribute(Attribute.BLOCK_INTERACTION_RANGE) == null) return;
        try {
            player.getAttribute(Attribute.ENTITY_INTERACTION_RANGE)
                    .getModifiers()
                    .stream()
                    .filter(modifier -> modifier.getKey().equals(RANGE_MODIFIER_KEY))
                    .forEach(modifier -> {
                        player.getAttribute(Attribute.ENTITY_INTERACTION_RANGE).removeModifier(modifier);
                    });

                player.getAttribute(Attribute.BLOCK_INTERACTION_RANGE)
                    .getModifiers()
                    .stream()
                    .filter(modifier -> modifier.getKey().equals(RANGE_MODIFIER_KEY))
                    .forEach(modifier -> {
                        player.getAttribute(Attribute.BLOCK_INTERACTION_RANGE).removeModifier(modifier);
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Checks if the player has the reach attribute bonus.
     *
     * @param player The player to check.
     * @return true if the player has the reach attribute bonus, false otherwise.
     */
    public static boolean hasRangeAttribute(Player player) {
        if (player.getAttribute(Attribute.ENTITY_INTERACTION_RANGE) == null && player.getAttribute(Attribute.BLOCK_INTERACTION_RANGE) == null) return false;

        double baseValueEntity = player.getAttribute(Attribute.ENTITY_INTERACTION_RANGE).getBaseValue();
        double currentValueEntity = player.getAttribute(Attribute.ENTITY_INTERACTION_RANGE).getValue();
        double expectedValueEntity = baseValueEntity + BONUS_VALUE;

        double baseValueBlock = player.getAttribute(Attribute.BLOCK_INTERACTION_RANGE).getBaseValue();
        double currentValueBlock = player.getAttribute(Attribute.BLOCK_INTERACTION_RANGE).getValue();
        double expectedValueBlock = baseValueBlock + BONUS_VALUE;

        return Math.abs(currentValueEntity - expectedValueEntity) < 0.01 && Math.abs(currentValueBlock - expectedValueBlock) < 0.01;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        int phase = MayorManager.phaseMayor;

        if (phase == 2) {
            City playerCity = CityManager.getPlayerCity(player.getUniqueId());
            if (playerCity == null) return;

            if (!PerkManager.hasPerk(playerCity.getMayor(), Perks.FRUIT_DEMON.getId())) return;

            if (!hasRangeAttribute(player)) applyReachBonus(player);
        } else {
            removeReachBonus(player);
        }
    }


    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (hasRangeAttribute(player)) {
            removeReachBonus(player);
        }
    }
}
