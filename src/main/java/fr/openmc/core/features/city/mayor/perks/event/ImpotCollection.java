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
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.*;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.UUID;


public class ImpotCollection {

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
            zombie.customName(Component.text("Serviteur de " + city.getMayor()));
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

            zombie.setMetadata("mayor:zombie", new FixedMetadataValue(OMCPlugin.getInstance(), player.getUniqueId().toString()));
        }
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Zombie)) return;
        if (!(event.getEntity() instanceof Player)) return;

        Zombie zombie = (Zombie) event.getDamager();
        Player victim = (Player) event.getEntity();

        if (!zombie.hasMetadata("mayor:zombie")) return;

        String ownerUuid = zombie.getMetadata("mayor:zombie").get(0).asString();
        OfflinePlayer owner = Bukkit.getOfflinePlayer(UUID.fromString(ownerUuid));
        if (!(owner instanceof Player) || !((Player) owner).isOnline()) return;

        Player mayorPlayer = (Player) owner;

        double amount = 1000;
        // si il a pas assez d'argent dans son compte courant
        if (EconomyManager.getInstance().getBalance(victim.getUniqueId()) < amount) {
            // si il a pas assez d'argent dans sa banque
            if (BankManager.getInstance().getBankBalance(victim.getUniqueId()) < amount) {
                MessagesManager.sendMessage(victim, Component.text("§8§o*grr vous avez de la chance !*"), Prefix.MAYOR, MessageType.INFO, false);
                return;
            }

            // si il a assez d'argent dans sa banque
            BankManager.getInstance().withdrawBankBalance(victim.getUniqueId(), amount);
            EconomyManager.getInstance().withdrawBalance(owner.getUniqueId(), amount);

            MessagesManager.sendMessage(victim, Component.text("Tu as perdu §6" + amount + EconomyManager.getEconomyIcon() + "§f à cause du Maire " + mayorPlayer.getName()), Prefix.MAYOR, MessageType.WARNING, false);
            MessagesManager.sendMessage(mayorPlayer, Component.text("Vous venez de prélévez §6" + amount + EconomyManager.getEconomyIcon() + "§f à " + victim.getName()), Prefix.MAYOR, MessageType.INFO, false);

        } else {
            // si il a assez d'argent dans son compte courant
            EconomyManager.getInstance().withdrawBalance(victim.getUniqueId(), amount);
            EconomyManager.getInstance().withdrawBalance(owner.getUniqueId(), amount);

            MessagesManager.sendMessage(victim, Component.text("Tu as perdu §6" + amount + EconomyManager.getEconomyIcon() + "§f à cause du Maire " + mayorPlayer.getName()), Prefix.MAYOR, MessageType.WARNING, false);
            MessagesManager.sendMessage(mayorPlayer, Component.text("Vous venez de prélévez §6" + amount + EconomyManager.getEconomyIcon() + "§f à " + victim.getName()), Prefix.MAYOR, MessageType.INFO, false);

        }
    }
}
