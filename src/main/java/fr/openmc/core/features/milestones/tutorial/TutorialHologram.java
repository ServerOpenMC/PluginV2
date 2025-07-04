package fr.openmc.core.features.milestones.tutorial;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.utils.entities.TextDisplay;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.joml.Vector3f;

import java.io.File;

public class TutorialHologram {
    private static TextDisplay tutorialHologram;
    private static Location tutorialHologramLocation;
    private static final File leaderBoardFile = new File(OMCPlugin.getInstance().getDataFolder() + "/data/holograms", "tutorial.yml");

    public TutorialHologram(Location location, float scale) {
        loadTutorialHologramConfig();
        tutorialHologramLocation = location;
        Component title = Component.text(":openmc:");

        Component line = Component.text("§7Bienvenue sur OpenMC !")
                .append(Component.newline())
                .append(Component.text("§7Pour commencer, consultez le §6§lWiki§7."))
                .append(Component.newline())
                .append(Component.text("§7Vous pouvez aussi consulter le §6§lDiscord§7 pour de l'aide."));
        tutorialHologram = new TextDisplay(title.append(line), location, new Vector3f(scale));
    }

    private static void loadTutorialHologramConfig() {
        if (!leaderBoardFile.exists()) {
            leaderBoardFile.getParentFile().mkdirs();
            OMCPlugin.getInstance().saveResource("data/leaderboards.yml", false);
        }
        FileConfiguration leaderBoardConfig = YamlConfiguration.loadConfiguration(leaderBoardFile);
        tutorialHologramLocation = leaderBoardConfig.getLocation("contributors-location");
    }
}
