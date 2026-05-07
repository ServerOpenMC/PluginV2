package fr.openmc.core.features.city.menu.playerlist;

import fr.openmc.api.input.dialog.DialogInput;
import fr.openmc.api.menulib.PaginatedMenu;
import fr.openmc.api.menulib.template.ConfirmMenu;
import fr.openmc.api.menulib.utils.InventorySize;
import fr.openmc.api.menulib.utils.ItemBuilder;
import fr.openmc.api.menulib.utils.StaticSlots;
import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.CityPermission;
import fr.openmc.core.features.city.actions.CityKickAction;
import fr.openmc.core.features.city.commands.CityInviteCommands;
import fr.openmc.core.features.city.menu.CityPermsMenu;
import fr.openmc.core.features.city.sub.milestone.rewards.MemberLimitRewards;
import fr.openmc.core.registry.items.CustomItemRegistry;
import fr.openmc.core.utils.bukkit.SkullUtils;
import fr.openmc.core.utils.cache.CacheOfflinePlayer;
import fr.openmc.core.utils.text.InputUtils;
import fr.openmc.core.utils.text.messages.MessageType;
import fr.openmc.core.utils.text.messages.MessagesManager;
import fr.openmc.core.utils.text.messages.Prefix;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
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

import static fr.openmc.core.utils.text.InputUtils.MAX_LENGTH_PLAYERNAME;

public class CityPlayerListMenu extends PaginatedMenu {

    public CityPlayerListMenu(Player owner) {
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

        boolean hasPermissionKick = city.hasPermission(player.getUniqueId(), CityPermission.KICK);
        boolean hasPermissionPerms = city.hasPermission(player.getUniqueId(), CityPermission.MANAGE_PERMS);
        boolean hasPermissionOwner = city.hasPermission(player.getUniqueId(), CityPermission.OWNER);

        for (UUID uuid : city.getMembers()) {
            OfflinePlayer playerOffline = CacheOfflinePlayer.getOfflinePlayer(uuid);

            String title = city.getRankName(uuid) + " ";

            List<Component> lorePlayer;
            if (city.hasPermission(playerOffline.getUniqueId(), CityPermission.OWNER)) {
                lorePlayer = TranslationManager.translationLore("feature.city.menus.members.owner.lore");
            } else if (hasPermissionPerms && hasPermissionKick) {
                if (city.hasPermission(playerOffline.getUniqueId(), CityPermission.OWNER)) {
                    lorePlayer = TranslationManager.translationLore("feature.city.menus.members.cant_edit_owner.lore");
                } else {
                    lorePlayer = TranslationManager.translationLore("feature.city.menus.members.manage.lore");
                }
            } else if (hasPermissionPerms) {
                lorePlayer = TranslationManager.translationLore("feature.city.menus.members.perms.lore");
            } else if (hasPermissionKick) {
                if (player.getUniqueId().equals(playerOffline.getUniqueId())) {
                    lorePlayer = TranslationManager.translationLore("feature.city.menus.members.cant_self_kick.lore");
                } else if (city.hasPermission(playerOffline.getUniqueId(), CityPermission.OWNER)) {
                    lorePlayer = TranslationManager.translationLore("feature.city.menus.members.cant_kick_owner.lore");
                } else {
                    lorePlayer = TranslationManager.translationLore("feature.city.menus.members.kick.lore");
                }
            } else {
                lorePlayer = TranslationManager.translationLore("feature.city.menus.members.member.lore");
            }

            List<Component> finalLorePlayer = lorePlayer;
            items.add(new ItemBuilder(this, SkullUtils.getPlayerSkull(uuid), itemMeta -> {
                itemMeta.displayName(Component.text(title + playerOffline.getName()).decoration(TextDecoration.ITALIC, false));
                itemMeta.lore(finalLorePlayer);
            }).setOnClick(inventoryClickEvent -> {
                if (city.hasPermission(playerOffline.getUniqueId(), CityPermission.OWNER)) {
                    return;
                }

                if (hasPermissionPerms && hasPermissionKick) {
                    CityPlayerGestionMenu menu = new CityPlayerGestionMenu(player, playerOffline);
                    menu.open();
                } else if (hasPermissionPerms) {
                    new CityPermsMenu(player, playerOffline.getUniqueId(), false).open();
                } else if (hasPermissionKick) {
                    if (player.getUniqueId().equals(playerOffline.getUniqueId()))
                        return;

                    if (city.hasPermission(playerOffline.getUniqueId(), CityPermission.OWNER))
                        return;

                    ConfirmMenu menu = new ConfirmMenu(
                            player,
                            () -> {
                                player.closeInventory();
                                CityKickAction.startKick(player, playerOffline);
                            },
                            player::closeInventory,
                            List.of(TranslationManager.translation("feature.city.menus.members.kick.confirm", Component.text(playerOffline.getName()).color(NamedTextColor.GRAY))),
                            List.of(TranslationManager.translation("feature.city.menus.members.kick.deny", Component.text(playerOffline.getName()).color(NamedTextColor.GRAY))));
                    menu.open();
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
        Player player = getOwner();

        City playerCity = CityManager.getPlayerCity(player.getUniqueId());

        Map<Integer, ItemBuilder> map = new HashMap<>();
        map.put(45, new ItemBuilder(this, Material.ARROW, itemMeta -> {
          itemMeta.displayName(TranslationManager.translation("messages.menus.back"));
          itemMeta.lore(List.of(TranslationManager.translation("messages.menus.back_lore")));
        }, true));

        map.put(49, new ItemBuilder(this, CustomItemRegistry.getByName("_iainternal:icon_cancel").getBest(), itemMeta -> {
	        itemMeta.displayName(TranslationManager.translation("messages.menus.close"));
        }).setOnClick(inventoryClickEvent ->
                getOwner().closeInventory()
        ));

        map.put(48,
                new ItemBuilder(this,
                        Objects.requireNonNull(CustomItemRegistry.getByName("_iainternal:icon_back_orange")).getBest(),
	                    itemMeta -> itemMeta.displayName(TranslationManager.translation("messages.menus.previous_page"))).setPreviousPageButton());
        map.put(50,
                new ItemBuilder(this, Objects.requireNonNull(CustomItemRegistry.getByName("_iainternal:icon_next_orange")).getBest(),
	                    itemMeta -> itemMeta.displayName(TranslationManager.translation("messages.menus.next_page"))).setNextPageButton());


        map.put(53, new ItemBuilder(this, Objects.requireNonNull(CustomItemRegistry.getByName("_iainternal:icon_search")).getBest(), itemMeta -> {
	        itemMeta.displayName(TranslationManager.translation("feature.city.menus.members.invite.title"));
            itemMeta.lore(
                    TranslationManager.translationLore(
                            "feature.city.menus.members.invite.lore",
                            Component.text(playerCity.getMembers().size()).color(NamedTextColor.GRAY),
                            Component.text(MemberLimitRewards.getMemberLimit(playerCity.getLevel())).color(NamedTextColor.GRAY)
                    )
            );
        }).setOnClick(inventoryClickEvent -> {
	        DialogInput.send(player, TranslationManager.translation("feature.city.menus.members.invite.prompt"), MAX_LENGTH_PLAYERNAME, input -> {
                if (input == null) return;

                if (InputUtils.isInputPlayer(input)) {
                    Player playerToInvite = Bukkit.getPlayer(input);
                    CityInviteCommands.invite(player, playerToInvite);
                } else {
	                MessagesManager.sendMessage(player, TranslationManager.translation("feature.city.menus.members.invite.invalid"), Prefix.CITY, MessageType.ERROR, true);
                }
            });
        }));
        return map;
    }

    @Override
    public @NotNull Component getName() {
	    return TranslationManager.translation("feature.city.menus.members.name");
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
