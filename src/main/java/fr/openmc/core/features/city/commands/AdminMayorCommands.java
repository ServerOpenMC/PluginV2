package fr.openmc.core.features.city.commands;

import fr.openmc.core.features.city.mayor.managers.MayorManager;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.*;
import revxrsal.commands.bukkit.annotation.CommandPermission;

import java.sql.SQLException;

@Command({"adminmayor"})
@CommandPermission("omc.admins.commands.adminmayor")
public class AdminMayorCommands {
    @Subcommand({"setphase"})
    @CommandPermission("omc.admins.commands.adminmayor")
    public void setPhase(Player sender, int phase) throws SQLException {
        MayorManager mayorManager = MayorManager.getInstance();
        if (phase == 1) {
            mayorManager.initPhase1();
        } else if (phase == 2){
            mayorManager.initPhase2();
        }
    }
}