package fr.openmc.core.features.city.sub.mayor.perks.event;

import fr.openmc.api.omcplayer.OMCPlayer;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.OMCRegistry;
import fr.openmc.core.features.city.models.city.City;
import fr.openmc.core.features.economy.BankManager;
import fr.openmc.core.features.economy.EconomyManager;
import fr.openmc.core.utils.text.messages.MessageType;
import fr.openmc.core.utils.text.messages.MessagesManager;
import fr.openmc.core.utils.text.messages.Prefix;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.HashMap;
import java.util.UUID;


public class ImpotCollection implements Listener {
    /**
     * Spawns zombies around the player in the specified city.
     *
     * @param player The player around whom the zombies will be spawned.
     * @param city   The city where the zombies will be spawned.
     */
    public static void spawnZombies(Player player, City city) {
        World world = player.getWorld();
        Location center = player.getLocation();

        for (int i = 0; i < 5; i++) {
            Location spawnLoc = center.clone().add(
                    (Math.random() - 0.5) * 6,
                    0,
                    (Math.random() - 0.5) * 6
            );
            spawnLoc.setY(world.getHighestBlockYAt(spawnLoc));

            Zombie zombie = (Zombie) world.spawnEntity(spawnLoc, EntityType.ZOMBIE);
            zombie.customName(TranslationManager.translation(
                    "feature.city.mayor.perk.event.impot.zombie.name",
                    city.getMayor().getName()
            ).color(NamedTextColor.GRAY));
            zombie.setCustomNameVisible(true);
            zombie.setTarget(player);

            EntityEquipment equipment = zombie.getEquipment();
            if (equipment != null) {
                equipment.setHelmet(OMCRegistry.CUSTOM_ITEMS.SUIT_HELMET.getBest());
                equipment.setChestplate(OMCRegistry.CUSTOM_ITEMS.SUIT_CHESTPLATE.getBest());
                equipment.setLeggings(OMCRegistry.CUSTOM_ITEMS.SUIT_LEGGINGS.getBest());
                equipment.setBoots(OMCRegistry.CUSTOM_ITEMS.SUIT_BOOTS.getBest());
            }

            zombie.setShouldBurnInDay(false);

            zombie.setMetadata("mayor:zombie", new FixedMetadataValue(OMCPlugin.getInstance(), city.getMayor().getMayorUUID()));
        }
    }

    private final EconomyManager economyManager = OMCRegistry.FEATURES.ECONOMY.get();
    private final BankManager bankManager = OMCRegistry.FEATURES.BANK.get();
    private final HashMap<UUID, Double> playerWithdrawnAmount = new HashMap<>();

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Zombie zombie)) return;
        if (!(event.getEntity() instanceof Player vP)) return;

        if (!zombie.hasMetadata("mayor:zombie")) return;

        String ownerUUID = zombie.getMetadata("mayor:zombie").getFirst().asString();
        OMCPlayer mayorPlayer = OMCPlayer.of(UUID.fromString(ownerUUID));
        OMCPlayer victimPlayer = OMCPlayer.of(vP.getUniqueId());

        if (mayorPlayer == null) return;

        double amount = 1000;

        if (victimPlayer.economy().getBalance() < amount) {
            if (bankManager.getBankBalance(victimPlayer.getUniqueId()) < amount) {
                victimPlayer.message().send(TranslationManager.translation("feature.city.mayor.perk.event.impot.victim.lucky"), Prefix.MAYOR, MessageType.INFO, false);
                return;
            }

            bankManager.withdraw(victimPlayer.getUniqueId(), amount);
        } else {
            victimPlayer.economy().withdrawBalance(amount, "Impôt prélevé par le maire " + mayorPlayer.getName());
        }

        mayorPlayer.economy().addBalance(amount, "Impôt prélevé par le maire " + mayorPlayer.getName());

        double newTotal = playerWithdrawnAmount.getOrDefault(victimPlayer.getUniqueId(), 0.0) + amount;
        playerWithdrawnAmount.put(victimPlayer.getUniqueId(), newTotal);
	    
	    Component amountComponent = Component.text(amount + economyManager.getEconomyIcon()).color(NamedTextColor.GOLD);
        victimPlayer.message().send(TranslationManager.translation(
                "feature.city.mayor.perk.event.impot.victim.lost",
                amountComponent,
                Component.text(mayorPlayer.getName()).color(NamedTextColor.WHITE)
        ), Prefix.MAYOR, MessageType.WARNING, false);
        mayorPlayer.message().send(TranslationManager.translation(
                "feature.city.mayor.perk.event.impot.mayor.collected",
                amountComponent,
                Component.text(victimPlayer.getName()).color(NamedTextColor.WHITE)
        ), Prefix.MAYOR, MessageType.INFO, false);

        if (newTotal >= 5000) {
            for (Entity entity : victimPlayer.getWorld().getEntities()) {
                if (entity instanceof Zombie zombie1) {
                    if (!zombie1.hasMetadata("mayor:zombie")) continue;
                    String zOwnerUuid = zombie1.getMetadata("mayor:zombie").getFirst().asString();
                    if (!zOwnerUuid.equals(ownerUUID)) continue;
                    if (zombie1.getTarget() != null && zombie1.getTarget().getUniqueId().equals(victimPlayer.getUniqueId())) {
                        zombie1.remove();
                    }
                }
            }

            victimPlayer.message().send(TranslationManager.translation("feature.city.mayor.perk.event.impot.zombies.done"), Prefix.MAYOR, MessageType.INFO, false);
        }
    }
}
