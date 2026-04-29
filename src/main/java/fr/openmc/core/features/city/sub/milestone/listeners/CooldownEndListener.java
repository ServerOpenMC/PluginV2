package fr.openmc.core.features.city.sub.milestone.listeners;

import fr.openmc.api.cooldown.CooldownEndEvent;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.sub.mayor.managers.MayorManager;
import fr.openmc.core.features.city.sub.milestone.CityLevels;
import fr.openmc.core.features.city.sub.milestone.events.CityUpgradeEvent;
import fr.openmc.core.features.city.sub.milestone.rewards.FeaturesRewards;
import fr.openmc.core.features.city.sub.statistics.CityStatisticsManager;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.Objects;

public class CooldownEndListener implements Listener {
    @EventHandler
    public void onUpgradeEnd(CooldownEndEvent event) {
        String group = event.getGroup();

        if (!Objects.equals(group, "city:upgrade-level")) return;

        City city = CityManager.getCity(event.getCooldownUUID());

        if (city == null) return;

        Bukkit.getScheduler().runTask(OMCPlugin.getInstance(), () ->
                Bukkit.getPluginManager().callEvent(new CityUpgradeEvent(city))
        );

        int oldLevel = city.getLevel();
        boolean hadMayorBefore = FeaturesRewards.hasUnlockFeature(city, FeaturesRewards.Feature.MAYOR);

        if (oldLevel + 1 > CityLevels.values().length) return;

        city.setLevel(oldLevel + 1);
	    
	    MessagesManager.broadcastMessage(Component.text("La ville §d" + city.getName() + " §fest passée au §3Niveau " + city.getLevel() + " §f! Un maximum de GG !"), Prefix.CITY, MessageType.INFO);

        CityStatisticsManager.removeStats(city.getUniqueId());

        boolean hasMayorNow = FeaturesRewards.hasUnlockFeature(city, FeaturesRewards.Feature.MAYOR);

        if (!hadMayorBefore && hasMayorNow) {
            if (MayorManager.phaseMayor == 1) {
                MayorManager.initCityPhase1(city, null);
            } else if (MayorManager.phaseMayor == 2) {
                MayorManager.initCityPhase2(city);
            }
        }
    }
}
