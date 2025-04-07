package fr.openmc.core.features.city.mayor.perks;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.mayor.managers.MayorManager;
import fr.openmc.core.features.city.mayor.managers.PerkManager;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.potion.PotionEffectType;

import java.util.UUID;

public class DemonFruitPerk implements Listener {
    private static final UUID RANGE_MODIFIER_UUID = UUID.fromString("c3b98a4c-87f3-4b19-bd4c-3e6f6e4c1f2b");
    private static final String RANGE_MODIFIER_NAME = "DemonFruitPerk";
    private static final double BONUS_VALUE = 1.0;

    public static void applyReachBonus(Player player) {
        if (player.getAttribute(Attribute.ENTITY_INTERACTION_RANGE) == null) {
            return;
        }

        player.getAttribute(Attribute.ENTITY_INTERACTION_RANGE)
                .getModifiers()
                .forEach(modifier -> {
                    if (modifier.getName().equals(RANGE_MODIFIER_NAME)) {
                        player.getAttribute(Attribute.ENTITY_INTERACTION_RANGE).removeModifier(modifier);
                    }
                });

        AttributeModifier modifier = new AttributeModifier(RANGE_MODIFIER_UUID, RANGE_MODIFIER_NAME, BONUS_VALUE, AttributeModifier.Operation.ADD_NUMBER);
        player.getAttribute(Attribute.ENTITY_INTERACTION_RANGE).addModifier(modifier);
    }

    public static void removeReachBonus(Player player) {
        if (player.getAttribute(Attribute.ENTITY_INTERACTION_RANGE) == null) return;

        player.getAttribute(Attribute.ENTITY_INTERACTION_RANGE)
                .getModifiers()
                .stream()
                .filter(modifier -> modifier.getName().equals(RANGE_MODIFIER_NAME))
                .forEach(modifier -> player.getAttribute(Attribute.ENTITY_INTERACTION_RANGE).removeModifier(modifier));
    }

    public static boolean hasRangeAttribute(Player player) {
        if (player.getAttribute(Attribute.ENTITY_INTERACTION_RANGE) == null) return false;

        double baseValue = player.getAttribute(Attribute.ENTITY_INTERACTION_RANGE).getBaseValue();
        double currentValue = player.getAttribute(Attribute.ENTITY_INTERACTION_RANGE).getValue();
        double expectedValue = baseValue + BONUS_VALUE;

        return Math.abs(currentValue - expectedValue) < 0.01;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        int phase = MayorManager.getInstance().phaseMayor;

        if (phase == 2) {
            City playerCity = CityManager.getPlayerCity(player.getUniqueId());
            if (playerCity == null) return;

            if (!PerkManager.hasPerk(playerCity.getMayor(), 4)) return;

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
