package fr.openmc.core.features.adminshop.shopinterfaces;

import dev.xernas.menulib.Menu;
import dev.xernas.menulib.utils.InventorySize;
import dev.xernas.menulib.utils.ItemBuilder;
import fr.openmc.core.features.adminshop.menu.buy.AdminShopBuy;
import fr.openmc.core.features.adminshop.menu.category.colored.AdminColoredShop;
import fr.openmc.core.features.adminshop.menu.category.colored.BlockType;
import fr.openmc.core.features.adminshop.menu.sell.AdminShopSell;
import fr.openmc.core.utils.customitems.CustomItemRegistry;
import lombok.NonNull;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public abstract class BaseShop extends Menu {
    @NonNull
    private final String name;
    private final BaseItems[] items;

    public BaseShop(Player player, String name, BaseItems[] items) {
        super(player);
        this.name = name;
        this.items = items;
    }

    // GetName (MenuLib)
    @Override
    public @NotNull String getName() {
        return name;
    }

    // InventorySize (MenuLib)
    @Override
    public @NotNull InventorySize getInventorySize() {
        return InventorySize.LARGEST;
    }

    // InventoryClickEvent (MenuLib)
    @Override
    public void onInventoryClick(InventoryClickEvent inventoryClickEvent) {}

    // GetContent (MenuLib)
    @Override
    public @NotNull Map<Integer, ItemStack> getContent() {
        Map<Integer, ItemStack> content = new HashMap<>();

        for(int i = 0; i < getInventorySize().getSize(); i++) {
            if((i % 2) == 0) content.put(i, new ItemBuilder(this, Material.LIGHT_BLUE_STAINED_GLASS_PANE, itemMeta -> itemMeta.setDisplayName(" ")));
            else content.put(i, new ItemBuilder(this, Material.BLUE_STAINED_GLASS_PANE, itemMeta -> itemMeta.setDisplayName(" ")));
        }

        for(BaseItems item : items) {
            content.put(item.getSlots(), new ItemBuilder(this, Objects.requireNonNull(Material.getMaterial(item.named())), itemMeta ->  {
                        itemMeta.setDisplayName(item.getName());

                        itemMeta.setLore(getStrings(item));

                    }).setOnClick(event -> getClicks(event, item, getOwner(),null))
            );
        }

        content.put(45, new ItemBuilder(this, CustomItemRegistry.getByName("menu:previous_page").getBest(), ItemMeta -> {
        }).setBackButton());
        content.put(53, new ItemBuilder(this, CustomItemRegistry.getByName("menu:close_button").getBest(), ItemMeta -> {
        }).setCloseButton());

        return content;
    }

    // Click events
    public static void getClicks(InventoryClickEvent event, BaseItems item, Player player, String material) {

        AdminShopBuy buy = new AdminShopBuy(player, item, material);
        AdminShopSell sell = new AdminShopSell(player, item, material);

        ClickType click = event.getClick();

        boolean isRightClick = click.isRightClick();
        boolean isLeftClick = click.isLeftClick();

        switch (item.getType()) {
            case SELL_BUY -> {
                if(isLeftClick) buy.open();
                else if(isRightClick) {
                    if(getTotalItemsInInventory(item,material, player) == 0) {
                        player.closeInventory();
                        player.sendMessage("§cVous n'avez pas d'item à vendre.");
                        return;
                    }
                    sell.open();
                }
            }
            case BUY -> buy.open();
            case SELL -> {
                if(getTotalItemsInInventory(item,material, player) == 0) {
                    player.closeInventory();
                    player.sendMessage("§cVous n'avez pas d'item à vendre.");
                    return;
                }
                sell.open();
            }
            case WHITE_CONCRETE_POWDER -> new AdminColoredShop(player, BlockType.CONCRETE_POWDER).open();
            case WHITE_CONCRETE -> new AdminColoredShop(player, BlockType.CONCRETE).open();
            case WHITE_WOOL -> new AdminColoredShop(player, BlockType.WOOL).open();
            case TERRACOTTA -> {
                if(isRightClick) new AdminColoredShop(player, BlockType.TERRACOTTA).open();
                else if(isLeftClick) buy.open();
            }
            case GLASS -> {
                if(isRightClick) new AdminColoredShop(player, BlockType.GLASS).open();
                else buy.open();
            }
            case GLASS_PANE -> {
                if(isRightClick) new AdminColoredShop(player, BlockType.GLASS_PANE).open();
                else buy.open();
            }
            case LOG -> {
                if(isRightClick) new AdminColoredShop(player, BlockType.LOG).open();
                else buy.open();
            }
            case DYE -> {
                if(isRightClick) new AdminColoredShop(player, BlockType.DYE).open();
                else sell.open();
            }
            case LEAVES -> {
                if(isRightClick) new AdminColoredShop(player, BlockType.LEAVES).open();
                else sell.open();
            }
        }
    }

    // Lore
    @NotNull
    public static List<String> getStrings(BaseItems item) {
        List<String> lore = new ArrayList<>();

        // Lore
        switch (item.getType()) {
            case BUY -> {
                lore.add("§aAcheter pour §e" + item.getBuyPrize() + "$ §7/u");
                lore.add("§7");
                lore.add("§8■ §7Click gauche pour §aacheter");
            }
            case SELL -> {
                lore.add("§cVendre pour §e" + item.getSellPrize() + "$ §7/u");
                lore.add("§7");
                lore.add("§8■ §7Click droit pour §cvendre");
            }
            case SELL_BUY -> {
                lore.add("§aAcheter pour §e" + item.getBuyPrize() + "$ §7/u");
                lore.add("§cVendre pour §e" + item.getSellPrize() + "$ §7/u");
                lore.add("§7");
                lore.add("§8■ §7Click gauche pour §aacheter");
                lore.add("§8■ §7Click droit pour §cvendre");
            }
            case LOG -> {
                lore.add("§aAcheter pour §e" + item.getBuyPrize() + "$ §7/u");
                lore.add("§7");
                lore.add("§8■ §7Click droit pour plus de variantes.");
                lore.add("§8■ §7Click gauche pour §aacheter.");
            }
            case GLASS_PANE, GLASS, TERRACOTTA, DYE -> {
                lore.add("§aAcheter pour §e" + item.getBuyPrize() + "$ §7/u");
                lore.add("§7");
                lore.add("§8■ §7Click droit pour plus de couleur.");
                lore.add("§8■ §7Click gauche pour §aacheter.");
            }
            case WHITE_CONCRETE, WHITE_CONCRETE_POWDER, WHITE_WOOL -> {
                lore.add("§8■ §7Click droit pour plus de couleur.");
            }
        }
        return lore;
    }

    public static int getTotalItemsInInventory(BaseItems items,String material, Player player) {
        ItemStack[] contents = player.getInventory().getContents();
        int total = 0;

        for (ItemStack stack : contents) {
            if (stack != null && stack.getType() == Objects.requireNonNull(Material.getMaterial(material == null ? items.named() : items.named() + "_" + material))) {
                total += stack.getAmount();
            }
        }

        return total;
    }

}
