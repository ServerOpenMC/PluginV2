package fr.openmc.core.features.corporation.menu.shop;

import fr.openmc.api.input.signgui.SignGUI;
import fr.openmc.api.input.signgui.exception.SignGUIVersionException;
import fr.openmc.api.menulib.PaginatedMenu;
import fr.openmc.api.menulib.utils.ItemBuilder;
import fr.openmc.api.menulib.utils.StaticSlots;
import fr.openmc.core.features.corporation.company.Company;
import fr.openmc.core.features.corporation.manager.CompanyManager;
import fr.openmc.core.features.corporation.shops.Shop;
import fr.openmc.core.utils.ItemUtils;
import fr.openmc.core.utils.api.ItemAdderApi;
import fr.openmc.core.utils.api.PapiApi;
import fr.openmc.core.utils.customitems.CustomItemRegistry;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class ShopSearchMenu extends PaginatedMenu {

    public ShopSearchMenu(Player owner) {
        super(owner);
    }

    @Override
    public @Nullable Material getBorderMaterial() {
        return Material.BLUE_STAINED_GLASS_PANE;
    }

    @Override
    public @NotNull List<Integer> getStaticSlots() {
        return StaticSlots.STANDARD;
    }

    @Override
    public @NotNull List<ItemStack> getItems() {
        List<ItemStack> items = new java.util.ArrayList<>();

        for (Shop shops : CompanyManager.shops){

            if (shops==null){continue;}

            List<Component> loc = new ArrayList<>();
            double x = shops.getBlocksManager().getMultiblock(shops.getUuid()).getStockBlock().getBlockX();
            double y = shops.getBlocksManager().getMultiblock(shops.getUuid()).getStockBlock().getBlockY();
            double z = shops.getBlocksManager().getMultiblock(shops.getUuid()).getStockBlock().getBlockZ();

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
        buttons.put(49, new ItemBuilder(this, CustomItemRegistry.getByName("menu:close_button").getBest(), itemMeta -> itemMeta.setDisplayName("§7Fermer"))
                .setCloseButton());
        ItemBuilder nextPageButton = new ItemBuilder(this, CustomItemRegistry.getByName("menu:next_page").getBest(), itemMeta -> itemMeta.setDisplayName("§aPage suivante"));
        ItemBuilder searchButton = new ItemBuilder(this, CustomItemRegistry.getByName("menu:search_btn").getBest().getType(), itemMeta ->
                itemMeta.setDisplayName("Rechercher"));
        if ((getPage() != 0 && !isLastPage()) || !CompanyManager.getShops().isEmpty()) {
            buttons.put(48, new ItemBuilder(this, CustomItemRegistry.getByName("menu:previous_page").getBest(), itemMeta -> itemMeta.setDisplayName("§cPage précédente"))
                    .setPreviousPageButton());
            buttons.put(50, nextPageButton.setNextPageButton());
            buttons.put(45, searchButton.setOnClick(inventoryClick -> {
                String[] lines = new String[4];
                lines[0] = "";
                lines[1] = " ᐱᐱᐱᐱᐱᐱᐱ ";
                lines[2] = "Entrez le nom";
                lines[3] = "du shop/joueur";

                SignGUI gui = null;
                try {
                    gui = SignGUI.builder()
                            .setLines(null, lines[1] , lines[2], lines[3])
                            .setType(ItemUtils.getSignType(getOwner()))
                            .setHandler((p, result) -> {
                                String input = result.getLine(0);

                                boolean shopFind = false;

                                for (Shop shop : CompanyManager.shops){
                                    double x = shop.getBlocksManager().getMultiblock(shop.getUuid()).getStockBlock().getBlockX();
                                    double y = shop.getBlocksManager().getMultiblock(shop.getUuid()).getStockBlock().getBlockY();
                                    double z = shop.getBlocksManager().getMultiblock(shop.getUuid()).getStockBlock().getBlockZ();

                                    if (shop.getName().contains(input)){
                                        MessagesManager.sendMessage(getOwner(), Component.text("§lLocation du shop §a"+ shop.getName() + " : §r x : " + x + " y : " + y + " z : " + z), Prefix.SHOP, MessageType.INFO, false);
                                        shopFind = true;
                                        break;
                                    }
                                    Player player = Bukkit.getPlayer(input);
                                    if (player==null) continue;
                                    if (shop.getOwner().isCompany()){
                                        Company company = shop.getOwner().getCompany();
                                        if (company.getAllMembers().contains(player.getUniqueId())){
                                            MessagesManager.sendMessage(getOwner(), Component.text("§lLocation du shop §a"+ shop.getName() + " : §r x : " + x + " y : " + y + " z : " + z), Prefix.SHOP, MessageType.INFO, false);
                                            shopFind = true;
                                            break;
                                        }
                                    }
                                    if (shop.getOwner().isPlayer()){
                                        Player shopPlayer = Bukkit.getPlayer(shop.getOwner().getPlayer());
                                        if (shopPlayer==null){
                                            continue;
                                        }
                                        if (shopPlayer.equals(player)){
                                            MessagesManager.sendMessage(getOwner(), Component.text("§lLocation du shop §a"+ shop.getName() + " : §r x : " + x + " y : " + y + " z : " + z), Prefix.SHOP, MessageType.INFO, false);
                                            shopFind = true;
                                            break;
                                        }
                                    }
                                }

                                if (!shopFind){
                                    MessagesManager.sendMessage(getOwner(), Component.text("§cAucun shop trouvé !"), Prefix.SHOP, MessageType.INFO, false);
                                }

                                return Collections.emptyList();
                            })
                            .build();
                } catch (SignGUIVersionException e) {
                    throw new RuntimeException(e);
                }

                gui.open(getOwner());
            }));
        }
        return buttons;
    }

    @Override
    public @NotNull String getName() {
        if (PapiApi.hasPAPI() && ItemAdderApi.hasItemAdder()) {
            return PlaceholderAPI.setPlaceholders(getOwner(), "§r§f%img_offset_-11%%img_large_shop_menu%");
        } else {
            return "§l§6search";
        }
    }

    @Override
    public void onInventoryClick(InventoryClickEvent inventoryClickEvent) {

    }
}
