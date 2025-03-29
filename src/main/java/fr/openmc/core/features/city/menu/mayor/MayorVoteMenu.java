package fr.openmc.core.features.city.menu.mayor;

import dev.xernas.menulib.PaginatedMenu;
import dev.xernas.menulib.utils.ItemBuilder;
import dev.xernas.menulib.utils.StaticSlots;
import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.mayor.PerkType;
import fr.openmc.core.features.city.mayor.Perks;
import fr.openmc.core.utils.customitems.CustomItemRegistry;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class MayorVoteMenu extends PaginatedMenu {
    public MayorVoteMenu(Player owner) {
        super(owner);
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
//        Player player = getOwner();
//
//        City city = CityManager.getPlayerCity(player.getUniqueId());
//        assert city != null;
//
        List<ItemStack> items = new ArrayList<>();
//
//        for (Perks newPerk : Perks.values()) {
//            if (newPerk == perk2 || newPerk == perk3) continue;
//
//            ItemStack perkItem = new ItemBuilder(this, newPerk.getMaterial(), itemMeta -> {
//                itemMeta.displayName(Component.text(newPerk.getName()));
//                itemMeta.lore(newPerk.getLore());
//            }).setOnClick(inventoryClickEvent -> {
//                boolean isPerkEvent = (newPerk.getType() != null && newPerk.getType() == PerkType.EVENT) && (
//                        (perk2 != null && perk2.getType() == PerkType.EVENT) ||
//                                (perk3 != null && perk3.getType() == PerkType.EVENT)
//                );
//                if (isPerkEvent) {
//                    MessagesManager.sendMessage(player, Component.text("Vous ne pouvez pas choisir 2 Réformes de Type Evenement!"), Prefix.CITY, MessageType.ERROR, false);
//                    return;
//                }
//
//                if (Objects.equals(perkNumber, "perk2")) {
//                    new MayorCreateMenu(player, newPerk, perk3).open();
//                } else if (Objects.equals(perkNumber, "perk3")) {
//                    new MayorCreateMenu(player, perk2, newPerk).open();
//                }
//
//            });
//
//            items.add(perkItem);
//        }
        return items;
    }

    @Override
    public Map<Integer, ItemStack> getButtons() {
        Map<Integer, ItemStack> map = new HashMap<>();
//        map.put(49, new ItemBuilder(this, CustomItemRegistry.getByName("menu:close_button").getBest(), itemMeta -> {
//            itemMeta.displayName(Component.text("§7Revenir en arrière"));
//        }).setOnClick(inventoryClickEvent -> {
//            new MayorCreateMenu(getOwner(), perk2, perk3).open();
//        }));
//        map.put(48, new ItemBuilder(this, CustomItemRegistry.getByName("menu:previous_page").getBest(), itemMeta -> {
//            itemMeta.displayName(Component.text("§cPage précédente"));
//        }).setPreviousPageButton());
//        map.put(50, new ItemBuilder(this, CustomItemRegistry.getByName("menu:next_page").getBest(), itemMeta -> {
//            itemMeta.displayName(Component.text("§aPage suivante"));
//        }).setNextPageButton());
        return map;
    }

    @Override
    public @NotNull String getName() {
        return "Menu des Villes - Membres";
    }

    @Override
    public void onInventoryClick(InventoryClickEvent inventoryClickEvent) {
        //empty
    }
}
