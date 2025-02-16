package fr.openmc.core.features.city.menu;

import dev.xernas.menulib.Menu;
import dev.xernas.menulib.utils.InventorySize;
import dev.xernas.menulib.utils.ItemBuilder;
import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.utils.chronometer.Chronometer;
import fr.openmc.core.utils.chronometer.ChronometerType;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static fr.openmc.core.features.city.mascots.MascotsListener.*;
import static fr.openmc.core.utils.chronometer.Chronometer.startChronometer;

public class MascotMenu extends Menu {

    private final Entity mascots;

    public MascotMenu(Player owner, Entity mascots) {
        super(owner);
        this.mascots = mascots;
    }

    @Override
    public @NotNull String getName() {
        return "";
    }

    @Override
    public @NotNull InventorySize getInventorySize() {
        return InventorySize.NORMAL;
    }

    @Override
    public void onInventoryClick(InventoryClickEvent inventoryClickEvent) {

    }

    @Override
    public @NotNull Map<Integer, ItemStack> getContent() {
        Map<Integer, ItemStack> map = new HashMap<>();

        map.put(11, new ItemBuilder(this, getSpawnEgg(mascots), itemMeta -> {
            itemMeta.setDisplayName("Mascottes");
            itemMeta.addEnchant(Enchantment.EFFICIENCY, 1, true);
            itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }).setNextMenu(new MascotsSkinMenu(getOwner(), getSpawnEgg(mascots), mascots)));

        map.put(13, new ItemBuilder(this, Material.CHEST, itemMeta -> {
            itemMeta.setDisplayName("Déplacer");
            if (Chronometer.containsChronometer(mascots.getUniqueId(), "mascotsCooldown")){
                List<String> lore = new ArrayList<>();
                lore.add("§c en cooldown : " + Chronometer.getRemainingTime(mascots.getUniqueId(), "mascotsCooldown"));
                itemMeta.setLore(lore);
            }
            itemMeta.addEnchant(Enchantment.EFFICIENCY, 1, true);
            itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }).setOnClick(inventoryClickEvent -> {
            if (!Chronometer.containsChronometer(mascots.getUniqueId(), "mascotsCooldown")){
                if (hasAvailableSlot(getOwner())){
                    City city = CityManager.getPlayerCity(getOwner().getUniqueId());
                    if (city == null) {
                        return;
                    }
                    String city_uuid = city.getUUID();
                    if (!movingMascots.contains(city_uuid)) {
                        startChronometer(getOwner(), "mascotsMove", 120, ChronometerType.ACTION_BAR, "remaining : %sec%", ChronometerType.ACTION_BAR, "§cdéplacement de la masctte annulé");
                        movingMascots.add(city_uuid);
                        giveChest(getOwner());
                    }
                }
            }
            getOwner().closeInventory();
        }));

        map.put(15, new ItemBuilder(this,Material.NETHERITE_UPGRADE_SMITHING_TEMPLATE, itemMeta -> {
            itemMeta.setDisplayName("Upgrades");
            itemMeta.addEnchant(Enchantment.EFFICIENCY, 1, true);
            itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }).setOnClick(inventoryClickEvent -> {
            PersistentDataContainer data = mascots.getPersistentDataContainer();
            String mascotsUUID = data.get(mascotsKey, PersistentDataType.STRING);
            upgradeMascots(mascotsUUID, mascots.getUniqueId());
            getOwner().closeInventory();
        }));

        return map;
    }

    private Material getSpawnEgg(Entity entity) {
        String eggName = entity.getType().name() + "_SPAWN_EGG";
        if (Material.matchMaterial(eggName) == null){
            return Material.ZOMBIE_SPAWN_EGG;
        }
        return Material.matchMaterial(eggName);
    }
}
