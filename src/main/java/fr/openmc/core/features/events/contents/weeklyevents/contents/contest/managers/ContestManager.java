package fr.openmc.core.features.events.contents.weeklyevents.contents.contest.managers;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import fr.openmc.api.menulib.Menu;
import fr.openmc.core.CommandsManager;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.bootstrap.features.Feature;
import fr.openmc.core.bootstrap.features.annotations.Credit;
import fr.openmc.core.bootstrap.features.types.DatabaseFeature;
import fr.openmc.core.bootstrap.features.types.LoadAfterItemsAdder;
import fr.openmc.core.bootstrap.integration.DatabaseManager;
import fr.openmc.core.bootstrap.integration.OMCLogger;
import fr.openmc.core.features.economy.EconomyManager;
import fr.openmc.core.features.events.contents.weeklyevents.contents.contest.commands.ContestCommand;
import fr.openmc.core.features.events.contents.weeklyevents.contents.contest.events.ContestEndEvent;
import fr.openmc.core.features.events.contents.weeklyevents.contents.contest.listeners.ContestIntractEvents;
import fr.openmc.core.features.events.contents.weeklyevents.contents.contest.menu.ContributionMenu;
import fr.openmc.core.features.events.contents.weeklyevents.contents.contest.menu.MoreInfoMenu;
import fr.openmc.core.features.events.contents.weeklyevents.contents.contest.menu.TradeMenu;
import fr.openmc.core.features.events.contents.weeklyevents.contents.contest.menu.VoteMenu;
import fr.openmc.core.features.events.contents.weeklyevents.contents.contest.models.ContestData;
import fr.openmc.core.features.events.contents.weeklyevents.contents.contest.models.ContestPlayer;
import fr.openmc.core.features.leaderboards.LeaderboardManager;
import fr.openmc.core.features.mailboxes.MailboxManager;
import fr.openmc.core.hooks.WorldGuardHook;
import fr.openmc.core.hooks.itemsadder.ItemsAdderHook;
import fr.openmc.core.registry.items.CustomItemRegistry;
import fr.openmc.core.utils.bukkit.ParticleUtils;
import fr.openmc.core.utils.cache.CacheOfflinePlayer;
import fr.openmc.core.utils.text.ColorUtils;
import fr.openmc.core.utils.text.messages.TranslationManager;
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
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static fr.openmc.core.features.mailboxes.utils.MailboxUtils.getHoverEvent;

@Credit(developers = {"iambibi_"}, graphist = {"Gexary", "Tfloa"})
public class ContestManager extends Feature implements DatabaseFeature, LoadAfterItemsAdder {

    public static ContestData data;
    public static Map<UUID, ContestPlayer> dataPlayer = new HashMap<>();

    private static final List<String> colorContest = Arrays.asList(
            "WHITE","YELLOW","LIGHT_PURPLE","RED","AQUA","GREEN","BLUE",
            "DARK_GRAY","GRAY","GOLD","DARK_PURPLE","DARK_AQUA","DARK_RED",
            "DARK_GREEN","DARK_BLUE"
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
    @Override
    public void init() {
        // ** LISTENERS **
        if (ItemsAdderHook.isEnable()) {
            OMCPlugin.registerEvents(
                    new ContestIntractEvents()
            );
        }

        // ** COMMANDS **
        CommandsManager.getHandler().register(
                new ContestCommand()
        );

        // ** MANAGER EXTERNE **
        TradeYMLManager.init();

        // ** LOAD DATAS **
        initContestData();
        loadContestPlayerData();

        // ** PARTICLE REGION **
        if (WorldGuardHook.isEnable()) {
            ParticleUtils.spawnContestParticlesInRegion("spawn", Bukkit.getWorld("world"), 10, 70, 135);
        }
    }

    @Override
    public void save() {
        ContestManager.saveContestData();
        ContestManager.saveContestPlayerData();
    }

    private static Dao<ContestData, Integer> contestDao;
    private static Dao<ContestPlayer, UUID> playerDao;

    /**
     * Initialise la base de données pour les contests et les joueurs
     * (création des tables si elles n’existent pas encore)
     */
    @Override
    public void initDB(ConnectionSource connectionSource) throws SQLException {
        TableUtils.createTableIfNotExists(connectionSource, ContestData.class);
        contestDao = DaoManager.createDao(connectionSource, ContestData.class);

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
                data = new ContestData("Mayonnaise", "Ketchup", "YELLOW", "RED", 0, 0);
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
        OMCLogger.info("Saving contest player data...");
        dataPlayer.forEach((player, data) -> {
            try {
                playerDao.createOrUpdate(data);
            } catch (SQLException e) {
                OMCLogger.warn("Failed to save contest player data for {}: {}", player, e.getMessage(), e);
            }
        });
        OMCLogger.info("Contest player data saved successfully.");
    }

    /**
     * Vide les tables relatives au contest (Contest et ContestPlayer) dans la DB.
     */
    public static void clearDB() {
        try {
            TableUtils.clearTable(DatabaseManager.getConnectionSource(), ContestData.class);
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
        ParticleUtils.color1 = null;
        ParticleUtils.color2 = null;

        Bukkit.broadcast(TranslationManager.translation("feature.events.contest.broadcast.phase1"));

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

        Bukkit.broadcast(TranslationManager.translation("feature.events.contest.broadcast.phase2"));

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

        Bukkit.broadcast(TranslationManager.translation("feature.events.contest.broadcast.phase3"));

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
        baseBookMeta.setTitle(TranslationManager.translationString("feature.events.contest.book.title"));
        baseBookMeta.setAuthor(TranslationManager.translationString("feature.events.contest.book.author"));

        List<Component> lore = TranslationManager.translationLore(
                "feature.events.contest.book.lore",
                Component.text(camp1Name).decoration(TextDecoration.ITALIC, false).color(color1),
                Component.text(camp2Name).decoration(TextDecoration.ITALIC, false).color(color2)
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

        baseBookMeta.addPages(TranslationManager.translation(
                "feature.events.contest.book.page.global",
                Component.text(campWinner).decoration(TextDecoration.ITALIC, false).color(colorWinner),
                Component.text(voteWinnerTaux + "%").decoration(TextDecoration.ITALIC, false),
                Component.text(pointsWinnerTaux + "%").decoration(TextDecoration.ITALIC, false),
                Component.text(campLooser).decoration(TextDecoration.ITALIC, false).color(colorLooser),
                Component.text(voteLooserTaux + "%").decoration(TextDecoration.ITALIC, false),
                Component.text(pointsLooserTaux + "%").decoration(TextDecoration.ITALIC, false),
                Component.text(multiplicateurPoint).decoration(TextDecoration.ITALIC, false).color(NamedTextColor.AQUA)
        ));


        // 2EME PAGE - LES CLASSEMENTS
        final Component[] leaderboard = {TranslationManager.translation("feature.events.contest.book.page.ranking.title")};

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
                    .append(Component.text(" - ", NamedTextColor.DARK_GRAY))
                    .append(Component.text(dataOrdered.getPoints()).color(NamedTextColor.AQUA));
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

            OfflinePlayer offlinePlayer = CacheOfflinePlayer.getOfflinePlayer(uuid);
            int points = dataPlayer1.getPoints();

            if (offlinePlayer.isOnline()) {
                Player player = Bukkit.getPlayer(uuid);

                if (player != null) {
                    Component messageMail = TranslationManager.translation("feature.events.contest.mail.received")
                            .appendNewline()
                            .append(TranslationManager.translation("feature.events.contest.mail.click"))
                            .clickEvent(ClickEvent.runCommand("mailbox"))
                            .hoverEvent(getHoverEvent(TranslationManager.translationString("feature.events.contest.mail.hover")))
                            .append(TranslationManager.translation("feature.events.contest.mail.open_mailbox"));

                    player.sendMessage(messageMail);
                }
            }

            String playerCampName = data.get("camp" + dataPlayer1.getCamp());
            NamedTextColor playerCampColor = ColorUtils.getReadableColor(dataPlayer1.getColor());
            String playerTitleContest = ContestPlayerManager.getTitleWithPoints(points) + playerCampName; // ex. Novice en + Moutarde

            bookMetaPlayer.addPages(TranslationManager.translation(
                    "feature.events.contest.book.page.personal",
                    Component.text(playerCampName).decoration(TextDecoration.ITALIC, false).color(playerCampColor),
                    Component.text(playerTitleContest).decoration(TextDecoration.ITALIC, false).color(playerCampColor),
                    Component.text(rank.get()),
                    Component.text(points).color(NamedTextColor.AQUA)
            ));

            List<ItemStack> itemListRewards = new ArrayList<>();
            Component textRewards = TranslationManager.translation("feature.events.contest.book.page.rewards.title");

            int money;
            int aywenite;

            double multiplicator = ContestPlayerManager.getMultiplicatorFromRank(
                    ContestPlayerManager.getRankContestFromOfflineInt(offlinePlayer));
            if (ContestPlayerManager.hasWinInCampFromOfflinePlayer(offlinePlayer)) {
                // Gagnant - ARGENT
                int moneyMin = 10000;
                int moneyMax = 12000;
                moneyMin = (int) (moneyMin * multiplicator);
                moneyMax = (int) (moneyMax * multiplicator);

                Random randomMoney = new Random();
                money = randomMoney.nextInt(moneyMin, moneyMax);
                EconomyManager.addBalance(offlinePlayer.getUniqueId(), money, "Récompense contest - Gagnant");
 
                // Gagnant - Aywenite
                int ayweniteMin = 40;
                int ayweniteMax = 60;
                ayweniteMin = (int) (ayweniteMin * multiplicator);
                ayweniteMax = (int) (ayweniteMax * multiplicator);
                Random randomAwyenite = new Random();
                aywenite = randomAwyenite.nextInt(ayweniteMin, ayweniteMax);
                
                // Gagnant - EVENT
                winners.add(offlinePlayer.getUniqueId());
            } else {
                // Perdant - ARGENT
                int moneyMin = 2000;
                int moneyMax = 4000;
                moneyMin = (int) (moneyMin * multiplicator);
                moneyMax = (int) (moneyMax * multiplicator);

                Random randomMoney = new Random();
                money = randomMoney.nextInt(moneyMin, moneyMax);
                EconomyManager.addBalance(offlinePlayer.getUniqueId(), money, "Récompense contest - Perdant");

                // Perdant - Aywenite
                int ayweniteMin = 20;
                int ayweniteMax = 25;
                ayweniteMin = (int) (ayweniteMin * multiplicator);
                ayweniteMax = (int) (ayweniteMax * multiplicator);
                Random randomAwyenite = new Random();
                aywenite = randomAwyenite.nextInt(ayweniteMin, ayweniteMax);
                
                // Perdant - EVENT
                losers.add(offlinePlayer.getUniqueId());
            }

            // PRINT REWARDS
            textRewards = textRewards
                    .appendNewline()
                    .append(TranslationManager.translation("feature.events.contest.book.page.rewards.money.prefix"))
                    .append(Component.text(money).color(NamedTextColor.GOLD))
                    .append(TranslationManager.translation("feature.events.contest.book.page.rewards.money.suffix"))
                    .appendNewline()
                    .append(TranslationManager.translation("feature.events.contest.book.page.rewards.aywenite.prefix"))
                    .append(Component.text(aywenite).color(NamedTextColor.LIGHT_PURPLE))
                    .append(TranslationManager.translation("feature.events.contest.book.page.rewards.aywenite.suffix"))
                    .appendNewline()
                    .append(TranslationManager.translation("feature.events.contest.book.page.rewards.boost.prefix"))
                    .append(Component.text(multiplicator).color(NamedTextColor.AQUA));

            bookMetaPlayer.addPages(textRewards);

            bookPlayer.setItemMeta(bookMetaPlayer);

            if (CustomItemRegistry.getByName("omc_items:aywenite") != null) {
                    ItemStack ayweniteItemStack = Objects.requireNonNull(CustomItemRegistry.getByName("omc_items:aywenite")).getBest();
                ayweniteItemStack.setAmount(aywenite);
                itemListRewards.add(ayweniteItemStack);
            }
            itemListRewards.add(bookPlayer);

            ItemStack[] rewards = itemListRewards.toArray(new ItemStack[0]);
            playerItemsMap.put(offlinePlayer, rewards);
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
        data = new ContestData(camp1, camp2, color1, color2, 0, 0);
    }
}
