package fr.openmc.core.features.milestones.bossbar;

import fr.openmc.core.features.displays.bossbar.BaseBossbar;
import fr.openmc.core.features.milestones.*;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

public class MilestoneBossBar extends BaseBossbar {

    private final Milestone<?> milestone;

    public MilestoneBossBar(Milestone<?> milestone) {
        this.milestone = milestone;
    }
    public static final String PLACEHOLDER_MILESTONE_BOSSBAR = "§6Étape %s : %s";
    public static final String PLACEHOLDER_MILESTONE_BOSSBAR_PROGRESS = "§6Étape %s : %s (%s/%s)";

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

        String questName = quest.getName(player.getUniqueId());

        if (goal <= 1) {
            bar.name(Component.text(
                    PLACEHOLDER_MILESTONE_BOSSBAR.formatted(currentStep + 1, questName)
            ));

            bar.progress((float) currentStep / maxStep);
        } else {
            bar.name(Component.text(
                    PLACEHOLDER_MILESTONE_BOSSBAR_PROGRESS.formatted(
                            currentStep + 1,
                            quest.getName(player.getUniqueId()),
                            progress,
                            goal
                    )
            ));

            bar.progress((float) progress / goal);
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
