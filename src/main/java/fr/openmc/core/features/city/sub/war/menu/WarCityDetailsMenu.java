package fr.openmc.core.features.city.sub.war.menu;

import fr.openmc.api.menulib.Menu;
import fr.openmc.api.menulib.utils.InventorySize;
import fr.openmc.api.menulib.utils.ItemBuilder;
import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityType;
import fr.openmc.core.features.city.sub.mascots.Mascot;
import fr.openmc.core.features.city.sub.mascots.MascotUtils;
import fr.openmc.core.features.city.sub.mayor.Mayor;
import fr.openmc.core.features.city.sub.mayor.managers.MayorManager;
import fr.openmc.core.features.city.sub.mayor.managers.PerkManager;
import fr.openmc.core.features.city.sub.mayor.perks.Perks;
import fr.openmc.core.features.economy.EconomyManager;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static fr.openmc.core.features.city.sub.mascots.MascotUtils.getEntityByMascotUUID;

public class WarCityDetailsMenu extends Menu {

    private final City city;

    public WarCityDetailsMenu(Player owner, City city) {
        super(owner);
        this.city = city;
    }

    @Override
    public @NotNull String getName() {
        return "Menu de guerre - " + city.getName();
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

        Mayor mayor = city.getMayor();
        if (MayorManager.getInstance().phaseMayor == 2 && mayor != null) {
            Perks perk1 = PerkManager.getPerkById(mayor.getIdPerk1());
            Perks perk2 = PerkManager.getPerkById(mayor.getIdPerk2());
            Perks perk3 = PerkManager.getPerkById(mayor.getIdPerk3());

            ItemStack iaPerk1 = (perk1 != null) ? perk1.getItemStack() : ItemStack.of(Material.DEAD_BRAIN_CORAL_BLOCK);
            String namePerk1 = (perk1 != null) ? perk1.getName() : "§8Réforme Vide";
            List<Component> lorePerk1 = (perk1 != null) ? new ArrayList<>(perk1.getLore()) : null;
            map.put(11, new ItemBuilder(this, iaPerk1, itemMeta -> {
                itemMeta.customName(Component.text(namePerk1));
                itemMeta.lore(lorePerk1);
                itemMeta.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
                itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            }));

            ItemStack iaPerk2 = (perk2 != null) ? perk2.getItemStack() : ItemStack.of(Material.DEAD_BRAIN_CORAL_BLOCK);
            String namePerk2 = (perk2 != null) ? perk2.getName() : "§8Réforme Vide";
            List<Component> lorePerk2 = (perk2 != null) ? new ArrayList<>(perk2.getLore()) : null;
            map.put(13, new ItemBuilder(this, iaPerk2, itemMeta -> {
                itemMeta.customName(Component.text(namePerk2));
                itemMeta.lore(lorePerk2);
                itemMeta.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
                itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            }));

            ItemStack iaPerk3 = (perk3 != null) ? perk3.getItemStack() : ItemStack.of(Material.DEAD_BRAIN_CORAL_BLOCK);
            String namePerk3 = (perk3 != null) ? perk3.getName() : "§8Réforme Vide";
            List<Component> lorePerk3 = (perk3 != null) ? new ArrayList<>(perk3.getLore()) : null;
            map.put(15, new ItemBuilder(this, iaPerk3, itemMeta -> {
                itemMeta.customName(Component.text(namePerk3));
                itemMeta.lore(lorePerk3);
                itemMeta.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
                itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            }));
        }
        Mascot mascot = city.getMascot();
        LivingEntity mascotMob = MascotUtils.loadMascot(mascot);
        Location mascotLocation = mascotMob == null ? new Location(Bukkit.getWorld("world"), 0, 0, 0) : mascotMob.getLocation();

        map.put(8, new ItemBuilder(this, new ItemStack(Bukkit.getItemFactory().getSpawnEgg(getEntityByMascotUUID(mascot.getMascotUUID()).getType())),
                itemMeta -> {
                    itemMeta.displayName(Component.text("§7Niveau de la Mascotte : §4" + mascot.getLevel()));
                    itemMeta.lore(List.of(Component.text("§7Location de la Mascotte : §c" + mascotLocation.getX() + " " + mascotLocation.getY() + " " + mascotLocation.getZ())));
                }));

        map.put(9, new ItemBuilder(this, new ItemStack(Material.PAPER),
                itemMeta -> itemMeta.displayName(Component.text("§7Taille : §6" + city.getChunks().size() + " chunks"))));

        map.put(22, new ItemBuilder(this, new ItemStack(Material.DIAMOND),
                itemMeta -> itemMeta.displayName(Component.text("§7Richesses : §6" + EconomyManager.getFormattedSimplifiedNumber(city.getBalance()) + " " + EconomyManager.getEconomyIcon()))));

        map.put(4, new ItemBuilder(this, new ItemStack(Material.PLAYER_HEAD), itemMeta -> {
            itemMeta.displayName(Component.text("§7Population : §d" + city.getMembers().size() + (city.getMembers().size() > 1 ? " joueurs" : " joueur")));
            itemMeta.lore(List.of(Component.text("§7Population connecté : §d" + city.getOnlineMembers().size() + (city.getMembers().size() > 1 ? " joueurs" : " joueur"))));
        }).setOnClick(inventoryClickEvent -> new WarPlayerListMenu(player, city).open()));

        map.put(26, new ItemBuilder(this, new ItemStack(city.getType().equals(CityType.WAR) ? Material.RED_BANNER : Material.GREEN_BANNER),
                itemMeta -> itemMeta.displayName(Component.text("§7Type : " + (city.getType().equals(CityType.WAR) ? "§cGuerre" : "§aPaix")))));

        map.put(18, new ItemBuilder(this, Material.ARROW, itemMeta -> {
            itemMeta.itemName(Component.text("§aRetour"));
            itemMeta.lore(List.of(
                    Component.text("§7Vous allez retourner au Menu de Guerre"),
                    Component.text("§e§lCLIQUEZ ICI POUR CONFIRMER")
            ));
        }).setOnClick(inventoryClickEvent -> new WarCityDetailsMenu(getOwner(), city).open()));

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
