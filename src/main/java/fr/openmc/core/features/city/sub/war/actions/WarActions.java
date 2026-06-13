package fr.openmc.core.features.city.sub.war.actions;

import fr.openmc.api.menulib.template.ConfirmMenu;
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
import fr.openmc.core.utils.text.messages.MessageType;
import fr.openmc.core.utils.text.messages.MessagesManager;
import fr.openmc.core.utils.text.messages.Prefix;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
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
            MessagesManager.sendMessage(player, TranslationManager.translation("messages.city.player_no_in_city"), Prefix.CITY, MessageType.ERROR, false);
            return;
        }

        if (!FeaturesRewards.hasUnlockFeature(launchCity, FeaturesRewards.Feature.WAR)) {
            MessagesManager.sendMessage(player, TranslationManager.translation(
                    "messages.city.havent_unlocked_feature",
                    Component.text(FeaturesRewards.getFeatureUnlockLevel(FeaturesRewards.Feature.WAR))
                            .color(NamedTextColor.RED)
            ), Prefix.CITY, MessageType.ERROR, false);
            return;
        }

        if (!launchCity.getType().equals(CityType.WAR)) {
            MessagesManager.sendMessage(player,
                    TranslationManager.translation("feature.city.war.begin.type_required"),
                    Prefix.CITY, MessageType.ERROR, false);
            return;
        }

        if (!cityAttack.getType().equals(CityType.WAR)) {
            MessagesManager.sendMessage(player,
                    TranslationManager.translation("feature.city.war.begin.target_not_war"),
                    Prefix.CITY, MessageType.ERROR, false);
            return;
        }

        if (!launchCity.hasPermission(player.getUniqueId(), CityPermission.LAUNCH_WAR)) {
            MessagesManager.sendMessage(player,
                    TranslationManager.translation("feature.city.war.begin.no_permission_launch"),
                    Prefix.CITY, MessageType.ERROR, false);
            return;
        }

        if (WarManager.getPendingDefenseFor(launchCity) != null) {
            MessagesManager.sendMessage(player,
                    TranslationManager.translation("feature.city.war.begin.already_declared"),
                    Prefix.CITY, MessageType.ERROR, false);
            return;
        }

        if (launchCity.isInWar()) {
            MessagesManager.sendMessage(player,
                    TranslationManager.translation("feature.city.war.begin.already_in_war"),
                    Prefix.CITY, MessageType.ERROR, false);
            return;
        }

        if (WarManager.getPendingDefenseFor(cityAttack) != null) {
            MessagesManager.sendMessage(player,
                    TranslationManager.translation("feature.city.war.begin.target_preparing"),
                    Prefix.CITY, MessageType.ERROR, false);
            return;
        }

        if (cityAttack.isInWar()) {
            MessagesManager.sendMessage(player,
                    TranslationManager.translation("feature.city.war.begin.target_in_war"),
                    Prefix.CITY, MessageType.ERROR, false);
            return;
        }

        if (cityAttack.isImmune()) {
            MessagesManager.sendMessage(player,
                    TranslationManager.translation("feature.city.war.begin.target_immune"),
                    Prefix.CITY, MessageType.ERROR, false);
            return;
        }

        if (launchCity.isImmune()) {
            MessagesManager.sendMessage(player,
                    TranslationManager.translation("feature.city.war.begin.city_immune"),
                    Prefix.CITY, MessageType.ERROR, false);
            return;
        }

        if (cityAttack.getOnlineMembers().isEmpty()) {
            MessagesManager.sendMessage(player,
                    TranslationManager.translation("feature.city.war.begin.target_no_online"),
                    Prefix.CITY, MessageType.ERROR, false);
            return;
        }

        int attackers = launchCity.getOnlineMembers().size();
        int defenders = cityAttack.getOnlineMembers().size();
        int maxSize = Math.min(attackers, defenders);

        if (maxSize < 1) {
            MessagesManager.sendMessage(player,
                    TranslationManager.translation("feature.city.war.begin.no_combat"),
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
            MessagesManager.sendMessage(player, TranslationManager.translation(
                    "feature.city.war.begin.not_enough_members",
                    Component.text(count).color(NamedTextColor.RED)
            ), Prefix.CITY, MessageType.ERROR, false);
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
                    TranslationManager.translation("feature.city.war.begin.city_already_in_war"),
                    Prefix.CITY, MessageType.ERROR, false);
            return;
        }

        ConfirmMenu menu = new ConfirmMenu(player,
                () -> {
                    finishLaunchWar(player, cityLaunch, cityAttack, attackers);
                    player.closeInventory();
                },
                player::closeInventory,
                TranslationManager.translationLore(
                        "feature.city.war.begin.confirm.lore",
                        Component.text(cityAttack.getName()).color(NamedTextColor.RED),
                        Component.text(attackers.size()).color(NamedTextColor.RED)
                ),
                TranslationManager.translationLore(
                        "feature.city.war.begin.confirm.cancel",
                        Component.text(cityAttack.getName()).color(NamedTextColor.RED)
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
                    TranslationManager.translation("feature.city.war.begin.city_already_in_war"),
                    Prefix.CITY, MessageType.ERROR, false);
            return;
        }

        int requiredParticipants = attackers.size();
        Set<UUID> allDefenders = new HashSet<>(cityAttack.getMembers());

        Component info = TranslationManager.translation(
                "feature.city.war.begin.defense.attacked",
                Component.text(cityLaunch.getName()).color(NamedTextColor.YELLOW),
                Component.text(requiredParticipants).color(NamedTextColor.RED)
        );
        Component clickToJoin = TranslationManager.translation("feature.city.war.begin.defense.click_to_join")
                .clickEvent(ClickEvent.runCommand("/war acceptdefense"))
                .hoverEvent(HoverEvent.showText(TranslationManager.translation("feature.city.war.begin.defense.hover_join")));

        for (UUID uuid : allDefenders) {
            Player defender = Bukkit.getPlayer(uuid);
            if (defender != null && defender.isOnline()) {
                MessagesManager.sendMessage(defender, info, Prefix.CITY, MessageType.WARNING, false);
                defender.sendMessage(clickToJoin);
            }
        }

        Component infoAttackers = TranslationManager.translation(
                "feature.city.war.begin.attacker.chosen",
                Component.text(cityAttack.getName()).color(NamedTextColor.YELLOW)
        );

        for (UUID uuid : attackers) {
            Player attacker = Bukkit.getPlayer(uuid);
            if (attacker != null && attacker.isOnline()) {
                MessagesManager.sendMessage(attacker, infoAttackers, Prefix.CITY, MessageType.INFO, false);
            }
        }

        MessagesManager.sendMessage(player, TranslationManager.translation(
                "feature.city.war.begin.waiting_defense",
                Component.text(cityAttack.getName()).color(NamedTextColor.YELLOW)
        ), Prefix.CITY, MessageType.INFO, false);

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