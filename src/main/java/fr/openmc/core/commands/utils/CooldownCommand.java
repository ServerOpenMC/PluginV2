package fr.openmc.core.commands.utils;

import fr.openmc.api.cooldown.DynamicCooldownManager;
import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.utils.text.DateUtils;
import fr.openmc.core.utils.text.messages.MessageType;
import fr.openmc.core.utils.text.messages.MessagesManager;
import fr.openmc.core.utils.text.messages.Prefix;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Description;
import revxrsal.commands.bukkit.annotation.CommandPermission;

public class CooldownCommand {

    @Command("cooldowns")
    @Description("Permet d'avoir la liste des cooldowns")
    @CommandPermission("omc.commands.cooldowns")
    public void cooldowns(Player sender) {
        if (DynamicCooldownManager.getCooldowns(sender.getUniqueId()) == null) {
            MessagesManager.sendMessage(
                    sender,
                    TranslationManager.translation("command.utils.cooldowns.no_cooldown"),
                    Prefix.OPENMC,
                    MessageType.INFO,
                    true
            );
            return;
        }

        MessagesManager.sendMessage(
                sender,
                TranslationManager.translation("command.utils.cooldowns.list_cooldowns"),
                Prefix.OPENMC,
                MessageType.INFO,
                true
        );

        DynamicCooldownManager.getCooldowns(sender.getUniqueId()).forEach(
                (group, cooldown) -> sender.sendMessage(TranslationManager.translation("command.utils.cooldowns.list",
                        Component.text(group),
                        Component.text(DateUtils.convertMillisToTime(cooldown.getRemaining()))
                ))
        );

        City playerCity = CityManager.getCity(sender.getUniqueId());

        if (playerCity != null) {
            DynamicCooldownManager.getCooldowns(playerCity.getUniqueId()).forEach(
                    (group, cooldown) -> sender.sendMessage(TranslationManager.translation("command.utils.cooldowns.list",
                            Component.text(group),
                            Component.text(DateUtils.convertMillisToTime(cooldown.getRemaining()))
                    ))
            );
        }
    }
}
