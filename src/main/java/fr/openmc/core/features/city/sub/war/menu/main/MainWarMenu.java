package fr.openmc.core.features.city.sub.war.menu.main;

import fr.openmc.api.menulib.PaginatedMenu;
import fr.openmc.api.menulib.utils.InventorySize;
import fr.openmc.api.menulib.utils.ItemBuilder;
import fr.openmc.api.menulib.utils.StaticSlots;
import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.CityPermission;
import fr.openmc.core.features.city.CityType;
import fr.openmc.core.features.city.sub.mascots.models.Mascot;
import fr.openmc.core.features.city.sub.mayor.managers.MayorManager;
import fr.openmc.core.features.city.sub.mayor.managers.PerkManager;
import fr.openmc.core.features.city.sub.mayor.models.Mayor;
import fr.openmc.core.features.city.sub.mayor.perks.Perks;
import fr.openmc.core.features.city.sub.war.WarManager;
import fr.openmc.core.features.city.sub.war.actions.WarActions;
import fr.openmc.core.features.city.sub.war.menu.MoreInfoMenu;
import fr.openmc.core.features.economy.EconomyManager;
import fr.openmc.core.registry.items.CustomItemRegistry;
import fr.openmc.core.utils.bukkit.SkullUtils;
import fr.openmc.core.utils.cache.PlayerNameCache;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
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

public class MainWarMenu extends PaginatedMenu {

    public MainWarMenu(Player owner) {
        super(owner);
    }

    @Override
    public @Nullable Material getBorderMaterial() {
        return Material.AIR;
    }

    @Override
    public @NotNull List<Integer> getStaticSlots() {
        return StaticSlots.getStandardSlots(getInventorySize());
    }

    @Override
    public @NotNull InventorySize getInventorySize() {
        return InventorySize.LARGEST;
    }

    @Override
    public int getSizeOfItems() {
        return getItems().size();
    }

    @Override
    public List<ItemStack> getItems() {
        List<ItemStack> items = new ArrayList<>();
        Player player = getOwner();

        List<City> warCities = CityManager.getCities().stream()
                .sorted((c1, c2) -> Integer.compare(c2.getOnlineMembers().size(), c1.getOnlineMembers().size()))
                .toList();

        for (City city : warCities) {
            if (city.getUniqueId().equals(CityManager.getPlayerCity(player.getUniqueId()).getUniqueId())) continue;
            if (city.getType() != CityType.WAR) continue;
            if (city.isImmune()) continue;
            if (WarManager.getPendingDefenseFor(city) != null) continue;
            if (city.isInWar()) continue;

            long onlineCount = city.getOnlineMembers().size();

            UUID ownerUUID = city.getPlayerWithPermission(CityPermission.OWNER);

            Mascot mascot = city.getMascot();

            LivingEntity mascotMob = (LivingEntity) mascot.getEntity();
            Location mascotLocation = mascotMob == null ? new Location(Bukkit.getWorld("world"), 0, 0, 0) : mascotMob.getLocation();

            List<Component> loreCity = new ArrayList<>(List.of(
                    Component.empty(),
                    TranslationManager.translation(
                            "feature.city.war.menu.main.owner",
                            Component.text(PlainTextComponentSerializer.plainText().serialize(PlayerNameCache.name(player.getUniqueId())))
                                    .color(NamedTextColor.LIGHT_PURPLE)
                    ).color(NamedTextColor.GRAY),
                    TranslationManager.translation(
                            "feature.city.war.menu.main.population_online",
                            Component.text(onlineCount).color(NamedTextColor.GREEN)
                    ).color(NamedTextColor.GRAY),
                    TranslationManager.translation(
                            "feature.city.war.menu.main.mascot_level",
                            Component.text(city.getMascot().getLevel()).color(NamedTextColor.DARK_RED)
                    ).color(NamedTextColor.GRAY),
                    TranslationManager.translation(
                            "feature.city.war.menu.main.mascot_location",
                            Component.text(mascotLocation.getX() + " " + mascotLocation.getY() + " " + mascotLocation.getZ()).color(NamedTextColor.RED)
                    ).color(NamedTextColor.GRAY)
            ));

            Mayor mayor = city.getMayor();
            if (MayorManager.phaseMayor == 2 && mayor != null) {
                Perks perk1 = PerkManager.getPerkById(mayor.getIdPerk1());
                Perks perk2 = PerkManager.getPerkById(mayor.getIdPerk2());
                Perks perk3 = PerkManager.getPerkById(mayor.getIdPerk3());

                loreCity.add(TranslationManager.translation("feature.city.war.menu.main.reforms").color(NamedTextColor.GRAY));
                if (perk1 != null) loreCity.add(Component.text(" - ")
                        .color(NamedTextColor.DARK_GRAY)
                        .append(TranslationManager.translation(perk1.getNameKey())));
                if (perk2 != null) loreCity.add(Component.text("- ")
                        .color(NamedTextColor.DARK_GRAY)
                        .append(TranslationManager.translation(perk2.getNameKey())));
                if (perk3 != null) loreCity.add(Component.text(" - ")
                        .color(NamedTextColor.DARK_GRAY)
                        .append(TranslationManager.translation(perk3.getNameKey())));
            }

            loreCity.add(TranslationManager.translation(
                    "feature.city.war.menu.main.wealth",
                    Component.text(EconomyManager.getFormattedSimplifiedNumber(city.getBalance()) + EconomyManager.getEconomyIcon())
                            .color(NamedTextColor.GOLD)
            ).color(NamedTextColor.GRAY));

            loreCity.add(Component.empty());
            loreCity.add(TranslationManager.translation("feature.city.war.menu.main.click_details")
                    .color(NamedTextColor.YELLOW).decorate(TextDecoration.BOLD));
            loreCity.add(TranslationManager.translation("feature.city.war.menu.main.click_launch")
                    .color(NamedTextColor.YELLOW).decorate(TextDecoration.BOLD));


            items.add(new ItemBuilder(this, SkullUtils.getPlayerSkull(ownerUUID), itemMeta -> {
                itemMeta.displayName(Component.text(city.getName()).color(NamedTextColor.RED));
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
    public Map<Integer, ItemBuilder> getButtons() {
        Map<Integer, ItemBuilder> map = new HashMap<>();
        map.put(49, new ItemBuilder(this, Objects.requireNonNull(CustomItemRegistry.getByName("_iainternal:icon_cancel")).getBest(), itemMeta -> itemMeta.displayName(TranslationManager.translation("messages.menus.close"))).setCloseButton());
        map.put(48, new ItemBuilder(this, Objects.requireNonNull(CustomItemRegistry.getByName("_iainternal:icon_back_orange")).getBest(), itemMeta -> itemMeta.displayName(TranslationManager.translation("messages.menus.previous_page"))).setPreviousPageButton());
        map.put(50, new ItemBuilder(this, Objects.requireNonNull(CustomItemRegistry.getByName("_iainternal:icon_next_orange")).getBest(), itemMeta -> itemMeta.displayName(TranslationManager.translation("messages.menus.next_page"))).setNextPageButton());

        List<Component> loreInfo = TranslationManager.translationLore("feature.city.war.menu.more_info.lore");

        map.put(53, new ItemBuilder(this, Material.BOOK, itemMeta -> {
            itemMeta.displayName(TranslationManager.translation("feature.city.war.menu.more_info.title"));
            itemMeta.lore(loreInfo);
        }).setOnClick(inventoryClickEvent -> new MoreInfoMenu(getOwner()).open()));

        return map;
    }

    @Override
    public @NotNull Component getName() {
        return TranslationManager.translation("feature.city.war.menu.main.title");
    }

    @Override
    public String getTexture() {
        return "§r§f:offset_-48::city_template6x9:";
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
