package fr.openmc.core.features.quests.objects;

import fr.openmc.core.features.quests.rewards.QuestReward;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


@Getter
public class Quest {

    private final String name;
    private final String baseDescription;
    private final ItemStack icon;
    private final boolean isLargeActionBar;
    private final List<QuestTier> tiers = new ArrayList<>();
    private final Map<UUID, Integer> progress = new ConcurrentHashMap<>();
    private final Map<UUID, Boolean> progressLock = new ConcurrentHashMap<>();
    private final Map<UUID, Integer> currentTier = new ConcurrentHashMap<>();
    private final Map<UUID, Set<Integer>> completedTiers = new ConcurrentHashMap<>();

    /**
     * Constructor for Quest.
     *
     * @param name            The name of the quest
     * @param baseDescription The base description of the quest
     * @param icon            The icon representing the quest - ItemStack
     */
    public Quest(String name, String baseDescription, ItemStack icon) {
        this.name = name;
        this.baseDescription = baseDescription;
        this.icon = icon;
        this.isLargeActionBar = false;
    }

    /**
     * Constructor for Quest.
     *
     * @param name            The name of the quest
     * @param baseDescription The base description of the quest
     * @param icon            The icon representing the quest - Material
     */
    public Quest(String name, String baseDescription, Material icon) {
        this.name = name;
        this.baseDescription = baseDescription;
        this.icon = new ItemStack(icon);
        this.isLargeActionBar = false;
    }

    /**
     * Constructor for Quest.
     *
     * @param name            The name of the quest
     * @param baseDescription The base description of the quest
     * @param icon            The icon representing the quest - ItemStack
     * @param isLargeActionBar If true, the quest will be displayed in large action bar
     */
    public Quest(String name, String baseDescription, ItemStack icon, boolean isLargeActionBar) {
        this.name = name;
        this.baseDescription = baseDescription;
        this.icon = icon;
        this.isLargeActionBar = isLargeActionBar;
    }

    /**
     * Constructor for Quest.
     *
     * @param name            The name of the quest
     * @param baseDescription The base description of the quest
     * @param icon            The icon representing the quest - Material
     * @param isLargeActionBar If true, the quest will be displayed in large action bar
     */
    public Quest(String name, String baseDescription, Material icon, boolean isLargeActionBar) {
        this.name = name;
        this.baseDescription = baseDescription;
        this.icon = new ItemStack(icon);
        this.isLargeActionBar = isLargeActionBar;
    }

    /**
     * Add a tier to the quest.
     * @param tier The tier to add
     */
    public void addTier(QuestTier tier) {
        this.tiers.add(tier);
    }

    /**
     * Add multiple tiers to the quest.
     * @param tiers The tiers to add
     */
    public void addTiers(QuestTier... tiers) {
        Collections.addAll(this.tiers, tiers);
    }

    /**
     * Check if the quest is fully completed for a player.
     *
     * @param playerUUID The UUID of the player
     * @return true if the quest is fully completed, false otherwise
     */
    public boolean isFullyCompleted(UUID playerUUID) {
        int playerTier = this.currentTier.getOrDefault(playerUUID, 0);
        return playerTier >= this.tiers.size();
    }

    /**
     * Get the current progress of the quest for a player.
     *
     * @param playerUUID The UUID of the player
     * @return the current progress of the quest for the player, or 0 if not found
     */
    public int getProgress(UUID playerUUID) {
        return this.progress.getOrDefault(playerUUID, 0);
    }

    /**
     * Get the current tier index of the quest for a player.
     *
     * @param playerUUID The UUID of the player
     * @return the current tier index of the quest for the player, or 0 if not found
     */
    public int getCurrentTierIndex(UUID playerUUID) {
        int tierIndex = this.currentTier.getOrDefault(playerUUID, 0);
        return Math.min(tierIndex, this.tiers.size());
    }

    /**
     * Get the current tier of the quest for a player.
     *
     * @param playerUUID The UUID of the player
     * @return the current tier of the quest for the player, or null if not found
     */
    public QuestTier getCurrentTier(UUID playerUUID) {
        int tierIndex = this.getCurrentTierIndex(playerUUID);
        return tierIndex < this.tiers.size() ? this.tiers.get(tierIndex) : null;
    }

    /**
     * Get the current target of the quest for a player.
     *
     * @param playerUUID The UUID of the player
     * @return the current target of the quest for the player, or 0 if not found
     */
    public int getCurrentTarget(UUID playerUUID) {
        QuestTier tier = this.getCurrentTier(playerUUID);
        return tier != null ? tier.getTarget() : 0;
    }

    /**
     * Get the next tier index of the quest for a player.
     *
     * @param playerUUID The UUID of the player
     * @return the next tier index of the quest for the player, or 0 if not found
     */
    public int getNextTierIndex(UUID playerUUID) {
        int tierIndex = this.getCurrentTierIndex(playerUUID);
        return Math.min(tierIndex + 1, this.tiers.size());
    }

    /**
     * Get the next tier target of the quest for a player.
     *
     * @param playerUUID The UUID of the player
     * @return the next tier target of the quest for the player, or 0 if not found
     */
    public int getNextTierTarget(UUID playerUUID) {
        int nextTierIndex = this.getNextTierIndex(playerUUID);
        return nextTierIndex < this.tiers.size() ? this.tiers.get(nextTierIndex).getTarget() : 0;
    }

    /**
     * Get the description of the quest for a player.
     * <p>
     * The description can contain the placeholder {target} which will be replaced by the current target number.
     * And the placeholder {s} which will be replaced by "s" if the current target is greater than 1.
     * @param playerUUID The UUID of the player
     * @return the description of the quest for the player
     */
    public String getDescription(UUID playerUUID) {
        return this.baseDescription
                .replace("{target}", String.valueOf(this.getCurrentTarget(playerUUID)))
                .replace("{s}", this.getCurrentTarget(playerUUID) > 1 ? "s" : "");
    }

    /**
     * Get the next tier description of the quest for a player.
     * <p>
     * The description can contain the placeholder {target} which will be replaced by the next tier target number.
     * And the placeholder {s} which will be replaced by "s" if the next tier target is greater than 1.
     * @param playerUUID The UUID of the player
     * @return the next tier description of the quest for the player
     */
    public String getNextTierDescription(UUID playerUUID) {
        return this.baseDescription
                .replace("{target}", String.valueOf(this.getNextTierTarget(playerUUID)))
                .replace("{s}", this.getNextTierTarget(playerUUID) > 1 ? "s" : "");
    }

    /**
     * Complete the current tier for a player.
     * <p>
     * This method will give the rewards of the tier to the player and update the current tier.
     * @param uuid the UUID of the player
     * @param tierIndex the index of the tier to complete
     */
    public void completeTier(UUID uuid, int tierIndex) {
        Set<Integer> playerCompletedTiers = this.completedTiers.computeIfAbsent(uuid, k -> new HashSet<>());
        if (!playerCompletedTiers.contains(tierIndex) && tierIndex < this.tiers.size() && !this.isFullyCompleted(uuid)) {
            playerCompletedTiers.add(tierIndex);
            this.currentTier.put(uuid, Math.min(tierIndex + 1, this.tiers.size()));
            Player player = Bukkit.getPlayer(uuid);
            if (player != null && player.isOnline()) {
                QuestTier tier = this.tiers.get(tierIndex);
                for (QuestReward reward : tier.getRewards()) {
                    reward.giveReward(player);
                }
                boolean isLastTier = tierIndex == this.tiers.size() - 1;
                Component titleMain = Component.text(
                                "✦ ", TextColor.color(15770808))
                        .append(Component.text(isLastTier
                                        ? "Quête terminée !"
                                        : "Palier " + (tierIndex + 1) + " terminé !",
                                TextColor.color(6216131)))
                        .append(Component.text(" ✦", TextColor.color(15770808)));

                Component titleSub = Component.text(this.name, TextColor.color(8087790));
                String message = isLastTier ? "§6★ §aQuête terminée ! §e" + this.name + " §7est maintenant complète !" : "§e★ §aPalier " + (tierIndex + 1) + " §7de §e" + this.name + " §avalidé !";

                player.showTitle(Title.title(
                        titleMain,
                        titleSub,
                        Title.Times.times(Duration.ofMillis(300L), Duration.ofSeconds(3L), Duration.ofMillis(500L)))
                );
                player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0F, 1.2F);
                player.playSound(player.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 0.7F, 1.1F);
                MessagesManager.sendMessage(player, Component.text(message), Prefix.QUEST, MessageType.SUCCESS, true);
            }
        }
    }

    /**
     * Complete a specific step of the current tier for a player.
     * <p>
     * This method will check if the step is completed and if so, it will complete the step and check if the tier is completed.
     * @param playerUUID the UUID of the player
     * @param stepIndex the index of the step to complete
     */
    public void completeStep(UUID playerUUID, int stepIndex) {
        QuestTier currentTier = getCurrentTier(playerUUID);
        if (currentTier == null || stepIndex >= currentTier.getSteps().size()) {
            return;
        }

        Player player = Bukkit.getPlayer(playerUUID);
        if (player == null || !player.isOnline()) {
            return;
        }

        QuestStep step = currentTier.getSteps().get(stepIndex);
        if (step.isCompleted(playerUUID)) {
            int tierIndex = getCurrentTierIndex(playerUUID);
            String stepName = "Étape " + (stepIndex + 1);

            String message = "§a✓ §7" + stepName + " §7de §e" + this.name + " §avalidée !";
            MessagesManager.sendMessage(player, Component.text(message), Prefix.QUEST, MessageType.SUCCESS, true);
            player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0F, 2.0F);

            if (currentTier.areStepsCompleted(playerUUID) && currentTier.isRequireStepsCompletion()) {
                int currentProgress = getProgress(playerUUID);
                if (currentProgress >= currentTier.target()) {
                    completeTier(playerUUID, tierIndex);
                }
            }
        }
    }

    /**
     * Check if the current tier is completed for a player.
     * <p>
     * This method will check if the current progress is greater than or equal to the target of the current tier.
     * If so, it will complete the tier.
     * @param playerUUID the UUID of the player
     */
    private void checkTierCompletion(UUID playerUUID) {
        int playerTier = this.currentTier.getOrDefault(playerUUID, 0);
        if (playerTier < this.tiers.size()) {
            QuestTier tier = this.tiers.get(playerTier);
            int currentProgress = this.progress.getOrDefault(playerUUID, 0);
            Set<Integer> playerCompletedTiers = this.completedTiers.computeIfAbsent(playerUUID, (k) -> new HashSet<>());

            if (currentProgress >= tier.target() && !playerCompletedTiers.contains(playerTier) &&
                    (playerTier == 0 || playerCompletedTiers.contains(playerTier - 1))) {

                if (!tier.isRequireStepsCompletion() || tier.areStepsCompleted(playerUUID)) {
                    this.completeTier(playerUUID, playerTier);
                }
            }
        }
    }

    /**
     * Increment the progress of the quest for a player.
     * <p>
     * This method will check if the quest is fully completed and if not, it will increment the progress.
     * @param playerUUID The UUID of the player
     */
    public void incrementProgress(UUID playerUUID) {
        incrementProgress(playerUUID, 1);
    }

    /**
     * Increment the progress of the quest for a player by a specified amount.
     * <p>
     * This method will check if the quest is fully completed and if not, it will increment the progress.
     * @param playerUUID The UUID of the player
     * @param amount The amount to increment the progress by
     */
    public void incrementProgress(UUID playerUUID, int amount) {
        if (!this.isFullyCompleted(playerUUID) && !this.progressLock.getOrDefault(playerUUID, false)) {
            this.progressLock.put(playerUUID, true);

            try {
                Player player = Bukkit.getPlayer(playerUUID);
                if (player != null && player.getGameMode() != GameMode.SURVIVAL) return;
                int currentProgress = this.progress.getOrDefault(playerUUID, 0);
                int newProgress = currentProgress + amount;
                int currentTarget = this.getCurrentTarget(playerUUID);
                if (newProgress >= currentTarget) {
                    newProgress = currentTarget;
                }

                if (currentProgress < currentTarget) {
                    this.progress.put(playerUUID, newProgress);
                    this.checkTierCompletion(playerUUID);

                    if (player != null && player.isOnline()) {
                        if (this.isLargeActionBar && newProgress % 50 != 0) return;
                        Component actionBar = Component.text()
                                .append(MiniMessage.miniMessage().deserialize(Prefix.QUEST.getPrefix()))
                                .append(Component.text(" » ", NamedTextColor.DARK_GRAY))
                                .append(Component.text("Progression de la quête ", NamedTextColor.GRAY))
                                .append(Component.text(this.name, NamedTextColor.WHITE))
                                .append(Component.text(" : ", NamedTextColor.GRAY))
                                .append(Component.text(newProgress + "/" + currentTarget, NamedTextColor.GOLD))
                                .build();

                        player.sendActionBar(actionBar);
                    }
                }
            } finally {
                this.progressLock.put(playerUUID, false);
            }
        }
    }

    /**
     * Increment the progress of a specific step of the current tier for a player.
     * <p>
     * This method will check if the step is completed and if so, it will complete the step and check if the tier is completed.
     * @param playerUUID the UUID of the player
     * @param stepIndex the index of the step to increment
     */
    public void incrementStepProgress(UUID playerUUID, int stepIndex) {
        incrementStepProgress(playerUUID, stepIndex, 1);
    }

    /**
     * Increment the progress of a specific step of the current tier for a player by a specified amount.
     * <p>
     * This method will check if the step is completed and if so, it will complete the step and check if the tier is completed.
     * @param playerUUID the UUID of the player
     * @param stepIndex the index of the step to increment
     * @param amount The amount to increment the progress by
     */
    public void incrementStepProgress(UUID playerUUID, int stepIndex, int amount) {
        QuestTier tier = getCurrentTier(playerUUID);
        if (tier != null && stepIndex >= 0 && stepIndex < tier.getSteps().size()) {
            QuestStep step = tier.getSteps().get(stepIndex);
            step.incrementProgress(playerUUID, amount);
            if (step.isCompleted(playerUUID)) {
                completeStep(playerUUID, stepIndex);
            }
        }
    }

    /**
     * Increment the progress of a specific step of the current tier for a player by a specified amount.
     * <p>
     * This method will check if the step is completed and if so, it will complete the step and check if the tier is completed.
     * @param playerUUID the UUID of the player
     * @param stepDescription the description of the step to increment
     * @param amount The amount to increment the progress by
     */
    public void incrementStepProgressByDescription(UUID playerUUID, String stepDescription, int amount) {
        QuestTier tier = getCurrentTier(playerUUID);
        if (tier != null) {
            List<QuestStep> steps = tier.getSteps();
            for (int i = 0; i < steps.size(); i++) {
                QuestStep step = steps.get(i);
                if (step.getDescription().equals(stepDescription)) {
                    step.incrementProgress(playerUUID, amount);
                    if (step.isCompleted(playerUUID)) {
                        completeStep(playerUUID, i);
                    }
                    break;
                }
            }
        }
    }
}