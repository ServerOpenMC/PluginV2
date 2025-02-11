package fr.openmc.core.features.city.mascots;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.menu.MascotMenu;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import lombok.SneakyThrows;
import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;


import static fr.openmc.core.features.city.CityManager.*;

public class MascotsListener implements Listener {

    // TODO chose à rejouter :
    //TODO trouver un moyen que la mascotte ne se transforme pas a cause d'un éclair
    //TODO faire en sorte que l'objet en puisse pas être jeté/sortie de l'inventaire du joueur
    //TODO rajouter la vérification pour savoir si le joueur est maire/adjoint pour ouvrir le menu de la mascotte
    //TODO trouver un moyen d'empecher la mascotte de bouger/ être bouger

    public static File mascotsFile;
    public static YamlConfiguration mascotsConfig;
    private static NamespacedKey chestKey;
    public static NamespacedKey mascotsKey;
    private final Map<UUID, BukkitRunnable> regenTasks = new HashMap<>();
    private final Map<UUID, BukkitRunnable> cooldownTasks = new HashMap<>();

    @SneakyThrows
    public MascotsListener (OMCPlugin plugin) {
        // TODO remplacer le yml par la db
        mascotsFile = new File(plugin.getDataFolder() + "/data", "mascots.yml");
        loadMascotsConfig();
        chestKey = new NamespacedKey(plugin, "mascots_chest");
        mascotsKey = new NamespacedKey(plugin, "mascotsKey");
        List<String> city_uuids = getAllCityUUIDs();
        for (String uuid : city_uuids) {
            UUID mascotsUUID = getMascotsUUIDbyCityUUID(uuid);
            if (mascotsUUID==null){return;}
            MascotsRegeneration(mascotsUUID);
        }
    }

    @SneakyThrows
    @EventHandler
    private void onChestPlace (BlockPlaceEvent e){
        Player player = e.getPlayer();
        World world = Bukkit.getWorld("world");
        World player_world = player.getWorld();
        ItemStack item = e.getItemInHand();

        if (item.getType() == Material.CHEST) {
            ItemMeta meta = item.getItemMeta();

            if (meta != null) {
                PersistentDataContainer itemData = meta.getPersistentDataContainer();

                if (itemData.has(chestKey, PersistentDataType.STRING) && "id".equals(itemData.get(chestKey, PersistentDataType.STRING))) {

                    if (player_world!=world){
                        MessagesManager.sendMessage(player, Component.text("Impossible de poser le coffre"), Prefix.CITY, MessageType.ERROR, false);
                        return;
                    }

                    if (mascotsConfig.contains("mascots." + getCityUUIDByPlayer(player))){
                        MessagesManager.sendMessage(player, Component.text("Vous possésez déjà une mascotte"), Prefix.CITY, MessageType.ERROR, false);
                        player.getInventory().remove(item);
                        e.setCancelled(true);
                        return;
                    }

                    List<String> city_uuids = getAllCityUUIDs();
                    Block block = e.getBlockPlaced();
                    Location mascot_spawn = new Location(player_world, block.getX()+0.5, block.getY(), block.getZ()+0.5);
                    Chunk chunk = e.getBlock().getChunk();
                    int chunkX = chunk.getX();
                    int chunkZ = chunk.getZ();
                    for (String uuid : city_uuids) {
                        City city = new City(uuid);
                        if (city.hasChunk(chunkX,chunkZ) && !uuid.equals(String.valueOf(getCityUUIDByPlayer(player)))){
                            MessagesManager.sendMessage(player, Component.text("§cImpossible de poser le coffre"), Prefix.CITY, MessageType.ERROR, false);
                            e.setCancelled(true);
                            return;
                        }
                    }

                    player_world.getBlockAt(mascot_spawn).setType(Material.AIR);
                    LivingEntity mob = (LivingEntity) player_world.spawnEntity(mascot_spawn,EntityType.ZOMBIE);

                    mob.setAI(false);
                    mob.setMaxHealth(300);
                    mob.setHealth(300);
                    mob.setPersistent(true);
                    mob.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, Integer.MAX_VALUE, 0, true, true));
                    mob.addPotionEffect(new PotionEffect(PotionEffectType.WATER_BREATHING, Integer.MAX_VALUE, 0, true, true));
                    mob.setCustomName("§lMascotte §c" + mob.getHealth() + "/300❤");
                    mob.setCustomNameVisible(true);
                    PersistentDataContainer data = mob.getPersistentDataContainer();
                    // l'uuid de la ville lui est approprié pour l'identifié
                    data.set(mascotsKey, PersistentDataType.STRING, String.valueOf(getCityUUIDByPlayer(player)));

                    mascotsConfig.set("mascots." + getCityUUIDByPlayer(player) + ".level", String.valueOf(MascotsLevels.level1));
                    mascotsConfig.set("mascots." + getCityUUIDByPlayer(player) + ".uuid", String.valueOf(mob.getUniqueId()));
                    saveMascotsConfig();
                }
            }
        }
    }

    @SneakyThrows
    @EventHandler
    private void onMascotTakeDamage (EntityDamageByEntityEvent e){
        Entity damageEntity = e.getEntity();
        Entity damager = e.getDamager();
        PersistentDataContainer data = damageEntity.getPersistentDataContainer();
        double baseDamage;
        if (data.has(mascotsKey, PersistentDataType.STRING)){
            if (damager instanceof Player player){
                String mascotsUUID = data.get(mascotsKey, PersistentDataType.STRING);
                assert mascotsUUID != null;
                if (mascotsUUID.equals(String.valueOf(getCityUUIDByPlayer(player)))){
                    MessagesManager.sendMessage(player, Component.text("§cVous ne pouvez pas attaquer votre mascotte"), Prefix.CITY, MessageType.INFO, false);
                    e.setCancelled(true);
                    return;
                }
                if (!player.getEquipment().getItemInMainHand().getEnchantments().isEmpty()) {
                    baseDamage = e.getDamage(EntityDamageByEntityEvent.DamageModifier.BASE);
                    e.setDamage(baseDamage);
                }
                LivingEntity mob = (LivingEntity) damageEntity;
                try {
                    double newHealth = Math.floor(mob.getHealth());
                    mob.setHealth(newHealth);
                    double maxHealth = mob.getMaxHealth();
                    mob.setCustomName("§lMascotte §c" + newHealth + "/" + maxHealth + "❤");
                    if (regenTasks.containsKey(damageEntity.getUniqueId())) {
                        regenTasks.get(damageEntity.getUniqueId()).cancel();
                        regenTasks.remove(damageEntity.getUniqueId());
                    }
                    startRegenCooldown(damageEntity.getUniqueId());
                    if (newHealth <= 0) {
                        mob.setHealth(0);
                    }
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
                return;
            }
            e.setCancelled(true);
        }
    }

    @SneakyThrows
    @EventHandler
    private void onInteractWithMascots (PlayerInteractEntityEvent e){
        Player player = e.getPlayer();
        Entity clickEntity = e.getRightClicked();
        PersistentDataContainer data = clickEntity.getPersistentDataContainer();
        if (data.has(mascotsKey, PersistentDataType.STRING)){
            String mascotsUUID = data.get(mascotsKey, PersistentDataType.STRING);
            if (mascotsUUID == null){return;}
            if (mascotsUUID.equals(String.valueOf(getCityUUIDByPlayer(player)))){
                new MascotMenu(player, clickEntity).open();
            } else {
                MessagesManager.sendMessage(player, Component.text("§cCette mascots ne vous appartient pas"), Prefix.CITY, MessageType.ERROR, false);
            }
        }
    }

    private void startRegenCooldown(UUID mascotsUUID) {
        if (cooldownTasks.containsKey(mascotsUUID)) {
            cooldownTasks.get(mascotsUUID).cancel();
            startRegenCooldown(mascotsUUID);
        }

        BukkitRunnable cooldownTask = new BukkitRunnable() {
            @Override
            public void run() {
                MascotsRegeneration(mascotsUUID);
                cooldownTasks.remove(mascotsUUID);
            }
        };

        cooldownTasks.put(mascotsUUID, cooldownTask);
        cooldownTask.runTaskLater(OMCPlugin.getInstance(), 10 * 60 * 20L);
    }

    private void MascotsRegeneration(UUID mascotsUUID) {
        if (regenTasks.containsKey(mascotsUUID)) {
            return;
        }
        BukkitRunnable task = new BukkitRunnable() {
            @Override
            public void run() {
                LivingEntity mascots = (LivingEntity) Bukkit.getEntity(mascotsUUID);
                if (mascots == null || mascots.isDead()) {
                    regenTasks.remove(mascotsUUID);
                    this.cancel();
                    return;
                }

                if (mascots.getHealth() >= mascots.getMaxHealth()) {
                    regenTasks.remove(mascotsUUID);
                    this.cancel();
                    return;
                }

                double newHealth = Math.min(mascots.getHealth() + 1, mascots.getMaxHealth());
                mascots.setHealth(newHealth);
            }
        };

        regenTasks.put(mascotsUUID, task);
        task.runTaskTimer(OMCPlugin.getInstance(), 0L, 60L);
    }

    public static void upgradeMascots (String city_uuid, UUID entityUUID) {
        LivingEntity mob = (LivingEntity) Bukkit.getEntity(entityUUID);
        assert mob != null;
        if (mob.getPersistentDataContainer().has(mascotsKey, PersistentDataType.STRING)){
            loadMascotsConfig();
            MascotsLevels mascotsLevels = MascotsLevels.valueOf((String) mascotsConfig.get("mascots." + city_uuid +".level"));
            double lastHealth = mascotsLevels.getHealth();
            if (mascotsLevels != MascotsLevels.level10){
                int nextLevel = Integer.parseInt(String.valueOf(mascotsLevels).replaceAll("[^0-9]", ""));
                nextLevel += 1;
                mascotsConfig.set("mascots." + city_uuid + ".level", String.valueOf(MascotsLevels.valueOf("level"+nextLevel)));
                saveMascotsConfig();
                mascotsLevels = MascotsLevels.valueOf((String) mascotsConfig.get("mascots." + city_uuid +".level"));
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

    public static void changeMascotsSkin (Entity mascots, EntityType skin) {
        World world = Bukkit.getWorld("world");
        Location mascotsLoc = mascots.getLocation();
        LivingEntity mob = (LivingEntity) mascots;
        double baseHealth = mob.getHealth();
        double maxHealth = mob.getMaxHealth();
        String name = mob.getCustomName();
        String mascotsUUID = mob.getPersistentDataContainer().get(mascotsKey, PersistentDataType.STRING);
        mob.remove();
        assert world != null;
        LivingEntity newMascots = (LivingEntity) world.spawnEntity(mascotsLoc,skin);
        newMascots.setAI(false);
        newMascots.setMaxHealth(maxHealth);
        newMascots.setHealth(baseHealth);
        newMascots.setPersistent(true);
        newMascots.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, Integer.MAX_VALUE, 0, true, true));
        newMascots.addPotionEffect(new PotionEffect(PotionEffectType.WATER_BREATHING, Integer.MAX_VALUE, 0, true, true));
        newMascots.setCustomName(name);
        newMascots.setCustomNameVisible(true);
        PersistentDataContainer newData = newMascots.getPersistentDataContainer();
        assert mascotsUUID != null;
        newData.set(mascotsKey, PersistentDataType.STRING, mascotsUUID);
    }

    public static void RemoveMascotsFromCity (String city_uuid) {
        loadMascotsConfig();
        UUID mascotsUUID = getMascotsUUIDbyCityUUID(city_uuid);
        if (mascotsUUID!=null){
            Entity mascots =  Bukkit.getEntity(getMascotsUUIDbyCityUUID(city_uuid));
            if (mascots!=null){
                mascots.remove();
                mascotsConfig.set("mascots." + city_uuid, null);
            }
        }
    }

    private static UUID getMascotsUUIDbyCityUUID (String city_uuid){
        if (city_uuid==null){
            return null;
        }
        loadMascotsConfig();
        if (!mascotsConfig.contains(city_uuid)){
            return null;
        }
        return (UUID) mascotsConfig.get("mascots." + city_uuid + "uuid");
    }

    public static void GiveChest (Player player) {
        if (!hasAvailableSlot(player)){
            MessagesManager.sendMessage(player, Component.text("Vous n'avez pas assez de place dans votre inventaire"), Prefix.CITY, MessageType.ERROR, false);
            return;
        }
        ItemStack specialChest = new ItemStack(Material.CHEST);
        ItemMeta meta = specialChest.getItemMeta();
        if (meta != null) {
            PersistentDataContainer data = meta.getPersistentDataContainer();
            data.set(chestKey,PersistentDataType.STRING, "id");
            specialChest.setItemMeta(meta);
        } else {
            OMCPlugin.getInstance().getLogger().severe("Erreur lors de l'initialisation de l'ItemMeta du coffre des mascottes");
            return;
        }
        player.getInventory().addItem(specialChest);
    }

    public static boolean hasAvailableSlot(Player player){
        Inventory inv = player.getInventory();
        for (ItemStack item: inv.getContents()) {
            if(item == null) {
                return true;
            }
        }
        return false;
    }

    public static void loadMascotsConfig() {
        if(!mascotsFile.exists()) {
            mascotsFile.getParentFile().mkdirs();
            OMCPlugin.getInstance().saveResource("data/mascots.yml", false);
        }

        mascotsConfig = YamlConfiguration.loadConfiguration(mascotsFile);
    }

    public static void saveMascotsConfig() {
        try {
            mascotsConfig.save(mascotsFile);
            mascotsConfig = YamlConfiguration.loadConfiguration(mascotsFile);
        } catch (IOException e) {
            OMCPlugin.getInstance().getLogger().severe("Impossible de sauvegarder le fichier de configuration des mascots");
            e.printStackTrace();
        }
    }
}
