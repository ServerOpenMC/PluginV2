package fr.openmc.core.features.quests.menus;

import dev.lone.itemsadder.api.FontImages.FontImageWrapper;
import fr.openmc.api.menulib.Menu;
import fr.openmc.api.menulib.utils.InventorySize;
import fr.openmc.api.menulib.utils.ItemMenuBuilder;
import fr.openmc.core.OMCRegistry;
import fr.openmc.core.features.economy.EconomyManager;
import fr.openmc.core.features.quests.QuestsManager;
import fr.openmc.core.features.quests.objects.Quest;
import fr.openmc.core.features.quests.objects.QuestStep;
import fr.openmc.core.features.quests.objects.QuestTier;
import fr.openmc.core.features.quests.rewards.QuestItemReward;
import fr.openmc.core.features.quests.rewards.QuestMoneyReward;
import fr.openmc.core.features.quests.rewards.QuestReward;
import fr.openmc.core.registry.items.CustomItemRegistry;
import fr.openmc.core.utils.text.messages.TranslationManager;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.TooltipDisplay;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class QuestsMenu extends Menu {
    private int currentPage;
    private static String TITLE;
    private final int totalPages;
    private Player target;
    private final Map<Integer, Integer> slotToQuestIndex = new HashMap<>();

    public QuestsMenu(Player player, int currentPage) {
        super(player);
        this.currentPage = currentPage;
        this.totalPages = (int) Math.ceil(QuestsManager.getAllQuests().size() / 9.0F);
        this.target = player;
    }

    public QuestsMenu(Player player, Player target, int currentPage) {
        super(player);
        this.currentPage = currentPage;
        this.totalPages = (int) Math.ceil(QuestsManager.getAllQuests().size() / 9.0F);
        this.target = target;
    }

    public QuestsMenu(Player player) {
        this(player, 0);
        this.target = player;
    }

    public QuestsMenu(Player player, Player target) {
        this(player, 0);
        this.target = target;
    }

    public @NotNull Component getName() {
        return TranslationManager.translation("feature.quests.menu.title");
    }

    @Override
    public String getTexture() {
        return FontImageWrapper.replaceFontImages("§r§f:offset_-25::quests_menu:");
    }

    public @NotNull InventorySize getInventorySize() {
        return InventorySize.NORMAL;
    }

    public void onInventoryClick(InventoryClickEvent event) {
        int slot = event.getSlot();
        if (slot == 19 && this.currentPage > 0) {
            --this.currentPage;
            this.refresh();
        } else if (slot == 25 && this.currentPage < this.totalPages - 1) {
            ++this.currentPage;
            this.refresh();
        } else if (slot >= 9 && slot <= 17) {
            Integer questIndex = this.slotToQuestIndex.get(slot);
            if (questIndex != null && questIndex < QuestsManager.getAllQuests().size()) {
                Quest quest = QuestsManager.getAllQuests().get(questIndex);
                UUID playerUUID = this.target.getUniqueId();

                Set<Integer> pendingQuestIndexes = quest.getPendingRewardTiers(playerUUID);

                if (!pendingQuestIndexes.isEmpty()) {
                    int tierIndex = pendingQuestIndexes.iterator().next();
                    boolean allClaimed = quest.claimPendingRewards(target, tierIndex);

                    if (allClaimed || !quest.getPendingRewardTiers(playerUUID).contains(tierIndex))
                        this.refresh();
                }
            }
        }
    }

    public @NotNull Map<Integer, ItemMenuBuilder> getContent() {
        Map<Integer, ItemMenuBuilder> content = new HashMap<>();
        slotToQuestIndex.clear();

        int startIndex = this.currentPage * 9;
        int endIndex = Math.min(startIndex + 9, QuestsManager.getAllQuests().size());
        int slotIndex = 9;

        for(int i = startIndex; i < endIndex; ++i) {
            Quest quest = QuestsManager.getAllQuests().get(i);
            ItemStack item = this.createQuestItem(quest);
            content.put(slotIndex, new ItemMenuBuilder(this, item));
            this.slotToQuestIndex.put(slotIndex, i);
            ++slotIndex;
        }

        if (this.currentPage > 0) {
            content.put(19, new ItemMenuBuilder(this, OMCRegistry.CUSTOM_ITEMS.QUESTS_LEFT_ARROW, meta ->
                    meta.displayName(TranslationManager.translation("feature.quests.menu.page.previous")
                            .decoration(TextDecoration.ITALIC, false))
            ));
        }

        if (this.currentPage < this.totalPages - 1) {
            content.put(25, new ItemMenuBuilder(this, OMCRegistry.CUSTOM_ITEMS.QUESTS_RIGHT_ARROW, meta ->
                    meta.displayName(TranslationManager.translation("feature.quests.menu.page.next")
                            .decoration(TextDecoration.ITALIC, false))
            ));
        }

        return content;
    }

    @Override
    public void onClose(InventoryCloseEvent event) {
        //empty
    }

    @Override
    public List<Integer> getTakableSlot() {
        return List.of();
    }

    private void updateInventory() {
        this.getInventory().clear();
        Map<Integer, ItemMenuBuilder> content = this.getContent();

        for (Map.Entry<Integer, ItemMenuBuilder> entry : content.entrySet()) {
            this.getInventory().setItem(entry.getKey(), entry.getValue());
        }
    }

    private void refresh() {
        this.updateInventory();
        (new QuestsMenu(this.getOwner(), target, this.currentPage)).open();
    }

    private ItemStack createQuestItem(Quest quest) {
        ItemStack item = quest.getIcon();
        ItemMeta meta = item.getItemMeta();
        this.createItems(quest, item, meta);
        return item;
    }

    @SuppressWarnings("UnstableApiUsage")
    private void createItems(Quest quest, ItemStack item, ItemMeta meta) {
        UUID playerUUID = this.target.getUniqueId();
        int currentTierIndex = quest.getCurrentTierIndex(playerUUID);
        int progress = quest.getProgress(playerUUID);
        int tiersTotal = quest.getTiers().size();
        QuestTier currentTier = quest.getCurrentTier(playerUUID);
        if (currentTier == null && !quest.isFullyCompleted(playerUUID)) {
            currentTierIndex = 0;
            currentTier = quest.getTiers().getFirst();
        }

        int target = quest.isFullyCompleted(playerUUID) ? (quest.getTiers().get(tiersTotal - 1)).target() : (currentTier != null ? currentTier.target() : 0);
        boolean isCompleted = quest.isFullyCompleted(playerUUID);

        Set<Integer> pendingQuestIndexes = quest.getPendingRewardTiers(playerUUID);
        boolean hasPendingRewards = quest.hasPendingRewards(playerUUID);

        if (isCompleted) {
            meta.addEnchant(Enchantment.SHARPNESS, 1, true);
            item.setData(DataComponentTypes.TOOLTIP_DISPLAY, TooltipDisplay.tooltipDisplay().addHiddenComponents(DataComponentTypes.ENCHANTMENTS).build());
        }

        Component bar = Component.text("                                ")
                .color(NamedTextColor.DARK_GRAY)
                .decorate(TextDecoration.STRIKETHROUGH)
                .decoration(TextDecoration.ITALIC, false);
        int tierIndex = quest.isFullyCompleted(playerUUID) ? currentTierIndex : currentTierIndex + 1;
        Component tierDisplay = TranslationManager.translation(
                "feature.quests.menu.tier_display",
                Component.text(tierIndex).color(NamedTextColor.WHITE),
                Component.text(tiersTotal).color(NamedTextColor.WHITE)
        ).color(NamedTextColor.GRAY);

        Component nameIcon = hasPendingRewards
                ? Component.text("⚑", NamedTextColor.LIGHT_PURPLE)
                : isCompleted
                ? Component.text("✓", NamedTextColor.GREEN)
                : Component.text("➤", NamedTextColor.GOLD);

        Component displayName = TranslationManager.translation(
                "feature.quests.menu.quest_item.name",
                nameIcon,
                Component.text(quest.getName()).color(NamedTextColor.YELLOW),
                tierDisplay
        ).decoration(TextDecoration.ITALIC, false);
        meta.displayName(displayName);
        List<Component> lore = new ArrayList<>();
        lore.add(bar);
        quest.getDescription(playerUUID).forEach(string -> {
            lore.add(Component.text(string, NamedTextColor.GRAY)
                    .decoration(TextDecoration.ITALIC, false));
        });
        lore.add(bar);

        if (hasPendingRewards) {
            lore.add(Component.text("✶ ", NamedTextColor.LIGHT_PURPLE)
                    .append(TranslationManager.translation("feature.quests.menu.pending_rewards.title")
                            .color(NamedTextColor.LIGHT_PURPLE))
                    .decoration(TextDecoration.ITALIC, false));
            for (Integer ti : pendingQuestIndexes) {
                if (ti < quest.getTiers().size()) {
                    QuestTier tier = quest.getTiers().get(ti);
                    lore.add(Component.text("  ➤ ", NamedTextColor.DARK_PURPLE)
                            .append(TranslationManager.translation(
                                    "feature.quests.menu.pending_rewards.tier",
                                    Component.text(ti + 1).color(NamedTextColor.LIGHT_PURPLE)
                            ).color(NamedTextColor.LIGHT_PURPLE))
                            .decoration(TextDecoration.ITALIC, false));

                    for (QuestReward reward : tier.getRewards()) {
                        if (reward instanceof QuestItemReward itemReward) {
                            ItemStack rewardItem = itemReward.getItemStack();
                            String itemName = PlainTextComponentSerializer.plainText().serialize(rewardItem.displayName());
                            lore.add(Component.text("    - ", NamedTextColor.DARK_GRAY)
                                    .append(Component.text(itemName, NamedTextColor.WHITE))
                                    .append(Component.space())
                                    .append(Component.text("x" + itemReward.getAmount(), NamedTextColor.GRAY))
                                    .decoration(TextDecoration.ITALIC, false));
                        } else if (reward instanceof QuestMoneyReward(double amount)) {
                            lore.add(Component.text("    - ", NamedTextColor.DARK_GRAY)
                                    .append(Component.text(EconomyManager.getFormattedSimplifiedNumber(amount), NamedTextColor.GOLD))
                                    .append(Component.space())
                                    .append(Component.text(EconomyManager.getEconomyIcon(), NamedTextColor.WHITE))
                                    .decoration(TextDecoration.ITALIC, false));
                        }
                    }
                }
            }
        }

        if (currentTier != null) {
            lore.add(Component.text("➤ ", NamedTextColor.GOLD)
                    .append(TranslationManager.translation("feature.quests.menu.rewards.title")
                            .color(NamedTextColor.YELLOW))
                    .decoration(TextDecoration.ITALIC, false));
            for (QuestReward reward : currentTier.getRewards()) {
                if (reward instanceof QuestItemReward itemReward) {
                    ItemStack rewardItem = itemReward.getItemStack();
                    String itemName = PlainTextComponentSerializer.plainText().serialize(rewardItem.displayName());
                    lore.add(Component.text("  - ", NamedTextColor.DARK_GRAY)
                            .append(Component.text(itemName, NamedTextColor.WHITE))
                            .append(Component.space())
                            .append(Component.text("x" + itemReward.getAmount(), NamedTextColor.GRAY))
                            .decoration(TextDecoration.ITALIC, false));
                } else if (reward instanceof QuestMoneyReward(double amount)) {
                    lore.add(Component.text("  - ", NamedTextColor.DARK_GRAY)
                            .append(Component.text(EconomyManager.getFormattedSimplifiedNumber(amount), NamedTextColor.GOLD))
                            .append(Component.space())
                            .append(Component.text(EconomyManager.getEconomyIcon(), NamedTextColor.WHITE))
                            .decoration(TextDecoration.ITALIC, false));
                }
            }
            lore.add(Component.empty());
        }

        if (isCompleted) {
            lore.add(TranslationManager.translation("feature.quests.menu.completed")
                    .color(NamedTextColor.GREEN)
                    .decoration(TextDecoration.ITALIC, false));
        } else if (currentTier != null) {
            int progressPercent = (int) Math.min(100.0F, Math.floor((double) progress / target * 100.0F));
            int barLength = 26;
            int filledLength = (int)((double)barLength * ((double)progress / (double)target));
            Component progressBar = Component.text("[", NamedTextColor.DARK_GRAY)
                    .decoration(TextDecoration.ITALIC, false);

            for (int i = 0; i < barLength; ++i) {
                NamedTextColor segmentColor = i < filledLength ? NamedTextColor.GREEN : NamedTextColor.DARK_GRAY;
                progressBar = progressBar.append(Component.text(" ", segmentColor)
                        .decoration(TextDecoration.STRIKETHROUGH, true)
                        .decoration(TextDecoration.ITALIC, false));
            }

            progressBar = progressBar.append(Component.text("]", NamedTextColor.DARK_GRAY)
                    .decoration(TextDecoration.ITALIC, false));

            lore.add(TranslationManager.translation(
                            "feature.quests.menu.progress",
                            Component.text(progress).color(NamedTextColor.YELLOW),
                            Component.text(target).color(NamedTextColor.YELLOW),
                            Component.text(progressPercent).color(NamedTextColor.GRAY)
                    ).color(NamedTextColor.WHITE)
                            .decoration(TextDecoration.ITALIC, false));
            lore.add(progressBar);
            lore.add(Component.empty());
            lore.add(Component.text("➤ ", NamedTextColor.GOLD)
                    .append(TranslationManager.translation("feature.quests.menu.current_objective")
                            .color(NamedTextColor.YELLOW))
                    .decoration(TextDecoration.ITALIC, false));
            quest.getDescription(playerUUID).forEach(string -> {
                lore.add(Component.text("  " + string, NamedTextColor.WHITE)
                        .decoration(TextDecoration.ITALIC, false));
            });
            lore.addAll(quest.getAdditionalLore());
            if (currentTier.getSteps() != null && !currentTier.getSteps().isEmpty()) {
                lore.add(Component.empty());
                lore.add(Component.text("◆ ", NamedTextColor.GOLD)
                        .append(TranslationManager.translation("feature.quests.menu.steps.title")
                                .color(NamedTextColor.YELLOW))
                        .decoration(TextDecoration.ITALIC, false));

                for (int i = 0; i < currentTier.getSteps().size(); i++) {
                    QuestStep step = currentTier.getSteps().get(i);
                    boolean stepCompleted = step.isCompleted(playerUUID);

                    Component stepIcon = Component.text(stepCompleted ? "✅" : "❌", stepCompleted ? NamedTextColor.GREEN : NamedTextColor.RED);
                    String stepDescription = step.getDescription();
                    lore.add(Component.text("  * ", NamedTextColor.GRAY)
                            .append(Component.text(stepDescription, NamedTextColor.GRAY))
                            .append(Component.space())
                            .append(stepIcon)
                            .decoration(TextDecoration.ITALIC, false));
                }
            }

            if (currentTierIndex < tiersTotal - 1) {
                QuestTier nextTier = quest.getTiers().get(currentTierIndex + 1);
                lore.add(bar);
                lore.add(Component.text("◇ ", NamedTextColor.GRAY)
                        .append(TranslationManager.translation("feature.quests.menu.next_tier.title")
                                .color(NamedTextColor.DARK_GRAY))
                        .decoration(TextDecoration.ITALIC, false));
                quest.getNextTierDescription(playerUUID).forEach(description -> {
                    lore.add(Component.text("  " + description, NamedTextColor.DARK_GRAY)
                            .decoration(TextDecoration.ITALIC, false));
                });

                if (nextTier.getSteps() != null && !nextTier.getSteps().isEmpty()) {
                    for (int i = 0; i < nextTier.getSteps().size(); i++) {
                        QuestStep step = nextTier.getSteps().get(i);
                        lore.add(Component.text("  ▪ " + step.getDescription(), NamedTextColor.DARK_GRAY)
                                .decoration(TextDecoration.ITALIC, false));
                    }
                }
            }
        }

        lore.add(bar);
        meta.lore(lore);
        item.setItemMeta(meta);
    }
}
