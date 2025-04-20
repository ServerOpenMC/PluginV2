package fr.openmc.core.features.homes;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.CommandsManager;
import fr.openmc.core.features.homes.command.*;
import fr.openmc.core.features.homes.utils.HomeUtil;
import fr.openmc.core.features.homes.world.DisabledWorldHome;
import fr.openmc.core.utils.database.DatabaseManager;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.generator.WorldInfo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
public class HomesManager {

    // Les TODOs présents, sont du plus, donc je prend mon temps pour les faire / faire les autres features
    // TODO: Faire le menu sign pour changer le nom du home
    // TODO: Dans le menu HomeChangeIcon, ajouter les items vanilla + un menu pour faire une recherche par nom, les filtres, etc

    public static List<Home> homes = new ArrayList<>();
    public static List<HomeLimit> homeLimits = new ArrayList<>();
    public DisabledWorldHome disabledWorldHome;
    @Getter private static HomesManager instance;

    public HomesManager() {
        instance = this;
        disabledWorldHome = new DisabledWorldHome(OMCPlugin.getInstance());

        CommandsManager.getHandler().getAutoCompleter().registerSuggestion("homes",
                (args, sender, command) -> {
            Player player = Bukkit.getPlayer(sender.getUniqueId());
            List<String> suggestions = new ArrayList<>();
            if (player == null) return suggestions;

            if (args.isEmpty()) {
                if (player.hasPermission("omc.admin.homes.teleport.others")) {
                    suggestions.addAll(
                            Bukkit.getOnlinePlayers().stream()
                                    .map(OfflinePlayer::getName)
                                    .map(name -> name + ":")
                                    .toList()
                    );
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

                if(arg.contains(":") && player.hasPermission("omc.admin.homes.teleport.others")) {
                    if (command.getName().equalsIgnoreCase("home") ||
                        command.getName().equalsIgnoreCase("delhome") ||
                        command.getName().equalsIgnoreCase("relocatehome") ||
                        command.getName().equalsIgnoreCase("renamehome")) {
                        String[] split = arg.split(":", 2);
                        OfflinePlayer target = Bukkit.getOfflinePlayer(split[0]);

                        if(target != null && target.hasPlayedBefore()) {
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

            if(command.getName().equalsIgnoreCase("home") ||
                command.getName().equalsIgnoreCase("delhome") ||
                command.getName().equalsIgnoreCase("relocatehome") ||
                command.getName().equalsIgnoreCase("renamehome")) {
                suggestions.addAll(getHomesNames(player.getUniqueId()));
            }
            return suggestions;
        });

        CommandsManager.getHandler().getAutoCompleter().registerSuggestion("homeWorldsAdd",
                (args, sender, command) -> {
            List<String> suggestions = new ArrayList<>(Bukkit.getWorlds().stream().map(WorldInfo::getName).toList());
            suggestions.removeAll(disabledWorldHome.getDisabledWorlds());
            return suggestions;
        });

        CommandsManager.getHandler().getAutoCompleter().registerSuggestion("homeWorldsRemove",
                (args, sender, command) ->
                new ArrayList<>(disabledWorldHome.getDisabledWorlds())
        );

        CommandsManager.getHandler().register(
                new SetHome(this),
                new RenameHome(this),
                new DelHome(this),
                new RelocateHome(this),
                new TpHome(this),
                new HomeWorld(disabledWorldHome)
        );

        loadHomeLimit();
        loadHomes();
    }

    public void saveHomesData() {
        saveHomes();
        saveHomeLimit();
    }

    public void addHome(Home home) {
        homes.add(home);
    }

    public void removeHome(Home home) {
        homes.remove(home);
    }

    public void renameHome(Home home, String newName) {
        home.setName(newName);
    }

    public void relocateHome(Home home, Location newLoc) {
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

    public int getHomeLimit(UUID owner) {
        HomeLimit homeLimit = homeLimits.stream()
                .filter(hl -> hl.getPlayerUUID().equals(owner))
                .findFirst()
                .orElse(null);

        if (homeLimit == null) {
            homeLimit = new HomeLimit(owner, HomeLimits.LIMIT_0);
            homeLimits.add(homeLimit);
        }

        return homeLimit == null ? 0 : homeLimit.getHomeLimit().getLimit();
    }

    public void updateHomeLimit(UUID owner) {
        HomeLimit homeLimit = homeLimits.stream()
                .filter(hl -> hl.getPlayerUUID().equals(owner))
                .findFirst()
                .orElse(null);
        if (homeLimit == null) {
            homeLimits.add(new HomeLimit(owner, HomeLimits.LIMIT_0));
        } else {
            int currentLimitIndex = homeLimit.getHomeLimit().ordinal();
            HomeLimits newLimit = HomeLimits.values()[currentLimitIndex + 1];
            homeLimit.setHomeLimit(newLimit);
        }
    }

    // DB methods

    public static void init_db(Connection conn) throws SQLException {
        String createHomesTable = "CREATE TABLE IF NOT EXISTS homes (" +
                "owner VARCHAR(36), " +
                "name VARCHAR(32), " +
                "x DOUBLE, " +
                "y DOUBLE, " +
                "z DOUBLE, " +
                "yaw FLOAT, " +
                "pitch FLOAT, " +
                "world VARCHAR(32), " +
                "icon VARCHAR(32))";
        conn.prepareStatement(createHomesTable).executeUpdate();

        String createHomesLimitsTable = "CREATE TABLE IF NOT EXISTS homes_limits (" +
                "player_uuid VARCHAR(36) PRIMARY KEY, " +
                "`limit` INT)";
        conn.prepareStatement(createHomesLimitsTable).executeUpdate();
    }

    private static void loadHomeLimit() {
        try {
            PreparedStatement statement = DatabaseManager.getConnection().prepareStatement("SELECT player_uuid, `limit` FROM homes_limits");
            statement.executeQuery();
            ResultSet rs = statement.getResultSet();

            while (rs.next()) {
                UUID playerUUID = UUID.fromString(rs.getString("player_uuid"));
                int limit = rs.getInt("limit");
                HomeLimit homeLimit = new HomeLimit(playerUUID, HomeLimits.values()[limit]);

                homeLimits.add(homeLimit);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void saveHomeLimit() {
        try {
            PreparedStatement statement = DatabaseManager.getConnection().prepareStatement("TRUNCATE TABLE homes_limits");
            statement.executeUpdate();

            for (HomeLimit homeLimit : homeLimits) {
                statement = DatabaseManager.getConnection().prepareStatement("INSERT INTO homes_limits (player_uuid, `limit`) VALUES (?, ?)");
                statement.setString(1, homeLimit.getPlayerUUID().toString());
                HomeLimits limit = homeLimit.getHomeLimit();
                statement.setInt(2, Integer.parseInt(limit.name().split("_")[1]));
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void loadHomes() {
        try {
            PreparedStatement statement = DatabaseManager.getConnection().prepareStatement("SELECT owner, name, x, y, z, yaw, pitch, world, icon FROM homes");
            statement.executeQuery();
            ResultSet rs = statement.getResultSet();

            while (rs.next()) {
                UUID owner = UUID.fromString(rs.getString("owner"));
                String name = rs.getString("name");
                double x = rs.getDouble("x");
                double y = rs.getDouble("y");
                double z = rs.getDouble("z");
                float yaw = rs.getFloat("yaw");
                float pitch = rs.getFloat("pitch");
                String world = rs.getString("world");
                String icon = rs.getString("icon");

                Location location = new Location(Bukkit.getWorld(world), x, y, z, yaw, pitch);
                Home home = new Home(owner, name, location, HomeUtil.getHomeIcon(icon));

                homes.add(home);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void saveHomes() {
        try {
            PreparedStatement statement = DatabaseManager.getConnection().prepareStatement("TRUNCATE TABLE homes");
            statement.executeUpdate();

            for (Home home : homes) {
                statement = DatabaseManager.getConnection().prepareStatement("INSERT INTO homes (owner, name, x, y, z, yaw, pitch, world, icon) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)");
                statement.setString(1, home.getOwner().toString());
                statement.setString(2, home.getName());
                statement.setDouble(3, home.getLocation().getX());
                statement.setDouble(4, home.getLocation().getY());
                statement.setDouble(5, home.getLocation().getZ());
                statement.setFloat(6, home.getLocation().getYaw());
                statement.setFloat(7, home.getLocation().getPitch());
                statement.setString(8, home.getLocation().getWorld().getName());
                statement.setString(9, home.getIcon().getId());

                statement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
