package fr.openmc.core.features.city.sub.war.menu.selection;

import fr.openmc.api.menulib.PaginatedMenu;
import fr.openmc.api.menulib.utils.InventorySize;
import fr.openmc.api.menulib.utils.ItemBuilder;
import fr.openmc.api.menulib.utils.StaticSlots;
import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityPermission;
import fr.openmc.core.features.city.sub.mayor.managers.MayorManager;
import fr.openmc.core.features.city.sub.war.actions.WarActions;
import fr.openmc.core.registry.items.CustomItemRegistry;
import fr.openmc.core.utils.bukkit.SkullUtils;
import fr.openmc.core.utils.cache.CacheOfflinePlayer;
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

public class WarChooseParticipantsMenu extends PaginatedMenu {

    private final City cityLaunch;
    private final City cityAttack;
    private final int count;
    private final Set<UUID> selected;

    public WarChooseParticipantsMenu(Player owner, City cityLaunch, City cityAttack, int count, Set<UUID> selected) {
        super(owner);
        this.cityLaunch = cityLaunch;
        this.cityAttack = cityAttack;
        this.count = count;
        this.selected = selected;
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

        List<UUID> sortedMembers = cityLaunch.getOnlineMembers().stream()
                .sorted(Comparator.comparing((UUID uuid) -> !Objects.requireNonNull(Bukkit.getPlayer(uuid)).isOnline())
                        .thenComparing(uuid -> {
                            if (cityLaunch.hasPermission(uuid, CityPermission.OWNER)) return 0;
                            else if (MayorManager.cityMayor.get(cityLaunch.getUniqueId()) != null && (MayorManager.cityMayor.get(cityLaunch.getUniqueId()).getMayorUUID().equals(uuid)))
                                return 1;
                            else return 2;
                        }))
                .toList();

        for (UUID memberUUID : sortedMembers) {
            OfflinePlayer offline = CacheOfflinePlayer.getOfflinePlayer(memberUUID);
            boolean isSelected = selected.contains(memberUUID);
            boolean isOwner = cityLaunch.hasPermission(memberUUID, CityPermission.OWNER);
            boolean isMayor = MayorManager.phaseMayor == 2
                    && cityLaunch.getMayor() != null
                    && cityLaunch.getMayor().getMayorUUID().equals(memberUUID);

            String prefix = isOwner
                    ? TranslationManager.translationString("feature.city.war.menu.players.role.owner")
                    : isMayor
                    ? TranslationManager.translationString("feature.city.war.menu.players.role.mayor")
                    : TranslationManager.translationString("feature.city.war.menu.players.role.member");

            ItemBuilder item = new ItemBuilder(this, SkullUtils.getPlayerSkull(memberUUID), meta -> {
                Component prefixComponent = isSelected
                        ? TranslationManager.translation("feature.city.war.menu.participants.selected_prefix")
                        : Component.empty();
                meta.displayName(prefixComponent.append(Component.text(prefix + offline.getName()))
                        .decoration(TextDecoration.ITALIC, false));
                meta.lore(List.of(TranslationManager.translation(
                        isSelected ? "feature.city.war.menu.participants.click_remove" : "feature.city.war.menu.participants.click_select"
                ).color(isSelected ? NamedTextColor.RED : NamedTextColor.GREEN).decorate(TextDecoration.BOLD)));
            }).setOnClick(event -> {
                if (isSelected) {
                    selected.remove(memberUUID);
                } else {
                    if (selected.size() >= count) {
                        MessagesManager.sendMessage(player,
                                TranslationManager.translation(
                                        "feature.city.war.menu.participants.already_selected",
                                        Component.text(count)
                                ),
                                Prefix.CITY, MessageType.ERROR, false);
                        return;
                    }
                    selected.add(memberUUID);
                }
                new WarChooseParticipantsMenu(player, cityLaunch, cityAttack, count, selected).open();
            });

            items.add(item);
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
        Player player = getOwner();

        map.put(48, new ItemBuilder(this, CustomItemRegistry.getByName("_iainternal:icon_back_orange").getBest(), meta -> {
            meta.displayName(TranslationManager.translation("messages.menus.previous_page"));
        }).setPreviousPageButton());

        map.put(49, new ItemBuilder(this, CustomItemRegistry.getByName("_iainternal:icon_cancel").getBest(), meta -> {
            meta.displayName(TranslationManager.translation("messages.menus.close"));
        }).setCloseButton());

        map.put(50, new ItemBuilder(this, CustomItemRegistry.getByName("_iainternal:icon_next_orange").getBest(), meta -> {
            meta.displayName(TranslationManager.translation("messages.menus.next_page"));
        }).setNextPageButton());

        map.put(53, new ItemBuilder(this, selected.size() == count ? Material.LIME_CONCRETE : Material.RED_CONCRETE, itemMeta -> {
            itemMeta.displayName(TranslationManager.translation("feature.city.war.menu.participants.confirm.title")
                    .color(selected.size() == count ? NamedTextColor.GREEN : NamedTextColor.RED));
            itemMeta.lore(List.of(TranslationManager.translation(
                    "feature.city.war.menu.participants.confirm.lore",
                    Component.text(selected.size()).color(NamedTextColor.GREEN),
                    Component.text(count).color(NamedTextColor.RED)
            ).color(NamedTextColor.GRAY)));
        }).setOnClick(e -> {
            if (selected.size() != count) {
                MessagesManager.sendMessage(player,
                        TranslationManager.translation(
                                "feature.city.war.menu.participants.must_select",
                                Component.text(count).color(NamedTextColor.RED)
                        ),
                        Prefix.CITY, MessageType.ERROR, false);
                return;
            }

            List<UUID> attackers = selected.stream().toList();

            WarActions.confirmLaunchWar(player, cityLaunch, cityAttack, attackers);
        }));


        return map;
    }

    @Override
    public @NotNull Component getName() {
        return TranslationManager.translation("feature.city.war.menu.participants.title");
    }

    @Override
    public String getTexture() {
        return "§r§f:offset_-48::city_template6x9:";
    }

    @Override
    public void onInventoryClick(InventoryClickEvent event) {
    }

    @Override
    public void onClose(InventoryCloseEvent event) {
    }
}
