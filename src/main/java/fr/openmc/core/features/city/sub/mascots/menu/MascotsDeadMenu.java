package fr.openmc.core.features.city.sub.mascots.menu;

import fr.openmc.api.cooldown.DynamicCooldownManager;
import fr.openmc.api.menulib.Menu;
import fr.openmc.api.menulib.utils.InventorySize;
import fr.openmc.api.menulib.utils.ItemBuilder;
import fr.openmc.api.menulib.utils.MenuUtils;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.menu.CityMenu;
import fr.openmc.core.items.CustomItemRegistry;
import fr.openmc.core.utils.DateUtils;
import fr.openmc.core.utils.ItemUtils;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class MascotsDeadMenu extends Menu {

    private final String city_uuid;

    private static final int AYWENITE_REDUCE = 32;
    private static final long COOLDOWN_REDUCE = 3600000L; // 1 hour in milliseconds


    public MascotsDeadMenu(Player owner, String city_uuid) {
        super(owner);
        this.city_uuid = city_uuid;
    }

    @Override
    public @NotNull String getName() {
        return "Menu des Mascottes [DEAD]";
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

        Supplier<ItemStack> reduceItemSupplier = () -> {
            return new ItemBuilder(this, Material.DIAMOND, itemMeta -> {
                itemMeta.displayName(Component.text("§7Votre §cMascotte §7est morte"));
                itemMeta.lore(List.of(
                        Component.text("§7Votre §cmascotte est morte§7, vous pouvez faire réduire le temps de réanimation"),
                        Component.text("§7qui est actuellement de :"),
                        Component.text("§8 - §c" + DateUtils.convertMillisToTime(DynamicCooldownManager.getRemaining(city_uuid, "city:immunity"))),
                        Component.text("§7Pour réduire le temps de 1 heure, vous devez posséder de :"),
                        Component.text("§8- §d" + AYWENITE_REDUCE + " d'Aywenite"),
                        Component.empty(),
                        Component.text("§e§lCLIQUEZ ICI POUR REDUIRE LE TEMPS DE REANIMATION")
                ));
            }).setOnClick(inventoryClickEvent -> {
                City city = CityManager.getCity(city_uuid);
                if (city == null) {
                    MessagesManager.sendMessage(player, MessagesManager.Message.PLAYERNOCITY.getMessage(), Prefix.CITY, MessageType.ERROR, false);
                    player.closeInventory();
                    return;
                }

                if (!ItemUtils.takeAywenite(player, AYWENITE_REDUCE)) return;
                
                DynamicCooldownManager.reduceCooldown(player, city_uuid, "city:immunity", COOLDOWN_REDUCE);

                MessagesManager.sendMessage(player, Component.text("Vous venez de dépenser §d" + AYWENITE_REDUCE + " d'Aywenite §fpour §bréduire §fle cooldown d'une heure"), Prefix.CITY, MessageType.SUCCESS, false);
            });
        };
        MenuUtils.runDynamicItem(player, this, 13, reduceItemSupplier)
                .runTaskTimer(OMCPlugin.getInstance(), 0L, 20L);

        map.put(18, new ItemBuilder(this, Material.ARROW, itemMeta -> {
            itemMeta.displayName(Component.text("§aRetour"));
            itemMeta.lore(List.of(Component.text("§7Retourner au menu des villes")));
        }).setOnClick(event -> {
            CityMenu menu = new CityMenu(player);
            menu.open();
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