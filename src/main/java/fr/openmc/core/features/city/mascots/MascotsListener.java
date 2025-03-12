package fr.openmc.core.features.city.mascots;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.city.CPermission;
import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.commands.CityCommands;
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
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.*;
import org.bukkit.event.weather.LightningStrikeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

import static fr.openmc.core.features.city.CityManager.*;
import static fr.openmc.core.features.city.mascots.MascotsManager.*;
import static fr.openmc.core.utils.chronometer.Chronometer.startChronometer;
import static fr.openmc.core.utils.chronometer.Chronometer.stopChronometer;

public class MascotsListener implements Listener {


    public static final Map<UUID, BukkitRunnable> regenTasks = new HashMap<>();
    private final Map<UUID, BukkitRunnable> cooldownTasks = new HashMap<>();
    public static List<String> movingMascots = new ArrayList<>();
    private final List<UUID> respawnGive = new ArrayList<>();

    @SneakyThrows
    public MascotsListener () {

        List<String> city_uuids = getAllCityUUIDs();
        if (city_uuids!=null){
            for (String city_uuid : city_uuids) {
                UUID mascotsUUID = getMascotsUUIDbyCityUUID(city_uuid);
                if (mascotsUUID==null){continue;}
                mascotsRegeneration(mascotsUUID);
                loadMascotsConfig();
                if (mascotsConfig.getBoolean("mascots." + city_uuid + ".immunity.activate") && mascotsConfig.getBoolean("mascots." + city_uuid + ".alive")){
                    long duration = mascotsConfig.getLong("mascots." + city_uuid + ".immunity.time");
                    startImmunityTimer(city_uuid, duration);
                }
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
                        OMCPlugin.getInstance().getLogger().info("error world");
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
                        MessagesManager.sendMessage(player, Component.text("§cVous possédez déjà une mascotte"), Prefix.CITY, MessageType.ERROR, false);
                        player.getInventory().remove(item);
                        e.setCancelled(true);
                        return;
                    }

                    Block block = e.getBlockPlaced();
                    Location mascot_spawn = new Location(player_world, block.getX()+0.5, block.getY(), block.getZ()+0.5);

                    if (mascot_spawn.clone().add(0, 1, 0).getBlock().getType().isSolid()) {
                        MessagesManager.sendMessage(player, Component.text("§cIl y a un block au dessus"), Prefix.CITY, MessageType.ERROR, false);
                        e.setCancelled(true);
                        return;
                    }

                    Chunk chunk = e.getBlock().getChunk();
                    int chunkX = chunk.getX();
                    int chunkZ = chunk.getZ();

                    if (!city.hasChunk(chunkX,chunkZ)){
                        MessagesManager.sendMessage(player, Component.text("§cImpossible de poser le coffre"), Prefix.CITY, MessageType.ERROR, false);
                        OMCPlugin.getInstance().getLogger().info("error chunk");
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
                    stopChronometer(player, "Mascot:chest", null, "%null%");
                    mascotSpawn.remove(player.getUniqueId());
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

                String mascotsID = data.get(mascotsKey, PersistentDataType.STRING);

                if (mascotsID!=null) {

                    City city = CityManager.getPlayerCity(player.getUniqueId());
                    if (city == null) {
                        MessagesManager.sendMessage(player, MessagesManager.Message.PLAYERNOCITY.getMessage(), Prefix.CITY, MessageType.ERROR, false);
                        e.setCancelled(true);
                        return;
                    }
                    String city_uuid = city.getUUID();

                    String city_type = CityManager.getCityType(city_uuid);

                    if (city_type==null){
                        return;
                    }

                    if (city_type.equals("peace")){
                        MessagesManager.sendMessage(player, Component.text("§cCette ville est en situation de §apaix"), Prefix.CITY, MessageType.INFO, false);
                        e.setCancelled(true);
                        return;
                    }

                    if (mascotsConfig.getBoolean("mascots." + mascotsID + ".immunity.activate")){
                        MessagesManager.sendMessage(player, Component.text("§cCette mascotte est immunisée pour le moment"), Prefix.CITY, MessageType.INFO, false);
                        e.setCancelled(true);
                        return;
                    }

                    if (mascotsID.equals(city_uuid)){
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
                        CityCommands.startBalanceCooldown(city_uuid);

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
                    MessagesManager.sendMessage(player, Component.text("§cCette mascotte ne vous appartient pas"), Prefix.CITY, MessageType.ERROR, false);
                }
            } else {
                MessagesManager.sendMessage(player, Component.text("§cVous n'avez pas la permission de faire cela"), Prefix.CITY, MessageType.ERROR, false);
            }
        }
    }

    @EventHandler
    void onLightningStrike(LightningStrikeEvent e) {
        Location strikeLocation = e.getLightning().getLocation();

        for (Entity entity : strikeLocation.getWorld().getNearbyEntities(strikeLocation, 3, 3, 3)) {
            if (entity instanceof LivingEntity) {
                PersistentDataContainer data = entity.getPersistentDataContainer();
                if (data.has(mascotsKey, PersistentDataType.STRING)) {
                    e.setCancelled(true);
                    return;
                }
            }
        }
    }

    @EventHandler
    void onPistonExtend(BlockPistonExtendEvent e) {
        Location pistonHeadLocation = e.getBlock().getRelative(e.getDirection()).getLocation();
        for (Entity entity : pistonHeadLocation.getWorld().getNearbyEntities(pistonHeadLocation, 0.5, 0.5, 0.5)) {
            if (entity instanceof LivingEntity) {
                PersistentDataContainer data = entity.getPersistentDataContainer();
                if (data.has(mascotsKey, PersistentDataType.STRING)) {
                    e.setCancelled(true);
                    return;
                }
            }
        }
        for (Block block : e.getBlocks()) {
            Location futureLocation = block.getRelative(e.getDirection()).getLocation();
            for (Entity entity : block.getWorld().getNearbyEntities(futureLocation, 0.5, 0.5, 0.5)) {
                if (entity instanceof LivingEntity) {
                    PersistentDataContainer data = entity.getPersistentDataContainer();
                    if (data.has(mascotsKey, PersistentDataType.STRING)) {
                        e.setCancelled(true);
                        return;
                    }
                }
            }
        }
    }

    @EventHandler
    void onTransform(EntityTransformEvent event) {
        Entity entity = event.getEntity();
        PersistentDataContainer data = entity.getPersistentDataContainer();
        if (data.has(mascotsKey, PersistentDataType.STRING)){
            event.setCancelled(true);
        }
    }

    @EventHandler
    void onPortal(EntityPortalEvent event) {
        Entity entity = event.getEntity();
        PersistentDataContainer data = entity.getPersistentDataContainer();
        if (data.has(mascotsKey, PersistentDataType.STRING)){
            event.setCancelled(true);
        }
    }

    @EventHandler
    void onMascotDied (EntityDeathEvent e){
        Entity entity = e.getEntity();
        Player killer = e.getEntity().getKiller();

        PersistentDataContainer data = entity.getPersistentDataContainer();
        if (data.has(mascotsKey, PersistentDataType.STRING)){

            loadMascotsConfig();
            String city_uuid = data.get(mascotsKey, PersistentDataType.STRING);
            String level = mascotsConfig.getString("mascots." + city_uuid + ".level");
            mascotsConfig.set("mascots." + city_uuid + ".immunity.activate", true);
            mascotsConfig.set("mascots." + city_uuid + ".alive", false);
            saveMascotsConfig();

            entity.setCustomName("§lMascotte en attente de §csoins");
            e.setCancelled(true);

            City city = CityManager.getCity(city_uuid);
            if (city!=null ) {
                for (UUID townMember : city.getMembers()){
                    if (Bukkit.getEntity(townMember) instanceof Player player){
                        for (PotionEffect potionEffect : MascotsLevels.valueOf(level).getBonus()){
                            player.removePotionEffect(potionEffect.getType());
                        }
                        giveMascotsEffect(city_uuid, townMember);
                    }
                }

                if (killer==null){
                    return;
                }
                City cityEnemy = CityManager.getPlayerCity(killer.getUniqueId());
                if (cityEnemy!=null){
                    int point;

                    if (level!=null){
                        point = Integer.parseInt(level.replaceAll("[^0-9]", ""));
                    } else {
                        point = 1;
                    }

                    cityEnemy.updatePowerPoints(point);
                    city.updatePowerPoints(-point);

                    cityEnemy.updateBalance(0.15*city.getBalance()/100);
                    city.updateBalance(-(0.15*city.getBalance()/100));
                }

            }
        }
    }

    @EventHandler
    void onAxolotlBucket (PlayerBucketEntityEvent e) {
        Entity entity = e.getEntity();
        PersistentDataContainer data = entity.getPersistentDataContainer();
        if (data.has(mascotsKey, PersistentDataType.STRING)){
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
            MessagesManager.sendMessage(player, Component.text("§cVous ne pouvez pas jeter cette objet"), Prefix.CITY, MessageType.ERROR, false);

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
                MessagesManager.sendMessage(player, Component.text("§cVous ne pouvez pas jeter cette objet"), Prefix.CITY, MessageType.ERROR, false);
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
    public void onPlayerJoin (PlayerJoinEvent e) {
        Player player = e.getPlayer();
        City city = CityManager.getPlayerCity(player.getUniqueId());
        if (city != null){
            String city_uuid = city.getUUID();
            giveMascotsEffect(city_uuid, player.getUniqueId());
        }
    }

    @EventHandler
    void onPlayerQuit (PlayerQuitEvent e) {
        Player player = e.getPlayer();
        City city = CityManager.getPlayerCity(player.getUniqueId());
        if (city == null) {
            return;
        }
        String city_uuid = city.getUUID();
        for (ItemStack item : player.getInventory().getContents()){
            if (item!=null){
                ItemMeta itemMeta = item.getItemMeta();
                if (itemMeta==null){continue;}
                PersistentDataContainer data = itemMeta.getPersistentDataContainer();
                if (data.has(chestKey, PersistentDataType.STRING) && data.get(chestKey, PersistentDataType.STRING)=="id"){
                    player.getInventory().remove(item);
                    if (Chronometer.containsChronometer(player.getUniqueId(), "mascotsMove")){
                        Entity mob = Bukkit.getEntity(getMascotsUUIDbyCityUUID(city_uuid));
                        if (mob!=null){
                            startChronometer(mob,"mascotsCooldown", 3600*5, null, "%null%", null, "%null%");
                        }
                        return;
                    }
                    if (mascotSpawn.containsKey(player.getUniqueId())){
                        Location loc = mascotSpawn.get(player.getUniqueId());
                        spawnMascot(city_uuid, loc.getWorld(), loc);
                        mascotSpawn.remove(player.getUniqueId());
                    }
                    break;
                }
            }
        }
    }

    @EventHandler
    void onTimeEnd (Chronometer.ChronometerEndEvent e){
        Entity entity = e.getEntity();
        String group = e.getGroup();
        if (group.equals("mascotsMove") && entity instanceof Player player){
            City city = CityManager.getPlayerCity(player.getUniqueId());
            if (city==null){
                return;
            }
            String city_uuid = city.getUUID();
            movingMascots.remove(city_uuid);
            Entity mascot = Bukkit.getEntity(getMascotsUUIDbyCityUUID(city_uuid));
            if (mascot!=null){
                startChronometer(mascot,"mascotsCooldown", 3600*5, null, "%null%", null, "%null%");
            }
            return;
        }

        if (group.equals("Mascot:chest") && entity instanceof Player player){
            City city = CityManager.getPlayerCity(player.getUniqueId());
            if (city==null){
                return;
            }
            MascotsManager.removeChest(player);
            String city_uuid = city.getUUID();
            Location mascot = mascotSpawn.get(player.getUniqueId());
            spawnMascot(city_uuid, mascot.getWorld(), mascot);
            mascotSpawn.remove(player.getUniqueId());
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
