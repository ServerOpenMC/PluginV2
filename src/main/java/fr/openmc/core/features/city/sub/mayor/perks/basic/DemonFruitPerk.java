package fr.openmc.core.features.city.sub.mayor.perks.basic;

import fr.openmc.core.features.city.models.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.sub.mayor.managers.MayorManager;
import fr.openmc.core.features.city.sub.mayor.perks.PerkUtils;
import fr.openmc.core.features.city.sub.mayor.perks.Perks;
import fr.openmc.core.features.dream.DreamUtils;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public class DemonFruitPerk implements Listener {
    private static final NamespacedKey RANGE_MODIFIER_KEY = new NamespacedKey("mayor_perks","demon_fruit");
    private static final double BONUS_VALUE = 1.0;

    private final CityManager cityManager;
    private final MayorManager mayorManager;

    public DemonFruitPerk(CityManager cityManager, MayorManager mayorManager) {
        this.cityManager = cityManager;
        this.mayorManager = mayorManager;
    }

    /**
     * Applies the reach bonus to the player.
     *
     * @param player The player to apply the bonus to.
     */
    public static void applyReachBonus(Player player) {
        AttributeInstance entityInteraction = player.getAttribute(Attribute.ENTITY_INTERACTION_RANGE);
        AttributeInstance blockInteraction = player.getAttribute(Attribute.BLOCK_INTERACTION_RANGE);

        if (entityInteraction == null) return;
        if (blockInteraction == null) return;

        if (!entityInteraction.getModifiers().isEmpty())
            entityInteraction.getModifiers()
                    .forEach(modifierEntity -> {
                        if (modifierEntity.getKey().equals(RANGE_MODIFIER_KEY)) {
                            entityInteraction.removeModifier(modifierEntity);
                        }
                    });

        AttributeModifier modifierEntity = new AttributeModifier(RANGE_MODIFIER_KEY, BONUS_VALUE, AttributeModifier.Operation.ADD_NUMBER);
        entityInteraction.addModifier(modifierEntity);

        if (!blockInteraction.getModifiers().isEmpty())
            blockInteraction.getModifiers()
                .forEach(modifierBlock -> {
                    if (modifierBlock.getKey().equals(RANGE_MODIFIER_KEY)) {
                        blockInteraction.removeModifier(modifierBlock);
                    }
                });

        AttributeModifier modifierBlock = new AttributeModifier(RANGE_MODIFIER_KEY, BONUS_VALUE, AttributeModifier.Operation.ADD_NUMBER);
        blockInteraction.addModifier(modifierBlock);
    }

    /**
     * Removes the reach bonus from the player.
     *
     * @param player The player to remove the bonus from.
     */
    public static void removeReachBonus(Player player) {
        AttributeInstance entityInteraction = player.getAttribute(Attribute.ENTITY_INTERACTION_RANGE);
        AttributeInstance blockInteraction = player.getAttribute(Attribute.BLOCK_INTERACTION_RANGE);

        if (entityInteraction == null) return;
        if (blockInteraction == null) return;

        try {
            entityInteraction.getModifiers()
                    .stream()
                    .filter(modifier -> modifier.getKey().equals(RANGE_MODIFIER_KEY))
                    .forEach(entityInteraction::removeModifier);

            blockInteraction.getModifiers()
                    .stream()
                    .filter(modifier -> modifier.getKey().equals(RANGE_MODIFIER_KEY))
                    .forEach(blockInteraction::removeModifier);
        } catch (Exception e) {
            throw new RuntimeException(e);
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

        AttributeInstance entityInteraction = player.getAttribute(Attribute.ENTITY_INTERACTION_RANGE);
        AttributeInstance blockInteraction = player.getAttribute(Attribute.BLOCK_INTERACTION_RANGE);

        if (entityInteraction == null) return false;
        if (blockInteraction == null) return false;

        double baseValueEntity = entityInteraction.getBaseValue();
        double currentValueEntity = entityInteraction.getValue();
        double expectedValueEntity = baseValueEntity + BONUS_VALUE;

        double baseValueBlock = blockInteraction.getBaseValue();
        double currentValueBlock = blockInteraction.getValue();
        double expectedValueBlock = baseValueBlock + BONUS_VALUE;

        return Math.abs(currentValueEntity - expectedValueEntity) < 0.01 && Math.abs(currentValueBlock - expectedValueBlock) < 0.01;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        int phase = mayorManager.phaseMayor;

        if (phase == 2) {
            City playerCity = City.ofPlayer(player);
            if (playerCity == null) return;

            if (!PerkUtils.hasPerk(playerCity.getMayor(), Perks.FRUIT_DEMON.getId())) return;

            if (!hasRangeAttribute(player)) applyReachBonus(player);
        } else {
            removeReachBonus(player);
        }
    }

    @EventHandler
    public void onDreamEntrered(PlayerTeleportEvent event) {
        Player player = event.getPlayer();

        if (!DreamUtils.isDreamWorld(event.getTo())) return;
        if (DreamUtils.isDreamWorld(event.getFrom())) return;

        if (hasRangeAttribute(player)) {
            removeReachBonus(player);
        }
    }

    @EventHandler
    public void onDreamLeave(PlayerTeleportEvent event) {
        if (!DreamUtils.isDreamWorld(event.getFrom())) return;
        if (DreamUtils.isDreamWorld(event.getTo())) return;

        Player player = event.getPlayer();
        int phase = mayorManager.phaseMayor;

        if (phase == 2) {
            City playerCity = City.ofPlayer(player);
            if (playerCity == null) return;

            if (!PerkUtils.hasPerk(playerCity.getMayor(), Perks.FRUIT_DEMON.getId())) return;

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
