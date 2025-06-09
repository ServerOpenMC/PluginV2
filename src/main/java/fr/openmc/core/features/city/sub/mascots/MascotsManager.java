package fr.openmc.core.features.city.sub.mascots;

import fr.openmc.api.cooldown.DynamicCooldownManager;
import fr.openmc.core.CommandsManager;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.sub.MascotRegenerationUtils;
import fr.openmc.core.features.city.sub.mascots.commands.AdminMascotsCommands;
import fr.openmc.core.features.city.sub.mascots.listeners.MascotsDamageListener;
import fr.openmc.core.features.city.sub.mascots.listeners.MascotsDeathListener;
import fr.openmc.core.features.city.sub.mascots.listeners.MascotsInteractionListener;
import fr.openmc.core.features.city.sub.mascots.listeners.MascotsProtectionsListener;
import fr.openmc.core.utils.ItemUtils;
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
import org.bukkit.inventory.EntityEquipment;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class MascotsManager {
    public static NamespacedKey mascotsKey = new NamespacedKey(OMCPlugin.getInstance(), "mascotsKey");

    public static List<String> movingMascots = new ArrayList<>();

    public static HashMap<String, Mascot> mascotsByCityUUID = new HashMap<>();
    public static HashMap<UUID, Mascot> mascotsByEntityUUID = new HashMap<>();

    public static final String PLACEHOLDER_MASCOT_NAME = "§l%s §c%a/%a❤";
    public static final String DEAD_MASCOT_NAME = "☠ §cMascotte Morte";

    public MascotsManager() {
        OMCPlugin plugin = OMCPlugin.getInstance();

        //changement du spigot.yml pour permettre aux mascottes d'avoir 3000 cœurs
        File spigotYML = new File("spigot.yml");
        YamlConfiguration spigotYMLConfig = YamlConfiguration.loadConfiguration(spigotYML);
        spigotYMLConfig.set("settings.attribute.maxHealth.max", 6000.0);
        try {
            spigotYMLConfig.save(new File("spigot.yml"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        loadAllMascots();

        for (Mascot mascot : mascotsByEntityUUID.values()) {
            Entity mob = mascot.getEntity();
            if (mascot.isImmunity()) {
                if (mob != null) mob.setGlowing(true);
            } else if (mob != null) mob.setGlowing(false);
        }

        OMCPlugin.registerEvents(
                new MascotsProtectionsListener(),
                new MascotsInteractionListener(),
                new MascotsDamageListener(),
                new MascotsDeathListener()
        );

        CommandsManager.getHandler().register(
                new AdminMascotsCommands()
        );

        for (Mascot mascot : MascotsManager.mascotsByCityUUID.values()) {
            MascotRegenerationUtils.mascotsRegeneration(mascot);
        }
    }

    public static void init_db(Connection conn) throws SQLException {
        conn.prepareStatement("CREATE TABLE IF NOT EXISTS mascots (city_uuid VARCHAR(8) NOT NULL PRIMARY KEY, level INT NOT NULL, mascot_uuid VARCHAR(36) NOT NULL, immunity BOOLEAN NOT NULL, alive BOOLEAN NOT NULL, x MEDIUMINT NOT NULL, z MEDIUMINT NOT NULL);").executeUpdate();
    }

    public static void loadAllMascots() {
        String query = "SELECT city_uuid, mascot_uuid, level, immunity, alive, x, z FROM mascots";
        try (PreparedStatement statement = DatabaseManager.getConnection().prepareStatement(query);
             ResultSet rs = statement.executeQuery()) {

            while (rs.next()) {
                World world = Bukkit.getWorld("world");

                String cityUUID = rs.getString("city_uuid");
                City city = CityManager.getCity(cityUUID);
                if (city == null) continue;

                UUID entityUUID = UUID.fromString(rs.getString("mascot_uuid"));
                int level = rs.getInt("level");
                boolean immunity = rs.getBoolean("immunity");
                boolean alive = rs.getBoolean("alive");
                Chunk chunk = world.getChunkAt(rs.getInt("x"), rs.getInt("z"));

                mascotsByEntityUUID.put(entityUUID, new Mascot(city, entityUUID, level, immunity, alive, chunk));
                mascotsByCityUUID.put(city.getUUID(), new Mascot(city, entityUUID, level, immunity, alive, chunk));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void saveMascots() {
        String query;

        if (OMCPlugin.isUnitTestVersion()) {
            query = "MERGE INTO mascots " +
                    "KEY(city_uuid) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?)";
        } else {
            query = "INSERT INTO mascots (city_uuid, mascot_uuid, level, immunity, alive, x, z) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ? )" +
                    "ON DUPLICATE KEY UPDATE mascot_uuid = ?, level = ?, immunity = ?, alive = ?, x = ?, z = ?";
        }

        try (PreparedStatement statement = DatabaseManager.getConnection().prepareStatement(query)) {
            for (Mascot mascot : mascotsByCityUUID.values()) {

                statement.setString(1, mascot.getCity().getUUID());
                statement.setString(2, mascot.getMascotUUID().toString());
                statement.setInt(3, mascot.getLevel());
                statement.setBoolean(4, mascot.isImmunity());
                statement.setBoolean(5, mascot.isAlive());
                statement.setInt(6, mascot.getChunk().getX());
                statement.setInt(7, mascot.getChunk().getZ());


                statement.setString(8, mascot.getMascotUUID().toString());
                statement.setInt(9, mascot.getLevel());
                statement.setBoolean(10, mascot.isImmunity());
                statement.setBoolean(11, mascot.isAlive());
                statement.setInt(12, mascot.getChunk().getX());
                statement.setInt(13, mascot.getChunk().getZ());

                statement.addBatch();
            }

            statement.executeBatch();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void createMascot(City city, World player_world, Location mascot_spawn) {
        LivingEntity mob = (LivingEntity) player_world.spawnEntity(mascot_spawn, EntityType.ZOMBIE);

        String cityUUID = city.getUUID();

        Chunk chunk = mascot_spawn.getChunk();
        setMascotsData(mob, Component.text(PLACEHOLDER_MASCOT_NAME.formatted(
                city.getName(),
                300.0,
                300.0
        )), 300, 300);
        mob.setGlowing(true);

        PersistentDataContainer data = mob.getPersistentDataContainer();
        data.set(mascotsKey, PersistentDataType.STRING, cityUUID);

        Bukkit.getScheduler().runTaskAsynchronously(OMCPlugin.getInstance(), () -> {
            try {
                PreparedStatement statement = DatabaseManager.getConnection().prepareStatement("INSERT INTO mascots VALUE (?, 1, ?, true, true, ?, ?)");
                statement.setString(1, cityUUID);
                statement.setString(2, String.valueOf(mob.getUniqueId()));
                statement.setInt(3, chunk.getX());
                statement.setInt(4, chunk.getZ());
                statement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });

        MascotUtils.addMascotForCity(city, mob.getUniqueId(), chunk);
    }

    public static void removeMascotsFromCity(City city) {
        Mascot mascot = city.getMascot();

        if (mascot == null) return;

        LivingEntity mascots = (LivingEntity) mascot.getEntity();

        if (mascots != null) mascots.remove();

        Bukkit.getScheduler().runTaskAsynchronously(OMCPlugin.getInstance(), () -> {
            try {
                PreparedStatement statement = DatabaseManager.getConnection().prepareStatement("DELETE FROM mascots WHERE city_uuid = ?");
                statement.setString(1, city.getUUID());
                statement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });

        MascotUtils.removeMascotOfCity(city);
    }

    public static void reviveMascots(String city_uuid) {
        City city = CityManager.getCity(city_uuid);

        Mascot mascot = city.getMascot();
        if (mascot == null) return;

        mascot.setAlive(true);
        mascot.setImmunity(false);

        int level = mascot.getLevel();

        LivingEntity entity = (LivingEntity) mascot.getEntity();

        if (entity == null) return;

        entity.setHealth(Math.floor(0.10 * entity.getMaxHealth()));
        entity.customName(Component.text(PLACEHOLDER_MASCOT_NAME.formatted(
                city.getName(),
                Math.floor(0.10 * entity.getMaxHealth()),
                entity.getMaxHealth()
        )));
        entity.setGlowing(false);

        MascotRegenerationUtils.mascotsRegeneration(mascot);

        for (UUID townMember : city.getMembers()) {
            if (!(Bukkit.getEntity(townMember) instanceof Player player)) return;

            for (PotionEffect potionEffect : MascotsLevels.valueOf("level" + level).getMalus()) {
                player.removePotionEffect(potionEffect.getType());
            }
        }
    }

    public static void upgradeMascots(String city_uuid) {
        City city = CityManager.getCity(city_uuid);
        if (city == null) return;

        Mascot mascot = city.getMascot();

        if (mascot == null) return;

        int level = mascot.getLevel();

        LivingEntity mob = (LivingEntity) mascot.getEntity();
        if (mob == null) return;

        if (!MascotUtils.isMascot(mob)) return;

        MascotsLevels mascotsLevels = MascotsLevels.valueOf("level" + level);
        double lastHealth = mascotsLevels.getHealth();
        if (mascotsLevels == MascotsLevels.level10) return;

        mascot.setLevel(level + 1);
        mascotsLevels = MascotsLevels.valueOf("level" + level);

        try {
            double maxHealth = mascotsLevels.getHealth();
            mob.setMaxHealth(maxHealth);

            if (mob.getHealth() == lastHealth) {
                mob.setHealth(maxHealth);
            }

            mob.customName(Component.text(PLACEHOLDER_MASCOT_NAME.formatted(
                    city.getName(),
                    Math.floor(mob.getHealth()),
                    maxHealth
            )));
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public static void changeMascotsSkin(Mascot mascots, EntityType skin, Player player, Material matAywenite, int aywenite) {
        World world = Bukkit.getWorld("world");
        LivingEntity entityMascot = (LivingEntity) mascots.getEntity();
        Location mascotsLoc = entityMascot.getLocation();

        boolean glowing = entityMascot.isGlowing();
        long cooldown = 0;
        boolean hasCooldown = false;

        // to avoid the suffocation of the mascot when it changes skin to a spider for exemple
        if (mascotsLoc.clone().add(0, 1, 0).getBlock().getType().isSolid() && entityMascot.getHeight() <= 1.0) {
            MessagesManager.sendMessage(player, Component.text("Libérez de l'espace au dessus de la macotte pour changer son skin"), Prefix.CITY, MessageType.INFO, false);
            return;
        }

        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                Location checkLoc = mascotsLoc.clone().add(x, 0, z);
                Material blockType = checkLoc.getBlock().getType();

                if (blockType != Material.AIR) {
                    MessagesManager.sendMessage(player, Component.text("Libérez de l'espace tout autour de la macotte pour changer son skin"), Prefix.CITY, MessageType.INFO, false);
                    return;
                }
            }
        }

        double baseHealth = entityMascot.getHealth();
        double maxHealth = entityMascot.getMaxHealth();
        Component name = entityMascot.customName();
        String mascotsCustomUUID = entityMascot.getPersistentDataContainer().get(mascotsKey, PersistentDataType.STRING);

        if (!DynamicCooldownManager.isReady(mascots.getMascotUUID().toString(), "mascots:move")) {
            cooldown = DynamicCooldownManager.getRemaining(mascots.getMascotUUID().toString(), "mascots:move");
            hasCooldown = true;
            DynamicCooldownManager.clear(entityMascot.getUniqueId().toString(), "mascots:move");
        }

        entityMascot.remove();

        if (world == null) return;

        LivingEntity newMascots = (LivingEntity) world.spawnEntity(mascotsLoc, skin);
        newMascots.setGlowing(glowing);

        if (hasCooldown) {
            DynamicCooldownManager.use(newMascots.getUniqueId().toString(), "mascots:move", cooldown);
        }

        setMascotsData(newMascots, name, maxHealth, baseHealth);
        PersistentDataContainer newData = newMascots.getPersistentDataContainer();

        if (mascotsCustomUUID != null) {
            newData.set(mascotsKey, PersistentDataType.STRING, mascotsCustomUUID);
            mascots.setMascotUUID(newMascots.getUniqueId());
        }

        ItemUtils.removeItemsFromInventory(player, matAywenite, aywenite);
    }


    private static void setMascotsData(LivingEntity mob, Component customName, double maxHealth, double baseHealth) {
        mob.setAI(false);
        mob.setMaxHealth(maxHealth);
        mob.setHealth(baseHealth);
        mob.setPersistent(true);
        mob.setRemoveWhenFarAway(false);

        mob.customName(customName != null ? customName : Component.text(PLACEHOLDER_MASCOT_NAME.formatted(
                "Mascotte",
                Math.floor(baseHealth),
                maxHealth
        )));
        mob.setCustomNameVisible(true);

        mob.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, Integer.MAX_VALUE, 0, true, true));
        mob.addPotionEffect(new PotionEffect(PotionEffectType.WATER_BREATHING, Integer.MAX_VALUE, 0, true, true));

        mob.setCanPickupItems(false);

        EntityEquipment equipment = mob.getEquipment();
        if (equipment == null) return;

        equipment.clear();

        equipment.setHelmetDropChance(0f);
        equipment.setChestplateDropChance(0f);
        equipment.setLeggingsDropChance(0f);
        equipment.setBootsDropChance(0f);
        equipment.setItemInMainHandDropChance(0f);
        equipment.setItemInOffHandDropChance(0f);
    }

}
