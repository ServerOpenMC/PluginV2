package fr.openmc.core.features.city.menu;

import fr.openmc.api.menulib.Menu;
import fr.openmc.api.menulib.utils.InventorySize;
import fr.openmc.api.menulib.utils.ItemBuilder;
import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.CityType;
import fr.openmc.core.features.city.actions.CityChangeAction;
import fr.openmc.core.features.city.conditions.CityTypeConditions;
import fr.openmc.core.features.city.sub.milestone.rewards.FeaturesRewards;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class CityTypeMenu extends Menu {
    public CityTypeMenu(Player owner) {
        super(owner);
    }

    @Override
    public @NotNull Component getName() {
        return TranslationManager.translation("feature.city.menus.type.name");
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

        City city = CityManager.getPlayerCity(player.getUniqueId());
        boolean enchantPeace = city.getType() == CityType.PEACE;
        List<Component> peaceInfo = TranslationManager.translationLore("feature.city.menus.type.peace.lore");

        map.put(11, new ItemBuilder(this, Material.POPPY, itemMeta -> {
            itemMeta.displayName(TranslationManager.translation("feature.city.menus.type.peace.title"));
            itemMeta.lore(peaceInfo);
            itemMeta.setEnchantmentGlintOverride(enchantPeace);
        }).setOnClick(inventoryClickEvent -> {
            if (!CityTypeConditions.canCityChangeType(city, player, CityType.PEACE)) return;

            CityChangeAction.beginChangeCity(player, CityType.PEACE);
        }));

        List<Component> warInfo;
        if (!FeaturesRewards.hasUnlockFeature(city, FeaturesRewards.Feature.TYPE_WAR)) {
            warInfo = TranslationManager.translationLore(
                    "feature.city.menus.type.war.lore.locked",
                    Component.text(FeaturesRewards.getFeatureUnlockLevel(FeaturesRewards.Feature.TYPE_WAR)).color(NamedTextColor.RED)
            );
        } else {
            warInfo = TranslationManager.translationLore("feature.city.menus.type.war.lore");
        }

        boolean enchantWar = city.getType() == CityType.WAR;
        map.put(15, new ItemBuilder(this, Material.TNT, itemMeta -> {
            itemMeta.displayName(TranslationManager.translation("feature.city.menus.type.war.title"));
            itemMeta.lore(warInfo);
            itemMeta.setEnchantmentGlintOverride(enchantWar);
        }).setOnClick(inventoryClickEvent -> {
            if (!CityTypeConditions.canCityChangeType(city, player, CityType.WAR)) return;

            CityChangeAction.beginChangeCity(player, CityType.WAR);
        }));

        map.put(18, new ItemBuilder(this, Material.ARROW, itemMeta -> {
            itemMeta.itemName(TranslationManager.translation("messages.menus.back"));
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
