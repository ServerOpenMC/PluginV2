package fr.openmc.core.registry.items;

import dev.lone.itemsadder.api.CustomBlock;
import dev.lone.itemsadder.api.CustomStack;
import fr.openmc.core.CommandsManager;
import fr.openmc.core.bootstrap.features.types.HasListeners;
import fr.openmc.core.bootstrap.registries.KeyedRegistry;
import fr.openmc.core.bootstrap.registries.Registry;
import fr.openmc.core.features.bits.contents.items.KitchenBox;
import fr.openmc.core.features.bits.contents.items.MedievalBox;
import fr.openmc.core.features.bits.contents.items.ModernBox;
import fr.openmc.core.features.bits.contents.items.OfficeBox;
import fr.openmc.core.features.events.contents.dailyevents.contents.miraculousfishing.contents.items.EpicFishingTreasureLootbox;
import fr.openmc.core.features.events.contents.dailyevents.contents.miraculousfishing.contents.items.FishingFurnitureLootbox;
import fr.openmc.core.features.events.contents.dailyevents.contents.miraculousfishing.contents.items.LegendaryFishingTreasureLootbox;
import fr.openmc.core.features.events.contents.dailyevents.contents.miraculousfishing.contents.items.RareFishingTreasureLootbox;
import fr.openmc.core.hooks.itemsadder.ItemsAdderHook;
import fr.openmc.core.registry.items.contents.AywenCap;
import fr.openmc.core.registry.items.contents.Hammer;
import fr.openmc.core.registry.items.listeners.BlockBreakListener;
import fr.openmc.core.registry.items.listeners.BlockPlaceListener;
import fr.openmc.core.registry.items.listeners.EquipableItemListener;
import fr.openmc.core.registry.items.listeners.InteractListener;
import io.papermc.paper.persistence.PersistentDataContainerView;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.Set;

public class CustomItemRegistry extends Registry<String, CustomItem>
        implements KeyedRegistry<String, CustomItem>, HasListeners {

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
    public final CustomItem COMPANY_BOX = register("omc_shops:company_box", Material.CHEST);
    public final CustomItem HOMES_ICON_BIN_RED = register("omc_homes:omc_homes_icon_bin_red", Material.CHEST);
    public final CustomItem HOMES_ICON_BIN = register("omc_homes:omc_homes_icon_bin", Material.CHEST);
    public final CustomItem HOMES_ICON_INFORMATION = register("omc_homes:omc_homes_icon_information", Material.CHEST);
    public final CustomItem HOMES_ICON_UPGRADE = register("omc_homes:omc_homes_icon_upgrade", Material.CHEST);

    /* Blocs */
    public final CustomItem AYWENITE_BLOCK = register("omc_blocks:aywenite_block", Material.AMETHYST_BLOCK);
    public final CustomItem PELUCHE_SEINYY = register("omc_plush:peluche_seinyy", Material.PAPER);
    public final CustomItem PELUCHE_AWYEN = register("omc_plush:peluche_awyen", Material.PAPER);
    public final CustomItem URNE = register("omc_blocks:urne", Material.GLASS);
    public final CustomItem CAISSE = register("omc_shops:caisse", Material.PAPER);

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

    /* Daily Event */
    public final CustomItem TENDERS = register("omc_daily_events:tenders", Material.COOKED_CHICKEN);
    public final CustomItem SPONGE_BOB = register("omc_daily_events:bob_sponge", Material.SPONGE);
    public final CustomItem KEBAB_FERMENTED = register("omc_daily_events:kebab_fermented", Material.COOKED_BEEF);

    public final CustomItem ANCIENT_FISHER_HELMET = register("omc_daily_events:ancient_fishing_helmet", Material.IRON_HELMET);
    public final CustomItem ANCIENT_FISHER_CHESTPLATE = register("omc_daily_events:ancient_fishing_chestplate", Material.IRON_CHESTPLATE);
    public final CustomItem ANCIENT_FISHER_LEGGINGS = register("omc_daily_events:ancient_fishing_leggings", Material.IRON_LEGGINGS);
    public final CustomItem ANCIENT_FISHER_BOOTS = register("omc_daily_events:ancient_fishing_boots", Material.IRON_BOOTS);

    public final CustomItem FISHERMAN_BLUE_FISH = register("omc_daily_events:fisherman_blue_fish", Material.PAPER);
    public final CustomItem FISHERMAN_CYAN_FISH = register("omc_daily_events:fisherman_cyan_fish", Material.PAPER);
    public final CustomItem FISHERMAN_ORANGE_FISH = register("omc_daily_events:fisherman_orange_fish", Material.PAPER);
    public final CustomItem FISHERMAN_RED_FISH = register("omc_daily_events:fisherman_red_fish", Material.PAPER);
    public final CustomItem FISHERMAN_BOAT = register("omc_daily_events:fisherman_boat", Material.PAPER);
    public final CustomItem FISHERMAN_CHAIR = register("omc_daily_events:fisherman_chair", Material.PAPER);
    public final CustomItem FISHERMAN_FISH_BOX = register("omc_daily_events:fisherman_fish_box", Material.PAPER);
    public final CustomItem FISHERMAN_FISH_RACK = register("omc_daily_events:fisherman_fish_rack", Material.PAPER);
    public final CustomItem FISHERMAN_FISHING_POLE = register("omc_daily_events:fisherman_fishing_pole", Material.PAPER);
    public final CustomItem FISHERMAN_FISHINGPOLE_RACK = register("omc_daily_events:fisherman_fishingpole_rack", Material.PAPER);
    public final CustomItem FISHERMAN_FLOATIE = register("omc_daily_events:fisherman_floatie", Material.PAPER);
    public final CustomItem FISHERMAN_HANGING_FISH = register("omc_daily_events:fisherman_hanging_fish", Material.PAPER);
    public final CustomItem FISHERMAN_LANDING_NET = register("omc_daily_events:fisherman_landing_net", Material.PAPER);
    public final CustomItem FISHERMAN_LARGE_FISHNET = register("omc_daily_events:fisherman_large_fishnet", Material.PAPER);
    public final CustomItem FISHERMAN_LOBSTER_TRAP = register("omc_daily_events:fisherman_lobster_trap", Material.PAPER);
    public final CustomItem FISHERMAN_STAND = register("omc_daily_events:fisherman_stand", Material.PAPER);
    public final CustomItem FISHERMAN_TABLE = register("omc_daily_events:fisherman_table", Material.PAPER);

    public final CustomItem RARE_FISHING_TREASURE = register(new RareFishingTreasureLootbox("omc_daily_events:rare_fishing_treasure_lootbox"));
    public final CustomItem EPIC_FISHING_TREASURE = register(new EpicFishingTreasureLootbox("omc_daily_events:epic_fishing_treasure_lootbox"));
    public final CustomItem LEGENDARY_FISHING_TREASURE = register(new LegendaryFishingTreasureLootbox("omc_daily_events:legendary_fishing_treasure_lootbox"));
    public final CustomItem FISHING_FURNITURE_BOX = register(new FishingFurnitureLootbox("omc_daily_events:fishing_furniture_lootbox"));

    public final CustomItem COIN = register("omc_daily_events:coin", Material.GOLD_INGOT);
    public final CustomItem POISSON_STEVE_HEAD = register("omc_daily_events:poisson_steve_head", Material.PLAYER_HEAD);
    public final CustomItem KRAKEN_HEAD = register("omc_daily_events:kraken_head", Material.PLAYER_HEAD);
    public final CustomItem LEVIATHAN_HEAD = register("omc_daily_events:leviathan_head", Material.PLAYER_HEAD);
    public final CustomItem VAMPIRE_HEAD = register("omc_daily_events:vampire_head", Material.PLAYER_HEAD);

    public final CustomItem OBESE_POTATO = register("omc_daily_events:obese_potato", Material.NOTE_BLOCK);
    public final CustomItem OBESE_POISONOUS_POTATO = register("omc_daily_events:obese_poisonous_potato", Material.NOTE_BLOCK);
    public final CustomItem OBESE_BAKED_POTATO = register("omc_daily_events:obese_baked_potato", Material.NOTE_BLOCK);
    public final CustomItem OBESE_CARROT = register("omc_daily_events:obese_carrot", Material.NOTE_BLOCK);
    public final CustomItem OBESE_BEETROOT = register("omc_daily_events:obese_beetroot", Material.NOTE_BLOCK);
    public final CustomItem OBESE_NETHER_WART = register("omc_daily_events:obese_nether_wart", Material.NOTE_BLOCK);
    public final CustomItem OBESE_ONION = register("omc_daily_events:obese_onion", Material.NOTE_BLOCK);
    public final CustomItem OBESE_GOLDEN_APPLE = register("omc_daily_events:obese_golden_apple", Material.NOTE_BLOCK);

    public final CustomItem OBESE_POTATO_STEM = register("omc_daily_events:obese_potato_stem", Material.SHORT_GRASS);
    public final CustomItem OBESE_CARROT_STEM = register("omc_daily_events:obese_carrot_stem", Material.SHORT_GRASS);
    public final CustomItem OBESE_BEETROOT_STEM = register("omc_daily_events:obese_beetroot_stem", Material.SHORT_GRASS);
    public final CustomItem OBESE_NETHER_WART_STEM = register("omc_daily_events:obese_nether_wart_stem", Material.SHORT_GRASS);
    public final CustomItem OBESE_GOLDEN_APPLE_STEM = register("omc_daily_events:obese_golden_apple_stem", Material.SHORT_GRASS);

    public final CustomItem PEELED_OBESE_POTATO = register("omc_daily_events:peeled_obese_potato", Material.NOTE_BLOCK);
    public final CustomItem PEELED_OBESE_BEETROOT = register("omc_daily_events:peeled_obese_beetroot", Material.NOTE_BLOCK);
    public final CustomItem PEELED_OBESE_CARROT = register("omc_daily_events:peeled_obese_carrot", Material.NOTE_BLOCK);
    public final CustomItem PEELED_OBESE_ONION = register("omc_daily_events:peeled_obese_onion", Material.NOTE_BLOCK);

    public final CustomItem FERMENTUM = register("omc_daily_events:fermentum", Material.PAPER);
    public final CustomItem BLESSED_BREAD = register("omc_daily_events:blessed_bread", Material.BREAD);

    public final CustomItem GOLDEN_PUMPKIN = register("omc_daily_events:golden_pumpkin", Material.PUMPKIN);
    public final CustomItem GOLDEN_MELON = register("omc_daily_events:golden_melon", Material.MELON);
    public final CustomItem GOLDEN_BEETROOT = register("omc_daily_events:golden_beetroot", Material.BEETROOT);
    public final CustomItem GOLDEN_POTATO = register("omc_daily_events:golden_potato", Material.POTATO);
    public final CustomItem GOLDEN_WHEAT = register("omc_daily_events:golden_wheat", Material.WHEAT);
    public final CustomItem REALLY_GOLDEN_CARROT = register("omc_daily_events:really_golden_carrot", Material.CARROT);

    public final CustomItem ABONDANCE_HELMET = register("omc_daily_events:abondance_helmet", Material.IRON_HELMET);
    public final CustomItem ABONDANCE_CHESTPLATE = register("omc_daily_events:abondance_chestplate", Material.IRON_CHESTPLATE);
    public final CustomItem ABONDANCE_LEGGINGS = register("omc_daily_events:abondance_leggings", Material.IRON_LEGGINGS);
    public final CustomItem ABONDANCE_BOOTS = register("omc_daily_events:abondance_boots", Material.IRON_BOOTS);

    public final CustomItem KITCHEN_BARSTOOL = register("kitchen:kitchen_barstool", Material.PAPER);
    public final CustomItem KITCHEN_BIN = register("kitchen:kitchen_bin", Material.PAPER);
    public final CustomItem KITCHEN_CHAIR = register("kitchen:kitchen_chair", Material.PAPER);
    public final CustomItem KITCHEN_COOKERHOOD = register("kitchen:kitchen_cookerhood", Material.PAPER);
    public final CustomItem KITCHEN_DISH = register("kitchen:kitchen_dish", Material.PAPER);
    public final CustomItem KITCHEN_DISH_PILE = register("kitchen:kitchen_dish_pile", Material.PAPER);
    public final CustomItem KITCHEN_ELEMENT = register("kitchen:kitchen_element", Material.PAPER);
    public final CustomItem KITCHEN_FRIDGE = register("kitchen:kitchen_fridge", Material.PAPER);
    public final CustomItem KITCHEN_KNIFES = register("kitchen:kitchen_knifes", Material.PAPER);
    public final CustomItem KITCHEN_MICROWAVE = register("kitchen:kitchen_microwave", Material.PAPER);
    public final CustomItem KITCHEN_PAN = register("kitchen:kitchen_pan", Material.PAPER);
    public final CustomItem KITCHEN_POT = register("kitchen:kitchen_pot", Material.PAPER);
    public final CustomItem KITCHEN_SINK = register("kitchen:kitchen_sink", Material.PAPER);
    public final CustomItem KITCHEN_STOVE = register("kitchen:kitchen_stove", Material.PAPER);
    public final CustomItem KITCHEN_WALL_ELEMENT = register("kitchen:kitchen_wall_element", Material.PAPER);
    public final CustomItem KITCHEN_WINE = register("kitchen:kitchen_wine", Material.PAPER);
    public final CustomItem KITCHEN_WINERACK = register("kitchen:kitchen_winerack", Material.PAPER);

    public final CustomItem MEDIEVAL_TAVERN_FURNITURE_V1_BAR = register("elitecreatures:medieval_tavern_furniture_v1_bar", Material.PAPER);
    public final CustomItem MEDIEVAL_TAVERN_FURNITURE_V1_BAR_CHAIR = register("elitecreatures:medieval_tavern_furniture_v1_bar_chair", Material.PAPER);
    public final CustomItem MEDIEVAL_TAVERN_FURNITURE_V1_BAR_CORNER = register("elitecreatures:medieval_tavern_furniture_v1_bar_corner", Material.PAPER);
    public final CustomItem MEDIEVAL_TAVERN_FURNITURE_V1_BAR_SIGN = register("elitecreatures:medieval_tavern_furniture_v1_bar_sign", Material.PAPER);
    public final CustomItem MEDIEVAL_TAVERN_FURNITURE_V1_BARREL_STACK = register("elitecreatures:medieval_tavern_furniture_v1_barrel_stack", Material.PAPER);
    public final CustomItem MEDIEVAL_TAVERN_FURNITURE_V1_BEER_TANK = register("elitecreatures:medieval_tavern_furniture_v1_beer_tank", Material.PAPER);
    public final CustomItem MEDIEVAL_TAVERN_FURNITURE_V1_BEER_TANK_WALL = register("elitecreatures:medieval_tavern_furniture_v1_beer_tank_wall", Material.PAPER);
    public final CustomItem MEDIEVAL_TAVERN_FURNITURE_V1_CANDLE = register("elitecreatures:medieval_tavern_furniture_v1_candle", Material.PAPER);
    public final CustomItem MEDIEVAL_TAVERN_FURNITURE_V1_CARPET = register("elitecreatures:medieval_tavern_furniture_v1_carpet", Material.PAPER);
    public final CustomItem MEDIEVAL_TAVERN_FURNITURE_V1_CHAIR = register("elitecreatures:medieval_tavern_furniture_v1_chair", Material.PAPER);
    public final CustomItem MEDIEVAL_TAVERN_FURNITURE_V1_DART_BOARD = register("elitecreatures:medieval_tavern_furniture_v1_dart_board", Material.PAPER);
    public final CustomItem MEDIEVAL_TAVERN_FURNITURE_V1_DRINKING_HORN = register("elitecreatures:medieval_tavern_furniture_v1_drinking_horn", Material.PAPER);
    public final CustomItem MEDIEVAL_TAVERN_FURNITURE_V1_GLASS_HANGING_BAR = register("elitecreatures:medieval_tavern_furniture_v1_glass_hanging_bar", Material.PAPER);
    public final CustomItem MEDIEVAL_TAVERN_FURNITURE_V1_SHELF_1 = register("elitecreatures:medieval_tavern_furniture_v1_shelf_1", Material.PAPER);
    public final CustomItem MEDIEVAL_TAVERN_FURNITURE_V1_SHELF_2 = register("elitecreatures:medieval_tavern_furniture_v1_shelf_2", Material.PAPER);
    public final CustomItem MEDIEVAL_TAVERN_FURNITURE_V1_SHELF_3 = register("elitecreatures:medieval_tavern_furniture_v1_shelf_3", Material.PAPER);
    public final CustomItem MEDIEVAL_TAVERN_FURNITURE_V1_SHELF_4 = register("elitecreatures:medieval_tavern_furniture_v1_shelf_4", Material.PAPER);
    public final CustomItem MEDIEVAL_TAVERN_FURNITURE_V1_TABLE = register("elitecreatures:medieval_tavern_furniture_v1_table", Material.PAPER);
    public final CustomItem MEDIEVAL_TAVERN_FURNITURE_V1_WALL_LAMP = register("elitecreatures:medieval_tavern_furniture_v1_wall_lamp", Material.PAPER);
    public final CustomItem MEDIEVAL_TAVERN_FURNITURE_V1_WANTD_PAPER = register("elitecreatures:medieval_tavern_furniture_v1_wantd_paper", Material.PAPER);

    public final CustomItem OFFICE_FURNITURE_V2_BOARD_1 = register("elitecreatures:office_furniture_v2_board_1", Material.PAPER);
    public final CustomItem OFFICE_FURNITURE_V2_BOARD_2 = register("elitecreatures:office_furniture_v2_board_2", Material.PAPER);
    public final CustomItem OFFICE_FURNITURE_V2_CHAIRCEO = register("elitecreatures:office_furniture_v2_chairceo", Material.PAPER);
    public final CustomItem OFFICE_FURNITURE_V2_CHAIR = register("elitecreatures:office_furniture_v2_chair", Material.PAPER);
    public final CustomItem OFFICE_FURNITURE_V2_COMPUTER = register("elitecreatures:office_furniture_v2_computer", Material.PAPER);
    public final CustomItem OFFICE_FURNITURE_V2_CUPBOARD_1 = register("elitecreatures:office_furniture_v2_cupboard_1", Material.PAPER);
    public final CustomItem OFFICE_FURNITURE_V2_CUPBOARD_2 = register("elitecreatures:office_furniture_v2_cupboard_2", Material.PAPER);
    public final CustomItem OFFICE_FURNITURE_V2_CUPBOARD_3 = register("elitecreatures:office_furniture_v2_cupboard_3", Material.PAPER);
    public final CustomItem OFFICE_FURNITURE_V2_DRAWER = register("elitecreatures:office_furniture_v2_drawer", Material.PAPER);
    public final CustomItem OFFICE_FURNITURE_V2_FILE = register("elitecreatures:office_furniture_v2_file", Material.PAPER);
    public final CustomItem OFFICE_FURNITURE_V2_LAMP = register("elitecreatures:office_furniture_v2_lamp", Material.PAPER);
    public final CustomItem OFFICE_FURNITURE_V2_POTTEDPLANTS = register("elitecreatures:office_furniture_v2_pottedplants", Material.PAPER);
    public final CustomItem OFFICE_FURNITURE_V2_PRINTER = register("elitecreatures:office_furniture_v2_printer", Material.PAPER);
    public final CustomItem OFFICE_FURNITURE_V2_PROJECTOR_1 = register("elitecreatures:office_furniture_v2_projector_1", Material.PAPER);
    public final CustomItem OFFICE_FURNITURE_V2_PROJECTOR_2 = register("elitecreatures:office_furniture_v2_projector_2", Material.PAPER);
    public final CustomItem OFFICE_FURNITURE_V2_RUBBISHBIN = register("elitecreatures:office_furniture_v2_rubbishbin", Material.PAPER);
    public final CustomItem OFFICE_FURNITURE_V2_SOFA_1 = register("elitecreatures:office_furniture_v2_sofa_1", Material.PAPER);
    public final CustomItem OFFICE_FURNITURE_V2_SOFA_2 = register("elitecreatures:office_furniture_v2_sofa_2", Material.PAPER);
    public final CustomItem OFFICE_FURNITURE_V2_TABLECEO = register("elitecreatures:office_furniture_v2_tableceo", Material.PAPER);
    public final CustomItem OFFICE_FURNITURE_V2_TABLE_1 = register("elitecreatures:office_furniture_v2_table_1", Material.PAPER);
    public final CustomItem OFFICE_FURNITURE_V2_TABLE_2 = register("elitecreatures:office_furniture_v2_table_2", Material.PAPER);

    public final CustomItem MODERN_FURNITURE_PACK_VOL2_BED = register("elitecreatures:modern_furniture_pack_vol2_bed", Material.PAPER);
    public final CustomItem MODERN_FURNITURE_PACK_VOL2_BOARD = register("elitecreatures:modern_furniture_pack_vol2_board", Material.PAPER);
    public final CustomItem MODERN_FURNITURE_PACK_VOL2_CABINET = register("elitecreatures:modern_furniture_pack_vol2_cabinet", Material.PAPER);
    public final CustomItem MODERN_FURNITURE_PACK_VOL2_CARPET = register("elitecreatures:modern_furniture_pack_vol2_carpet", Material.PAPER);
    public final CustomItem MODERN_FURNITURE_PACK_VOL2_CHAIR = register("elitecreatures:modern_furniture_pack_vol2_chair", Material.PAPER);
    public final CustomItem MODERN_FURNITURE_PACK_VOL2_COMPUTERCHAIR = register("elitecreatures:modern_furniture_pack_vol2_computerchair", Material.PAPER);
    public final CustomItem MODERN_FURNITURE_PACK_VOL2_COMPUTERDESK = register("elitecreatures:modern_furniture_pack_vol2_computerdesk", Material.PAPER);
    public final CustomItem MODERN_FURNITURE_PACK_VOL2_LAMP = register("elitecreatures:modern_furniture_pack_vol2_lamp", Material.PAPER);
    public final CustomItem MODERN_FURNITURE_PACK_VOL2_MACBOOK = register("elitecreatures:modern_furniture_pack_vol2_macbook", Material.PAPER);
    public final CustomItem MODERN_FURNITURE_PACK_VOL2_PICTURE = register("elitecreatures:modern_furniture_pack_vol2_picture", Material.PAPER);
    public final CustomItem MODERN_FURNITURE_PACK_VOL2_PLANTPOT = register("elitecreatures:modern_furniture_pack_vol2_plantpot", Material.PAPER);
    public final CustomItem MODERN_FURNITURE_PACK_VOL2_SHELF = register("elitecreatures:modern_furniture_pack_vol2_shelf", Material.PAPER);
    public final CustomItem MODERN_FURNITURE_PACK_VOL2_SOFA_01 = register("elitecreatures:modern_furniture_pack_vol2_sofa_01", Material.PAPER);
    public final CustomItem MODERN_FURNITURE_PACK_VOL2_SOFA_02 = register("elitecreatures:modern_furniture_pack_vol2_sofa_02", Material.PAPER);
    public final CustomItem MODERN_FURNITURE_PACK_VOL2_TABLE_01 = register("elitecreatures:modern_furniture_pack_vol2_table_01", Material.PAPER);
    public final CustomItem MODERN_FURNITURE_PACK_VOL2_TABLE_02 = register("elitecreatures:modern_furniture_pack_vol2_table_02", Material.PAPER);
    public final CustomItem MODERN_FURNITURE_PACK_VOL2_TABLE_03 = register("elitecreatures:modern_furniture_pack_vol2_table_03", Material.PAPER);
    public final CustomItem MODERN_FURNITURE_PACK_VOL2_TV = register("elitecreatures:modern_furniture_pack_vol2_tv", Material.PAPER);
    public final CustomItem MODERN_FURNITURE_PACK_VOL2_WARDROBE_01 = register("elitecreatures:modern_furniture_pack_vol2_wardrobe_01", Material.PAPER);
    public final CustomItem MODERN_FURNITURE_PACK_VOL2_WARDROBE_02 = register("elitecreatures:modern_furniture_pack_vol2_wardrobe_02", Material.PAPER);

    public final CustomItem MODERN_BOX = register(new ModernBox("omc_bits:modern_box"));
    public final CustomItem KITCHEN_BOX = register(new KitchenBox("omc_bits:kitchen_box"));
    public final CustomItem OFFICE_BOX = register(new OfficeBox("omc_bits:office_box"));
    public final CustomItem MEDIEVAL_BOX = register(new MedievalBox("omc_bits:medieval_box"));

    @Override
    public Set<Listener> getListeners() {
        return Set.of(
                new BlockBreakListener(),
                new EquipableItemListener(),
                new InteractListener(),
                new BlockPlaceListener()
        );
    }

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

    public Optional<CustomItem> get(Block block) {
        if (block == null) return Optional.empty();

        if (!ItemsAdderHook.isEnable())
            throw new IllegalStateException("Impossible d'avoir un CustomItem via un Block, néccésite que ItemsAdder soit activé");

        CustomBlock customBlock = CustomBlock.byAlreadyPlaced(block);

        if (customBlock == null) return Optional.empty();

        return this.get(customBlock.getNamespacedID());
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
            public @NotNull ItemStack getVanilla() {
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
            public @NotNull ItemStack getVanilla() {
                ItemStack item = new ItemStack(material);
                ItemMeta meta = item.getItemMeta();
                meta.displayName(Component.text(displayName).decoration(TextDecoration.ITALIC, false));
                item.setItemMeta(meta);
                return item;
            }
        });
    }
}
