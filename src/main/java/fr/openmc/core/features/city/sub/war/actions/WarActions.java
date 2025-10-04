package fr.openmc.core.features.city.sub.war.actions;

import fr.openmc.api.menulib.defaultmenu.ConfirmMenu;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.CityPermission;
import fr.openmc.core.features.city.CityType;
import fr.openmc.core.features.city.sub.milestone.rewards.FeaturesRewards;
import fr.openmc.core.features.city.sub.war.WarManager;
import fr.openmc.core.features.city.sub.war.WarPendingDefense;
import fr.openmc.core.features.city.sub.war.menu.selection.WarChooseParticipantsMenu;
import fr.openmc.core.features.city.sub.war.menu.selection.WarChooseSizeMenu;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

public class WarActions {

    /**
     * Begins the process of launching a war against another city.
     *
     * @param player     The player initiating the war.
     * @param cityAttack The city that is being attacked.
     */
    public static void beginLaunchWar(Player player, City cityAttack) {
        UUID launcherUUID = player.getUniqueId();
        City launchCity = CityManager.getPlayerCity(launcherUUID);

        if (launchCity == null) {
            MessagesManager.sendMessage(player, MessagesManager.Message.PLAYER_NO_CITY.getMessage(), Prefix.CITY, MessageType.ERROR, false);
            return;
        }

        if (!FeaturesRewards.hasUnlockFeature(launchCity, FeaturesRewards.Feature.WAR)) {
            MessagesManager.sendMessage(player, Component.text("Vous n'avez pas débloqué cette Feature ! Veuillez Améliorer votre Ville au niveau " + FeaturesRewards.getFeatureUnlockLevel(FeaturesRewards.Feature.WAR) + "!"), Prefix.CITY, MessageType.ERROR, false);
            return;
        }

        if (!launchCity.getType().equals(CityType.WAR)) {
            MessagesManager.sendMessage(player,
                    Component.text("Votre ville n'est pas dans un statut de §cgueere §f! Changez la type de votre ville avec §c/city type §fou dans le §cMenu Principal des Villes"),
                    Prefix.CITY, MessageType.ERROR, false);
            return;
        }

        if (!cityAttack.getType().equals(CityType.WAR)) {
            MessagesManager.sendMessage(player,
                    Component.text("La ville que vous essayez d'attaquer n'est pas dans un statut de guerre!"),
                    Prefix.CITY, MessageType.ERROR, false);
            return;
        }

        if (!launchCity.hasPermission(player.getUniqueId(), CityPermission.LAUNCH_WAR)) {
            MessagesManager.sendMessage(player,
                    Component.text("Vous n'avez pas la permission de lancer une guerre pour la ville"),
                    Prefix.CITY, MessageType.ERROR, false);
            return;
        }

        if (WarManager.getPendingDefenseFor(launchCity) != null) {
            MessagesManager.sendMessage(player,
                    Component.text("Vous avez déjà été déclaré en guerre !"),
                    Prefix.CITY, MessageType.ERROR, false);
            return;
        }

        if (launchCity.isInWar()) {
            MessagesManager.sendMessage(player,
                    Component.text("Votre ville est en déjà en guerre!"),
                    Prefix.CITY, MessageType.ERROR, false);
            return;
        }

        if (WarManager.getPendingDefenseFor(cityAttack) != null) {
            MessagesManager.sendMessage(player,
                    Component.text("La ville que vous essayez d'attaquer et déjà en préparation des troupes"),
                    Prefix.CITY, MessageType.ERROR, false);
            return;
        }

        if (cityAttack.isInWar()) {
            MessagesManager.sendMessage(player,
                    Component.text("La ville que vous essayez d'attaquer est déjà en guerre!"),
                    Prefix.CITY, MessageType.ERROR, false);
            return;
        }

        if (cityAttack.isImmune()) {
            MessagesManager.sendMessage(player,
                    Component.text("La ville que vous essayez d'attaquer est en période d'immunité!"),
                    Prefix.CITY, MessageType.ERROR, false);
            return;
        }

        if (launchCity.isImmune()) {
            MessagesManager.sendMessage(player,
                    Component.text("Votre ville est en période d'immunité!"),
                    Prefix.CITY, MessageType.ERROR, false);
            return;
        }

        if (cityAttack.getOnlineMembers().isEmpty()) {
            MessagesManager.sendMessage(player,
                    Component.text("La ville que vous essayez d'attaquer n'a aucun membre de connecté !"),
                    Prefix.CITY, MessageType.ERROR, false);
            return;
        }

        int attackers = launchCity.getOnlineMembers().size();
        int defenders = cityAttack.getOnlineMembers().size();
        int maxSize = Math.min(attackers, defenders);

        if (maxSize < 1) {
            MessagesManager.sendMessage(player,
                    Component.text("Aucun combat possible (pas assez de joueurs connectés)"),
                    Prefix.CITY, MessageType.ERROR, false);
            return;
        }

        new WarChooseSizeMenu(player, launchCity, cityAttack, maxSize).open();
    }

    /**
     * Prepares the war launch by selecting participants.
     *
     * @param player     The player initiating the war.
     * @param cityLaunch The city launching the war.
     * @param cityAttack The city being attacked.
     * @param count      The number of participants for each side.
     */
    public static void preFinishLaunchWar(Player player, City cityLaunch, City cityAttack, int count) {
        List<UUID> available = cityLaunch.getOnlineMembers().stream().toList();

        if (available.size() < count) {
            player.sendMessage("§cPas assez de membres connectés pour lancer un combat en " + count + " vs " + count);
            return;
        }

        new WarChooseParticipantsMenu(player, cityLaunch, cityAttack, count, new HashSet<>()).open();
    }

    /**
     * Confirms the launch of a war between two cities.
     *
     * @param player       The player initiating the war.
     * @param cityLaunch   The city launching the war.
     * @param cityAttack   The city being attacked.
     * @param attackers    The list of UUIDs of players from the launching city who will participate in the war.
     */
    public static void confirmLaunchWar(Player player, City cityLaunch, City cityAttack, List<UUID> attackers) {
        if (cityLaunch.isInWar() || cityAttack.isInWar()) {
            MessagesManager.sendMessage(player,
                    Component.text("Une des villes est déjà en guerre!"),
                    Prefix.CITY, MessageType.ERROR, false);
            return;
        }

        ConfirmMenu menu = new ConfirmMenu(player,
                () -> {
                    finishLaunchWar(player, cityLaunch, cityAttack, attackers);
                    player.closeInventory();
                },
                player::closeInventory,
                List.of(
                        Component.text("§c§lATTENTION"),
                        Component.text("§7Vous êtes sur le point de lancer une guerre contre §c" + cityAttack.getName()),
                        Component.text("§7avec §c" + attackers.size() + " §7joueurs de votre ville.")
                ),
                List.of(
                        Component.text("§7Ne pas lancer une guerre contre §c" + cityAttack.getName())
                )
        );
        menu.open();

    }

    /**
     * Finalizes the war launch by notifying participants and starting the war.
     *
     * @param player       The player initiating the war.
     * @param cityLaunch   The city launching the war.
     * @param cityAttack   The city being attacked.
     * @param attackers    The list of UUIDs of players from the launching city who will participate in the war.
     */
    public static void finishLaunchWar(Player player, City cityLaunch, City cityAttack, List<UUID> attackers) {
        if (cityLaunch.isInWar() || cityAttack.isInWar()) {
            MessagesManager.sendMessage(player,
                    Component.text("Une des villes est déjà en guerre!"),
                    Prefix.CITY, MessageType.ERROR, false);
            return;
        }

        int requiredParticipants = attackers.size();
        Set<UUID> allDefenders = new HashSet<>(cityAttack.getMembers());

        TextComponent info = Component.text("§c⚔ Votre ville est attaquée par §e" + cityLaunch.getName() + "§c, il vous faut §4" + requiredParticipants + " §c joueur(s)!");
        TextComponent clickToJoin = Component.text("§aCliquez ici pour rejoindre la défense !")
                .clickEvent(ClickEvent.runCommand("/war acceptdefense"))
                .hoverEvent(HoverEvent.showText(Component.text("§aCliquez pour participer à la guerre")));

        for (UUID uuid : allDefenders) {
            Player defender = Bukkit.getPlayer(uuid);
            if (defender != null && defender.isOnline()) {
                MessagesManager.sendMessage(defender, info, Prefix.CITY, MessageType.WARNING, false);
                defender.sendMessage(clickToJoin);
            }
        }

        TextComponent infoAttackers = Component.text("§c⚔ Vous avez été choisi pour vous battre contre §e" + cityAttack.getName());

        for (UUID uuid : attackers) {
            Player attacker = Bukkit.getPlayer(uuid);
            if (attacker != null && attacker.isOnline()) {
                MessagesManager.sendMessage(attacker, infoAttackers, Prefix.CITY, MessageType.INFO, false);
            }
        }

        MessagesManager.sendMessage(player, Component.text("§8§oVeuillez attendre que " + cityAttack.getName() + " réagisse, la partie sera tout de même lancée dans 2 min"), Prefix.CITY, MessageType.INFO, false);

        WarPendingDefense pending = new WarPendingDefense(cityLaunch, cityAttack, attackers, requiredParticipants);
        WarManager.addPendingDefense(pending);

        Bukkit.getScheduler().runTaskLater(OMCPlugin.getInstance(), () -> {
            if (pending.isAlreadyExecuted()) return;

            launchWar(cityLaunch, cityAttack, attackers, new ArrayList<>(allDefenders), requiredParticipants, pending);
        }, 20 * 120L); // 2 minutes

    }

    /**
     * Launches the war between two cities with the selected participants.
     *
     * @param cityLaunch        The city launching the war.
     * @param cityAttack        The city being attacked.
     * @param attackers         The list of UUIDs of players from the launching city who will participate in the war.
     * @param allDefenders      The list of UUIDs of all potential defenders from the defending city.
     * @param requiredParticipants The number of defenders required starting the war.
     * @param pending           The pending defense object containing information about the war.
     */
    public static void launchWar(City cityLaunch, City cityAttack, List<UUID> attackers, List<UUID> allDefenders, int requiredParticipants, WarPendingDefense pending) {
        List<UUID> chosenDefenders = new ArrayList<>(pending.getAcceptedDefenders());

        if (chosenDefenders.size() < requiredParticipants) {
            List<UUID> available = allDefenders.stream()
                    .filter(uuid -> !chosenDefenders.contains(uuid))
                    .filter(uuid -> Bukkit.getPlayer(uuid) != null && Bukkit.getPlayer(uuid).isOnline())
                    .collect(Collectors.toList());

            Collections.shuffle(available);

            for (UUID uuid : available) {
                if (chosenDefenders.size() >= requiredParticipants) break;
                chosenDefenders.add(uuid);
            }
        }

        if (chosenDefenders.size() < requiredParticipants) {
            for (UUID uuid : cityLaunch.getMembers()) {
                Player pl = Bukkit.getPlayer(uuid);
                if (pl != null) {
                    MessagesManager.sendMessage(pl,
                            Component.text("La guerre a été annulée car la ville ennemie n'avait pas assez de défenseurs requis."),
                            Prefix.CITY, MessageType.ERROR, false);
                    return;
                }
            }
            return;
        }

        Sound sound = Sound.EVENT_RAID_HORN;

        for (UUID uuid : cityLaunch.getMembers()) {
            Player p = Bukkit.getPlayer(uuid);
            if (p != null && p.isOnline()) {
                p.playSound(p.getLocation(), sound, SoundCategory.MASTER, 1f, 1f);
            }
        }

        for (UUID uuid : cityAttack.getMembers()) {
            Player p = Bukkit.getPlayer(uuid);
            if (p != null && p.isOnline()) {
                p.playSound(p.getLocation(), sound, SoundCategory.MASTER, 1f, 1f);
            }
        }

        WarManager.startWar(cityLaunch, cityAttack, attackers, chosenDefenders);
    }
}