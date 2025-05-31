package fr.openmc.core.features.city.commands;

import fr.openmc.api.chronometer.Chronometer;
import fr.openmc.api.input.signgui.SignGUI;
import fr.openmc.api.input.signgui.exception.SignGUIVersionException;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.city.CPermission;
import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.CityMessages;
import fr.openmc.core.features.city.actions.*;
import fr.openmc.core.features.city.conditions.*;
import fr.openmc.core.features.city.mayor.CityLaw;
import fr.openmc.core.features.city.mayor.managers.MayorManager;
import fr.openmc.core.features.city.mayor.menu.MayorElectionMenu;
import fr.openmc.core.features.city.mayor.menu.MayorMandateMenu;
import fr.openmc.core.features.city.menu.CityMenu;
import fr.openmc.core.features.city.menu.CityTypeMenu;
import fr.openmc.core.features.city.menu.NoCityMenu;
import fr.openmc.core.features.city.menu.bank.CityBankMenu;
import fr.openmc.core.features.city.menu.list.CityListMenu;
import fr.openmc.core.utils.InputUtils;
import fr.openmc.core.utils.ItemUtils;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import revxrsal.commands.annotation.Optional;
import revxrsal.commands.annotation.*;
import revxrsal.commands.bukkit.annotation.CommandPermission;

import java.util.*;

@Command({"ville", "city"})
public class CityCommands {
    public static HashMap<Player, List<Player>> invitations = new HashMap<>(); // Invité, Inviteurs
    public static Map<String, BukkitRunnable> balanceCooldownTasks = new HashMap<>();

    @DefaultFor("~")
    void main(Player player) {
        City playerCity = CityManager.getPlayerCity(player.getUniqueId());
        if (!Chronometer.containsChronometer(player.getUniqueId(), "Mascot:chest")) {
                if (playerCity == null) {
                    NoCityMenu menu = new NoCityMenu(player);
                    menu.open();
                } else {
                    CityMenu menu = new CityMenu(player);
                    menu.open();
                }
        } else {
            MessagesManager.sendMessage(player, Component.text("Vous ne pouvez pas ouvrir le menu des villes si vous devez poser votre mascotte"), Prefix.CITY, MessageType.ERROR, false);
        }
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
    void create(Player player, @Optional String name) {
        if (!CityCreateConditions.canCityCreate(player, null)) {
            return;
        }

        if (name != null) {
            CityCreateAction.beginCreateCity(player, name);
            return;
        }

        String[] lines = new String[4];
        lines[0] = "";
        lines[1] = " ᐱᐱᐱᐱᐱᐱᐱ ";
        lines[2] = "Entrez votre nom";
        lines[3] = "de ville ci dessus";

        SignGUI gui;
        try {
            gui = SignGUI.builder()
                    .setLines(null, lines[1], lines[2], lines[3])
                    .setType(ItemUtils.getSignType(player))
                    .setHandler((p, result) -> {
                        String input = result.getLine(0);

                        Bukkit.getScheduler().runTask(OMCPlugin.getInstance(), () -> {
                            CityCreateAction.beginCreateCity(player, input);
                        });

                        return Collections.emptyList();
                    })
                    .build();
        } catch (SignGUIVersionException e) {
            throw new RuntimeException(e);
        }

        gui.open(player);
    }

    @Subcommand("delete")
    @CommandPermission("omc.commands.city.delete")
    @Description("Supprimer votre ville")
    void delete(Player sender) {
        CityDeleteAction.startDeleteCity(sender);
    }

    @Subcommand("invite")
    @CommandPermission("omc.commands.city.invite")
    @Description("Inviter un joueur dans votre ville")
    public static void invite(Player sender, @Named("invité") Player target) {
        City city = CityManager.getPlayerCity(sender.getUniqueId());

        if (!CityInviteConditions.canCityInvitePlayer(city, sender, target)) return;

        List<Player> playerInvitations = invitations.get(target);
        if (playerInvitations == null) {
            List<Player> newInvitations = new ArrayList<>();
            newInvitations.add(sender);
            invitations.put(target, newInvitations);
        } else {
            playerInvitations.add(sender);
        }
        MessagesManager.sendMessage(sender, Component.text("Tu as invité " + target.getName() + " dans ta ville"), Prefix.CITY, MessageType.SUCCESS, false);
        MessagesManager.sendMessage(target,
                Component.text("Tu as été invité(e) par " + sender.getName() + " dans la ville " + city.getName() + "\n")
                        .append(Component.text("§8Faite §a/city accept §8pour accepter\n").clickEvent(ClickEvent.runCommand("/city accept " + sender.getName())).hoverEvent(HoverEvent.showText(Component.text("Accepter l'invitation"))))
                        .append(Component.text("§8Faite §c/city deny §8pour refuser\n").clickEvent(ClickEvent.runCommand("/city deny " + sender.getName())).hoverEvent(HoverEvent.showText(Component.text("Refuser l'invitation")))),
                Prefix.CITY, MessageType.INFO, false);
    }

    @Subcommand("accept")
    @CommandPermission("omc.commands.city.accept")
    @Description("Accepter une invitation")
    public static void acceptInvitation(Player player, Player inviter) {
        List<Player> playerInvitations = invitations.get(player);
        if (!playerInvitations.contains(inviter)) {
            MessagesManager.sendMessage(player, Component.text(inviter.getName() + " ne vous a pas invité"), Prefix.CITY, MessageType.ERROR, false);
            return;
        }

        City newCity = CityManager.getPlayerCity(inviter.getUniqueId());

        if (!CityInviteConditions.canCityInviteAccept(newCity, inviter, player)) return;

        newCity.addPlayer(player.getUniqueId());

        invitations.remove(player);

        MessagesManager.sendMessage(player, Component.text("Tu as rejoint "+ newCity.getName()), Prefix.CITY, MessageType.SUCCESS, false);
        if (inviter.isOnline()) {
            MessagesManager.sendMessage(inviter, Component.text(player.getName()+" a accepté ton invitation !"), Prefix.CITY, MessageType.SUCCESS, true);
        }
    }

    @Subcommand("deny")
    @CommandPermission("omc.commands.city.deny")
    @Description("Refuser une invitation")
    public static void denyInvitation(Player player, Player inviter) {
        if (!CityInviteConditions.canCityInviteDeny(player, inviter)) return;

        invitations.remove(player);

        if (inviter.isOnline()) {
            MessagesManager.sendMessage(inviter, Component.text(player.getName() + " a refusé ton invitation"), Prefix.CITY, MessageType.WARNING, true);
        }
    }

    @Subcommand("rename")
    @CommandPermission("omc.commands.city.rename")
    @Description("Renommer une ville")
    void rename(Player player, @Named("nouveau nom") String name) {
        City playerCity = CityManager.getPlayerCity(player.getUniqueId());

        if (!CityManageConditions.canCityRename(playerCity, player)) return;

        if (!InputUtils.isInputCityName(name)) {
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

        if (!CityManageConditions.canCityTransfer(playerCity, sender)) return;

        if (playerCity == null) return;

        playerCity.changeOwner(player.getUniqueId());
        MessagesManager.sendMessage(sender, Component.text("Le nouveau maire est "+player.getName()), Prefix.CITY, MessageType.SUCCESS, false);

        if (player.isOnline()) {
            MessagesManager.sendMessage((Player) player, Component.text("Vous êtes devenu le maire de la ville"), Prefix.CITY, MessageType.INFO, true);
        }
    }

    @Subcommand("kick")
    @CommandPermission("omc.commands.city.kick")
    @Description("Exclure un habitant de votre ville")
    @AutoComplete("@city_members")
    void kick(Player sender, @Named("exclu") OfflinePlayer player) {
        CityKickAction.startKick(sender, player);
    }

    @Subcommand("leave")
    @CommandPermission("omc.commands.city.leave")
    @Description("Quitter votre ville")
    void leave(Player player) {
        City city = CityManager.getPlayerCity(player.getUniqueId());
        if (!CityLeaveCondition.canCityLeave(city, player)) return;

        CityLeaveAction.startLeave(player);
    }

    @Subcommand("claim")
    @CommandPermission("omc.commands.city.claim")
    @Description("Claim un chunk pour votre ville")
    void claim(Player sender) {
        City city = CityManager.getPlayerCity(sender.getUniqueId());

        if (!CityClaimCondition.canCityClaim(city, sender)) return;

        Chunk chunk = sender.getLocation().getChunk();

        CityClaimAction.startClaim(sender, chunk.getX(), chunk.getZ());
    }

    @Subcommand("list")
    @CommandPermission("omc.commands.city.list")
    public void list(Player player) {
        List<City> cities = new ArrayList<>(CityManager.getCities());
        if (cities.isEmpty()) {
            MessagesManager.sendMessage(player, Component.text("Aucune ville n'existe"), Prefix.CITY, MessageType.ERROR, false);
            return;
        }

        CityListMenu menu = new CityListMenu(player, cities);
        menu.open();
    }

    @Subcommand("type")
    @CommandPermission("omc.commands.city.type")
    public void change(Player sender) {
        new CityTypeMenu(sender).open();
    }

    // making the subcommand only "bank" overrides "bank deposit" and "bank withdraw"
    @Subcommand({"bank view"})
    @Description("Ouvre le menu de la banque de ville")
    void bank(Player player) {
        if (CityManager.getPlayerCity(player.getUniqueId()) == null)
            return;

        new CityBankMenu(player).open();
    }

    @Subcommand("bank deposit")
    @Description("Met de votre argent dans la banque de ville")
    void deposit(Player player, @Range(min=1) String input) {
        City city = CityManager.getPlayerCity(player.getUniqueId());

        if (!CityBankConditions.canCityDeposit(city, player)) return;

        city.depositCityBank(player, input);
    }

    @Subcommand("bank withdraw")
    @Description("Prend de l'argent de la banque de ville")
    void withdraw(Player player, @Range(min=1) String input) {
        City city = CityManager.getPlayerCity(player.getUniqueId());

        if (!city.hasPermission(player.getUniqueId(), CPermission.MONEY_TAKE)) {
            MessagesManager.sendMessage(player, MessagesManager.Message.PLAYERNOMONEYTAKE.getMessage(), Prefix.CITY, MessageType.ERROR, false);
            return;
        }

        city.withdrawCityBank(player, input);
    }

    @Subcommand({"mayor", "maire"})
    @CommandPermission("omc.commands.city.mayor")
    @Description("Ouvre le menu des maires")
    void mayor(Player sender) {
        City playerCity = CityManager.getPlayerCity(sender.getUniqueId());

        if (playerCity == null) {
            MessagesManager.sendMessage(sender, MessagesManager.Message.PLAYERNOCITY.getMessage(), Prefix.CITY, MessageType.ERROR, false);
        }

        if (MayorManager.getInstance().phaseMayor == 1) {
            MayorElectionMenu menu = new MayorElectionMenu(sender);
            menu.open();
        } else {
            MayorMandateMenu menu = new MayorMandateMenu(sender);
            menu.open();
        }
    }

    @Subcommand({"warp"})
    @Description("Teleporte au warp commun de la ville")
    void warp(Player player) {
        City playerCity = CityManager.getPlayerCity(player.getUniqueId());

        if (playerCity == null) return;

        CityLaw law = playerCity.getLaw();
        Location warp = law.getWarp();

        if (warp == null) {
            if (MayorManager.getInstance().phaseMayor == 2) {
                MessagesManager.sendMessage(player, Component.text("Le Warp de la Ville n'est pas encore défini ! Demandez au §6Maire §fActuel d'en mettre un ! §8§o*via /city setwarp ou avec le Menu des Lois*"), Prefix.CITY, MessageType.INFO, true);
                return;
            }
            MessagesManager.sendMessage(player, Component.text("Le Warp de la Ville n'est pas encore défini ! Vous devez attendre que un Maire soit élu pour mettre un Warp"), Prefix.CITY, MessageType.INFO, true);
            return;
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                player.sendTitle(PlaceholderAPI.setPlaceholders(player, "§0%img_tp_effect%"), "§a§lTéléportation...", 20, 10, 10);
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        player.teleport(warp);
                        MessagesManager.sendMessage(player, Component.text("Vous avez été envoyé au Warp §fde votre §dVille"), Prefix.CITY, MessageType.SUCCESS, true);
                    }
                }.runTaskLater(OMCPlugin.getInstance(), 10);
            }
        }.runTaskLater(OMCPlugin.getInstance(), 15);
    }

    @Subcommand({"setwarp"})
    @Description("Déplacer le warp de votre ville")
    void setWarpCommand(Player player) {
        MayorSetWarpAction.setWarp(player);
    }
}
