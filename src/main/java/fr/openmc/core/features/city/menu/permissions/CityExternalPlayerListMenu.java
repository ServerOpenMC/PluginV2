package fr.openmc.core.features.city.menu.permissions;

import fr.openmc.api.menulib.PaginatedMenu;
import fr.openmc.api.menulib.template.ItemMenuTemplate;
import fr.openmc.api.menulib.utils.InventorySize;
import fr.openmc.api.menulib.utils.ItemMenuBuilder;
import fr.openmc.api.menulib.utils.StaticSlots;
import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.CityPermission;
import fr.openmc.core.features.city.menu.CityPermsMenu;
import fr.openmc.core.utils.bukkit.SkullUtils;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CityExternalPlayerListMenu extends PaginatedMenu {

    public CityExternalPlayerListMenu(Player owner) {
        super(owner);
    }

    @Override
    public @Nullable Material getBorderMaterial() {
        return Material.AIR;
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
    public @NotNull List<Integer> getStaticSlots() {
        return StaticSlots.getStandardSlots(getInventorySize());
    }

    @Override
    public List<ItemStack> getItems() {
        List<ItemStack> items = new ArrayList<>();
        Player player = getOwner();

        City city = CityManager.getPlayerCity(player.getUniqueId());
        assert city != null;

        boolean hasPermissionPerms = city.hasPermission(player.getUniqueId(), CityPermission.MANAGE_PERMS);

        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            if (city.getMembers().contains(onlinePlayer.getUniqueId())) continue;

            items.add(new ItemMenuBuilder(this, SkullUtils.getPlayerSkull(onlinePlayer.getUniqueId()), itemMeta -> {
                itemMeta.displayName(Component.text(onlinePlayer.getName()).decoration(TextDecoration.ITALIC, false));
                itemMeta.lore(TranslationManager.translationLore("feature.city.menus.members.perms.lore"));
            }).setOnClick(_ -> {
                if (hasPermissionPerms) {
                    new CityPermsMenu(player, onlinePlayer.getUniqueId(), true).open();
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
        return TranslationManager.translation("feature.city.menus.perm_extern.name");
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
