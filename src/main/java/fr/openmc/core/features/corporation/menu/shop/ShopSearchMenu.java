package fr.openmc.core.features.corporation.menu.shop;

import fr.openmc.api.input.DialogInput;
import fr.openmc.api.menulib.PaginatedMenu;
import fr.openmc.api.menulib.utils.InventorySize;
import fr.openmc.api.menulib.utils.ItemBuilder;
import fr.openmc.api.menulib.utils.StaticSlots;
import fr.openmc.core.features.corporation.company.Company;
import fr.openmc.core.features.corporation.manager.CompanyManager;
import fr.openmc.core.features.corporation.manager.ShopBlocksManager;
import fr.openmc.core.features.corporation.shops.Shop;
import fr.openmc.core.items.CustomItemRegistry;
import fr.openmc.core.utils.ItemUtils;
import fr.openmc.core.utils.api.ItemsAdderApi;
import fr.openmc.core.utils.api.PapiApi;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
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

import static fr.openmc.core.utils.InputUtils.MAX_LENGTH;

public class ShopSearchMenu extends PaginatedMenu {

    public ShopSearchMenu(Player owner) {
        super(owner);
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
        return null;
    }

    @Override
    public @NotNull List<Integer> getStaticSlots() {
        return StaticSlots.getStandardSlots(getInventorySize());
    }

    @Override
    public @NotNull List<ItemStack> getItems() {
        List<ItemStack> items = new java.util.ArrayList<>();

        for (Shop shops : CompanyManager.shops){

            if (shops==null){continue;}

            List<Component> loc = new ArrayList<>();
            double x = ShopBlocksManager.getMultiblock(shops.getUuid()).getStockBlock().getBlockX();
            double y = ShopBlocksManager.getMultiblock(shops.getUuid()).getStockBlock().getBlockY();
            double z = ShopBlocksManager.getMultiblock(shops.getUuid()).getStockBlock().getBlockZ();

            loc.add(Component.text("§lLocation : §r x : " + x + " y : " + y + " z : " + z));

            items.add(new ItemBuilder(this, ItemUtils.getPlayerHead(getOwner().getUniqueId()) ,itemMeta -> {
                itemMeta.setDisplayName("§lshop :§r" + shops.getName());
                itemMeta.lore(loc);
            }));
        }

        return items;
    }

    @Override
    public Map<Integer, ItemStack> getButtons() {
        Map<Integer, ItemStack> buttons = new HashMap<>();
        buttons.put(49, new ItemBuilder(this, CustomItemRegistry.getByName("_iainternal:icon_cancel").getBest(), itemMeta -> itemMeta.setDisplayName("§7Fermer"))
                .setCloseButton());
        ItemBuilder nextPageButton = new ItemBuilder(this, CustomItemRegistry.getByName("_iainternal:icon_next_orange").getBest(), itemMeta -> itemMeta.setDisplayName("§aPage suivante"));
        ItemBuilder searchButton = new ItemBuilder(this, CustomItemRegistry.getByName("_iainternal:icon_search").getBest().getType(), itemMeta ->
                itemMeta.setDisplayName("Rechercher"));
        if ((getPage() != 0 && !isLastPage()) || !CompanyManager.getShops().isEmpty()) {
            buttons.put(48, new ItemBuilder(this, CustomItemRegistry.getByName("_iainternal:icon_back_orange").getBest(), itemMeta -> itemMeta.setDisplayName("§cPage précédente"))
                    .setPreviousPageButton());
            buttons.put(50, nextPageButton.setNextPageButton());
            buttons.put(45, searchButton.setOnClick(inventoryClick -> {
                DialogInput.send(getOwner(), Component.text("Entrez le nom du shop ou bien du joueur pour le rechercher"), MAX_LENGTH, input -> {
                    boolean shopFind = false;

                    for (Shop shop : CompanyManager.shops) {
                        double x = ShopBlocksManager.getMultiblock(shop.getUuid()).getStockBlock().getBlockX();
                        double y = ShopBlocksManager.getMultiblock(shop.getUuid()).getStockBlock().getBlockY();
                        double z = ShopBlocksManager.getMultiblock(shop.getUuid()).getStockBlock().getBlockZ();

                        if (shop.getName().contains(input)) {
                            MessagesManager.sendMessage(getOwner(), Component.text("§lLocation du shop §a" + shop.getName() + " : §r x : " + x + " y : " + y + " z : " + z), Prefix.SHOP, MessageType.INFO, false);
                            shopFind = true;
                            break;
                        }
                        Player player = Bukkit.getPlayer(input);
                        if (player == null) continue;
                        if (shop.getOwner().isCompany()) {
                            Company company = shop.getOwner().getCompany();
                            if (company.getAllMembers().contains(player.getUniqueId())) {
                                MessagesManager.sendMessage(getOwner(), Component.text("§lLocation du shop §a" + shop.getName() + " : §r x : " + x + " y : " + y + " z : " + z), Prefix.SHOP, MessageType.INFO, false);
                                shopFind = true;
                                break;
                            }
                        }
                        if (shop.getOwner().isPlayer()) {
                            Player shopPlayer = Bukkit.getPlayer(shop.getOwner().getPlayer());
                            if (shopPlayer == null) {
                                continue;
                            }
                            if (shopPlayer.equals(player)) {
                                MessagesManager.sendMessage(getOwner(), Component.text("§lLocation du shop §a" + shop.getName() + " : §r x : " + x + " y : " + y + " z : " + z), Prefix.SHOP, MessageType.INFO, false);
                                shopFind = true;
                                break;
                            }
                        }
                    }

                    if (!shopFind) {
                        MessagesManager.sendMessage(getOwner(), Component.text("§cAucun shop trouvé !"), Prefix.SHOP, MessageType.INFO, false);
                    }

                });
            }));
        }
        return buttons;
    }

    @Override
    public @NotNull String getName() {
        if (PapiApi.hasPAPI() && ItemsAdderApi.hasItemAdder()) {
            return PlaceholderAPI.setPlaceholders(getOwner(), "§r§f%img_offset_-11%%img_large_shop_menu%");
        } else {
            return "§l§6search";
        }
    }

    @Override
    public void onInventoryClick(InventoryClickEvent inventoryClickEvent) {

    }

    @Override
    public void onClose(InventoryCloseEvent event) {

    }

    @Override
    public List<Integer> getTakableSlot() {
        return List.of();
    }
}
