package fr.openmc.core.features.events.contents.weeklyevents.contents.contest.menu;

import dev.lone.itemsadder.api.FontImages.FontImageWrapper;
import fr.openmc.api.menulib.Menu;
import fr.openmc.api.menulib.utils.InventorySize;
import fr.openmc.api.menulib.utils.ItemMenuBuilder;
import fr.openmc.core.OMCRegistry;
import fr.openmc.core.features.events.contents.weeklyevents.contents.contest.managers.ContestManager;
import fr.openmc.core.features.events.contents.weeklyevents.contents.contest.managers.ContestPlayerManager;
import fr.openmc.core.features.events.contents.weeklyevents.contents.contest.managers.TradeYMLManager;
import fr.openmc.core.features.mailboxes.MailboxManager;
import fr.openmc.core.hooks.itemsadder.ItemsAdderHook;
import fr.openmc.core.utils.bukkit.ItemUtils;
import fr.openmc.core.utils.text.messages.MessageType;
import fr.openmc.core.utils.text.messages.MessagesManager;
import fr.openmc.core.utils.text.messages.Prefix;
import fr.openmc.core.utils.text.messages.TranslationManager;
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
    public @NotNull Component getName() {
        return TranslationManager.translation("feature.events.contest.trade.menu.title");
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
    public @NotNull Map<Integer, ItemMenuBuilder> getContent() {
        Player player = getOwner();
        Map<Integer, ItemMenuBuilder> inventory = new HashMap<>();

        String campName = ContestPlayerManager.getPlayerCampName(player);
        NamedTextColor campColor = ContestManager.dataPlayer.get(player.getUniqueId()).getColor();

        ItemStack shellContest = OMCRegistry.CUSTOM_ITEMS.CONTEST_SHELL.getBest();

        List<Component> loreInfo = TranslationManager.translationLore("feature.events.contest.trade.info.lore");

        List<Component> loreTrade = TranslationManager.translationLore(
                "feature.events.contest.trade.main.lore",
                Component.text("Team " + campName).decoration(TextDecoration.ITALIC, false).color(campColor)
        );

        inventory.put(4, new ItemMenuBuilder(this, shellContest, itemMeta -> {
            itemMeta.displayName(TranslationManager.translation("feature.events.contest.trade.main.name"));
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

            List<Component> lore = TranslationManager.translationLore(
                    "feature.events.contest.trade.offer.lore",
                    Component.text(amount).color(NamedTextColor.YELLOW),
                    Component.text(amountShell).color(NamedTextColor.AQUA)
            );


            inventory.put(tradeSlots.get(i), new ItemMenuBuilder(this, material, meta -> meta.lore(lore))
                    .setOnClick(event -> {
                        if (!ItemsAdderHook.isEnable()) {
                            MessagesManager.sendMessage(player,
                                    TranslationManager.translation("feature.events.contest.trade.unavailable"),
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

        inventory.put(27, new ItemMenuBuilder(this, Material.ARROW, true));

        inventory.put(35, new ItemMenuBuilder(this, Material.EMERALD, itemMeta -> {
            itemMeta.displayName(TranslationManager.translation("feature.events.contest.vote.info.name"));
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
        ItemStack baseShell = OMCRegistry.CUSTOM_ITEMS.CONTEST_SHELL.getBest();
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
                TranslationManager.translation(
                        "feature.events.contest.trade.success",
                        Component.text(itemsRemoved).color(NamedTextColor.YELLOW),
                        tradeName.color(NamedTextColor.YELLOW),
                        Component.text(shellsEarned).color(NamedTextColor.AQUA)
                ),
                Prefix.CONTEST, MessageType.SUCCESS, true);
    }

    /**
     * Envoie un message d'erreur indiquant que le joueur ne possède pas assez d'items pour l'échange.
     *
     * @param player le joueur destinataire du message
     */
    private void sendNotEnoughMessage(Player player) {
        MessagesManager.sendMessage(player,
                TranslationManager.translation("feature.events.contest.trade.not_enough"),
                Prefix.CONTEST, MessageType.ERROR, true);
    }
}
