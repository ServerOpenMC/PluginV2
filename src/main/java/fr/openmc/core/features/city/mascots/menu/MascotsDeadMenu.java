package fr.openmc.core.features.city.mascots.menu;

import fr.openmc.api.menulib.Menu;
import fr.openmc.api.menulib.utils.InventorySize;
import fr.openmc.api.menulib.utils.ItemBuilder;
import fr.openmc.core.features.city.CPermission;
import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.mascots.Mascot;
import fr.openmc.core.features.city.mascots.MascotsLevels;
import fr.openmc.core.features.city.mascots.MascotsManager;
import fr.openmc.core.features.city.menu.CityMenu;
import fr.openmc.core.utils.ItemUtils;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MascotsDeadMenu extends Menu {

    private final String city_uuid;
    List<Component> requiredItemsLore = new ArrayList<>();
    Map<Material, Integer> requiredItems;

    public MascotsDeadMenu(Player owner, String city_uuid) {
        super(owner);
        this.city_uuid = city_uuid;


        City city = CityManager.getCity(city_uuid);

        Mascot mascot = city.getMascot();

        Map<Material, Integer> itemCount = new HashMap<>();
        requiredItemsLore.add(Component.text("§bRequière :"));

        int level = mascot.getLevel();
        requiredItems = MascotsLevels.valueOf("level"+level).getRequiredItems();

        for (ItemStack item : getOwner().getInventory().getContents()) {
            if (item == null) continue;
            if (requiredItems.containsKey(item.getType())) {
                itemCount.put(item.getType(), itemCount.getOrDefault(item.getType(), 0) + item.getAmount());
            }
        }

        for (Map.Entry<Material, Integer> entry : requiredItems.entrySet()) {
            Material material = entry.getKey();
            int requiredAmount = entry.getValue();
            int playerAmount = itemCount.getOrDefault(material, 0);

            String color = (playerAmount >= requiredAmount) ? "§a" : "§c";
            requiredItemsLore.add(Component.text(color + material.name() + " (" + playerAmount + "/" + requiredAmount + ")"));
        }
    }

    @Override
    public @NotNull String getName() {
        return "Menu des Mascottes [DEAD]";
    }

    @Override
    public @NotNull InventorySize getInventorySize() {
        return InventorySize.SMALLEST;
    }

    @Override
    public void onInventoryClick(InventoryClickEvent inventoryClickEvent) {

    }

    @Override
    public @NotNull Map<Integer, ItemStack> getContent() {
        Map<Integer, ItemStack> map = new HashMap<>();
        Player player = getOwner();

        try {
            map.put(4, new ItemBuilder(this, Material.APPLE, itemMeta -> {
                itemMeta.displayName(Component.text("§7Soigner votre §cMascotte"));
                itemMeta.lore(requiredItemsLore);
            }).setOnClick(inventoryClickEvent -> {
                City city = CityManager.getCity(city_uuid);
                if (city == null) {
                    MessagesManager.sendMessage(player, MessagesManager.Message.PLAYERNOCITY.getMessage(), Prefix.CITY, MessageType.ERROR, false);
                    player.closeInventory();
                    return;
                }
                if (city.hasPermission(player.getUniqueId(), CPermission.MASCOT_HEAL)) {
                    if (hasRequiredItems(player, requiredItems)) {
                        removeRequiredItems(player, requiredItems);
                        MascotsManager.reviveMascots(city_uuid);
                    }
                } else {
                    MessagesManager.sendMessage(player, MessagesManager.Message.NOPERMISSION.getMessage(), Prefix.CITY, MessageType.ERROR, false);
                }
                player.closeInventory();
            }));

            map.put(0, new ItemBuilder(this, Material.ARROW, itemMeta -> {
                itemMeta.displayName(Component.text("§aRetour"));
                itemMeta.lore(List.of(Component.text("§7Retourner au menu des villes")));
            }).setOnClick(event -> {
                CityMenu menu = new CityMenu(player);
                menu.open();
            }));

            return map;
        } catch (Exception e) {
            MessagesManager.sendMessage(player, Component.text("§cUne Erreur est survenue, veuillez contacter le Staff"), Prefix.OPENMC, MessageType.ERROR, false);
            player.closeInventory();
            e.printStackTrace();
        }
        return map;
    }

    @Override
    public void onClose(InventoryCloseEvent event) {
        //empty
    }

    @Override
    public List<Integer> getTakableSlot() {
        return List.of();
    }

    private boolean hasRequiredItems(Player player, Map<Material, Integer> requiredItems) {
        for (Map.Entry<Material, Integer> entry : requiredItems.entrySet()) {
            Material material = entry.getKey();
            int amount = entry.getValue();
            if (!ItemUtils.hasEnoughItems(player, material, amount)){
                return false;
            }
        }
        return true;
    }

    private void removeRequiredItems(Player player, Map<Material, Integer> requiredItems) {

        for (Map.Entry<Material, Integer> entry : requiredItems.entrySet()) {
            Material material = entry.getKey();
            int amountToRemove = entry.getValue();
            ItemUtils.removeItemsFromInventory(player, material, amountToRemove);

        }
    }
}