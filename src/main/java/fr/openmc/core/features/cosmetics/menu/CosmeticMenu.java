package fr.openmc.core.features.cosmetics.menu;

import com.sk89q.jnbt.NamedTag;
import dev.lone.itemsadder.api.CustomStack;
import fr.openmc.api.menulib.PaginatedMenu;
import fr.openmc.api.menulib.default_menu.ConfirmMenu;
import fr.openmc.api.menulib.utils.ItemBuilder;
import fr.openmc.api.menulib.utils.ItemUtils;
import fr.openmc.api.menulib.utils.StaticSlots;
import fr.openmc.core.features.cosmetics.CosmeticManager;
import fr.openmc.core.features.cosmetics.CosmeticPointManager;
import io.papermc.paper.adventure.PaperAdventure;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.checkerframework.checker.units.qual.C;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class CosmeticMenu extends PaginatedMenu {

    private static final ItemStack CANCEL_ITEM;
    private static final ItemStack PREVIOUS_PAGE_ITEM;
    private static final ItemStack NEXT_PAGE_ITEM;

    static {
        ItemStack cancelItem = CustomStack.getInstance("_iainternal:icon_cancel").getItemStack().clone();
        cancelItem.editMeta(meta -> meta.displayName(Component.text("Fermer", NamedTextColor.RED).decoration(TextDecoration.ITALIC, false)));
        CANCEL_ITEM = cancelItem;
        ItemStack previousPageItem = CustomStack.getInstance("omc_quests:quests_left_arrow").getItemStack().clone();
        previousPageItem.editMeta(meta -> meta.displayName(Component.text("Page précédente").decoration(TextDecoration.ITALIC, false)));
        PREVIOUS_PAGE_ITEM = previousPageItem;
        ItemStack nextPageItem = CustomStack.getInstance("omc_quests:quests_right_arrow").getItemStack().clone();
        nextPageItem.editMeta(meta -> meta.displayName(Component.text("Page suivante").decoration(TextDecoration.ITALIC, false)));
        NEXT_PAGE_ITEM = nextPageItem;
    }

    private final Player player;

    public CosmeticMenu(Player owner) {
        super(owner);
        this.player = owner;
    }

    @Override
    public @NotNull Material getBorderMaterial() {
        return Material.GRAY_STAINED_GLASS_PANE;
    }

    @Override
    public @NotNull List<Integer> getStaticSlots() {
        return StaticSlots.BOTTOM;
    }

    @Override
    public @NotNull List<ItemStack> getItems() {
        List<ItemStack> items = new ArrayList<>();
        CosmeticManager.getCosmetics().forEach(cosmetic -> {
            ItemStack item = new ItemStack(cosmetic.getItem());
            if (CosmeticManager.ownsCosmetic(player.getUniqueId(), cosmetic)) {
                item.editMeta(meta -> {
                    meta.displayName(Component.text(cosmetic.getName(), NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false));
                    meta.lore(List.of(Component.text("Cliquez pour équipper", NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false)));
                });
            } else {
                item.editMeta(meta -> {
                    ((LeatherArmorMeta) meta).setColor(Color.fromRGB(45, 45, 45));
                    meta.displayName(Component.text(cosmetic.getName(), NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false));
                    meta.lore(List.of(Component.text("Prix: " + cosmetic.getPrice() + " points cosmétiques", NamedTextColor.YELLOW).decoration(TextDecoration.ITALIC, false),
                            Component.text("Cliquez pour acheter", NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false)));
                });
            }
            items.add(item);
        });
        return items;
    }

    @Override
    public Map<Integer, ItemStack> getButtons() {
        Map<Integer, ItemStack> map = new HashMap<>();
        map.put(49, new ItemBuilder(this, CANCEL_ITEM)
                .setCloseButton());
        map.put(48, new ItemBuilder(this, PREVIOUS_PAGE_ITEM)
                .setPreviousPageButton());
        map.put(50, new ItemBuilder(this, NEXT_PAGE_ITEM)
                .setNextPageButton());
        return map;
    }

    @Override
    public @NotNull String getName() {
        return "Page " + (getPage() + 1) + " — Points : " + CosmeticPointManager.getCosmeticPoint(player.getUniqueId());
    }

    @Override
    public void onInventoryClick(InventoryClickEvent inventoryClickEvent) {
        ItemStack item = inventoryClickEvent.getCurrentItem();
        if (item != null) {
            ItemMeta itemMeta = item.getItemMeta();
            String name = PaperAdventure.asPlain(itemMeta.displayName(), Locale.FRENCH);
            CosmeticManager.getCosmetics().forEach(cosmetic -> {
                if (name.equals(cosmetic.getName())) {
                    if (CosmeticManager.ownsCosmetic(player.getUniqueId(), cosmetic)) {
                        CosmeticManager.getActivatedCosmetics().put(player.getUniqueId(), cosmetic);
                        CosmeticManager.getBodyCosmeticsManager().spawnPlayerBodyCosmetic(((CraftPlayer) player).getHandle(), cosmetic);
                        player.sendMessage(Component.text("Vous avez sélectionné la cosmétique " + cosmetic.getName() + ".", NamedTextColor.GREEN));
                        player.closeInventory();
                    } else {
                        if (CosmeticPointManager.hasEnoughCosmeticPoint(cosmetic.getPrice(), player.getUniqueId())) {
                            new ConfirmMenu(player,
                                    () -> {
                                        CosmeticPointManager.removeCosmeticPoint(cosmetic.getPrice(), player.getUniqueId());
                                        CosmeticManager.addOwnedCosmetic(player.getUniqueId(), cosmetic);
                                        player.sendMessage(Component.text("Vous avez acheté la cosmétique " + cosmetic.getName() + " pour " + cosmetic.getPrice() + " Points Cosmétique.", NamedTextColor.GREEN));
                                        player.closeInventory();
                                    },
                                    () -> {
                                        player.sendMessage(Component.text("Achat de la cosmétique annulé.", NamedTextColor.YELLOW));
                                        player.closeInventory();
                                    },
                                    List.of(Component.text("Acheter cette cosmétique pour " + cosmetic.getPrice() + " Points Cosmétiques")),
                                    List.of(Component.text("Annuler l'achat de cette cosmétique"))
                            ).open();

                        } else {
                            player.sendMessage(Component.text("Vous n'avez pas assez de points cosmétiques pour acheter cette cosmétique.", NamedTextColor.RED));
                            player.closeInventory();
                        }
                    }
                }
            });

        }
    }

    @Override
    public void onClose(InventoryCloseEvent event) {

    }

    @Override
    public List<Integer> getTakableSlot() {
        return List.of();
    }

}
