package fr.openmc.core.features.city.mayor.perks.event;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.city.City;
import fr.openmc.core.features.economy.BankManager;
import fr.openmc.core.features.economy.EconomyManager;
import fr.openmc.core.utils.customitems.CustomItemRegistry;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import net.kyori.adventure.text.Component;
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
            zombie.customName(Component.text("Serviteur de " + city.getMayor().getName()));
            zombie.setCustomNameVisible(true);
            zombie.setTarget(player);

            EntityEquipment equipment = zombie.getEquipment();
            if (equipment != null) {
                equipment.setHelmet(CustomItemRegistry.getByName("omc_items:suit_helmet").getBest());
                equipment.setChestplate(CustomItemRegistry.getByName("omc_items:suit_chestplate").getBest());
                equipment.setLeggings(CustomItemRegistry.getByName("omc_items:suit_leggings").getBest());
                equipment.setBoots(CustomItemRegistry.getByName("omc_items:suit_boots").getBest());
            }

            zombie.setShouldBurnInDay(false);

            zombie.setMetadata("mayor:zombie", new FixedMetadataValue(OMCPlugin.getInstance(), city.getMayor().getUUID()));
        }
    }

    private final HashMap<UUID, Double> playerWithdrawnAmount = new HashMap<>();

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Zombie)) return;
        if (!(event.getEntity() instanceof Player)) return;

        Zombie zombie = (Zombie) event.getDamager();
        Player victim = (Player) event.getEntity();

        if (!zombie.hasMetadata("mayor:zombie")) return;

        String ownerUuid = zombie.getMetadata("mayor:zombie").get(0).asString();
        UUID uuid = UUID.fromString(ownerUuid);
        Player mayorPlayer = Bukkit.getPlayer(uuid);
        if (mayorPlayer == null) return;

        double amount = 1000;

        if (EconomyManager.getInstance().getBalance(victim.getUniqueId()) < amount) {
            if (BankManager.getInstance().getBankBalance(victim.getUniqueId()) < amount) {
                MessagesManager.sendMessage(victim, Component.text("§8§o*grr vous avez de la chance !*"), Prefix.MAYOR, MessageType.INFO, false);
                return;
            }

            BankManager.getInstance().withdrawBankBalance(victim.getUniqueId(), amount);
        } else {
            EconomyManager.getInstance().withdrawBalance(victim.getUniqueId(), amount);
        }
        EconomyManager.getInstance().addBalance(mayorPlayer.getUniqueId(), amount);

        double newTotal = playerWithdrawnAmount.getOrDefault(victim.getUniqueId(), 0.0) + amount;
        playerWithdrawnAmount.put(victim.getUniqueId(), newTotal);

        MessagesManager.sendMessage(victim, Component.text("Tu as perdu §6" + amount + EconomyManager.getEconomyIcon() + "§f à cause du Maire " + mayorPlayer.getName()), Prefix.MAYOR, MessageType.WARNING, false);
        MessagesManager.sendMessage(mayorPlayer, Component.text("Vous venez de prélever §6" + amount + EconomyManager.getEconomyIcon() + "§f à " + victim.getName()), Prefix.MAYOR, MessageType.INFO, false);

        if (newTotal >= 5000) {
            for (Entity entity : victim.getWorld().getEntities()) {
                if (entity instanceof Zombie) {
                    Zombie z = (Zombie) entity;

                    if (!z.hasMetadata("mayor:zombie")) continue;
                    String zOwnerUuid = z.getMetadata("mayor:zombie").get(0).asString();
                    if (!zOwnerUuid.equals(ownerUuid)) continue;
                    if (z.getTarget() != null && z.getTarget().getUniqueId().equals(victim.getUniqueId())) {
                        z.remove();
                    }
                }
            }

            MessagesManager.sendMessage(victim, Component.text("§8§o*les zombies ont eu tout ce qu'ils voulaient*"), Prefix.MAYOR, MessageType.INFO, false);
        }
    }
}
