package fr.openmc.core.features.scoreboards;

import fr.openmc.core.CommandsManager;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.commands.utils.Restart;
import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.sub.mascots.MascotUtils;
import fr.openmc.core.features.city.sub.war.War;
import fr.openmc.core.features.city.sub.war.WarManager;
import fr.openmc.core.features.contest.ContestData;
import fr.openmc.core.features.contest.managers.ContestManager;
import fr.openmc.core.features.corporation.company.Company;
import fr.openmc.core.features.corporation.manager.CompanyManager;
import fr.openmc.core.features.economy.EconomyManager;
import fr.openmc.core.utils.DateUtils;
import fr.openmc.core.utils.DirectionUtils;
import fr.openmc.core.utils.api.ItemAdderApi;
import fr.openmc.core.utils.api.LuckPermsApi;
import fr.openmc.core.utils.api.PapiApi;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import lombok.Getter;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.*;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Description;
import revxrsal.commands.bukkit.annotation.CommandPermission;

import java.time.DayOfWeek;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class ScoreboardManager implements Listener {
    @Getter
    static ScoreboardManager instance;

    public Set<UUID> disabledPlayers = new HashSet<>();
    public HashMap<UUID, Scoreboard> playerScoreboards = new HashMap<>();
    private final boolean canShowLogo = PapiApi.hasPAPI() && ItemAdderApi.hasItemAdder();
    OMCPlugin plugin = OMCPlugin.getInstance();
    private GlobalTeamManager globalTeamManager = null;

    public ScoreboardManager() {
        instance = this;

        OMCPlugin.registerEvents(this);
        CommandsManager.getHandler().register(this);

        Bukkit.getScheduler().runTaskTimer(plugin, this::updateAllScoreboards, 0L, 20L * 5); //20x5 = 5s

        Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (disabledPlayers.contains(player.getUniqueId())) continue;

                City city = CityManager.getPlayerCity(player.getUniqueId());
                if (city == null || !city.isInWar()) continue;

                updateScoreboard(player);
            }
        }, 0L, 20L); // 1s
        if (LuckPermsApi.hasLuckPerms()) globalTeamManager = new GlobalTeamManager(playerScoreboards);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (disabledPlayers.contains(player.getUniqueId())) return;

        Scoreboard sb = createNewScoreboard(player);
        player.setScoreboard(sb);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        playerScoreboards.remove(player.getUniqueId());
    }

    @Command("sb")
    @CommandPermission("omc.commands.scoreboard")
    @Description("Active / désactive le scoreboard")
    public void onScoreboardCommand(Player player) {
        UUID uuid = player.getUniqueId();
        if (disabledPlayers.contains(uuid)) {
            disabledPlayers.remove(uuid);

            playerScoreboards.remove(uuid);
            player.setScoreboard(createNewScoreboard(player));
            updateScoreboard(player);

            MessagesManager.sendMessage(player, Component.text("Scoreboard activé").color(NamedTextColor.GREEN), Prefix.OPENMC, MessageType.INFO, true);
        } else {
            disabledPlayers.add(uuid);
            player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
            MessagesManager.sendMessage(player, Component.text("Scoreboard désactivé").color(NamedTextColor.RED), Prefix.OPENMC, MessageType.INFO, true);
        }
    }

    private Scoreboard createNewScoreboard(Player player) {
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        Objective objective;
        if (canShowLogo) {
            objective = scoreboard.registerNewObjective("sb_aywen", "dummy", PlaceholderAPI.setPlaceholders(player, "%img_openmc%"));
        } else {
            objective = scoreboard.registerNewObjective("sb_aywen", "dummy", Component.text("OPENMC").decorate(TextDecoration.BOLD).color(NamedTextColor.LIGHT_PURPLE));
        }
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        updateScoreboard(player, scoreboard, objective);
        return scoreboard;
    }

    public void updateAllScoreboards() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (disabledPlayers.contains(player.getUniqueId())) continue;

            updateScoreboard(player);
        }
    }

    private void updateScoreboard(Player player) {
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

    private void updateScoreboard(Player player, Scoreboard scoreboard, Objective objective) {
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
            War war = city != null ? city.getWar() : null;
            if (war != null && city.isInWar()) {

                objective.getScore("§7").setScore(14);

                objective.getScore("§8• §fNom: §7" + player.getName()).setScore(13);

                String cityName = city != null ? city.getName() : "Aucune";
                objective.getScore("§8• §fVille§7: " + cityName).setScore(12);

                objective.getScore("  ").setScore(11);
                objective.getScore("§c§l⚔ GUERRE EN COURS ⚔").setScore(10);

                City cityEnemy = war.getCityAttacker().equals(city) ?
                        war.getCityDefender() : war.getCityAttacker();
                String ennemyName = cityEnemy.getName();
                objective.getScore("§8• §cEnnemi§7: " + ennemyName).setScore(9);

                War.WarPhase phase = war.getPhase();
                objective.getScore("§8• §6Phase§7: " + WarManager.getFormattedPhase(phase)).setScore(8);

                Chunk chunk = cityEnemy.getMascot().getChunk();
                World world = chunk.getWorld();
                int x = (chunk.getX() << 4) + 8;
                int z = (chunk.getZ() << 4) + 8;
                int y = world.getHighestBlockYAt(x, z);
                Location centerChunkLocation = new Location(world, x, y, z);
                String direction = DirectionUtils.getDirectionArrow(player, centerChunkLocation);
                double distance = centerChunkLocation.distance(player.getLocation());
                int rounded = (int) Math.round(distance);
                objective.getScore("§8• §cMascotte: " + direction + " (" + rounded + "m)").setScore(7);

                switch (war.getPhase()) {
                    case PREPARATION:
                        int secondsPreparationRemaining = war.getPreparationTimeRemaining();
                        String timePreparationFormatted = DateUtils.convertSecondToTime(secondsPreparationRemaining);
                        objective.getScore("§8• §eDébut dans§7: " + timePreparationFormatted).setScore(6);
                        break;
                    case COMBAT:
                        LivingEntity mobMascot = MascotUtils.loadMascot(city.getMascot());
                        if (mobMascot != null) {
                            objective.getScore("§8• §6Votre Mascotte§7: §c" + mobMascot.getHealth() + "§4/§c" + mobMascot.getMaxHealth()).setScore(5);
                        }

                        LivingEntity mobMascotEnemy = MascotUtils.loadMascot(cityEnemy.getMascot());
                        if (mobMascotEnemy != null) {
                            objective.getScore("§8• §4Mascotte Enemnie§7: §c" + mobMascotEnemy.getHealth() + "§4/§c" + mobMascotEnemy.getMaxHealth()).setScore(4);
                        }

                        int secondsCombatRemaining = war.getCombatTimeRemaining();
                        String timeCombatFormatted = DateUtils.convertSecondToTime(secondsCombatRemaining);
                        objective.getScore("§8• §eFin dans§7: " + timeCombatFormatted).setScore(3);
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
            String chunkCityName = (chunkCity != null) ? chunkCity.getName() : "Nature";
            objective.getScore("§8• §fLocation§7: " + chunkCityName).setScore(6);
        }

        if (CompanyManager.getInstance().isInCompany(player.getUniqueId())){
            Company company = CompanyManager.getCompany(player.getUniqueId());
            String compName = company != null ? company.getName() : "Introuvable";
            objective.getScore("§8• §fEntreprise§7: "+compName).setScore(9);
        }

        String balance = EconomyManager.getInstance().getMiniBalance(player.getUniqueId());
        objective.getScore("§8• §r"+EconomyManager.getEconomyIcon()+" §d"+balance).setScore(8);


        ContestData data = ContestManager.getInstance().data;
        int phase = data.getPhase();
        if(phase != 1) {
            objective.getScore(" ").setScore(5);
            objective.getScore("§8• §6§lCONTEST!").setScore(4);
            objective.getScore(ChatColor.valueOf(data.getColor1()) + data.getCamp1() + " §8VS " + ChatColor.valueOf(data.getColor2()) + data.getCamp2()).setScore(3);
            objective.getScore("§cFin dans " + DateUtils.getTimeUntilNextDay(DayOfWeek.MONDAY)).setScore(2);
        }

        objective.getScore("   ").setScore(1);
        objective.getScore("§d      ᴘʟᴀʏ.ᴏᴘᴇɴᴍᴄ.ꜰʀ").setScore(0);

        if (LuckPermsApi.hasLuckPerms() && globalTeamManager != null) globalTeamManager.updatePlayerTeam(player);
    }
}
