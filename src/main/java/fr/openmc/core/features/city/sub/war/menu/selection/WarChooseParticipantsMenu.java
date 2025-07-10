package fr.openmc.core.features.city.sub.war.menu.selection;

import fr.openmc.api.menulib.PaginatedMenu;
import fr.openmc.api.menulib.utils.ItemBuilder;
import fr.openmc.api.menulib.utils.ItemUtils;
import fr.openmc.api.menulib.utils.StaticSlots;
import fr.openmc.core.features.city.CPermission;
import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.sub.mayor.managers.MayorManager;
import fr.openmc.core.features.city.sub.war.actions.WarActions;
import fr.openmc.core.utils.CacheOfflinePlayer;
import fr.openmc.core.items.CustomItemRegistry;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
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
        return Material.GRAY_STAINED_GLASS_PANE;
    }

    @Override
    public @NotNull List<Integer> getStaticSlots() {
        return StaticSlots.STANDARD;
    }

    @Override
    public @NotNull List<ItemStack> getItems() {
        List<ItemStack> items = new ArrayList<>();
        Player player = getOwner();

        List<UUID> sortedMembers = cityLaunch.getOnlineMembers().stream()
                .sorted(Comparator.comparing((UUID uuid) -> !Objects.requireNonNull(Bukkit.getPlayer(uuid)).isOnline())
                        .thenComparing(uuid -> {
                            if (cityLaunch.hasPermission(uuid, CPermission.OWNER)) return 0;
                            else if (MayorManager.cityMayor.get(cityLaunch.getUUID()).getUUID().equals(uuid))
                                return 1;
                            else return 2;
                        }))
                .toList();

        for (UUID uuid : sortedMembers) {
            OfflinePlayer offline = CacheOfflinePlayer.getOfflinePlayer(uuid);
            boolean isSelected = selected.contains(uuid);
            boolean isOwner = cityLaunch.hasPermission(uuid, CPermission.OWNER);
            boolean isMayor = MayorManager.phaseMayor == 2 && cityLaunch.getMayor().getUUID().equals(uuid);

            String prefix = isOwner ? "Propriétaire " : isMayor ? "Maire " : "Membre ";

            ItemBuilder item = new ItemBuilder(this, ItemUtils.getPlayerSkull(uuid), meta -> {
                meta.displayName(Component.text((isSelected ? "§a✔ " : "") + prefix + offline.getName())
                        .decoration(TextDecoration.ITALIC, false));
                meta.lore(List.of(Component.text(isSelected ? "§c§lCLIQUEZ POUR RETIRER" : "§a§lCLIQUEZ POUR SÉLECTIONNER")));
            }).setOnClick(event -> {
                if (isSelected) {
                    selected.remove(uuid);
                } else {
                    if (selected.size() >= count) {
                        MessagesManager.sendMessage(player,
                                Component.text("Vous avez déjà sélectionné " + count + " joueur(s)."),
                                Prefix.CITY, MessageType.ERROR, false);
                        return;
                    }
                    selected.add(uuid);
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
    public Map<Integer, ItemStack> getButtons() {
        Map<Integer, ItemStack> map = new HashMap<>();
        Player player = getOwner();

        map.put(48, new ItemBuilder(this, CustomItemRegistry.getByName("_iainternal:icon_back_orange").getBest(), meta -> {
            meta.displayName(Component.text("§cPage précédente"));
        }).setPreviousPageButton());

        map.put(49, new ItemBuilder(this, CustomItemRegistry.getByName("_iainternal:icon_cancel").getBest(), meta -> {
            meta.displayName(Component.text("§7Fermer"));
        }).setCloseButton());

        map.put(50, new ItemBuilder(this, CustomItemRegistry.getByName("_iainternal:icon_next_orange").getBest(), meta -> {
            meta.displayName(Component.text("§aPage suivante"));
        }).setNextPageButton());

        map.put(53, new ItemBuilder(this, selected.size() == count ? Material.LIME_CONCRETE : Material.RED_CONCRETE, itemMeta -> {
            itemMeta.displayName(Component.text((selected.size() == count ? "§a" : "§c") + "Valider la sélection"));
            itemMeta.lore(List.of(
                    Component.text("§7Participants sélectionnés : §a" + selected.size() + "/" + count)
            ));
        }).setOnClick(e -> {
            if (selected.size() != count) {
                MessagesManager.sendMessage(player,
                        Component.text("Vous devez sélectionner " + count + " joueur(s)."),
                        Prefix.CITY, MessageType.ERROR, false);
                return;
            }

            List<UUID> attackers = selected.stream().toList();

            WarActions.confirmLaunchWar(player, cityLaunch, cityAttack, attackers);
        }));


        return map;
    }

    @Override
    public @NotNull String getName() {
        return "Menu de Guerre - Participants";
    }

    @Override
    public void onInventoryClick(InventoryClickEvent event) {
    }

    @Override
    public void onClose(InventoryCloseEvent event) {
    }
}
