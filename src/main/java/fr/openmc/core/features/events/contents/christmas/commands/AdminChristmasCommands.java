package fr.openmc.core.features.events.contents.christmas.commands;

import fr.openmc.core.OMCRegistry;
import fr.openmc.core.features.events.contents.deposit.DepositEventManager;
import fr.openmc.core.utils.text.DateUtils;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Subcommand;
import revxrsal.commands.bukkit.annotation.CommandPermission;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;

@Command("christmas admin")
public class AdminChristmasCommands {
    @Subcommand("start")
    @CommandPermission("omc.admins.commands.christmas.admin.start")
    void start(Player player) {
        LocalDateTime now = DateUtils.getLocalDateTime();
        LocalDateTime newYear = LocalDate.of(now.getYear() + 1, Month.JANUARY, 1).atStartOfDay();

        Duration duration = Duration.between(now, newYear);

        DepositEventManager.start(OMCRegistry.DEPOSIT_EVENTS.CHRISTMAS, duration);
    }

    @Subcommand("force-end")
    @CommandPermission("omc.admins.commands.christmas.admin.force-end")
    void end(Player player) {
        DepositEventManager.end(OMCRegistry.DEPOSIT_EVENTS.CHRISTMAS);
    }
}
