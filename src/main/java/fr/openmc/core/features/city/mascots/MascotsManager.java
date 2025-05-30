package fr.openmc.core.features.city.mascots;

import fr.openmc.api.cooldown.DynamicCooldownManager;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.models.Mascot;
import fr.openmc.core.utils.ItemUtils;
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

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class MascotsManager {

    public static long IMMUNITY_COOLDOWN = 10080L * 60 * 1000; // 10080 minutes en ms

    public static NamespacedKey chestKey;
    public static NamespacedKey mascotsKey;
    public static List<Mascot> mascots;

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

        chestKey = new NamespacedKey(OMCPlugin.getInstance(), "mascots_chest");
        mascotsKey = new NamespacedKey(OMCPlugin.getInstance(), "mascotsKey");

        loadMascots();

        for (Mascot mascot : mascots){
            Entity mob = MascotUtils.loadMascot(mascot);
            if (mascot.isImmunity()){
                if (mob != null) mob.setGlowing(true);
            } else if (mob != null) mob.setGlowing(false);
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

    public static void createMascot(String city_uuid, World player_world, Location mascot_spawn) {
        LivingEntity mob = (LivingEntity) player_world.spawnEntity(mascot_spawn,EntityType.ZOMBIE);

        Chunk chunk = mascot_spawn.getChunk();
        setMascotsData(mob,null, 300, 300);
        mob.setGlowing(true);

        PersistentDataContainer data = mob.getPersistentDataContainer();
        // L'uuid de la ville lui est approprié pour l'identifier
        data.set(mascotsKey, PersistentDataType.STRING, city_uuid);

        Bukkit.getScheduler().runTaskAsynchronously(OMCPlugin.getInstance(), () -> {
            try {
                mascotsDao.create(new Mascot(city_uuid, mob.getUniqueId(), 1, true, true, chunk.getX(), chunk.getZ()));
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });

        MascotUtils.addMascotForCity(city_uuid, mob.getUniqueId(), chunk);
        // Immunité persistante de 7 jours pour la mascotte
        DynamicCooldownManager.use(city_uuid, "mascot:immunity", IMMUNITY_COOLDOWN);
    }

    public static void removeMascotsFromCity(String city_uuid) {
        City city = CityManager.getCity(city_uuid);
        if (city == null) return;

        Mascot mascot = city.getMascot();

        if (mascot == null) return;

        LivingEntity mascotEntity = MascotUtils.loadMascot(mascot);

        if (mascotEntity != null) mascotEntity.remove();

        Bukkit.getScheduler().runTaskAsynchronously(OMCPlugin.getInstance(), () -> {
            try {
                mascotsDao.delete(mascot);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });

        MascotUtils.removeMascotOfCity(city_uuid);
    }

    public static void giveMascotsEffect(UUID playerUUID) {
        if (!(Bukkit.getPlayer(playerUUID) instanceof Player player)) return;

        City city = CityManager.getPlayerCity(playerUUID);
        if (city == null) return;

        Mascot mascot = city.getMascot();
        if (mascot == null) return;

        if (!MascotUtils.mascotsContains(city.getUUID())) return;

        if (mascot.isAlive()) return;

        int level = mascot.getLevel();

        for (PotionEffect potionEffect : MascotsLevels.valueOf("level" + level).getMalus()) {
            player.addPotionEffect(potionEffect);
        }
    }

    public static void reviveMascots(String city_uuid) {
        if (MascotUtils.mascotsContains(city_uuid)) return;

        City city = CityManager.getCity(city_uuid);

        MascotUtils.changeMascotState(city_uuid, true);
        MascotUtils.changeMascotImmunity(city_uuid, false);

        Mascot mascot = city.getMascot();
        if (mascot == null) return;

        int level = mascot.getLevel();

        LivingEntity entity = MascotUtils.loadMascot(mascot);

        if (entity == null) return;

        entity.setHealth(Math.floor(0.10 * entity.getMaxHealth()));
        entity.setCustomName("§l" + city.getName() + " §c" + entity.getHealth() + "/" + entity.getMaxHealth() + "❤");
        entity.setGlowing(false);
        MascotsListener.mascotsRegeneration(mascot.getMascotUUID());

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

        LivingEntity mob = MascotUtils.loadMascot(mascot);
        if (mob == null) return;

        if (!MascotUtils.isMascot(mob)) return;

        MascotsLevels mascotsLevels = MascotsLevels.valueOf("level" + level);
        double lastHealth = mascotsLevels.getHealth();
        if (mascotsLevels == MascotsLevels.level10) return;

        MascotUtils.setMascotLevel(city_uuid, level + 1);
        mascotsLevels = MascotsLevels.valueOf("level" + level);

        try {
            int maxHealth = mascotsLevels.getHealth();
            mob.setMaxHealth(maxHealth);

            if (mob.getHealth() == lastHealth) {
                mob.setHealth(maxHealth);
            }

            double currentHealth = mob.getHealth();
            mob.setCustomName("§l" + MascotUtils.getCityFromMascot(mascot.getMascotUUID()).getName() + " §c" + currentHealth + "/" + maxHealth + "❤");
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public static void changeMascotsSkin(Entity mascots, EntityType skin, Player player, Material matAywenite, int aywenite) {
        World world = Bukkit.getWorld("world");
        Location mascotsLoc = mascots.getLocation();

        Mascot mascot = MascotUtils.getMascotByEntity(mascots);
        if (mascot==null){
            return;
        }

        LivingEntity mob = MascotUtils.loadMascot(mascot);
        boolean glowing = mascots.isGlowing();
        long cooldown = 0;
        boolean hasCooldown = false;

        // to avoid the suffocation of the mascot when it changes skin to a spider for exemple
        if (mascotsLoc.clone().add(0, 1, 0).getBlock().getType().isSolid() && mob.getHeight() <= 1.0) {
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

        double baseHealth = mob.getHealth();
        double maxHealth = mob.getMaxHealth();
        String name = mob.getCustomName();
        String mascotsCustomUUID = mob.getPersistentDataContainer().get(mascotsKey, PersistentDataType.STRING);

        if (!DynamicCooldownManager.isReady(mascots.getUniqueId().toString(), "mascots:move")) {
            cooldown = DynamicCooldownManager.getRemaining(mascots.getUniqueId().toString(), "mascots:move");
            hasCooldown = true;
            DynamicCooldownManager.clear(mob.getUniqueId().toString(), "mascots:move");
        }

        mob.remove();

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
            MascotUtils.setMascotUUID(mascotsCustomUUID, newMascots.getUniqueId());
        }

        ItemUtils.removeItemsFromInventory(player, matAywenite, aywenite);
    }


    private static void setMascotsData(LivingEntity mob, String customName, double maxHealth, double baseHealth) {
        mob.setAI(false);
        mob.setMaxHealth(maxHealth);
        mob.setHealth(baseHealth);
        mob.setPersistent(true);
        mob.setRemoveWhenFarAway(false);

        mob.setCustomName(Objects.requireNonNullElseGet(customName, () -> "§lMascotte §c" + mob.getHealth() + "/300❤"));
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
