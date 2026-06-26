package fr.openmc.core.features.events.contents.dailyevents.contents.miraculousfishing;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.events.contents.dailyevents.contents.miraculousfishing.registry.SeaCreatureLoot;
import fr.openmc.core.registry.loottable.loots.CustomLoot;
import fr.openmc.core.registry.loottable.loots.MoneyLoot;
import fr.openmc.core.registry.loottable.loots.RepresentedItem;
import fr.openmc.core.registry.loottable.loots.TableLoot;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Vector;

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

    public static void sendLootMessage(Player player, CustomLoot loot, int amount) {
        Component base = Component.text(" - ", NamedTextColor.GRAY);

        if (amount != -1)
            base = base.append(Component.text(amount + "x "));

        if (loot.getDisplayText() != null &&
                !(loot instanceof TableLoot) &&
                !(loot instanceof SeaCreatureLoot)) {
            base = base.append(loot.getDisplayText())
                    .append(Component.text(" ("+ Math.round(loot.getChance() * 100.0) +"% ★)", NamedTextColor.AQUA));

            player.sendMessage(base);
        }
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
            sendLootMessage(player, loot, -1);

            Entity entity = seaCreatureLoot.getSeaCreatureMob().spawn(hookLocation);

            entity.setInvulnerable(true);
            applyVelocity(hookLocation, player.getEyeLocation(), entity);
            entity.setInvulnerable(false);

            if (entity instanceof Mob mob)
                mob.setTarget(player);

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
        sendLootMessage(player, loot, itemEntity.getItemStack().getAmount());

        applyVelocity(hookLocation, player.getEyeLocation(), itemEntity);
    }

    /**
     * L'item décidé en fonction du CustomLoot
     * @param loot le custom loot
     * @return un item stack en fonction du loot
     */
    private static ItemStack getLaunchedItem(CustomLoot loot) {
        // * Si c'est une loot qui peut être représenter par un item
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
    private static void applyVelocity(Location origin, Location destination, Entity entity) {
        // * Revient à faire le vecteur vitesse entre 2 vecteur (xp - xh, yp - yh, zp - zh)
        Vector velocity = destination.toVector().subtract(origin.toVector());
        velocity.multiply(0.1);
        velocity.setY(velocity.getY() + Math.sqrt(Math.sqrt(
                velocity.getX()*2 + velocity.getY()*2 + velocity.getZ()*2)) * 0.08);
        entity.setVelocity(velocity);
    }
}
