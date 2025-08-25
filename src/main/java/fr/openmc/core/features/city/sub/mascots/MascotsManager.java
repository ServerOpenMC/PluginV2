package fr.openmc.core.features.city.sub.mascots;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import fr.openmc.api.cooldown.DynamicCooldownManager;
import fr.openmc.core.CommandsManager;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.sub.mascots.commands.AdminMascotsCommands;
import fr.openmc.core.features.city.sub.mascots.listeners.*;
import fr.openmc.core.features.city.sub.mascots.models.Mascot;
import fr.openmc.core.features.city.sub.mascots.models.MascotsLevels;
import fr.openmc.core.features.city.sub.mascots.utils.MascotRegenerationUtils;
import fr.openmc.core.features.city.sub.mascots.utils.MascotUtils;
import fr.openmc.core.utils.ItemUtils;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.configuration.file.YamlConfiguration;
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
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class MascotsManager {
    public static final List<UUID> movingMascots = new ArrayList<>();
    public static final HashMap<UUID, Mascot> mascotsByCityUUID = new HashMap<>();
    public static final HashMap<UUID, Mascot> mascotsByEntityUUID = new HashMap<>();
    public static final String PLACEHOLDER_MASCOT_NAME = "§l%s §c%.0f/%.0f❤";
    public static final String DEAD_MASCOT_NAME = "☠ §cMascotte Morte";
    private static final NamespacedKey MAX_HEALTH_KEY = NamespacedKey.fromString("openmc:trans_rights_are_human_rights");
    public static NamespacedKey mascotsKey;
    private static Dao<Mascot, String> mascotsDao;

    public MascotsManager() {
        // changement du spigot.yml pour permettre aux mascottes d'avoir 3000 cœurs
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
                new MascotImmuneListener(),
                new MascotsTargetListener()
        );
        if (!OMCPlugin.isUnitTestVersion()) {
            new MascotsSoundListener();
        }

        CommandsManager.getHandler().register(
                new AdminMascotsCommands()
        );

        for (Mascot mascot : MascotsManager.mascotsByCityUUID.values()) {
            MascotRegenerationUtils.mascotsRegeneration(mascot);
        }
    }

    public static void initDB(ConnectionSource connectionSource) throws SQLException {
        TableUtils.createTableIfNotExists(connectionSource, Mascot.class);
        mascotsDao = DaoManager.createDao(connectionSource, Mascot.class);
    }

    public static void loadMascots() {
        try {
            assert mascotsDao != null;
            mascotsDao.queryForAll().forEach(mascot -> {
                mascotsByCityUUID.put(mascot.getCityUUID(), mascot);
                mascotsByEntityUUID.put(mascot.getMascotUUID(), mascot);
            });
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void saveMascots() {
        mascotsByCityUUID.forEach((cityUUID, mascot) -> {
            try {
                mascotsDao.createOrUpdate(mascot);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public static void createMascot(City city, UUID cityUUID, String cityName, World player_world, Location mascot_spawn) {
        LivingEntity mob = (LivingEntity) player_world.spawnEntity(mascot_spawn, EntityType.ZOMBIE);

        Chunk chunk = mascot_spawn.getChunk();
        setMascotsData(mob, cityName, 300, 300);
        mob.setGlowing(true);

        PersistentDataContainer data = mob.getPersistentDataContainer();
        data.set(mascotsKey, PersistentDataType.STRING, cityUUID.toString());

        Bukkit.getScheduler().runTaskAsynchronously(OMCPlugin.getInstance(), () -> {
            try {
                mascotsDao.create(new Mascot(cityUUID, mob.getUniqueId(), 1, true, true, chunk.getX(), chunk.getZ()));
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });

        MascotUtils.addMascotForCity(city, mob.getUniqueId(), chunk);
    }

    public static void removeMascotsFromCity(City city) {
        Mascot mascot = city.getMascot();

        if (mascot == null) return;

        LivingEntity mascotEntity = (LivingEntity) mascot.getEntity();

        if (mascotEntity != null) mascotEntity.remove();

        Bukkit.getScheduler().runTaskAsynchronously(OMCPlugin.getInstance(), () -> {
            try {
                mascotsDao.delete(mascot);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });

        MascotUtils.removeMascotOfCity(mascot);
    }

    public static void upgradeMascots(UUID cityUUID) {
        City city = CityManager.getCity(cityUUID);
        if (city == null) return;

        Mascot mascot = city.getMascot();

        if (mascot == null) return;

        int level = mascot.getLevel();

        LivingEntity mob = (LivingEntity) mascot.getEntity();
        if (mob == null) return;

        if (!MascotUtils.canBeAMascot(mob)) return;

        MascotsLevels mascotsLevels = MascotsLevels.valueOf("level" + level);
        double lastHealth = mascotsLevels.getHealth();
        if (mascotsLevels == MascotsLevels.level10) return;

        mascot.setLevel(level + 1);

        level = mascot.getLevel();

        mascotsLevels = MascotsLevels.valueOf("level" + level);

        double maxHealth = mascotsLevels.getHealth();
        mob.getAttribute(Attribute.MAX_HEALTH).removeModifier(MAX_HEALTH_KEY);
        mob.getAttribute(Attribute.MAX_HEALTH).addModifier(new AttributeModifier(MAX_HEALTH_KEY, maxHealth, AttributeModifier.Operation.ADD_NUMBER));

        if (mob.getHealth() == lastHealth) {
            mob.setHealth(maxHealth);
        }

        mob.customName(Component.text(PLACEHOLDER_MASCOT_NAME.formatted(
                city.getName(),
                mob.getHealth(),
                maxHealth
        )));
    }

    public static void changeMascotsSkin(Mascot mascots, EntityType skin, Player player, int aywenite) {
        World world = Bukkit.getWorld("world");
        LivingEntity entityMascot = (LivingEntity) mascots.getEntity();
        Location mascotsLoc = entityMascot.getLocation();
        UUID mascotUUID = entityMascot.getUniqueId();

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
        double maxHealth = entityMascot.getAttribute(Attribute.MAX_HEALTH).getValue();
        String cityUUID = entityMascot.getPersistentDataContainer().get(mascotsKey, PersistentDataType.STRING);

        if (!DynamicCooldownManager.isReady(mascots.getMascotUUID(), "mascots:move")) {
            cooldown = DynamicCooldownManager.getRemaining(mascots.getMascotUUID(), "mascots:move");
            hasCooldown = true;
            DynamicCooldownManager.clear(entityMascot.getUniqueId(), "mascots:move");
        }

        entityMascot.remove();

        if (world == null) return;

        LivingEntity newMascots = (LivingEntity) world.spawnEntity(mascotsLoc, skin);
        newMascots.setGlowing(glowing);

        if (hasCooldown) {
            DynamicCooldownManager.use(newMascots.getUniqueId(), "mascots:move", cooldown);
        }

        setMascotsData(newMascots, mascots.getCity().getName(), maxHealth, baseHealth);
        PersistentDataContainer newData = newMascots.getPersistentDataContainer();
        MascotsManager.mascotsByEntityUUID.remove(mascotUUID);
        MascotsManager.mascotsByEntityUUID.put(newMascots.getUniqueId(), mascots);

        if (cityUUID != null) {
            newData.set(mascotsKey, PersistentDataType.STRING, cityUUID);
            mascots.setMascotUUID(newMascots.getUniqueId());
        }

        ItemUtils.takeAywenite(player, aywenite);
    }


    private static void setMascotsData(LivingEntity mob, String cityName, double maxHealth, double baseHealth) {
        mob.setAI(false);
        mob.getAttribute(Attribute.MAX_HEALTH).addModifier(new AttributeModifier(MAX_HEALTH_KEY, maxHealth, AttributeModifier.Operation.ADD_NUMBER));
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
