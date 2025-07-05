package fr.openmc.core.features.city.sub.war.menu.main;

import fr.openmc.api.menulib.PaginatedMenu;
import fr.openmc.api.menulib.utils.InventorySize;
import fr.openmc.api.menulib.utils.ItemBuilder;
import fr.openmc.api.menulib.utils.ItemUtils;
import fr.openmc.api.menulib.utils.StaticSlots;
import fr.openmc.core.features.city.CPermission;
import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.CityType;
import fr.openmc.core.features.city.sub.mascots.models.Mascot;
import fr.openmc.core.features.city.sub.mayor.managers.MayorManager;
import fr.openmc.core.features.city.sub.mayor.managers.PerkManager;
import fr.openmc.core.features.city.sub.mayor.models.Mayor;
import fr.openmc.core.features.city.sub.mayor.perks.Perks;
import fr.openmc.core.features.city.sub.war.actions.WarActions;
import fr.openmc.core.features.city.sub.war.menu.MoreInfoMenu;
import fr.openmc.core.features.economy.EconomyManager;
import fr.openmc.core.utils.PlayerNameCache;
import fr.openmc.core.utils.customitems.CustomItemRegistry;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

public class MainWarMenu extends PaginatedMenu {

    public MainWarMenu(Player owner) {
        super(owner);
    }

    @Override
    public @Nullable Material getBorderMaterial() {
        return Material.GRAY_STAINED_GLASS_PANE;
    }

    @Override
    public @NotNull List<Integer> getStaticSlots() {
        return StaticSlots.STANDARD;
    }

    @Override
    public @NotNull InventorySize getInventorySize() {
        return InventorySize.LARGEST;
    }

    @Override
    public @NotNull List<ItemStack> getItems() {
        List<ItemStack> items = new ArrayList<>();
        Player player = getOwner();

            List<City> warCities = CityManager.getCities().stream()
                    .sorted((c1, c2) -> Integer.compare(c2.getOnlineMembers().size(), c1.getOnlineMembers().size()))
                    .collect(Collectors.toList());

            for (City city : warCities) {
                if (Objects.equals(city.getUUID(), CityManager.getPlayerCity(player.getUniqueId()).getUUID())) continue;
                if (city.getType() != CityType.WAR) continue;
                if (city.isImmune()) continue;

                long onlineCount = city.getOnlineMembers().size();

                UUID ownerUUID = city.getPlayerWithPermission(CPermission.OWNER);
                String ownerName = PlayerNameCache.getName(ownerUUID);

                Mascot mascot = city.getMascot();

                LivingEntity mascotMob = (LivingEntity) mascot.getEntity();
                Location mascotLocation = mascotMob == null ? new Location(Bukkit.getWorld("world"), 0, 0, 0) : mascotMob.getLocation();

                List<Component> loreCity = new ArrayList<>(List.of(
                        Component.text(""),
                        Component.text("§7Propriétaire : §d" + ownerName),
                        Component.text("§7Population (en ligne) : §a" + onlineCount),
                        Component.text("§7Mascotte  : §4niv. " + city.getMascot().getLevel()),
                        Component.text("§7Location : §c" + mascotLocation.getX() + " " + mascotLocation.getY() + " " + mascotLocation.getZ())
                ));

                Mayor mayor = city.getMayor();
                if (MayorManager.phaseMayor == 2 && mayor != null) {
                    Perks perk1 = PerkManager.getPerkById(mayor.getIdPerk1());
                    Perks perk2 = PerkManager.getPerkById(mayor.getIdPerk2());
                    Perks perk3 = PerkManager.getPerkById(mayor.getIdPerk3());

                    loreCity.add(Component.text("§7Réformes  : "));
                    if (perk1 != null) loreCity.add(Component.text("§8 - " + perk1.getName()));
                    if (perk2 != null) loreCity.add(Component.text("§8 - " + perk2.getName()));
                    if (perk3 != null) loreCity.add(Component.text("§8 - " + perk3.getName()));
                }

                loreCity.add(Component.text("§7Richesses : §6" + EconomyManager.getFormattedSimplifiedNumber(city.getBalance()) + EconomyManager.getEconomyIcon()));

                loreCity.add(Component.text(""));
                loreCity.add(Component.text("§e§lCLIQUE DROIT POUR PLUS D'INFORMATIONS SUR LA VILLE"));
                loreCity.add(Component.text("§e§lCLIQUE GAUCHE POUR LANCER UNE GUERRE"));


                items.add(new ItemBuilder(this, ItemUtils.getPlayerSkull(ownerUUID), itemMeta -> {
                    itemMeta.displayName(Component.text("§c" + city.getName()));
                    itemMeta.lore(loreCity);
                }).setOnClick(inventoryClickEvent -> {
                    if (inventoryClickEvent.getClick() == ClickType.LEFT) {
                        WarActions.beginLaunchWar(player, city);
                    } else if (inventoryClickEvent.getClick() == ClickType.RIGHT) {
                        new WarCityDetailsMenu(player, city).open();
                    }
                }));
            }
            return items;
    }

    @Override
    public List<Integer> getTakableSlot() {
        return List.of();
    }

    @Override
    public Map<Integer, ItemStack> getButtons() {
        Map<Integer, ItemStack> map = new HashMap<>();
        map.put(49, new ItemBuilder(this, Objects.requireNonNull(CustomItemRegistry.getByName("menu:close_button")).getBest(), itemMeta -> itemMeta.displayName(Component.text("§7Fermer"))).setCloseButton());
        map.put(48, new ItemBuilder(this, Objects.requireNonNull(CustomItemRegistry.getByName("menu:previous_page")).getBest(), itemMeta -> itemMeta.displayName(Component.text("§cPage précédente"))).setPreviousPageButton());
        map.put(50, new ItemBuilder(this, Objects.requireNonNull(CustomItemRegistry.getByName("menu:next_page")).getBest(), itemMeta -> itemMeta.displayName(Component.text("§aPage suivante"))).setNextPageButton());

        List<Component> loreInfo = Arrays.asList(
                Component.text("§7Apprenez en plus sur les Guerres !"),
                Component.text("§7La préparation..., le combat, ..."),
                Component.text("§e§lCLIQUEZ ICI POUR EN SAVOIR PLUS!")
        );

        map.put(53, new ItemBuilder(this, Material.BOOK, itemMeta -> {
            itemMeta.displayName(Component.text("§r§aPlus d'info !"));
            itemMeta.lore(loreInfo);
        }).setNextMenu(new MoreInfoMenu(getOwner())));

        return map;
    }

    @Override
    public @NotNull String getName() {
        return "Menu des guerres";
    }

    @Override
    public void onInventoryClick(InventoryClickEvent inventoryClickEvent) {
        //empty
    }

    @Override
    public void onClose(InventoryCloseEvent event) {
        //empty
    }
}
