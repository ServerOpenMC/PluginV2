package fr.openmc.core.features.city.actions;

import com.sk89q.worldedit.math.BlockVector2;
import fr.openmc.api.cooldown.DynamicCooldownManager;
import fr.openmc.api.input.location.ItemInteraction;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.city.CPermission;
import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.CityType;
import fr.openmc.core.features.city.conditions.CityCreateConditions;
import fr.openmc.core.features.city.mascots.MascotsManager;
import fr.openmc.core.features.city.mayor.ElectionType;
import fr.openmc.core.features.city.mayor.managers.MayorManager;
import fr.openmc.core.features.city.mayor.managers.PerkManager;
import fr.openmc.core.features.city.mayor.perks.Perks;
import fr.openmc.core.features.economy.EconomyManager;
import fr.openmc.core.utils.DateUtils;
import fr.openmc.core.utils.ItemUtils;
import fr.openmc.core.utils.api.WorldGuardApi;
import fr.openmc.core.utils.customitems.CustomItemRegistry;
import fr.openmc.core.utils.database.DatabaseManager;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static fr.openmc.core.features.city.mayor.managers.MayorManager.PHASE_1_DAY;

public class CityCreateAction {

    private static final Map<UUID, String> pendingCities = new HashMap<>();

    public static void beginCreateCity(Player player, String cityName) {
        if (!CityCreateConditions.canCityCreate(player, cityName)) return;

        pendingCities.put(player.getUniqueId(), cityName);

        ItemInteraction.runLocationInteraction(
                player,
                getMascotStick(),
                "Mascot:chest",
                300,
                "Vous avez reçu un coffre pour poser votre mascotte",
                "§cCréation annulée",
                location -> {
                    if (!isValidLocation(player, location)) return false;
                    finalizeCreation(player, location);
                    return true;
                },
                () -> {
                    pendingCities.remove(player.getUniqueId());
                }
        );
    }

    private static ItemStack getMascotStick() {
        ItemStack stick = CustomItemRegistry.getByName("omc_items:mascot_stick").getBest();
        ItemMeta meta = stick.getItemMeta();
        if (meta != null) {
            meta.displayName(Component.text("§lMascotte"));
            meta.lore(List.of(
                    Component.text("§cVotre mascotte sera posée à l'emplacement du coffre."),
                    Component.text("§cCe coffre n'est pas retirable."),
                    Component.text("§cDéconnexion = annulation.")
            ));
            stick.setItemMeta(meta);
        }
        return stick;
    }

    private static boolean isValidLocation(Player player, Location location) {
        if (location == null || location.getWorld() == null) return false;
        if (!"world".equals(location.getWorld().getName())) {
            MessagesManager.sendMessage(player, Component.text("§cCoffre uniquement dans le monde principal"), Prefix.CITY, MessageType.ERROR, false);
            return false;
        }
        if (location.clone().add(0, 1, 0).getBlock().getType().isSolid()) {
            MessagesManager.sendMessage(player, Component.text("§cAucun bloc ne doit être au-dessus du coffre"), Prefix.CITY, MessageType.ERROR, false);
            return false;
        }
        return true;
    }

    public static void finalizeCreation(Player player, Location mascotLocation) {
        ItemStack ayweniteItemStack = CustomItemRegistry.getByName("omc_items:aywenite").getBest();

        UUID uuid = player.getUniqueId();
        String pendingCityName = pendingCities.remove(uuid);
        if (pendingCityName == null) return;

        String cityUUID = UUID.randomUUID().toString().substring(0, 8);
        Chunk chunk = mascotLocation.getChunk();

        // on le refait pour voir si le nb d'item n'a pas changé, d'argent, si le mec na pas rej une ville
        if (!CityCreateConditions.canCityCreate(player, pendingCityName)) return;

        if (WorldGuardApi.doesChunkContainWGRegion(chunk)) {
            MessagesManager.sendMessage(player, Component.text("Ce chunk est dans une région protégée"), Prefix.CITY, MessageType.ERROR, false);
            return;
        }

        if (CityManager.isChunkClaimedInRadius(chunk, 1)) {
            MessagesManager.sendMessage(player, Component.text("Une des parcelles autour de ce chunk est claim!"), Prefix.CITY, MessageType.ERROR, false);
            return;
        }

        EconomyManager.withdrawBalance(player.getUniqueId(), CityCreateConditions.MONEY_CREATE);
        ItemUtils.removeItemsFromInventory(player, ayweniteItemStack.getType(), CityCreateConditions.AYWENITE_CREATE);

        // DB insert
        Bukkit.getScheduler().runTaskAsynchronously(OMCPlugin.getInstance(), () -> {
            try (PreparedStatement st = DatabaseManager.getConnection()
                    .prepareStatement("INSERT INTO city_regions (city_uuid, x, z) VALUES (?, ?, ?)")) {
                st.setString(1, cityUUID);
                st.setInt(2, chunk.getX());
                st.setInt(3, chunk.getZ());
                st.execute();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });


        City city = CityManager.createCity(player, cityUUID, pendingCityName, CityType.PEACE);
        city.addPlayer(uuid);
        city.addPermission(uuid, CPermission.OWNER);

        CityManager.claimedChunks.put(BlockVector2.at(chunk.getX(), chunk.getZ()), city);
        CityManager.freeClaim.put(cityUUID, 15);

        // Maire
        MayorManager mayorManager = MayorManager.getInstance();
        if (mayorManager.phaseMayor == 1) { // si création pendant le choix des maires
            mayorManager.createMayor(null, null, city, null, null, null, null, ElectionType.OWNER_CHOOSE);
        } else { // si création pendant les réformes actives
            NamedTextColor color = mayorManager.getRandomMayorColor();
            List<Perks> perks = PerkManager.getRandomPerksAll();
            mayorManager.createMayor(player.getName(), player.getUniqueId(), city, perks.getFirst(), perks.get(1), perks.get(2), color, ElectionType.OWNER_CHOOSE);
            MessagesManager.sendMessage(player, Component.text("Vous avez été désigné comme §6Maire de la Ville.\n§8§oVous pourrez choisir vos Réformes dans " + DateUtils.getTimeUntilNextDay(PHASE_1_DAY)), Prefix.MAYOR, MessageType.SUCCESS, true);
        }

        // Lois
        MayorManager.createCityLaws(city, false, null);

        // Mascotte
        player.getWorld().getBlockAt(mascotLocation).setType(Material.AIR);
        MascotsManager.createMascot(city.getUUID(), player.getWorld(), mascotLocation);

        // Feedback
        MessagesManager.sendMessage(player, Component.text("§aVotre ville a été crée : " + pendingCityName), Prefix.CITY, MessageType.SUCCESS, true);
        MessagesManager.sendMessage(player, Component.text("§7+ §615 chunks gratuits"), Prefix.CITY, MessageType.INFO, false);

        DynamicCooldownManager.use(uuid.toString(), "city:big", 60000);
    }
}
//public class CityCreateAction {
//
//    private static final ItemStack ayweniteItemStack = CustomItemRegistry.getByName("omc_items:aywenite").getBest();
//
//    private static final Map<UUID, String> pendingCities = new HashMap<>();
//
//    public static void beginCreateCity(Player player, String cityName) {
//
//        if (!CityCreateConditions.canCityCreate(player, cityName)){
//            MessagesManager.sendMessage(player, MessagesManager.Message.NOPERMISSION.getMessage(), Prefix.CITY, MessageType.ERROR, false);
//            return;
//        }
//
//        futurCreateCity.computeIfAbsent(player.getUniqueId(), k -> new HashMap<>()).put(cityName, CityType.PEACE);
//
//        ItemStack mascotsItem = CustomItemRegistry.getByName("omc_items:mascot_stick").getBest();
//        ItemMeta meta = mascotsItem.getItemMeta();
//
//        if (meta != null) {
//            List<Component> info = new ArrayList<>();
//            info.add(Component.text("§cVotre mascotte sera posé a l'emplacement du coffre et créera votre ville"));
//            info.add(Component.text("§cCe coffre n'est pas retirable"));
//            info.add(Component.text("§clors de votre déconnection la création sera annuler"));
//
//            meta.displayName(Component.text("§lMascotte"));
//            meta.lore(info);
//        }
//
//        mascotsItem.setItemMeta(meta);
//
//        ItemInteraction.runLocationInteraction(
//                player,
//                mascotsItem,
//                "Mascot:chest",
//                300,
//                "Vous avez reçu un coffre pour poser votre mascotte",
//                "§cCréation annulée",
//                mascotSpawn -> {
//                    if (mascotSpawn == null) return true;
//
//                    World world = Bukkit.getWorld("world");
//                    World player_world = player.getWorld();
//
//                    if (player_world != world) {
//                        MessagesManager.sendMessage(player, Component.text("§cImpossible de poser le coffre dans ce monde"), Prefix.CITY, MessageType.INFO, false);
//                        return false;
//                    }
//
//                    if (mascotSpawn.clone().add(0, 1, 0).getBlock().getType().isSolid()) {
//                        MessagesManager.sendMessage(player, Component.text("§cIl ne doit pas y avoir de block au dessus du coffre"), Prefix.CITY, MessageType.INFO, false);
//                        return false;
//                    }
//
//                    if (!futurCreateCity.containsKey(player.getUniqueId())) {
//                        MessagesManager.sendMessage(player, MessagesManager.Message.PLAYERNOCITY.getMessage(), Prefix.CITY, MessageType.ERROR, false);
//                        return false;
//                    }
//
//                    Chunk chunk = mascotSpawn.getChunk();
//
//                    String futurCityName = futurCreateCity.get(player.getUniqueId()).keySet().iterator().next();
//                    boolean cityAdd = endCreateCity(player, cityName, chunk);
//
//                    // on return true maintenant pour eviter que createCity s'execute plusieurs fois
//                    if (!cityAdd) {
//                        return true;
//                    }
//
//                    futurCreateCity.remove(player.getUniqueId());
//                    City city = CityManager.getPlayerCity(player.getUniqueId());
//
//                    if (city == null) {
//                        MessagesManager.sendMessage(player, Component.text("§cErreur : la ville n'a pas été reconnu"), Prefix.CITY, MessageType.ERROR, false);
//                        return true;
//                    }
//
//                    String city_uuid = city.getUUID();
//
//                    if (MascotUtils.mascotsContains(city_uuid) && !movingMascots.contains(city_uuid)) {
//                        MessagesManager.sendMessage(player, Component.text("§cVous possédez déjà une mascotte"), Prefix.CITY, MessageType.INFO, false);
//                        return true;
//                    }
//
//                    player_world.getBlockAt(mascotSpawn).setType(Material.AIR);
//
//                    MascotsManager.createMascot(city_uuid, player_world, mascotSpawn);
//                    return true;
//                }
//        );
//    }
//
//    public static boolean endCreateCity(Player player, String cityName, Chunk mascotsChunk) {
//        UUID uuid = player.getUniqueId();
//
//        String cityUUID = UUID.randomUUID().toString().substring(0, 8);
//
//        AtomicBoolean isClaimed = new AtomicBoolean(false);
//
//        if (WorldGuardApi.doesChunkContainWGRegion(mascotsChunk)) {
//            MessagesManager.sendMessage(player, Component.text("Ce chunk est dans une région protégée"), Prefix.CITY, MessageType.ERROR, false);
//            return false;
//        }
//
//        for (int x = -1; x <= 1; x++) {
//            for (int z = -1; z <= 1; z++) {
//                if (CityManager.isChunkClaimed(mascotsChunk.getX() + x, mascotsChunk.getZ() + z)) {
//                    isClaimed.set(true);
//                    break;
//                }
//            }
//        }
//
//        if (isClaimed.get()) {
//            MessagesManager.sendMessage(player, Component.text("Une des parcelles autour de ce chunk est claim! "), Prefix.CITY, MessageType.ERROR, false);
//            return false;
//        }
//
//        Bukkit.getScheduler().runTaskAsynchronously(OMCPlugin.getInstance(), () -> {
//            try {
//                PreparedStatement statement = DatabaseManager.getConnection().prepareStatement("INSERT INTO city_regions (city_uuid, x, z) VALUES (?, ?, ?)");
//                statement.setString(1, cityUUID);
//
//                statement.setInt(2, mascotsChunk.getX());
//                statement.setInt(3, mascotsChunk.getZ());
//                statement.addBatch();
//
//                statement.executeBatch();
//                statement.close();
//            } catch (SQLException e) {
//                MessagesManager.sendMessage(player, Component.text("Une erreur est survenue, réessayez plus tard"), Prefix.CITY, MessageType.ERROR, false);
//                throw new RuntimeException(e);
//            }
//        });
//
//        if (EconomyManager.getInstance().getBalance(player.getUniqueId()) < MONEY_CREATE) {
//            MessagesManager.sendMessage(player, Component.text("§cTu n'as pas assez d'Argent pour créer ta ville (" + MONEY_CREATE).append(Component.text(EconomyManager.getEconomyIcon() +" §cnécessaires)")).decoration(TextDecoration.ITALIC, false), Prefix.CITY, MessageType.ERROR, false);
//        }
//
//        if (!ItemUtils.hasEnoughItems(player, Objects.requireNonNull(CustomItemRegistry.getByName("omc_items:aywenite")).getBest().getType(), AYWENITE_CREATE)) {
//            MessagesManager.sendMessage(player, Component.text("§cTu n'as pas assez d'§dAywenite §cpour créer ta ville (" + AYWENITE_CREATE +" nécessaires)"), Prefix.CITY, MessageType.ERROR, false);
//        }
//
//        EconomyManager.getInstance().withdrawBalance(player.getUniqueId(), MONEY_CREATE);
//        ItemUtils.removeItemsFromInventory(player, ayweniteItemStack.getType(), AYWENITE_CREATE);
//
//        City city = CityManager.createCity(player, cityUUID, cityName, CityType.PEACE);
//        city.addPlayer(uuid);
//        city.addPermission(uuid, CPermission.OWNER);
//
//        CityManager.claimedChunks.put(BlockVector2.at(mascotsChunk.getX(), mascotsChunk.getZ()), city);
//        CityManager.freeClaim.put(cityUUID, 15);
//
//        player.closeInventory();
//
//        // SETUP MAIRE
//        MayorManager mayorManager = MayorManager.getInstance();
//        if (mayorManager.phaseMayor == 1) { // si création pendant le choix des maires
//            mayorManager.createMayor(null, null, city, null, null, null, null, ElectionType.OWNER_CHOOSE);
//        } else { // si création pendant les réformes actives
//            NamedTextColor color = mayorManager.getRandomMayorColor();
//            List<Perks> perks = PerkManager.getRandomPerksAll();
//            mayorManager.createMayor(player.getName(), player.getUniqueId(), city, perks.getFirst(), perks.get(1), perks.get(2), color, ElectionType.OWNER_CHOOSE);
//            MessagesManager.sendMessage(player, Component.text("Vous avez été désigné comme §6Maire de la Ville.\n§8§oVous pourrez choisir vos Réformes dans " + DateUtils.getTimeUntilNextDay(PHASE_1_DAY)), Prefix.MAYOR, MessageType.SUCCESS, true);
//        }
//
//        // SETUP LAW
//        MayorManager.createCityLaws(city, false, null);
//
//        MessagesManager.sendMessage(player, Component.text("Votre ville a été créée : " + cityName), Prefix.CITY, MessageType.SUCCESS, true);
//        MessagesManager.sendMessage(player, Component.text("Vous disposez de 15 claims gratuits"), Prefix.CITY, MessageType.SUCCESS, false);
//
//        DynamicCooldownManager.use(uuid.toString(), "city:big", 60000); //1 minute
//
//        return true;
//    }
//}
