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
import fr.openmc.core.registry.items.CustomItemRegistry;
import fr.openmc.core.utils.cache.CacheOfflinePlayer;
import fr.openmc.core.utils.text.messages.MessageType;
import fr.openmc.core.utils.text.messages.MessagesManager;
import fr.openmc.core.utils.text.messages.Prefix;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
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
                String nameKey;
                if (edit) {
                    nameKey = hasPerm
                            ? "feature.city.menus.perms.permission.remove"
                            : "feature.city.menus.perms.permission.add";
                } else {
                    nameKey = "feature.city.menus.perms.permission";
                }

                NamedTextColor permColor = hasPerm ? NamedTextColor.RED : NamedTextColor.GREEN;
                itemMeta.displayName(TranslationManager.translation(
                        nameKey,
                        permission.getDisplayName().color(permColor)
                ).decoration(TextDecoration.ITALIC, false));

                if (edit) {
                    String loreKey = hasPerm
                            ? "feature.city.menus.perms.permission.lore.remove"
                            : "feature.city.menus.perms.permission.lore.add";
                    itemMeta.lore(TranslationManager.translationLore(loreKey));
                } else {
                    itemMeta.lore(List.of());
                }
            }).setOnClick(inventoryClickEvent -> {
                if (!edit)
                    MessagesManager.sendMessage(getOwner(), TranslationManager.translation("messages.city.player_no_permission_access"), Prefix.CITY, MessageType.ERROR, true);
                else {
                    CityPermsCommands.swap(player, CacheOfflinePlayer.getOfflinePlayer(memberUUID), permission);
                    new CityPermsMenu(player, memberUUID, true).open();
                }
            }).hide(ItemUtils.getDataComponentType());

            items.add(itemBuilder);
        }

        return items;
    }

    @Override
    public Map<Integer, ItemBuilder> getButtons() {
        Map<Integer, ItemBuilder> map = new HashMap<>();

        map.put(45, new ItemBuilder(this, Material.ARROW, itemMeta -> {
            itemMeta.displayName(TranslationManager.translation("messages.menus.back"));
            itemMeta.lore(List.of(TranslationManager.translation("messages.menus.back_lore")));
        }, true));

        map.put(48, new ItemBuilder(this, CustomItemRegistry.getByName("_iainternal:icon_back_orange").getBest(), itemMeta -> {
            itemMeta.displayName(TranslationManager.translation("messages.menus.previous_page"));
            itemMeta.lore(TranslationManager.translationLore("messages.menus.previous_page_lore"));
        }).setPreviousPageButton());

        map.put(50, new ItemBuilder(this, CustomItemRegistry.getByName("_iainternal:icon_next_orange").getBest(), itemMeta -> {
            itemMeta.displayName(TranslationManager.translation("messages.menus.next_page"));
            itemMeta.lore(TranslationManager.translationLore("messages.menus.next_page_lore"));
        }).setNextPageButton());

        if (edit) {
            map.put(53, new ItemBuilder(this, Material.GOLD_BLOCK, itemMeta -> {
                itemMeta.displayName(TranslationManager.translation("feature.city.menus.perms.bulk.title"));
                itemMeta.lore(TranslationManager.translationLore("feature.city.menus.perms.bulk.lore"));
            }).setOnClick(inventoryClickEvent -> {
                if (inventoryClickEvent.isLeftClick()) CityPermsCommands.removeAll(getOwner(), CacheOfflinePlayer.getOfflinePlayer(memberUUID));
                else if (inventoryClickEvent.isRightClick()) CityPermsCommands.addAll(getOwner(), CacheOfflinePlayer.getOfflinePlayer(memberUUID));
                
	            new CityPermsMenu(getOwner(), memberUUID, true).open();
            }));
        }
        
        return map;
    }

    @Override
    public @NotNull Component getName() {
        return TranslationManager.translation("feature.city.menus.perms.name", Component.text(CacheOfflinePlayer.getOfflinePlayer(memberUUID).getName()));
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
