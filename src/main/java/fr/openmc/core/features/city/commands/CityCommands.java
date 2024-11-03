package fr.openmc.core.features.city.commands;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector2;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.flags.*;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.managers.storage.StorageException;
import com.sk89q.worldguard.protection.regions.ProtectedPolygonalRegion;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.CityMessages;
import fr.openmc.core.features.city.CityUtils;
import fr.openmc.core.features.utils.economy.EconomyManager;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import org.bukkit.*;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.*;
import revxrsal.commands.bukkit.annotation.CommandPermission;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

@Command({"ville", "city"})
public class CityCommands {
    HashMap<Player, Player> invitations = new HashMap<>(); // Invité, Inviteur

    private Location[] getCorners(Player player) {
        World world = player.getWorld();
        Location location = player.getLocation();
        int chunkRadius = 2;
        int chunkX = location.getChunk().getX();
        int chunkZ = location.getChunk().getZ();

        Location minLocation = new Location(world, (chunkX-chunkRadius) * 16, location.getY(), (chunkZ-chunkRadius) * 16);
        Location maxLocation = new Location(world, (chunkX+chunkRadius+1) * 16 - 1, location.getY(), (chunkZ+chunkRadius + 1) * 16 - 1);

        return new Location[]{minLocation, maxLocation};
    }

    private int calculatePrice(int chunkCount) {
        return 5000 + ((chunkCount-25) * 1000);
    }

    private boolean isInvalidName(String name) {
        if (name.length() > 24) {
            return true;
        }

        if (!name.matches("[a-zA-Z0-9]+")) {
            return true;
        }

        return false;
    }

    @Subcommand("accept")
    @CommandPermission("omc.commands.city.accept")
    @Description("Accepter une invitation")
    void accept(Player player) {
        if (!invitations.containsKey(player)) {
            MessagesManager.sendMessageType(player, "Tu n'as aucune invitations en attente", Prefix.CITY, MessageType.ERROR, false);
            return;
        }
        Player inviter = invitations.get(player);
        String newCity = CityManager.getPlayerCity(inviter.getUniqueId());
        CityManager.playerJoinCity(player.getUniqueId(), newCity);

        invitations.remove(player);

        MessagesManager.sendMessageType(inviter, "Tu as rejoins "+ CityManager.getCityName(newCity), Prefix.CITY, MessageType.SUCCESS, false);
        if (inviter.isOnline()) {
            MessagesManager.sendMessageType(inviter, player.getName()+" a accepté ton invitation!", Prefix.CITY, MessageType.SUCCESS, true);
        }
    }

    @Subcommand("rename")
    @CommandPermission("omc.commands.city.rename")
    @Description("Renommer une ville")
    void rename(Player player, @Named("nouveau nom") String name) {
        String playerCity = CityManager.getPlayerCity(player.getUniqueId());

        if (playerCity == null) {
            MessagesManager.sendMessageType(player, "Tu n'habite dans aucune ville", Prefix.CITY, MessageType.ERROR, false);
        }

        if (!CityManager.getOwnerUUID(playerCity).equals(player.getUniqueId())) {
            MessagesManager.sendMessageType(player, "Tu n'es pas le maire de la ville", Prefix.CITY, MessageType.ERROR, false);
            return;
        }

        if (isInvalidName(name)) {
            MessagesManager.sendMessageType(player, "Le nom de ville est invalide, il doit alphanumerique et 24 charactères max", Prefix.CITY, MessageType.ERROR, false);
            return;
        }

        CityManager.renameCity(playerCity, name);
        MessagesManager.sendMessageType(player, "La ville a été renommée", Prefix.CITY, MessageType.SUCCESS, false);
    }

    @Subcommand("transfer")
    @CommandPermission("omc.commands.city.transfer")
    @Description("Transferer la propriété de votre ville")
    @AutoComplete("@city_members")
    void transfer(Player sender, @Named("maire") OfflinePlayer player) {
        String playerCity = CityManager.getPlayerCity(sender.getUniqueId());
        if (playerCity == null) {
            MessagesManager.sendMessageType(sender, "Tu n'habite aucune ville", Prefix.CITY, MessageType.ERROR, false);
            return;
        }

        if (CityManager.getOwnerUUID(playerCity) != sender.getUniqueId()) {
            MessagesManager.sendMessageType(sender, "Tu n'est pas maire de la ville", Prefix.CITY, MessageType.ERROR, false);
            return;
        }

        if (!CityManager.getMembers(playerCity).contains(sender.getUniqueId())) {
            MessagesManager.sendMessageType(sender, "Ce joueur n'habite pas dans votre ville", Prefix.CITY, MessageType.ERROR, false);
            return;
        }

        CityManager.changeOwner(player.getUniqueId(), playerCity);
        MessagesManager.sendMessageType(sender, "Le maire est devenu "+player.getName(), Prefix.CITY, MessageType.SUCCESS, false);

        if (player.isOnline()) {
            MessagesManager.sendMessageType((Player) player, "Vous êtes devenu le maire de la ville", Prefix.CITY, MessageType.INFO, true);
        }
    }

    @Subcommand("deny")
    @CommandPermission("omc.commands.city.deny")
    @Description("Refuser une invitation")
    void deny(Player player) {
        if (!invitations.containsKey(player)) {
            MessagesManager.sendMessageType(player, "Tu n'as aucune invitations en attente", Prefix.CITY, MessageType.ERROR, false);
            return;
        }
        Player inviter = invitations.get(player);
        invitations.remove(player);

        if (inviter.isOnline()) {
            MessagesManager.sendMessageType(inviter, player.getName()+" a refusé ton invitation", Prefix.CITY, MessageType.WARNING, true);
        }
    }

    @Subcommand("kick")
    @CommandPermission("omc.commands.city.kick")
    @Description("Exclure un habitant de votre ville")
    @AutoComplete("@city_members")
    void kick(Player sender, @Named("exclu") OfflinePlayer player) {
        String playerCity = CityManager.getPlayerCity(sender.getUniqueId());
        if (playerCity == null) {
            MessagesManager.sendMessageType(sender, "Tu n'habite dans aucune ville", Prefix.CITY, MessageType.ERROR, false);
            return;
        }

        if (sender.getUniqueId().equals(player.getUniqueId())) {
            MessagesManager.sendMessageType(sender, "Tu ne peux pas t'exclure de la ville", Prefix.CITY, MessageType.ERROR, false);
            return;
        }

        UUID owner = CityManager.getOwnerUUID(playerCity);
        if (owner == null) {
            MessagesManager.sendMessageType(sender, "Impossible de l'exclure de la ville", Prefix.CITY, MessageType.ERROR, false);
            return;
        }
        if (!owner.equals(sender.getUniqueId())) {
            MessagesManager.sendMessageType(sender, "Tu n'es pas le maire de la ville", Prefix.CITY, MessageType.ERROR, false);
            return;
        }

        if (CityManager.playerLeaveCity(player.getUniqueId())) {
            MessagesManager.sendMessageType(sender, "Tu as exclu "+player.getName()+" de la ville", Prefix.CITY, MessageType.SUCCESS, false);

            if (player.isOnline()) {
                MessagesManager.sendMessageType((Player) player, "Tu as été exclu de ta ville", Prefix.CITY, MessageType.INFO, true);
            }
        } else {
            MessagesManager.sendMessageType(sender, "Impossible de l'exclure de la ville", Prefix.CITY, MessageType.ERROR, false);
        }
    }

    @Subcommand("leave")
    @CommandPermission("omc.commands.city.leave")
    @Description("Quitter votre ville")
    void leave(Player player) {
        String playerCity = CityManager.getPlayerCity(player.getUniqueId());
        if (playerCity == null) {
            MessagesManager.sendMessageType(player, "Tu n'habite dans aucune ville", Prefix.CITY, MessageType.ERROR, false);
            return;
        }

        UUID owner = CityManager.getOwnerUUID(playerCity);
        if (owner == null) {
            MessagesManager.sendMessageType(player, "Impossible de supprimer la ville, réesayez demain", Prefix.CITY, MessageType.ERROR, false);
            return;
        }

        if (owner.equals(player.getUniqueId())) {
            MessagesManager.sendMessageType(player, "Tu est maire de la ville, transfert ou supprime", Prefix.CITY, MessageType.ERROR, false);
            return;
        }

        if (CityManager.playerLeaveCity(player.getUniqueId())) {
            MessagesManager.sendMessageType(player, "Tu as quitté ta ville", Prefix.CITY, MessageType.SUCCESS, false);
        } else {
            MessagesManager.sendMessageType(player, "Impossible de quitter la ville", Prefix.CITY, MessageType.ERROR, false);
        }
    }

    @Subcommand("invite")
    @CommandPermission("omc.commands.city.invite")
    @Description("Inviter un joueur dans votre ville")
    void add(Player sender, @Named("invité") Player target) {
        String player_city = CityManager.getPlayerCity(sender.getUniqueId());
        if (player_city == null) {
            MessagesManager.sendMessageType(sender, "Tu n'habite aucune ville", Prefix.CITY, MessageType.ERROR, false);
            return;
        }

        if (CityManager.getOwnerUUID(player_city) != sender.getUniqueId()) {
            MessagesManager.sendMessageType(sender, "Tu n'est pas maire de la ville", Prefix.CITY, MessageType.ERROR, false);
            return;
        }
        //TODO: Vérifier qu'il y ai la place

        if (CityManager.getPlayerCity(target.getUniqueId()) != null) {
            MessagesManager.sendMessageType(sender, "Cette personne habite déjà une ville", Prefix.CITY, MessageType.ERROR, false);
            return;
        }

        if (invitations.containsKey(target)) {
            MessagesManager.sendMessageType(sender, "Cette personne as déjà une invitations en attente", Prefix.CITY, MessageType.ERROR, false);
            return;
        }

        invitations.put(target, sender);
        MessagesManager.sendMessageType(sender, "Tu as invité "+target.getName()+" dans ta ville", Prefix.CITY, MessageType.SUCCESS, false);
        MessagesManager.sendMessageType(target, sender.getName()+" t'as invité dans sa ville", Prefix.CITY, MessageType.INFO, false);
    }

    @Subcommand("delete")
    @CommandPermission("omc.commands.city.delete")
    @Description("Supprimer votre ville")
    void delMessage(Player sender) {
        sender.sendMessage("§cÊtes-vous sûr de vouloir supprimer votre ville ?");
        sender.sendMessage("§cCette action est §4§lirréversible");
        sender.sendMessage("§cSi vous êtes sûr faite §n/city delconfirm");
    }

    @Subcommand("delconfirm")
    @CommandPermission("omc.commands.city.delete")
    @Description("Supprimer votre ville")
    void delete(Player sender) {
        String playerCity = CityManager.getPlayerCity(sender.getUniqueId());
        if (playerCity == null) {
            MessagesManager.sendMessageType(sender, "Vous ne faites partie d'aucune ville", Prefix.CITY, MessageType.ERROR, false);
            return;
        }

        if (!CityManager.getOwnerUUID(playerCity).equals(sender.getUniqueId())) {
            MessagesManager.sendMessageType(sender, "Tu n'es pas maire de la ville", Prefix.CITY, MessageType.ERROR, false);
            return;
        }

        CityManager.deleteCity(playerCity);
        MessagesManager.sendMessageType(sender, "Votre ville a été supprimé", Prefix.CITY, MessageType.SUCCESS, false);
    }

    @Subcommand("claim")
    @CommandPermission("omc.commands.city.claim")
    @Description("Claim un chunk pour votre ville")
    void claim(Player sender) {
        String playerCity = CityManager.getPlayerCity(sender.getUniqueId());
        if (playerCity == null) {
            MessagesManager.sendMessageType(sender, "Vous ne faites partie d'aucune ville", Prefix.CITY, MessageType.ERROR, false);
            return;
        }

        if (!Objects.equals(CityManager.getOwnerUUID(playerCity).toString(), sender.getUniqueId().toString())) {
            MessagesManager.sendMessageType(sender, "Vous n'êtes pas maire de la ville", Prefix.CITY, MessageType.ERROR, false);
            return;
        }

        org.bukkit.World bWorld = sender.getWorld();
        if (!bWorld.getName().equals("world")) {
            MessagesManager.sendMessageType(sender, "Vous ne pouvais pas étendre votre ville ici", Prefix.CITY, MessageType.ERROR, false);
            return;
        }

        com.sk89q.worldedit.world.World world = BukkitAdapter.adapt(bWorld);
        RegionManager regionManager = WorldGuard.getInstance().getPlatform().getRegionContainer().get(world);
        ProtectedPolygonalRegion oldRegion = (ProtectedPolygonalRegion) regionManager.getRegion("city_" + playerCity);
        if (oldRegion == null) {
            MessagesManager.sendMessageType(sender, "Impossible de trouver votre ville", Prefix.CITY, MessageType.ERROR, false);
            return;
        }

        Chunk chunk = sender.getLocation().getChunk();
        int chunkX = chunk.getX();
        int chunkZ = chunk.getZ();

        AtomicBoolean keepGoing = new AtomicBoolean(true);
        regionManager.getApplicableRegionsIDs(BlockVector3.at(chunkX * 16, 200, chunkZ * 16)).forEach(region -> {
            if (region.equals("__global__")) return;
            MessagesManager.sendMessageType(sender, "Cette endroit fais déjà partie d'une ville ", Prefix.CITY, MessageType.ERROR, false);
            keepGoing.set(false);
        });
        if (!keepGoing.get()) return;

        int area = (int) Math.ceil(CityUtils.getPolygonalRegionArea(oldRegion)/256);

        if (area >= 50) {
            MessagesManager.sendMessageType(sender, "Votre ville est trop grande", Prefix.CITY, MessageType.ERROR, false);
            return;
        }

        int price = calculatePrice(area);
        if (CityManager.getBalance(playerCity) < price) {
            MessagesManager.sendMessageType(sender, "Votre ville n'a pas assez d'argent ("+price+EconomyManager.getEconomyIcon()+" nécessaire)", Prefix.CITY, MessageType.ERROR, false);
            return;
        }

        CityManager.updateBalance(playerCity, (double) (price*-1));

        BlockVector2[] chunkPoints = new BlockVector2[4];
        chunkPoints[0] = BlockVector2.at(chunkX * 16, chunkZ * 16);
        chunkPoints[1] = BlockVector2.at(chunkX * 16, (chunkZ + 1) * 16 - 1);
        chunkPoints[2] = BlockVector2.at((chunkX + 1) * 16 - 1, (chunkZ + 1) * 16 - 1);
        chunkPoints[3] = BlockVector2.at((chunkX + 1) * 16 - 1, chunkZ * 16);

        List<BlockVector2> existingPoints = new ArrayList<>(oldRegion.getPoints());

        BlockVector2 closestPoint = existingPoints.getFirst();
        int closestPointIndex = 0;
        double minDistance = Double.MAX_VALUE;

        for (int i = 0; i < existingPoints.size(); i++) {
            BlockVector2 point = existingPoints.get(i);
            for (BlockVector2 chunkPoint : chunkPoints) {
                double distance = point.distance(chunkPoint);
                if (distance < minDistance) {
                    minDistance = distance;
                    closestPoint = point;
                    closestPointIndex = i;
                }
            }
        }

        int closestChunkPointIndex = 0;
        minDistance = Double.MAX_VALUE;

        for (int i = 0; i < chunkPoints.length; i++) {
            double distance = closestPoint.distance(chunkPoints[i]);
            if (distance < minDistance) {
                minDistance = distance;
                closestChunkPointIndex = i;
            }
        }

        List<BlockVector2> newPoints = new ArrayList<>();

        for (int i = 0; i <= closestPointIndex; i++) {
            newPoints.add(existingPoints.get(i));
        }

        for (int i = 0; i < chunkPoints.length; i++) {
            int index = (closestChunkPointIndex + i) % chunkPoints.length;
            newPoints.add(chunkPoints[index]);
        }

        for (int i = closestPointIndex + 1; i < existingPoints.size(); i++) {
            newPoints.add(existingPoints.get(i));
        }

        ProtectedPolygonalRegion newRegion = new ProtectedPolygonalRegion(
                oldRegion.getId(),
                newPoints,
                oldRegion.getMinimumPoint().y(),
                oldRegion.getMaximumPoint().y()
        );

        newRegion.setFlags(oldRegion.getFlags());
        newRegion.setOwners(oldRegion.getOwners());
        newRegion.setMembers(oldRegion.getMembers());

        regionManager.removeRegion(oldRegion.getId());
        regionManager.addRegion(newRegion);

        try {
            regionManager.saveChanges();
            MessagesManager.sendMessageType(sender, "Votre ville a été agrandi", Prefix.CITY, MessageType.SUCCESS, false);
        } catch (StorageException e) {
            MessagesManager.sendMessageType(sender, "Impossible d'étendre votre ville", Prefix.CITY, MessageType.ERROR, false);
            e.printStackTrace();
        }
    }

    @Subcommand("money give")
    @CommandPermission("omc.commands.city.give")
    @Description("Transferer de l'argent vers la ville")
    void give(Player player, @Named("montant") @Range(min=1) double amount) {
        String playerCity = CityManager.getPlayerCity(player.getUniqueId());
        if (playerCity == null) {
            MessagesManager.sendMessageType(player, "Vous n'habitez dans aucune ville", Prefix.CITY, MessageType.ERROR, false);
            return;
        }

        if (EconomyManager.getInstance().withdrawBalance(player.getUniqueId(), amount)) {
            CityManager.updateBalance(playerCity, amount);
            MessagesManager.sendMessageType(player, "Vous avez transféré "+amount+EconomyManager.getEconomyIcon()+" à votre ville", Prefix.CITY, MessageType.ERROR, false);
        } else {
            MessagesManager.sendMessageType(player, "Vous n'avez pas accès d'argent", Prefix.CITY, MessageType.ERROR, false);
        }
    }

    @Subcommand("money balance")
    @CommandPermission("omc.commands.city.balance")
    @Description("Afficher l'argent de votre ville")
    void balance(Player player) {
        String playerCity = CityManager.getPlayerCity(player.getUniqueId());
        if (playerCity == null) {
            MessagesManager.sendMessageType(player, "Vous n'habitez dans aucune ville", Prefix.CITY, MessageType.ERROR, false);
            return;
        }

        double balance = CityManager.getBalance(playerCity);
        MessagesManager.sendMessageType(player, "Votre ville possède "+balance+EconomyManager.getEconomyIcon(), Prefix.CITY, MessageType.INFO, false);
    }

    @Subcommand("money take")
    @CommandPermission("omc.commands.city.take")
    @Description("Prendre de l'argent depuis votre ville")
    void take(Player player, @Named("montant") @Range(min=1) double amount) {
        String playerCity = CityManager.getPlayerCity(player.getUniqueId());
        if (playerCity == null) {
            MessagesManager.sendMessageType(player, "Vous n'habitez dans aucune ville", Prefix.CITY, MessageType.ERROR, false);
            return;
        }
        UUID owner = CityManager.getOwnerUUID(playerCity);
        if (owner == null || !owner.equals(player.getUniqueId())) {
            MessagesManager.sendMessageType(player, "Vous n'êtes pas maire de la ville", Prefix.CITY, MessageType.ERROR, false);
            return;
        }

        if (CityManager.getBalance(playerCity) < amount) {
            MessagesManager.sendMessageType(player, "Votre ville n'a pas accès d'argent en banque", Prefix.CITY, MessageType.ERROR, false);
            return;
        }

        CityManager.updateBalance(playerCity, amount*-1);
        EconomyManager.getInstance().addBalance(player.getUniqueId(), amount);
        MessagesManager.sendMessageType(player, amount+EconomyManager.getEconomyIcon()+" ont été transféré à votre compte", Prefix.CITY, MessageType.SUCCESS, false);
    }

    @Subcommand("info")
    @CommandPermission("omc.commands.city.info")
    @Description("Avoir des informations sur votre ville")
    void info(Player player) {
        String city = CityManager.getPlayerCity(player.getUniqueId());

        if (city == null) {
            MessagesManager.sendMessageType(player, "Tu n'habite aucune ville", Prefix.CITY, MessageType.ERROR, false);
            return;
        }

        CityMessages.sendInfo(player, city);
    }

    @Subcommand("create")
    @CommandPermission("omc.commands.city.create")
    @Description("Créer une ville")
    @Cooldown(value=60)
    void create(Player player, @Named("nom") String name) throws StorageException {
        World world = player.getWorld();
        Location[] corners = getCorners(player);

        if (CityManager.getPlayerCity(player.getUniqueId()) != null) {
            MessagesManager.sendMessageType(player, "Vous habitez déjà une ville", Prefix.CITY, MessageType.ERROR, false);
            return;
        }

        if (isInvalidName(name)) {
            MessagesManager.sendMessageType(player, "Le nom de ville est invalide, il doit alphanumerique et 24 charactères max", Prefix.CITY, MessageType.ERROR, false);
            return;
        }

        if (CityUtils.doesRegionOverlap(world, corners[0], corners[1])) {
            MessagesManager.sendMessageType(player, "Impossible de créer une ville ici", Prefix.CITY, MessageType.ERROR, false);
            return;
        }

        String regionUUID = UUID.randomUUID().toString().substring(0, 8);

        RegionManager regionManager = WorldGuard.getInstance().getPlatform().getRegionContainer().get(BukkitAdapter.adapt(world));
        assert regionManager != null;

        BlockVector2 corner1 = BlockVector2.at(corners[0].x(), corners[0].z());
        BlockVector2 corner2 = BlockVector2.at(corners[1].x(), corners[1].z());
        BlockVector2 corner3 = BlockVector2.at(corner1.x(), corner2.z());
        BlockVector2 corner4 = BlockVector2.at(corner2.x(), corner1.z());
        ProtectedPolygonalRegion region = new ProtectedPolygonalRegion("city_"+regionUUID, Arrays.asList(corner1, corner3, corner2, corner4), 400, -100);

        Flag<?>[] flagsToProtect = {
                Flags.USE,
                Flags.RIDE,
                Flags.BUILD,
                Flags.USE_ANVIL,
                Flags.CHEST_ACCESS,
                Flags.PLACE_VEHICLE,
                Flags.DAMAGE_ANIMALS,
                Flags.DESTROY_VEHICLE,
                Flags.ENTITY_PAINTING_DESTROY,
                Flags.ENTITY_ITEM_FRAME_DESTROY
        };

        for (Flag<?> flag : flagsToProtect) {
            if (flag != null) {
                region.setFlag((StateFlag) flag, StateFlag.State.DENY);
                region.setFlag(flag.getRegionGroupFlag(), RegionGroup.NON_MEMBERS);
            }
        }

        regionManager.addRegion(region);
        regionManager.saveChanges();

        CityManager.createCity(player.getUniqueId(), regionUUID, name);
        CityManager.playerJoinCity(player.getUniqueId(), regionUUID);

        MessagesManager.sendMessageType(player, "Votre ville a été créer", Prefix.CITY, MessageType.SUCCESS, false);
    }
}