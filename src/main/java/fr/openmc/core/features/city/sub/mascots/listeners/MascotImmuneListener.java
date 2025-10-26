package fr.openmc.core.features.city.sub.mascots.listeners;

import fr.openmc.api.cooldown.CooldownEndEvent;
import fr.openmc.api.cooldown.CooldownStartEvent;
import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.sub.mascots.MascotsManager;
import fr.openmc.core.features.city.sub.mascots.models.Mascot;
import net.kyori.adventure.text.Component;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class MascotImmuneListener implements Listener {

    @EventHandler
    void onStartMascotImmune(CooldownStartEvent event) {
        if (!event.getGroup().equals("city:immunity")) return;

        City cityImmune = CityManager.getCity(event.getCooldownUUID());

        if (cityImmune == null) return;

        Mascot mascot = cityImmune.getMascot();
        Entity entityMascot = mascot.getEntity();

        if (entityMascot == null) return;

        entityMascot.setGlowing(true);
        mascot.setImmunity(true);
    }

    @EventHandler
    void onEndMascotImmune(CooldownEndEvent event) {
        if (!event.getGroup().equals("city:immunity")) return;

        City cityImmune = CityManager.getCity(event.getCooldownUUID());

        if (cityImmune == null) return;

        Mascot mascot = cityImmune.getMascot();
        Entity entityMascot = mascot.getEntity();

        if (entityMascot == null) return;
        if (!(entityMascot instanceof LivingEntity mascotMob)) return;

        entityMascot.setGlowing(false);

        mascot.setImmunity(false);
        mascot.setAlive(true);

        AttributeInstance maxHealthInst = mascotMob.getAttribute(Attribute.MAX_HEALTH);
        if (maxHealthInst == null) return;

        entityMascot.customName(Component.text(MascotsManager.PLACEHOLDER_MASCOT_NAME.formatted(
                cityImmune.getName(),
                mascotMob.getHealth(),
                maxHealthInst.getValue()
        )));
    }
}
