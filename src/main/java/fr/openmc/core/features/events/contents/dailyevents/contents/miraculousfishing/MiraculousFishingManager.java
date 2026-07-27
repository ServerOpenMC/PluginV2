package fr.openmc.core.features.events.contents.dailyevents.contents.miraculousfishing;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.bootstrap.features.Feature;
import fr.openmc.core.bootstrap.features.types.HasListeners;
import fr.openmc.core.features.events.contents.dailyevents.contents.miraculousfishing.listeners.EatKebabFermentedListener;
import fr.openmc.core.features.events.contents.dailyevents.contents.miraculousfishing.listeners.PlayerFishListener;
import fr.openmc.core.features.events.contents.dailyevents.contents.miraculousfishing.listeners.PlayerNotPickUpListener;
import fr.openmc.core.features.events.contents.dailyevents.contents.miraculousfishing.registry.SeaCreatureLoot;
import fr.openmc.core.registry.loottable.loots.CustomLoot;
import fr.openmc.core.registry.loottable.loots.MoneyLoot;
import fr.openmc.core.registry.loottable.loots.RepresentedItem;
import fr.openmc.core.registry.loottable.loots.TableLoot;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Vector;

import java.util.Set;

public class MiraculousFishingManager extends Feature implements HasListeners {

    public static final NamespacedKey NOT_PICKUP_KEY = new NamespacedKey(OMCPlugin.getInstance(), "not_pickup");

    @Override
    public Set<Listener> getListeners() {
        return Set.of(
                new PlayerFishListener(),
                new PlayerNotPickUpListener(),
                new EatKebabFermentedListener()
        );
    }

    /**
     * Simule un item qui est lancé du bouchon de pêche jusqu'au joueur, via un CustomLoot.
     * @param player le joueur visé
     * @param hookLocation la position du bouchon, position de spawn de l'item
     * @param loot le CustomLoot qui sera utilisé pour déterminer l'item à lancer
     */
    public static void simulateLaunchLoot(Player player, Location hookLocation, CustomLoot loot) {
        // * Gestion spécial pour les Sea Creature
        if (loot instanceof SeaCreatureLoot seaCreatureLoot) {
            // * On envoie le message de loot
            loot.sendLootMessage(player, -1);

            Entity entity = seaCreatureLoot.getSeaCreatureMob().spawn(hookLocation);

            if (seaCreatureLoot.isThrowCreature()) {
                entity.setInvulnerable(true);
                applyVelocity(hookLocation, player.getEyeLocation(), entity, 0.2);
                entity.setInvulnerable(false);

                if (entity instanceof Mob mob) {
                    mob.setAggressive(true);
                    mob.setTarget(player);
                }
            }

            return;
        }

        ItemStack displayItem = getLaunchedItem(loot);
        if (displayItem == null) return;

        // * Spawn de l'entité Item
        Item itemEntity = hookLocation.getWorld().dropItem(hookLocation, displayItem);
        itemEntity.setCanPlayerPickup(true);
        itemEntity.setCanMobPickup(true);
        itemEntity.setGlowing(true);

        // * On envoie le message de loot
        loot.sendLootMessage(player, itemEntity.getItemStack().getAmount());

        applyVelocity(hookLocation, player.getEyeLocation(), itemEntity, 0.1);
    }

    /**
     * L'item décidé en fonction du CustomLoot
     * @param loot le custom loot
     * @return un item stack en fonction du loot
     */
    private static ItemStack getLaunchedItem(CustomLoot loot) {
        // * Si c'est un loot qui ne doit pas etre lancé
        if (loot instanceof TableLoot || loot instanceof SeaCreatureLoot) return null;

        // * Si c'est un loot qui peut être représenter par un item
        if (loot instanceof RepresentedItem itemDisplayed) {
            ItemStack item = itemDisplayed.getRepresentativeItem();
            // * et que si c'est une item, qui ne doit pas etre donné (ex Money)
            if (loot instanceof MoneyLoot)
                item.editPersistentDataContainer(c ->
                        c.set(NOT_PICKUP_KEY, PersistentDataType.BOOLEAN, true));
            return item;
        }

        return null;
    }

    /**
     * Applique la vélocité habituel de minecraft lorsqu'une entité est péché
     * @param origin La position d'origine
     * @param destination la position de destination
     * @param entity l'entité à qui appliquer la vélocité
     */
    private static void applyVelocity(Location origin, Location destination, Entity entity, double force) {
        // * Revient à faire le vecteur vitesse entre 2 vecteur (xp - xh, yp - yh, zp - zh)
        Vector velocity = destination.toVector().subtract(origin.toVector());
        velocity.multiply(force);

        double m = Math.sqrt(
                velocity.getX() * velocity.getX()
                        + velocity.getY() * velocity.getY()
                        + velocity.getZ() * velocity.getZ()
        );

        velocity.setY(velocity.getY() + m * 0.08);
        entity.setVelocity(velocity);
    }
}
