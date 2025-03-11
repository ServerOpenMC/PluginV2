package fr.openmc.core.features.city.mascots;

import dev.lone.itemsadder.api.CustomStack;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.utils.ItemUtils;
import fr.openmc.core.utils.chronometer.Chronometer;
import fr.openmc.core.utils.chronometer.ChronometerType;
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
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class MascotsManager {

    public static NamespacedKey chestKey;
    public static NamespacedKey mascotsKey;
    public static File mascotsFile;
    public static YamlConfiguration mascotsConfig;
    public static Map<String, Integer> freeClaim = new HashMap<>();
    public static Map<UUID, Location> mascotSpawn = new HashMap<>();

    public MascotsManager (OMCPlugin plugin) {
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

        saveMascotsConfig();
        chestKey = new NamespacedKey(plugin, "mascots_chest");
        mascotsKey = new NamespacedKey(plugin, "mascotsKey");
    }

    public static void giveMascotsEffect (String city_uuid, UUID playerUUID) {
        if (Bukkit.getPlayer(playerUUID) instanceof Player player) {
            if (city_uuid!=null){
                loadMascotsConfig();
                if (mascotsConfig.contains("mascots." + city_uuid)){
                    String level = mascotsConfig.getString("mascots." + city_uuid + ".level");
                    if (mascotsConfig.getBoolean("mascots." + city_uuid + ".alive")){
                        for (PotionEffect potionEffect : MascotsLevels.valueOf(level).getBonus()){
                            player.addPotionEffect(potionEffect);
                        }
                    } else {
                        for (PotionEffect potionEffect : MascotsLevels.valueOf(level).getMalus()){
                            player.addPotionEffect(potionEffect);
                        }
                    }
                }
            }
        }
    }

    public static void reviveMascots (String city_uuid) {
        loadMascotsConfig();
        if (mascotsConfig.contains("mascots." + city_uuid)){
            mascotsConfig.set("mascots." + city_uuid + ".alive", true);
            mascotsConfig.set("mascots." + city_uuid + ".immunity.activate", false);
            String level = mascotsConfig.getString("mascots." + city_uuid+ ".level");
            saveMascotsConfig();
            LivingEntity entity = (LivingEntity) Bukkit.getEntity(getMascotsUUIDbyCityUUID(city_uuid));
            entity.setHealth(Math.floor(0.10 * entity.getMaxHealth()));
            entity.setCustomName("§lMascotte §c" + entity.getHealth() + "/" + entity.getMaxHealth() + "❤");
            MascotsListener.mascotsRegeneration(getMascotsUUIDbyCityUUID(city_uuid));
            City city = CityManager.getCity(city_uuid);
            if (city==null){return;}
            for (UUID townMember : city.getMembers()){
                if (Bukkit.getEntity(townMember) instanceof Player player){
                    for (PotionEffect potionEffect : MascotsLevels.valueOf(level).getMalus()){
                        player.removePotionEffect(potionEffect.getType());
                    }
                    giveMascotsEffect(city_uuid, townMember);
                }
            }
        }
    }

    public static void giveChest (Player player) {
        if (!ItemUtils.hasAvailableSlot(player)){

            MessagesManager.sendMessage(player, Component.text("Vous n'avez pas assez de place dans votre inventaire : mascotte invoquée à vos coordonées"), Prefix.CITY, MessageType.ERROR, false);
            City city = CityManager.getPlayerCity(player.getUniqueId());

            if (city == null) {
                MessagesManager.sendMessage(player, MessagesManager.Message.PLAYERNOCITY.getMessage(), Prefix.CITY, MessageType.ERROR, false);
                return;
            }

            String city_uuid = city.getUUID();
            spawnMascot(city_uuid, player.getWorld(), new Location(player.getWorld(), player.getLocation().getBlockX()+0.5, player.getLocation().getBlockY(), player.getLocation().getBlockZ()+0.5));
            return;
        }

        ItemStack specialChest = new ItemStack(Material.CHEST);
        ItemMeta meta = specialChest.getItemMeta();

        if (meta != null) {

            List<Component> info = new ArrayList<>();
            info.add(Component.text("§cVotre mascotte sera posé a l'emplacement du coffre"));
            info.add(Component.text("§cCe coffre n'est pas retirable"));
            info.add(Component.text("§clors de votre déconnection la mascotte sera placé"));

            meta.setDisplayName("§lMascotte");
            meta.lore(info);
            PersistentDataContainer data = meta.getPersistentDataContainer();
            data.set(chestKey,PersistentDataType.STRING, "id");
            specialChest.setItemMeta(meta);

        } else {

            City city = CityManager.getPlayerCity(player.getUniqueId());
            if (city == null) {
                MessagesManager.sendMessage(player, MessagesManager.Message.PLAYERNOCITY.getMessage(), Prefix.CITY, MessageType.ERROR, false);
                return;
            }
            String city_uuid = city.getUUID();
            spawnMascot(city_uuid, player.getWorld(), new Location(player.getWorld(), player.getLocation().getBlockX()+0.5, player.getLocation().getBlockY(), player.getLocation().getBlockZ()+0.5));
            OMCPlugin.getInstance().getLogger().severe("Erreur lors de l'initialisation de l'ItemMeta du coffre des mascottes");
            return;
        }

        player.getInventory().addItem(specialChest);
        mascotSpawn.put(player.getUniqueId(), new Location(player.getWorld(), player.getLocation().getBlockX()+0.5, player.getLocation().getBlockY(), player.getLocation().getBlockZ()+0.5));
    }

    public static void removeChest (Player player){
        ItemStack specialChest = new ItemStack(Material.CHEST);
        ItemMeta meta = specialChest.getItemMeta();
        if (meta != null){
            List<Component> info = new ArrayList<>();
            info.add(Component.text("§cVotre mascotte sera posé a l'emplacement du coffre"));
            info.add(Component.text("§cCe coffre n'est pas retirable"));
            info.add(Component.text("§clors de votre déconnection la mascotte sera placé"));

            meta.setDisplayName("§lMascotte");
            meta.lore(info);
            PersistentDataContainer data = meta.getPersistentDataContainer();
            data.set(chestKey,PersistentDataType.STRING, "id");
            specialChest.setItemMeta(meta);

            if (player.getInventory().contains(specialChest)){
                player.getInventory().remove(specialChest);
            }
        }
    }

    public static void upgradeMascots (String city_uuid, UUID entityUUID) {
        LivingEntity mob = (LivingEntity) Bukkit.getEntity(entityUUID);
        if (mob==null){
            return;
        }
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

    public static void changeMascotsSkin(Entity mascots, EntityType skin) {
        World world = Bukkit.getWorld("world");
        Location mascotsLoc = mascots.getLocation();
        LivingEntity mob = (LivingEntity) mascots;

        if (mascotsLoc.clone().add(0, 1, 0).getBlock().getType().isSolid() && mob.getHeight() <= 1.0) {
            return;
        }

        double baseHealth = mob.getHealth();
        double maxHealth = mob.getMaxHealth();
        String name = mob.getCustomName();
        String mascotsCustomUUID = mob.getPersistentDataContainer().get(mascotsKey, PersistentDataType.STRING);

        mob.remove();

        if (world != null) {
            LivingEntity newMascots = (LivingEntity) world.spawnEntity(mascotsLoc, skin);
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


    public static void spawnMascot(String city_uuid, World player_world, Location mascot_spawn) {

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
        mascotsConfig.set("mascots." + city_uuid + ".alive", true);
        saveMascotsConfig();
        // immunité persistente de 7 jours pour la mascotte
        MascotsListener.startImmunityTimer(city_uuid, 10080);
    }

    private static void setMascotsData(LivingEntity mob, String customName, double maxHealth, double baseHealth) {
        mob.setAI(false);
        mob.setMaxHealth(maxHealth);
        mob.setHealth(baseHealth);
        mob.setPersistent(true);
        mob.setRemoveWhenFarAway(false);
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

    public static UUID getMascotsUUIDbyCityUUID(String city_uuid){
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
            MessagesManager.sendMessage(player, MessagesManager.Message.CITYNOFREECLAIM.getMessage(), Prefix.CITY, MessageType.ERROR, false);
            return;
        }
        if (freeClaim.get(city_uuid)-claim <= 0){
            freeClaim.remove(city_uuid);
            MessagesManager.sendMessage(player, Component.text("Tous les claims gratuits ont été retirés"), Prefix.CITY, MessageType.SUCCESS, false);
            return;
        }
        freeClaim.replace(city_uuid, freeClaim.get(city_uuid)-claim);
        MessagesManager.sendMessage(player, Component.text(claim + " claims gratuits retirés"), Prefix.CITY, MessageType.SUCCESS, false);
    }


    public static boolean hasEnoughCroqStar (Player player, MascotsLevels mascotsLevels) {
        String itemNamespace = "city:croqstar";
        int requiredAmount = mascotsLevels.getUpgradeCost();
        int count = 0;

        for (ItemStack item : player.getInventory().getContents()) {
            if (item != null) {
                CustomStack customStack = CustomStack.byItemStack(item);
                if (customStack != null && customStack.getNamespacedID().equals(itemNamespace)) {
                    count += item.getAmount();
                    if (count >= requiredAmount) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static void removeCrocStar(Player player, MascotsLevels mascotsLevels) {
        String itemNamespace = "city:croqstar";
        PlayerInventory inventory = player.getInventory();
        int amountToRemove = mascotsLevels.getUpgradeCost();

        for (ItemStack item : inventory.getContents()) {
            if (item != null) {
                CustomStack customStack = CustomStack.byItemStack(item);
                if (customStack != null && customStack.getNamespacedID().equals(itemNamespace)) {
                    int stackAmount = item.getAmount();

                    if (stackAmount > amountToRemove) {
                        item.setAmount(stackAmount - amountToRemove);
                        return;
                    } else {
                        amountToRemove -= stackAmount;
                        item.setAmount(0);
                    }
                    if (amountToRemove <= 0) {
                        return;
                    }
                }
            }
        }
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
        try {
            loadMascotsConfig();
            if (mascotsConfig.contains("data")){
                mascotsConfig.set("data", null);
                for (String city_uuid : freeClaim.keySet()){
                    City city = CityManager.getCity(city_uuid);
                    if (city==null){
                        continue;
                    }
                    if (freeClaim.get(city_uuid)!=null){
                        mascotsConfig.set("data." + city_uuid, freeClaim.get(city_uuid));
                        saveMascotsConfig();
                    }
                }
            }

        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
