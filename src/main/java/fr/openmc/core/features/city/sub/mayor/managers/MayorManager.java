package fr.openmc.core.features.city.sub.mayor.managers;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import fr.openmc.api.cooldown.DynamicCooldownManager;
import fr.openmc.api.hooks.FancyNpcsHook;
import fr.openmc.api.hooks.ItemsAdderHook;
import fr.openmc.core.CommandsManager;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.CityPermission;
import fr.openmc.core.features.city.sub.mayor.ElectionType;
import fr.openmc.core.features.city.sub.mayor.commands.AdminMayorCommands;
import fr.openmc.core.features.city.sub.mayor.commands.MayorCommands;
import fr.openmc.core.features.city.sub.mayor.listeners.JoinListener;
import fr.openmc.core.features.city.sub.mayor.listeners.PhaseListener;
import fr.openmc.core.features.city.sub.mayor.listeners.UrneListener;
import fr.openmc.core.features.city.sub.mayor.models.*;
import fr.openmc.core.features.city.sub.mayor.perks.Perks;
import fr.openmc.core.features.city.sub.mayor.perks.basic.*;
import fr.openmc.core.features.city.sub.mayor.perks.event.*;
import fr.openmc.core.features.city.sub.milestone.rewards.FeaturesRewards;
import fr.openmc.core.utils.cache.CacheOfflinePlayer;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

import java.sql.SQLException;
import java.time.DayOfWeek;
import java.util.*;

public class MayorManager {
    @Getter
    private static ConnectionSource connectionSource;

    public static final int MEMBER_REQUEST_ELECTION = 3;

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
    public static Map<UUID, Mayor> cityMayor = new HashMap<>();
    public static final Map<UUID, CityLaw> cityLaws = new HashMap<>();
    public static Map<UUID, List<MayorCandidate>> cityElections = new HashMap<>();
    public static Map<UUID, List<MayorVote>> playerVote = new HashMap<>();

    private static final Random RANDOM = new Random();

    public static void init() {
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

        if (ItemsAdderHook.isHasItemAdder()) {
            OMCPlugin.registerEvents(
                    new UrneListener());
        }
        if (FancyNpcsHook.isHasFancyNpc()) {
            OMCPlugin.registerEvents(
                    new NPCManager());
        }

        CommandsManager.getHandler().register(
                new MayorCommands(),
                new AdminMayorCommands()
        );

        loadMayorConstant();
        loadCityMayors();
        loadMayorCandidates();
        loadPlayersVote();
        loadCityLaws();
    }

    private static Dao<Mayor, String> mayorsDao;
    private static Dao<MayorCandidate, UUID> candidatesDao;
    private static Dao<MayorVote, UUID> votesDao;
    private static Dao<CityLaw, String> lawsDao;
    private static Dao<MayorConstant, Integer> constantsDao;

    public static void initDB(ConnectionSource connectionSource) throws SQLException {
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

        MayorManager.connectionSource = connectionSource;
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
            throw new RuntimeException(e);
        }
    }

    public static void saveMayorConstant() {
        try {
            constantsDao.createOrUpdate(new MayorConstant(phaseMayor));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void loadCityMayors() {
        try {
            List<Mayor> mayors = mayorsDao.queryForAll();

            mayors.forEach(mayor -> cityMayor.put(mayor.getCityUUID(), mayor));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void saveCityMayors() {
        cityMayor.forEach((city, mayor) -> {
            try {
                mayorsDao.createOrUpdate(mayor);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public static void loadMayorCandidates() {
        try {
            List<MayorCandidate> candidates = candidatesDao.queryForAll();

            candidates.forEach(candidate -> {
                cityElections.computeIfAbsent(candidate.getCityUUID(), k -> new ArrayList<>()).add(candidate);
            });
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void saveMayorCandidates() {
        cityElections.forEach(
                (city, candidates) -> candidates.forEach(candidate -> {
                    try {
                        candidatesDao.createOrUpdate(candidate);
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                }));
    }

    public static void loadPlayersVote() {
        try {
            votesDao.queryForAll().forEach(
                    vote -> playerVote.computeIfAbsent(vote.getCity().getUniqueId(), k -> new ArrayList<>()).add(vote));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void savePlayersVote() {
        playerVote.forEach((city, votes) -> votes.forEach(vote -> {
            try {
                votesDao.createOrUpdate(vote);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }));
    }

    public static void loadCityLaws() {
        try {
            List<CityLaw> laws = lawsDao.queryForAll();

            laws.forEach(law -> cityLaws.put(law.getCityUUID(), law));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void saveCityLaws() {
        cityLaws.forEach((city, law) -> {
            try {
                lawsDao.createOrUpdate(law);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public static void removeCity(City city) throws SQLException {
        DeleteBuilder<Mayor, String> mayorsDelete = mayorsDao.deleteBuilder();
        mayorsDelete.where().eq("city_uuid", city.getUniqueId());
        mayorsDao.delete(mayorsDelete.prepare());
        cityMayor.remove(city.getUniqueId());

        DeleteBuilder<MayorCandidate, UUID> candidatesDelete = candidatesDao.deleteBuilder();
        candidatesDelete.where().eq("city_uuid", city.getUniqueId());
        candidatesDao.delete(candidatesDelete.prepare());
        cityElections.remove(city.getUniqueId());

        DeleteBuilder<MayorVote, UUID> votesDelete = votesDao.deleteBuilder();
        votesDelete.where().eq("city_uuid", city.getUniqueId());
        votesDao.delete(votesDelete.prepare());
        playerVote.remove(city.getUniqueId());

        DeleteBuilder<CityLaw, String> lawsDelete = lawsDao.deleteBuilder();
        lawsDelete.where().eq("city_uuid", city.getUniqueId());
        lawsDao.delete(lawsDelete.prepare());
        cityLaws.remove(city.getUniqueId());
    }

    // setup elections
    public static void initPhase1() {
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

                TableUtils.createTableIfNotExists(connectionSource, Mayor.class);
                mayorsDao = DaoManager.createDao(connectionSource, Mayor.class);

                TableUtils.createTableIfNotExists(connectionSource, MayorCandidate.class);
                candidatesDao = DaoManager.createDao(connectionSource, MayorCandidate.class);

                TableUtils.createTableIfNotExists(connectionSource, MayorVote.class);
                votesDao = DaoManager.createDao(connectionSource, MayorVote.class);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });

        Map<UUID, Mayor> copyCityMayor = cityMayor;
        cityMayor = new HashMap<>();
        cityElections = new HashMap<>();
        playerVote = new HashMap<>();
        for (City city : CityManager.getCities()) {
            if (!FeaturesRewards.hasUnlockFeature(city, FeaturesRewards.Feature.MAYOR)) continue;

            initCityPhase1(city, copyCityMayor);
        }

        NPCManager.updateAllNPCS();

        Bukkit.broadcast(Component.text("""
                §8§m                                                     §r
                §7
                §3§lMAIRE !§r §7Les élections sont ouvertes !§7
                §8§oPrésentez vous, votez pour des maires, ...
                §8§oRegardez si vous avez assez de membres !
                §7
                §8§m                                                     §r"""
        ));
    }

    public static void initPhase2() {
        OMCPlugin.getInstance().getSLF4JLogger().debug("MAYOR - INIT PHASE 2");
        phaseMayor = 2;

        // TRAITEMENT DE CHAQUE VILLE - Complexité de O(n log(n))
        for (City city : CityManager.getCities()) {
            if (!FeaturesRewards.hasUnlockFeature(city, FeaturesRewards.Feature.MAYOR)) continue;

            initCityPhase2(city);
        }

        NPCManager.updateAllNPCS();

        Bukkit.broadcast(Component.text("""
                §8§m                                                     §r
                §7
                §3§lMAIRE !§r §7Vos réformes sont actives !§7
                §8§oFaites vos stratégies, farmez, et pleins d'autres choses !
                §7
                §8§m                                                     §r"""));
    }

    public static void initCityPhase1(City city, Map<UUID, Mayor> copyCityMayor) {
        // PERKS INIT
        if (copyCityMayor != null) {
            for (UUID uuid : city.getMembers()) {
                OfflinePlayer offlinePlayer = CacheOfflinePlayer.getOfflinePlayer(uuid);
                if (offlinePlayer.isOnline()) {
                    Player player = offlinePlayer.getPlayer();

                    if (player == null) continue;

                    Mayor oldMayor = copyCityMayor.get(city.getUniqueId());

                    // Fou de Rage
                    if (PerkManager.hasPerk(oldMayor, Perks.FOU_DE_RAGE.getId())) {
                        player.removePotionEffect(PotionEffectType.STRENGTH);
                        player.removePotionEffect(PotionEffectType.RESISTANCE);
                    }

                    // Mineur Dévoué
                    if (PerkManager.hasPerk(oldMayor, Perks.MINER.getId())) {
                        MinerPerk.updatePlayerEffects(player);
                    }

                    // Mascotte de Compagnie
                    if (PerkManager.hasPerk(oldMayor, Perks.MASCOTS_FRIENDLY.getId())) {
                        MascotFriendlyPerk.updatePlayerEffects(player);
                    }

                    // Fruit du Démon
                    if (PerkManager.hasPerk(oldMayor, Perks.FRUIT_DEMON.getId())) {
                        DemonFruitPerk.removeReachBonus(player);
                    }
                }
            }
        }

        if (city.getMembers().size() >= MEMBER_REQUEST_ELECTION) {
            createMayor(null, null, city, null, null, null, null, ElectionType.ELECTION);
        }
        createMayor(null, null, city, null, null, null, null, ElectionType.OWNER_CHOOSE);
    }

    public static void initCityPhase2(City city) {
        OMCPlugin.getInstance().getSLF4JLogger().debug("- City : {}", city.getName());
        runSetupMayor(city);

        for (UUID uuid : city.getMembers()) {
            OfflinePlayer offlinePlayer = CacheOfflinePlayer.getOfflinePlayer(uuid);
            if (offlinePlayer.isOnline()) {
                Player player = offlinePlayer.getPlayer();
                if (player == null) continue;
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

                // Fou de Rage
                if (PerkManager.hasPerk(city.getMayor(), Perks.FOU_DE_RAGE.getId())) {
                    City locCity = CityManager.getCityFromChunk(player.getLocation().getChunk());
                    RagePerk.updateEffect(locCity, player);
                }
            }
        }
    }

    /**
     * Create a new mayor for the city with the given perks and color.
     *
     * @param city The city to update mayor
     */
    public static void runSetupMayor(City city) {
        UUID ownerUUID = city.getPlayerWithPermission(CityPermission.OWNER);
        String ownerName = CacheOfflinePlayer.getOfflinePlayer(ownerUUID).getName();
        Mayor mayor = city.getMayor();

        if (city.getElectionType() == ElectionType.OWNER_CHOOSE) {
            // si maire n'a pas choisis les perks
            if ((mayor.getIdPerk1() == 0) && (mayor.getIdPerk2() == 0) && (mayor.getIdPerk3() == 0)) {
                NamedTextColor color = getRandomMayorColor();
                List<Perks> perks = PerkManager.getRandomPerksAll(city);
                if (perks.size() < 3) {
                OMCPlugin.getInstance().getSLF4JLogger().warn(
                        "No unlocked mayor perks combination found for city {} (OWNER_CHOOSE)",
                        city.getName()
                );
                return;
            }
                createMayor(ownerName, ownerUUID, city, perks.getFirst(), perks.get(1), perks.get(2), color,
                        ElectionType.OWNER_CHOOSE);
            }
        } else {
            if (cityElections.containsKey(city.getUniqueId())) { // s'il y a des maires qui se sont présenté
                List<MayorCandidate> candidates = cityElections.get(city.getUniqueId());

                // Code fait avec ChatGPT pour avoir une complexité de O(n log(n)) au lieu de
                // 0(n²)
                PriorityQueue<MayorCandidate> candidateQueue = new PriorityQueue<>(
                        Comparator.comparingInt(MayorCandidate::getVote).reversed());
                candidateQueue.addAll(candidates);

                MayorCandidate electedMayor = candidateQueue.peek();

                Perks perk1;
                if (mayor == null || (mayor.getIdPerk1() == 0)) {
                    perk1 = PerkManager.getRandomPerkEvent(city);
                } else {
                    perk1 = PerkManager.getPerkById(mayor.getIdPerk1());
                }
                Perks perk2 = PerkManager.getPerkById(electedMayor.getIdChoicePerk2());
                Perks perk3 = PerkManager.getPerkById(electedMayor.getIdChoicePerk3());

                createMayor(electedMayor.getName(), electedMayor.getCandidateUUID(), city, perk1, perk2, perk3,
                        electedMayor.getCandidateColor(), ElectionType.ELECTION);

            } else {
                // personne s'est présenté, owner = maire
                NamedTextColor color = getRandomMayorColor();
                List<Perks> perks = PerkManager.getRandomPerksBasic(city);
                  Perks perk1;
                  if (mayor == null || (mayor.getIdPerk1() == 0)) {
                      perk1 = PerkManager.getRandomPerkEvent(city);
                  } else {
                      perk1 = PerkManager.getPerkById(mayor.getIdPerk1());
                  }

                if (perk1 == null || perks.size() < 2) {
                    OMCPlugin.getInstance().getSLF4JLogger().warn(
                            "Unable to select unlocked mayor perks for city {} (ELECTION)",
                            city.getName()
                    );
                    return;
                }

                  createMayor(ownerName, ownerUUID, city, perk1, perks.getFirst(),
                          perks.get(1), color, ElectionType.ELECTION);

            }
        }

        Bukkit.getScheduler().runTaskAsynchronously(OMCPlugin.getInstance(), () -> {
            try {
                DeleteBuilder<MayorCandidate, UUID> candidatesDelete = candidatesDao.deleteBuilder();
                candidatesDelete.where().eq("city_uuid", city.getUniqueId());
                candidatesDao.delete(candidatesDelete.prepare());

                DeleteBuilder<MayorVote, UUID> votesDelete = votesDao.deleteBuilder();
                votesDelete.where().eq("city_uuid", city.getUniqueId());
                votesDao.delete(votesDelete.prepare());
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
        
        // on supprime donc les elections de la ville avec laquelle le maire a été élu
        cityElections.remove(city.getUniqueId());
        // on supprime donc les votes de la ville avec laquelle le maire a été élu
        playerVote.remove(city.getUniqueId());
    }

    /**
     * Create a new candidate for the city with the given perks and color.
     *
     * @param city      The city to add a candidate
     * @param candidate The candidate to add
     */
    public static void createCandidate(City city, MayorCandidate candidate) {
        List<MayorCandidate> candidates = cityElections.computeIfAbsent(city.getUniqueId(), key -> new ArrayList<>());

        candidates.add(candidate);
    }

    /**
     * Get the candidate for the player in the city.
     *
     * @param playerUUID The playerUUID to get a candidate
     */
    public static MayorCandidate getCandidate(UUID playerUUID) {
        for (List<MayorCandidate> candidates : cityElections.values()) {
            for (MayorCandidate candidate : candidates) {
                if (candidate.getCandidateUUID().equals(playerUUID)) {
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

        if (cityElections.get(playerCity.getUniqueId()) == null)
            return false;

        return cityElections.get(playerCity.getUniqueId())
                .stream()
                .anyMatch(candidate -> candidate.getCandidateUUID().equals(player.getUniqueId()));
    }

    /**
     * Remove the player from the vote list.
     *
     * @param player The player to remove a vote
     */
    public static void removeVotePlayer(Player player) {
        playerVote.forEach((city, votes) -> votes.removeIf(vote -> vote.getVoter().equals(player.getUniqueId())));
    }

    /**
     * Vote a candidate for the player in the city.
     *
     * @param playerCity The city where player are
     * @param player     The player who votes
     * @param candidate  The candidate to vote
     */
    public static void voteCandidate(City playerCity, Player player, MayorCandidate candidate) {
        candidate.setVote(candidate.getVote() + 1);
        List<MayorVote> votes = playerVote.computeIfAbsent(playerCity.getUniqueId(), key -> new ArrayList<>());

        votes.add(new MayorVote(playerCity.getUniqueId(), player.getUniqueId(), candidate));
    }

    /**
     * Check if the player has voted for the city.
     *
     * @param player The player to check
     */
    public static boolean hasVoted(Player player) {
        City playerCity = CityManager.getPlayerCity(player.getUniqueId());

        if (playerVote.get(playerCity.getUniqueId()) == null)
            return false;

        return playerVote.get(playerCity.getUniqueId())
                .stream()
                .anyMatch(mayorVote -> mayorVote.getVoter().equals(player.getUniqueId()) && mayorVote.getCandidate() != null);
    }

    /**
     * Get the player vote for the city.
     *
     * @param player The player to get a vote
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

        Mayor mayor = cityMayor.get(playerCity.getUniqueId());
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
        Mayor mayor = cityMayor.get(city.getUniqueId());
        if (mayor != null) {
            mayor.setIdPerk1(perk1.getId());
        } else { // au cas où meme si théoriquement c impossible
            cityMayor.put(city.getUniqueId(),
                    new Mayor(city.getUniqueId(), null, null, null, perk1.getId(), 0, 0, city.getElectionType()));
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
        Mayor mayor = cityMayor.get(city.getUniqueId());
        int idPerk1 = perk1 != null ? perk1.getId() : 0;
        int idPerk2 = perk2 != null ? perk2.getId() : 0;
        int idPerk3 = perk3 != null ? perk3.getId() : 0;
        if (mayor != null) {
            mayor.setName(playerName);
            mayor.setMayorUUID(playerUUID);
            mayor.setMayorColor(color);
            mayor.setIdPerk1(idPerk1);
            mayor.setIdPerk2(idPerk2);
            mayor.setIdPerk3(idPerk3);
            mayor.setElectionType(city.getElectionType());
        } else { // au cas où meme si c théoriquement impossible (ont défini tous les maires à la
                 // phase 1 et on le crée quand on crée la ville)
            cityMayor.put(city.getUniqueId(),
                    new Mayor(city.getUniqueId(), playerName, playerUUID, color, idPerk1, idPerk2, idPerk3, type));
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
            
        } else { // au cas où meme si c théoriquement impossible (ont défini tous les maires à la
                 // phase 1 et on le crée quand on crée la ville)
            cityLaws.put(city.getUniqueId(), new CityLaw(city.getUniqueId(), pvp, locationWarp));
        }
    }
}
