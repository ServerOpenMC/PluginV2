package fr.openmc.core.features.city.sub.war.commands;

import fr.openmc.api.cooldown.DynamicCooldownManager;
import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.CityPermission;
import fr.openmc.core.features.city.CityType;
import fr.openmc.core.features.city.sub.milestone.rewards.FeaturesRewards;
import fr.openmc.core.features.city.sub.war.WarManager;
import fr.openmc.core.features.city.sub.war.WarPendingDefense;
import fr.openmc.core.features.city.sub.war.actions.WarActions;
import fr.openmc.core.features.city.sub.war.menu.main.MainWarMenu;
import fr.openmc.core.utils.text.DateUtils;
import fr.openmc.core.utils.text.messages.MessageType;
import fr.openmc.core.utils.text.messages.MessagesManager;
import fr.openmc.core.utils.text.messages.Prefix;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.CommandPlaceholder;
import revxrsal.commands.annotation.Description;
import revxrsal.commands.annotation.Subcommand;
import revxrsal.commands.bukkit.annotation.CommandPermission;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Command({"guerre", "war"})
@CommandPermission("omc.commands.city.war")
public class WarCommand {
    @CommandPlaceholder()
    void mainCommand(Player player) {
        City playerCity = CityManager.getPlayerCity(player.getUniqueId());
        if (playerCity == null) {
            MessagesManager.sendMessage(player, TranslationManager.translation("messages.city.player_no_in_city"), Prefix.CITY, MessageType.ERROR, false);
            return;
        }

        if (!FeaturesRewards.hasUnlockFeature(playerCity, FeaturesRewards.Feature.WAR)) {
            MessagesManager.sendMessage(player, TranslationManager.translation(
                    "messages.city.havent_unlocked_feature",
                    Component.text(FeaturesRewards.getFeatureUnlockLevel(FeaturesRewards.Feature.WAR)).color(NamedTextColor.RED)
            ), Prefix.CITY, MessageType.ERROR, false);
            return;
        }

        if (!playerCity.getType().equals(CityType.WAR)) {
            MessagesManager.sendMessage(player,
                    TranslationManager.translation("feature.city.war.command.type_required"),
                    Prefix.CITY, MessageType.ERROR, false);
            return;
        }

        if (playerCity.isImmune()) {
            MessagesManager.sendMessage(player,
                    TranslationManager.translation(
                            "feature.city.war.command.city_immune",
                            Component.text(DateUtils.convertMillisToTime(DynamicCooldownManager.getRemaining(playerCity.getUniqueId(), "city:immunity")))
                                    .color(NamedTextColor.GOLD)
                    ),
                    Prefix.CITY, MessageType.ERROR, false);
            return;
        }

        if (!playerCity.hasPermission(player.getUniqueId(), CityPermission.LAUNCH_WAR)) {
            MessagesManager.sendMessage(player,
                    TranslationManager.translation("feature.city.war.command.no_permission_launch"),
                    Prefix.CITY, MessageType.ERROR, false);
            return;
        }

        if (WarManager.getPendingDefenseFor(playerCity) != null) {
            MessagesManager.sendMessage(player,
                    TranslationManager.translation("feature.city.war.command.already_declared"),
                    Prefix.CITY, MessageType.ERROR, false);
            return;
        }

        if (playerCity.isInWar()) {
            MessagesManager.sendMessage(player,
                    TranslationManager.translation("feature.city.war.command.already_in_war"),
                    Prefix.CITY, MessageType.ERROR, false);
            return;
        }

        new MainWarMenu(player).open();
    }

    @Subcommand("acceptdefense")
    @CommandPermission("omc.commands.city.war.acceptdefense")
    @Description("Accepter de participer a une guerre")
    public void acceptDefense(Player player) {
        City city = CityManager.getPlayerCity(player.getUniqueId());
        if (city == null) {
            MessagesManager.sendMessage(player, TranslationManager.translation("messages.city.player_no_in_city"), Prefix.CITY, MessageType.ERROR, false);
            return;
        }

        if (!FeaturesRewards.hasUnlockFeature(city, FeaturesRewards.Feature.WAR)) {
            MessagesManager.sendMessage(player, TranslationManager.translation(
                    "messages.city.havent_unlocked_feature",
                    Component.text(FeaturesRewards.getFeatureUnlockLevel(FeaturesRewards.Feature.WAR)).color(NamedTextColor.RED)
            ), Prefix.CITY, MessageType.ERROR, false);
            return;
        }

        WarPendingDefense pending = WarManager.getPendingDefenseFor(city);
        if (pending == null) {
            MessagesManager.sendMessage(player,
                    TranslationManager.translation("feature.city.war.command.defense.none"),
                    Prefix.CITY, MessageType.ERROR, false);
            return;
        }

        boolean accepted = pending.accept(player.getUniqueId());
        if (!accepted) {
            MessagesManager.sendMessage(player,
                    TranslationManager.translation("feature.city.war.command.defense.full"),
                    Prefix.CITY, MessageType.ERROR, false);
            return;
        }

        MessagesManager.sendMessage(player,
                TranslationManager.translation("feature.city.war.command.defense.accepted"),
                Prefix.CITY, MessageType.ERROR, false);

        if (pending.getAcceptedDefenders().size() >= pending.getRequired() && !pending.isAlreadyExecuted()) {
            pending.setAlreadyExecuted(true);

            City defendingCity = pending.getDefender();
            City attackingCity = pending.getAttacker();
            List<UUID> attackers = pending.getAttackers();

            WarActions.launchWar(attackingCity, defendingCity, attackers,
                    new ArrayList<>(defendingCity.getMembers()), pending.getRequired(), pending);
        }
    }
}
