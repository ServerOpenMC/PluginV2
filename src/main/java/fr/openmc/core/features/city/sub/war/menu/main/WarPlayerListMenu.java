package fr.openmc.core.features.city.sub.war.menu.main;

import fr.openmc.api.menulib.PaginatedMenu;
import fr.openmc.api.menulib.template.ItemMenuTemplate;
import fr.openmc.api.menulib.utils.InventorySize;
import fr.openmc.api.menulib.utils.ItemMenuBuilder;
import fr.openmc.api.menulib.utils.StaticSlots;
import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityPermission;
import fr.openmc.core.features.city.sub.mayor.managers.MayorManager;
import fr.openmc.core.utils.bukkit.SkullUtils;
import fr.openmc.core.utils.cache.CacheOfflinePlayer;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class WarPlayerListMenu extends PaginatedMenu {

    private final City city;

    public WarPlayerListMenu(Player owner, City city) {
        super(owner);
        this.city = city;
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
    public @Nullable Material getBorderMaterial() {
        return Material.AIR;
    }

    @Override
    public @NotNull List<Integer> getStaticSlots() {
        return StaticSlots.getStandardSlots(getInventorySize());
    }

    @Override
    public List<ItemStack> getItems() {
        List<ItemStack> items = new ArrayList<>();

        List<UUID> sortedMembers = city.getMembers().stream()
                .sorted(Comparator.comparing((UUID uuid) -> !Bukkit.getPlayer(uuid).isOnline())
                        .thenComparing(uuid -> {
                            if (city.hasPermission(uuid, CityPermission.OWNER)) return 0;
                            else if (MayorManager.cityMayor.get(city.getUniqueId()).getMayorUUID().equals(uuid))
                                return 1;
                            else return 2;
                        }))
                .toList();

        for (UUID memberUUID : sortedMembers) {
            OfflinePlayer playerOffline = CacheOfflinePlayer.getOfflinePlayer(memberUUID);

            boolean hasPermissionOwner = city.hasPermission(memberUUID, CityPermission.OWNER);
            String title;
            if (hasPermissionOwner) {
                title = TranslationManager.translationString("feature.city.war.menu.players.role.owner");
            } else if (MayorManager.cityMayor.get(city.getUniqueId()).getMayorUUID().equals(memberUUID)) {
                title = TranslationManager.translationString("feature.city.war.menu.players.role.mayor");
            } else {
                title = TranslationManager.translationString("feature.city.war.menu.players.role.member");
            }

            String finalTitle = title;
            items.add(new ItemMenuBuilder(this, SkullUtils.getPlayerSkull(memberUUID), itemMeta -> itemMeta.displayName(Component.text(finalTitle + playerOffline.getName()).decoration(TextDecoration.ITALIC, false))));
        }

        return items;
    }

    @Override
    public List<Integer> getTakableSlot() {
        return List.of();
    }

    @Override
    public Map<Integer, ItemMenuBuilder> getButtons() {
        Map<Integer, ItemMenuBuilder> map = new HashMap<>();
        map.put(45, new ItemMenuBuilder(this, Material.ARROW, true));
        map.put(49, ItemMenuTemplate.BTN_CANCEL.apply(this));
        map.put(48, ItemMenuTemplate.BTN_PREVIOUS_PAGE_ORANGE.apply(this));
        map.put(50, ItemMenuTemplate.BTN_NEXT_PAGE_ORANGE.apply(this));
        return map;
    }

    @Override
    public @NotNull Component getName() {
        return TranslationManager.translation("feature.city.war.menu.players.title");
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
