package fr.openmc.core.registry.items;

import dev.lone.itemsadder.api.CustomStack;
import fr.openmc.core.CommandsManager;
import fr.openmc.core.bootstrap.registries.Registry;
import fr.openmc.core.hooks.itemsadder.ItemsAdderHook;
import fr.openmc.core.registry.items.contents.AywenCap;
import fr.openmc.core.registry.items.contents.Hammer;
import io.papermc.paper.persistence.PersistentDataContainerView;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public class CustomItemRegistry extends Registry<String, CustomItem> {

    public static final NamespacedKey CUSTOM_ITEM_KEY =
            new NamespacedKey("openmc", "custom_item");

    @Override
    public void postInit() {
        CommandsManager.getHandler().register(new CustomItemsDebugCommand());

        // ** REGISTRER ITEMSTACK OF ITEM ADDER **

        /* Buttons */
        register("_iainternal:icon_cancel", Material.DARK_OAK_DOOR, "Fermer");
        register("_iainternal:icon_back_orange", Material.ARROW, "Page précédente");
        register("_iainternal:icon_next_orange", Material.ARROW, "Page suivante");
        register("_iainternal:icon_search", Material.SPYGLASS, "Rechercher");
        register("omc_menus:accept_btn", Material.GREEN_CONCRETE, "Accepter");
        register("omc_menus:refuse_btn", Material.RED_CONCRETE, "Refuser");
        register("omc_quests:quests_right_arrow", Material.ARROW, "Suivant");
        register("omc_quests:quests_left_arrow", Material.ARROW, "Précédent");
        register("omc_menus:1_btn", Material.PAPER);
        register("omc_menus:10_btn", Material.PAPER);
        register("omc_menus:64_btn", Material.PAPER);
        register("omc_menus:minus_btn", Material.PAPER);
        register("omc_menus:plus_btn", Material.PAPER);
        register("omc_menus:mailbox_accept_btn", Material.PAPER);
        register("omc_menus:mailbox_refuse_btn", Material.PAPER);
        register("omc_menus:mailbox_cancel_btn", Material.PAPER);
        register("omc_menus:mailbox_arrow_left", Material.PAPER);
        register("omc_menus:mailbox_arrow_right", Material.PAPER);
        register("omc_menus:mailbox_send", Material.PAPER);
        register("omc_menus:mailbox_hourglass", Material.PAPER);

        /* Items */
        register("omc_contest:contest_shell", Material.NAUTILUS_SHELL);
        register("omc_items:aywenite", Material.AMETHYST_SHARD);
        register("omc_foods:kebab", Material.COOKED_BEEF);
        register("omc_foods:the_mixture", Material.HONEY_BOTTLE);
        register("omc_foods:courgette", Material.SEA_PICKLE);
        register("omc_items:mascot_stick", Material.STICK);
        register("omc_items:warp_stick", Material.STICK);
        register("omc_items:aywen_cap", Material.IRON_HELMET);
        register("omc_items:suit_helmet", Material.IRON_HELMET);
        register("omc_items:suit_chestplate", Material.IRON_CHESTPLATE);
        register("omc_items:suit_leggings", Material.IRON_LEGGINGS);
        register("omc_items:suit_boots", Material.IRON_BOOTS);
        register("omc_company:company_box", Material.CHEST);
        register("omc_homes:omc_homes_icon_bin_red", Material.CHEST);
        register("omc_homes:omc_homes_icon_bin", Material.CHEST);
        register("omc_homes:omc_homes_icon_information", Material.CHEST);
        register("omc_homes:omc_homes_icon_upgrade", Material.CHEST);

        /* Blocs */
        register("omc_blocks:aywenite_block", Material.AMETHYST_BLOCK);
        register("omc_plush:peluche_seinyy", Material.PAPER);
        register("omc_plush:peluche_awyen", Material.PAPER);
        register("omc_blocks:urne", Material.GLASS);

        /* Homes icons */
        register("omc_homes:omc_homes_icon_axenq", Material.CHEST);
        register("omc_homes:omc_homes_icon_bank", Material.CHEST);
        register("omc_homes:omc_homes_icon_chateau", Material.CHEST);
        register("omc_homes:omc_homes_icon_chest", Material.CHEST);
        register("omc_homes:omc_homes_icon_grass", Material.CHEST);
        register("omc_homes:omc_homes_icon_maison", Material.CHEST);
        register("omc_homes:omc_homes_icon_sandblock", Material.CHEST);
        register("omc_homes:omc_homes_icon_shop", Material.CHEST);
        register("omc_homes:omc_homes_icon_xernas", Material.CHEST);

        /* Equipable */
        register(new AywenCap("omc_items:aywen_cap"));

        /* Hammer */
        register(new Hammer("omc_items:iron_hammer", Material.IRON_PICKAXE, 1, 0));
        register(new Hammer("omc_items:diamond_hammer", Material.DIAMOND_PICKAXE, 1, 1));
        register(new Hammer("omc_items:netherite_hammer", Material.NETHERITE_PICKAXE, 1, 2));
    }

    public CustomItem get(String id) {
        if (super.get(id) != null) return super.get(id);

        return values().stream()
                .filter(item -> item.getId().split(":")[1].equals(id))
                .findFirst()
                .orElse(null);
    }

    public CustomItem get(ItemStack stack) {
        if (stack == null) return null;
        PersistentDataContainerView view = stack.getPersistentDataContainer();
        String id = view.get(CUSTOM_ITEM_KEY, PersistentDataType.STRING);

        if (id == null && ItemsAdderHook.isEnable()) {
            CustomStack itemIa = CustomStack.byItemStack(stack);

            if (itemIa == null) return null;

            return this.get(itemIa.getNamespacedID());
        } else {
            return this.get(id);
        }
    }

    public void register(CustomItem item) {
        register(item.getId(), item);
    }

    public void register(String name, ItemStack item) {
        register(name, new CustomItem(name) {
            @Override
            public ItemStack getVanilla() {
                return item;
            }
        });
    }

    public void register(String name, Material item) {
        register(name, new ItemStack(item));
    }

    public void register(String name, Material material, String displayName) {
        register(new CustomItem(name) {
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

    public void register(Iterable<CustomItem> items) {
        for (CustomItem item : items) {
            register(item);
        }
    }
}
