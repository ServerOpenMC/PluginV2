package fr.openmc.core.features.city.mascots;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.city.CPermission;
import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.menu.MascotMenu;
import fr.openmc.core.utils.chronometer.Chronometer;
import fr.openmc.core.utils.chronometer.ChronometerType;
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
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
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
import java.util.*;

import static fr.openmc.core.features.city.CityManager.*;
import static fr.openmc.core.utils.chronometer.Chronometer.startChronometer;

public class MascotsListener implements Listener {

    public static File mascotsFile;
    public static YamlConfiguration mascotsConfig;
    private static NamespacedKey chestKey;
    public static NamespacedKey mascotsKey;
    private final Map<UUID, BukkitRunnable> regenTasks = new HashMap<>();
    private final Map<UUID, BukkitRunnable> cooldownTasks = new HashMap<>();
    public static Map<String, Integer> freeClaim = new HashMap<>();
    public static List<String> movingMascots = new ArrayList<>();
    private final List<UUID> respawnGive = new ArrayList<>();

    @SneakyThrows
    public MascotsListener (OMCPlugin plugin) {

        //changement du spigot.yml pour permettre au mascottes d'avoir 3000 coeurs
        File spigotYML = new File("spigot.yml");
        YamlConfiguration spigotYMLConfig = YamlConfiguration.loadConfiguration(spigotYML);
        spigotYMLConfig.set("settings.attribute.maxHealth.max", 6000.0);
        try {
            spigotYMLConfig.save(new File("spigot.yml"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        mascotsFile = new File(plugin.getDataFolder() + "/data", "mascots.yml");
        loadMascotsConfig();

        if (mascotsConfig.getConfigurationSection("data")!=null){
            for (String city_uuid : mascotsConfig.getConfigurationSection("data").getKeys(false)){
                freeClaim.put(city_uuid, mascotsConfig.getInt("data." + city_uuid));
            }
        }

        mascotsConfig.set("data", null);
        saveMascotsConfig();
        chestKey = new NamespacedKey(plugin, "mascots_chest");
        mascotsKey = new NamespacedKey(plugin, "mascotsKey");

        List<String> city_uuids = getAllCityUUIDs();
        for (String uuid : city_uuids) {
            UUID mascotsUUID = getMascotsUUIDbyCityUUID(uuid);
            if (mascotsUUID==null){continue;}
            mascotsRegeneration(mascotsUUID);
            loadMascotsConfig();
            if (mascotsConfig.getBoolean("mascots." + uuid + ".immunity.activate")){
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
                        return;
                    }

                    City city = CityManager.getPlayerCity(player.getUniqueId());
                    assert city != null;
                    String city_uuid = city.getUUID();

                    if (mascotsConfig.contains("mascots." + city_uuid) && !movingMascots.contains(city_uuid)){
                        MessagesManager.sendMessage(player, Component.text("§cVous possésez déjà une mascotte"), Prefix.CITY, MessageType.ERROR, false);
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
                        City citys = new City(uuid);
                        if (citys.hasChunk(chunkX,chunkZ) && !uuid.equals(city_uuid)){
                            MessagesManager.sendMessage(player, Component.text("§cImpossible de poser le coffre"), Prefix.CITY, MessageType.ERROR, false);
                            e.setCancelled(true);
                            return;
                        }
                    }

                    player_world.getBlockAt(mascot_spawn).setType(Material.AIR);

                    if (movingMascots.contains(city_uuid)){
                        Entity mob = Bukkit.getEntity(getMascotsUUIDbyCityUUID(city_uuid));
                        if (mob!=null){
                            mob.teleport(mascot_spawn);
                            movingMascots.remove(city_uuid);
                            Chronometer.stopChronometer(player, "mascotsMove", ChronometerType.ACTION_BAR, "mascotte déplacer");
                            startChronometer(mob,"mascotsCooldown", 30, null, "%null%", null, "%null%");
                            return;
                        }
                    }

                    LivingEntity mob = (LivingEntity) player_world.spawnEntity(mascot_spawn,EntityType.ZOMBIE);

                    setMascotsData(mob,null, 300, 300);

                    PersistentDataContainer data = mob.getPersistentDataContainer();
                    // l'uuid de la ville lui est approprié pour l'identifié
                    data.set(mascotsKey, PersistentDataType.STRING, city_uuid);

                    loadMascotsConfig();
                    mascotsConfig.set("mascots." + city_uuid + ".level", String.valueOf(MascotsLevels.level1));
                    mascotsConfig.set("mascots." + city_uuid + ".uuid", String.valueOf(mob.getUniqueId()));
                    mascotsConfig.set("mascots." + city_uuid + ".immunity.activate", true);
                    mascotsConfig.set("mascots." + city_uuid + ".immunity.time", 10080); // en minute
                    saveMascotsConfig();
                    startImmunityTimer(city_uuid, 10080);
                    freeClaim.put(city_uuid, 25);
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
                // 3600*5
                startChronometer(mascot,"mascotsCooldown", 30, null, "%null%", null, "%null%");
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
                mascotsRegeneration(mascotsUUID);
                cooldownTasks.remove(mascotsUUID);
            }
        };

        cooldownTasks.put(mascotsUUID, cooldownTask);
        cooldownTask.runTaskLater(OMCPlugin.getInstance(), 10 * 60 * 20L);
    }

    private void mascotsRegeneration(UUID mascotsUUID) {
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

    private void startImmunityTimer(String city_uuid, long duration) {
        BukkitRunnable immunityTask = new BukkitRunnable() {
            long endTime = duration;
            @Override
            public void run() {
                loadMascotsConfig();
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
        String mascotsCustomUUID = mob.getPersistentDataContainer().get(mascotsKey, PersistentDataType.STRING);
        mob.remove();

        if (world != null) {

            LivingEntity newMascots = (LivingEntity) world.spawnEntity(mascotsLoc,skin);
            setMascotsData(newMascots, name, maxHealth, baseHealth);
            PersistentDataContainer newData = newMascots.getPersistentDataContainer();

            if (mascotsCustomUUID != null) {
                newData.set(mascotsKey, PersistentDataType.STRING, mascotsCustomUUID);
                loadMascotsConfig();
                mascotsConfig.set("mascots." + mascotsCustomUUID + ".uuid", String.valueOf(newMascots.getUniqueId()));
                saveMascotsConfig();
            }
        }
    }

    private static void setMascotsData(LivingEntity mob, String customName, double maxHealth, double baseHealth) {
        mob.setAI(false);
        mob.setMaxHealth(maxHealth);
        mob.setHealth(baseHealth);
        mob.setPersistent(true);
        mob.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, Integer.MAX_VALUE, 0, true, true));
        mob.addPotionEffect(new PotionEffect(PotionEffectType.WATER_BREATHING, Integer.MAX_VALUE, 0, true, true));

        mob.setCustomName(Objects.requireNonNullElseGet(customName, () -> "§lMascotte §c" + mob.getHealth() + "/300❤"));

        mob.setCustomNameVisible(true);
    }

    public static void removeMascotsFromCity (String city_uuid) {
        loadMascotsConfig();
        UUID mascotUUID = getMascotsUUIDbyCityUUID(city_uuid);
        OMCPlugin.getInstance().getLogger().info("" + mascotUUID);
        if (mascotsConfig.contains("mascots." + city_uuid)){
            if (mascotUUID!=null){
                LivingEntity mascots = (LivingEntity) Bukkit.getEntity(mascotUUID);
                if (mascots!=null){
                    mascots.remove();
                }
            }
            OMCPlugin.getInstance().getLogger().info("mascots retirer");
            mascotsConfig.set("mascots." + city_uuid, null);
            saveMascotsConfig();
        }
    }

    private static UUID getMascotsUUIDbyCityUUID (String city_uuid){
        if (city_uuid==null){
            return null;
        }
        loadMascotsConfig();
        if (!mascotsConfig.contains("mascots." + city_uuid)){
            return null;
        }
        String uuid = mascotsConfig.getString("mascots." + city_uuid + ".uuid");
        if (uuid==null){
            return null;
        }
        return UUID.fromString(uuid);
    }

    public static void giveChest (Player player) {
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

    public static void addFreeClaim (int claim, Player player) {
        City city = CityManager.getPlayerCity(player.getUniqueId());
        if (city == null) {
            MessagesManager.sendMessage(player, MessagesManager.Message.PLAYERNOCITY.getMessage(), Prefix.CITY, MessageType.ERROR, false);
            return;
        }
        String city_uuid = city.getUUID();
        if (!freeClaim.containsKey(city_uuid)){
            freeClaim.put(city_uuid, claim);
            return;
        }
        freeClaim.replace(city_uuid, freeClaim.get(city_uuid)+claim);
        MessagesManager.sendMessage(player, Component.text(claim + " claims gratuits ajoutés"), Prefix.CITY, MessageType.SUCCESS, false);
    }

    public static void removeFreeClaim (int claim, Player player) {
        City city = CityManager.getPlayerCity(player.getUniqueId());
        if (city == null) {
            MessagesManager.sendMessage(player, MessagesManager.Message.PLAYERNOCITY.getMessage(), Prefix.CITY, MessageType.ERROR, false);
            return;
        }
        String city_uuid = city.getUUID();
        if (!freeClaim.containsKey(city_uuid)){
            MessagesManager.sendMessage(player, Component.text("§cCette ville n'a pas de claims gratuits"), Prefix.CITY, MessageType.ERROR, false);
            return;
        }
        if (freeClaim.get(city_uuid)-claim <= 0){
            freeClaim.remove(city_uuid);
            MessagesManager.sendMessage(player, Component.text("tous les claims gratuits ont été retirés"), Prefix.CITY, MessageType.SUCCESS, false);
            return;
        }
        freeClaim.replace(city_uuid, freeClaim.get(city_uuid)-claim);
        MessagesManager.sendMessage(player, Component.text(claim + " claims gratuits retirés"), Prefix.CITY, MessageType.SUCCESS, false);
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

    public static void saveFreeClaimMap() {
       for (String city_uuid : freeClaim.keySet()){
           City city = CityManager.getCity(city_uuid);
           if (city==null){
               continue;
           }
           loadMascotsConfig();
           mascotsConfig.set("data." + city_uuid, freeClaim.get(city_uuid));
           saveMascotsConfig();
       }
    }
}
