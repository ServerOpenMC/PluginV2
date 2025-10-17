package fr.openmc.core.features.contest.menu;

import dev.lone.itemsadder.api.CustomStack;
import dev.lone.itemsadder.api.FontImages.FontImageWrapper;
import fr.openmc.api.hooks.ItemsAdderHook;
import fr.openmc.api.menulib.Menu;
import fr.openmc.api.menulib.utils.InventorySize;
import fr.openmc.api.menulib.utils.ItemBuilder;
import fr.openmc.core.features.contest.managers.ContestManager;
import fr.openmc.core.features.contest.managers.ContestPlayerManager;
import fr.openmc.core.features.contest.managers.TradeYMLManager;
import fr.openmc.core.features.mailboxes.MailboxManager;
import fr.openmc.core.items.CustomItemRegistry;
import fr.openmc.core.utils.ItemUtils;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TranslatableComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class TradeMenu extends Menu {

    private static final String SHELL_NAMESPACE = "omc_contest:contest_shell";

    public TradeMenu(Player owner) {
        super(owner);
    }

    @Override
    public @NotNull String getName() {
        return "Menu des Contests - Échanges";
    }

    @Override
    public String getTexture() {
        return FontImageWrapper.replaceFontImages("§r§f:offset_-48::contest_menu:");
    }

    @Override
    public @NotNull InventorySize getInventorySize() {
        return InventorySize.LARGE;
    }

    @Override
    public void onInventoryClick(InventoryClickEvent click) {
        // empty
    }

    @Override
    public @NotNull Map<Integer, ItemBuilder> getContent() {
        Player player = getOwner();
        Map<Integer, ItemBuilder> inventory = new HashMap<>();

        String campName = ContestPlayerManager.getPlayerCampName(player);
        NamedTextColor campColor = ContestManager.dataPlayer.get(player.getUniqueId()).getColor();

        ItemStack shellContest = CustomItemRegistry.getByName(SHELL_NAMESPACE).getBest();

        List<Component> loreInfo = Arrays.asList(
                Component.text("§7Apprenez en plus sur les contest !"),
                Component.text("§7Le déroulement, les résultats, ..."),
                Component.text("§e§lCLIQUEZ ICI POUR EN VOIR PLUS!")
        );

        List<Component> loreTrade = Arrays.asList(
                Component.text("§7Vendez un maximum de ressources"),
                Component.text("§7Contre des §bcoquillages de contest"),
                Component.text("§7Pour faire gagner la ")
                        .append(Component.text("Team " + campName).decoration(TextDecoration.ITALIC, false).color(campColor))
        );

        inventory.put(4, new ItemBuilder(this, shellContest, itemMeta -> {
            itemMeta.displayName(Component.text("§7Les échanges"));
            itemMeta.lore(loreTrade);
        }));

        List<Map<String, Object>> trades = TradeYMLManager.getTradeSelected(true)
                .stream()
                .sorted(Comparator.comparing(trade -> (String) trade.get("ress")))
                .toList();

        List<Integer> tradeSlots = Arrays.asList(10, 11, 12, 13, 14, 15, 16, 20, 21, 22, 23, 24);
        for (int i = 0; i < trades.size() && i < tradeSlots.size(); i++) {
            Map<String, Object> trade = trades.get(i);
            Material material = Material.getMaterial((String) trade.get("ress"));
            int amount = (int) trade.get("amount");
            int amountShell = (int) trade.get("amount_shell");

            List<Component> lore = Arrays.asList(
                    Component.text("§7Vendez §e" + amount + " §7pour §b" + amountShell + " coquillage(s)"),
                    Component.text("§e§lCLIQUE-GAUCHE POUR VENDRE UNE FOIS"),
                    Component.text("§e§lSHIFT-CLIQUE-GAUCHE POUR VENDRE TOUTE CETTE RESSOURCE")
            );


            inventory.put(tradeSlots.get(i), new ItemBuilder(this, material, meta -> meta.lore(lore))
                    .setOnClick(event -> {
                        if (!ItemsAdderHook.isHasItemAdder()) {
                            MessagesManager.sendMessage(player,
                                    Component.text("§cFonctionnalité bloquée. Contactez l'administration."),
                                    Prefix.CONTEST, MessageType.ERROR, true);
                            return;
                        }

                        if (event.getCurrentItem() == null) return;

                        TranslatableComponent tradeName = ItemUtils.getItemTranslation(event.getCurrentItem().getType());

                        if (event.isShiftClick()) {
                            handleBulkTrade(player, event.getCurrentItem(), amount, amountShell, tradeName);
                        } else if (event.isLeftClick()) {
                            handleSingleTrade(player, event.getCurrentItem(), amount, amountShell, tradeName);
                        }
                    })
            );
        }

        inventory.put(27, new ItemBuilder(this, Material.ARROW, itemMeta -> itemMeta.displayName(Component.text("§r§aRetour")), true));

        inventory.put(35, new ItemBuilder(this, Material.EMERALD, itemMeta -> {
            itemMeta.displayName(Component.text("§r§aPlus d'info !"));
            itemMeta.lore(loreInfo);
        }).setOnClick(inventoryClickEvent -> new MoreInfoMenu(getOwner()).open()));

        return inventory;
    }

    @Override
    public void onClose(InventoryCloseEvent event) {
        //empty
    }

    @Override
    public List<Integer> getTakableSlot() {
        return List.of();
    }

    /**
     * Gère l'échange simple d'items pour un trade.
     * <p>
     * Vérifie si le joueur possède assez d'items, supprime les items échangés, attribue les coquillages
     * correspondants et envoie un message de succès.
     *
     * @param player       le joueur effectuant le trade
     * @param item         l'item concerné par l'échange
     * @param itemsRemoved le nombre d'items à retirer
     * @param shellsEarned le nombre de coquillages à attribuer
     * @param tradeName    le nom du trade sous forme de composant traduisible
     */
    private void handleSingleTrade(Player player, ItemStack item, int itemsRemoved, int shellsEarned, TranslatableComponent tradeName) {
        if (!ItemUtils.hasEnoughItems(player, item, itemsRemoved)) {
            sendNotEnoughMessage(player);
            return;
        }
        ItemUtils.removeItemsFromInventory(player, item, itemsRemoved);
        giveShells(player, shellsEarned);
        sendSuccessMessage(player, itemsRemoved, shellsEarned, tradeName);
    }

    /**
     * Gère l'échange en masse d'items pour un trade.
     * <p>
     * Vérifie si le joueur possède assez d'items, calcule la somme totale d'items dans l'inventaire,
     * détermine le nombre de coquillages à attribuer et d'items à retirer, puis réalise l'échange et
     * envoie un message de succès.
     *
     * @param player      le joueur effectuant le trade
     * @param item        l'item concerné par l'échange
     * @param amount      le nombre minimal d'items pour réaliser un échange
     * @param amountShell le nombre de coquillages attribués pour cet échange
     * @param tradeName   le nom du trade sous forme de composant traduisible
     */
    private void handleBulkTrade(Player player, ItemStack item, int amount, int amountShell, TranslatableComponent tradeName) {
        if (!ItemUtils.hasEnoughItems(player, item, amount)) {
            sendNotEnoughMessage(player);
            return;
        }
        int totalItems = Arrays.stream(player.getInventory().getContents())
                .filter(is -> is != null && is.getType() == item.getType())
                .mapToInt(ItemStack::getAmount)
                .sum();
        int shellsEarned = (totalItems / amount) * amountShell;
        int itemsRemoved = (shellsEarned / amountShell) * amount;
        ItemUtils.removeItemsFromInventory(player, item, itemsRemoved);
        giveShells(player, shellsEarned);
        sendSuccessMessage(player, itemsRemoved, shellsEarned, tradeName);
    }

    /**
     * Attribue au joueur un certain nombre de coquillages en répartissant l'item coquillage en stacks.
     * <p>
     * Si l'inventaire du joueur ne peut accueillir tous les items, ceux-ci sont envoyés par courrier.
     *
     * @param player le joueur destinataire des coquillages
     * @param amount le nombre total de coquillages à attribuer
     */
    private void giveShells(Player player, int amount) {
        ItemStack baseShell = CustomStack.getInstance(SHELL_NAMESPACE).getItemStack();
        List<ItemStack> stacks = ItemUtils.splitAmountIntoStack(baseShell, amount);
        List<ItemStack> leftovers = new ArrayList<>();
        for (ItemStack stack : stacks) {
            HashMap<Integer, ItemStack> result = player.getInventory().addItem(stack);
            if (!result.isEmpty()) {
                leftovers.addAll(result.values());
            }
        }
        if (!leftovers.isEmpty()) {
            MailboxManager.sendItems(player, player, leftovers.toArray(new ItemStack[0]));
        }
    }

    /**
     * Envoie un message de succès au joueur après un trade réussi.
     *
     * Le message indique le nombre d'items échangés et le nombre de coquillages obtenus.
     *
     * @param player       le joueur destinataire du message
     * @param itemsRemoved le nombre d'items échangés
     * @param shellsEarned le nombre de coquillages obtenus
     * @param tradeName    le nom du trade sous forme de composant
     */
    private void sendSuccessMessage(Player player, int itemsRemoved, int shellsEarned, Component tradeName) {
        MessagesManager.sendMessage(player,
                Component.text("§7Vous avez échangé §e" + itemsRemoved + " ")
                        .append(tradeName).color(NamedTextColor.YELLOW)
                        .append(Component.text(" §7contre §b" + shellsEarned + " coquillage(s) de contest")),
                Prefix.CONTEST, MessageType.SUCCESS, true);
    }

    /**
     * Envoie un message d'erreur indiquant que le joueur ne possède pas assez d'items pour l'échange.
     *
     * @param player le joueur destinataire du message
     */
    private void sendNotEnoughMessage(Player player) {
        MessagesManager.sendMessage(player,
                Component.text("§cVous n'avez pas assez de cette ressource pour l'échanger !"),
                Prefix.CONTEST, MessageType.ERROR, true);
    }
}
