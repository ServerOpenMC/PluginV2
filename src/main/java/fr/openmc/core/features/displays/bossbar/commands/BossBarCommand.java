package fr.openmc.core.features.displays.bossbar.commands;

import fr.openmc.core.features.displays.bossbar.BossbarManager;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.CommandPlaceholder;

@Command({"omcbossbar", "bb", "bossbaromc"})
public class BossBarCommand {

    @CommandPlaceholder()
    public void mainCommand(Player player) {
        BossbarManager.toggleBossBar(player, "omc:help");
    }
}
