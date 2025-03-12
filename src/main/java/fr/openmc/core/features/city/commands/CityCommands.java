package fr.openmc.core.features.city.commands;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.city.listeners.CityTypeCooldown;
import fr.openmc.core.features.city.mascots.MascotsLevels;
import fr.openmc.core.features.city.mascots.MascotsManager;
import fr.openmc.core.utils.BlockVector2;
import fr.openmc.core.features.city.*;
import fr.openmc.core.features.city.menu.*;
import fr.openmc.core.features.economy.EconomyManager;
import fr.openmc.core.utils.cooldown.DynamicCooldown;
import fr.openmc.core.utils.cooldown.DynamicCooldownManager;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;
import revxrsal.commands.annotation.*;
import revxrsal.commands.bukkit.annotation.CommandPermission;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

@Command({"ville", "city"})
public class CityCommands {
    public static HashMap<Player, Player> invitations = new HashMap<>(); // Invité, Inviteur
    public static Map<String, BukkitRunnable> balanceCooldownTasks = new HashMap<>();

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
            MessagesManager.sendMessage(player, Component.text("Tu n'as aucune invitation en attente"), Prefix.CITY, MessageType.ERROR, false);
            return;
        }
        Player inviter = invitations.get(player);
        City newCity = CityManager.getPlayerCity(inviter.getUniqueId());

        if (newCity == null) {
            MessagesManager.sendMessage(inviter, Component.text("L'invitation a expiré"), Prefix.CITY, MessageType.SUCCESS, false);
            return;
        }

        newCity.addPlayer(player.getUniqueId());

        invitations.remove(player);

        MessagesManager.sendMessage(player, Component.text("Tu as rejoint "+ newCity.getName()), Prefix.CITY, MessageType.SUCCESS, false);
        if (inviter.isOnline()) {
            MessagesManager.sendMessage(inviter, Component.text(player.getName()+" a accepté ton invitation !"), Prefix.CITY, MessageType.SUCCESS, true);
        }
    }

    @Subcommand("rename")
    @CommandPermission("omc.commands.city.rename")
    @Description("Renommer une ville")
    void rename(Player player, @Named("nouveau nom") String name) {
        City playerCity = CityManager.getPlayerCity(player.getUniqueId());

        if (playerCity == null) {
            MessagesManager.sendMessage(player, MessagesManager.Message.PLAYERNOCITY.getMessage(), Prefix.CITY, MessageType.ERROR, false);
        }

        if (!(playerCity.hasPermission(player.getUniqueId(), CPermission.RENAME))) {
            MessagesManager.sendMessage(player, Component.text("Tu n'es pas le maire de la ville"), Prefix.CITY, MessageType.ERROR, false);
            return;
        }

        if (isInvalidName(name)) {
            MessagesManager.sendMessage(player, Component.text("Le nom de ville est invalide, il doit seulement comporter des caractères alphanumeriques et maximum 24 caractères."), Prefix.CITY, MessageType.ERROR, false);
            return;
        }

        playerCity.renameCity(name);
        MessagesManager.sendMessage(player, Component.text("La ville a été renommée en " + name), Prefix.CITY, MessageType.SUCCESS, false);
    }

    @Subcommand("transfer")
    @CommandPermission("omc.commands.city.transfer")
    @Description("Transfert la propriété de votre ville")
    @AutoComplete("@city_members")
    void transfer(Player sender, @Named("maire") OfflinePlayer player) {
        City playerCity = CityManager.getPlayerCity(sender.getUniqueId());
        if (playerCity == null) {
            MessagesManager.sendMessage(sender, MessagesManager.Message.PLAYERNOCITY.getMessage(), Prefix.CITY, MessageType.ERROR, false);
            return;
        }

        if (!(playerCity.hasPermission(sender.getUniqueId(), CPermission.OWNER))) {
            MessagesManager.sendMessage(sender, Component.text("Tu n'es pas le maire de la ville"), Prefix.CITY, MessageType.ERROR, false);
            return;
        }

        if (!playerCity.getMembers().contains(sender.getUniqueId())) {
            MessagesManager.sendMessage(sender, Component.text("Ce joueur n'habite pas dans votre ville"), Prefix.CITY, MessageType.ERROR, false);
            return;
        }

        playerCity.changeOwner(player.getUniqueId());
        MessagesManager.sendMessage(sender, Component.text("Le nouveau maire est "+player.getName()), Prefix.CITY, MessageType.SUCCESS, false);

        if (player.isOnline()) {
            MessagesManager.sendMessage((Player) player, Component.text("Vous êtes devenu le maire de la ville"), Prefix.CITY, MessageType.INFO, true);
        }
    }

    @Subcommand("deny")
    @CommandPermission("omc.commands.city.deny")
    @Description("Refuser une invitation")
    void deny(Player player) {
        if (!invitations.containsKey(player)) {
            MessagesManager.sendMessage(player, Component.text("Tu n'as aucune invitation en attente"), Prefix.CITY, MessageType.ERROR, false);
            return;
        }
        Player inviter = invitations.get(player);
        invitations.remove(player);

        if (inviter.isOnline()) {
            MessagesManager.sendMessage(inviter, Component.text(player.getName()+" a refusé ton invitation"), Prefix.CITY, MessageType.WARNING, true);
        }
    }

    @Subcommand("kick")
    @CommandPermission("omc.commands.city.kick")
    @Description("Exclure un habitant de votre ville")
    @AutoComplete("@city_members")
    void kick(Player sender, @Named("exclu") OfflinePlayer player) {
        City city = CityManager.getPlayerCity(sender.getUniqueId());
        if (city == null) {
            MessagesManager.sendMessage(sender, MessagesManager.Message.PLAYERNOCITY.getMessage(), Prefix.CITY, MessageType.ERROR, false);
            return;
        }

        if (sender.getUniqueId().equals(player.getUniqueId())) {
            MessagesManager.sendMessage(sender, Component.text("Tu ne peux pas t'auto exclure de la ville"), Prefix.CITY, MessageType.ERROR, false);
            return;
        }

        if (!(city.hasPermission(player.getUniqueId(), CPermission.KICK))) {
            MessagesManager.sendMessage(sender, Component.text("Tu n'as pas la permission d'exclure " + player.getName()), Prefix.CITY, MessageType.ERROR, false);
            return;
        }

        if (city.hasPermission(player.getUniqueId(), CPermission.OWNER)) {
            MessagesManager.sendMessage(sender, Component.text("Tu ne peux pas exclure le maire de la ville"), Prefix.CITY, MessageType.ERROR, false);
            return;
        }

        if (city.removePlayer(player.getUniqueId())) {
            MessagesManager.sendMessage(sender, Component.text("Tu as exclu "+player.getName()+" de la ville "+ city.getCityName()), Prefix.CITY, MessageType.SUCCESS, false);

            if (player.isOnline()) {
                MessagesManager.sendMessage((Player) player, Component.text("Tu as été exclu de la ville "+ city.getCityName()), Prefix.CITY, MessageType.INFO, true);
            }
        } else {
            MessagesManager.sendMessage(sender, Component.text("Impossible d'exclure "+player.getName()+" de la ville"), Prefix.CITY, MessageType.ERROR, false);
        }
    }

    @Subcommand("leave")
    @CommandPermission("omc.commands.city.leave")
    @Description("Quitter votre ville")
    void leave(Player player) {
        City city = CityManager.getPlayerCity(player.getUniqueId());
        if (city == null) {
            MessagesManager.sendMessage(player, MessagesManager.Message.PLAYERNOCITY.getMessage(), Prefix.CITY, MessageType.ERROR, false);
            return;
        }

        if (city.hasPermission(player.getUniqueId(), CPermission.OWNER)) {
            MessagesManager.sendMessage(player, Component.text("Tu ne peux pas quitter la ville car tu en es le maire, supprime la ou transfère la propriété"), Prefix.CITY, MessageType.ERROR, false);
            return;
        }

        if (city.removePlayer(player.getUniqueId())) {
            MessagesManager.sendMessage(player, Component.text("Tu as quitté "+ city.getCityName()), Prefix.CITY, MessageType.SUCCESS, false);
        } else {
            MessagesManager.sendMessage(player, Component.text("Impossible de quitter la ville"), Prefix.CITY, MessageType.ERROR, false);
        }
    }

    @Subcommand("invite")
    @CommandPermission("omc.commands.city.invite")
    @Description("Inviter un joueur dans votre ville")
    void add(Player sender, @Named("invité") Player target) {
        City city = CityManager.getPlayerCity(sender.getUniqueId());
        if (city == null) {
            MessagesManager.sendMessage(sender, MessagesManager.Message.PLAYERNOCITY.getMessage(), Prefix.CITY, MessageType.ERROR, false);
            return;
        }

        if (!(city.hasPermission(sender.getUniqueId(), CPermission.INVITE))) {
            MessagesManager.sendMessage(sender, Component.text("Tu n'as pas la permission d'inviter des joueurs dans la ville"), Prefix.CITY, MessageType.ERROR, false);
            return;
        }

        if (CityManager.getPlayerCity(target.getUniqueId()) != null) {
            MessagesManager.sendMessage(sender, Component.text("Cette personne est déjà dans une ville"), Prefix.CITY, MessageType.ERROR, false);
            return;
        }

        if (invitations.containsKey(target)) {
            MessagesManager.sendMessage(sender, Component.text("Cette personne as déjà une invitation en attente"), Prefix.CITY, MessageType.ERROR, false);
            return;
        }

        invitations.put(target, sender);
        MessagesManager.sendMessage(sender, Component.text("Tu as invité "+target.getName()+" dans ta ville"), Prefix.CITY, MessageType.SUCCESS, false);
        MessagesManager.sendMessage(target, Component.text("Tu as été invité(e) par " + sender.getName() + "dans la ville "+city.getCityName()), Prefix.CITY, MessageType.INFO, false);
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
            MessagesManager.sendMessage(sender, MessagesManager.Message.PLAYERNOCITY.getMessage(), Prefix.CITY, MessageType.ERROR, false);
            return;
        }

        if (!city.getPlayerWith(CPermission.OWNER).equals(uuid)) {
            MessagesManager.sendMessage(sender, Component.text("Tu n'es pas le maire de la ville"), Prefix.CITY, MessageType.ERROR, false);
            return;
        }

        for (UUID townMember : city.getMembers()){
            if (Bukkit.getPlayer(townMember) instanceof Player player){
                player.clearActivePotionEffects();
            }
        }

        city.delete();
        MessagesManager.sendMessage(sender, Component.text("Votre ville a été supprimée"), Prefix.CITY, MessageType.SUCCESS, false);

        DynamicCooldownManager.use(uuid, "city:big", 60000); //1 minute
    }

    @Subcommand("claim")
    @CommandPermission("omc.commands.city.claim")
    @Description("Claim un chunk pour votre ville")
    void claim(Player sender) {
        City city = CityManager.getPlayerCity(sender.getUniqueId());
        if (city == null) {
            MessagesManager.sendMessage(sender, MessagesManager.Message.PLAYERNOCITY.getMessage(), Prefix.CITY, MessageType.ERROR, false);
            return;
        }

        if (!(city.hasPermission(sender.getUniqueId(), CPermission.CLAIM))) {
            MessagesManager.sendMessage(sender, Component.text("Tu n'as pas la permission de claim"), Prefix.CITY, MessageType.ERROR, false);
            return;
        }

        org.bukkit.World bWorld = sender.getWorld();
        if (!bWorld.getName().equals("world")) {
            MessagesManager.sendMessage(sender, Component.text("Tu ne peux pas étendre ta ville ici"), Prefix.CITY, MessageType.ERROR, false);
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
            MessagesManager.sendMessage(sender, Component.text("Ce chunk n'est pas adjacent à ta ville"), Prefix.CITY, MessageType.ERROR, false);
            return;
        }

        if (CityManager.isChunkClaimed(chunkX, chunkZ)) {
            // TODO: Vérifier si le chunk est dans le spawn
            MessagesManager.sendMessage(sender, Component.text("Ce chunk est déjà claim"), Prefix.CITY, MessageType.ERROR, false);
            return;
        }

        if (city.getChunks().size() >= 50) {
            MessagesManager.sendMessage(sender, Component.text("Ta ville est trop grande"), Prefix.CITY, MessageType.ERROR, false);
            return;
        }

        int price = calculatePrice(city.getChunks().size());

        if (!MascotsManager.freeClaim.containsKey(city.getUUID())) {
            if (city.getBalance() < price) {
                MessagesManager.sendMessage(sender, Component.text("Ta ville n'a pas assez d'argent ("+price+EconomyManager.getEconomyIcon()+" nécessaires)"), Prefix.CITY, MessageType.ERROR, false);
                return;
            }
        }

        if (MascotsManager.freeClaim.containsKey(city.getUUID())){
            MascotsManager.freeClaim.replace(city.getUUID(), MascotsManager.freeClaim.get(city.getUUID()) - 1);
            if (MascotsManager.freeClaim.get(city.getUUID())<= 0){
                MascotsManager.freeClaim.remove(city.getUUID());
            }
        } else {
            city.updateBalance((double) (price*-1));
        }
        city.addChunk(sender.getChunk());
        MessagesManager.sendMessage(sender, Component.text("Ta ville a été étendue"), Prefix.CITY, MessageType.SUCCESS, false);
    }

    @Subcommand("money give")
    @CommandPermission("omc.commands.city.give")
    @Description("Transferer de l'argent vers la ville")
    void give(Player player, @Named("montant") @Range(min=1) double amount) {
        City city = CityManager.getPlayerCity(player.getUniqueId());
        if (city == null) {
            MessagesManager.sendMessage(player, MessagesManager.Message.PLAYERNOCITY.getMessage(), Prefix.CITY, MessageType.ERROR, false);
            return;
        }

        if (!(city.hasPermission(player.getUniqueId(), CPermission.MONEY_GIVE))) {
            MessagesManager.sendMessage(player, Component.text("Tu n'as pas la permission de donner de l'argent à ta ville"), Prefix.CITY, MessageType.ERROR, false);
            return;
        }

        if (balanceCooldownTasks.containsKey(city.getUUID())){
            MessagesManager.sendMessage(player, Component.text("Ta ville a été attaquer tu n'as donc pas accès à la banque de vlle"), Prefix.CITY, MessageType.ERROR, false);
            return;
        }

        if (EconomyManager.getInstance().withdrawBalance(player.getUniqueId(), amount)) {
            city.updateBalance(amount);
            MessagesManager.sendMessage(player, Component.text("Tu as transféré "+amount+EconomyManager.getEconomyIcon()+" à la ville"), Prefix.CITY, MessageType.ERROR, false);
        } else {
            MessagesManager.sendMessage(player, Component.text("Tu n'as pas assez d'argent"), Prefix.CITY, MessageType.ERROR, false);
        }
    }

    @Subcommand("money balance")
    @CommandPermission("omc.commands.city.balance")
    @Description("Afficher l'argent de votre ville")
    void balance(Player player) {
        City city = CityManager.getPlayerCity(player.getUniqueId());
        if (city == null) {
            MessagesManager.sendMessage(player, MessagesManager.Message.PLAYERNOCITY.getMessage(), Prefix.CITY, MessageType.ERROR, false);
            return;
        }

        if (!(city.hasPermission(player.getUniqueId(), CPermission.MONEY_BALANCE))) {
            MessagesManager.sendMessage(player, Component.text("Tu n'as pas la permission de consulter l'argent de la ville"), Prefix.CITY, MessageType.ERROR, false);
            return;
        }

        double balance = city.getBalance();
        MessagesManager.sendMessage(player, Component.text(city.getCityName()+ " possède "+balance+EconomyManager.getEconomyIcon()), Prefix.CITY, MessageType.INFO, false);
    }

    @Subcommand("money take")
    @CommandPermission("omc.commands.city.take")
    @Description("Prendre de l'argent depuis votre ville")
    void take(Player player, @Named("montant") @Range(min=1) double amount) {
        City city = CityManager.getPlayerCity(player.getUniqueId());
        if (city == null) {
            MessagesManager.sendMessage(player, MessagesManager.Message.PLAYERNOCITY.getMessage(), Prefix.CITY, MessageType.ERROR, false);
            return;
        }

        if (!(city.hasPermission(player.getUniqueId(), CPermission.MONEY_TAKE))) {
            MessagesManager.sendMessage(player, Component.text("Tu n'as pas la permission de prendre de l'argent de ta ville"), Prefix.CITY, MessageType.ERROR, false);
            return;
        }

        if (balanceCooldownTasks.containsKey(city.getUUID())){
            MessagesManager.sendMessage(player, Component.text("Ta ville a été attaquer tu n'as donc pas accès à la banque de vlle"), Prefix.CITY, MessageType.ERROR, false);
            return;
        }

        if (city.getBalance() < amount) {
            MessagesManager.sendMessage(player, Component.text("Ta ville n'a pas assez d'argent en banque"), Prefix.CITY, MessageType.ERROR, false);
            return;
        }

        city.updateBalance(amount*-1);
        EconomyManager.getInstance().addBalance(player.getUniqueId(), amount);
        MessagesManager.sendMessage(player, Component.text(amount+EconomyManager.getEconomyIcon()+" ont été transférés à votre compte"), Prefix.CITY, MessageType.SUCCESS, false);
    }

    @Subcommand("info")
    @CommandPermission("omc.commands.city.info")
    @Description("Avoir des informations sur votre ville")
    void info(Player player) {
        City city = CityManager.getPlayerCity(player.getUniqueId());

        if (city == null) {
            MessagesManager.sendMessage(player, MessagesManager.Message.PLAYERNOCITY.getMessage(), Prefix.CITY, MessageType.ERROR, false);
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
            MessagesManager.sendMessage(player, MessagesManager.Message.PLAYERINCITY.getMessage(), Prefix.CITY, MessageType.ERROR, false);
            return;
        }

        if (isInvalidName(name)) {
            MessagesManager.sendMessage(player, Component.text("Le nom de ville est invalide, il doit contenir seulement des caractères alphanumerique et doit faire moins de 24 charactères"), Prefix.CITY, MessageType.ERROR, false);
            return;
        }

        new CityTypeMenu(player, name).open();
    }
    @Subcommand("change")
    @CommandPermission("omc.commands.city.change")
    public void change(Player sender) {
        City city = CityManager.getPlayerCity(sender.getUniqueId());
        if (city==null){
            MessagesManager.sendMessage(sender, MessagesManager.Message.PLAYERINCITY.getMessage(), Prefix.CITY, MessageType.ERROR, false);
            return;
        }
        sender.sendMessage("§cEs-tu sûr de vouloir changer le type de ta ville ?");
        sender.sendMessage("§cSi tu fais cela ta mascotte §4§lPERDERA 2 NIVEAUX");
        sender.sendMessage("§cSi tu en es sûr fais §n/city chgconfirm");

        //TODO: mettre ConfirmMenu
    }

    @Subcommand("chgconfirm")
    @CommandPermission("omc.commands.city.chgconfirm")
    public void changeConfirm (Player sender){
        City city = CityManager.getPlayerCity(sender.getUniqueId());
        if (city==null){
            MessagesManager.sendMessage(sender, MessagesManager.Message.PLAYERINCITY.getMessage(), Prefix.CITY, MessageType.ERROR, false);
            return;
        }

        MascotsManager.loadMascotsConfig();
        if (MascotsManager.mascotsConfig.contains("mascots." + city.getUUID())){
            if (!MascotsManager.mascotsConfig.getBoolean("mascots." + city.getUUID() + "alive")){
                MessagesManager.sendMessage(sender, Component.text("Vous devez soigner votre mascotte avant"), Prefix.CITY, MessageType.ERROR, false);
                return;
            }
        }

        if (CityTypeCooldown.isOnCooldown(city.getUUID())){
            MessagesManager.sendMessage(sender, Component.text("Vous devez attendre " + CityTypeCooldown.getRemainingCooldown(city.getUUID())/1000 + " seconds pour changer de type de ville"), Prefix.CITY, MessageType.ERROR, false);
            return;
        }
        CityManager.changeCityType(city.getUUID());
        CityTypeCooldown.setCooldown(city.getUUID());

        LivingEntity mob = (LivingEntity) Bukkit.getEntity(MascotsManager.getMascotsUUIDbyCityUUID(city.getUUID()));
        MascotsLevels mascotsLevels = MascotsLevels.valueOf((String) MascotsManager.mascotsConfig.get("mascots." + city.getUUID() +".level"));

        for (UUID townMember : city.getMembers()){
            if (Bukkit.getPlayer(townMember) instanceof Player player){
                for (PotionEffect potionEffect : mascotsLevels.getBonus()){
                    player.removePotionEffect(potionEffect.getType());
                }
                MascotsManager.giveMascotsEffect(city.getUUID(), player.getUniqueId());
            }
        }

        double lastHealth = mascotsLevels.getHealth();
        int newLevel = Integer.parseInt(String.valueOf(mascotsLevels).replaceAll("[^0-9]", ""))-2;
        if (newLevel < 1){
            newLevel = 1;
        }
        MascotsManager.mascotsConfig.set("mascots." + city.getUUID() + ".level", String.valueOf(MascotsLevels.valueOf("level"+newLevel)));
        MascotsManager.saveMascotsConfig();
        mascotsLevels = MascotsLevels.valueOf((String)  MascotsManager.mascotsConfig.get("mascots." + city.getUUID() +".level"));

        try {
            int maxHealth = mascotsLevels.getHealth();
            mob.setMaxHealth(maxHealth);
            if (mob.getHealth() >= lastHealth){
                mob.setHealth(maxHealth);
            }
            double currentHealth = mob.getHealth();
            mob.setCustomName("§lMascotte §c" + currentHealth + "/" + maxHealth + "❤");
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public static void startBalanceCooldown(String city_uuid) {
        if (balanceCooldownTasks.containsKey(city_uuid)) {
            balanceCooldownTasks.get(city_uuid).cancel();
        }

        BukkitRunnable cooldownTask = new BukkitRunnable() {
            @Override
            public void run() {
                balanceCooldownTasks.remove(city_uuid);
            }
        };

        balanceCooldownTasks.put(city_uuid, cooldownTask);
        cooldownTask.runTaskLater(OMCPlugin.getInstance(), 30 * 60 * 20L);
    }
}