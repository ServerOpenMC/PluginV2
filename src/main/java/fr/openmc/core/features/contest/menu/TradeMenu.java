package fr.openmc.core.features.contest.menu;

import dev.lone.itemsadder.api.CustomStack;
import dev.xernas.menulib.Menu;
import dev.xernas.menulib.utils.InventorySize;
import dev.xernas.menulib.utils.ItemBuilder;
import fr.openmc.core.features.contest.managers.ContestManager;
import fr.openmc.core.features.contest.managers.ContestPlayerManager;
import fr.openmc.core.features.mailboxes.MailboxManager;
import fr.openmc.core.utils.ItemUtils;
import fr.openmc.core.utils.PapiAPI;
import fr.openmc.core.utils.customitems.CustomItemRegistry;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
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
        NamedTextColor campColor = contestManager.dataPlayer.get(player.getUniqueId().toString()).getColor();

        // ITEM ADDER
        String namespaceShellContest = "omc_contest:contest_shell";
        ItemStack shellContest = CustomItemRegistry.getByName(namespaceShellContest).getBest();

        List<Component> loreInfo = Arrays.asList(
                Component.text("§7Apprenez en plus sur les Contest !"),
                Component.text("§7Le déroulement..., Les résultats, ..."),
                Component.text("§e§lCLIQUEZ ICI POUR EN VOIR PLUS!")
        );

        List<Component> loreTrade = Arrays.asList(
                Component.text("§7Vendez un maximum de ressources"),
                Component.text("§7Contre des §bCoquillages de Contest"),
                Component.text("§7Pour faire gagner la ")
                        .append(Component.text("Team " + campName).decoration(TextDecoration.ITALIC, false).color(campColor))
        );

        inventory.put(4, new ItemBuilder(this, shellContest, itemMeta -> {
            itemMeta.displayName(Component.text("§7Les Trades"));
            itemMeta.lore(loreTrade);
        }));

        List<Map<String, Object>> selectedTrades = contestManager.getTradeSelected(true).stream()
                .sorted(Comparator.comparing(trade -> (String) trade.get("ress")))
                .collect(Collectors.toList());

        List<Integer> slotTrade = Arrays.asList(10, 11, 12, 13, 14, 15, 16, 20, 21, 22, 23, 24);

        for (int i = 0; i < selectedTrades.size(); i++) {
            Map<String, Object> trade = selectedTrades.get(i);
            Integer slot = slotTrade.get(i);

            Material m = Material.getMaterial((String) trade.get("ress"));
            List<Component> loreTrades = Arrays.asList(
                    Component.text("§7Vendez §e" + trade.get("amount") + " de cette ressource §7pour §b" + trade.get("amount_shell") + " Coquillage(s) de Contest"),
                    Component.text("§e§lCLIQUE-GAUCHE POUR VENDRE UNE FOIS"),
                    Component.text("§e§lSHIFT-CLIQUE-GAUCHE POUR VENDRE TOUTE CETTE RESSOURCE")
            );

            inventory.put(slot, new ItemBuilder(this, m, itemMeta -> {
                itemMeta.lore(loreTrades);
            }).setOnClick(inventoryClickEvent -> {
                if (!CustomItemRegistry.hasItemsAdder()) {
                    MessagesManager.sendMessage(player, Component.text("§cFonctionnalité bloqué. Veuillez contactez l'administration"), Prefix.CONTEST, MessageType.ERROR, true);
                    return;
                }

                String m1 = String.valueOf(inventoryClickEvent.getCurrentItem().getType());
                int amount = (int) trade.get("amount");
                int amountShell = (int) trade.get("amount_shell");
                ItemStack shellContestItem = CustomStack.getInstance(namespaceShellContest).getItemStack();
                if (inventoryClickEvent.isLeftClick() && inventoryClickEvent.isShiftClick()) {
                    int items = 0;
                    for (ItemStack is : player.getInventory().getContents()) {
                        if (is != null && is.getType() == inventoryClickEvent.getCurrentItem().getType()) {
                            items = items + is.getAmount();
                        }
                    }

                    if (ItemUtils.hasEnoughItems(player, inventoryClickEvent.getCurrentItem().getType(), amount)) {
                        int amountShell2 = (items / amount) * amountShell;
                        int items1 = (amountShell2 / amountShell) * amount;
                        ItemUtils.removeItemsFromInventory(player, inventoryClickEvent.getCurrentItem().getType(), items1);
                        int slotEmpty = ItemUtils.getSlotNull(player);
                        int stackAvailable = slotEmpty * 64;
                        int additem = Math.min(amountShell2, stackAvailable);
                        if (stackAvailable >=64) {
                            shellContestItem.setAmount(additem);
                            for (ItemStack item : ItemUtils.splitAmountIntoStack(shellContestItem)) {
                                player.getInventory().addItem(item);
                            }
                            int remain1 = amountShell2 - additem;
                            if(remain1 != 0) {
                                int numbertoStack = ItemUtils.getNumberItemToStack(player, shellContestItem);
                                if (numbertoStack > 0) {
                                    shellContestItem.setAmount(numbertoStack);
                                    player.getInventory().addItem(shellContestItem);
                                }

                                ItemStack newshellContestItem = CustomStack.getInstance(namespaceShellContest).getItemStack();
                                int remain2 = remain1 - numbertoStack;
                                if (remain2 != 0) {
                                    newshellContestItem.setAmount(remain2);
                                    List<ItemStack> itemlist = ItemUtils.splitAmountIntoStack(newshellContestItem);
                                    ItemStack[] shellContestArray = itemlist.toArray(new ItemStack[itemlist.size()]);
                                    MailboxManager.sendItems(player, player, shellContestArray);
                                }
                            }
                        } else {
                            shellContestItem.setAmount(amountShell2);
                            ItemStack[] shellContestArray = new ItemStack[]{shellContestItem, shellContestItem};
                            for (ItemStack item : ItemUtils.splitAmountIntoStack(shellContestItem)) {
                                player.getInventory().addItem(item);
                            }

                            MailboxManager.sendItems(player, player, shellContestArray);
                        }

                        MessagesManager.sendMessage(player, Component.text("§7Vous avez échangé §e" + items1 + " " + m1 + " §7contre§b " + amountShell2 + " Coquillages(s) de Contest"), Prefix.CONTEST, MessageType.SUCCESS, true);
                    } else {
                        MessagesManager.sendMessage(player, Component.text("§cVous n'avez pas assez de cette ressource pour pouvoir l'échanger!"), Prefix.CONTEST, MessageType.ERROR, true);
                    }
                } else if (inventoryClickEvent.isLeftClick()) {
                    if (ItemUtils.hasEnoughItems(player, inventoryClickEvent.getCurrentItem().getType(), amount)) {

                        //mettre dans l'inv ou boite mail?
                        if (Arrays.asList(player.getInventory().getStorageContents()).contains(null)) {
                            shellContestItem.setAmount(amountShell);
                            for (ItemStack item : ItemUtils.splitAmountIntoStack(shellContestItem)) {
                                player.getInventory().addItem(item);
                            }
                        } else {
                            shellContestItem.setAmount(amountShell);
                            ItemStack[] shellContestArray = new ItemStack[]{shellContestItem};
                            MailboxManager.sendItems(player, player, shellContestArray);
                        }

                        ItemUtils.removeItemsFromInventory(player, inventoryClickEvent.getCurrentItem().getType(), amount);
                        MessagesManager.sendMessage(player, Component.text("§7Vous avez échangé §e" + amount + " " + m1 + " §7contre§b " + amountShell + " Coquillages(s) de Contest"), Prefix.CONTEST, MessageType.SUCCESS, true);
                    } else {
                        MessagesManager.sendMessage(player, Component.text("§cVous n'avez pas assez de cette ressource pour pouvoir l'échanger!"), Prefix.CONTEST, MessageType.ERROR, true);
                    }
                }
            }));
        }

        inventory.put(27, new ItemBuilder(this, Material.ARROW, itemMeta -> {
            itemMeta.displayName(Component.text("§r§aRetour"));
        }).setBackButton());

        inventory.put(35, new ItemBuilder(this, Material.EMERALD, itemMeta -> {
            itemMeta.displayName(Component.text("§r§aPlus d'info !"));
            itemMeta.lore(loreInfo);
        }).setNextMenu(new MoreInfoMenu(getOwner())));

        return inventory;
    }
}
