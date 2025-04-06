package fr.openmc.core.features.quests.objects;

import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
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
    private final List<QuestTier> tiers = new ArrayList<>();
    private final Map<UUID, Integer> progress = new ConcurrentHashMap<>();
    private final Map<UUID, Boolean> progressLock = new ConcurrentHashMap<>();
    private final Map<UUID, Integer> currentTier = new ConcurrentHashMap<>();
    private final Map<UUID, Set<Integer>> completedTiers = new ConcurrentHashMap<>();

    public Quest(String name, String baseDescription, ItemStack icon) {
        this.name = name;
        this.baseDescription = baseDescription;
        this.icon = icon;
    }

    public void addTier(QuestTier tier) {
        this.tiers.add(tier);
    }

    public boolean isFullyCompleted(UUID playerUUID) {
        int playerTier = this.currentTier.getOrDefault(playerUUID, 0);
        return playerTier >= this.tiers.size();
    }

    public int getProgress(UUID playerUUID) {
        return this.progress.getOrDefault(playerUUID, 0);
    }

    public int getCurrentTierIndex(UUID playerUUID) {
        int tierIndex = this.currentTier.getOrDefault(playerUUID, 0);
        return Math.min(tierIndex, this.tiers.size());
    }

    public QuestTier getCurrentTier(UUID playerUUID) {
        int tierIndex = this.getCurrentTierIndex(playerUUID);
        return tierIndex < this.tiers.size() ? this.tiers.get(tierIndex) : null;
    }

    public int getCurrentTarget(UUID playerUUID) {
        QuestTier tier = this.getCurrentTier(playerUUID);
        return tier != null ? tier.target() : 0;
    }

    public String getDescription() {
        StringBuilder description = new StringBuilder(this.baseDescription + "\n");

        for(int i = 0; i < this.tiers.size(); ++i) {
            QuestTier tier = this.tiers.get(i);
            description.append("§7Palier ").append(i + 1).append(": ").append(tier.description()).append("\n");
        }

        return description.toString();
    }

    public void completeTier(UUID uuid, int tierIndex) {
        Set<Integer> playerCompletedTiers = this.completedTiers.computeIfAbsent(uuid, k -> new HashSet<>());
        if (!playerCompletedTiers.contains(tierIndex) && tierIndex < this.tiers.size() && !this.isFullyCompleted(uuid)) {
            playerCompletedTiers.add(tierIndex);
            this.currentTier.put(uuid, Math.min(tierIndex + 1, this.tiers.size()));
            Player player = Bukkit.getPlayer(uuid);
            if (player != null && player.isOnline()) {
                QuestTier tier = this.tiers.get(tierIndex);
                tier.reward().giveReward(player);
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

    private void checkTierCompletion(UUID playerUUID) {
        int playerTier = this.currentTier.getOrDefault(playerUUID, 0);
        if (playerTier < this.tiers.size()) {
            QuestTier tier = this.tiers.get(playerTier);
            int currentProgress = this.progress.getOrDefault(playerUUID, 0);
            Set<Integer> playerCompletedTiers = this.completedTiers.computeIfAbsent(playerUUID, (k) -> new HashSet<>());
            if (currentProgress == tier.target() && !playerCompletedTiers.contains(playerTier) && (playerTier == 0 || playerCompletedTiers.contains(playerTier - 1))) {
                this.completeTier(playerUUID, playerTier);
            }

        }
    }

    public void incrementProgress(UUID playerUUID, int amount) {
        if (!this.isFullyCompleted(playerUUID) && !(Boolean)this.progressLock.getOrDefault(playerUUID, false)) {
            this.progressLock.put(playerUUID, true);

            try {
                int currentProgress = this.progress.getOrDefault(playerUUID, 0);
                int newProgress = currentProgress + amount;
                int currentTarget = this.getCurrentTarget(playerUUID);
                if (newProgress >= currentTarget) {
                    newProgress = currentTarget;
                }

                if (currentProgress < currentTarget) {
                    this.progress.put(playerUUID, newProgress);
                    this.checkTierCompletion(playerUUID);
                    return;
                }
            } finally {
                this.progressLock.put(playerUUID, false);
            }

        }
    }
}
