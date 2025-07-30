package fr.openmc.core.features.homes;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import fr.openmc.core.CommandsManager;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.homes.command.*;
import fr.openmc.core.features.homes.models.Home;
import fr.openmc.core.features.homes.models.HomeLimit;
import fr.openmc.core.features.homes.world.DisabledWorldHome;
import fr.openmc.core.utils.database.DatabaseManager;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.generator.WorldInfo;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
public class HomesManager {

    public static final List<Home> homes = new ArrayList<>();
    public static final List<HomeLimit> homeLimits = new ArrayList<>();
    public final DisabledWorldHome disabledWorldHome;

    public HomesManager() {
        disabledWorldHome = new DisabledWorldHome(OMCPlugin.getInstance());

        CommandsManager.getHandler().getAutoCompleter().registerSuggestion("homes",
                (args, sender, command) -> {
                    Player player = Bukkit.getPlayer(sender.getUniqueId());
                    List<String> suggestions = new ArrayList<>();
                    if (player == null)
                        return suggestions;

                    if (args.isEmpty()) {
                        if (player.hasPermission("omc.admin.homes.teleport.others")) {
                            suggestions.addAll(
                                    Bukkit.getOnlinePlayers().stream()
                                            .map(OfflinePlayer::getName)
                                            .map(name -> name + ":")
                                            .toList());
                            if (command.getName().equalsIgnoreCase("home") ||
                                    command.getName().equalsIgnoreCase("delhome") ||
                                    command.getName().equalsIgnoreCase("relocatehome") ||
                                    command.getName().equalsIgnoreCase("renamehome")) {
                                suggestions.addAll(getHomes(player.getUniqueId()).stream()
                                        .map(Home::getName)
                                        .toList());
                            }
                        }
                    } else {
                        String arg = args.getFirst();

                        if (arg.contains(":") && player.hasPermission("omc.admin.homes.teleport.others")) {
                            if (command.getName().equalsIgnoreCase("home") ||
                                    command.getName().equalsIgnoreCase("delhome") ||
                                    command.getName().equalsIgnoreCase("relocatehome") ||
                                    command.getName().equalsIgnoreCase("renamehome")) {
                                String[] split = arg.split(":", 2);
                                OfflinePlayer target = Bukkit.getOfflinePlayer(split[0]);

                                if (target.hasPlayedBefore()) {
                                    String prefix = split[0] + ":";
                                    suggestions.addAll(getHomesNames(target.getUniqueId())
                                            .stream()
                                            .map(home -> prefix + home)
                                            .toList());
                                }
                            }
                        } else {
                            if (player.hasPermission("omc.admin.homes.teleport.others")) {
                                suggestions.addAll(Bukkit.getOnlinePlayers().stream()
                                        .map(OfflinePlayer::getName)
                                        .filter(name -> name.toLowerCase().startsWith(arg.toLowerCase()))
                                        .map(name -> name + ":")
                                        .toList());
                            }

                            if (command.getName().equalsIgnoreCase("home") ||
                                    command.getName().equalsIgnoreCase("delhome") ||
                                    command.getName().equalsIgnoreCase("relocatehome") ||
                                    command.getName().equalsIgnoreCase("renamehome")) {
                                suggestions.addAll(getHomes(player.getUniqueId()).stream()
                                        .map(Home::getName)
                                        .filter(name -> name.toLowerCase().startsWith(arg.toLowerCase()))
                                        .toList());
                            }
                        }

                        return suggestions;
                    }

                    if (command.getName().equalsIgnoreCase("home") ||
                            command.getName().equalsIgnoreCase("delhome") ||
                            command.getName().equalsIgnoreCase("relocatehome") ||
                            command.getName().equalsIgnoreCase("renamehome")) {
                        suggestions.addAll(getHomesNames(player.getUniqueId()));
                    }
                    return suggestions;
                });

        CommandsManager.getHandler().getAutoCompleter().registerSuggestion("homeWorldsAdd",
                (args, sender, command) -> {
                    List<String> suggestions = new ArrayList<>(
                            Bukkit.getWorlds().stream().map(WorldInfo::getName).toList());
                    suggestions.removeAll(disabledWorldHome.getDisabledWorlds());
                    return suggestions;
                });

        CommandsManager.getHandler().getAutoCompleter().registerSuggestion("homeWorldsRemove",
                (args, sender, command) -> new ArrayList<>(disabledWorldHome.getDisabledWorlds()));

        CommandsManager.getHandler().register(
                new SetHome(this),
                new RenameHome(),
                new DelHome(),
                new RelocateHome(this),
                new TpHome(),
                new HomeWorld(disabledWorldHome),
                new UpgradeHome()
        );

        loadHomeLimit();
        loadHomes();
    }

    public static void saveHomesData() {
        saveHomes();
        saveHomeLimit();
    }

    public static void addHome(Home home) {
        homes.add(home);
    }

    public static void removeHome(Home home) {
        homes.remove(home);
    }

    public static void renameHome(Home home, String newName) {
        home.setName(newName);
    }

    public static void relocateHome(Home home, Location newLoc) {
        home.setLocation(newLoc);
    }

    public static List<Home> getHomes(UUID owner) {
        return homes
                .stream()
                .filter(home -> home.getOwner().equals(owner))
                .toList();
    }

    public static List<String> getHomesNames(UUID owner) {
        return getHomes(owner)
                .stream()
                .map(Home::getName)
                .toList();
    }

    public static int getHomeLimit(UUID owner) {
        HomeLimit homeLimit = homeLimits.stream()
                .filter(hl -> hl.getPlayer().equals(owner))
                .findFirst()
                .orElse(null);

        if (homeLimit == null) {
            homeLimit = new HomeLimit(owner, HomeLimits.LIMIT_0);
            homeLimits.add(homeLimit);
        }

        return homeLimit.getLimit();
    }

    public static void updateHomeLimit(UUID owner) {
        HomeLimit homeLimit = homeLimits.stream()
                .filter(hl -> hl.getPlayer().equals(owner))
                .findFirst()
                .orElse(null);
        if (homeLimit == null) {
            homeLimits.add(new HomeLimit(owner, HomeLimits.LIMIT_0));
        } else {
            int currentLimitIndex = homeLimit.getHomeLimit().ordinal();
            HomeLimits newLimit = HomeLimits.values()[currentLimitIndex + 1];
            homeLimit.setLimit(newLimit.getLimit());
        }
    }

    // DB methods

    private static Dao<Home, UUID> homesDao;
    private static Dao<HomeLimit, UUID> limitsDao;

    public static void init_db(ConnectionSource connectionSource) throws SQLException {
        TableUtils.createTableIfNotExists(connectionSource, Home.class);
        homesDao = DaoManager.createDao(connectionSource, Home.class);

        TableUtils.createTableIfNotExists(connectionSource, HomeLimit.class);
        limitsDao = DaoManager.createDao(connectionSource, HomeLimit.class);
    }

    private static void loadHomeLimit() {
        try {
            homeLimits.addAll(limitsDao.queryForAll());

            for (HomeLimit homeLimit : homeLimits) {
                if (homeLimit.getLimit() == 0) homeLimit.setLimit(HomeLimits.LIMIT_0.getLimit());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void saveHomeLimit() {
        try {
            TableUtils.clearTable(DatabaseManager.getConnectionSource(), HomeLimit.class);
            limitsDao.create(homeLimits);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void loadHomes() {
        try {
            homes.addAll(homesDao.queryForAll());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void saveHomes() {
        try {
            TableUtils.clearTable(DatabaseManager.getConnectionSource(), Home.class);
            homesDao.create(homes);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
