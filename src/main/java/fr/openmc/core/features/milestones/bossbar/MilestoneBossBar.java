package fr.openmc.core.features.milestones.bossbar;

import fr.openmc.core.features.displays.bossbar.BaseBossbar;
import fr.openmc.core.features.milestones.MilestoneStep;
import fr.openmc.core.features.milestones.MilestonesManager;
import fr.openmc.core.features.milestones.models.Milestone;
import fr.openmc.core.features.milestones.quests.MilestoneQuest;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

public class MilestoneBossBar extends BaseBossbar {

    private final Milestone<?> milestone;

    public MilestoneBossBar(Milestone<?> milestone) {
        this.milestone = milestone;
    }
    public static final String PLACEHOLDER_MILESTONE_BOSSBAR = "feature.milestones.placeholder.1";
    public static final String PLACEHOLDER_MILESTONE_BOSSBAR_PROGRESS = "feature.milestones.placeholder.2";

    @Override
    protected String id() {
        return "omc:" + milestone.getType().toString() + "_milestone";
    }

    @Override
    protected void update(Player player, BossBar bar) {
        int currentStep = MilestonesManager.getPlayerStep(milestone.getType(), player);

        MilestoneStep[] steps = milestone.getStepEnum();

        if (currentStep >= steps.length) return; // pas affiché par défaut (shouldDisplay())

        int maxStep = steps.length;
        MilestoneStep step = steps[currentStep];
        MilestoneQuest quest = step.getQuest();

        int progress = quest.getProgress(player.getUniqueId());
        int goal = quest.getCurrentTarget(player.getUniqueId());

        Component questName = quest.getName(player.getUniqueId());

        if (goal <= 1) {
            bar.name(TranslationManager.translation(PLACEHOLDER_MILESTONE_BOSSBAR,
                            Component.text(currentStep + 1),
                            questName).color(milestone.getBossBarOptions().textColor()));

            bar.progress((float) currentStep / maxStep);
        } else {
            bar.name(TranslationManager.translation(PLACEHOLDER_MILESTONE_BOSSBAR_PROGRESS,
                            Component.text(currentStep + 1),
                            questName,
                            Component.text(progress),
                            Component.text(goal)
                    ).color(milestone.getBossBarOptions().textColor()));

            bar.progress((float) progress / goal);
        }
    }

    @Override
    protected Float progress(Player player) {
        int currentStep = MilestonesManager.getPlayerStep(milestone.getType(), player);

        MilestoneStep[] steps = milestone.getStepEnum();

        if (currentStep >= steps.length) return null;

        int maxStep = steps.length;
        MilestoneStep step = steps[currentStep];
        MilestoneQuest quest = step.getQuest();

        int progress = quest.getProgress(player.getUniqueId());
        int goal = quest.getCurrentTarget(player.getUniqueId());

        if (goal <= 1) {
            return (float) currentStep / maxStep;
        } else {
            return (float) progress / goal;
        }
    }

    @Override
    protected BossBar.Color color(Player player) {
        return milestone.getBossBarOptions().color();
    }

    @Override
    protected BossBar.Overlay style(Player player) {
        return milestone.getBossBarOptions().style();
    }

    @Override
    protected boolean shouldDisplay(Player player) {
        return milestone.shouldDisplayBossBar(player);
    }

    @Override
    protected int weight() {
        return 5;
    }

    @Override
    protected Integer updateInterval() {
        return 2;
    }
}
