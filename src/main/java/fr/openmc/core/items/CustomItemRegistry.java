package fr.openmc.core.items;

import fr.openmc.core.CommandsManager;
import io.papermc.paper.persistence.PersistentDataContainerView;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.HashSet;

public class CustomItemRegistry {
    static final HashMap<String, CustomItem> items = new HashMap<>();
    static final NamespacedKey customNameKey = new NamespacedKey("aywen", "custom_item");

    public static void init() {
        CommandsManager.getHandler().register(new CustomItemsDebugCommand());

        // ** REGISTRER ITEMSTACK OF ITEM ADDER **

        /* Buttons */
        registerSimpleItem("_iainternal:icon_cancel", Material.DARK_OAK_DOOR, "Fermer");
        registerSimpleItem("_iainternal:icon_back_orange", Material.ARROW, "Page précédente");
        registerSimpleItem("_iainternal:icon_next_orange", Material.ARROW, "Page suivante");
        registerSimpleItem("_iainternal:icon_search", Material.SPYGLASS, "Rechercher");
        registerSimpleItem("omc_menus:accept_btn", Material.GREEN_CONCRETE, "Accepter");
        registerSimpleItem("omc_menus:refuse_btn", Material.RED_CONCRETE, "Refuser");
        registerSimpleItem("omc_quests:quests_right_arrow", Material.ARROW, "Suivant");
        registerSimpleItem("omc_quests:quests_left_arrow", Material.ARROW, "Précédent");
        registerSimpleItem("omc_menus:1_btn", Material.PAPER);
        registerSimpleItem("omc_menus:10_btn", Material.PAPER);
        registerSimpleItem("omc_menus:64_btn", Material.PAPER);
        registerSimpleItem("omc_menus:minus_btn", Material.PAPER);
        registerSimpleItem("omc_menus:plus_btn", Material.PAPER);
        registerSimpleItem("omc_menus:mailbox_accept_btn", Material.PAPER);
        registerSimpleItem("omc_menus:mailbox_refuse_btn", Material.PAPER);
        registerSimpleItem("omc_menus:mailbox_cancel_btn", Material.PAPER);
        registerSimpleItem("omc_menus:mailbox_arrow_left", Material.PAPER);
        registerSimpleItem("omc_menus:mailbox_arrow_right", Material.PAPER);
        registerSimpleItem("omc_menus:mailbox_send", Material.PAPER);
        registerSimpleItem("omc_menus:mailbox_hourglass", Material.PAPER);

        /* Items */
        registerSimpleItem("omc_contest:contest_shell", Material.NAUTILUS_SHELL);
        registerSimpleItem("omc_items:aywenite", Material.AMETHYST_SHARD);
        registerSimpleItem("omc_foods:kebab", Material.COOKED_BEEF);
        registerSimpleItem("omc_foods:the_mixture", Material.HONEY_BOTTLE);
        registerSimpleItem("omc_foods:courgette", Material.SEA_PICKLE);
        registerSimpleItem("omc_items:mascot_stick", Material.STICK);
        registerSimpleItem("omc_items:warp_stick", Material.STICK);
        registerSimpleItem("omc_items:aywen_cap", Material.IRON_HELMET);
        registerSimpleItem("omc_items:suit_helmet", Material.IRON_HELMET);
        registerSimpleItem("omc_items:suit_chestplate", Material.IRON_CHESTPLATE);
        registerSimpleItem("omc_items:suit_leggings", Material.IRON_LEGGINGS);
        registerSimpleItem("omc_items:suit_boots", Material.IRON_BOOTS);
        registerSimpleItem("omc_company:company_box", Material.CHEST);
        registerSimpleItem("omc_homes:omc_homes_icon_bin_red", Material.CHEST);
        registerSimpleItem("omc_homes:omc_homes_icon_bin", Material.CHEST);
        registerSimpleItem("omc_homes:omc_homes_icon_information", Material.CHEST);
        registerSimpleItem("omc_homes:omc_homes_icon_upgrade", Material.CHEST);

        /* Blocs */
        registerSimpleItem("omc_blocks:aywenite_block", Material.AMETHYST_BLOCK);
        registerSimpleItem("omc_plush:peluche_seinyy", Material.PAPER);
        registerSimpleItem("omc_plush:peluche_awyen", Material.PAPER);
        registerSimpleItem("omc_blocks:urne", Material.GLASS);

        /* Homes icons */
        registerSimpleItem("omc_homes:omc_homes_icon_axenq", Material.CHEST);
        registerSimpleItem("omc_homes:omc_homes_icon_bank", Material.CHEST);
        registerSimpleItem("omc_homes:omc_homes_icon_chateau", Material.CHEST);
        registerSimpleItem("omc_homes:omc_homes_icon_chest", Material.CHEST);
        registerSimpleItem("omc_homes:omc_homes_icon_grass", Material.CHEST);
        registerSimpleItem("omc_homes:omc_homes_icon_maison", Material.CHEST);
        registerSimpleItem("omc_homes:omc_homes_icon_sandblock", Material.CHEST);
        registerSimpleItem("omc_homes:omc_homes_icon_shop", Material.CHEST);
        registerSimpleItem("omc_homes:omc_homes_icon_xernas", Material.CHEST);
    }

    public static void register(String name, CustomItem item) {
        if (!name.matches("[a-zA-Z0-9_:]+")) {
            throw new IllegalArgumentException("Custom item name dont match regex \"[a-zA-Z0-9_:]+\"");
        }

        items.put(name, item);
    }

    public static void registerItem(CustomItem item) {
        register(item.getName(), item);
    }

    public static void registerSimpleItem(String name, ItemStack item) {
        register(name, new CustomItem(name) {
            @Override
            public ItemStack getVanilla() {
                return item;
            }
        });
    }

    public static void registerSimpleItem(String name, Material item) {
        registerSimpleItem(name, new ItemStack(item));
    }

    public static void registerSimpleItem(String name, Material material, String displayName) {
        registerItem(new CustomItem(name) {
            @Override
            public ItemStack getVanilla() {
                ItemStack item = new ItemStack(material);
                ItemMeta meta = item.getItemMeta();
                meta.displayName(Component.text(displayName).decoration(TextDecoration.ITALIC, false));
                item.setItemMeta(meta);
                return item;
            }
        });
    }

    @Nullable
    public static CustomItem getByName(String name) {
        return items.get(name);
    }

    @Nullable
    public static CustomItem getByItemStack(ItemStack stack) {
        PersistentDataContainerView view = stack.getPersistentDataContainer();
        String name = view.get(customNameKey, PersistentDataType.STRING);

        return name == null ? null : getByName(name);
    }

    public static HashSet<String> getNames() {
        return new HashSet<>(items.keySet());
    }
}
