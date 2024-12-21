package fr.openmc.core.features.city.commands;

import fr.openmc.core.utils.BlockVector2;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.city.*;
import fr.openmc.core.features.city.menu.CityMenu;
import fr.openmc.core.features.economy.EconomyManager;
import fr.openmc.core.utils.cooldown.DynamicCooldown;
import fr.openmc.core.utils.cooldown.DynamicCooldownManager;
import fr.openmc.core.utils.database.DatabaseManager;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import org.bukkit.*;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.*;
import revxrsal.commands.bukkit.annotation.CommandPermission;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

@Command({"ville", "city"})
public class CityCommands {
    public static HashMap<Player, Player> invitations = new HashMap<>(); // Invité, Inviteur

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
            MessagesManager.sendMessageType(player, "Tu n'as aucune invitation en attente", Prefix.CITY, MessageType.ERROR, false);
            return;
        }
        Player inviter = invitations.get(player);
        City newCity = CityManager.getPlayerCity(inviter.getUniqueId());

        if (newCity == null) {
            MessagesManager.sendMessageType(inviter, "L'invitation a expiré", Prefix.CITY, MessageType.SUCCESS, false);
            return;
        }

        newCity.addPlayer(player.getUniqueId());

        invitations.remove(player);

        MessagesManager.sendMessageType(player, "Tu as rejoint "+ newCity.getName(), Prefix.CITY, MessageType.SUCCESS, false);
        if (inviter.isOnline()) {
            MessagesManager.sendMessageType(inviter, player.getName()+" a accepté ton invitation !", Prefix.CITY, MessageType.SUCCESS, true);
        }
    }

    @Subcommand("rename")
    @CommandPermission("omc.commands.city.rename")
    @Description("Renommer une ville")
    void rename(Player player, @Named("nouveau nom") String name) {
        City playerCity = CityManager.getPlayerCity(player.getUniqueId());

        if (playerCity == null) {
            MessagesManager.sendMessageType(player, MessagesManager.Message.PLAYERNOCITY.getMessage(), Prefix.CITY, MessageType.ERROR, false);
        }

        if (!(playerCity.hasPermission(player.getUniqueId(), CPermission.RENAME))) {
            MessagesManager.sendMessageType(player, "Tu n'es pas le maire de la ville", Prefix.CITY, MessageType.ERROR, false);
            return;
        }

        if (isInvalidName(name)) {
            MessagesManager.sendMessageType(player, "Le nom de ville est invalide, il doit seulement comporter des caractères alphanumeriques et maximum 24 caractères.", Prefix.CITY, MessageType.ERROR, false);
            return;
        }

        playerCity.renameCity(name);
        MessagesManager.sendMessageType(player, "La ville a été renommée en " + name, Prefix.CITY, MessageType.SUCCESS, false);
    }

    @Subcommand("transfer")
    @CommandPermission("omc.commands.city.transfer")
    @Description("Transfert la propriété de votre ville")
    @AutoComplete("@city_members")
    void transfer(Player sender, @Named("maire") OfflinePlayer player) {
        City playerCity = CityManager.getPlayerCity(sender.getUniqueId());
        if (playerCity == null) {
            MessagesManager.sendMessageType(sender, MessagesManager.Message.PLAYERNOCITY.getMessage(), Prefix.CITY, MessageType.ERROR, false);
            return;
        }

        if (!(playerCity.hasPermission(sender.getUniqueId(), CPermission.OWNER))) {
            MessagesManager.sendMessageType(sender, "Tu n'es pas le maire de la ville", Prefix.CITY, MessageType.ERROR, false);
            return;
        }

        if (!playerCity.getMembers().contains(sender.getUniqueId())) {
            MessagesManager.sendMessageType(sender, "Ce joueur n'habite pas dans votre ville", Prefix.CITY, MessageType.ERROR, false);
            return;
        }

        playerCity.changeOwner(player.getUniqueId());
        MessagesManager.sendMessageType(sender, "Le nouveau maire est "+player.getName(), Prefix.CITY, MessageType.SUCCESS, false);

        if (player.isOnline()) {
            MessagesManager.sendMessageType((Player) player, "Vous êtes devenu le maire de la ville", Prefix.CITY, MessageType.INFO, true);
        }
    }

    @Subcommand("deny")
    @CommandPermission("omc.commands.city.deny")
    @Description("Refuser une invitation")
    void deny(Player player) {
        if (!invitations.containsKey(player)) {
            MessagesManager.sendMessageType(player, "Tu n'as aucune invitation en attente", Prefix.CITY, MessageType.ERROR, false);
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
        City city = CityManager.getPlayerCity(sender.getUniqueId());
        if (city == null) {
            MessagesManager.sendMessageType(sender, MessagesManager.Message.PLAYERNOCITY.getMessage(), Prefix.CITY, MessageType.ERROR, false);
            return;
        }

        if (sender.getUniqueId().equals(player.getUniqueId())) {
            MessagesManager.sendMessageType(sender, "Tu ne peux pas t'auto exclure de la ville", Prefix.CITY, MessageType.ERROR, false);
            return;
        }

        if (!(city.hasPermission(player.getUniqueId(), CPermission.KICK))) {
            MessagesManager.sendMessageType(sender, "Tu n'as pas la permission d'exclure " + player.getName(), Prefix.CITY, MessageType.ERROR, false);
            return;
        }

        if (city.hasPermission(player.getUniqueId(), CPermission.OWNER)) {
            MessagesManager.sendMessageType(sender, "Tu ne peux pas exclure le maire de la ville", Prefix.CITY, MessageType.ERROR, false);
            return;
        }

        if (city.removePlayer(player.getUniqueId())) {
            MessagesManager.sendMessageType(sender, "Tu as exclu "+player.getName()+" de la ville "+ city.getCityName(), Prefix.CITY, MessageType.SUCCESS, false);

            if (player.isOnline()) {
                MessagesManager.sendMessageType((Player) player, "Tu as été exclu de la ville "+ city.getCityName(), Prefix.CITY, MessageType.INFO, true);
            }
        } else {
            MessagesManager.sendMessageType(sender, "Impossible d'exclure "+player.getName()+" de la ville", Prefix.CITY, MessageType.ERROR, false);
        }
    }

    @Subcommand("leave")
    @CommandPermission("omc.commands.city.leave")
    @Description("Quitter votre ville")
    void leave(Player player) {
        City city = CityManager.getPlayerCity(player.getUniqueId());
        if (city == null) {
            MessagesManager.sendMessageType(player, MessagesManager.Message.PLAYERNOCITY.getMessage(), Prefix.CITY, MessageType.ERROR, false);
            return;
        }

        if (city.hasPermission(player.getUniqueId(), CPermission.OWNER)) {
            MessagesManager.sendMessageType(player, "Tu ne peux pas quitter la ville car tu en es le maire, supprime la ou transfère la propriété", Prefix.CITY, MessageType.ERROR, false);
            return;
        }

        if (city.removePlayer(player.getUniqueId())) {
            MessagesManager.sendMessageType(player, "Tu as quitté "+ city.getCityName(), Prefix.CITY, MessageType.SUCCESS, false);
        } else {
            MessagesManager.sendMessageType(player, "Impossible de quitter la ville", Prefix.CITY, MessageType.ERROR, false);
        }
    }

    @Subcommand("invite")
    @CommandPermission("omc.commands.city.invite")
    @Description("Inviter un joueur dans votre ville")
    void add(Player sender, @Named("invité") Player target) {
        City city = CityManager.getPlayerCity(sender.getUniqueId());
        if (city == null) {
            MessagesManager.sendMessageType(sender, MessagesManager.Message.PLAYERNOCITY.getMessage(), Prefix.CITY, MessageType.ERROR, false);
            return;
        }

        if (!(city.hasPermission(sender.getUniqueId(), CPermission.INVITE))) {
            MessagesManager.sendMessageType(sender, "Tu n'as pas la permission d'inviter des joueurs dans la ville", Prefix.CITY, MessageType.ERROR, false);
            return;
        }

        if (CityManager.getPlayerCity(target.getUniqueId()) != null) {
            MessagesManager.sendMessageType(sender, "Cette personne est déjà dans une ville", Prefix.CITY, MessageType.ERROR, false);
            return;
        }

        if (invitations.containsKey(target)) {
            MessagesManager.sendMessageType(sender, "Cette personne as déjà une invitation en attente", Prefix.CITY, MessageType.ERROR, false);
            return;
        }

        invitations.put(target, sender);
        MessagesManager.sendMessageType(sender, "Tu as invité "+target.getName()+" dans ta ville", Prefix.CITY, MessageType.SUCCESS, false);
        MessagesManager.sendMessageType(target, "Tu as été invité(e) par " + sender.getName() + "dans la ville "+city.getCityName(), Prefix.CITY, MessageType.INFO, false);
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
    @DynamicCooldown(group="city:big", message = "§cTu dois attendre avant de pouvoir supprimer ta ville (%sec% secondes)")
    void delete(Player sender) {
        UUID uuid = sender.getUniqueId();

        City city = CityManager.getPlayerCity(uuid);
        if (city == null) {
            MessagesManager.sendMessageType(sender, MessagesManager.Message.PLAYERNOCITY.getMessage(), Prefix.CITY, MessageType.ERROR, false);
            return;
        }

        if (!city.getPlayerWith(CPermission.OWNER).equals(uuid)) {
            MessagesManager.sendMessageType(sender, "Tu n'es pas le maire de la ville", Prefix.CITY, MessageType.ERROR, false);
            return;
        }

        city.delete();
        MessagesManager.sendMessageType(sender, "Votre ville a été supprimée", Prefix.CITY, MessageType.SUCCESS, false);

        DynamicCooldownManager.use(uuid, "city:big", 60000); //1 minute
    }

    @Subcommand("claim")
    @CommandPermission("omc.commands.city.claim")
    @Description("Claim un chunk pour votre ville")
    void claim(Player sender) {
        City city = CityManager.getPlayerCity(sender.getUniqueId());
        if (city == null) {
            MessagesManager.sendMessageType(sender, MessagesManager.Message.PLAYERNOCITY.getMessage(), Prefix.CITY, MessageType.ERROR, false);
            return;
        }

        if (!(city.hasPermission(sender.getUniqueId(), CPermission.CLAIM))) {
            MessagesManager.sendMessageType(sender, "Tu n'as pas la permission de claim", Prefix.CITY, MessageType.ERROR, false);
            return;
        }

        org.bukkit.World bWorld = sender.getWorld();
        if (!bWorld.getName().equals("world")) {
            MessagesManager.sendMessageType(sender, "Tu ne peux pas étendre ta ville ici", Prefix.CITY, MessageType.ERROR, false);
            return;
        }

        Chunk chunk = sender.getLocation().getChunk();
        int chunkX = chunk.getX();
        int chunkZ = chunk.getZ();
        BlockVector2 chunkVec2 = BlockVector2.at(chunkX, chunkZ);

        AtomicBoolean isFar = new AtomicBoolean(true);
        for (BlockVector2 chnk: city.getChunks()) {
            if (chnk.distance(chunkVec2) == 1) { //Si c'est en diagonale alors ça sera sqrt(2) ≈1.41
                isFar.set(false);
                break;
            }
        }

        if (isFar.get()) {
            MessagesManager.sendMessageType(sender, "Ce chunk n'est pas adjacent à ta ville", Prefix.CITY, MessageType.ERROR, false);
            return;
        }

        if (CityManager.isChunkClaimed(chunkX, chunkZ)) {
            // TODO: Vérifier si le chunk est dans le spawn
            MessagesManager.sendMessageType(sender, "Ce chunk est déjà claim", Prefix.CITY, MessageType.ERROR, false);
            return;
        }

        if (city.getChunks().size() >= 50) {
            MessagesManager.sendMessageType(sender, "Ta ville est trop grande", Prefix.CITY, MessageType.ERROR, false);
            return;
        }

        int price = calculatePrice(city.getChunks().size());
        if (city.getBalance() < price) {
            MessagesManager.sendMessageType(sender, "Ta ville n'a pas assez d'argent ("+price+EconomyManager.getEconomyIcon()+" nécessaires)", Prefix.CITY, MessageType.ERROR, false);
            return;
        }

        city.updateBalance((double) (price*-1));
        city.addChunk(sender.getChunk());
        MessagesManager.sendMessageType(sender, "Ta ville a été étendue", Prefix.CITY, MessageType.SUCCESS, false);
    }

    @Subcommand("money give")
    @CommandPermission("omc.commands.city.give")
    @Description("Transferer de l'argent vers la ville")
    void give(Player player, @Named("montant") @Range(min=1) double amount) {
        City city = CityManager.getPlayerCity(player.getUniqueId());
        if (city == null) {
            MessagesManager.sendMessageType(player, MessagesManager.Message.PLAYERNOCITY.getMessage(), Prefix.CITY, MessageType.ERROR, false);
            return;
        }

        if (!(city.hasPermission(player.getUniqueId(), CPermission.MONEY_GIVE))) {
            MessagesManager.sendMessageType(player, "Tu n'as pas la permission de donner de l'argent à ta ville", Prefix.CITY, MessageType.ERROR, false);
            return;
        }

        if (EconomyManager.getInstance().withdrawBalance(player.getUniqueId(), amount)) {
            city.updateBalance(amount);
            MessagesManager.sendMessageType(player, "Tu as transféré "+amount+EconomyManager.getEconomyIcon()+" à la ville", Prefix.CITY, MessageType.ERROR, false);
        } else {
            MessagesManager.sendMessageType(player, "Tu n'as pas assez d'argent", Prefix.CITY, MessageType.ERROR, false);
        }
    }

    @Subcommand("money balance")
    @CommandPermission("omc.commands.city.balance")
    @Description("Afficher l'argent de votre ville")
    void balance(Player player) {
        City city = CityManager.getPlayerCity(player.getUniqueId());
        if (city == null) {
            MessagesManager.sendMessageType(player, MessagesManager.Message.PLAYERNOCITY.getMessage(), Prefix.CITY, MessageType.ERROR, false);
            return;
        }

        if (!(city.hasPermission(player.getUniqueId(), CPermission.MONEY_BALANCE))) {
            MessagesManager.sendMessageType(player, "Tu n'as pas la permission de consulter l'argent de la ville", Prefix.CITY, MessageType.ERROR, false);
            return;
        }

        double balance = city.getBalance();
        MessagesManager.sendMessageType(player, city.getCityName()+ " possède "+balance+EconomyManager.getEconomyIcon(), Prefix.CITY, MessageType.INFO, false);
    }

    @Subcommand("money take")
    @CommandPermission("omc.commands.city.take")
    @Description("Prendre de l'argent depuis votre ville")
    void take(Player player, @Named("montant") @Range(min=1) double amount) {
        City city = CityManager.getPlayerCity(player.getUniqueId());
        if (city == null) {
            MessagesManager.sendMessageType(player, MessagesManager.Message.PLAYERNOCITY.getMessage(), Prefix.CITY, MessageType.ERROR, false);
            return;
        }

        if (!(city.hasPermission(player.getUniqueId(), CPermission.MONEY_TAKE))) {
            MessagesManager.sendMessageType(player, "Tu n'as pas la permission de prendre de l'argent de ta ville", Prefix.CITY, MessageType.ERROR, false);
            return;
        }

        if (city.getBalance() < amount) {
            MessagesManager.sendMessageType(player, "Ta ville n'a pas assez d'argent en banque", Prefix.CITY, MessageType.ERROR, false);
            return;
        }

        city.updateBalance(amount*-1);
        EconomyManager.getInstance().addBalance(player.getUniqueId(), amount);
        MessagesManager.sendMessageType(player, amount+EconomyManager.getEconomyIcon()+" ont été transférés à votre compte", Prefix.CITY, MessageType.SUCCESS, false);
    }

    @Subcommand("info")
    @CommandPermission("omc.commands.city.info")
    @Description("Avoir des informations sur votre ville")
    void info(Player player) {
        City city = CityManager.getPlayerCity(player.getUniqueId());

        if (city == null) {
            MessagesManager.sendMessageType(player, MessagesManager.Message.PLAYERNOCITY.getMessage(), Prefix.CITY, MessageType.ERROR, false);
            return;
        }

        CityMessages.sendInfo(player, city);
    }

    @Subcommand("create")
    @CommandPermission("omc.commands.city.create")
    @Description("Créer une ville")
    @DynamicCooldown(group="city:big", message = "§cTu dois attendre avant de pouvoir créer une ville (%sec% secondes)")
    void create(Player player, @Named("nom") String name) {
        UUID uuid = player.getUniqueId();

        if (CityManager.getPlayerCity(uuid) != null) {
            MessagesManager.sendMessageType(player, MessagesManager.Message.PLAYERINCITY.getMessage(), Prefix.CITY, MessageType.ERROR, false);
            return;
        }

        if (isInvalidName(name)) {
            MessagesManager.sendMessageType(player, "Le nom de ville est invalide, il doit contenir seulement des caractères alphanumerique et doit faire moins de 24 charactères", Prefix.CITY, MessageType.ERROR, false);
            return;
        }

        String cityUUID = UUID.randomUUID().toString().substring(0, 8);

        Chunk origin = player.getChunk();
        AtomicBoolean isClaimed = new AtomicBoolean(false);
        for (int x = origin.getX()-2; x <= origin.getX()+2; x++) {
            for (int z = origin.getZ() - 2; z <= origin.getZ() + 2; z++) {
                if (CityManager.isChunkClaimed(x,z)) {
                    isClaimed.set(true);
                    break;
                }
            }
        }

        if (isClaimed.get()) {
            MessagesManager.sendMessageType(player, "Cette parcelle est déjà claim", Prefix.CITY, MessageType.ERROR, false);
            return;
        }

        Bukkit.getScheduler().runTaskAsynchronously(OMCPlugin.getInstance(), () -> {
            try {
                PreparedStatement statement = DatabaseManager.getConnection().prepareStatement("INSERT INTO city_regions (city_uuid, x, z) VALUES (?, ?, ?)");
                statement.setString(1, cityUUID);

                for (int x = origin.getX()-2; x <= origin.getX()+2; x++) {
                    for (int z = origin.getZ()-2; z <= origin.getZ()+2; z++) {
                        statement.setInt(2, x);
                        statement.setInt(3, z);
                        statement.addBatch();
                    }
                }

                statement.executeBatch();
                statement.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });

        City city = CityManager.createCity(uuid, cityUUID, name);
        city.addPlayer(uuid);
        city.addPermission(uuid, CPermission.OWNER);

        MessagesManager.sendMessageType(player, "Votre ville a été créée", Prefix.CITY, MessageType.SUCCESS, false);

        DynamicCooldownManager.use(uuid, "city:big", 60000); //1 minute
    }
}
