package fr.openmc.core.features.city.sub.war.menu.main;

import fr.openmc.api.menulib.Menu;
import fr.openmc.api.menulib.utils.InventorySize;
import fr.openmc.api.menulib.utils.ItemBuilder;
import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityType;
import fr.openmc.core.features.city.sub.mascots.models.Mascot;
import fr.openmc.core.features.city.sub.mayor.managers.MayorManager;
import fr.openmc.core.features.city.sub.mayor.managers.PerkManager;
import fr.openmc.core.features.city.sub.mayor.models.Mayor;
import fr.openmc.core.features.city.sub.mayor.perks.Perks;
import fr.openmc.core.features.economy.EconomyManager;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WarCityDetailsMenu extends Menu {

    private final City city;

    public WarCityDetailsMenu(Player owner, City city) {
        super(owner);
        this.city = city;
    }

    @Override
    public @NotNull Component getName() {
        return TranslationManager.translation(
                "feature.city.war.menu.details.title",
                Component.text(city.getName()).color(NamedTextColor.YELLOW)
        );
    }

    @Override
    public String getTexture() {
        return "§r§f:offset_-48::city_template3x9:";
    }

    @Override
    public @NotNull InventorySize getInventorySize() {
        return InventorySize.NORMAL;
    }

    @Override
    public void onInventoryClick(InventoryClickEvent inventoryClickEvent) {

    }

    @Override
    public @NotNull Map<Integer, ItemBuilder> getContent() {
        Map<Integer, ItemBuilder> map = new HashMap<>();
        Player player = getOwner();

        Mayor mayor = city.getMayor();
        if (MayorManager.phaseMayor == 2 && mayor != null) {
            Perks perk1 = PerkManager.getPerkById(mayor.getIdPerk1());
            Perks perk2 = PerkManager.getPerkById(mayor.getIdPerk2());
            Perks perk3 = PerkManager.getPerkById(mayor.getIdPerk3());

            ItemStack iaPerk1 = (perk1 != null) ? perk1.getItemStack() : ItemStack.of(Material.DEAD_BRAIN_CORAL_BLOCK);
            Component namePerk1 = (perk1 != null) ? TranslationManager.translation(perk1.getNameKey()) :
                    TranslationManager.translation("feature.city.mayor.perk.none.name");
            List<Component> lorePerk1 = (perk1 != null) ? new ArrayList<>(TranslationManager.translationLore(perk1.getLoreKey())) : null;
            map.put(11, new ItemBuilder(this, iaPerk1, itemMeta -> {
                itemMeta.customName(namePerk1);
                itemMeta.lore(lorePerk1);
            }).hide((perk1 != null) ? perk1.getToHide() : null));

            ItemStack iaPerk2 = (perk2 != null) ? perk2.getItemStack() : ItemStack.of(Material.DEAD_BRAIN_CORAL_BLOCK);
            Component namePerk2 = (perk2 != null) ? TranslationManager.translation(perk2.getNameKey()) :
                    TranslationManager.translation("feature.city.mayor.perk.none.name");
            List<Component> lorePerk2 = (perk2 != null) ? new ArrayList<>(TranslationManager.translationLore(perk2.getLoreKey())) : null;
            map.put(13, new ItemBuilder(this, iaPerk2, itemMeta -> {
                itemMeta.customName(namePerk2);
                itemMeta.lore(lorePerk2);
            }).hide((perk2 != null) ? perk2.getToHide() : null));

            ItemStack iaPerk3 = (perk3 != null) ? perk3.getItemStack() : ItemStack.of(Material.DEAD_BRAIN_CORAL_BLOCK);
            Component namePerk3 = (perk3 != null) ? TranslationManager.translation(perk3.getNameKey()) :
                    TranslationManager.translation("feature.city.mayor.perk.none.name");
            List<Component> lorePerk3 = (perk3 != null) ? new ArrayList<>(TranslationManager.translationLore(perk3.getLoreKey())) : null;
            map.put(15, new ItemBuilder(this, iaPerk3, itemMeta -> {
                itemMeta.customName(namePerk3);
                itemMeta.lore(lorePerk3);
            }).hide((perk3 != null) ? perk3.getToHide() : null));
        }
        Mascot mascot = city.getMascot();
        LivingEntity mascotMob = (LivingEntity) mascot.getEntity();
        Location mascotLocation = mascotMob == null ? new Location(Bukkit.getWorld("world"), 0, 0, 0) : mascotMob.getLocation();

        map.put(8, new ItemBuilder(this, city.getMascot().getMascotEgg(),
                itemMeta -> {
                    itemMeta.displayName(TranslationManager.translation(
                            "feature.city.war.menu.details.mascot.level",
                            Component.text(mascot.getLevel()).color(NamedTextColor.DARK_RED)
                    ).color(NamedTextColor.GRAY));
                    itemMeta.lore(List.of(TranslationManager.translation(
                            "feature.city.war.menu.details.mascot.location",
                            Component.text(mascotLocation.getX() + " " + mascotLocation.getY() + " " + mascotLocation.getZ()).color(NamedTextColor.RED)
                    ).color(NamedTextColor.GRAY)));
                }));

        map.put(9, new ItemBuilder(this, new ItemStack(Material.PAPER),
                itemMeta -> itemMeta.displayName(TranslationManager.translation(
                        "feature.city.war.menu.details.size",
                        Component.text(city.getChunks().size()).color(NamedTextColor.GOLD)
                ).color(NamedTextColor.GRAY))));

        map.put(22, new ItemBuilder(this, new ItemStack(Material.DIAMOND),
                itemMeta -> itemMeta.displayName(TranslationManager.translation(
                        "feature.city.war.menu.details.wealth",
                        Component.text(EconomyManager.getFormattedSimplifiedNumber(city.getBalance()) + " " + EconomyManager.getEconomyIcon())
                                .color(NamedTextColor.GOLD)
                ).color(NamedTextColor.GRAY))));

        map.put(4, new ItemBuilder(this, new ItemStack(Material.PLAYER_HEAD), itemMeta -> {
            itemMeta.displayName(TranslationManager.translation(
                    "feature.city.war.menu.details.population",
                    Component.text(city.getMembers().size()).color(NamedTextColor.LIGHT_PURPLE),
                    TranslationManager.translation(city.getMembers().size() > 1
                            ? "feature.city.war.menu.details.population.players"
                            : "feature.city.war.menu.details.population.player")
            ).color(NamedTextColor.GRAY));
            itemMeta.lore(List.of(TranslationManager.translation(
                    "feature.city.war.menu.details.population_online",
                    Component.text(city.getOnlineMembers().size()).color(NamedTextColor.LIGHT_PURPLE),
                    TranslationManager.translation(city.getOnlineMembers().size() > 1
                            ? "feature.city.war.menu.details.population.players"
                            : "feature.city.war.menu.details.population.player")
            ).color(NamedTextColor.GRAY)));
        }).setOnClick(inventoryClickEvent -> new WarPlayerListMenu(player, city).open()));

        map.put(26, new ItemBuilder(this, new ItemStack(city.getType().equals(CityType.WAR) ? Material.RED_BANNER : Material.GREEN_BANNER),
                itemMeta -> itemMeta.displayName(TranslationManager.translation(
                        "feature.city.war.menu.details.type",
                        city.getType().getDisplayName()
                ).color(NamedTextColor.GRAY))));

        map.put(18, new ItemBuilder(this, Material.ARROW, itemMeta -> {
            itemMeta.itemName(TranslationManager.translation("messages.menus.back"));
            itemMeta.lore(TranslationManager.translationLore("messages.menus.back_lore"));
        }, true));

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
