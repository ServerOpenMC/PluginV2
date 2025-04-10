package fr.openmc.core.features.city.mascots;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.utils.ItemUtils;
import fr.openmc.core.utils.chronometer.Chronometer;
import fr.openmc.core.utils.database.DatabaseManager;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class MascotsManager {

    public static NamespacedKey chestKey;
    public static NamespacedKey mascotsKey;
    public static List<Mascot> mascots = new ArrayList<>();
    public static HashMap<String, Integer> freeClaim = new HashMap<>();
    public static Map<UUID, Location> mascotSpawn = new HashMap<>();

    public MascotsManager(OMCPlugin plugin) {
        //changement du spigot.yml pour permettre aux mascottes d'avoir 3000 cœurs
        File spigotYML = new File("spigot.yml");
        YamlConfiguration spigotYMLConfig = YamlConfiguration.loadConfiguration(spigotYML);
        spigotYMLConfig.set("settings.attribute.maxHealth.max", 6000.0);
        try {
            spigotYMLConfig.save(new File("spigot.yml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        chestKey = new NamespacedKey(plugin, "mascots_chest");
        mascotsKey = new NamespacedKey(plugin, "mascotsKey");

        mascots = getAllMascots();
        freeClaim = getAllFreeClaims();

        for (Mascot mascot : mascots){
            UUID mascotUUID = UUID.fromString(mascot.getMascotUuid());
            Entity mob = Bukkit.getEntity(mascotUUID);
            if (mascot.isImmunity()){
                if (mob != null) mob.setGlowing(true);
            } else if (mob != null) mob.setGlowing(false);
        }
    }

    public static void init_db(Connection conn) throws SQLException {
        conn.prepareStatement("CREATE TABLE IF NOT EXISTS free_claim (city_uuid VARCHAR(8) NOT NULL PRIMARY KEY, claim INT NOT NULL);").executeUpdate();
        conn.prepareStatement("CREATE TABLE IF NOT EXISTS mascots (city_uuid VARCHAR(8) NOT NULL PRIMARY KEY, level INT NOT NULL, mascot_uuid VARCHAR(36) NOT NULL, immunity BOOLEAN NOT NULL, immunity_time BIGINT NOT NULL, alive BOOLEAN NOT NULL);").executeUpdate();
    }

    public static HashMap<String, Integer> getAllFreeClaims() {
        HashMap<String, Integer> freeClaims = new HashMap<>();

        String query = "SELECT city_uuid, claim FROM free_claim";
        try (PreparedStatement statement = DatabaseManager.getConnection().prepareStatement(query);
             ResultSet rs = statement.executeQuery()) {

            while (rs.next()) {
                String cityUuid = rs.getString("city_uuid");
                int claim = rs.getInt("claim");
                freeClaims.put(cityUuid, claim);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return freeClaims;
    }

    public static List<Mascot> getAllMascots() {
        List<Mascot> mascots = new ArrayList<>();

        String query = "SELECT city_uuid, mascot_uuid, level, immunity, immunity_time, alive FROM mascots";
        try (PreparedStatement statement = DatabaseManager.getConnection().prepareStatement(query);
             ResultSet rs = statement.executeQuery()) {

            while (rs.next()) {
                String cityUuid = rs.getString("city_uuid");
                String mascotUuid = rs.getString("mascot_uuid");
                int level = rs.getInt("level");
                boolean immunity = rs.getBoolean("immunity");
                long immunity_time = rs.getLong("immunity_time");
                boolean alive = rs.getBoolean("alive");
                mascots.add(new Mascot(cityUuid, mascotUuid, level, immunity, immunity_time, alive)); // Ajouter à la liste
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return mascots;
    }

    public static void saveFreeClaims(HashMap<String, Integer> freeClaims){
        String query;
        
        if (OMCPlugin.isUnitTestVersion()) {
            query = "MERGE INTO free_claim KEY(city_uuid) VALUES (?, ?)";
        } else {
            query = "INSERT INTO free_claim (city_uuid, claim) VALUES (?, ?) ON DUPLICATE KEY UPDATE claim = ?";
        }
        try (PreparedStatement statement = DatabaseManager.getConnection().prepareStatement(query)) {
            for (Map.Entry<String, Integer> entry : freeClaims.entrySet()) {
                statement.setString(1, entry.getKey());
                statement.setInt(2, entry.getValue());
                statement.setInt(3, entry.getValue());
                statement.addBatch();
            }
            statement.executeBatch();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void saveMascots(List<Mascot> mascots) {
        String query;

        if (OMCPlugin.isUnitTestVersion()) {
            query = "MERGE INTO mascots " +
                    "KEY(city_uuid) " +
                    "VALUES (?, ?, ?, ?, ?, ?)";
        } else {
            query = "INSERT INTO mascots (city_uuid, mascot_uuid, level, immunity, immunity_time, alive) " +
                    "VALUES (?, ?, ?, ?, ?, ?) " +
                    "ON DUPLICATE KEY UPDATE mascot_uuid = ?, level = ?, immunity = ?, immunity_time = ?, alive = ?";
        }

        try (PreparedStatement statement = DatabaseManager.getConnection().prepareStatement(query)) {
            for (Mascot mascot : mascots) {

                statement.setString(1, mascot.getCityUuid());
                statement.setString(2, mascot.getMascotUuid());
                statement.setInt(3, mascot.getLevel());
                statement.setBoolean(4, mascot.isImmunity());
                statement.setLong(5, mascot.getImmunity_time());
                statement.setBoolean(6, mascot.isAlive());

                statement.setString(7, mascot.getMascotUuid());
                statement.setInt(8, mascot.getLevel());
                statement.setBoolean(9, mascot.isImmunity());
                statement.setLong(10, mascot.getImmunity_time());
                statement.setBoolean(11, mascot.isAlive());

                statement.addBatch();
            }

            statement.executeBatch();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void createMascot(String city_uuid, World player_world, Location mascot_spawn) {
        LivingEntity mob = (LivingEntity) player_world.spawnEntity(mascot_spawn,EntityType.ZOMBIE);

        setMascotsData(mob,null, 300, 300);
        mob.setGlowing(true);

        PersistentDataContainer data = mob.getPersistentDataContainer();
        // L'uuid de la ville lui est approprié pour l'identifié
        data.set(mascotsKey, PersistentDataType.STRING, city_uuid);

        // Immunité persistante de 7 jours pour la mascotte
        MascotsListener.startImmunityTimer(city_uuid, 10080);
        Bukkit.getScheduler().runTaskAsynchronously(OMCPlugin.getInstance(), () -> {
            try {
                PreparedStatement statement = DatabaseManager.getConnection().prepareStatement("INSERT INTO mascots VALUE (?, 1, ?, true, ?, true)");
                statement.setString(1, city_uuid);
                statement.setString(2, String.valueOf(mob.getUniqueId()));
                statement.setInt(3, 10080);
                statement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });

        MascotUtils.addMascotForCity(city_uuid, mob.getUniqueId());
    }

    public static void removeMascotsFromCity(String city_uuid) {
        UUID mascotUUID = MascotUtils.getMascotUUIDOfCity(city_uuid);

        if (mascotUUID!=null){
            LivingEntity mascots = (LivingEntity) Bukkit.getEntity(mascotUUID);
            if (mascots!=null){
                mascots.remove();
            }
            Bukkit.getScheduler().runTaskAsynchronously(OMCPlugin.getInstance(), () -> {
                try {
                    PreparedStatement statement = DatabaseManager.getConnection().prepareStatement("DELETE FROM mascots WHERE city_uuid = ?");
                    statement.setString(1, city_uuid);
                    statement.executeUpdate();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            });
        }

        MascotUtils.removeMascotOfCity(city_uuid);
    }

    public static void giveMascotsEffect(String city_uuid, UUID playerUUID) {
        if (Bukkit.getPlayer(playerUUID) instanceof Player player) {
            if (city_uuid!=null){
                if (MascotUtils.mascotsContains(city_uuid)){
                    int level = MascotUtils.getMascotLevel(city_uuid);
                    if (MascotUtils.getMascotState(city_uuid)){
                        for (PotionEffect potionEffect : MascotsLevels.valueOf("level"+level).getBonus()){
                            player.addPotionEffect(potionEffect);
                        }
                    } else {
                        for (PotionEffect potionEffect : MascotsLevels.valueOf("level"+level).getMalus()){
                            player.addPotionEffect(potionEffect);
                        }
                    }
                }
            }
        }
    }

    public static void reviveMascots(String city_uuid) {
        if (MascotUtils.mascotsContains(city_uuid)){
            MascotUtils.changeMascotState(city_uuid, true);
            MascotUtils.changeMascotImmunity(city_uuid, false);
            int level = MascotUtils.getMascotLevel(city_uuid);
            if (MascotUtils.getMascotUUIDOfCity(city_uuid)!=null){
                LivingEntity entity = (LivingEntity) Bukkit.getEntity(Objects.requireNonNull(MascotUtils.getMascotUUIDOfCity(city_uuid)));
                if (entity!=null){
                    entity.setHealth(Math.floor(0.10 * entity.getMaxHealth()));
                    entity.setCustomName("§lMascotte §c" + entity.getHealth() + "/" + entity.getMaxHealth() + "❤");
                    entity.setGlowing(false);
                    MascotsListener.mascotsRegeneration(MascotUtils.getMascotUUIDOfCity(city_uuid));
                    City city = CityManager.getCity(city_uuid);
                    if (city==null){return;}
                    for (UUID townMember : city.getMembers()){
                        if (Bukkit.getEntity(townMember) instanceof Player player){
                            for (PotionEffect potionEffect : MascotsLevels.valueOf("level"+level).getMalus()){
                                player.removePotionEffect(potionEffect.getType());
                            }
                            giveMascotsEffect(city_uuid, townMember);
                        }
                    }
                }
            }
        }
    }

    public static void giveChest(Player player) {
        if (!ItemUtils.hasAvailableSlot(player)){

            MessagesManager.sendMessage(player, Component.text("Vous n'avez pas assez de place dans votre inventaire : mascotte invoquée à vos coordonnées"), Prefix.CITY, MessageType.ERROR, false);
            City city = CityManager.getPlayerCity(player.getUniqueId());

            if (city == null) {
                MessagesManager.sendMessage(player, MessagesManager.Message.PLAYERNOCITY.getMessage(), Prefix.CITY, MessageType.ERROR, false);
                return;
            }

            String city_uuid = city.getUUID();
            createMascot(city_uuid, player.getWorld(), new Location(player.getWorld(), player.getLocation().getBlockX()+0.5, player.getLocation().getBlockY(), player.getLocation().getBlockZ()+0.5));
            return;
        }

        ItemStack specialChest = new ItemStack(Material.CHEST);
        ItemMeta meta = specialChest.getItemMeta();

        if (meta != null) {

            List<Component> info = new ArrayList<>();
            info.add(Component.text("§cVotre mascotte sera posé a l'emplacement du coffre"));
            info.add(Component.text("§cCe coffre n'est pas retirable"));
            info.add(Component.text("§clors de votre déconnexion la mascotte sera placée"));

            meta.displayName(Component.text("§lMascotte"));
            meta.lore(info);
            PersistentDataContainer data = meta.getPersistentDataContainer();
            data.set(chestKey,PersistentDataType.STRING, "id");
            specialChest.setItemMeta(meta);

        } else {

            City city = CityManager.getPlayerCity(player.getUniqueId());
            if (city == null) {
                MessagesManager.sendMessage(player, MessagesManager.Message.PLAYERNOCITY.getMessage(), Prefix.CITY, MessageType.ERROR, false);
                return;
            }
            String city_uuid = city.getUUID();
            createMascot(city_uuid, player.getWorld(), new Location(player.getWorld(), player.getLocation().getBlockX()+0.5, player.getLocation().getBlockY(), player.getLocation().getBlockZ()+0.5));
            OMCPlugin.getInstance().getLogger().severe("Erreur lors de l'initialisation de l'ItemMeta du coffre des mascottes");
            return;
        }

        player.getInventory().addItem(specialChest);
        mascotSpawn.put(player.getUniqueId(), new Location(player.getWorld(), player.getLocation().getBlockX()+0.5, player.getLocation().getBlockY(), player.getLocation().getBlockZ()+0.5));
    }

    public static void removeChest(Player player){
        ItemStack specialChest = new ItemStack(Material.CHEST);
        ItemMeta meta = specialChest.getItemMeta();
        if (meta != null){
            List<Component> info = new ArrayList<>();
            info.add(Component.text("§cVotre mascotte sera posé a l'emplacement du coffre"));
            info.add(Component.text("§cCe coffre n'est pas retirable"));
            info.add(Component.text("§clors de votre déconnexion la mascotte sera placée"));

            meta.displayName(Component.text("§lMascotte"));
            meta.lore(info);
            PersistentDataContainer data = meta.getPersistentDataContainer();
            data.set(chestKey,PersistentDataType.STRING, "id");
            specialChest.setItemMeta(meta);

            if (player.getInventory().contains(specialChest)){
                player.getInventory().remove(specialChest);
            }
        }
    }

    public static void upgradeMascots(String city_uuid, UUID entityUUID) {
        LivingEntity mob = (LivingEntity) Bukkit.getEntity(entityUUID);
        if (mob==null){
            return;
        }
        if (mob.getPersistentDataContainer().has(mascotsKey, PersistentDataType.STRING)){

            MascotsLevels mascotsLevels = MascotsLevels.valueOf("level" + MascotUtils.getMascotLevel(city_uuid));
            double lastHealth = mascotsLevels.getHealth();
            if (mascotsLevels != MascotsLevels.level10){

                MascotUtils.setMascotLevel(city_uuid, MascotUtils.getMascotLevel(city_uuid)+1);
                mascotsLevels = MascotsLevels.valueOf("level" + MascotUtils.getMascotLevel(city_uuid));

                try {
                    int maxHealth = mascotsLevels.getHealth();
                    mob.setMaxHealth(maxHealth);
                    if (mob.getHealth() == lastHealth){
                        mob.setHealth(maxHealth);
                    }
                    double currentHealth = mob.getHealth();
                    mob.setCustomName("§lMascotte §c" + currentHealth + "/" + maxHealth + "❤");
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }
        }
    }

    public static void changeMascotsSkin(Entity mascots, EntityType skin) {
        World world = Bukkit.getWorld("world");
        Location mascotsLoc = mascots.getLocation();
        LivingEntity mob = (LivingEntity) mascots;
        int cooldown = 0;
        boolean hasCooldown = false;

        // to avoid the suffocation of the mascot when it changes skin to a spider for exemple
        if (mascotsLoc.clone().add(0, 1, 0).getBlock().getType().isSolid() && mob.getHeight() <= 1.0) {
            return;
        }

        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                Location checkLoc = mascotsLoc.clone().add(x, 0, z);
                Material blockType = checkLoc.getBlock().getType();

                if (blockType != Material.AIR) {
                    return;
                }
            }
        }

        double baseHealth = mob.getHealth();
        double maxHealth = mob.getMaxHealth();
        String name = mob.getCustomName();
        String mascotsCustomUUID = mob.getPersistentDataContainer().get(mascotsKey, PersistentDataType.STRING);

        if (Chronometer.containsChronometer(mob.getUniqueId(), "mascotsCooldown")) {
            cooldown = Chronometer.getRemainingTime(mob.getUniqueId(), "mascotsCooldown");
            hasCooldown = true;
            Chronometer.stopChronometer(mob, "mascotsCooldown", null, "%null%");
        }

        mob.remove();

        if (world != null) {
            LivingEntity newMascots = (LivingEntity) world.spawnEntity(mascotsLoc, skin);

            if (hasCooldown){
                Chronometer.startChronometer(newMascots, "mascotsCooldown" , cooldown, null, "%null", null, "%null%");
            }

            setMascotsData(newMascots, name, maxHealth, baseHealth);
            PersistentDataContainer newData = newMascots.getPersistentDataContainer();

            if (mascotsCustomUUID != null) {
                newData.set(mascotsKey, PersistentDataType.STRING, mascotsCustomUUID);
                MascotUtils.setMascotUUID(mascotsCustomUUID, newMascots.getUniqueId());
            }
        }
    }

    private static void setMascotsData(LivingEntity mob, String customName, double maxHealth, double baseHealth) {
        mob.setAI(false);
        mob.setMaxHealth(maxHealth);
        mob.setHealth(baseHealth);
        mob.setPersistent(true);
        mob.setRemoveWhenFarAway(false);
        mob.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, Integer.MAX_VALUE, 0, true, true));
        mob.addPotionEffect(new PotionEffect(PotionEffectType.WATER_BREATHING, Integer.MAX_VALUE, 0, true, true));

        mob.setCustomName(Objects.requireNonNullElseGet(customName, () -> "§lMascotte §c" + mob.getHealth() + "/300❤"));

        mob.setCustomNameVisible(true);
    }
}
