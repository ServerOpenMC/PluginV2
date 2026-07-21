package fr.openmc.core.features.events.contents.dailyevents.contents.miraculousfishing.listeners;

import fr.openmc.core.OMCRegistry;
import fr.openmc.core.registry.items.CustomItem;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.entity.Boat;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import javax.annotation.Nullable;
import java.util.Optional;

/**
 * Classe implémentant le /prout initial, un peu modifié en gardant le même principe
 */
public class EatKebabFermentedListener implements Listener {

    @EventHandler
    public void onFoodEated(PlayerItemConsumeEvent event) {
        Optional<CustomItem> customItem = OMCRegistry.CUSTOM_ITEMS.get(event.getItem());

        if (customItem.isEmpty() || !customItem.get().getId().equals(OMCRegistry.CUSTOM_ITEMS.KEBAB_FERMENTED.getId())) return;

        proutAction(event.getPlayer());
    }

    /**
     * l'original, l'unique, le vrai
     */
    private void proutAction(Player player) {
        player.sendMessage(TranslationManager.translation("feature.dailyevents.miraculousfishing.eat_kebab_fermented.smelt"));

        if(player.isInsideVehicle()){
            if(player.getVehicle() instanceof Boat boat){
                ItemStack itemBoat = ItemStack.of(boat.getBoatMaterial());
                player.getVehicle().remove();
                player.getWorld().dropItemNaturally(boat.getLocation(), itemBoat);

                player.sendMessage(TranslationManager.translation("feature.dailyevents.miraculousfishing.eat_kebab_fermented.boat"));
            }
        }

        // Make the player jump
        final Vector currentVelocity = player.getVelocity();
        currentVelocity.setY(0.55d);

        player.setVelocity(currentVelocity);

        // Spawn some cloud particles
        final Location location = player.getLocation();
        final @Nullable World world = location.getWorld();

        if (world != null) {
            world.spawnParticle(Particle.CLOUD, location, 3, 0.02d, -0.04d, 0.02d, 0.09d);

            // Funny sound!
            world.playSound(location, Sound.ENTITY_VILLAGER_NO,  SoundCategory.PLAYERS, 0.8f, 2.3f);
            world.playSound(location, Sound.ENTITY_GOAT_EAT,  SoundCategory.PLAYERS,0.7f, 0.2f);
        }

        // Broadcast the message
        Bukkit.broadcast(TranslationManager.translation("feature.dailyevents.miraculousfishing.eat_kebab_fermented.broadcast",
                Component.text(player.getName())));
    }
}
