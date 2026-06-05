package fr.openmc.core.registry.items;

import dev.lone.itemsadder.api.CustomStack;
import fr.openmc.core.CommandsManager;
import fr.openmc.core.bootstrap.registries.KeyedRegistry;
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

import java.util.Optional;

public class CustomItemRegistry extends Registry<String, CustomItem> implements KeyedRegistry<String, CustomItem> {

    public static final NamespacedKey CUSTOM_ITEM_KEY =
            new NamespacedKey("openmc", "custom_item");

    /* Buttons */
    public final CustomItem ICON_CANCEL = register("_iainternal:icon_cancel", Material.DARK_OAK_DOOR, "Fermer");
    public final CustomItem ICON_BACK_ORANGE = register("_iainternal:icon_back_orange", Material.ARROW, "Page précédente");
    public final CustomItem ICON_NEXT_ORANGE = register("_iainternal:icon_next_orange", Material.ARROW, "Page suivante");
    public final CustomItem ICON_SEARCH = register("_iainternal:icon_search", Material.SPYGLASS, "Rechercher");
    public final CustomItem ACCEPT_BTN = register("omc_menus:accept_btn", Material.GREEN_CONCRETE, "Accepter");
    public final CustomItem REFUSE_BTN = register("omc_menus:refuse_btn", Material.RED_CONCRETE, "Refuser");
    public final CustomItem QUESTS_RIGHT_ARROW = register("omc_quests:quests_right_arrow", Material.ARROW, "Suivant");
    public final CustomItem QUESTS_LEFT_ARROW = register("omc_quests:quests_left_arrow", Material.ARROW, "Précédent");
    public final CustomItem BTN_1 = register("omc_menus:1_btn", Material.PAPER);
    public final CustomItem BTN_10 = register("omc_menus:10_btn", Material.PAPER);
    public final CustomItem BTN_64 = register("omc_menus:64_btn", Material.PAPER);
    public final CustomItem MINUS_BTN = register("omc_menus:minus_btn", Material.PAPER);
    public final CustomItem PLUS_BTN = register("omc_menus:plus_btn", Material.PAPER);
    public final CustomItem MAILBOX_ACCEPT_BTN = register("omc_menus:mailbox_accept_btn", Material.PAPER);
    public final CustomItem MAILBOX_REFUSE_BTN = register("omc_menus:mailbox_refuse_btn", Material.PAPER);
    public final CustomItem MAILBOX_CANCEL_BTN = register("omc_menus:mailbox_cancel_btn", Material.PAPER);
    public final CustomItem MAILBOX_ARROW_LEFT = register("omc_menus:mailbox_arrow_left", Material.PAPER);
    public final CustomItem MAILBOX_ARROW_RIGHT = register("omc_menus:mailbox_arrow_right", Material.PAPER);
    public final CustomItem MAILBOX_SEND = register("omc_menus:mailbox_send", Material.PAPER);
    public final CustomItem MAILBOX_HOURGLASS = register("omc_menus:mailbox_hourglass", Material.PAPER);

    /* Items */
    public final CustomItem CONTEST_SHELL = register("omc_contest:contest_shell", Material.NAUTILUS_SHELL);
    public final CustomItem AYWENITE = register("omc_items:aywenite", Material.AMETHYST_SHARD);
    public final CustomItem KEBAB = register("omc_foods:kebab", Material.COOKED_BEEF);
    public final CustomItem THE_MIXTURE = register("omc_foods:the_mixture", Material.HONEY_BOTTLE);
    public final CustomItem COURGETTE = register("omc_foods:courgette", Material.SEA_PICKLE);
    public final CustomItem MASCOT_STICK = register("omc_items:mascot_stick", Material.STICK);
    public final CustomItem WARP_STICK = register("omc_items:warp_stick", Material.STICK);
    public final CustomItem AYWEN_CAP = register(new AywenCap("omc_items:aywen_cap"));
    public final CustomItem SUIT_HELMET = register("omc_items:suit_helmet", Material.IRON_HELMET);
    public final CustomItem SUIT_CHESTPLATE = register("omc_items:suit_chestplate", Material.IRON_CHESTPLATE);
    public final CustomItem SUIT_LEGGINGS = register("omc_items:suit_leggings", Material.IRON_LEGGINGS);
    public final CustomItem SUIT_BOOTS = register("omc_items:suit_boots", Material.IRON_BOOTS);
    public final CustomItem COMPANY_BOX = register("omc_company:company_box", Material.CHEST);
    public final CustomItem HOMES_ICON_BIN_RED = register("omc_homes:omc_homes_icon_bin_red", Material.CHEST);
    public final CustomItem HOMES_ICON_BIN = register("omc_homes:omc_homes_icon_bin", Material.CHEST);
    public final CustomItem HOMES_ICON_INFORMATION = register("omc_homes:omc_homes_icon_information", Material.CHEST);
    public final CustomItem HOMES_ICON_UPGRADE = register("omc_homes:omc_homes_icon_upgrade", Material.CHEST);

    /* Blocs */
    public final CustomItem AYWENITE_BLOCK = register("omc_blocks:aywenite_block", Material.AMETHYST_BLOCK);
    public final CustomItem PELUCHE_SEINYY = register("omc_plush:peluche_seinyy", Material.PAPER);
    public final CustomItem PELUCHE_AWYEN = register("omc_plush:peluche_awyen", Material.PAPER);
    public final CustomItem URNE = register("omc_blocks:urne", Material.GLASS);

    /* Homes icons */
    public final CustomItem HOMES_ICON_AXENQ = register("omc_homes:omc_homes_icon_axenq", Material.CHEST);
    public final CustomItem HOMES_ICON_BANK = register("omc_homes:omc_homes_icon_bank", Material.CHEST);
    public final CustomItem HOMES_ICON_CHATEAU = register("omc_homes:omc_homes_icon_chateau", Material.CHEST);
    public final CustomItem HOMES_ICON_CHEST = register("omc_homes:omc_homes_icon_chest", Material.CHEST);
    public final CustomItem HOMES_ICON_GRASS = register("omc_homes:omc_homes_icon_grass", Material.CHEST);
    public final CustomItem HOMES_ICON_MAISON = register("omc_homes:omc_homes_icon_maison", Material.CHEST);
    public final CustomItem HOMES_ICON_SANDBLOCK = register("omc_homes:omc_homes_icon_sandblock", Material.CHEST);
    public final CustomItem HOMES_ICON_SHOP = register("omc_homes:omc_homes_icon_shop", Material.CHEST);
    public final CustomItem HOMES_ICON_XERNAS = register("omc_homes:omc_homes_icon_xernas", Material.CHEST);

    /* Hammer */
    public final CustomItem IRON_HAMMER = register(new Hammer("omc_items:iron_hammer", Material.IRON_PICKAXE, 1, 0));
    public final CustomItem DIAMOND_HAMMER = register(new Hammer("omc_items:diamond_hammer", Material.DIAMOND_PICKAXE, 1, 1));
    public final CustomItem NETHERITE_HAMMER = register(new Hammer("omc_items:netherite_hammer", Material.NETHERITE_PICKAXE, 1, 2));

    @Override
    public void postInit() {
        CommandsManager.getHandler().register(new CustomItemsDebugCommand());
    }

    @Override
    public String key(CustomItem registryObject) {
        return registryObject.getId();
    }

    @Override
    public Optional<CustomItem> get(String id) {
        if (super.get(id).isPresent()) return super.get(id);

        return values().stream()
                .filter(item -> item.getId().split(":")[1].equals(id))
                .findFirst();
    }

    public Optional<CustomItem> get(ItemStack stack) {
        if (stack == null) return Optional.empty();

        PersistentDataContainerView view = stack.getPersistentDataContainer();
        String id = view.get(CUSTOM_ITEM_KEY, PersistentDataType.STRING);

        if (id == null && ItemsAdderHook.isEnable()) {
            CustomStack itemIa = CustomStack.byItemStack(stack);

            if (itemIa == null) return Optional.empty();

            return this.get(itemIa.getNamespacedID());
        } else {
            return this.get(id);
        }
    }

    public CustomItem getOrThrow(ItemStack stack) {
        if (stack == null) throw new IllegalArgumentException("ItemStack cannot be null");

        PersistentDataContainerView view = stack.getPersistentDataContainer();
        String id = view.get(CUSTOM_ITEM_KEY, PersistentDataType.STRING);

        if (id == null && ItemsAdderHook.isEnable()) {
            CustomStack itemIa = CustomStack.byItemStack(stack);

            return this.getOrThrow(itemIa.getNamespacedID());
        } else {
            return this.getOrThrow(id);
        }
    }

    public CustomItem register(String name, ItemStack item) {
        return register(name, new CustomItem(name) {
            @Override
            public ItemStack getVanilla() {
                return item;
            }
        });
    }

    public CustomItem register(String name, Material item) {
        return register(name, new ItemStack(item));
    }

    public CustomItem register(String name, Material material, String displayName) {
        return register(new CustomItem(name) {
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
}
