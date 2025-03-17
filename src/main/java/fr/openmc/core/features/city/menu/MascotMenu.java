package fr.openmc.core.features.city.menu;

import dev.xernas.menulib.Menu;
import dev.xernas.menulib.utils.InventorySize;
import dev.xernas.menulib.utils.ItemBuilder;
import fr.openmc.core.features.city.CPermission;
import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.mascots.MascotUtils;
import fr.openmc.core.features.city.mascots.MascotsLevels;
import fr.openmc.core.utils.ItemUtils;
import fr.openmc.core.utils.chronometer.Chronometer;
import fr.openmc.core.utils.chronometer.ChronometerType;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static fr.openmc.core.features.city.mascots.MascotsListener.*;
import static fr.openmc.core.features.city.mascots.MascotsManager.*;
import static fr.openmc.core.utils.chronometer.Chronometer.startChronometer;

public class MascotMenu extends Menu {

    private final Entity mascots;
    private City city;

    public MascotMenu(Player owner, Entity mascots) {
        super(owner);
        this.mascots = mascots;
        this.city = CityManager.getPlayerCity(getOwner().getUniqueId());
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

        List<Component> requiredAmount = new ArrayList<>();
        requiredAmount.add(Component.text("Nécessite " + MascotsLevels.valueOf("level" + MascotUtils.getMascotLevel(city.getUUID())).getUpgradeCost() + " Croq'Stars"));

        map.put(11, new ItemBuilder(this, getSpawnEgg(mascots), itemMeta -> {
            itemMeta.setDisplayName("Mascottes");
            itemMeta.addEnchant(Enchantment.EFFICIENCY, 1, true);
            itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }).setOnClick(inventoryClickEvent -> {
            if (city.hasPermission(getOwner().getUniqueId(), CPermission.MASCOT_SKIN)){
                new MascotsSkinMenu(getOwner(), getSpawnEgg(mascots), mascots).open();
            } else {
                MessagesManager.sendMessage(getOwner(), MessagesManager.Message.NOPERMISSION.getMessage(), Prefix.CITY, MessageType.ERROR, false);
                getOwner().closeInventory();
            }
        }));

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
                if (city.hasPermission(getOwner().getUniqueId(), CPermission.MASCOT_MOVE)){
                    if (ItemUtils.hasAvailableSlot(getOwner())){
                        city = CityManager.getPlayerCity(getOwner().getUniqueId());
                        if (city == null) {
                            MessagesManager.sendMessage(getOwner(), MessagesManager.Message.PLAYERNOCITY.getMessage(), Prefix.CITY, MessageType.ERROR, false);
                            getOwner().closeInventory();
                            return;
                        }
                        String city_uuid = city.getUUID();
                        if (!movingMascots.contains(city_uuid)) {
                            startChronometer(getOwner(), "mascotsMove", 120, ChronometerType.ACTION_BAR, "remaining : %sec%", ChronometerType.ACTION_BAR, "§cdéplacement de la masctte annulé");
                            movingMascots.add(city_uuid);
                            giveChest(getOwner());
                        }
                    }
                } else {
                    MessagesManager.sendMessage(getOwner(), MessagesManager.Message.NOPERMISSION.getMessage(), Prefix.CITY, MessageType.ERROR, false);
                }
            }
            getOwner().closeInventory();
        }));

        map.put(15, new ItemBuilder(this,Material.NETHERITE_UPGRADE_SMITHING_TEMPLATE, itemMeta -> {
            itemMeta.setDisplayName("Upgrades");
            itemMeta.lore(requiredAmount);
            itemMeta.addEnchant(Enchantment.EFFICIENCY, 1, true);
            itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }).setOnClick(inventoryClickEvent -> {

            if (city == null) {
                MessagesManager.sendMessage(getOwner(), MessagesManager.Message.PLAYERNOCITY.getMessage(), Prefix.CITY, MessageType.ERROR, false);
                getOwner().closeInventory();
                return;
            }
            if (city.hasPermission(getOwner().getUniqueId(), CPermission.MASCOT_UPGRADE)){
                String city_uuid = city.getUUID();
                if (hasEnoughCroqStar(getOwner(), MascotsLevels.valueOf("level" + MascotUtils.getMascotLevel(city_uuid)))){
                    removeCrocStar(getOwner(), MascotsLevels.valueOf("level" + MascotUtils.getMascotLevel(city_uuid)));
                    upgradeMascots(city_uuid, mascots.getUniqueId());
                    getOwner().closeInventory();
                    return;
                }
                MessagesManager.sendMessage(getOwner(), Component.text("Vous n'avez pas assez de Croq'Star"), Prefix.CITY, MessageType.ERROR, false);

            } else {
                MessagesManager.sendMessage(getOwner(), MessagesManager.Message.NOPERMISSION.getMessage(), Prefix.CITY, MessageType.ERROR, false);
            }
            getOwner().closeInventory();
        }));

        map.put(26, new ItemBuilder(this,Material.APPLE, itemMeta -> {
            if (MascotUtils.getMascotImmunity(city.getUUID())){
                List<Component> lore = new ArrayList<>();
                lore.add(Component.text("temps restant : §a§l" + MascotUtils.getMascotImmunityTime(city.getUUID())));
                itemMeta.setDisplayName("Immunité : §aactivé");
                itemMeta.lore(lore);
            } else {
                itemMeta.setDisplayName("Immunité : §adésactivé");
            }
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
