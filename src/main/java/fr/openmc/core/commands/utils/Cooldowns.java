package fr.openmc.core.commands.utils;

import fr.openmc.api.cooldown.DynamicCooldownManager;
import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.utils.DateUtils;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Description;
import revxrsal.commands.bukkit.annotation.CommandPermission;

public class Cooldowns {

    @Command("cooldowns")
    @Description("Permet d'avoir la liste des cooldowns")
    @CommandPermission("omc.commands.cooldowns")
    public void cooldowns(Player sender) {
        if (DynamicCooldownManager.getCooldowns(sender.getUniqueId()) == null) {
            MessagesManager.sendMessage(
                    sender,
                    Component.text("§cAucun cooldown actif"),
                    Prefix.OPENMC,
                    MessageType.INFO,
                    true
            );
            return;
        }

        MessagesManager.sendMessage(
                sender,
                Component.text("Liste des cooldowns actifs :"),
                Prefix.OPENMC,
                MessageType.INFO,
                true
        );

        DynamicCooldownManager.getCooldowns(sender.getUniqueId()).forEach(
                (group, cooldown) -> {
                    sender.sendMessage(
                            Component.text("§a- " + group + " : " + DateUtils.convertMillisToTime(cooldown.getRemaining()))
                    );
                }
        );

        City playerCity = CityManager.getCity(sender.getUniqueId());

        if (playerCity != null) {
            DynamicCooldownManager.getCooldowns(playerCity.getUniqueId()).forEach(
                    (group, cooldown) -> {
                        sender.sendMessage(
                                Component.text("§a- " + group + " : " + DateUtils.convertMillisToTime(cooldown.getRemaining()))
                        );
                    }
            );
        }
    }
}
