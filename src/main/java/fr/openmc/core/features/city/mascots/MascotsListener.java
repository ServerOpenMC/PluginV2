package fr.openmc.core.features.city.mascots;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.city.CPermission;
import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.menu.MascotMenu;
import fr.openmc.core.features.city.menu.MascotsDeadMenu;
import fr.openmc.core.utils.chronometer.Chronometer;
import fr.openmc.core.utils.chronometer.ChronometerType;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import lombok.SneakyThrows;
import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

import static fr.openmc.core.features.city.CityManager.*;
import static fr.openmc.core.features.city.mascots.MascotsManager.*;
import static fr.openmc.core.utils.chronometer.Chronometer.startChronometer;

public class MascotsListener implements Listener {


    public static final Map<UUID, BukkitRunnable> regenTasks = new HashMap<>();
    private final Map<UUID, BukkitRunnable> cooldownTasks = new HashMap<>();
    public static List<String> movingMascots = new ArrayList<>();
    private final List<UUID> respawnGive = new ArrayList<>();

    @SneakyThrows
    public MascotsListener () {
        List<String> city_uuids = getAllCityUUIDs();
        for (String uuid : city_uuids) {
            UUID mascotsUUID = getMascotsUUIDbyCityUUID(uuid);
            if (mascotsUUID==null){continue;}
            mascotsRegeneration(mascotsUUID);
            loadMascotsConfig();
            if (mascotsConfig.getBoolean("mascots." + uuid + ".immunity.activate") && mascotsConfig.getBoolean("mascots." + uuid + ".alive")){
                long duration = mascotsConfig.getLong("mascots." + uuid + ".immunity.time");
                startImmunityTimer(uuid, duration);
            }
        }
    }

    @SneakyThrows
    @EventHandler
    void onChestPlace (BlockPlaceEvent e){
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
                        MessagesManager.sendMessage(player, Component.text("§cImpossible de poser le coffre"), Prefix.CITY, MessageType.ERROR, false);
                        e.setCancelled(true);
                        return;
                    }

                    City city = CityManager.getPlayerCity(player.getUniqueId());
                    if (city==null){
                        MessagesManager.sendMessage(player,MessagesManager.Message.PLAYERNOCITY.getMessage(), Prefix.CITY, MessageType.ERROR, false);
                        e.setCancelled(true);
                        return;
                    }
                    String city_uuid = city.getUUID();

                    if (mascotsConfig.contains("mascots." + city_uuid) && !movingMascots.contains(city_uuid)){
                        MessagesManager.sendMessage(player, Component.text("§cVous possésez déjà une mascotte"), Prefix.CITY, MessageType.ERROR, false);
                        player.getInventory().remove(item);
                        e.setCancelled(true);
                        return;
                    }

                    Block block = e.getBlockPlaced();
                    Location mascot_spawn = new Location(player_world, block.getX()+0.5, block.getY(), block.getZ()+0.5);
                    Chunk chunk = e.getBlock().getChunk();
                    int chunkX = chunk.getX();
                    int chunkZ = chunk.getZ();

                    if (!city.hasChunk(chunkX,chunkZ)){
                        MessagesManager.sendMessage(player, Component.text("§cImpossible de poser le coffre"), Prefix.CITY, MessageType.ERROR, false);
                        e.setCancelled(true);
                        return;
                    }

                    player_world.getBlockAt(mascot_spawn).setType(Material.AIR);

                    if (movingMascots.contains(city_uuid)){
                        Entity mob = Bukkit.getEntity(getMascotsUUIDbyCityUUID(city_uuid));
                        if (mob!=null){
                            mob.teleport(mascot_spawn);
                            movingMascots.remove(city_uuid);
                            Chronometer.stopChronometer(player, "mascotsMove", ChronometerType.ACTION_BAR, "mascotte déplacer");
                            //Cooldown de 5h pour déplacer la mascottes ( se reset au relancement du serv )
                            startChronometer(mob,"mascotsCooldown", 3600*5, null, "%null%", null, "%null%");
                            return;
                        }
                    }

                    spawnMascot(city_uuid, player_world, mascot_spawn);
                }
            }
        }
    }

    @SneakyThrows
    @EventHandler
    void onMascotTakeDamage (EntityDamageByEntityEvent e){
        Entity damageEntity = e.getEntity();
        Entity damager = e.getDamager();
        PersistentDataContainer data = damageEntity.getPersistentDataContainer();
        double baseDamage;

        if (data.has(mascotsKey, PersistentDataType.STRING)){

            if (damager instanceof Player player){

                String mascotsUUID = data.get(mascotsKey, PersistentDataType.STRING);
                if (mascotsUUID!=null) {
                    if (mascotsConfig.getBoolean("mascots." + mascotsUUID + ".immunity.activate")){
                        MessagesManager.sendMessage(player, Component.text("§cCette mascotte est immuniser pour le moment"), Prefix.CITY, MessageType.INFO, false);
                        e.setCancelled(true);
                        return;
                    }

                    City city = CityManager.getPlayerCity(player.getUniqueId());
                    if (city == null) {
                        MessagesManager.sendMessage(player, Component.text( MessagesManager.Message.PLAYERNOCITY.getMessage() + "" +
                                "vous ne pouvez donc pas attaquer cette mascots"), Prefix.CITY, MessageType.ERROR, false);
                        e.setCancelled(true);
                        return;
                    }
                    String city_uuid = city.getUUID();

                    if (mascotsUUID.equals(city_uuid)){
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

            }
            e.setCancelled(true);
        }
    }

    @SneakyThrows
    @EventHandler
    void onInteractWithMascots (PlayerInteractEntityEvent e){
        Player player = e.getPlayer();
        Entity clickEntity = e.getRightClicked();
        PersistentDataContainer data = clickEntity.getPersistentDataContainer();

        if (data.has(mascotsKey, PersistentDataType.STRING)){

            String mascotsUUID = data.get(mascotsKey, PersistentDataType.STRING);
            if (mascotsUUID == null){return;}

            City city = CityManager.getPlayerCity(player.getUniqueId());

            if (city == null) {
                MessagesManager.sendMessage(player, MessagesManager.Message.PLAYERNOCITY.getMessage(), Prefix.CITY, MessageType.ERROR, false);
                return;
            }

            String city_uuid = city.getUUID();
            if (city.hasPermission(player.getUniqueId(), CPermission.PERMS)){
                if (mascotsUUID.equals(city_uuid)){
                    loadMascotsConfig();
                    if (!mascotsConfig.getBoolean("mascots." + city_uuid + ".alive")){
                        new MascotsDeadMenu(player, city_uuid).open();
                        return;
                    }
                    new MascotMenu(player, clickEntity).open();
                } else {
                    MessagesManager.sendMessage(player, Component.text("§cCette mascots ne vous appartient pas"), Prefix.CITY, MessageType.ERROR, false);
                }
            } else {
                MessagesManager.sendMessage(player, Component.text("§cVous n'avez pas la permission de faire cela"), Prefix.CITY, MessageType.ERROR, false);
            }
        }
    }

    @EventHandler
    public void onTransform(EntityTransformEvent event) {
        Entity entity = event.getEntity();
        PersistentDataContainer data = entity.getPersistentDataContainer();
        if (data.has(mascotsKey, PersistentDataType.STRING)){
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPortal(EntityPortalEvent event) {
        Entity entity = event.getEntity();
        PersistentDataContainer data = entity.getPersistentDataContainer();
        if (data.has(mascotsKey, PersistentDataType.STRING)){
            event.setCancelled(true);
        }
    }

    @EventHandler
    void onMascotDied (EntityDeathEvent e){
        Entity entity = e.getEntity();
        PersistentDataContainer data = entity.getPersistentDataContainer();
        if (data.has(mascotsKey, PersistentDataType.STRING)){
            loadMascotsConfig();
            String city_uuid = data.get(mascotsKey, PersistentDataType.STRING);
            mascotsConfig.set("mascots." + city_uuid + ".immunity.activate", true);
            mascotsConfig.set("mascots." + city_uuid + ".alive", false);
            saveMascotsConfig();
            entity.setCustomName("§lMascotte en attente de §csoins");
            e.setCancelled(true);
        }
    }

    @EventHandler
    void onItemDrop(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItemDrop().getItemStack();
        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer itemData = meta.getPersistentDataContainer();
        if (itemData.has(chestKey, PersistentDataType.STRING) && "id".equals(itemData.get(chestKey, PersistentDataType.STRING))) {
            event.setCancelled(true);
            MessagesManager.sendMessage(player, Component.text("§cVous ne pouvez pas jéter cette objet"), Prefix.CITY, MessageType.ERROR, false);

        }
    }

    @EventHandler
    void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        ItemStack item =  event.getCursor();
        ItemMeta meta = item.getItemMeta();
        if (meta==null){
            return;
        }
        PersistentDataContainer itemData = meta.getPersistentDataContainer();
        if (itemData.has(chestKey, PersistentDataType.STRING) && "id".equals(itemData.get(chestKey, PersistentDataType.STRING))) {
            if (event.getInventory().getType() != InventoryType.PLAYER &&
                    event.getInventory().getType() != InventoryType.CREATIVE &&
                    event.getInventory().getType() != InventoryType.CRAFTING ) {
                player.sendMessage("" + event.getInventory().getType());
                event.setCancelled(true);
                MessagesManager.sendMessage(player, Component.text("§cVous ne pouvez pas déplacer cette objet ici"), Prefix.CITY, MessageType.ERROR, false);
                return;
            }
            InventoryType.SlotType slotType = event.getSlotType();
            if (slotType == InventoryType.SlotType.CRAFTING) {
                event.setCancelled(true);
                MessagesManager.sendMessage(player, Component.text("§cVous ne pouvez pas déplacer cette objet ici"), Prefix.CITY, MessageType.ERROR, false);
                return;
            }
            if (event.getClick() == ClickType.DROP || event.getClick() == ClickType.CONTROL_DROP) {
                event.setCancelled(true);
                MessagesManager.sendMessage(player, Component.text("§cVous ne pouvez pas jéter cette objet"), Prefix.CITY, MessageType.ERROR, false);
            }
        }
    }

    @EventHandler
    void onPlayerDeath(PlayerDeathEvent event) {
        for (ItemStack item : event.getDrops()) {
            ItemMeta meta = item.getItemMeta();
            PersistentDataContainer itemData = meta.getPersistentDataContainer();
            if (itemData.has(chestKey, PersistentDataType.STRING) && "id".equals(itemData.get(chestKey, PersistentDataType.STRING))) {
                event.getDrops().remove(item);
                respawnGive.add(event.getPlayer().getUniqueId());
                break;
            }
        }
    }

    @EventHandler
    void onPlayerRespawn (PlayerRespawnEvent e) {
        Player player = e.getPlayer();
        if (respawnGive.contains(player.getUniqueId())){
            respawnGive.remove(player.getUniqueId());
            giveChest(player);
        }
    }

    @EventHandler
    void onTimeEnd (Chronometer.ChronometerEndEvent e){
        Entity entity = e.getEntity();
        String group = e.getGroup();
        if (group.equals("mascotsMove") && entity instanceof Player player){
            City city = CityManager.getPlayerCity(player.getUniqueId());
            assert city != null;
            String city_uuid = city.getUUID();
            movingMascots.remove(city_uuid);
            Entity mascot = Bukkit.getEntity(getMascotsUUIDbyCityUUID(city_uuid));
            if (mascot!=null){
                startChronometer(mascot,"mascotsCooldown", 3600*5, null, "%null%", null, "%null%");
            }
        }
    }

    private void startRegenCooldown(UUID mascotsUUID) {
        if (cooldownTasks.containsKey(mascotsUUID)) {
            cooldownTasks.get(mascotsUUID).cancel();
        }

        BukkitRunnable cooldownTask = new BukkitRunnable() {
            @Override
            public void run() {
                mascotsRegeneration(mascotsUUID);
                cooldownTasks.remove(mascotsUUID);
            }
        };

        cooldownTasks.put(mascotsUUID, cooldownTask);
        cooldownTask.runTaskLater(OMCPlugin.getInstance(), 10 * 60 * 20L);
    }

    public static void mascotsRegeneration(UUID mascotsUUID) {
        if (regenTasks.containsKey(mascotsUUID)) {
            return;
        }
        if (Bukkit.getEntity(mascotsUUID)!=null){
            PersistentDataContainer data = Bukkit.getEntity(mascotsUUID).getPersistentDataContainer();
            if (data.has(mascotsKey, PersistentDataType.STRING)){
                String city_uuid = data.get(mascotsKey, PersistentDataType.STRING);
                loadMascotsConfig();
                if (!mascotsConfig.contains("mascots." + city_uuid)){
                    regenTasks.remove(mascotsUUID);
                    return;
                }
                if (!mascotsConfig.getBoolean("mascots." + city_uuid + ".alive")){return;}
            }
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
                    mascots.setCustomName("§lMascotte §c" + mascots.getHealth() + "/" + mascots.getMaxHealth() + "❤");
                    regenTasks.remove(mascotsUUID);
                    this.cancel();
                    return;
                }

                double newHealth = Math.min(mascots.getHealth() + 1, mascots.getMaxHealth());
                mascots.setHealth(newHealth);
                mascots.setCustomName("§lMascotte §c" + mascots.getHealth() + "/" + mascots.getMaxHealth() + "❤");
            }
        };

        regenTasks.put(mascotsUUID, task);
        task.runTaskTimer(OMCPlugin.getInstance(), 0L, 60L);
    }

    public static void startImmunityTimer(String city_uuid, long duration) {
        BukkitRunnable immunityTask = new BukkitRunnable() {
            long endTime = duration;
            @Override
            public void run() {
                loadMascotsConfig();
                if (!mascotsConfig.contains("mascots." + city_uuid)){
                    this.cancel();
                    return;
                }
                if (endTime == 0){
                    mascotsConfig.set("mascots." + city_uuid + ".immunity.activate", false);
                    mascotsConfig.set("mascots." + city_uuid + ".immunity.time", 0);
                    saveMascotsConfig();
                    this.cancel();
                    return;
                }
                endTime -= 1;
                mascotsConfig.set("mascots." + city_uuid + ".immunity.time", endTime);
                saveMascotsConfig();
            }
        };
        immunityTask.runTaskTimer(OMCPlugin.getInstance(), 1200L, 1200L); // Vérifie chaque minute
    }
}
