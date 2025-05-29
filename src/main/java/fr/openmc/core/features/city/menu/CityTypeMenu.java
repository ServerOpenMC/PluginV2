package fr.openmc.core.features.city.menu;

import fr.openmc.api.menulib.Menu;
import fr.openmc.api.menulib.utils.InventorySize;
import fr.openmc.api.menulib.utils.ItemBuilder;
import fr.openmc.core.features.city.CityType;
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

import static fr.openmc.core.features.city.commands.CityCommands.futurCreateCity;

public class CityTypeMenu extends Menu {
    String name;
    public CityTypeMenu(Player owner, String name) {
        super(owner);
        this.name = name;
    }

    @Override
    public @NotNull String getName() {
        return "Menu des villes - Type";
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
        Player player = getOwner();
        try {
            List<Component> peaceInfo = new ArrayList<>();
            peaceInfo.add(Component.text("§aLa sécurité est assurée"));
            peaceInfo.add(Component.text("§fObjectif : relaxez vous et construisez la"));
            peaceInfo.add(Component.text("§fville de vos rêves"));

            List<Component> warInfo = new ArrayList<>();
            warInfo.add(Component.text("§cLa guerre vous attend"));
            warInfo.add(Component.text("§fObjectif : devenir la ville la plus puissante"));
            warInfo.add(Component.text("§cATTENTION : les autres villes en situation de guerre"));
            warInfo.add(Component.text("§cpeuvent tuer votre mascotte et détruire les constructions"));

            map.put(11, new ItemBuilder(this, Material.POPPY, itemMeta -> {
                itemMeta.displayName(Component.text("§aVille en paix"));
                itemMeta.lore(peaceInfo);
            }).setOnClick(inventoryClickEvent -> {
                runChoiceType(player, CityType.PEACE);
            }));

            map.put(15, new ItemBuilder(this, Material.DIAMOND_SWORD, itemMeta -> {
                itemMeta.displayName(Component.text("§cVille en guerre"));
                itemMeta.lore(warInfo);
            }).setOnClick(inventoryClickEvent -> {
                runChoiceType(player, CityType.WAR);
            }));

            return map;
        } catch (Exception e) {
            MessagesManager.sendMessage(player, Component.text("§cUne Erreur est survenue, veuillez contacter le Staff"), Prefix.OPENMC, MessageType.ERROR, false);
            player.closeInventory();
            e.printStackTrace();
        }
        return map;
    }

    private void runChoiceType(Player player, CityType type) {
        futurCreateCity.computeIfAbsent(player.getUniqueId(), k -> new HashMap<>()).put(name, type);


        getOwner().closeInventory();
    }

    @Override
    public void onClose(InventoryCloseEvent event) {
        //empty
    }

    @Override
    public List<Integer> getTakableSlot() {
        return List.of();
    }
}
