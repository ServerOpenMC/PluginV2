package fr.openmc.core.features.adminshop.shopinterfaces;

import dev.xernas.menulib.Menu;
import dev.xernas.menulib.MenuLib;
import dev.xernas.menulib.utils.InventorySize;
import dev.xernas.menulib.utils.ItemBuilder;
import fr.openmc.core.features.adminshop.menu.category.colored.COLOR;
import fr.openmc.core.features.adminshop.menu.category.colored.LEAVES;
import fr.openmc.core.features.adminshop.menu.category.colored.LOGTYPES;
import fr.openmc.core.utils.customitems.CustomItemRegistry;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class ColoredShop extends Menu {

    private final String name;
    private final String materialType;
    private final String materialName;

    public ColoredShop(Player player, String name, String materialType, String materialName) {
        super(player);
        this.name = name;
        this.materialType = materialType.toUpperCase();
        this.materialName = materialName;
    }

    @Override
    public @NotNull String getName() {
        return name;
    }
    @Override
    public @NotNull InventorySize getInventorySize() {
        return InventorySize.LARGEST;
    }
    @Override
    public void onInventoryClick(InventoryClickEvent inventoryClickEvent) {}

    @Override
    public @NotNull Map<Integer, ItemStack> getContent() {
        Map<Integer, ItemStack> content = new HashMap<>();

        for(int i = 0; i < getInventorySize().getSize(); i++) {
            if((i % 2) == 0) content.put(i, new ItemBuilder(this, Material.LIGHT_BLUE_STAINED_GLASS_PANE, itemMeta -> itemMeta.setDisplayName(" ")));
            else content.put(i, new ItemBuilder(this, Material.BLUE_STAINED_GLASS_PANE, itemMeta -> itemMeta.setDisplayName(" ")));
        }

        if (materialType.equalsIgnoreCase("log")){

            for (LOGTYPES item : LOGTYPES.values()) {

                if (Material.getMaterial(item.named() + "_" + materialType) == null){
                    content.put(item.getSlots(), new ItemBuilder(this, Objects.requireNonNull(Material.getMaterial(item.named() + "_STEM")), itemMeta ->  {
                                itemMeta.setDisplayName("§7" + item.getName());
                                itemMeta.setLore(BaseShop.getStrings(item));
                            }).setOnClick(event -> BaseShop.getClicks(event, item, getOwner(), "STEM"))
                    );
                    continue;
                }

                content.put(item.getSlots(), new ItemBuilder(this, Objects.requireNonNull(Material.getMaterial(item.named() + "_" + materialType)), itemMeta ->  {
                            itemMeta.setDisplayName("§7" + materialName + " " + item.getName());
                            itemMeta.setLore(BaseShop.getStrings(item));
                        }).setOnClick(event -> BaseShop.getClicks(event, item, getOwner(), materialType))
                );
            }
        } else if (materialType.equalsIgnoreCase("leaves")) {
            for (LEAVES item : LEAVES.values()){
                content.put(item.getSlots(), new ItemBuilder(this, Objects.requireNonNull(Material.getMaterial(item.named() + "_" + materialType)), itemMeta ->  {
                            itemMeta.setDisplayName("§7" + materialName + " " + item.getName());
                            itemMeta.setLore(BaseShop.getStrings(item));
                        }).setOnClick(event -> BaseShop.getClicks(event, item, getOwner(), materialType))
                );
            }
        } else {

            for(COLOR item : COLOR.values()) {

                content.put(item.getSlots(), new ItemBuilder(this, Objects.requireNonNull(Material.getMaterial(item.named() + "_" + materialType.toUpperCase())), itemMeta -> {
                            itemMeta.setDisplayName("§7" + materialName + " " + item.getName());
                            itemMeta.setLore(BaseShop.getStrings(item));
                        }).setOnClick(event -> BaseShop.getClicks(event, item, getOwner(), materialType))
                );
            }
        }

        content.put(45, new ItemBuilder(this, CustomItemRegistry.getByName("menu:previous_page").getBest(), ItemMeta -> {
        }).setNextMenu(MenuLib.getLastMenu(getOwner())));
        content.put(53, new ItemBuilder(this, CustomItemRegistry.getByName("menu:close_button").getBest(), ItemMeta -> {
        }).setCloseButton());

        return content;
    }
}
