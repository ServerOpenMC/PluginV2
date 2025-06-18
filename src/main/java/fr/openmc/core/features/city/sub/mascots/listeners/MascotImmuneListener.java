package fr.openmc.core.features.city.sub.mascots.listeners;

import fr.openmc.api.cooldown.CooldownEndEvent;
import fr.openmc.api.cooldown.CooldownStartEvent;
import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.sub.mascots.MascotsManager;
import fr.openmc.core.features.city.sub.mascots.models.Mascot;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class MascotImmuneListener implements Listener {

    @EventHandler
    void onStartMascotImmune(CooldownStartEvent event) {
        if (!event.getGroup().equals("city:immunity")) return;

        City cityImmune = CityManager.getCity(event.getUUID());

        if (cityImmune == null) return;

        Mascot mascot = cityImmune.getMascot();
        LivingEntity entityMascot = (LivingEntity) mascot.getEntity();

        entityMascot.setGlowing(true);
        mascot.setImmunity(true);
    }

    @EventHandler
    void onEndMascotImmune(CooldownEndEvent event) {
        if (!event.getGroup().equals("city:immunity")) return;

        City cityImmune = CityManager.getCity(event.getUUID());

        if (cityImmune == null) return;

        Mascot mascot = cityImmune.getMascot();
        LivingEntity entityMascot = (LivingEntity) mascot.getEntity();

        entityMascot.setGlowing(false);

        mascot.setImmunity(false);
        mascot.setAlive(true);

        entityMascot.customName(Component.text(MascotsManager.PLACEHOLDER_MASCOT_NAME.formatted(
                cityImmune.getName(),
                entityMascot.getHealth(),
                entityMascot.getMaxHealth()
        )));
    }
}
