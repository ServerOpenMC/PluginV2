package fr.openmc.core.features.city.menu;

import dev.xernas.menulib.Menu;
import dev.xernas.menulib.utils.InventorySize;
import dev.xernas.menulib.utils.ItemBuilder;
import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.utils.menu.ConfirmMenu;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CityMenu extends Menu {

    public CityMenu(Player owner) {
        super(owner);
    }

    @Override
    public @NotNull String getName() {
        return "Menu des villes";
    }

    @Override
    public @NotNull InventorySize getInventorySize() {
        return InventorySize.LARGER;
    }

    @Override
    public void onInventoryClick(InventoryClickEvent click) {
    }

    @Override
    public @NotNull Map<Integer, ItemStack> getContent() {
        Map<Integer, ItemStack> inventory = new HashMap<>();
        Player player = getOwner();

        City city = CityManager.getPlayerCity(player.getUniqueId());
        assert city != null;

        inventory.put(4, new ItemBuilder(this, Material.BOOKSHELF, itemMeta -> {
            itemMeta.itemName(Component.text("§d" + city.getCityName()));
            itemMeta.lore(List.of(Component.text("§7Membre(s) : " + city.getMembers().size())));
        }));

        inventory.put(8, new ItemBuilder(this, Material.DRAGON_EGG, itemMeta -> {
            itemMeta.itemName(Component.text("§cVotre Mascotte"));
            itemMeta.lore(List.of(Component.text("§cVie : §7null/null"))); //TODO: Mascottes
        }));

        inventory.put(19, new ItemBuilder(this, Material.OAK_FENCE, itemMeta -> {
            itemMeta.itemName(Component.text("§6Taille de votre Ville"));
            itemMeta.lore(List.of(Component.text("§7Superficie" + city.getChunks().size())));
        }));


        inventory.put(44, new ItemBuilder(this, Material.OAK_DOOR, itemMeta -> {
            itemMeta.itemName(Component.text("§cPartir de la Ville"));
            itemMeta.lore(List.of(Component.text("§e§lCLIQUEZ ICI POUR PARTIR")));
        }).setOnClick(inventoryClickEvent -> {
            ConfirmMenu menu = new ConfirmMenu(player, null, this::accept, this::refuse, "§7Voulez vous vraiment partir de " + city.getCityName() + " ?", "§7Rester dans la ville "  + city.getCityName());
            menu.open();
        }));

        return inventory;
    }

    private void accept() {
        Bukkit.dispatchCommand(getOwner(), "city leave");
    }

    private void refuse() {
    }
}