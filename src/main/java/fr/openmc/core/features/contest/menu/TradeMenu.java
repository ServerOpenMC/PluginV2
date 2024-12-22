package fr.openmc.core.features.contest.menu;

import dev.lone.itemsadder.api.CustomStack;
import dev.xernas.menulib.Menu;
import dev.xernas.menulib.utils.InventorySize;
import dev.xernas.menulib.utils.ItemBuilder;
import fr.openmc.core.features.contest.managers.ContestManager;
import fr.openmc.core.features.contest.managers.ContestPlayerManager;
import fr.openmc.core.utils.ItemUtils;
import fr.openmc.core.utils.PapiAPI;
import fr.openmc.core.utils.customitems.CustomItemRegistry;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;


public class TradeMenu extends Menu {
    private final ContestManager contestManager;
    private final ContestPlayerManager contestPlayerManager;

    public TradeMenu(Player owner) {
        super(owner);
        this.contestManager = ContestManager.getInstance();
        this.contestPlayerManager = ContestPlayerManager.getInstance();
    }

    @Override
    public @NotNull String getName() {
        if (PapiAPI.hasPAPI() && CustomItemRegistry.hasItemsAdder()) {
            return PlaceholderAPI.setPlaceholders(getOwner(), "§r§f%img_offset_-48%%img_contest_menu%");
        } else {
            return "Menu des Contests";
        }
    }

    @Override
    public @NotNull InventorySize getInventorySize() {
        return InventorySize.LARGE;
    }

    @Override public void onInventoryClick(InventoryClickEvent click) {}

    @Override
    public @NotNull Map<Integer, ItemStack> getContent() {
        Player player = getOwner();
        Map<Integer, ItemStack> inventory = new HashMap<>();

        String campName = contestPlayerManager.getPlayerCampName(player);
        ChatColor campColor = contestManager.dataPlayer.get(player.getUniqueId().toString()).color();
        Material shell_contest = CustomStack.getInstance("contest:contest_shell").getItemStack().getType();

        List<String> loreinfo = Arrays.asList(
                "§7Apprenez en plus sur les Contest !",
                "§7Le déroulement..., Les résultats, ...",
                "§e§lCLIQUEZ ICI POUR EN VOIR PLUS!"
        );

        List<String> lore_trade = Arrays.asList(
                "§7Vendez un maximum de ressources",
                "§7Contre des §bCoquillages de Contest",
                "§7Pour faire gagner la " + campColor + "Team " + campName
        );

        inventory.put(4, new ItemBuilder(this, shell_contest, itemMeta -> {
            itemMeta.setDisplayName("§7Les Trades");
            itemMeta.setLore(lore_trade);
            itemMeta.setCustomModelData(10000);
        }));

        List<Map<String, Object>> selectedTrades = contestManager.getTradeSelected(true).stream()
                .sorted(Comparator.comparing(trade -> (String) trade.get("ress")))
                .collect(Collectors.toList());

        List<Integer> slot_trade = Arrays.asList(10, 11, 12, 13, 14, 15, 16, 20, 21, 22, 23, 24);

        for (int i = 0; i < selectedTrades.size(); i++) {
            Map<String, Object> trade = selectedTrades.get(i);
            Integer slot = slot_trade.get(i);

            Material m = Material.getMaterial((String) trade.get("ress"));
            List<String> lore_trades = Arrays.asList(
                    "§7Vendez §e" + trade.get("amount") + " de cette ressource §7pour §b" + trade.get("amount_shell") + " Coquillage(s) de Contest",
                    "§e§lCLIQUE-GAUCHE POUR VENDRE UNE FOIS",
                    "§e§lSHIFT-CLIQUE-GAUCHE POUR VENDRE TOUTE CETTE RESSOURCE"
            );

            inventory.put(slot, new ItemBuilder(this, m, itemMeta -> {
                itemMeta.setLore(lore_trades);
            }).setOnClick(inventoryClickEvent -> {
                String m1 = String.valueOf(inventoryClickEvent.getCurrentItem().getType());
                int amount = (int) trade.get("amount");
                int amount_shell = (int) trade.get("amount_shell");
                ItemStack shell_contestItem = CustomStack.getInstance("contest:contest_shell").getItemStack();
                if (inventoryClickEvent.isLeftClick() && inventoryClickEvent.isShiftClick()) {
                    int items = 0;
                    for (ItemStack is : player.getInventory().getContents()) {
                        if (is != null && is.getType() == inventoryClickEvent.getCurrentItem().getType()) {
                            items = items + is.getAmount();
                        }
                    }

                    if (ItemUtils.hasEnoughItems(player, inventoryClickEvent.getCurrentItem().getType(), amount)) {
                        int amount_shell2 = (items / amount) * amount_shell;
                        int items1 = (amount_shell2 / amount_shell) * amount;
                        ItemUtils.removeItemsFromInventory(player, inventoryClickEvent.getCurrentItem().getType(), items1);
                        int slot_empty = ItemUtils.getSlotNull(player);
                        int stack_available = slot_empty * 64;
                        int additem = Math.min(amount_shell2, stack_available);
                        if (stack_available >=64) {
                            shell_contestItem.setAmount(additem);
                            for (ItemStack item : ItemUtils.splitAmountIntoStack(shell_contestItem)) {
                                player.getInventory().addItem(item);
                            }
                            int remain1 = amount_shell2 - additem;
                            if(remain1 != 0) {
                                int numbertoStack = ItemUtils.getNumberItemToStack(player, shell_contestItem);
                                if (numbertoStack > 0) {
                                    shell_contestItem.setAmount(numbertoStack);
                                    player.getInventory().addItem(shell_contestItem);
                                }

                                ItemStack newshell_contestItem = CustomStack.getInstance("contest:contest_shell").getItemStack();
                                int remain2 = remain1 - numbertoStack;
                                if (remain2 != 0) {
                                    newshell_contestItem.setAmount(remain2);
                                    List<ItemStack> itemlist = ItemUtils.splitAmountIntoStack(newshell_contestItem);
                                    ItemStack[] shell_contest_array = itemlist.toArray(new ItemStack[itemlist.size()]);
                                    //TODO: MailboxManager.sendItems(player, player, shell_contest_array);
                                }
                            }
                        } else {
                            shell_contestItem.setAmount(amount_shell2);
                            ItemStack[] shell_contest_array = new ItemStack[]{shell_contestItem, shell_contestItem};
                            for (ItemStack item : ItemUtils.splitAmountIntoStack(shell_contestItem)) {
                                player.getInventory().addItem(item);
                            }

                            //TODO: MailboxManager.sendItems(player, player, shell_contest_array);
                        }

                        MessagesManager.sendMessageType(player, "§7Vous avez échangé §e" + items1 + " " + m1 + " §7contre§b " + amount_shell2 + " Coquillages(s) de Contest", Prefix.CONTEST, MessageType.SUCCESS, true);
                    } else {
                        MessagesManager.sendMessageType(player, "§cVous n'avez pas assez de cette ressource pour pouvoir l'échanger!", Prefix.CONTEST, MessageType.ERROR, true);
                    }
                } else if (inventoryClickEvent.isLeftClick()) {
                    if (ItemUtils.hasEnoughItems(player, inventoryClickEvent.getCurrentItem().getType(), amount)) {

                        //mettre dans l'inv ou boite mail?
                        if (Arrays.asList(player.getInventory().getStorageContents()).contains(null)) {
                            shell_contestItem.setAmount(amount_shell);
                            for (ItemStack item : ItemUtils.splitAmountIntoStack(shell_contestItem)) {
                                player.getInventory().addItem(item);
                            }
                        } else {
                            shell_contestItem.setAmount(amount_shell);
                            ItemStack[] shell_contest_array = new ItemStack[]{shell_contestItem};
                            //TODO: MailboxManager.sendItems(player, player, shell_contest_array);
                        }

                        ItemUtils.removeItemsFromInventory(player, inventoryClickEvent.getCurrentItem().getType(), amount);
                        MessagesManager.sendMessageType(player, "§7Vous avez échangé §e" + amount + " " + m1 + " §7contre§b " + amount_shell + " Coquillages(s) de Contest", Prefix.CONTEST, MessageType.SUCCESS, true);
                    } else {
                        MessagesManager.sendMessageType(player, "§cVous n'avez pas assez de cette ressource pour pouvoir l'échanger!", Prefix.CONTEST, MessageType.ERROR, true);
                    }
                }
            }));
        }

        inventory.put(27, new ItemBuilder(this, Material.ARROW, itemMeta -> {
            itemMeta.setDisplayName("§r§aRetour");
        }).setBackButton());

        inventory.put(35, new ItemBuilder(this, Material.EMERALD, itemMeta -> {
            itemMeta.setDisplayName("§r§aPlus d'info !");
            itemMeta.setLore(loreinfo);
        }).setNextMenu(new MoreInfoMenu(getOwner())));

        return inventory;
    }
}
