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
import fr.openmc.core.features.city.*;
import fr.openmc.core.features.city.menu.CityMenu;
import fr.openmc.core.features.economy.EconomyManager;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import org.bukkit.*;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.*;
import revxrsal.commands.bukkit.annotation.CommandPermission;

import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

@Command({"ville", "city"})
public class CityCommands {
    public static HashMap<Player, Player> invitations = new HashMap<>(); // Invité, Inviteur

    private final MessagesManager msgCity  = new MessagesManager(Prefix.CITY);

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

    @DefaultFor("~")
    void main(Player player) {
        CityMenu menu = new CityMenu(player);
        menu.open();
    }

    @Subcommand("accept")
    @CommandPermission("omc.commands.city.accept")
    @Description("Accepter une invitation")
    void accept(Player player) {
        if (!invitations.containsKey(player)) {
            msgCity.error(player, "Tu n'as aucune invitation en attente");
            return;
        }
        Player inviter = invitations.get(player);
        City newCity = CityManager.getPlayerCity(inviter.getUniqueId());

        if (newCity == null) {
            msgCity.success(inviter, "L'invitation a expiré");
            return;
        }

        newCity.addPlayer(player.getUniqueId());

        invitations.remove(player);

        msgCity.success(player, "Tu as rejoint "+ newCity.getName());
        if (inviter.isOnline()) {
            msgCity.success(inviter, player.getName()+" a accepté ton invitation !");
        }
    }

    @Subcommand("rename")
    @CommandPermission("omc.commands.city.rename")
    @Description("Renommer une ville")
    void rename(Player player, @Named("nouveau nom") String name) {
        City playerCity = CityManager.getPlayerCity(player.getUniqueId());

        if (playerCity == null) {
            msgCity.error(player, MessagesManager.Message.PLAYERNOCITY.getMessage());
        }

        if (!(playerCity.hasPermission(player.getUniqueId(), CPermission.RENAME))) {
            msgCity.error(player, "Tu n'es pas le maire de la ville");
            return;
        }

        if (isInvalidName(name)) {
            msgCity.error(player, "Le nom de ville est invalide, il doit seulement comporter des caractères alphanumeriques et maximum 24 caractères.");
            return;
        }

        playerCity.renameCity(name);
        msgCity.success(player, "La ville a été renommée en " + name);
    }

    @Subcommand("transfer")
    @CommandPermission("omc.commands.city.transfer")
    @Description("Transfert la propriété de votre ville")
    @AutoComplete("@city_members")
    void transfer(Player sender, @Named("maire") OfflinePlayer player) {
        City playerCity = CityManager.getPlayerCity(sender.getUniqueId());
        if (playerCity == null) {
            msgCity.error(sender, MessagesManager.Message.PLAYERNOCITY.getMessage());
            return;
        }

        if (!(playerCity.hasPermission(sender.getUniqueId(), CPermission.OWNER))) {
            msgCity.error(sender, "Tu n'es pas le maire de la ville");
            return;
        }

        if (!playerCity.getMembers().contains(sender.getUniqueId())) {
            msgCity.error(sender, "Ce joueur n'habite pas dans votre ville");
            return;
        }

        playerCity.changeOwner(player.getUniqueId());
        msgCity.success(sender, "Le nouveau maire est "+player.getName());

        if (player.isOnline()) {
            msgCity.info((Player) player, "Vous êtes devenu le maire de la ville");
        }
    }

    @Subcommand("deny")
    @CommandPermission("omc.commands.city.deny")
    @Description("Refuser une invitation")
    void deny(Player player) {
        if (!invitations.containsKey(player)) {
            msgCity.error(player, "Tu n'as aucune invitation en attente");
            return;
        }
        Player inviter = invitations.get(player);
        invitations.remove(player);

        if (inviter.isOnline()) {
            msgCity.warning(inviter, player.getName()+" a refusé ton invitation");
        }
    }

    @Subcommand("kick")
    @CommandPermission("omc.commands.city.kick")
    @Description("Exclure un habitant de votre ville")
    @AutoComplete("@city_members")
    void kick(Player sender, @Named("exclu") OfflinePlayer player) {
        City city = CityManager.getPlayerCity(sender.getUniqueId());
        if (city == null) {
            msgCity.error(sender, MessagesManager.Message.PLAYERNOCITY.getMessage());
            return;
        }

        if (sender.getUniqueId().equals(player.getUniqueId())) {
            msgCity.error(sender, "Tu ne peux pas t'auto exclure de la ville");
            return;
        }

        if (!(city.hasPermission(player.getUniqueId(), CPermission.KICK))) {
            msgCity.error(sender, "Tu n'as pas la permission d'exclure " + player.getName());
            return;
        }

        if (city.hasPermission(player.getUniqueId(), CPermission.OWNER)) {
            msgCity.error(sender, "Tu ne peux pas exclure le maire de la ville");
            return;
        }

        if (city.removePlayer(player.getUniqueId())) {
            msgCity.success(sender, "Tu as exclu "+player.getName()+" de la ville "+ city.getCityName());

            if (player.isOnline()) {
                msgCity.info((Player) player, "Tu as été exclu de la ville "+ city.getCityName());
            }
        } else {
            msgCity.error(sender, "Impossible d'exclure "+player.getName()+" de la ville");
        }
    }

    @Subcommand("leave")
    @CommandPermission("omc.commands.city.leave")
    @Description("Quitter votre ville")
    void leave(Player player) {
        City city = CityManager.getPlayerCity(player.getUniqueId());
        if (city == null) {
            msgCity.error(player, MessagesManager.Message.PLAYERNOCITY.getMessage());
            return;
        }

        if (city.hasPermission(player.getUniqueId(), CPermission.OWNER)) {
            msgCity.error(player, "Tu ne peux pas quitter la ville car tu en es le maire, supprime la ou transfère la propriété");
            return;
        }

        if (city.removePlayer(player.getUniqueId())) {
            msgCity.success(player, "Tu as quitté "+ city.getCityName());
        } else {
            msgCity.error(player, "Impossible de quitter la ville");
        }
    }

    @Subcommand("invite")
    @CommandPermission("omc.commands.city.invite")
    @Description("Inviter un joueur dans votre ville")
    void add(Player sender, @Named("invité") Player target) {
        City city = CityManager.getPlayerCity(sender.getUniqueId());
        if (city == null) {
            msgCity.error(sender, MessagesManager.Message.PLAYERNOCITY.getMessage());
            return;
        }

        if (!(city.hasPermission(sender.getUniqueId(), CPermission.INVITE))) {
            msgCity.error(sender, "Tu n'as pas la permission d'inviter des joueurs dans la ville");
            return;
        }

        if (CityManager.getPlayerCity(target.getUniqueId()) != null) {
            msgCity.error(sender, "Cette personne est déjà dans une ville");
            return;
        }

        if (invitations.containsKey(target)) {
            msgCity.error(sender, "Cette personne as déjà une invitation en attente");
            return;
        }

        invitations.put(target, sender);
        msgCity.success(sender, "Tu as invité "+target.getName()+" dans ta ville");
        msgCity.info(target, "Tu as été invité(e) par " + sender.getName() + "dans la ville "+city.getCityName());
    }

    @Subcommand("delete")
    @CommandPermission("omc.commands.city.delete")
    @Description("Supprimer votre ville")
    void delMessage(Player sender) {
        sender.sendMessage("§cEs-tu sûr de vouloir supprimer ta ville ?");
        sender.sendMessage("§cCette action est §4§lIRREVERSIBLE");
        sender.sendMessage("§cSi tu en es sûr fais §n/city delconfirm");
    }

    @Subcommand("delconfirm")
    @CommandPermission("omc.commands.city.delete")
    @Description("Supprimer votre ville")
    void delete(Player sender) {
        City city = CityManager.getPlayerCity(sender.getUniqueId());
        if (city == null) {
            msgCity.error(sender, MessagesManager.Message.PLAYERNOCITY.getMessage());
            return;
        }

        if (!city.getPlayerWith(CPermission.OWNER).equals(sender.getUniqueId())) {
            msgCity.error(sender, "Tu n'es pas le maire de la ville");
            return;
        }

        city.delete();
        msgCity.success(sender, "Votre ville a été supprimée");
    }

    @Subcommand("claim")
    @CommandPermission("omc.commands.city.claim")
    @Description("Claim un chunk pour votre ville")
    void claim(Player sender) {
        City city = CityManager.getPlayerCity(sender.getUniqueId());
        if (city == null) {
            msgCity.error(sender, MessagesManager.Message.PLAYERNOCITY.getMessage());
            return;
        }

        if (!(city.hasPermission(sender.getUniqueId(), CPermission.CLAIM))) {
            msgCity.error(sender, "Tu n'as pas la permission de claim");
            return;
        }

        org.bukkit.World bWorld = sender.getWorld();
        if (!bWorld.getName().equals("world")) {
            msgCity.error(sender, "Tu ne peux pas étendre ta ville ici");
            return;
        }

        com.sk89q.worldedit.world.World world = BukkitAdapter.adapt(bWorld);
        RegionManager regionManager = WorldGuard.getInstance().getPlatform().getRegionContainer().get(world);
        ProtectedPolygonalRegion oldRegion = (ProtectedPolygonalRegion) regionManager.getRegion("city_" + city.getUUID());
        if (oldRegion == null) {
            msgCity.error(sender, "Impossible de trouver la ville "+ city.getCityName());
            return;
        }

        Chunk chunk = sender.getLocation().getChunk();
        int chunkX = chunk.getX();
        int chunkZ = chunk.getZ();

        AtomicBoolean keepGoing = new AtomicBoolean(true);
        regionManager.getApplicableRegionsIDs(BlockVector3.at(chunkX * 16, 200, chunkZ * 16)).forEach(region -> {
            if (region.equals("__global__")) return;
            msgCity.error(sender, "Cet endroit fais déjà partie d'une ville ");
            keepGoing.set(false);
        });
        if (!keepGoing.get()) return;

        int area = (int) Math.ceil(CityUtils.getPolygonalRegionArea(oldRegion)/256);

        if (area >= 50) {
            msgCity.error(sender, "Ta ville est trop grande");
            return;
        }

        int price = calculatePrice(area);
        if (city.getBalance() < price) {
            msgCity.error(sender, "Ta ville n'a pas assez d'argent ("+price+EconomyManager.getEconomyIcon()+" nécessaires)");
            return;
        }

        city.updateBalance((double) (price*-1));

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
            msgCity.success(sender, "Ta ville a été étendue");
        } catch (StorageException e) {
            msgCity.error(sender, "Impossible d'étendre la ville");
            e.printStackTrace();
        }
    }

    @Subcommand("money give")
    @CommandPermission("omc.commands.city.give")
    @Description("Transferer de l'argent vers la ville")
    void give(Player player, @Named("montant") @Range(min=1) double amount) {
        City city = CityManager.getPlayerCity(player.getUniqueId());
        if (city == null) {
            msgCity.error(player, MessagesManager.Message.PLAYERNOCITY.getMessage());
            return;
        }

        if (!(city.hasPermission(player.getUniqueId(), CPermission.MONEY_GIVE))) {
            msgCity.error(player, "Tu n'as pas la permission de donner de l'argent à ta ville");
            return;
        }

        if (EconomyManager.getInstance().withdrawBalance(player.getUniqueId(), amount)) {
            city.updateBalance(amount);
            msgCity.error(player, "Tu as transféré "+amount+EconomyManager.getEconomyIcon()+" à la ville");
        } else {
            msgCity.error(player, "Tu n'as pas assez d'argent");
        }
    }

    @Subcommand("money balance")
    @CommandPermission("omc.commands.city.balance")
    @Description("Afficher l'argent de votre ville")
    void balance(Player player) {
        City city = CityManager.getPlayerCity(player.getUniqueId());
        if (city == null) {
            msgCity.error(player, MessagesManager.Message.PLAYERNOCITY.getMessage());
            return;
        }

        if (!(city.hasPermission(player.getUniqueId(), CPermission.MONEY_BALANCE))) {
            msgCity.error(player, "Tu n'as pas la permission de consulter l'argent de la ville");
            return;
        }

        double balance = city.getBalance();
        msgCity.info(player, city.getCityName()+ " possède "+balance+EconomyManager.getEconomyIcon());
    }

    @Subcommand("money take")
    @CommandPermission("omc.commands.city.take")
    @Description("Prendre de l'argent depuis votre ville")
    void take(Player player, @Named("montant") @Range(min=1) double amount) {
        City city = CityManager.getPlayerCity(player.getUniqueId());
        if (city == null) {
            msgCity.error(player, MessagesManager.Message.PLAYERNOCITY.getMessage());
            return;
        }

        if (!(city.hasPermission(player.getUniqueId(), CPermission.MONEY_TAKE))) {
            msgCity.error(player, "Tu n'as pas la permission de prendre de l'argent de ta ville");
            return;
        }

        if (city.getBalance() < amount) {
            msgCity.error(player, "Ta ville n'a pas assez d'argent en banque");
            return;
        }

        city.updateBalance(amount*-1);
        EconomyManager.getInstance().addBalance(player.getUniqueId(), amount);
        msgCity.success(player, amount+EconomyManager.getEconomyIcon()+" ont été transférés à votre compte");
    }

    @Subcommand("info")
    @CommandPermission("omc.commands.city.info")
    @Description("Avoir des informations sur votre ville")
    void info(Player player) {
        City city = CityManager.getPlayerCity(player.getUniqueId());

        if (city == null) {
            msgCity.error(player, MessagesManager.Message.PLAYERNOCITY.getMessage());
            return;
        }

        CityMessages.sendInfo(player, city);
    }

    @Subcommand("create")
    @CommandPermission("omc.commands.city.create")
    @Description("Créer une ville")
    @Cooldown(value=60)
    void create(Player player, @Named("nom") String name) throws StorageException, SQLException {
        World world = player.getWorld();
        Location[] corners = getCorners(player);

        if (CityManager.getPlayerCity(player.getUniqueId()) != null) {
            msgCity.error(player, MessagesManager.Message.PLAYERINCITY.getMessage());
            return;
        }

        if (isInvalidName(name)) {
            msgCity.error(player, "Le nom de ville est invalide, il doit contenir seulement des caractères alphanumerique et doit faire moins de 24 charactères");
            return;
        }

        if (CityUtils.doesRegionOverlap(world, corners[0], corners[1])) {
            msgCity.error(player, "Impossible de créer une ville ici");
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

        City city = CityManager.createCity(player.getUniqueId(), regionUUID, name);
        city.addPlayer(player.getUniqueId());
        city.addPermission(player.getUniqueId(), CPermission.OWNER);

        msgCity.error(player, "Votre ville a été créée");
    }
}
