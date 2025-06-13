package fr.openmc.core.features.city.mayor.managers;

import fr.openmc.api.cooldown.DynamicCooldownManager;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.city.CPermission;
import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.mayor.*;
import fr.openmc.core.features.city.mayor.listeners.JoinListener;
import fr.openmc.core.features.city.mayor.listeners.PhaseListener;
import fr.openmc.core.features.city.mayor.listeners.UrneListener;
import fr.openmc.core.features.city.mayor.perks.Perks;
import fr.openmc.core.features.city.mayor.perks.basic.*;
import fr.openmc.core.features.city.mayor.perks.event.*;
import fr.openmc.core.features.city.models.CityLaw;
import fr.openmc.core.features.city.models.Mayor;
import fr.openmc.core.features.city.models.MayorCandidate;
import fr.openmc.core.features.city.models.MayorConstant;
import fr.openmc.core.features.city.models.MayorVote;
import fr.openmc.core.utils.CacheOfflinePlayer;
import fr.openmc.core.utils.api.FancyNpcApi;
import fr.openmc.core.utils.api.ItemAdderApi;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;
import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Random;
import java.util.UUID;

public class MayorManager {
    public static int MEMBER_REQ_ELECTION = 3;

    public static final String TABLE_MAYOR = "city_mayor";
    public static final String TABLE_ELECTION = "city_election";
    public static final String TABLE_VOTE = "city_vote";
    public static final String TABLE_LAW = "city_law";
    public static final String TABLE_CONSTANTS = "mayor_constants";

    private static final List<NamedTextColor> LIST_MAYOR_COLOR = List.of(
            NamedTextColor.RED,
            NamedTextColor.GOLD,
            NamedTextColor.YELLOW,
            NamedTextColor.GREEN,
            NamedTextColor.DARK_GREEN,
            NamedTextColor.BLUE,
            NamedTextColor.AQUA,
            NamedTextColor.DARK_BLUE,
            NamedTextColor.DARK_PURPLE,
            NamedTextColor.LIGHT_PURPLE,
            NamedTextColor.WHITE,
            NamedTextColor.GRAY,
            NamedTextColor.DARK_GRAY);

    public static final DayOfWeek PHASE_1_DAY = DayOfWeek.TUESDAY;
    public static final DayOfWeek PHASE_2_DAY = DayOfWeek.THURSDAY;

    public static int phaseMayor;
    public static HashMap<String, Mayor> cityMayor = new HashMap<>();
    public static HashMap<String, CityLaw> cityLaws = new HashMap<>();
    public static Map<String, List<MayorCandidate>> cityElections = new HashMap<>();
    public static Map<String, List<MayorVote>> playerVote = new HashMap<>();

    private static final Random RANDOM = new Random();

    public MayorManager() {
        // LISTENERS
        new PhaseListener(OMCPlugin.getInstance());
        OMCPlugin.registerEvents(
                new JoinListener(),
                new RagePerk(),
                new MinerPerk(),
                new MascotFriendlyPerk(),
                new DemonFruitPerk(),
                new CityHunterPerk(),
                new AyweniterPerk(),
                new GPSTrackerPerk(),
                new SymbiosisPerk(),
                new ImpotCollection(),
                new AgriculturalEssorPerk(),
                new MineralRushPerk(),
                new MilitaryDissuasion(),
                new IdyllicRain());
        if (ItemAdderApi.hasItemAdder()) {
            OMCPlugin.registerEvents(
                    new UrneListener());
        }
        if (FancyNpcApi.hasFancyNpc()) {
            OMCPlugin.registerEvents(
                    new NPCManager());
        }

        loadMayorConstant();
        loadCityMayors();
        loadMayorCandidates();
        loadPlayersVote();
        loadCityLaws();

        // new BukkitRunnable() {
        // @Override
        // public void run() {
        // Bukkit.getLogger().info("===== MayorManager Debug =====");
        //
        // Bukkit.getLogger().info("City Mayors:");
        // System.out.println(cityMayor);
        // for (Map.Entry<City, Mayor> entry : cityMayor.entrySet()) {
        // Bukkit.getLogger().info(entry.getKey() + " -> " + entry.getValue().getName()
        // + " " + entry.getValue().getUUID() + " " + entry.getValue().getIdPerk1()+ " "
        // + entry.getValue().getIdPerk2()+ " " + entry.getValue().getIdPerk3());
        // }
        //
        // Bukkit.getLogger().info("City Law:");
        // System.out.println(cityLaws);
        // for (Map.Entry<City, CityLaw> entry : cityLaws.entrySet()) {
        // Bukkit.getLogger().info(entry.getKey() + " -> war^" +
        // entry.getValue().getWarp() + " PVP " + entry.getValue().isPvp());
        // }
        //
        // Bukkit.getLogger().info("City Elections:");
        // for (Map.Entry<City, List<MayorCandidate>> entry : cityElections.entrySet())
        // {
        // Bukkit.getLogger().info(entry.getKey() + " -> " + entry.getValue());
        // }
        //
        // Bukkit.getLogger().info("Player Votes:");
        // for (Map.Entry<City, List<MayorVote>> entry : playerVote.entrySet()) {
        // Bukkit.getLogger().info(entry.getKey() + " -> " + entry.getValue());
        // }
        //
        // Bukkit.getLogger().info("================================");
        // }
        // }.runTaskTimer(OMCPlugin.ggetInstance, 0, 600L); // 600 ticks = 30 secondes
    }

    private static Dao<Mayor, String> mayorsDao;
    private static Dao<MayorCandidate, UUID> candidatesDao;
    private static Dao<MayorVote, UUID> votesDao;
    private static Dao<CityLaw, String> lawsDao;
    private static Dao<MayorConstant, Integer> constantsDao;

    public static void init_db(ConnectionSource connectionSource) throws SQLException {
        TableUtils.createTableIfNotExists(connectionSource, Mayor.class);
        mayorsDao = DaoManager.createDao(connectionSource, Mayor.class);

        TableUtils.createTableIfNotExists(connectionSource, MayorCandidate.class);
        candidatesDao = DaoManager.createDao(connectionSource, MayorCandidate.class);

        TableUtils.createTableIfNotExists(connectionSource, MayorVote.class);
        votesDao = DaoManager.createDao(connectionSource, MayorVote.class);

        TableUtils.createTableIfNotExists(connectionSource, CityLaw.class);
        lawsDao = DaoManager.createDao(connectionSource, CityLaw.class);

        TableUtils.createTableIfNotExists(connectionSource, MayorConstant.class);
        constantsDao = DaoManager.createDao(connectionSource, MayorConstant.class);
    }

    // Load and Save Data Methods
    public static void loadMayorConstant() {
        try {
            MayorConstant constant = constantsDao.queryForFirst();
            if (constant == null) {
                constant = new MayorConstant(1);
                constantsDao.create(constant);
            }

            phaseMayor = constant.getPhase();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void saveMayorConstant() {
        try {
            constantsDao.createOrUpdate(new MayorConstant(phaseMayor));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void loadCityMayors() {
        try {
            List<Mayor> mayors = mayorsDao.queryForAll();

            mayors.forEach(mayor -> cityMayor.put(mayor.getCity(), mayor));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void saveCityMayors() {
        cityMayor.forEach((city, mayor) -> {
            try {
                mayorsDao.createOrUpdate(mayor);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public static void loadMayorCandidates() {
        try {
            List<MayorCandidate> candidates = candidatesDao.queryForAll();

            candidates.forEach(candidate -> {
                cityElections.computeIfAbsent(candidate.getCity(), k -> new ArrayList<>()).add(candidate);
            });
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void saveMayorCandidates() {
        cityElections.forEach(
                (city, candidates) -> candidates.forEach(candidate -> {
                    try {
                        candidatesDao.createOrUpdate(candidate);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }));
    }

    public static void loadPlayersVote() {
        try {
            votesDao.queryForAll().forEach(
                    vote -> playerVote.computeIfAbsent(vote.getCity().getUUID(), k -> new ArrayList<>()).add(vote));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void savePlayersVote() {
        playerVote.forEach((city, votes) -> votes.forEach(vote -> {
            try {
                votesDao.createOrUpdate(vote);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }));
    }

    public static void loadCityLaws() {
        try {
            List<CityLaw> laws = lawsDao.queryForAll();

            laws.forEach(law -> cityLaws.put(law.getCity(), law));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void saveCityLaws() {
        cityLaws.forEach((city, law) -> {
            try {
                lawsDao.createOrUpdate(law);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public static void removeCity(City city) throws SQLException {
        DeleteBuilder<Mayor, String> mayorsDelete = mayorsDao.deleteBuilder();
        mayorsDelete.where().eq("city", city.getUUID());
        mayorsDao.delete(mayorsDelete.prepare());
        cityMayor.remove(city.getUUID());

        DeleteBuilder<MayorCandidate, UUID> candidatesDelete = candidatesDao.deleteBuilder();
        candidatesDelete.where().eq("city", city.getUUID());
        candidatesDao.delete(candidatesDelete.prepare());
        cityElections.remove(city.getUUID());

        DeleteBuilder<MayorVote, UUID> votesDelete = votesDao.deleteBuilder();
        votesDelete.where().eq("city", city.getUUID());
        votesDao.delete(votesDelete.prepare());
        playerVote.remove(city.getUUID());

        DeleteBuilder<CityLaw, String> lawsDelete = lawsDao.deleteBuilder();
        lawsDelete.where().eq("city", city.getUUID());
        lawsDao.delete(lawsDelete.prepare());
        cityLaws.remove(city.getUUID());
    }

    // setup elections
    public static void initPhase1() throws SQLException {
        // ---OUVERTURE DES ELECTIONS---
        phaseMayor = 1;

        DynamicCooldownManager.clear("city:agricultural_essor");
        DynamicCooldownManager.clear("city:mineral_rush");

        // On vide toutes les tables
        Bukkit.getScheduler().runTaskAsynchronously(OMCPlugin.getInstance(), () -> {
            try {
                TableUtils.dropTable(mayorsDao, false);
                TableUtils.dropTable(candidatesDao, false);
                TableUtils.dropTable(votesDao, false);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
        HashMap<String, Mayor> copyCityMayor = cityMayor;
        cityMayor = new HashMap<>();
        cityElections = new HashMap<>();
        playerVote = new HashMap<>();
        for (City city : CityManager.getCities()) {
            // PERKS INIT
            for (UUID uuid : city.getMembers()) {
                OfflinePlayer offlinePlayer = CacheOfflinePlayer.getOfflinePlayer(uuid);
                if (offlinePlayer.isOnline()) {
                    Player player = offlinePlayer.getPlayer();
                    // Fou de Rage
                    if (PerkManager.hasPerk(copyCityMayor.get(city.getUUID()), Perks.FOU_DE_RAGE.getId())) {
                        player.removePotionEffect(PotionEffectType.STRENGTH);
                        player.removePotionEffect(PotionEffectType.RESISTANCE);
                    }

                    // Mineur Dévoué
                    if (PerkManager.hasPerk(copyCityMayor.get(city.getUUID()), Perks.MINER.getId())) {
                        MinerPerk.updatePlayerEffects(player);
                    }

                    // Mascotte de Compagnie
                    if (PerkManager.hasPerk(copyCityMayor.get(city.getUUID()), Perks.MASCOTS_FRIENDLY.getId())) {
                        MascotFriendlyPerk.updatePlayerEffects(player);
                    }

                    // Fruit du Démon
                    if (PerkManager.hasPerk(copyCityMayor.get(city.getUUID()), Perks.FRUIT_DEMON.getId())) {
                        DemonFruitPerk.removeReachBonus(player);
                    }
                }
            }

            if (city.getMembers().size() >= MEMBER_REQ_ELECTION) {
                createMayor(null, null, city, null, null, null, null, ElectionType.ELECTION);
            }
            createMayor(null, null, city, null, null, null, null, ElectionType.OWNER_CHOOSE);

        }

        NPCManager.updateAllNPCS();

        Bukkit.broadcast(Component.text("""
                §8§m                                                     §r
                §7
                §3§lMAIRE!§r §7Les Elections sont ouvertes !§7
                §8§oPrésentez vous, votez pour des maires, ...
                §8§oRegardez si vous avez assez de membres!
                §7
                §8§m                                                     §r"""));
    }

    public static void initPhase2() {
        Bukkit.getLogger().info("MAYOR - INIT PHASE 2");
        phaseMayor = 2;

        // TRAITEMENT DE CHAQUE VILLE - Complexité de O(n log(n))
        for (City city : CityManager.getCities()) {
            Bukkit.getLogger().info("- City : " + city.getName());
            runSetupMayor(city);

            for (UUID uuid : city.getMembers()) {
                OfflinePlayer offlinePlayer = CacheOfflinePlayer.getOfflinePlayer(uuid);
                if (offlinePlayer.isOnline()) {
                    Player player = offlinePlayer.getPlayer();
                    // Mineur Dévoué
                    if (PerkManager.hasPerk(city.getMayor(), Perks.MINER.getId())) {
                        MinerPerk.updatePlayerEffects(player);
                    }

                    // Mascotte de Compagnie
                    if (PerkManager.hasPerk(city.getMayor(), Perks.MASCOTS_FRIENDLY.getId())) {
                        MascotFriendlyPerk.updatePlayerEffects(player);
                    }

                    // Fruit du Démon
                    if (PerkManager.hasPerk(city.getMayor(), Perks.FRUIT_DEMON.getId())) {
                        DemonFruitPerk.applyReachBonus(player);
                    }
                }
            }
        }

        NPCManager.updateAllNPCS();

        Bukkit.broadcast(Component.text("""
                §8§m                                                     §r
                §7
                §3§lMAIRE!§r §7Vos Réformes sont actives !§7
                §8§oFaites vos stratégies, farmez, et pleins d'autres choses !
                §7
                §8§m                                                     §r"""));
    }

    /**
     * Create a new mayor for the city with the given perks and color.
     *
     * @param city The city to update mayor
     */
    public static void runSetupMayor(City city) {
        UUID ownerUUID = city.getPlayerWithPermission(CPermission.OWNER);
        String ownerName = CacheOfflinePlayer.getOfflinePlayer(ownerUUID).getName();
        Mayor mayor = city.getMayor();

        if (city.getElectionType() == ElectionType.OWNER_CHOOSE) {
            // si maire a pas choisis les perks
            if ((mayor.getIdPerk1() == 0) && (mayor.getIdPerk2() == 0) && (mayor.getIdPerk3() == 0)) {
                NamedTextColor color = getRandomMayorColor();
                List<Perks> perks = PerkManager.getRandomPerksAll();
                createMayor(ownerName, ownerUUID, city, perks.getFirst(), perks.get(1), perks.get(2), color,
                        ElectionType.OWNER_CHOOSE);
            }
        } else {
            // si owner a pas choisi perk event
            if (mayor.getIdPerk1() == 0) {
                mayor.setIdPerk1(PerkManager.getRandomPerkEvent().getId());
            }

            if (cityElections.containsKey(city.getUUID())) { // si y'a des maires qui se sont présenter
                List<MayorCandidate> candidates = cityElections.get(city.getUUID());

                // Code fait avec ChatGPT pour avoir une complexité de O(n log(n)) au lieu de
                // 0(n²)
                PriorityQueue<MayorCandidate> candidateQueue = new PriorityQueue<>(
                        Comparator.comparingInt(MayorCandidate::getVote).reversed());
                candidateQueue.addAll(candidates);

                MayorCandidate mayorWinner = candidateQueue.peek();
                Perks perk1 = PerkManager.getPerkById(mayor.getIdPerk1());
                Perks perk2 = PerkManager.getPerkById(mayorWinner.getIdChoicePerk2());
                Perks perk3 = PerkManager.getPerkById(mayorWinner.getIdChoicePerk3());

                createMayor(mayorWinner.getName(), mayorWinner.getId(), city, perk1, perk2, perk3,
                        mayorWinner.getCandidateColor(), ElectionType.ELECTION);

            } else {
                // personne s'est présenté, owner = maire
                NamedTextColor color = getRandomMayorColor();
                List<Perks> perks = PerkManager.getRandomPerksBasic();
                createMayor(ownerName, ownerUUID, city, PerkManager.getPerkById(mayor.getIdPerk1()), perks.getFirst(),
                        perks.get(1), color, ElectionType.ELECTION);

            }
        }

        Bukkit.getScheduler().runTaskAsynchronously(OMCPlugin.getInstance(), () -> {
            try {
                DeleteBuilder<MayorCandidate, UUID> candidatesDelete = candidatesDao.deleteBuilder();
                candidatesDelete.where().eq("city", city.getUUID());
                candidatesDao.delete(candidatesDelete.prepare());

                DeleteBuilder<MayorVote, UUID> votesDelete = votesDao.deleteBuilder();
                votesDelete.where().eq("city", city.getUUID());
                votesDao.delete(votesDelete.prepare());
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });

        // on supprime donc les elections de la ville ou le maire a été élu
        cityElections.remove(city.getUUID());
        // on supprime donc les votes de la ville ou le maire a été élu
        playerVote.remove(city.getUUID());
    }

    /**
     * Create a new candidate for the city with the given perks and color.
     *
     * @param city      The city to add candidate
     * @param candidate The candidate to add
     */
    public static void createCandidate(City city, MayorCandidate candidate) {
        List<MayorCandidate> candidates = cityElections.computeIfAbsent(city.getUUID(), key -> new ArrayList<>());

        candidates.add(candidate);
    }

    /**
     * Get the candidate for the player in the city.
     *
     * @param player The player to get candidate
     */
    public static MayorCandidate getCandidate(UUID player) {
        for (List<MayorCandidate> candidates : cityElections.values()) {
            for (MayorCandidate candidate : candidates) {
                if (candidate.getId().equals(player)) {
                    return candidate;
                }
            }
        }

        return null;
    }

    /**
     * Has the player candidated for the city.
     *
     * @param player The player to check
     */
    public static boolean hasCandidated(Player player) {
        City playerCity = CityManager.getPlayerCity(player.getUniqueId());

        if (cityElections.get(playerCity.getUUID()) == null)
            return false;

        return cityElections.get(playerCity.getUUID())
                .stream()
                .anyMatch(candidate -> candidate.getId().equals(player.getUniqueId()));
    }

    /**
     * Remove the player from the vote list.
     *
     * @param player The player to remove vote
     */
    public static void removeVotePlayer(Player player) {
        playerVote.forEach((city, votes) -> votes.removeIf(vote -> vote.getVoter().equals(player.getUniqueId())));
    }

    /**
     * Vote a candidate for the player in the city.
     *
     * @param playerCity The city where player are
     * @param player     The player who vote
     * @param candidate  The candidate to vote
     */
    public static void voteCandidate(City playerCity, Player player, MayorCandidate candidate) {
        candidate.setVote(candidate.getVote() + 1);
        List<MayorVote> votes = playerVote.computeIfAbsent(playerCity.getUUID(), key -> new ArrayList<>());

        votes.add(new MayorVote(playerCity.getUUID(), player.getUniqueId(), candidate));
    }

    /**
     * Check if the player has voted for the city.
     *
     * @param player The player to check
     */
    public static boolean hasVoted(Player player) {
        City playerCity = CityManager.getPlayerCity(player.getUniqueId());

        if (playerVote.get(playerCity.getUUID()) == null)
            return false;

        return playerVote.get(playerCity.getUUID())
                .stream()
                .anyMatch(mayorVote -> mayorVote.getVoter().equals(player.getUniqueId()));
    }

    /**
     * Get the player vote for the city.
     *
     * @param player The player to get vote
     */
    public static MayorCandidate getPlayerVote(Player player) {
        for (List<MayorVote> votes : playerVote.values()) {
            for (MayorVote vote : votes) {
                if (vote.getVoter().equals(player.getUniqueId())) {
                    return vote.getCandidate();
                }
            }
        }

        return null;
    }

    /**
     * Check if the owner has a choice perk.
     *
     * @param player The player to check
     */
    public static boolean hasChoicePerkOwner(Player player) {
        City playerCity = CityManager.getPlayerCity(player.getUniqueId());

        Mayor mayor = cityMayor.get(playerCity.getUUID());
        if (mayor == null)
            return false;

        return mayor.getIdPerk1() != 0;
    }

    /**
     * Set the perk for the owner.
     *
     * @param city  The city to set perk
     * @param perk1 The perk to set
     */
    public static void put1Perk(City city, Perks perk1) {
        Mayor mayor = cityMayor.get(city.getUUID());
        if (mayor != null) {
            mayor.setIdPerk1(perk1.getId());
        } else { // au cas ou meme si théoriquement c impossible
            cityMayor.put(city.getUUID(),
                    new Mayor(city.getUUID(), null, null, null, perk1.getId(), 0, 0, city.getElectionType()));
        }
    }

    /**
     * Create a new mayor for the city with the given perks and color.
     *
     * @param playerName The name of the mayor elected
     * @param playerUUID The UUID of the mayor elected
     * @param city       The city to create mayor
     * @param perk1      The first perk of the mayor
     * @param perk2      The second perk of the mayor
     * @param perk3      The third perk of the mayor
     * @param color      The color of the mayor
     * @param type       The type of the election
     */
    public static void createMayor(String playerName, UUID playerUUID, City city, Perks perk1, Perks perk2, Perks perk3,
            NamedTextColor color, ElectionType type) {
        Mayor mayor = cityMayor.get(city.getUUID());
        int idPerk1 = perk1 != null ? perk1.getId() : 0;
        int idPerk2 = perk2 != null ? perk2.getId() : 0;
        int idPerk3 = perk3 != null ? perk3.getId() : 0;
        if (mayor != null) {
            mayor.setName(playerName);
            mayor.setUuid(playerUUID);
            mayor.setMayorColor(color);
            mayor.setIdPerk1(idPerk1);
            mayor.setIdPerk2(idPerk2);
            mayor.setIdPerk3(idPerk3);
            mayor.setElectionType(city.getElectionType());
        } else { // au cas ou meme si c théoriquement impossible (on défini tous les maires a la
                 // phase 1 et on le crée quand on crée la ville)
            cityMayor.put(city.getUUID(),
                    new Mayor(city.getUUID(), playerName, playerUUID, color, idPerk1, idPerk2, idPerk3, type));
        }
    }

    /**
     * Get random mayor color from the list.
     */
    public static NamedTextColor getRandomMayorColor() {
        return LIST_MAYOR_COLOR.get(RANDOM.nextInt(LIST_MAYOR_COLOR.size()));
    }

    /**
     * Create a new city law for the city with the given pvp and warp.
     *
     * @param city         The city to create law
     * @param pvp          The pvp of the city
     * @param locationWarp The warp location of the city
     */
    public static void createCityLaws(City city, boolean pvp, Location locationWarp) {
        CityLaw laws = city.getLaw();
        if (laws != null) {
            laws.setPvp(pvp);
            laws.setWarp(locationWarp);

        } else { // au cas ou meme si c théoriquement impossible (on défini tous les maires a la
                 // phase 1 et on le crée quand on crée la ville)
            cityLaws.put(city.getUUID(), new CityLaw(city.getUUID(), pvp, locationWarp));
        }
    }
}
