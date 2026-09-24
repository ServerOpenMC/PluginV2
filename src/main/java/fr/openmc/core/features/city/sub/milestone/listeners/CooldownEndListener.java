package fr.openmc.core.features.city.sub.milestone.listeners;

import fr.openmc.api.cooldown.CooldownEndEvent;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.OMCRegistry;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.models.city.City;
import fr.openmc.core.features.city.sub.mayor.managers.MayorManager;
import fr.openmc.core.features.city.sub.mayor.models.MayorPhase;
import fr.openmc.core.features.city.sub.milestone.CityLevels;
import fr.openmc.core.features.city.sub.milestone.events.CityUpgradeEvent;
import fr.openmc.core.features.city.sub.milestone.rewards.FeaturesRewards;
import fr.openmc.core.features.city.sub.statistics.CityStatisticsManager;
import fr.openmc.core.utils.text.messages.MessageType;
import fr.openmc.core.utils.text.messages.MessagesManager;
import fr.openmc.core.utils.text.messages.Prefix;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.Objects;

public class CooldownEndListener implements Listener {

    public final CityManager cityManager;
    public final CityStatisticsManager cityStatisticsManager;
    public final MayorManager mayorManager;

    public CooldownEndListener(CityManager cityManager) {
        this.cityManager = cityManager;
        this.cityStatisticsManager = OMCRegistry.CITY_FEATURES.STATS;
        this.mayorManager = OMCRegistry.CITY_FEATURES.MAYOR;
    }

    @EventHandler
    public void onUpgradeEnd(CooldownEndEvent event) {
        String group = event.getGroup();

        if (!Objects.equals(group, "city:upgrade-level")) return;

        City city = City.of(event.getCooldownUUID());

        if (city == null) return;

        Bukkit.getScheduler().runTask(OMCPlugin.getInstance(), () ->
                Bukkit.getPluginManager().callEvent(new CityUpgradeEvent(city))
        );

        int oldLevel = city.getLevel();
        boolean hadMayorBefore = FeaturesRewards.hasUnlockFeature(city, FeaturesRewards.Feature.MAYOR);

        if (oldLevel + 1 > CityLevels.values().length) return;

        city.setLevel(oldLevel + 1);
	    
	    MessagesManager.broadcastMessage(
                TranslationManager.translation(
                        "feature.city.levels.upgrade.broadcast",
                        Component.text(city.getName()).color(NamedTextColor.LIGHT_PURPLE),
                        Component.text(city.getLevel()).color(NamedTextColor.DARK_AQUA)
                ),
                Prefix.CITY,
                MessageType.INFO
        );

        city.removeStats();

        boolean hasMayorNow = FeaturesRewards.hasUnlockFeature(city, FeaturesRewards.Feature.MAYOR);

        if (!hadMayorBefore && hasMayorNow) {
            switch (mayorManager.getMayorPhase()) {
                case OPEN_ELECTION -> mayorManager.initCityPhase1(city, null);
                case MAYOR_ELECTED -> mayorManager.initCityPhase2(city);
            }
        }
    }
}
