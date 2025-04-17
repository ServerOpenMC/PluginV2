package fr.openmc.core.features.city.menu.mascots;

import dev.xernas.menulib.Menu;
import dev.xernas.menulib.utils.InventorySize;
import dev.xernas.menulib.utils.ItemBuilder;
import fr.openmc.core.features.city.CPermission;
import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.mascots.MascotUtils;
import fr.openmc.core.features.city.mascots.MascotsLevels;
import fr.openmc.core.features.city.menu.CityMenu;
import fr.openmc.core.utils.DateUtils;
import fr.openmc.core.utils.ItemUtils;
import fr.openmc.core.utils.chronometer.Chronometer;
import fr.openmc.core.utils.chronometer.ChronometerType;
import fr.openmc.core.utils.customitems.CustomItemRegistry;
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

import java.util.*;

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

        List<Component> loreSkinMascot = List.of(
                Component.text("§7Vous pouvez changer l'apparence de votre §cMascotte"),
                Component.text(""),
                Component.text("§e§lCLIQUEZ ICI POUR CHANGER DE SKIN")
        );

        map.put(11, new ItemBuilder(this, getSpawnEgg(mascots), itemMeta -> {
            itemMeta.displayName(Component.text("§7Le Skin de la §cMascotte"));
            itemMeta.lore(loreSkinMascot);
            itemMeta.addEnchant(Enchantment.EFFICIENCY, 1, true);
            itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        }).setOnClick(inventoryClickEvent -> {
            if (!city.hasPermission(getOwner().getUniqueId(), CPermission.MASCOT_SKIN)){
                MessagesManager.sendMessage(getOwner(), MessagesManager.Message.NOPERMISSION.getMessage(), Prefix.CITY, MessageType.ERROR, false);
                getOwner().closeInventory();
                return;
            }
            new MascotsSkinMenu(getOwner(), getSpawnEgg(mascots), mascots).open();
        }));

        List<Component> lorePosMascot;

        if (Chronometer.containsChronometer(mascots.getUniqueId(), "mascotsCooldown")){
            lorePosMascot = List.of(
                    Component.text("§7Vous pouvez changer la position de votre §cMascotte"),
                    Component.text(""),
                    Component.text("§cCooldown §7: " + DateUtils.convertSecondToTime(Chronometer.getRemainingTime(mascots.getUniqueId(), "mascotsCooldown")))
            );
        } else {
            lorePosMascot = List.of(
                    Component.text("§7Vous pouvez changer la position de votre §cMascotte"),
                    Component.text(""),
                    Component.text("§e§lCLIQUEZ ICI POUR LA CHANGER DE POSITION")
            );
        }

        map.put(13, new ItemBuilder(this, Material.CHEST, itemMeta -> {
            itemMeta.displayName(Component.text("§7Déplacer votre §cMascotte"));
            itemMeta.lore(lorePosMascot);
            itemMeta.addEnchant(Enchantment.EFFICIENCY, 1, true);
            itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
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
                            startChronometer(getOwner(), "mascotsMove", 120, ChronometerType.ACTION_BAR, "Temps Restant : %sec%s", ChronometerType.ACTION_BAR, "§cDéplacement de la Mascotte annulé");
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

        List<Component> requiredAmount = new ArrayList<>();
        requiredAmount.add(Component.text("§7Nécessite §d" + MascotsLevels.valueOf("level" + MascotUtils.getMascotLevel(city.getUUID())).getUpgradeCost() + " d'Aywenite"));

        map.put(15, new ItemBuilder(this,Material.NETHERITE_UPGRADE_SMITHING_TEMPLATE, itemMeta -> {
            itemMeta.displayName(Component.text("§7Améloiorer votre §cMascotte"));
            itemMeta.lore(requiredAmount);
            itemMeta.addEnchant(Enchantment.EFFICIENCY, 1, true);
            itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            itemMeta.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
            itemMeta.addItemFlags(ItemFlag.HIDE_ARMOR_TRIM);
        }).setOnClick(inventoryClickEvent -> {

            if (city == null) {
                MessagesManager.sendMessage(getOwner(), MessagesManager.Message.PLAYERNOCITY.getMessage(), Prefix.CITY, MessageType.ERROR, false);
                getOwner().closeInventory();
                return;
            }
            if (city.hasPermission(getOwner().getUniqueId(), CPermission.MASCOT_UPGRADE)){
                String city_uuid = city.getUUID();
                int aywenite = MascotsLevels.valueOf("level" + MascotUtils.getMascotLevel(city_uuid)).getUpgradeCost();
                Material matAywenite = Objects.requireNonNull(CustomItemRegistry.getByName("omc_items:aywenite")).getBest().getType();
                if (ItemUtils.hasEnoughItems(getOwner(), matAywenite, aywenite)){
                    ItemUtils.removeItemsFromInventory(getOwner(), matAywenite, aywenite);
                    upgradeMascots(city_uuid, mascots.getUniqueId());
                    MessagesManager.sendMessage(getOwner(), Component.text("Vous avez amélioré votre mascotte au §cNiveau " + MascotUtils.getMascotLevel(city_uuid)), Prefix.CITY, MessageType.ERROR, false);
                    getOwner().closeInventory();
                    return;
                }
                MessagesManager.sendMessage(getOwner(), Component.text("Vous n'avez pas assez d'§dAywenite"), Prefix.CITY, MessageType.ERROR, false);

            } else {
                MessagesManager.sendMessage(getOwner(), MessagesManager.Message.NOPERMISSION.getMessage(), Prefix.CITY, MessageType.ERROR, false);
            }
            getOwner().closeInventory();
        }));

        map.put(18, new ItemBuilder(this, Material.ARROW, itemMeta -> {
            itemMeta.displayName(Component.text("§aRetour"));
            itemMeta.lore(List.of(Component.text("§7Retourner au menu des villes")));
        }).setOnClick(event -> {
            CityMenu menu = new CityMenu(getOwner());
            menu.open();
        }));

        if (MascotUtils.getMascotImmunity(city.getUUID())) {
            List<Component> lore = List.of(
                    Component.text("§7Vous avez une §bimmunité §7sur votre §cMascotte"),
                    Component.text("§cTemps restant §7: " + DateUtils.convertSecondToTime(MascotUtils.getMascotImmunityTime(city.getUUID())))
            );

            map.put(26, new ItemBuilder(this, Material.DIAMOND, itemMeta -> {
                itemMeta.displayName(Component.text("§7Votre §cMascotte §7est §bimmunisée§7!"));
                itemMeta.lore(lore);
            }));
        }

        return map;
    }

    public static Material getSpawnEgg(Entity entity) {
        String eggName = entity.getType().name() + "_SPAWN_EGG";
        if (Material.matchMaterial(eggName) == null){
            return Material.ZOMBIE_SPAWN_EGG;
        }
        return Material.matchMaterial(eggName);
    }
}
