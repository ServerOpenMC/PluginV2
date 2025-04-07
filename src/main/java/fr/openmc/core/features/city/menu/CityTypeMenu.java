package fr.openmc.core.features.city.menu;

import dev.xernas.menulib.Menu;
import dev.xernas.menulib.utils.InventorySize;
import dev.xernas.menulib.utils.ItemBuilder;
import fr.openmc.core.features.city.mascots.MascotsListener;
import fr.openmc.core.features.city.mascots.MascotsManager;
import fr.openmc.core.utils.chronometer.Chronometer;
import fr.openmc.core.utils.chronometer.ChronometerType;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class CityTypeMenu extends Menu {
    Player player;
    String name;
    public CityTypeMenu(Player owner, String name) {
        super(owner);
        this.player = owner;
        this.name = name;
    }

    @Override
    public @NotNull String getName() {
        return "Menu des villes";
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

        double x = player.getX();
        double y = player.getY();
        double z = player.getZ();

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
            MascotsListener.futurCreateCity.computeIfAbsent(player.getUniqueId(), k -> new HashMap<>()).put(name, "peace");

            MessagesManager.sendMessage(player, Component.text("Vous avez reçu un coffre pour poser votre mascotte"), Prefix.CITY, MessageType.SUCCESS, true);
            Chronometer.startChronometer(player, "Mascot:chest", 300, ChronometerType.ACTION_BAR, null, ChronometerType.ACTION_BAR, "Mascotte posé en " + x +" " + y + " " + z);
            MascotsManager.giveChest(player);
            getOwner().closeInventory();
        }));

        map.put(15, new ItemBuilder(this, Material.DIAMOND_SWORD, itemMeta -> {
            itemMeta.displayName(Component.text("§cVille en guerre"));
            itemMeta.lore(warInfo);
        }).setOnClick(inventoryClickEvent -> {
            MascotsListener.futurCreateCity.computeIfAbsent(player.getUniqueId(), k -> new HashMap<>()).put(name, "war");

            MessagesManager.sendMessage(player, Component.text("Vous avez reçu un coffre pour poser votre mascotte"), Prefix.CITY, MessageType.SUCCESS, true);
            Chronometer.startChronometer(player, "Mascot:chest", 300, ChronometerType.ACTION_BAR, null, ChronometerType.ACTION_BAR, "Mascote posé en " + x +" " + y + " " + z);
            MascotsManager.giveChest(player);
            getOwner().closeInventory();
        }));

        return map;
    }
}
