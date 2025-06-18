package fr.openmc.core.features.city.sub.mascots;

import fr.openmc.api.cooldown.DynamicCooldownManager;
import fr.openmc.core.CommandsManager;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.sub.mascots.commands.AdminMascotsCommands;
import fr.openmc.core.features.city.sub.mascots.listeners.*;
import fr.openmc.core.features.city.sub.mascots.utils.MascotRegenerationUtils;
import fr.openmc.core.features.city.sub.mascots.utils.MascotUtils;
import fr.openmc.core.features.city.models.Mascot;
import fr.openmc.core.utils.ItemUtils;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class MascotsManager {
     public static List<String> movingMascots = new ArrayList<>();

    public static NamespacedKey mascotsKey;

    public static HashMap<String, Mascot> mascotsByCityUUID = new HashMap<>();
    public static HashMap<UUID, Mascot> mascotsByEntityUUID = new HashMap<>();

    public static final String PLACEHOLDER_MASCOT_NAME = "§l%s §c%.0f/%.0f❤";
    public static final String DEAD_MASCOT_NAME = "☠ §cMascotte Morte";

    public MascotsManager() {
        //changement du spigot.yml pour permettre aux mascottes d'avoir 3000 cœurs
        File spigotYML = new File("spigot.yml");
        YamlConfiguration spigotYMLConfig = YamlConfiguration.loadConfiguration(spigotYML);
        spigotYMLConfig.set("settings.attribute.maxHealth.max", 6000.0);
        try {
            spigotYMLConfig.save(new File("spigot.yml"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        mascotsKey = new NamespacedKey(OMCPlugin.getInstance(), "mascotsKey");

        loadMascots();

        OMCPlugin.registerEvents(
                new MascotsProtectionsListener(),
                new MascotsInteractionListener(),
                new MascotsDamageListener(),
                new MascotsDeathListener(),
                new MascotImmuneListener()
        );

        CommandsManager.getHandler().register(
                new AdminMascotsCommands()
        );

        for (Mascot mascot : MascotsManager.mascotsByCityUUID.values()) {
            MascotRegenerationUtils.mascotsRegeneration(mascot);
        }
    }

    private static Dao<Mascot, String> mascotsDao;

    public static void init_db(ConnectionSource connectionSource) throws SQLException {
        TableUtils.createTableIfNotExists(connectionSource, Mascot.class);
        mascotsDao = DaoManager.createDao(connectionSource, Mascot.class);
    }

    public static void loadMascots() {
        try {
            assert mascotsDao != null;
            mascots = mascotsDao.queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void saveMascots() {
        mascots.forEach(mascot -> {
            try {
                mascotsDao.createOrUpdate(mascot);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public static void createMascot(City city, String cityUUID, String cityName, World player_world, Location mascot_spawn) {
        LivingEntity mob = (LivingEntity) player_world.spawnEntity(mascot_spawn, EntityType.ZOMBIE);

        Chunk chunk = mascot_spawn.getChunk();
        setMascotsData(mob, cityName, 300, 300);
        mob.setGlowing(true);

        PersistentDataContainer data = mob.getPersistentDataContainer();
        data.set(mascotsKey, PersistentDataType.STRING, cityUUID);

        Bukkit.getScheduler().runTaskAsynchronously(OMCPlugin.getInstance(), () -> {
            try {
                mascotsDao.create(new Mascot(city_uuid, mob.getUniqueId(), 1, true, true, chunk.getX(), chunk.getZ()));
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
        LivingEntity mascotEntity = MascotUtils.loadMascot(mascot);

        if (mascotEntity != null) mascotEntity.remove();

        Bukkit.getScheduler().runTaskAsynchronously(OMCPlugin.getInstance(), () -> {
            try {
                mascotsDao.delete(mascot);
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
                0.10 * entity.getMaxHealth(),
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
                    mob.getHealth(),
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

        setMascotsData(newMascots, mascots.getCity().getName(), maxHealth, baseHealth);
        PersistentDataContainer newData = newMascots.getPersistentDataContainer();

        if (mascotsCustomUUID != null) {
            newData.set(mascotsKey, PersistentDataType.STRING, mascotsCustomUUID);
            mascots.setMascotUUID(newMascots.getUniqueId());
        }

        ItemUtils.removeItemsFromInventory(player, matAywenite, aywenite);
    }


    private static void setMascotsData(LivingEntity mob, String cityName, double maxHealth, double baseHealth) {
        mob.setAI(false);
        mob.setMaxHealth(maxHealth);
        mob.setHealth(baseHealth);
        mob.setPersistent(true);
        mob.setRemoveWhenFarAway(false);

        mob.customName(Component.text(PLACEHOLDER_MASCOT_NAME.formatted(
                cityName,
                baseHealth,
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
