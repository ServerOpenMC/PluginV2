package fr.openmc.core.features.city.sub.mascots;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import fr.openmc.api.cooldown.DynamicCooldownManager;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.OMCRegistry;
import fr.openmc.core.features.city.models.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.sub.mascots.commands.AdminMascotsCommands;
import fr.openmc.core.features.city.sub.mascots.listeners.*;
import fr.openmc.core.features.city.sub.mascots.models.Mascot;
import fr.openmc.core.features.city.sub.mascots.models.MascotsLevels;
import fr.openmc.core.features.city.sub.mascots.utils.MascotRegenerationUtils;
import fr.openmc.core.features.city.sub.mascots.utils.MascotUtils;
import fr.openmc.core.lifecycle.interfaces.HasCommands;
import fr.openmc.core.lifecycle.interfaces.HasDatabase;
import fr.openmc.core.lifecycle.interfaces.HasListeners;
import fr.openmc.core.lifecycle.listeners.ListenerFactory;
import fr.openmc.core.registry.features.Feature;
import fr.openmc.core.registry.features.annotations.Credit;
import fr.openmc.core.utils.bukkit.ItemUtils;
import fr.openmc.core.utils.text.messages.MessageType;
import fr.openmc.core.utils.text.messages.MessagesManager;
import fr.openmc.core.utils.text.messages.Prefix;
import fr.openmc.core.utils.text.messages.TranslationManager;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
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
import java.util.*;

@Credit(developers = {"Nocolm"})
public class MascotsManager extends Feature implements HasDatabase, HasCommands, HasListeners {
    @Getter
    private final List<UUID> movingMascots = new ArrayList<>();
    @Getter
    private final HashMap<UUID, Mascot> mascotsByCityUUID = new HashMap<>();
    @Getter
    private final HashMap<UUID, Mascot> mascotsByEntityUUID = new HashMap<>();
    @Getter
    private NamespacedKey mascotsKey;
    private Dao<Mascot, String> mascotsDao;

    @Override
    public void init() {
        // changement du spigot.yml pour permettre aux mascottes d'avoir 3000 cœurs
        File spigotYML = new File("spigot.yml");
        YamlConfiguration spigotYMLConfig = YamlConfiguration.loadConfiguration(spigotYML);
        spigotYMLConfig.set("settings.attribute.maxHealth.max", 6000.0);
        try {
            spigotYMLConfig.save(new File("spigot.yml"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        mascotsKey = new NamespacedKey(OMCPlugin.getInstance(), "mascotsKey");

        loadMascots();

        if (OMCRegistry.HOOKS.PROTOCOL_LIB.isEnable())
            new MascotsSoundListener();

        for (Mascot mascot : this.mascotsByCityUUID.values()) {
            MascotRegenerationUtils.mascotsRegeneration(mascot);
        }
    }

    @Override
    public Set<Object> getCommands() {
        return Set.of(
                new AdminMascotsCommands()
        );
    }

    @Override
    public Set<ListenerFactory> getListeners() {
        return Set.of(
                MascotsInteractionListener::new,
                MascotsDamageListener::new,
                MascotsDeathListener::new,
                MascotsSleepingListener::new,
                MascotImmuneListener::new,
                MascotsTargetListener::new,
                MascotsRenameListener::new,
                MascotsPotionListener::new,
                MascotsProtectionsListener::new
        );
    }

    @Override
    public void save() {
        this.saveMascots();
    }

    @Override
    public void initDB(ConnectionSource connectionSource) throws SQLException {
        TableUtils.createTableIfNotExists(connectionSource, Mascot.class);
        mascotsDao = DaoManager.createDao(connectionSource, Mascot.class);
    }

    public void loadMascots() {
        try {
            assert mascotsDao != null;
            mascotsDao.queryForAll().forEach(mascot -> {
                mascotsByCityUUID.put(mascot.getCityUUID(), mascot);
                mascotsByEntityUUID.put(mascot.getMascotUUID(), mascot);
            });
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void saveMascots() {
        mascotsByCityUUID.forEach((cityUUID, mascot) -> {
            try {
                mascotsDao.createOrUpdate(mascot);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public City getCityFromEntity(UUID entityUUID) {
        City city = null;

        if (mascotsByEntityUUID.containsKey(entityUUID)) {
            Mascot mascot = mascotsByEntityUUID.get(entityUUID);
            city = mascot.getCity();
        }

        return city;
    }

    public void addMovingMascot(City city) {
        movingMascots.add(city.getUniqueId());
    }

    public void removeMovingMascot(City city) {
        movingMascots.remove(city.getUniqueId());
    }

    public void createMascot(City city, UUID cityUUID, String cityName, World playerWorld, Location mascotSpawn) {
        LivingEntity mob = (LivingEntity) playerWorld.spawnEntity(mascotSpawn, EntityType.ZOMBIE);

        Chunk chunk = mascotSpawn.getChunk();
        setMascotsData(mob, cityName, 300, 300);
        mob.setGlowing(true);

        PersistentDataContainer data = mob.getPersistentDataContainer();
        data.set(mascotsKey, PersistentDataType.STRING, cityUUID.toString());

        Bukkit.getScheduler().runTaskAsynchronously(OMCPlugin.getInstance(), () -> {
            try {
                mascotsDao.create(new Mascot(cityUUID, mob.getUniqueId(), 1, true, true, chunk.getX(), chunk.getZ()));
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });

        MascotsManager mascotsManager = OMCRegistry.CITY_FEATURES.MASCOTS;
        UUID mascotUUID = mob.getUniqueId();
        Mascot newMascot = new Mascot(city.getUniqueId(), mascotUUID, 1, true, true, chunk.getX(), chunk.getZ());
        mascotsManager.getMascotsByCityUUID().put(city.getUniqueId(), newMascot);
        mascotsManager.getMascotsByEntityUUID().put(mascotUUID, newMascot);
    }

    public void removeMascotsFromCity(City city) {
        Mascot mascot = city.getMascot();

        if (mascot == null) return;

        LivingEntity mascotEntity = (LivingEntity) mascot.getEntity();

        if (mascotEntity != null) mascotEntity.remove();

        Bukkit.getScheduler().runTaskAsynchronously(OMCPlugin.getInstance(), () -> {
            try {
                mascotsDao.delete(mascot);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });

        mascotsByCityUUID.remove(mascot.getCityUUID());
        mascotsByEntityUUID.remove(mascot.getMascotUUID());
    }

    public void upgradeMascots(UUID cityUUID) {
        City city = City.of(cityUUID);
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
        mob.getAttribute(Attribute.MAX_HEALTH).setBaseValue(maxHealth);
        if (mob.getHealth() == lastHealth) {
            mob.setHealth(maxHealth);
        }

        mob.customName(getAliveMascotName(
                city.getName(),
                mob.getHealth(),
                maxHealth
        ));
    }

    public void setMascotsData(LivingEntity mob, String cityName, double maxHealth, double baseHealth) {
        mob.setAI(false);

        mob.getAttribute(Attribute.MAX_HEALTH).setBaseValue(maxHealth);
        mob.setHealth(baseHealth);
        mob.setPersistent(true);
        mob.setRemoveWhenFarAway(false);

        mob.customName(getAliveMascotName(
                cityName,
                baseHealth,
                maxHealth
        ));
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

    public Component getAliveMascotName(String cityName, double health, double maxHealth) {
        String formattedHealth = String.format(Locale.US, "%.0f", health);
        String formattedMaxHealth = String.format(Locale.US, "%.0f", maxHealth);
        return TranslationManager.translation(
                "feature.city.mascots.name.alive",
                Component.text(cityName).decorate(TextDecoration.BOLD),
                Component.text(formattedHealth).color(NamedTextColor.RED),
                Component.text("/" + formattedMaxHealth + "♥").color(NamedTextColor.RED)
        );
    }

    public Component getDeadMascotName() {
        return TranslationManager.translation("feature.city.mascots.name.dead");
    }

}
