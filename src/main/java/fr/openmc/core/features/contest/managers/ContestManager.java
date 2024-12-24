package fr.openmc.core.features.contest.managers;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.time.DayOfWeek;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.commands.CommandsManager;
import fr.openmc.core.features.contest.ContestData;
import fr.openmc.core.features.contest.ContestPlayer;
import fr.openmc.core.features.contest.commands.ContestCommand;
import fr.openmc.core.features.contest.listeners.ContestIntractEvents;
import fr.openmc.core.features.contest.listeners.ContestListener;
import fr.openmc.core.features.economy.EconomyManager;
import fr.openmc.core.features.mailboxes.MailboxManager;
import fr.openmc.core.utils.customitems.CustomItemRegistry;
import fr.openmc.core.utils.database.DatabaseManager;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.scheduler.BukkitRunnable;
import revxrsal.commands.autocomplete.SuggestionProvider;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static fr.openmc.core.features.mailboxes.utils.MailboxUtils.getHoverEvent;
import static fr.openmc.core.features.mailboxes.utils.MailboxUtils.getRunCommand;

public class ContestManager {

    @Getter static ContestManager instance;

    public File contestFile;
    public YamlConfiguration contestConfig;
    private final OMCPlugin plugin;

    @Setter private ContestPlayerManager contestPlayerManager;

    public ContestData data;
    public Map<String, ContestPlayer> dataPlayer = new HashMap<>();

    private BukkitRunnable eventRunnable;

    private final List<String> colorContest = Arrays.asList(
            "WHITE","YELLOW","LIGHT_PURPLE","RED","AQUA","GREEN","BLUE",
            "DARK_GRAY","GRAY","GOLD","DARK_PURPLE","DARK_AQUA","DARK_RED",
            "DARK_GREEN","DARK_BLUE","BLACK"
    );

    public ContestManager(OMCPlugin plugin) {
        instance = this;

        //Const
        this.plugin = plugin;
        contestPlayerManager = ContestPlayerManager.getInstance();

        // LISTENERS
        OMCPlugin.registerEvents(
                new ContestListener(this.plugin)
        );
        if (CustomItemRegistry.hasItemsAdder()) {
            OMCPlugin.registerEvents(
                    new ContestIntractEvents()
            );
        }

        //COMMANDS
        CommandsManager.getHandler().getAutoCompleter().registerSuggestion("colorContest", SuggestionProvider.of(ContestManager.getInstance().getColorContestList()));
        CommandsManager.getHandler().getAutoCompleter().registerSuggestion("trade", SuggestionProvider.of(ContestManager.getInstance().getRessListFromConfig()));

        CommandsManager.getHandler().register(
                new ContestCommand()
        );

        //Load config
        this.contestFile = new File(plugin.getDataFolder() + "/data", "contest.yml");
        loadContestConfig();

        // Fill data and playerData
        initContestData();
        loadContestPlayerData();

//        // Logs of data and playerData
//        eventRunnable = new BukkitRunnable() {
//            @Override
//            public void run() {
//                plugin.getLogger().info(data + " " + data.getPhase() + " " + data.getCamp1() + " " + data.getColor1() + " " + data.getPoint1() + " " + data.getCamp2() + " " + data.getColor2() + " " + data.getPoint2());
//                plugin.getLogger().info(" ");
//                dataPlayer.forEach((uuid, data) -> {
//                    plugin.getLogger().info(uuid + " " + data.getCamp() + " " + data.getColor() + " " + data.getPoints() + " " + data.getName());
//                });
//            }
//        };
//
//        // tout les minutes
//        eventRunnable.runTaskTimer(plugin, 0, 100);
    }

    public static void init_db(Connection conn) throws SQLException {
        // Système de Contest
        conn.prepareStatement("CREATE TABLE IF NOT EXISTS contest (phase int(11), camp1 VARCHAR(36), color1 VARCHAR(36), camp2 VARCHAR(36), color2 VARCHAR(36), startdate VARCHAR(36), points1 int(11), points2 int(11))").executeUpdate();
        PreparedStatement state = conn.prepareStatement("SELECT COUNT(*) FROM contest");
        ResultSet rs = state.executeQuery();

        // push first contest
        if(rs.next()) {
            if(rs.getInt(1) == 0) {
                PreparedStatement states = conn.prepareStatement("INSERT INTO contest (phase, camp1, color1, camp2, color2, startdate, points1, points2) VALUES (1, 'Mayonnaise', 'YELLOW', 'Ketchup', 'RED', ?, 0,0)");

                String dateContestStart = "ven.";
                states.setString(1, dateContestStart);
                states.executeUpdate();
            }
        }

        // Table camps
        conn.prepareStatement("CREATE TABLE IF NOT EXISTS contest_camps (minecraft_uuid VARCHAR(36) UNIQUE, name VARCHAR(36), camps int(11), point_dep int(11))").executeUpdate();
    }

    private void loadContestConfig() {
        if(!contestFile.exists()) {
            contestFile.getParentFile().mkdirs();
            plugin.saveResource("data/contest.yml", false);
        }

        this.contestConfig = YamlConfiguration.loadConfiguration(contestFile);
    }

    public void saveContestConfig() {
        try {
            contestConfig.save(contestFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Impossible de sauvegarder le fichier de configuration des contests");
            e.printStackTrace();
        }
    }

    // CONTEST DATA
    public void initContestData() {
        try (PreparedStatement states = DatabaseManager.getConnection().prepareStatement("SELECT * FROM contest WHERE 1")) {
            ResultSet result = states.executeQuery();
            if (result.next()) {
                String camp1 = result.getString("camp1");
                String camp2 = result.getString("camp2");
                String color1 = result.getString("color1");
                String color2 = result.getString("color2");
                int phase = result.getInt("phase");
                String startdate = result.getString("startdate");
                int point1 = result.getInt("points1");
                int point2 = result.getInt("points2");

                data = new ContestData(camp1, camp2, color1, color2, phase, startdate, point1, point2);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void saveContestData() {
        try (PreparedStatement states = DatabaseManager.getConnection().prepareStatement("UPDATE contest SET phase = ?, camp1 = ?, color1 = ?, camp2 = ?, color2 = ?, startdate = ?, points1 = ?, points2 = ?")) {
            plugin.getLogger().info("Sauvegarde des données du Contest...");
            states.setInt(1, data.getPhase());
            states.setString(2, data.getCamp1());
            states.setString(3, data.getColor1());
            states.setString(4, data.getCamp2());
            states.setString(5, data.getColor2());
            states.setString(6, data.getStartdate());
            states.setInt(7, data.getPoint1());
            states.setInt(8, data.getPoint2());

            states.executeUpdate();
            plugin.getLogger().info("Sauvegarde des données du Contest réussi.");
        } catch (SQLException e) {
            plugin.getLogger().severe("Echec de la sauvegarde des données du Contest.");
            throw new RuntimeException(e);
        }
    }

    // CONTEST PLAYER DATA
    public void loadContestPlayerData() {
        try (PreparedStatement states = DatabaseManager.getConnection().prepareStatement("SELECT * FROM contest_camps")) {
            ResultSet result = states.executeQuery();
            while (result.next()) {
                String uuid = result.getString("minecraft_uuid");
                String name = result.getString("name");
                int points = result.getInt("point_dep");
                int camp = result.getInt("camps");
                String color = data.get("color" + camp);
                NamedTextColor campColor = ColorUtils.getNamedTextColor(color);

                dataPlayer.put(uuid, new ContestPlayer(name, points, camp, campColor));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void saveContestPlayerData() {
        try (PreparedStatement statement = DatabaseManager.getConnection().prepareStatement(
                "INSERT INTO contest_camps (minecraft_uuid, name, camps, point_dep) " +
                        "VALUES (?, ?, ?, ?) " +
                        "ON DUPLICATE KEY UPDATE " +
                        "name = VALUES(name), camps = VALUES(camps), point_dep = VALUES(point_dep)"
        )) {
            plugin.getLogger().info("Sauvegarde des données des Joueurs du Contest...");
            dataPlayer.forEach((uuid, playerData) -> {
                try {
                    statement.setString(1, uuid);
                    statement.setString(2, playerData.getName());
                    statement.setInt(3, playerData.getCamp());
                    statement.setInt(4, playerData.getPoints());

                    statement.addBatch();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            });

            statement.executeBatch();

            plugin.getLogger().info("Sauvegarde des données des Joueurs du Contest réussi.");
        } catch (SQLException e) {
            plugin.getLogger().severe("Echec de la sauvegarde des données des Joueurs du Contest.");
            e.printStackTrace();
        }
    }

    //PHASE 1
    public void initPhase1() {
        data.setPhase(2);

        Bukkit.broadcast(Component.text(
                "§8§m                                                     §r\n" +
                        "§7\n" +
                        "§6§lCONTEST!§r §7 Les votes sont ouverts !§7" +
                        "§7\n" +
                        "§8§o*on se retrouve au spawn pour pouvoir voter ou /contest...*\n" +
                        "§7\n" +
                        "§8§m                                                     §r"
        ));

        for (Player player : Bukkit.getOnlinePlayers()) {
            player.playSound(player.getEyeLocation(), Sound.BLOCK_AMETHYST_BLOCK_RESONATE, 1.0F, 0.2F);
        }

        plugin.getLogger().info("[CONTEST] Ouverture des votes");
    }
    //PHASE 2
    public void initPhase2() {
        List<Map<String, Object>> selectedTrades = getTradeSelected(true);
        for (Map<String, Object> trade : selectedTrades) {
            updateColumnBooleanFromRandomTrades(false, (String) trade.get("ress"));
        }

        List<Map<String, Object>> unselectedTrades = getTradeSelected(false);
        for (Map<String, Object> trade : unselectedTrades) {
            updateColumnBooleanFromRandomTrades(true, (String) trade.get("ress"));
        }

        data.setPhase(3);

        Bukkit.broadcast(Component.text(
                "§8§m                                                     §r\n" +
                        "§7\n" +
                        "§6§lCONTEST!§r §7Les contributions ont commencé!§7" +
                        "§7\nEchanger des ressources contre des Coquillages de Contest. Récoltez en un max et déposez les\n" +
                        "§8§ovia la Borne des Contest ou /contest\n" +
                        "§7\n" +
                        "§8§m                                                     §r"
        ));

        for (Player player : Bukkit.getOnlinePlayers()) {
            player.playSound(player.getEyeLocation(), Sound.ENTITY_FIREWORK_ROCKET_TWINKLE, 1.0F, 0.3F);
        }

        plugin.getLogger().info("[CONTEST] Ouverture des trades");
    }
    //PHASE 3
    public void initPhase3() {
        data.setPhase(4);

        for (Player player : Bukkit.getOnlinePlayers()) {
            player.playSound(player.getEyeLocation(), Sound.ENTITY_ENDER_DRAGON_DEATH, 1.0F, 2F);
        }

        Bukkit.broadcast(Component.text(
                "§8§m                                                     §r\n" +
                        "§7\n" +
                        "§6§lCONTEST!§r §7Time over! §7" +
                        "§7\nFin du Contest, retrouvez vos récompenses et le bilan de ce Contest\n" +
                        "§7sous forme de livre\n" +
                        "§8§o*/contest pour voir quand le prochain contest arrive*\n" +
                        "§7\n" +
                        "§8§m                                                     §r"
        ));
        Component message_mail = Component.text("Vous avez reçu la lettre du Contest", NamedTextColor.DARK_GREEN)
                .append(Component.text("\nCliquez-ici", NamedTextColor.YELLOW))
                .clickEvent(getRunCommand("mail"))
                .hoverEvent(getHoverEvent("Ouvrir la mailbox"))
                .append(Component.text(" pour ouvrir la mailbox", NamedTextColor.GOLD));
        Bukkit.broadcast(message_mail);

        // GET GLOBAL CONTEST INFORMATION
        String camp1Color = data.getColor1();
        String camp2Color = data.getColor2();
        NamedTextColor color1 = ColorUtils.getReadableColor(ColorUtils.getNamedTextColor(camp1Color));
        NamedTextColor color2 = ColorUtils.getReadableColor(ColorUtils.getNamedTextColor(camp2Color));
        String camp1Name = data.getCamp1();
        String camp2Name = data.getCamp2();

        //CREATE PART OF BOOK
        ItemStack baseBook = new ItemStack(Material.WRITTEN_BOOK);
        BookMeta baseBookMeta = (BookMeta) baseBook.getItemMeta();
        baseBookMeta.setTitle("Les Résultats du Contest");
        baseBookMeta.setAuthor("Les Contest");

        List<Component> lore = Arrays.asList(
                Component.text(camp1Name).decoration(TextDecoration.ITALIC, false).color(color1)
                        .append(Component.text(" §7VS "))
                        .append(Component.text(camp2Name).decoration(TextDecoration.ITALIC, false).color(color2)),
                Component.text("§e§lOuvrez ce livre pour en savoir plus!")
        );
        baseBookMeta.lore(lore);

        // GET VOTE AND POINT TAUX
        DecimalFormat df = new DecimalFormat("#.#");
        int vote1 = getVoteTaux(1);
        int vote2 = getVoteTaux(2);
        int totalvote = vote1 + vote2;
        int vote1Taux = (int) (((double) vote1 / totalvote) * 100);
        int vote2Taux = (int) (((double) vote2 / totalvote) * 100);
        int points1 = data.getPoint1();
        int points2 = data.getPoint2();

        int multiplicateurPoint = Math.abs(vote1Taux - vote2Taux)/16;
        multiplicateurPoint=Integer.parseInt(df.format(multiplicateurPoint));

        if (vote1Taux > vote2Taux) {
            if (points2<points1) {
                points2 *= multiplicateurPoint;
            }
        } else if (vote1Taux < vote2Taux) {
            if (points1<points2) {
                points1 *= multiplicateurPoint;
            }
        }

        int totalpoint = points1 + points2;
        int points1Taux = (int) (((double) points1 / totalpoint) * 100);
        points1Taux = Integer.parseInt(df.format(points1Taux));
        int points2Taux = (int) (((double) points2 / totalpoint) * 100);
        points2Taux = Integer.parseInt(df.format(points2Taux));

        // 1ERE PAGE - STATS GLOBAL
        String campWinner = null;
        NamedTextColor colorWinner = null;
        int voteWinnerTaux = 0;
        int pointsWinnerTaux = 0;

        String campLooser = null;
        NamedTextColor colorLooser = null;
        int voteLooserTaux = 0;
        int pointsLooserTaux = 0;

        if (points1 > points2) {
            campWinner = camp1Name;
            colorWinner = color1;
            voteWinnerTaux=vote1Taux;
            pointsWinnerTaux=points1Taux;

            campLooser = camp2Name;
            colorLooser = color2;
            voteLooserTaux = vote2Taux;
            pointsLooserTaux = points2Taux;
        } else {
            campWinner = camp2Name;
            colorWinner = color2;
            voteWinnerTaux = vote2Taux;
            pointsWinnerTaux = points2Taux;

            campLooser = camp1Name;
            colorLooser = color1;
            voteLooserTaux = vote1Taux;
            pointsLooserTaux = points1Taux;
        }

        baseBookMeta.addPages(
                Component.text("§8§lStatistiques Globales \n§0Gagnant : ")
                        .append(Component.text(campWinner).decoration(TextDecoration.ITALIC, false).color(colorWinner))
                        .append(Component.text("\n§0Taux de Vote : §8"))
                        .append(Component.text(voteWinnerTaux + "%").decoration(TextDecoration.ITALIC, false))
                        .append(Component.text("\n§0Taux de Points : §8"))
                        .append(Component.text(pointsWinnerTaux + "%").decoration(TextDecoration.ITALIC, false))
                        .append(Component.text( "\n\n§0Perdant : "))
                        .append(Component.text(campLooser).decoration(TextDecoration.ITALIC, false).color(colorLooser))
                        .append(Component.text("\n§0Taux de Vote : §8"))
                        .append(Component.text(voteLooserTaux + "%").decoration(TextDecoration.ITALIC, false))
                        .append(Component.text("\n§0Taux de Points : §8"))
                        .append(Component.text(pointsLooserTaux + "%").decoration(TextDecoration.ITALIC, false))
                        .append(Component.text("\n§0Multiplicateur d'Infériorité : §bx"))
                        .append(Component.text(multiplicateurPoint).decoration(TextDecoration.ITALIC, false).color(NamedTextColor.AQUA))
                        .append(Component.text("\n§8§oProchaine page : Classement des 10 Meilleurs Contributeur"))
        );


        // 2EME PAGE - LES CLASSEMENTS
        final Component[] leaderboard = {Component.text("§8§lLe Classement du Contest (Jusqu'au 10eme)")};

        Map<String, ContestPlayer> orderedMap = dataPlayer.entrySet()
                .stream()
                .sorted((entry1, entry2) -> Integer.compare(
                        entry2.getValue().getPoints(),
                        entry1.getValue().getPoints()
                ))
                .limit(10)
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));

        final int[] rankInt = {0};

        orderedMap.forEach((uuid, data) -> {
            NamedTextColor playerCampColor2 = ColorUtils.getReadableColor(data.getColor());

            Component rankComponent = Component.text("\n§0#" + (rankInt[0] + 1) + " ")
                    .append(Component.text(data.getName()).decoration(TextDecoration.ITALIC, false).color(playerCampColor2))
                    .append(Component.text(" §8- §b" + data.getPoints()));
            rankInt[0]++;
            leaderboard[0] = leaderboard[0].append(rankComponent);
        });

        baseBookMeta.addPages(leaderboard[0]);

        // STATS PERSO + REWARDS
        Map<OfflinePlayer, ItemStack[]> playerItemsMap = new HashMap<>();

        // For each player in contest
        orderedMap.forEach((uuid, dataPlayer1) -> {
            int rank = 1;
            ItemStack bookPlayer = new ItemStack(Material.WRITTEN_BOOK);
            BookMeta bookMetaPlayer = baseBookMeta.clone();

            plugin.getLogger().info(uuid + " " + dataPlayer1.getCamp() + " " + dataPlayer1.getColor() + " " + dataPlayer1.getPoints() + " " + dataPlayer1.getName());

            OfflinePlayer player = Bukkit.getOfflinePlayer(dataPlayer1.getName());
            int points = dataPlayer1.getPoints();

            String playerCampName = data.get("camp" + dataPlayer1.getCamp());
            NamedTextColor playerCampColor = ColorUtils.getReadableColor(dataPlayer1.getColor());
            String playerTitleContest = contestPlayerManager.getTitleWithPoints(points) + playerCampName;
            // ex Novice en + Moutarde

            bookMetaPlayer.addPages(
                    Component.text("§8§lStatistiques Personnelles\n§0Votre camp : ")
                            .append(Component.text(playerCampName).decoration(TextDecoration.ITALIC, false).color(playerCampColor))
                            .append(Component.text("\n§0Votre Titre sur Le Contest §8: "))
                            .append(Component.text(playerTitleContest).decoration(TextDecoration.ITALIC, false).color(playerCampColor))
                            .append(Component.text("\n§0Votre Rang sur Le Contest : §8#"))
                            .append(Component.text(rank))
                            .append(Component.text("\n§0Points Déposés : §b" + points))
            );

            int money = 0;
            if(contestPlayerManager.hasWinInCampFromOfflinePlayer(player)) {
                int moneyMin = 10000;
                int moneyMax = 12000;
                double multi = contestPlayerManager.getMultiMoneyFromRang(contestPlayerManager.getRankContestFromOfflineInt(player));
                moneyMin = (int) (moneyMin * multi);
                moneyMax = (int) (moneyMax * multi);

                money = contestPlayerManager.giveRandomly(moneyMin, moneyMax);
                EconomyManager.getInstance().addBalance(player.getUniqueId(), money);

            } else {
                int moneyMin = 2000;
                int moneyMax = 4000;
                double multi = contestPlayerManager.getMultiMoneyFromRang(contestPlayerManager.getRankContestFromOfflineInt(player));
                moneyMin = (int) (moneyMin * multi);
                moneyMax = (int) (moneyMax * multi);

                money = contestPlayerManager.giveRandomly(moneyMin, moneyMax);
                EconomyManager.getInstance().addBalance(player.getUniqueId(), money);
            }

            //TODO: Mettre Item qui permet d'améliorer sa Mascotte

            bookMetaPlayer.addPages(
                    Component.text("§8§lRécompenses\n§0+ " + money + "$ §b(x" + contestPlayerManager.getMultiMoneyFromRang(contestPlayerManager.getRankContestFromOfflineInt(player)) + ")")
            );

            bookPlayer.setItemMeta(bookMetaPlayer);

            List<ItemStack> itemlist = new ArrayList<>();
            itemlist.add(bookPlayer);

            ItemStack[] items = itemlist.toArray(new ItemStack[itemlist.size()]);
            playerItemsMap.put(player, items);
            rank++;
        });

        //EXECUTER LES REQUETES SQL DANS UN AUTRE THREAD
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                    addOneToLastContest(data.getCamp1()); // on ajoute 1 au contest précédant dans data/contest.yml pour signifier qu'il n'est plus prioritaire
                    deleteTableContest("contest_camps");
                    selectRandomlyContest(); // on pioche un contest qui a une valeur selected la + faible
                    dataPlayer=new HashMap<>(); // on supprime les données précédentes du joueurs
                    MailboxManager.sendItemsToAOfflinePlayerBatch(playerItemsMap); // on envoit les Items en mailbox ss forme de batch
        });

        plugin.getLogger().info("[CONTEST] Fermeture du Contest");
    }

    // TRADE METHODE
    public List<Map<String, Object>> getTradeSelected(boolean bool) {
        List<Map<?, ?>> contestTrades = contestConfig.getMapList("contestTrades");

        List<Map<String, Object>> filteredTrades = contestTrades.stream()
                .filter(trade -> (boolean) trade.get("selected") == bool)
                .map(trade -> (Map<String, Object>) trade)
                .collect(Collectors.toList());
        Collections.shuffle(filteredTrades);

        return filteredTrades.stream().limit(12).collect(Collectors.toList());
    }

    public void updateColumnBooleanFromRandomTrades(Boolean bool, String ress) {
        List<Map<String, Object>> contestTrades = (List<Map<String, Object>>) contestConfig.get("contestTrades");

        for (Map<String, Object> trade : contestTrades) {
            if (trade.get("ress").equals(ress)) {
                trade.put("selected", bool);
            }
        }
        saveContestConfig();
    }

    public DayOfWeek getCurrentDayOfWeek() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("E", Locale.FRENCH);

        LocalDate currentDate = LocalDate.now();
        String currentDayString = currentDate.format(formatter);

        //conversion ex ven. => FRIDAY
        return DayOfWeek.from(formatter.parse(currentDayString));
    }

    // GET TAUX DE VOTE D'UN CAMP
    public Integer getVoteTaux(Integer camps) {
        return (int) dataPlayer.values().stream()
                .filter(player -> player.getCamp() == camps)
                .count();
    }

    //END CONTEST METHODE

    public List<String> getRessListFromConfig() {
        FileConfiguration config = plugin.getConfig();
        List<Map<?, ?>> trades = config.getMapList("contestTrades");
        List<String> ressList = new ArrayList<>();

        for (Map<?, ?> tradeEntry : trades) {
            if (tradeEntry.containsKey("ress")) {
                ressList.add(tradeEntry.get("ress").toString());
            }
        }
        return ressList;
    }

    private void updateSelected(String camp) {
        List<Map<?, ?>> contestList = contestConfig.getMapList("contestList");
        List<Map<String, Object>> updatedContestList = new ArrayList<>();

        for (Map<?, ?> contest : contestList) {
            Map<String, Object> fusionContestList = new HashMap<>();

            for (Map.Entry<?, ?> entry : contest.entrySet()) {
                if (entry.getKey() instanceof String) {
                    fusionContestList.put((String) entry.getKey(), entry.getValue());
                }
            }

            if (fusionContestList.get("camp1").equals(camp)) {
                int selected = (int) fusionContestList.get("selected");
                fusionContestList.put("selected", selected + 1);
            }

            updatedContestList.add(fusionContestList);
        }
        contestConfig.set("contestList", updatedContestList);
        saveContestConfig();
    }
    public void addOneToLastContest(String camps) {
        List<Map<?, ?>> contestList = contestConfig.getMapList("contestList");

        for (Map<?, ?> contest : contestList) {
            if (contest.get("camp1").equals(camps)) {
                Map<String, Object> result = new HashMap<>();
                for (Map.Entry<?, ?> entry : contest.entrySet()) {
                    if (entry.getKey() instanceof String) {
                        result.put((String) entry.getKey(), entry.getValue());
                    }
                }
                updateSelected(camps);
            }
        }
    }

    public void selectRandomlyContest() {
        List<Map<?, ?>> contestList = contestConfig.getMapList("contestList");
        List<Map<String, Object>> orderredContestList = new ArrayList<>();

        for (Map<?, ?> contest : contestList) {
            Map<String, Object> fusionContest = new HashMap<>();
            for (Map.Entry<?, ?> entry : contest.entrySet()) {
                if (entry.getKey() instanceof String) {
                    fusionContest.put((String) entry.getKey(), entry.getValue());
                }
            }
            orderredContestList.add(fusionContest);
        }

        orderredContestList.sort(Comparator.comparingInt(c -> (int) c.get("selected")));

        Map<String, Object> contest = orderredContestList.get(0);

        data = new ContestData((String) contest.get("camp1"), (String) contest.get("camp2"), (String) contest.get("color1"), (String) contest.get("color2"), 1, "ven.", 0, 0);
    }

    public void deleteTableContest(String table) {
        String sql = "DELETE FROM " + table;
        try (PreparedStatement states = DatabaseManager.getConnection().prepareStatement(sql)) {
            states.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<String> getColorContestList() {
        List<String> color = new ArrayList<>();
        for (String colorName : colorContest) {
            color.add(colorName);
        }
        return color;
    }

    public void insertCustomContest(String camp1, String color1, String camp2, String color2) {
        data = new ContestData(camp1, color1, camp2, color2, 1, "ven.", 0, 0);
    }
}
