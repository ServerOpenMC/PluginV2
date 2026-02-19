package fr.openmc.core.features.displays.bossbar.contents;

import fr.openmc.core.features.displays.bossbar.BaseBossbar;
import fr.openmc.core.features.dream.DreamUtils;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;

import java.util.List;

public class MainBossbar extends BaseBossbar {

    private static int indexMessage = 0;

    @Override
    protected String id() {
        return "omc:help";
    }

    @Override
    protected void update(Player player, BossBar bar) {
        indexMessage = (indexMessage + 1) % HelpConfigManager.getHelpMessages().size();

        bar.name(HelpConfigManager.getHelpMessages().get(indexMessage));
    }

    @Override
    protected BossBar.Color color(Player player) {
        return BossBar.Color.RED;
    }

    @Override
    protected BossBar.Overlay style(Player player) {
        return BossBar.Overlay.PROGRESS;
    }

    @Override
    protected boolean shouldDisplay(Player player) {
        return !DreamUtils.isInDreamWorld(player);
    }

    @Override
    protected int weight() {
        return 10;
    }

    @Override
    protected Integer updateInterval() {
        return 20;
    }
}
