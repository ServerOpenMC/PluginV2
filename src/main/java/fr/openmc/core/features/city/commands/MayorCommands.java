package fr.openmc.core.features.city.commands;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.city.CPermission;
import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.CityMessages;
import fr.openmc.core.features.city.conditions.*;
import fr.openmc.core.features.city.listeners.CityTypeCooldown;
import fr.openmc.core.features.city.mascots.MascotUtils;
import fr.openmc.core.features.city.mascots.MascotsLevels;
import fr.openmc.core.features.city.mascots.MascotsManager;
import fr.openmc.core.features.city.mayor.ElectionType;
import fr.openmc.core.features.city.mayor.managers.MayorManager;
import fr.openmc.core.features.city.menu.CityMenu;
import fr.openmc.core.features.city.menu.CityTypeMenu;
import fr.openmc.core.features.city.menu.NoCityMenu;
import fr.openmc.core.features.city.menu.mayor.MayorElectionMenu;
import fr.openmc.core.features.city.menu.mayor.MayorMandateMenu;
import fr.openmc.core.features.economy.EconomyManager;
import fr.openmc.core.utils.BlockVector2;
import fr.openmc.core.utils.InputUtils;
import fr.openmc.core.utils.chronometer.Chronometer;
import fr.openmc.core.utils.chronometer.ChronometerType;
import fr.openmc.core.utils.cooldown.DynamicCooldown;
import fr.openmc.core.utils.cooldown.DynamicCooldownManager;
import fr.openmc.core.utils.database.DatabaseManager;
import fr.openmc.core.utils.menu.ConfirmMenu;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.*;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;
import revxrsal.commands.annotation.*;
import revxrsal.commands.bukkit.annotation.CommandPermission;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

import static fr.openmc.core.features.city.CityManager.getCityType;

@Command({"adminmayor"})
@CommandPermission("omc.admins.commands.adminmayor")
public class MayorCommands {
    @Subcommand({"setphase"})
    @CommandPermission("omc.admins.commands.adminmayor")
    public void setPhase(Player sender, int phase) throws SQLException {
        MayorManager mayorManager = MayorManager.getInstance();
        if (phase ==1) {
            mayorManager.initPhase1();
        } else if (phase == 2){
            mayorManager.initPhase2();
        }
    }
}