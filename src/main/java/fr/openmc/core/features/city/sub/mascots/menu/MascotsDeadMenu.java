package fr.openmc.core.features.city.sub.mascots.menu;

import fr.openmc.api.cooldown.DynamicCooldownManager;
import fr.openmc.api.menulib.Menu;
import fr.openmc.api.menulib.utils.InventorySize;
import fr.openmc.api.menulib.utils.ItemBuilder;
import fr.openmc.api.menulib.utils.MenuUtils;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.utils.bukkit.ItemUtils;
import fr.openmc.core.utils.text.DateUtils;
import fr.openmc.core.utils.text.messages.MessageType;
import fr.openmc.core.utils.text.messages.MessagesManager;
import fr.openmc.core.utils.text.messages.Prefix;
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
import java.util.UUID;
import java.util.function.Supplier;

public class MascotsDeadMenu extends Menu {

    private final UUID cityUUID;

    private static final int AYWENITE_REDUCE = 32;
    private static final long COOLDOWN_REDUCE = 3600000L; // 1 hour in milliseconds


    public MascotsDeadMenu(Player owner, UUID cityUUID) {
        super(owner);
        this.cityUUID = cityUUID;
    }

    @Override
    public @NotNull Component getName() {
        return TranslationManager.translation("feature.city.mascots.menu.dead.name");
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

        Supplier<ItemBuilder> reduceItemSupplier = () -> {
            return new ItemBuilder(this, Material.DIAMOND, itemMeta -> {
                itemMeta.displayName(TranslationManager.translation("feature.city.mascots.menu.dead.title"));
                itemMeta.lore(TranslationManager.translationLore(
                        "feature.city.mascots.menu.dead.lore",
                        Component.text(DateUtils.convertMillisToTime(DynamicCooldownManager.getRemaining(cityUUID, "city:immunity"))).color(NamedTextColor.RED),
                        Component.text(AYWENITE_REDUCE).color(NamedTextColor.LIGHT_PURPLE)
                ));
            }).setOnClick(inventoryClickEvent -> {
                City city = CityManager.getCity(cityUUID);
                if (city == null) {
                    MessagesManager.sendMessage(player, TranslationManager.translation("messages.city.player_no_in_city"), Prefix.CITY, MessageType.ERROR, false);
                    player.closeInventory();
                    return;
                }

                if (!ItemUtils.takeAywenite(player, AYWENITE_REDUCE)) return;

                DynamicCooldownManager.reduceCooldown(player, cityUUID, "city:immunity", COOLDOWN_REDUCE);

                MessagesManager.sendMessage(player,
                        TranslationManager.translation(
                                "feature.city.mascots.menu.dead.reduce.success",
                                Component.text(AYWENITE_REDUCE).color(NamedTextColor.LIGHT_PURPLE)
                        ),
                        Prefix.CITY, MessageType.SUCCESS, false);
            });
        };
        MenuUtils.runDynamicItem(player, this, 13, reduceItemSupplier)
                .runTaskTimer(OMCPlugin.getInstance(), 0L, 20L);

        map.put(18, new ItemBuilder(this, Material.ARROW, itemMeta -> {
            itemMeta.displayName(TranslationManager.translation("messages.menus.back"));
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