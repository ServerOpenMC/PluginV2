package fr.openmc.core.features.city.mascots;

import dev.lone.itemsadder.api.CustomStack;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.utils.ItemUtils;
import fr.openmc.core.utils.chronometer.Chronometer;
import fr.openmc.core.utils.chronometer.ChronometerType;
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
import org.bukkit.inventory.PlayerInventory;
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
    public static Map<UUID, Location> mascotSpawn = new HashMap<>();

    public MascotsManager (OMCPlugin plugin) {
        //changement du spigot.yml pour permettre au mascottes d'avoir 3000 coeurs
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
    }

    public static void init_db(Connection conn) throws SQLException {
        conn.prepareStatement("CREATE TABLE IF NOT EXISTS free_claim (city_uuid VARCHAR(8) NOT NULL PRIMARY KEY, claim DOUBLE DEFAULT 0);").executeUpdate();
        conn.prepareStatement("CREATE TABLE IF NOT EXISTS mascots (city_uuid VARCHAR(8) NOT NULL PRIMARY KEY, level INT NOT NULL, mascot_uuid VARCHAR(36) NOT NULL, immunity_active BOOLEAN NOT NULL, immunity_time BIGINT NOT NULL, alive BOOLEAN NOT NULL);").executeUpdate();
    }

    public static boolean freeClaimContains(String city_uuid) {
        String query = "SELECT EXISTS (SELECT 1 FROM free_claim WHERE city_uuid = ?) AS existe";

        try (PreparedStatement statement = DatabaseManager.getConnection().prepareStatement(query)) {
            statement.setString(1, city_uuid);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) == 1;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public static int getCityFreeClaim (String city_uuid){
        int freeClaim = 0;
        try {
            PreparedStatement statement = DatabaseManager.getConnection().prepareStatement("SELECT city_uuid FROM free_claim WHERE claim = ? ");
            statement.setString(1, city_uuid);
            ResultSet rs = statement.executeQuery();

            if (!rs.next()) {
                return freeClaim;
            }

            freeClaim = rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();

        }
        return freeClaim;
    }

    public static void addFreeClaim (String city_uuid, int claim) {
        Bukkit.getScheduler().runTaskAsynchronously(OMCPlugin.getInstance(), () -> {
            try {
                if (getCityFreeClaim(city_uuid)==0){
                    PreparedStatement statement = DatabaseManager.getConnection().prepareStatement("INSERT INTO free_claim VALUE (?, ?)");
                    statement.setString(1, city_uuid);
                    statement.setInt(2, claim);
                    statement.executeUpdate();
                } else {
                    PreparedStatement statement = DatabaseManager.getConnection().prepareStatement("UPDATE free_claim SET claim=? WHERE city_uuid=?;");
                    statement.setInt(1, claim);
                    statement.setString(2, city_uuid);
                    statement.executeUpdate();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public static void removeFreeClaim (String city_uuid, int claim) {
        Bukkit.getScheduler().runTaskAsynchronously(OMCPlugin.getInstance(), () -> {
            try {
                if (getCityFreeClaim(city_uuid) == 0 || getCityFreeClaim(city_uuid)-claim <= 0) {
                    PreparedStatement statement = DatabaseManager.getConnection().prepareStatement("DELETE FROM free_claim WHERE city_uuid = ?");
                    statement.setString(1, city_uuid);
                    statement.executeUpdate();
                } else {
                    PreparedStatement statement = DatabaseManager.getConnection().prepareStatement("UPDATE free_claim SET claim=? WHERE city_uuid=?;");
                    statement.setInt(1, getCityFreeClaim(city_uuid)-claim);
                    statement.setString(2, city_uuid);
                    statement.executeUpdate();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public static void deleteFreeClaim (String city_uuid) {
        Bukkit.getScheduler().runTaskAsynchronously(OMCPlugin.getInstance(), () -> {
            try {
                PreparedStatement statement = DatabaseManager.getConnection().prepareStatement("DELETE FROM free_claim WHERE city_uuid = ?");
                statement.setString(1, city_uuid);
                statement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public static boolean mascotsContains(String city_uuid) {
        String query = "SELECT EXISTS (SELECT 1 FROM mascots WHERE city_uuid = ?) AS existe";

        try (PreparedStatement statement = DatabaseManager.getConnection().prepareStatement(query)) {
            statement.setString(1, city_uuid);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) == 1;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public static void createMascot (String city_uuid, World player_world, Location mascot_spawn) {
        LivingEntity mob = (LivingEntity) player_world.spawnEntity(mascot_spawn,EntityType.ZOMBIE);

        setMascotsData(mob,null, 300, 300);

        PersistentDataContainer data = mob.getPersistentDataContainer();
        // l'uuid de la ville lui est approprié pour l'identifié
        data.set(mascotsKey, PersistentDataType.STRING, city_uuid);

        // immunité persistente de 7 jours pour la mascotte
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
    }

    public static void removeMascotsFromCity (String city_uuid) {
        UUID mascotUUID = getMascotUUIDByCityUUID(city_uuid);

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
    }

    public static int getMascotLevel (String city_uuid){
        int level = 0;
        try {
            PreparedStatement statement = DatabaseManager.getConnection().prepareStatement("SELECT city_uuid FROM mascots WHERE level = ? ");
            statement.setString(1, city_uuid);
            ResultSet rs = statement.executeQuery();

            if (!rs.next()) {
                return level;
            }

            level = rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();

        }
        return level;
    }

    public static void setMascotLevel (String city_uuid, int level){
        Bukkit.getScheduler().runTaskAsynchronously(OMCPlugin.getInstance(), () -> {
            try {
                PreparedStatement statement = DatabaseManager.getConnection().prepareStatement("UPDATE mascots SET level=? WHERE city_uuid=?;");
                statement.setInt(1, level);
                statement.setString(2, city_uuid);
                statement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public static UUID getMascotUUIDByCityUUID (String city_uuid) {
        UUID uuid = null;
        try {
            PreparedStatement statement = DatabaseManager.getConnection().prepareStatement("SELECT city_uuid FROM mascots WHERE mascot_uuid = ? ");
            statement.setString(1, city_uuid);
            ResultSet rs = statement.executeQuery();

            if (!rs.next()) {
                return null;
            }

            uuid = UUID.fromString(rs.getString(1));
        } catch (SQLException e) {
            e.printStackTrace();

        }
        return uuid;
    }

    public static void setMascotUUID (String city_uuid, UUID uuid){
        Bukkit.getScheduler().runTaskAsynchronously(OMCPlugin.getInstance(), () -> {
            try {
                PreparedStatement statement = DatabaseManager.getConnection().prepareStatement("UPDATE mascots SET level=? WHERE city_uuid=?;");
                statement.setString(1, uuid.toString());
                statement.setString(2, city_uuid);
                statement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public static boolean getMascotImmunity (String city_uuid) {
        boolean immunity = false;
        try {
            PreparedStatement statement = DatabaseManager.getConnection().prepareStatement("SELECT city_uuid FROM mascots WHERE immunity_active = ? ");
            statement.setString(1, city_uuid);
            ResultSet rs = statement.executeQuery();

            if (!rs.next()) {
                return false;
            }

            immunity = rs.getBoolean(1);
        } catch (SQLException e) {
            e.printStackTrace();

        }
        return immunity;
    }

    public static void changeMascotImmunity (String city_uuid) {
        boolean immunity = !getMascotImmunity(city_uuid);
        Bukkit.getScheduler().runTaskAsynchronously(OMCPlugin.getInstance(), () -> {
            try {
                PreparedStatement statement = DatabaseManager.getConnection().prepareStatement("UPDATE mascots SET immunity_active=? WHERE city_uuid=?;");
                statement.setBoolean(1, immunity);
                statement.setString(2, city_uuid);
                statement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public static long getMascoteImmunityTime (String city_uuid) {
        long time = 0;
        try {
            PreparedStatement statement = DatabaseManager.getConnection().prepareStatement("SELECT city_uuid FROM mascots WHERE immunity_time = ? ");
            statement.setString(1, city_uuid);
            ResultSet rs = statement.executeQuery();

            if (!rs.next()) {
                return time;
            }

            time = rs.getLong(1);
        } catch (SQLException e) {
            e.printStackTrace();

        }
        return time;
    }

    public static void setMascotImmunityTime (String city_uuid, long time){
        Bukkit.getScheduler().runTaskAsynchronously(OMCPlugin.getInstance(), () -> {
            try {
                PreparedStatement statement = DatabaseManager.getConnection().prepareStatement("UPDATE mascots SET immunity_time=? WHERE city_uuid=?;");
                statement.setLong(1, time);
                statement.setString(2, city_uuid);
                statement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public static boolean getMascotState (String city_uuid) {
        boolean alive = false;
        try {
            PreparedStatement statement = DatabaseManager.getConnection().prepareStatement("SELECT city_uuid FROM mascots WHERE alive = ? ");
            statement.setString(1, city_uuid);
            ResultSet rs = statement.executeQuery();

            if (!rs.next()) {
                return false;
            }

            alive = rs.getBoolean(1);
        } catch (SQLException e) {
            e.printStackTrace();

        }
        return alive;
    }

    public static void changeMascotState (String city_uuid){
        boolean alive = !getMascotState(city_uuid);
        Bukkit.getScheduler().runTaskAsynchronously(OMCPlugin.getInstance(), () -> {
            try {
                PreparedStatement statement = DatabaseManager.getConnection().prepareStatement("UPDATE mascots SET immunity_time=? WHERE city_uuid=?;");
                statement.setBoolean(1, alive);
                statement.setString(2, city_uuid);
                statement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public static void giveMascotsEffect (String city_uuid, UUID playerUUID) {
        if (Bukkit.getPlayer(playerUUID) instanceof Player player) {
            if (city_uuid!=null){
                if (mascotsContains(city_uuid)){
                    int level = getMascotLevel(city_uuid);
                    if (getMascotState(city_uuid)){
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

    public static void reviveMascots (String city_uuid) {
        if (mascotsContains(city_uuid)){
            changeMascotState(city_uuid);
            changeMascotImmunity(city_uuid);
            int level = getMascotLevel(city_uuid);
            if (getMascotUUIDByCityUUID(city_uuid)!=null){
                LivingEntity entity = (LivingEntity) Bukkit.getEntity(getMascotUUIDByCityUUID(city_uuid));
                if (entity!=null){
                    entity.setHealth(Math.floor(0.10 * entity.getMaxHealth()));
                    entity.setCustomName("§lMascotte §c" + entity.getHealth() + "/" + entity.getMaxHealth() + "❤");
                    MascotsListener.mascotsRegeneration(getMascotUUIDByCityUUID(city_uuid));
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

    public static void giveChest (Player player) {
        if (!ItemUtils.hasAvailableSlot(player)){

            MessagesManager.sendMessage(player, Component.text("Vous n'avez pas assez de place dans votre inventaire : mascotte invoquée à vos coordonées"), Prefix.CITY, MessageType.ERROR, false);
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
            info.add(Component.text("§clors de votre déconnection la mascotte sera placé"));

            meta.setDisplayName("§lMascotte");
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

    public static void removeChest (Player player){
        ItemStack specialChest = new ItemStack(Material.CHEST);
        ItemMeta meta = specialChest.getItemMeta();
        if (meta != null){
            List<Component> info = new ArrayList<>();
            info.add(Component.text("§cVotre mascotte sera posé a l'emplacement du coffre"));
            info.add(Component.text("§cCe coffre n'est pas retirable"));
            info.add(Component.text("§clors de votre déconnection la mascotte sera placé"));

            meta.setDisplayName("§lMascotte");
            meta.lore(info);
            PersistentDataContainer data = meta.getPersistentDataContainer();
            data.set(chestKey,PersistentDataType.STRING, "id");
            specialChest.setItemMeta(meta);

            if (player.getInventory().contains(specialChest)){
                player.getInventory().remove(specialChest);
            }
        }
    }

    public static void upgradeMascots (String city_uuid, UUID entityUUID) {
        LivingEntity mob = (LivingEntity) Bukkit.getEntity(entityUUID);
        if (mob==null){
            return;
        }
        if (mob.getPersistentDataContainer().has(mascotsKey, PersistentDataType.STRING)){

            MascotsLevels mascotsLevels = MascotsLevels.valueOf("level" + getMascotLevel(city_uuid));
            double lastHealth = mascotsLevels.getHealth();
            if (mascotsLevels != MascotsLevels.level10){

                setMascotLevel(city_uuid, getMascotLevel(city_uuid)+1);
                mascotsLevels = MascotsLevels.valueOf("level" + getMascotLevel(city_uuid));

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

        if (mascotsLoc.clone().add(0, 1, 0).getBlock().getType().isSolid() && mob.getHeight() <= 1.0) {
            return;
        }

        double baseHealth = mob.getHealth();
        double maxHealth = mob.getMaxHealth();
        String name = mob.getCustomName();
        String mascotsCustomUUID = mob.getPersistentDataContainer().get(mascotsKey, PersistentDataType.STRING);

        mob.remove();

        if (world != null) {
            LivingEntity newMascots = (LivingEntity) world.spawnEntity(mascotsLoc, skin);
            setMascotsData(newMascots, name, maxHealth, baseHealth);
            PersistentDataContainer newData = newMascots.getPersistentDataContainer();

            if (mascotsCustomUUID != null) {
                newData.set(mascotsKey, PersistentDataType.STRING, mascotsCustomUUID);
                setMascotUUID(mascotsCustomUUID, newMascots.getUniqueId());
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


    public static boolean hasEnoughCroqStar (Player player, MascotsLevels mascotsLevels) {
        String itemNamespace = "city:croqstar";
        int requiredAmount = mascotsLevels.getUpgradeCost();
        int count = 0;

        for (ItemStack item : player.getInventory().getContents()) {
            if (item != null) {
                CustomStack customStack = CustomStack.byItemStack(item);
                if (customStack != null && customStack.getNamespacedID().equals(itemNamespace)) {
                    count += item.getAmount();
                    if (count >= requiredAmount) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static void removeCrocStar(Player player, MascotsLevels mascotsLevels) {
        String itemNamespace = "city:croqstar";
        PlayerInventory inventory = player.getInventory();
        int amountToRemove = mascotsLevels.getUpgradeCost();

        for (ItemStack item : inventory.getContents()) {
            if (item != null) {
                CustomStack customStack = CustomStack.byItemStack(item);
                if (customStack != null && customStack.getNamespacedID().equals(itemNamespace)) {
                    int stackAmount = item.getAmount();

                    if (stackAmount > amountToRemove) {
                        item.setAmount(stackAmount - amountToRemove);
                        return;
                    } else {
                        amountToRemove -= stackAmount;
                        item.setAmount(0);
                    }
                    if (amountToRemove <= 0) {
                        return;
                    }
                }
            }
        }
    }
}
