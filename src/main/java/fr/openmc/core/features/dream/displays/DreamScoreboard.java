package fr.openmc.core.features.dream.displays;

import dev.lone.itemsadder.api.FontImages.FontImageWrapper;
import fr.openmc.api.scoreboard.SternalBoard;
import fr.openmc.core.features.displays.scoreboards.BaseScoreboard;
import fr.openmc.core.features.dream.DreamManager;
import fr.openmc.core.features.dream.DreamUtils;
import fr.openmc.core.features.dream.models.db.DreamPlayer;
import fr.openmc.core.features.dream.registries.DreamBiome;
import fr.openmc.core.features.dream.registries.DreamStructure;
import fr.openmc.core.utils.bedrock.CharRemplacementUtils;
import fr.openmc.core.utils.text.DateUtils;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

import static fr.openmc.core.utils.text.fonts.SmallCapsUtils.toSmall;
import static fr.openmc.core.utils.text.fonts.SmallCapsUtils.toSmallComponent;
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
        DreamBiome dreamBiome = DreamBiome.getDreamBiome(player);
        DreamPlayer dreamPlayer = DreamManager.getDreamPlayer(player);

        List<Component> lines = new ArrayList<>();

        lines.add(empty());
        lines.add(MiniMessage.miniMessage().deserialize("<gradient:#0011ff:#2556b6><font:omc_fonts:small_caps>%s</font></gradient>"
                .formatted(player.getName())).decoration(TextDecoration.BOLD, true));

        if (dreamPlayer != null) {
            long time = dreamPlayer.getDreamTime();
            int cold = dreamPlayer.getCold();

            lines.add(text(" " + CharRemplacementUtils.getPointChar(player) + " ", NamedTextColor.DARK_GRAY)
                    .append(TranslationManager.translation(player, "feature.dream.scoreboard.time", true).color(NamedTextColor.GRAY))
                    .appendSpace()
                    .append(toSmall(player, DateUtils.convertSecondToTime(time)).color(TextColor.color(0x00CC34)))
            );

            if (cold > 0)
                lines.add(text(" " + CharRemplacementUtils.getPointChar(player) + " ", NamedTextColor.DARK_GRAY)
                        .append(TranslationManager.translation(player, "feature.dream.scoreboard.cold", true).color(NamedTextColor.GRAY))
                        .appendSpace()
                        .append(text(dreamPlayer.getCold()).color(TextColor.color(0x44EBDA)))
                );

            lines.add(empty());
        }

        if (dreamBiome != null) {
            lines.add(text(" " + CharRemplacementUtils.getPointChar(player) + " ", NamedTextColor.DARK_GRAY)
                    .append(TranslationManager.translation(player,"feature.dream.scoreboard.biome", true).color(NamedTextColor.GRAY))
                    .appendSpace()
                    .append(dreamBiome.getSmallName())
            );
        }

        DreamStructure dreamStructure = DreamStructure.getDreamStructure(player);
        if (dreamStructure != null) {
            lines.add(text(" " + CharRemplacementUtils.getPointChar(player) + " ", NamedTextColor.DARK_GRAY)
                    .append(TranslationManager.translation(player, "feature.dream.scoreboard.location", true).color(NamedTextColor.GRAY))
                    .appendSpace()
                    .append(toSmallComponent(dreamStructure.getName()))
            );
        }

        lines.add(empty());
        lines.add(MiniMessage.miniMessage().deserialize(
                "    <gradient:#001a66:#1358c9><footer></gradient>",
                Placeholder.component("footer", TranslationManager.translation("feature.displays.scoreboard.footer.text", true))
        ));
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
