package fr.openmc.core.features.leaderboards.leaderboards;

import fr.openmc.core.bootstrap.integration.OMCLogger;
import fr.openmc.core.features.economy.EconomyManager;
import fr.openmc.core.features.events.contents.halloween.models.HalloweenData;
import fr.openmc.core.features.leaderboards.LeaderBoard;
import fr.openmc.core.features.leaderboards.LeaderBoardManager;
import fr.openmc.core.utils.cache.CachePlayerName;
import fr.openmc.core.utils.text.messages.TranslationManager;
import fr.openmc.core.utils.world.entities.TextDisplay;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.joml.Vector3f;

import java.util.concurrent.ThreadLocalRandom;

//TODO : DEVTEST
public class TestLeaderBoard extends LeaderBoard {

    public TestLeaderBoard(){
        FileConfiguration config = YamlConfiguration.loadConfiguration(LeaderBoardManager.getLeaderBoardFile());
        float scale = (float) config.getDouble("scale");
        super("TestLeaderBoard", config.getLocation("test-location"), null);
        if (this.location != null)
            this.display = new TextDisplay(createComponent(), this.location, new Vector3f(scale));
        else
            OMCLogger.warn("test-location is null");
    }

    @Override
    public void update() {
        if(this.display != null)
            this.display.updateText(createComponent());
    }

    private Component createComponent(){
        Component text = Component.text("-------- test --------")
                .color(NamedTextColor.YELLOW)
                .decorate(TextDecoration.BOLD);

        for (int i = 0; i < 10; i++){
            Component rank = Component.text("\n#" + i + " <-test->").color(LeaderBoardManager.getRankColor(i));
            text = text.append(rank);

        }

        return text.append(Component.text("\n" + ThreadLocalRandom.current().nextInt(0, 100 + 1)));
    }
}
