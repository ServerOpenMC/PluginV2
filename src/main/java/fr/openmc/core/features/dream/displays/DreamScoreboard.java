package fr.openmc.core.features.dream.displays;

import dev.lone.itemsadder.api.FontImages.FontImageWrapper;
import fr.openmc.api.scoreboard.SternalBoard;
import fr.openmc.core.features.displays.scoreboards.BaseScoreboard;
import fr.openmc.core.features.dream.DreamManager;
import fr.openmc.core.features.dream.DreamUtils;
import fr.openmc.core.features.dream.generation.DreamBiome;
import fr.openmc.core.features.dream.generation.DreamDimensionManager;
import fr.openmc.core.features.dream.generation.structures.DreamStructure;
import fr.openmc.core.features.dream.generation.structures.DreamStructuresManager;
import fr.openmc.core.features.dream.models.db.DreamPlayer;
import fr.openmc.core.utils.DateUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

import static fr.openmc.core.utils.messages.MessagesManager.textToSmall;
import static net.kyori.adventure.text.Component.empty;
import static net.kyori.adventure.text.Component.text;

/**
 * Classe utilitaire pour la mise à jour du Scoreboard dans la Dimension des Rêves.
 *
 * <p>Cette classe met à jour le Scoreboard d'un joueur en fonction du biome associé à la Dimension des Rêves.</p>
 */
public class DreamScoreboard extends BaseScoreboard {

    @Override
    protected void updateTitle(Player player, SternalBoard board) {
        board.updateTitle(canShowLogo
                ? Component.text(FontImageWrapper.replaceFontImages(":dream_openmc:"))
                : Component.text("OPENMC", NamedTextColor.DARK_BLUE));
    }

    @Override
    public void update(Player player, SternalBoard board) {
        DreamBiome dreamBiome = DreamDimensionManager.getDreamBiome(player);
        DreamPlayer dreamPlayer = DreamManager.getDreamPlayer(player);

        List<Component> lines = new ArrayList<>();

        lines.add(empty());
        lines.add(MiniMessage.miniMessage().deserialize("<gradient:#0011ff:#2556b6>%s</gradient>"
                .formatted(textToSmall(player.getName()))).decoration(TextDecoration.BOLD, true));

        if (dreamPlayer != null) {
            Long time = dreamPlayer.getDreamTime();
            int cold = dreamPlayer.getCold();

            lines.add(text(" • ", NamedTextColor.DARK_GRAY)
                    .append(text(textToSmall("temps:"), NamedTextColor.GRAY))
                    .appendSpace()
                    .append(text(textToSmall(DateUtils.convertSecondToTime(time))).color(TextColor.color(0x00CC34)))
            );

            if (cold > 0)
                lines.add(text(" • ", NamedTextColor.DARK_GRAY)
                        .append(text(textToSmall("froid:"), NamedTextColor.GRAY))
                        .appendSpace()
                        .append(text(String.valueOf(dreamPlayer.getCold())).color(TextColor.color(0x44EBDA)))
                );

            lines.add(empty());
        }

        if (dreamBiome != null) {
            lines.add(text(" • ", NamedTextColor.DARK_GRAY)
                    .append(text(textToSmall("biome:"), NamedTextColor.GRAY))
                    .appendSpace()
                    .append(dreamBiome.getName())
            );
        }

        DreamStructure dreamStructure = DreamStructuresManager.getStructureAt(player.getLocation());
        if (dreamStructure != null) {
            String nameLocation = dreamStructure.type().getName();
            lines.add(text(" • ", NamedTextColor.DARK_GRAY)
                    .append(text(textToSmall("location:"), NamedTextColor.GRAY))
                    .appendSpace()
                    .append(Component.text(textToSmall(nameLocation)))
            );
        }

        lines.add(empty());
        lines.add(MiniMessage.miniMessage().deserialize("    <gradient:#001a66:#1358c9>%s</gradient>".formatted(textToSmall("play.openmc.fr"))));
        board.updateLines(lines);
    }

    @Override
    public boolean shouldDisplay(Player player) {
        return DreamUtils.isInDreamWorld(player);
    }

    @Override
    public int priority() {
        return 666;
    }

    @Override
    public int updateInterval() {
        return 1;
    }
}
