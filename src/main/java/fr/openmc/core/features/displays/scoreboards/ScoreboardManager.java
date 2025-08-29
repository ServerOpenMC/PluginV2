package fr.openmc.core.features.displays.scoreboards;

import dev.lone.itemsadder.api.FontImages.FontImageWrapper;
import fr.openmc.api.hooks.ItemsAdderHook;
import fr.openmc.api.hooks.LuckPermsHook;
import fr.openmc.api.hooks.PapiHook;
import fr.openmc.api.hooks.WorldGuardHook;
import fr.openmc.core.CommandsManager;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.commands.utils.Restart;
import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.sub.war.War;
import fr.openmc.core.features.city.sub.war.WarManager;
import fr.openmc.core.features.contest.managers.ContestManager;
import fr.openmc.core.features.contest.models.Contest;
import fr.openmc.core.features.economy.EconomyManager;
import fr.openmc.core.utils.DateUtils;
import fr.openmc.core.utils.DirectionUtils;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.scoreboard.Criteria;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.time.DayOfWeek;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class ScoreboardManager implements Listener {
    @Getter
    static ScoreboardManager instance;

    public static final Set<UUID> disabledPlayers = new HashSet<>();
    public static final HashMap<UUID, Scoreboard> playerScoreboards = new HashMap<>();
    private static final boolean canShowLogo = PapiHook.hasPAPI() && ItemsAdderHook.hasItemAdder();
    final OMCPlugin plugin = OMCPlugin.getInstance();
    private static GlobalTeamManager globalTeamManager = null;

    public ScoreboardManager() {
        instance = this;

        OMCPlugin.registerEvents(
                new ScoreboardListener()
        );
        CommandsManager.getHandler().register(
                new ScoreboardCommand()
        );

        Bukkit.getScheduler().runTaskTimer(plugin, ScoreboardManager::updateAllScoreboards, 0L, 20L * 5); //20x5 = 5s

        Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (disabledPlayers.contains(player.getUniqueId())) continue;

                City city = CityManager.getPlayerCity(player.getUniqueId());
                if (city == null || !city.isInWar()) continue;

                updateScoreboard(player);
            }
        }, 0L, 20L); // 1s
        if (LuckPermsHook.hasLuckPerms()) globalTeamManager = new GlobalTeamManager(playerScoreboards);
    }

    public static Scoreboard createNewScoreboard(Player player) {
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        Objective objective;
        if (canShowLogo) {
            objective = scoreboard.registerNewObjective("sb_aywen", Criteria.DUMMY, Component.text(FontImageWrapper.replaceFontImages(":openmc:")));
        } else {
            objective = scoreboard.registerNewObjective("sb_aywen", Criteria.DUMMY, Component.text("OPENMC").decorate(TextDecoration.BOLD).color(NamedTextColor.LIGHT_PURPLE));
        }
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        updateScoreboard(player, scoreboard, objective);
        return scoreboard;
    }

    public static void updateAllScoreboards() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (disabledPlayers.contains(player.getUniqueId())) continue;

            updateScoreboard(player);
        }
    }

    public static void updateScoreboard(Player player) {
        playerScoreboards.computeIfAbsent(player.getUniqueId(), (uuid) -> {
            Scoreboard sb = createNewScoreboard(player);
            player.setScoreboard(sb);
            return sb;
        });

        Scoreboard scoreboard = playerScoreboards.get(player.getUniqueId());
        if (scoreboard == null) return;

        Objective objective = scoreboard.getObjective("sb_aywen");
        if (objective == null) return;

        updateScoreboard(player, scoreboard, objective);
    }

    private static void updateScoreboard(Player player, Scoreboard scoreboard, Objective objective) {
        /*
         * 07 |
         * 06 | Username
         * 05 | City name
         * 04 | Argent
         * 03 |
         * 02 | Nom territoire
         * 01 |
         * 00 | ip
         */

        for (String entry : scoreboard.getEntries()) {
            scoreboard.resetScores(entry);
        }

        // RESTART SCOREBOARD
        if (Restart.isRestarting) {
            objective.getScore("§7").setScore(3);
            objective.getScore("   ").setScore(2);
            objective.getScore("§cRedémarrage dans " + DateUtils.convertSecondToTime(Restart.remainingTime)).setScore(2);
            objective.getScore("   ").setScore(1);
            objective.getScore("§d      ᴘʟᴀʏ.ᴏᴘᴇɴᴍᴄ.ꜰʀ").setScore(0);
            return;
        }

        // WAR SCOREBOARD
        if (player.getWorld().getName().equalsIgnoreCase("world")) {
            City city = CityManager.getPlayerCity(player.getUniqueId());
            if (city != null && city.isInWar()) {
                War war = city.getWar();

                objective.getScore("§7").setScore(14);

                objective.getScore("§8• §fNom: §7" + player.getName()).setScore(13);

                String cityName = city.getName();
                objective.getScore("§8• §fVille§7: " + cityName).setScore(12);

                City chunkCity = CityManager.getCityFromChunk(player.getChunk().getX(), player.getChunk().getZ());
                String chunkCityName = (chunkCity != null) ? chunkCity.getName() : "Nature";
                objective.getScore("§8• §fLocation§7: " + chunkCityName).setScore(11);

                objective.getScore("  ").setScore(10);
                objective.getScore("§c§l⚔ GUERRE EN COURS ⚔").setScore(9);

                City cityEnemy = war.getCityAttacker().equals(city) ?
                        war.getCityDefender() : war.getCityAttacker();
                String ennemyName = cityEnemy.getName();
                objective.getScore("§8• §cEnnemi§7: " + ennemyName).setScore(8);

                War.WarPhase phase = war.getPhase();
                objective.getScore("§8• §6Phase§7: " + WarManager.getFormattedPhase(phase)).setScore(7);

                Chunk chunk = cityEnemy.getMascot().getChunk();
                World world = chunk.getWorld();
                int x = (chunk.getX() << 4) + 8;
                int z = (chunk.getZ() << 4) + 8;
                int y = world.getHighestBlockYAt(x, z);

                LivingEntity mobMascotEnemy = (LivingEntity) cityEnemy.getMascot().getEntity();
                LivingEntity mobMascot = (LivingEntity) city.getMascot().getEntity();

                Location mascotLocation = mobMascotEnemy != null ? mobMascotEnemy.getLocation() : new Location(world, x, y, z);
                String direction = DirectionUtils.getDirectionArrow(player, mascotLocation);
                double distance = mascotLocation.distance(player.getLocation());
                int rounded = (int) Math.round(distance);
                objective.getScore("§8• §cMascotte: " + direction + " (" + rounded + "m)").setScore(6);

                switch (war.getPhase()) {
                    case PREPARATION:
                        objective.getScore("   ").setScore(5);
                        int secondsPreparationRemaining = war.getPreparationTimeRemaining();
                        String timePreparationFormatted = DateUtils.convertSecondToTime(secondsPreparationRemaining);
                        objective.getScore("§8• §eDébut dans§7: " + timePreparationFormatted).setScore(4);
                        break;
                    case COMBAT:
                        objective.getScore("   ").setScore(5);
                        if (mobMascot != null) {
                            if (city.getMascot().isAlive()) {
                                objective.getScore("§8• §fVotre Mascotte§7: §c" + Math.floor(mobMascot.getHealth()) + "§4/§c" + mobMascot.getAttribute(Attribute.MAX_HEALTH).getValue() + " ❤").setScore(4);
                            } else {
                                objective.getScore("§8• §fVotre Mascotte§7: §4☠ MORT").setScore(4);
                            }
                        }

                        if (mobMascotEnemy != null) {
                            if (cityEnemy.getMascot().isAlive()) {
                                objective.getScore("§8• §4Mascotte Enemnie§7: §c" + Math.floor(mobMascotEnemy.getHealth()) + "§4/§c" + mobMascotEnemy.getAttribute(Attribute.MAX_HEALTH).getValue() + " ❤").setScore(3);
                            } else {
                                objective.getScore("§8• §4Mascotte Enemnie§7: §4☠ MORT").setScore(3);
                            }
                        }

                        int secondsCombatRemaining = war.getCombatTimeRemaining();
                        String timeCombatFormatted = DateUtils.convertSecondToTime(secondsCombatRemaining);
                        objective.getScore("§8• §eFin dans§7: " + timeCombatFormatted).setScore(2);
                        break;
                    case ENDED:
                        break;
                }

                objective.getScore("   ").setScore(1);
                objective.getScore("§d      ᴘʟᴀʏ.ᴏᴘᴇɴᴍᴄ.ꜰʀ").setScore(0);

                return;
            }
        }

        // GENERAL SCOREBOARD

        objective.getScore("§7").setScore(19);

        objective.getScore("§8• §fNom: §7" + player.getName()).setScore(18);

        if (player.getWorld().getName().equalsIgnoreCase("world")) {
            City city = CityManager.getPlayerCity(player.getUniqueId());
            String cityName = city != null ? city.getName() : "Aucune";
            objective.getScore("§8• §fVille§7: " + cityName).setScore(17);


            objective.getScore("  ").setScore(7);

            City chunkCity = CityManager.getCityFromChunk(player.getChunk().getX(), player.getChunk().getZ());
            boolean isInRegion = WorldGuardHook.isRegionConflict(player.getLocation());
            String location = isInRegion ? "§6Région Protégée" : "Nature";
            location = (chunkCity != null) ? chunkCity.getName() : location;
            objective.getScore("§8• §fLocation§7: " + location).setScore(6);
        }

        String balance = EconomyManager.getMiniBalance(player.getUniqueId());
        objective.getScore("§8• §r"+EconomyManager.getEconomyIcon()+" §d"+balance).setScore(8);


        Contest data = ContestManager.data;
        int phase = data.getPhase();
        if(phase != 1) {
            objective.getScore(" ").setScore(5);
            objective.getScore("§8• §6§lCONTEST!").setScore(4);
            objective.getScore(ChatColor.valueOf(data.getColor1()) + data.getCamp1() + " §8VS " + ChatColor.valueOf(data.getColor2()) + data.getCamp2()).setScore(3);
            objective.getScore("§cFin dans " + DateUtils.getTimeUntilNextDay(DayOfWeek.MONDAY)).setScore(2);
        }

        objective.getScore("   ").setScore(1);
        objective.getScore("§d      ᴘʟᴀʏ.ᴏᴘᴇɴᴍᴄ.ꜰʀ").setScore(0);

        if (LuckPermsHook.hasLuckPerms() && globalTeamManager != null) globalTeamManager.updatePlayerTeam(player);
    }
}
