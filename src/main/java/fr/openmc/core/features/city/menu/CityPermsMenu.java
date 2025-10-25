package fr.openmc.core.features.city.menu;

import fr.openmc.api.menulib.PaginatedMenu;
import fr.openmc.api.menulib.utils.InventorySize;
import fr.openmc.api.menulib.utils.ItemBuilder;
import fr.openmc.api.menulib.utils.ItemUtils;
import fr.openmc.api.menulib.utils.StaticSlots;
import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.CityPermission;
import fr.openmc.core.features.city.commands.CityPermsCommands;
import fr.openmc.core.items.CustomItemRegistry;
import fr.openmc.core.utils.CacheOfflinePlayer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class CityPermsMenu extends PaginatedMenu {
    private final City city;
    private final UUID memberUUID;
    private final boolean edit;

    public CityPermsMenu(Player owner, UUID memberUUID, boolean edit) {
        super(owner);
        this.city = CityManager.getPlayerCity(owner.getUniqueId());
        this.memberUUID = memberUUID;
        this.edit = edit;
    }

    @Override
    public @Nullable Material getBorderMaterial() {
        return Material.AIR;
    }

    @Override
    public @NotNull List<Integer> getStaticSlots() {
        return StaticSlots.getStaticSlots(getInventorySize(), StaticSlots.Type.BOTTOM);
    }

    @Override
    public List<ItemStack> getItems() {
        List<ItemStack> items = new ArrayList<>();
        Player player = getOwner();

        Set<CityPermission> memberPerms = city.getPermissions(memberUUID);
        for (CityPermission permission : CityPermission.values()) {
            if (permission == CityPermission.OWNER) continue;

            boolean hasPerm = memberPerms != null && memberPerms.contains(permission);
            ItemBuilder itemBuilder = new ItemBuilder(this, permission.getIcon(), itemMeta -> {
                itemMeta.setEnchantmentGlintOverride(hasPerm);
                String name;
                if (edit) {
                    if (hasPerm) {
                        name = "§cRetirer " + permission.getDisplayName();
                    } else {
                        name = "§aAjouter " + permission.getDisplayName();
                    }
                } else {
                    if (hasPerm) {
                        name = "§c" + permission.getDisplayName();
                    } else {
                        name = "§a" + permission.getDisplayName();
                    }
                }

                itemMeta.displayName(Component.text(name).decoration(TextDecoration.ITALIC, false));

                List<Component> lore = List.of(
                        Component.text("§e§lCLIQUEZ POUR " + (hasPerm ? "RETIRER" : "AJOUTER") + " CETTE PERMISSION")
                );
                itemMeta.lore(edit ? lore : List.of());
            }).setOnClick(inventoryClickEvent -> {
                if (!edit) return;
                CityPermsCommands.swap(player, CacheOfflinePlayer.getOfflinePlayer(memberUUID), permission);
                player.closeInventory();
                this.open();
            }).hide(ItemUtils.getDataComponentType());

            items.add(itemBuilder);
        }

        return items;
    }

    @Override
    public Map<Integer, ItemBuilder> getButtons() {
        Map<Integer, ItemBuilder> map = new HashMap<>();

        map.put(45, new ItemBuilder(this, Material.ARROW, itemMeta -> {
            itemMeta.displayName(Component.text("§aRetour"));
            itemMeta.lore(List.of(Component.text("§7Retourner au menu précédent")));
        }, true));

        map.put(48, new ItemBuilder(this, CustomItemRegistry.getByName("_iainternal:icon_back_orange").getBest(), itemMeta -> {
            itemMeta.displayName(Component.text("§aPage précédente"));
            itemMeta.lore(List.of(Component.text("§7Cliquez pour aller à la page précédente")));
        }).setPreviousPageButton());

        map.put(50, new ItemBuilder(this, CustomItemRegistry.getByName("_iainternal:icon_next_orange").getBest(), itemMeta -> {
            itemMeta.displayName(Component.text("§aPage suivante"));
            itemMeta.lore(List.of(Component.text("§7Cliquez pour aller à la page suivante")));
        }).setNextPageButton());

        return map;
    }

    @Override
    public @NotNull String getName() {
        return "Permissions de " + CacheOfflinePlayer.getOfflinePlayer(memberUUID).getName();
    }

    @Override
    public String getTexture() {
        return "§r§f:offset_-48::city_template6x9:";
    }

    @Override
    public @NotNull InventorySize getInventorySize() {
        return InventorySize.LARGEST;
    }

    @Override
    public void onInventoryClick(InventoryClickEvent e) {
    }

    @Override
    public void onClose(InventoryCloseEvent event) {
    }

    @Override
    public List<Integer> getTakableSlot() {
        return List.of();
    }

    @Override
    public int getSizeOfItems() {
        return CityPermission.values().length - 1;
    }
}
