package fr.openmc.core.features.quests.menus;

import dev.xernas.menulib.Menu;
import dev.xernas.menulib.utils.InventorySize;
import fr.openmc.core.features.quests.QuestsManager;
import fr.openmc.core.features.quests.objects.Quest;
import fr.openmc.core.features.quests.objects.QuestStep;
import fr.openmc.core.features.quests.objects.QuestTier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import fr.openmc.core.features.quests.rewards.QuestItemReward;
import fr.openmc.core.features.quests.rewards.QuestMoneyReward;
import fr.openmc.core.features.quests.rewards.QuestReward;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

public class QuestsMenu extends Menu {
    private int currentPage;
    private static String TITLE;
    private static final ItemStack LEFT_ARROW;
    private static final ItemStack RIGHT_ARROW;
    private final int totalPages;
    private final QuestsManager questsManager;

    public QuestsMenu(Player player, int currentPage) {
        super(player);
        this.questsManager = QuestsManager.getInstance();
        this.currentPage = currentPage;
        this.totalPages = (int) Math.ceil(this.questsManager.getAllQuests().size() / 9.0F);
        TITLE = "Quests (" + currentPage + "/" + this.totalPages + ")";
    }

    public QuestsMenu(Player player) {
        this(player, 0);
        TITLE = "Quests";
    }

    public @NotNull String getName() {
        return TITLE;
    }

    public @NotNull InventorySize getInventorySize() {
        return InventorySize.NORMAL;
    }

    public void onInventoryClick(InventoryClickEvent event) {
        int slot = event.getSlot();
        if (slot == 18 && this.currentPage > 0) {
            --this.currentPage;
            this.refresh();
        } else if (slot == 26 && this.currentPage < this.totalPages - 1) {
            ++this.currentPage;
            this.refresh();
        }

    }

    public @NotNull Map<Integer, ItemStack> getContent() {
        Map<Integer, ItemStack> content = new HashMap<>();
        int startIndex = this.currentPage * 9;
        int endIndex = Math.min(startIndex + 9, this.questsManager.getAllQuests().size());
        int slotIndex = 9;

        for(int i = startIndex; i < endIndex; ++i) {
            Quest quest = this.questsManager.getAllQuests().get(i);
            ItemStack item = this.createQuestItem(quest);
            content.put(slotIndex, item);
            ++slotIndex;
        }

        if (this.currentPage > 0) {
            content.put(18, LEFT_ARROW);
        }

        if (this.currentPage < this.totalPages - 1) {
            content.put(26, RIGHT_ARROW);
        }

        return content;
    }

    private void updateInventory() {
        this.getInventory().clear();
        Map<Integer, ItemStack> content = this.getContent();

        for(Map.Entry<Integer, ItemStack> entry : content.entrySet()) {
            this.getInventory().setItem(entry.getKey(), entry.getValue());
        }

    }

    private void refresh() {
        this.updateInventory();
        (new QuestsMenu(this.getOwner(), this.currentPage)).open();
    }

    private ItemStack createQuestItem(Quest quest) {
        ItemStack item = quest.getIcon();
        ItemMeta meta = item.getItemMeta();
        this.createItems(quest, item, meta);
        return item;
    }

    private void createItems(Quest quest, ItemStack item, ItemMeta meta) {
        UUID playerUUID = this.getOwner().getUniqueId();
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
        if (isCompleted) {
            meta.addEnchant(Enchantment.SHARPNESS, 1, true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }

        Component bar = Component.text("§8§m                                §r");
        int var10000 = quest.isFullyCompleted(playerUUID) ? currentTierIndex : currentTierIndex + 1;
        String tierDisplay = "§7[§f" + var10000 + "§8/§f" + tiersTotal + "§7]";
        String nameIcon = isCompleted ? "§2✓" : "§6➤";
        meta.displayName(Component.text(nameIcon + " §e" + quest.getName() + " " + tierDisplay));
        List<Component> lore = new ArrayList<>();
        lore.add(bar);
        lore.add(Component.text("§7" + quest.getBaseDescription()));
        lore.add(bar);
        if (currentTier != null) {
            String rewardDesc = "";
            String rewardIcon = "§e⭐ ";
            if (currentTier.reward() instanceof QuestMoneyReward questMoneyReward) {
                rewardDesc = "§a" + questMoneyReward.getAmount() + "$";
            } else {
                QuestReward progressBar = currentTier.reward();
                if (progressBar instanceof QuestItemReward questItemReward) {
                    ItemStack rewardItem = new ItemStack(questItemReward.getMaterial());
                    MiniMessage miniMessage = MiniMessage.miniMessage();
                    Component defaultName = miniMessage.deserialize("<lang:" + rewardItem.getType().translationKey() + ">");
                    rewardDesc = "§fx" + questItemReward.getAmount() + " §b" + PlainTextComponentSerializer.plainText().serialize(defaultName);
                }
            }

            lore.add(Component.text(rewardIcon + "§6Récompense: " + rewardDesc));
            lore.add(Component.text(""));
        }

        if (isCompleted) {
            lore.add(Component.text("  §aQuête complétée !  "));
        } else if (currentTier != null) {
            int progressPercent = (int) Math.min(100.0F, Math.ceil((double) progress / target * 100.0F));
            int barLength = 20;
            int filledLength = (int)((double)barLength * ((double)progress / (double)target));
            StringBuilder progressBar = new StringBuilder();

            for(int i = 0; i < barLength; ++i) {
                progressBar.append(i < filledLength ? "§a■" : "§7□");
            }

            lore.add(Component.text("§fProgrès: §e" + progress + "§6/§e" + target + " §7(" + progressPercent + "%)"));
            lore.add(Component.text(progressBar.toString()));
            lore.add(Component.text(""));
            lore.add(Component.text("§6➤ §eObjectif actuel:"));
            lore.add(Component.text("  §f" + currentTier.description()));

            // Display steps if the tier has steps
            if (currentTier.getSteps() != null && !currentTier.getSteps().isEmpty()) {
                lore.add(Component.text(""));
                lore.add(Component.text("§6◆ §eAvancement:"));

                for (int i = 0; i < currentTier.getSteps().size(); i++) {
                    QuestStep step = currentTier.getSteps().get(i);
                    int stepProgress = step.getProgress(playerUUID);
                    boolean stepCompleted = step.isCompleted(playerUUID);

                    // Nouveau format d'affichage avec symboles de complétion
                    String stepIcon = stepCompleted ? "§a✅" : "§c❌";
                    String stepDescription = step.getDescription();
                    // Formater correctement avec le nom de l'étape et son statut
                    lore.add(Component.text("  §7* " + stepDescription + " " + stepIcon));
                }
            }

            if (currentTierIndex < tiersTotal - 1) {
                QuestTier nextTier = quest.getTiers().get(currentTierIndex + 1);
                lore.add(Component.text(""));
                lore.add(Component.text("§7◇ §8Prochain tier:"));
                lore.add(Component.text("  §8" + nextTier.description()));

                // Display next tier steps if they exist
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

    static {
        LEFT_ARROW = new ItemStack(Material.ARROW);
        ItemMeta leftArrowMeta = LEFT_ARROW.getItemMeta();
        leftArrowMeta.displayName(Component.text("Previous page"));
        LEFT_ARROW.setItemMeta(leftArrowMeta);
        RIGHT_ARROW = new ItemStack(Material.ARROW);
        ItemMeta rightArrowMeta = RIGHT_ARROW.getItemMeta();
        rightArrowMeta.displayName(Component.text("Next page"));
        RIGHT_ARROW.setItemMeta(rightArrowMeta);
    }
}
