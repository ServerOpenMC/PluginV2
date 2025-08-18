package fr.openmc.core.features.quests.menus;

import fr.openmc.api.menulib.Menu;
import fr.openmc.api.menulib.utils.InventorySize;
import fr.openmc.api.menulib.utils.ItemBuilder;
import fr.openmc.core.features.economy.EconomyManager;
import fr.openmc.core.features.quests.QuestsManager;
import fr.openmc.core.features.quests.objects.Quest;
import fr.openmc.core.features.quests.objects.QuestStep;
import fr.openmc.core.features.quests.objects.QuestTier;
import fr.openmc.core.features.quests.rewards.QuestItemReward;
import fr.openmc.core.features.quests.rewards.QuestMoneyReward;
import fr.openmc.core.features.quests.rewards.QuestReward;
import fr.openmc.core.items.CustomItemRegistry;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemFlag;
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

    public @NotNull String getName() {
        return "Menu des Quêtes";
    }

    @Override
    public String getTexture() {
        return PlaceholderAPI.setPlaceholders(getOwner(), "§r§f%img_offset_-25%%img_quests_menu%");
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

    public @NotNull Map<Integer, ItemBuilder> getContent() {
        Map<Integer, ItemBuilder> content = new HashMap<>();
        slotToQuestIndex.clear();

        int startIndex = this.currentPage * 9;
        int endIndex = Math.min(startIndex + 9, QuestsManager.getAllQuests().size());
        int slotIndex = 9;

        for(int i = startIndex; i < endIndex; ++i) {
            Quest quest = QuestsManager.getAllQuests().get(i);
            ItemStack item = this.createQuestItem(quest);
            content.put(slotIndex, new ItemBuilder(this, item));
            this.slotToQuestIndex.put(slotIndex, i);
            ++slotIndex;
        }

        if (this.currentPage > 0) {
            content.put(19, new ItemBuilder(this, Objects.requireNonNull(CustomItemRegistry.getByName("omc_quests:quests_left_arrow")).getBest()));
        }

        if (this.currentPage < this.totalPages - 1) {
            content.put(25, new ItemBuilder(this, Objects.requireNonNull(CustomItemRegistry.getByName("omc_quests:quests_right_arrow")).getBest()));
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
        Map<Integer, ItemBuilder> content = this.getContent();

        for (Map.Entry<Integer, ItemBuilder> entry : content.entrySet()) {
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
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }

        Component bar = Component.text("§8§m                                §r");
        int var10000 = quest.isFullyCompleted(playerUUID) ? currentTierIndex : currentTierIndex + 1;
        String tierDisplay = "§7[§f" + var10000 + "§8/§f" + tiersTotal + "§7]";

        String nameIcon;
        if (hasPendingRewards)
            nameIcon = "§d⚑";
        else if (isCompleted)
            nameIcon = "§a✓";
        else
            nameIcon = "§6➤";

        meta.displayName(Component.text(nameIcon + " §e" + quest.getName() + " " + tierDisplay));
        List<Component> lore = new ArrayList<>();
        lore.add(bar);
        quest.getDescription(playerUUID).forEach(string -> {
            lore.add(Component.text("§7" + string));
        });
        lore.add(bar);

        if (hasPendingRewards) {
            lore.add(Component.text("§d✶ §dRécompenses en attente:"));
            for (Integer tierIndex : pendingQuestIndexes) {
                if (tierIndex < quest.getTiers().size()) {
                    QuestTier tier = quest.getTiers().get(tierIndex);
                    lore.add(Component.text("  §5➤ §dPalier " + (tierIndex + 1) + ":"));

                    for (QuestReward reward : tier.getRewards()) {
                        if (reward instanceof QuestItemReward itemReward) {
                            ItemStack rewardItem = itemReward.getItemStack();
                            String itemName = PlainTextComponentSerializer.plainText().serialize(rewardItem.displayName());
                            lore.add(Component.text("    §7- §f" + itemName + " §7x" + itemReward.getAmount()));
                        } else if (reward instanceof QuestMoneyReward moneyReward) {
                            lore.add(Component.text("    §7- §6" + EconomyManager.getFormattedSimplifiedNumber(moneyReward.getAmount()) + " §f" + EconomyManager.getEconomyIcon()));
                        }
                    }
                }
            }
        }

        if (currentTier != null) {
            lore.add(Component.text("§6➤ §eRécompenses:"));
            for (QuestReward reward : currentTier.getRewards()) {
                if (reward instanceof QuestItemReward itemReward) {
                    ItemStack rewardItem = itemReward.getItemStack();
                    String itemName = PlainTextComponentSerializer.plainText().serialize(rewardItem.displayName());
                    lore.add(Component.text("  §7- §f" + itemName + " §7x" + itemReward.getAmount()));
                } else if (reward instanceof QuestMoneyReward moneyReward) {
                    lore.add(Component.text("  §7- §6" + EconomyManager.getFormattedSimplifiedNumber(moneyReward.getAmount()) + " §f" + EconomyManager.getEconomyIcon()));
                }
            }
            lore.add(Component.empty());
        }

        if (isCompleted) {
            lore.add(Component.text("  §aQuête complétée !  "));
        } else if (currentTier != null) {
            int progressPercent = (int) Math.min(100.0F, Math.floor((double) progress / target * 100.0F));
            int barLength = 26;
            int filledLength = (int)((double)barLength * ((double)progress / (double)target));
            StringBuilder progressBar = new StringBuilder();
            progressBar.append("§8[§m");

            for(int i = 0; i < barLength; ++i) {
                progressBar.append(i < filledLength ? "§a§m " : "§8§m ");
            }

            progressBar.append("§m§8]");

            lore.add(Component.text("§fProgrès: §e" + progress + "§6/§e" + target + " §7(" + progressPercent + "%)"));
            lore.add(Component.text(progressBar.toString()));
            lore.add(Component.empty());
            lore.add(Component.text("§6➤ §eObjectif actuel:"));
            quest.getDescription(playerUUID).forEach(string -> {
                lore.add(Component.text("  §f" + string));
            });
            if (currentTier.getSteps() != null && !currentTier.getSteps().isEmpty()) {
                lore.add(Component.empty());
                lore.add(Component.text("§6◆ §eAvancement:"));

                for (int i = 0; i < currentTier.getSteps().size(); i++) {
                    QuestStep step = currentTier.getSteps().get(i);
                    boolean stepCompleted = step.isCompleted(playerUUID);

                    String stepIcon = stepCompleted ? "§a✅" : "§c❌";
                    String stepDescription = step.getDescription();
                    lore.add(Component.text("  §7* " + stepDescription + " " + stepIcon));
                }
            }

            if (currentTierIndex < tiersTotal - 1) {
                QuestTier nextTier = quest.getTiers().get(currentTierIndex + 1);
                lore.add(bar);
                lore.add(Component.text("§7◇ §8Prochain tier:"));
                lore.add(Component.text("  §8" + quest.getNextTierDescription(playerUUID)));

                if (nextTier.getSteps() != null && !nextTier.getSteps().isEmpty()) {
                    for (int i = 0; i < nextTier.getSteps().size(); i++) {
                        QuestStep step = nextTier.getSteps().get(i);
                        lore.add(Component.text("  §8▪ " + step.getDescription()));
                    }
                }
            }
        }

        lore.add(bar);
        meta.lore(lore);
        item.setItemMeta(meta);
    }
}
