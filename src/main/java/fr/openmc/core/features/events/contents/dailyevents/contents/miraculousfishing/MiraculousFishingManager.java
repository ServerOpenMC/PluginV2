package fr.openmc.core.features.events.contents.dailyevents.contents.miraculousfishing;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.registry.loottable.loots.CustomLoot;
import fr.openmc.core.registry.loottable.loots.MoneyLoot;
import fr.openmc.core.registry.loottable.loots.RepresentedItem;
import fr.openmc.core.registry.loottable.loots.TableLoot;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.FishHook;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Vector;

import java.util.List;

public class MiraculousFishingManager {

    public static final NamespacedKey NOT_PICKUP_KEY = new NamespacedKey(OMCPlugin.getInstance(), "not_pickup");
    public static final double FISHING_SPEED_MODIFIER = 0.4;

    /**
     * Applique un modificateur de vitesse de pêche à un FishHook.
     * @param hook le hook qui aura son temps réduit
     */
    public static void applyFishingSpeedModifier(FishHook hook) {
        hook.setWaitTime((int) (hook.getMinWaitTime() * FISHING_SPEED_MODIFIER),
                (int) (hook.getMaxWaitTime() * FISHING_SPEED_MODIFIER));
    }

    /**
     * Simule un item qui est lancé du bouchon de pêche jusqu'au joueur, via un CustomLoot.
     * @param player le joueur visé
     * @param hookLocation la position du bouchon, position de spawn de l'item
     * @param loot le CustomLoot qui sera utilisé pour déterminer l'item à lancer
     */
    public static void simulateLaunchLoot(Player player, Location hookLocation, CustomLoot loot) {
        ItemStack displayItem = getLaunchedItem(loot);

        if (displayItem == null) return;

        // * Spawn de l'entité Item
        Item itemEntity = hookLocation.getWorld().dropItem(hookLocation, displayItem);
        itemEntity.setCanPlayerPickup(true);
        itemEntity.setCanMobPickup(true);
        itemEntity.setGlowing(true);

        // * Revient à faire le vecteur vitesse entre 2 vecteur (xp - xh, yp - yh, zp - zh)
        Vector velocity = player.getEyeLocation().toVector().subtract(hookLocation.toVector());
        velocity.multiply(0.1);
        itemEntity.setVelocity(velocity);
    }

    /**
     * L'item décidé en fonction du CustomLoot
     * @param loot le custom loot
     * @return un item stack en fonction du loot
     */
    private static ItemStack getLaunchedItem(CustomLoot loot) {
        // * SI c'est un loot provenant d'une sous loot table, on roll puis on le renvoie
        if (loot instanceof TableLoot tableLoot) {
            List<CustomLoot> loots = tableLoot.getLootTable().rollLoots(null, false);
            return getLaunchedItem(loots.getFirst());
        // * Si c'est une loot qui peut être représenter par un item
        } else if (loot instanceof RepresentedItem itemDisplayed) {
            ItemStack item = itemDisplayed.getRepresentativeItem();
            // * et que si c'est une item, qui ne doit pas etre donné (ex Money)
            if (loot instanceof MoneyLoot)
                item.editPersistentDataContainer(c ->
                        c.set(NOT_PICKUP_KEY, PersistentDataType.BOOLEAN, true));
            else return item;
        }

        return null;
    }
}
