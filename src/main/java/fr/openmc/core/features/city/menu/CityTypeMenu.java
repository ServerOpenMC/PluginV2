package fr.openmc.core.features.city.menu;

import fr.openmc.api.menulib.Menu;
import fr.openmc.api.menulib.utils.InventorySize;
import fr.openmc.api.menulib.utils.ItemBuilder;
import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.CityType;
import fr.openmc.core.features.city.actions.CityChangeAction;
import fr.openmc.core.features.city.conditions.CityTypeConditions;
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


public class CityTypeMenu extends Menu {
    public CityTypeMenu(Player owner) {
        super(owner);
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

        City city = CityManager.getPlayerCity(player.getUniqueId());
        List<Component> peaceInfo = new ArrayList<>();

        boolean enchantPeace = city.getType() == CityType.PEACE;
        peaceInfo.add(Component.text("§7Votre sécurité est §aassurée§7!"));
        peaceInfo.add(Component.empty());
        peaceInfo.add(Component.text("§6§lTIPS: Parfait pour build, et échanger en toute tranquilité!"));

        map.put(11, new ItemBuilder(this, Material.POPPY, itemMeta -> {
            itemMeta.displayName(Component.text("§aVille en paix"));
            itemMeta.lore(peaceInfo);
            itemMeta.setEnchantmentGlintOverride(enchantPeace);
        }).setOnClick(inventoryClickEvent -> {
            if (!CityTypeConditions.canCityChangeType(city, player)) return;

            CityChangeAction.beginChangeCity(player, CityType.PEACE);
        }));

        List<Component> warInfo = new ArrayList<>();
        warInfo.add(Component.text("§7Un monde de §cguerre §7et de §cconcurrence."));
        warInfo.add(Component.empty());
        warInfo.add(Component.text("§c§l ⚠ ATTENTION"));
        warInfo.add(Component.text("§8- §cLes villes étant dans le même status que vous, pourront vous §cdéclarer la guerre!"));
        warInfo.add(Component.text("§6§lTIPS: Idéal pour les tryhardeurs et les compétitifs"));

        boolean enchantWar = city.getType() == CityType.WAR;
        map.put(15, new ItemBuilder(this, Material.TNT, itemMeta -> {
            itemMeta.displayName(Component.text("§cVille en guerre"));
            itemMeta.lore(warInfo);
            itemMeta.setEnchantmentGlintOverride(enchantWar);
        }).setOnClick(inventoryClickEvent -> {
            if (!CityTypeConditions.canCityChangeType(city, player)) return;

            CityChangeAction.beginChangeCity(player, CityType.WAR);
        }));

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
}
