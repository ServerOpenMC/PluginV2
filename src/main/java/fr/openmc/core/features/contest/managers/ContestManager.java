package fr.openmc.core.features.contest.managers;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import fr.openmc.api.hooks.ItemsAdderHook;
import fr.openmc.api.menulib.Menu;
import fr.openmc.core.CommandsManager;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.contest.ContestEndEvent;
import fr.openmc.core.features.contest.commands.ContestCommand;
import fr.openmc.core.features.contest.listeners.ContestIntractEvents;
import fr.openmc.core.features.contest.menu.ContributionMenu;
import fr.openmc.core.features.contest.menu.MoreInfoMenu;
import fr.openmc.core.features.contest.menu.TradeMenu;
import fr.openmc.core.features.contest.menu.VoteMenu;
import fr.openmc.core.features.contest.models.Contest;
import fr.openmc.core.features.contest.models.ContestPlayer;
import fr.openmc.core.features.economy.EconomyManager;
import fr.openmc.core.features.economy.Transaction;
import fr.openmc.core.features.economy.TransactionsManager;
import fr.openmc.core.features.leaderboards.LeaderboardManager;
import fr.openmc.core.features.mailboxes.MailboxManager;
import fr.openmc.core.items.CustomItemRegistry;
import fr.openmc.core.utils.CacheOfflinePlayer;
import fr.openmc.core.utils.ColorUtils;
import fr.openmc.core.utils.DateUtils;
import fr.openmc.core.utils.ParticleUtils;
import fr.openmc.core.utils.database.DatabaseManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import java.sql.SQLException;
import java.text.DecimalFormat;
import java.time.DayOfWeek;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static fr.openmc.core.features.mailboxes.utils.MailboxUtils.getHoverEvent;

public class ContestManager {

    private static final DayOfWeek START_CONTEST_DAY = DayOfWeek.FRIDAY;
    private static final DayOfWeek START_TRADE_CONTEST_DAY = DayOfWeek.SATURDAY;
    private static final DayOfWeek END_CONTEST_DAY = DayOfWeek.MONDAY;

    public static Contest data;
    public static Map<UUID, ContestPlayer> dataPlayer = new HashMap<>();

    private static final List<String> colorContest = Arrays.asList(
            "WHITE","YELLOW","LIGHT_PURPLE","RED","AQUA","GREEN","BLUE",
            "DARK_GRAY","GRAY","GOLD","DARK_PURPLE","DARK_AQUA","DARK_RED",
            "DARK_GREEN","DARK_BLUE","BLACK"
    );
    private static final Set<Class<? extends Menu>> contestMenus = new HashSet<>();

    static {
        contestMenus.add(ContributionMenu.class);
        contestMenus.add(MoreInfoMenu.class);
        contestMenus.add(TradeMenu.class);
        contestMenus.add(VoteMenu.class);
    }

    /**
     * Constructeur du ContestManager :
     * – Enregistre les évents liés aux contests si ItemsAdder est présent
     * - Enregistre les suggestions pour l’autocomplétion des commandes
     * - Enregistre la commande principale /contest
     * - Initialise les données globales et les joueurs
     * - Programme le lancement et la fin des différentes phases du contest
     */
    public static void init() {
        // ** LISTENERS **
        if (ItemsAdderHook.isHasItemAdder()) {
            OMCPlugin.registerEvents(
                    new ContestIntractEvents()
            );
        }

        // ** COMMANDS **
        CommandsManager.getHandler().register(
                new ContestCommand()
        );

        // ** MANAGER EXTERNE **
        new TradeYMLManager();

        // ** LOAD DATAS **
        initContestData();
        loadContestPlayerData();

        // ** SCHEDULE TASK **
        scheduleStartContest();
        scheduleStartTradeContest();
        scheduleEndContest();
    }

    private static Dao<Contest, Integer> contestDao;
    private static Dao<ContestPlayer, UUID> playerDao;

    /**
     * Initialise la base de données pour les contests et les joueurs
     * (création des tables si elles n’existent pas encore)
     */
    public static void initDB(ConnectionSource connectionSource) throws SQLException {
        TableUtils.createTableIfNotExists(connectionSource, Contest.class);
        contestDao = DaoManager.createDao(connectionSource, Contest.class);

        TableUtils.createTableIfNotExists(connectionSource, ContestPlayer.class);
        playerDao = DaoManager.createDao(connectionSource, ContestPlayer.class);
    }

    /**
     * Initialise les données globales du contest depuis la DB.
     * Si aucune donnée n’est trouvée, un contest par défaut est créé.
     */
    public static void initContestData() {
        try {
            data = contestDao.queryForFirst();
            if (data == null) {
                data = new Contest("Mayonnaise", "Ketchup", "YELLOW", "RED", 1, "ven.", 0, 0);
                contestDao.create(data);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Sauvegarde les données globales du contest dans la DB.
     */
    public static void saveContestData() {
        try {
            contestDao.update(data);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Charge les données des joueurs depuis la DB
     * et les insère dans la map dataPlayer.
     */
    public static void loadContestPlayerData() {
        try {
            playerDao.queryForAll().forEach(player -> dataPlayer.put(player.getUUID(), player));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Sauvegarde les données des joueurs (points, camp, etc.) dans la DB.
     */
    public static void saveContestPlayerData() {
        OMCPlugin.getInstance().getSLF4JLogger().info("Saving contest player data...");
        dataPlayer.forEach((player, data) -> {
            try {
                playerDao.createOrUpdate(data);
            } catch (SQLException e) {
                OMCPlugin.getInstance().getSLF4JLogger().warn("Failed to save contest player data for {}: {}", player, e.getMessage(), e);
            }
        });
        OMCPlugin.getInstance().getSLF4JLogger().info("Contest player data saved successfully.");
    }

    /**
     * Vide les tables relatives au contest (Contest et ContestPlayer) dans la DB.
     */
    public static void clearDB() {
        try {
            TableUtils.clearTable(DatabaseManager.getConnectionSource(), Contest.class);
            TableUtils.clearTable(DatabaseManager.getConnectionSource(), ContestPlayer.class);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Démarre la phase 1 du contest (phase de vote).
     * - Définit la phase sur 2
     * - Réinitialise les particules
     * - Diffuse un message et joue un son aux joueurs connectés
     */
    public static void initPhase1() {
        data.setPhase(2);
        ParticleUtils.color1 = null;
        ParticleUtils.color2 = null;

        Bukkit.broadcast(Component.text("""
                        §8§m                                                     §r
                        §7
                        §6§lCONTEST !§r §7 Les votes sont ouverts !§7
                        §7
                        §8§o*on se retrouve au spawn pour pouvoir voter ou /contest...*
                        §7
                        §8§m                                                     §r"""
        ));

        for (Player player : Bukkit.getOnlinePlayers()) {
            player.playSound(player.getEyeLocation(), Sound.BLOCK_AMETHYST_BLOCK_RESONATE, 1.0F, 0.2F);
        }
    }

    /**
     * Démarre la phase 2 du contest (contributions et échanges).
     * - Sélectionne et met à jour les trades
     * - Définit la phase sur 3
     * - Diffuse un message et joue un son aux joueurs connectés
     */
    public static void initPhase2() {
        List<Map<String, Object>> selectedTrades = TradeYMLManager.getTradeSelected(true);
        for (Map<String, Object> trade : selectedTrades) {
            TradeYMLManager.updateColumnBooleanFromRandomTrades(false, (String) trade.get("ress"));
        }

        List<Map<String, Object>> unselectedTrades = TradeYMLManager.getTradeSelected(false);
        for (Map<String, Object> trade : unselectedTrades) {
            TradeYMLManager.updateColumnBooleanFromRandomTrades(true, (String) trade.get("ress"));
        }

        data.setPhase(3);

        Bukkit.broadcast(Component.text("""
                        §8§m                                                     §r
                        §7
                        §6§lCONTEST !§r §7Les contributions ont commencé!§7
                        §7Échangez des ressources contre des Coquillages de Contest. Récoltez en un max et déposez les
                        §8§ovia la borne des contest ou /contest
                        §7
                        §8§m                                                     §r"""
        ));

        for (Player player : Bukkit.getOnlinePlayers()) {
            player.playSound(player.getEyeLocation(), Sound.ENTITY_FIREWORK_ROCKET_TWINKLE, 1.0F, 0.3F);
        }
    }

    /**
     * Démarre la phase 3 du contest (fin du contest et récompenses).
     * - Calcule les résultats et affiche les statistiques
     * - Crée un livre de résultats global + classement
     * - Donne un livre personnalisé avec récompenses à chaque joueur
     * - Envoie les récompenses via la mailbox
     * - Réinitialise les données en DB pour le prochain contest
     */
    public static void initPhase3() {
        data.setPhase(4);

        ParticleUtils.color1 = null;
        ParticleUtils.color2 = null;

        for (Player player : Bukkit.getOnlinePlayers()) {
            InventoryView openInv = player.getOpenInventory();
            InventoryHolder holder = openInv.getTopInventory().getHolder();

            if (holder instanceof Menu menu) {
                if (contestMenus.contains(menu.getClass())) {
                    player.closeInventory();
                }
            }

            player.playSound(player.getEyeLocation(), Sound.ENTITY_ENDER_DRAGON_DEATH, 1.0F, 2F);
        }

        Bukkit.broadcast(Component.text("""
                        §8§m                                                     §r
                        §7
                        §6§lCONTEST !§r §7Time over ! §7
                        §7Fin du contest, retrouvez vos récompenses et le bilan de ce Contest
                        §7sous forme de livre
                        §8§o*/contest pour voir quand le prochain contest arrive*
                        §7
                        §8§m                                                     §r"""
        ));
        Component messageMail = Component.text("Vous avez reçu la lettre du contest", NamedTextColor.DARK_GREEN)
                .append(Component.text("\nCliquez ici", NamedTextColor.YELLOW))
                .clickEvent(ClickEvent.runCommand("mailbox"))
                .hoverEvent(getHoverEvent("Ouvrir la mailbox"))
                .append(Component.text(" pour ouvrir la mailbox", NamedTextColor.GOLD));
        Bukkit.broadcast(messageMail);

        // GET GLOBAL CONTEST INFORMATION
        String camp1Color = data.getColor1();
        String camp2Color = data.getColor2();
        NamedTextColor color1 = ColorUtils.getReadableColor(ColorUtils.getNamedTextColor(camp1Color));
        NamedTextColor color2 = ColorUtils.getReadableColor(ColorUtils.getNamedTextColor(camp2Color));
        String camp1Name = data.getCamp1();
        String camp2Name = data.getCamp2();
        
        // Create part of the book
        ItemStack baseBook = new ItemStack(Material.WRITTEN_BOOK);
        BookMeta baseBookMeta = (BookMeta) baseBook.getItemMeta();
        baseBookMeta.setTitle("Les Résultats du contest");
        baseBookMeta.setAuthor("Les contests");

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
        int points1 = data.getPoints1();
        int points2 = data.getPoints2();

        int multiplicateurPoint = Math.abs(vote1Taux - vote2Taux)/16;
        multiplicateurPoint=Integer.parseInt(df.format(multiplicateurPoint));

        if (vote1Taux > vote2Taux) {
            if (points2<points1) {
                points2 *= multiplicateurPoint;
            }
        } else if (vote1Taux < vote2Taux && points1<points2) {
            points1 *= multiplicateurPoint;
        }

        int totalpoint = points1 + points2;
        int points1Taux = (int) (((double) points1 / totalpoint) * 100);
        points1Taux = Integer.parseInt(df.format(points1Taux));
        int points2Taux = (int) (((double) points2 / totalpoint) * 100);
        points2Taux = Integer.parseInt(df.format(points2Taux));

        // 1ERE PAGE - STATS GLOBAL
        String campWinner;
        NamedTextColor colorWinner;
        int voteWinnerTaux;
        int pointsWinnerTaux;
        
        String campLooser;
        NamedTextColor colorLooser;
        int voteLooserTaux;
        int pointsLooserTaux;

        if (points1 > points2) {
            campWinner = camp1Name;
            colorWinner = color1;
            voteWinnerTaux = vote1Taux;
            pointsWinnerTaux = points1Taux;

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
                Component.text("§8§lStatistiques globales \n§0Gagnant : ")
                        .append(Component.text(campWinner).decoration(TextDecoration.ITALIC, false).color(colorWinner))
                        .append(Component.text("\n§0Taux de vote : §8"))
                        .append(Component.text(voteWinnerTaux + "%").decoration(TextDecoration.ITALIC, false))
                        .append(Component.text("\n§0Taux de points : §8"))
                        .append(Component.text(pointsWinnerTaux + "%").decoration(TextDecoration.ITALIC, false))
                        .append(Component.text( "\n\n§0Perdant : "))
                        .append(Component.text(campLooser).decoration(TextDecoration.ITALIC, false).color(colorLooser))
                        .append(Component.text("\n§0Taux de vote : §8"))
                        .append(Component.text(voteLooserTaux + "%").decoration(TextDecoration.ITALIC, false))
                        .append(Component.text("\n§0Taux de points : §8"))
                        .append(Component.text(pointsLooserTaux + "%").decoration(TextDecoration.ITALIC, false))
                        .append(Component.text("\n§0Multiplicateur d'infériorité : §bx"))
                        .append(Component.text(multiplicateurPoint).decoration(TextDecoration.ITALIC, false).color(NamedTextColor.AQUA))
                        .append(Component.text("\n§8§oProchaine page : classement des 10 meilleurs contributeurs"))
        );


        // 2EME PAGE - LES CLASSEMENTS
        final Component[] leaderboard = {Component.text("§8§lLe classement du contest (jusqu'au 10ème)")};

        Map<UUID, ContestPlayer> orderedMap = dataPlayer.entrySet()
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

        orderedMap.forEach((uuid, dataOrdered) -> {
            NamedTextColor playerCampColor2 = ColorUtils.getReadableColor(dataOrdered.getColor());

            Component rankComponent = Component.text("\n#" + (rankInt[0] + 1) + " ").color(LeaderboardManager.getRankColor(rankInt[0] + 1))
                    .append(Component.text(dataOrdered.getName()).decoration(TextDecoration.ITALIC, false).color(playerCampColor2))
                    .append(Component.text(" §8- §b" + dataOrdered.getPoints()));
            rankInt[0]++;
            leaderboard[0] = leaderboard[0].append(rankComponent);
        });

        baseBookMeta.addPages(leaderboard[0]);
        
        List<UUID> winners = new ArrayList<>();
        List<UUID> losers = new ArrayList<>();

        // STATS PERSO + REWARDS
        Map<OfflinePlayer, ItemStack[]> playerItemsMap = new HashMap<>();
        AtomicInteger rank = new AtomicInteger(1);

        orderedMap.forEach((uuid, dataPlayer1) -> {
            ItemStack bookPlayer = new ItemStack(Material.WRITTEN_BOOK);
            BookMeta bookMetaPlayer = baseBookMeta.clone();

            OfflinePlayer player = CacheOfflinePlayer.getOfflinePlayer(uuid);
            int points = dataPlayer1.getPoints();

            String playerCampName = data.get("camp" + dataPlayer1.getCamp());
            NamedTextColor playerCampColor = ColorUtils.getReadableColor(dataPlayer1.getColor());
            String playerTitleContest = ContestPlayerManager.getTitleWithPoints(points) + playerCampName; // ex. Novice en + Moutarde

            bookMetaPlayer.addPages(
                    Component.text("§8§lStatistiques personnelles\n§0Votre camp : ")
                            .append(Component.text(playerCampName).decoration(TextDecoration.ITALIC, false).color(playerCampColor))
                            .append(Component.text("\n§0Votre titre sur le contest §8: "))
                            .append(Component.text(playerTitleContest).decoration(TextDecoration.ITALIC, false).color(playerCampColor))
                            .append(Component.text("\n§0Votre rang sur le contest : §8#"))
                            .append(Component.text(rank.get()))
                            .append(Component.text("\n§0Points déposés : §b" + points))
            );

            List<ItemStack> itemListRewards = new ArrayList<>();
            String textRewards = "§8§lRécompenses";

            int money;
            int aywenite;

            double multiplicator = ContestPlayerManager.getMultiplicatorFromRank(ContestPlayerManager.getRankContestFromOfflineInt(player));
            if(ContestPlayerManager.hasWinInCampFromOfflinePlayer(player)) {

                // Gagnant - ARGENT
                int moneyMin = 10000;
                int moneyMax = 12000;
                moneyMin = (int) (moneyMin * multiplicator);
                moneyMax = (int) (moneyMax * multiplicator);

                Random randomMoney = new Random();
                money = randomMoney.nextInt(moneyMin, moneyMax);
                EconomyManager.addBalance(player.getUniqueId(), money);
                TransactionsManager.registerTransaction(
                        new Transaction(
                                "CONSOLE",
                                player.getUniqueId().toString(),
                                money,
                                "Récompense contest - Gagnant"
                        )
                );
                // Gagnant - Aywenite
                int ayweniteMin = 40;
                int ayweniteMax = 60;
                ayweniteMin = (int) (ayweniteMin * multiplicator);
                ayweniteMax = (int) (ayweniteMax * multiplicator);
                Random randomAwyenite = new Random();
                aywenite = randomAwyenite.nextInt(ayweniteMin, ayweniteMax);
                
                // Gagnant - EVENT
                winners.add(player.getUniqueId());
            } else {
                // Perdant - ARGENT
                int moneyMin = 2000;
                int moneyMax = 4000;
                moneyMin = (int) (moneyMin * multiplicator);
                moneyMax = (int) (moneyMax * multiplicator);

                Random randomMoney = new Random();
                money = randomMoney.nextInt(moneyMin, moneyMax);
                EconomyManager.addBalance(player.getUniqueId(), money);
                TransactionsManager.registerTransaction(
                        new Transaction(
                                "CONSOLE",
                                player.getUniqueId().toString(),
                                money,
                                "Récompense contest - Perdant"
                        )
                );

                // Perdant - Aywenite
                int ayweniteMin = 20;
                int ayweniteMax = 25;
                ayweniteMin = (int) (ayweniteMin * multiplicator);
                ayweniteMax = (int) (ayweniteMax * multiplicator);
                Random randomAwyenite = new Random();
                aywenite = randomAwyenite.nextInt(ayweniteMin, ayweniteMax);
                
                // Perdant - EVENT
                losers.add(player.getUniqueId());
            }

            // PRINT REWARDS
            textRewards += "\n§8+ §6" + money + "$ ";
            textRewards += "\n§9+ §d" + aywenite + " d'Aywenite ";
            textRewards += "\n§7Boost de §b" + multiplicator;

            bookMetaPlayer.addPages(
                    Component.text(textRewards)
            );

            bookPlayer.setItemMeta(bookMetaPlayer);

            ItemStack ayweniteItemStack = CustomItemRegistry.getByName("omc_items:aywenite").getBest();
            ayweniteItemStack.setAmount(aywenite);
            itemListRewards.add(bookPlayer);
            itemListRewards.add(ayweniteItemStack);

            ItemStack[] rewards = itemListRewards.toArray(new ItemStack[0]);
            playerItemsMap.put(player, rewards);
            rank.getAndIncrement();
        });
        
        try {
            Bukkit.getServer().getPluginManager().callEvent(new ContestEndEvent(data, winners, losers));
        } catch (IllegalStateException e) {
            throw new RuntimeException(e);
        }
        
        // Exécuter les requêtes SQL dans un autre thread
        Bukkit.getScheduler().runTaskAsynchronously(OMCPlugin.getInstance(), () -> {
            TradeYMLManager.addOneToLastContest(data.getCamp1()); // on ajoute 1 au contest précédant dans data/contest.yml pour signifier qu'il n'est plus prioritaire

            try {
                TableUtils.clearTable(DatabaseManager.getConnectionSource(), ContestPlayer.class);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

            TradeYMLManager.selectRandomlyContest(); // on pioche un contest qui a une valeur selected la + faible
            dataPlayer = new HashMap<>(); // on supprime les données précédentes des joueurs
            MailboxManager.sendItemsToAOfflinePlayerBatch(playerItemsMap);
        });
    }

    /**
     * Calcule le nombre de votes pour un camp donné.
     * @param camps 1 ou 2
     * @return nombre de votes
     */
    public static Integer getVoteTaux(Integer camps) {
        return (int) dataPlayer.values().stream()
                .filter(player -> player.getCamp() == camps)
                .count();
    }

    /**
     * Retourne la liste des couleurs disponibles pour créer un contest.
     */
    public static List<String> getColorContestList() {
        return new ArrayList<>(colorContest);
    }

    /**
     * Insère un contest personnalisé dans la DB avec 2 camps et leurs couleurs.
     */
    public static void insertCustomContest(String camp1, String color1, String camp2, String color2) {
        data = new Contest(camp1, camp2, color1, color2, 1, "ven.", 0, 0);
    }

    /**
     * Programme le lancement de la phase 1 (votes) chaque vendredi à minuit.
     */
    private static void scheduleStartContest() {
        long delayInTicks = DateUtils.getSecondsUntilDayOfWeekMidnight(START_CONTEST_DAY) * 20;

        if (data.getPhase() == 1 && DateUtils.getCurrentDayOfWeek().equals(START_CONTEST_DAY)) {
            ContestManager.initPhase1();
        }

        Bukkit.getScheduler().runTaskLater(OMCPlugin.getInstance(), () -> {
            if (data.getPhase() != 1) return;

            ContestManager.initPhase1();
            scheduleStartContest();
        }, delayInTicks);
    }

    /**
     * Programme le lancement de la phase 2 (contributions) chaque samedi à minuit.
     */
    private static void scheduleStartTradeContest() {
        long delayInTicks = DateUtils.getSecondsUntilDayOfWeekMidnight(START_TRADE_CONTEST_DAY) * 20;

        if (data.getPhase() == 2 && DateUtils.getCurrentDayOfWeek().equals(START_TRADE_CONTEST_DAY)) {
            ContestManager.initPhase2();
        }

        Bukkit.getScheduler().runTaskLater(OMCPlugin.getInstance(), () -> {
            if (data.getPhase() != 2) return;

            ContestManager.initPhase2();
            scheduleStartTradeContest();
        }, delayInTicks);
    }

    /**
     * Programme la fin du contest (phase 3) chaque lundi à minuit.
     */
    private static void scheduleEndContest() {
        long delayInTicks = DateUtils.getSecondsUntilDayOfWeekMidnight(END_CONTEST_DAY) * 20;

        if (data.getPhase() == 3 && DateUtils.getCurrentDayOfWeek().equals(END_CONTEST_DAY)) {
            ContestManager.initPhase3();
        }

        Bukkit.getScheduler().runTaskLater(OMCPlugin.getInstance(), () -> {
            if (data.getPhase() != 3) return;

            ContestManager.initPhase3();
            scheduleEndContest();
        }, delayInTicks);
    }
}
