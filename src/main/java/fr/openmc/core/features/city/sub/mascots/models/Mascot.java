package fr.openmc.core.features.city.sub.mascots.models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import fr.openmc.api.cooldown.DynamicCooldownManager;
import fr.openmc.core.OMCRegistry;
import fr.openmc.core.features.city.models.city.City;
import fr.openmc.core.features.city.sub.mascots.MascotsManager;
import fr.openmc.core.utils.bukkit.ItemUtils;
import fr.openmc.core.utils.text.messages.MessageType;
import fr.openmc.core.utils.text.messages.MessagesManager;
import fr.openmc.core.utils.text.messages.Prefix;
import fr.openmc.core.utils.text.messages.TranslationManager;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.UUID;

@Getter
@Setter
@DatabaseTable(tableName = "mascots")
public class Mascot {
    private final MascotsManager mascotsManager = OMCRegistry.CITY_FEATURES.MASCOTS;

    @DatabaseField(id = true)
    private UUID cityUUID;
    @DatabaseField(canBeNull = false)
    private int level;
    @DatabaseField(canBeNull = false)
    private UUID mascotUUID;
    @DatabaseField(canBeNull = false)
    private boolean immunity;
    @DatabaseField(canBeNull = false)
    private boolean alive;
    @DatabaseField(canBeNull = false)
    private int x;
    @DatabaseField(canBeNull = false)
    private int z;

    private City city;
    Mascot() {
        // required by ORMLite
    }

    public Mascot(UUID cityUUID, UUID mascotUUID, int level, boolean immunity, boolean alive, int x, int z) {
        this.cityUUID = cityUUID;
        this.level = level;
        this.mascotUUID = mascotUUID;
        this.immunity = immunity;
        this.alive = alive;
        this.x = x;
        this.z = z;
    }

    public Chunk getChunk() {
        return Bukkit.getWorld("world").getChunkAt(x, z);
    }

    public void setChunk(Chunk chunk) {
        this.x = chunk.getX();
        this.z = chunk.getZ();
    }

    public Material getMascotEgg() {
        LivingEntity entity = (LivingEntity) this.getEntity();
        if (entity == null) {
            return Material.BARRIER; // Default fallback
        }
        String eggName = entity.getType().name() + "_SPAWN_EGG";
        if (Material.matchMaterial(eggName) == null) {
            return Material.BARRIER;
        }
        return Material.matchMaterial(eggName);
    }

    public Entity getEntity() {
        boolean toUnload = false;
        Chunk chunk = this.getChunk();
        if (!chunk.isLoaded()) {
            chunk.load();
            toUnload = true;
        }
        UUID mascot_uuid = this.getMascotUUID();
        if (mascot_uuid == null) {
            return null;
        }
        LivingEntity mob = (LivingEntity) Bukkit.getEntity(mascot_uuid);
        if (mob == null) {
            return null;
        }
        if (toUnload) chunk.unload();
        return mob;
    }

    public void changeMascotsSkin(EntityType skin, Player player, int aywenite) {
        World world = Bukkit.getWorld("world");
        LivingEntity entityMascot = (LivingEntity) this.getEntity();
        Location mascotsLoc = entityMascot.getLocation();
        UUID mascotUUID = entityMascot.getUniqueId();

        boolean glowing = entityMascot.isGlowing();
        long cooldown = 0;
        boolean hasCooldown = false;

        // to avoid the suffocation of the mascot when it changes skin to a spider for exemple
        if (mascotsLoc.clone().add(0, 1, 0).getBlock().getType().isSolid() && entityMascot.getHeight() <= 1.0) {
            MessagesManager.sendMessage(player,
                    TranslationManager.translation("feature.city.mascots.skin.error.space_above"),
                    Prefix.CITY, MessageType.INFO, false);
            return;
        }

        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                Location checkLoc = mascotsLoc.clone().add(x, 0, z);
                Material blockType = checkLoc.getBlock().getType();

                if (blockType != Material.AIR) {
                    MessagesManager.sendMessage(player,
                            TranslationManager.translation("feature.city.mascots.skin.error.space_around"),
                            Prefix.CITY, MessageType.INFO, false);
                    return;
                }
            }
        }

        double baseHealth = entityMascot.getHealth();
        double maxHealth = entityMascot.getAttribute(Attribute.MAX_HEALTH).getValue();
        String cityUUID = entityMascot.getPersistentDataContainer().get(mascotsManager.getMascotsKey(), PersistentDataType.STRING);

        if (!DynamicCooldownManager.isReady(this.getMascotUUID(), "mascots:move")) {
            cooldown = DynamicCooldownManager.getRemaining(this.getMascotUUID(), "mascots:move");
            hasCooldown = true;
            DynamicCooldownManager.clear(entityMascot.getUniqueId(), "mascots:move", false);
        }

        entityMascot.remove();

        if (world == null) return;

        LivingEntity newMascots = (LivingEntity) world.spawnEntity(mascotsLoc, skin);
        newMascots.setGlowing(glowing);

        if (hasCooldown) {
            DynamicCooldownManager.use(newMascots.getUniqueId(), "mascots:move", cooldown);
        }

        mascotsManager.setMascotsData(newMascots, this.getCity().getName(), maxHealth, baseHealth);
        PersistentDataContainer newData = newMascots.getPersistentDataContainer();
        mascotsManager.getMascotsByEntityUUID().remove(mascotUUID);
        mascotsManager.getMascotsByEntityUUID().put(newMascots.getUniqueId(), this);

        if (cityUUID != null) {
            newData.set(mascotsManager.getMascotsKey(), PersistentDataType.STRING, cityUUID);
            this.setMascotUUID(newMascots.getUniqueId());
        }

        ItemUtils.takeAywenite(player, aywenite);
    }

    public void updateDisplayName(LivingEntity entityMascot, double damage) {
        double newHealth = Math.floor(entityMascot.getHealth());
        entityMascot.setHealth(newHealth);
        AttributeInstance maxHealthInst = entityMascot.getAttribute(Attribute.MAX_HEALTH);
        if (maxHealthInst == null) return;
        double maxHealth = maxHealthInst.getValue();

        double healthAfterDamage = entityMascot.getHealth() - damage;
        if (healthAfterDamage < 0) healthAfterDamage = 0;

        if (!this.isAlive()) {
            entityMascot.customName(mascotsManager.getDeadMascotName());
        } else {
            entityMascot.customName(mascotsManager.getAliveMascotName(
                    this.getCity().getName(),
                    healthAfterDamage,
                    maxHealth
            ));
        }
    }

    public City getCity() {
        if (this.city != null) {
            return this.city;
        }
        this.city = City.of(this.cityUUID);
        return this.city;
    }

    @Override
    public String toString() {
        return "Mascot{" +
                "cityUUID='" + cityUUID + '\'' +
                ", level=" + level +
                ", mascotUUID=" + mascotUUID +
                ", immunity=" + immunity +
                ", alive=" + alive +
                ", x=" + x +
                ", z=" + z +
                '}';
    }
}
