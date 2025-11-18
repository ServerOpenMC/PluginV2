package fr.openmc.core.features.displays.scoreboards.sb;

import dev.lone.itemsadder.api.FontImages.FontImageWrapper;
import fr.openmc.api.scoreboard.SternalBoard;
import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.sub.war.War;
import fr.openmc.core.features.city.sub.war.WarManager;
import fr.openmc.core.features.displays.scoreboards.BaseScoreboard;
import fr.openmc.core.utils.DateUtils;
import fr.openmc.core.utils.DirectionUtils;
import fr.openmc.core.utils.MathUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

import static fr.openmc.core.utils.messages.MessagesManager.textToSmall;
import static net.kyori.adventure.text.Component.*;

public class CityWarScoreboard extends BaseScoreboard {

    @Override
    public void update(Player player, SternalBoard board) {

        City city = CityManager.getPlayerCity(player.getUniqueId());
        if (city == null || !city.isInWar()) return;

        War war = city.getWar();
        City enemyCity = war.getCityAttacker().equals(city) ? war.getCityDefender() : war.getCityAttacker();

        List<Component> lines = new ArrayList<>(MainScoreboard.getDefaultLines(player));

        lines.add(MiniMessage.miniMessage()
                .deserialize("<gradient:#FF0000:#FF7F7F>%s</gradient>".formatted(textToSmall("GUERRE EN COURS")))
                .decoration(TextDecoration.BOLD, true));

        lines.add(text("  • ", net.kyori.adventure.text.format.NamedTextColor.DARK_GRAY)
                .append(text(textToSmall("ennemi:"), TextColor.color(0xAAAAAA)))
                .appendSpace()
                .append(text(textToSmall(enemyCity.getName()), TextColor.color(0xFF0634)))
        );

        Component phaseComponent;
        switch (war.getPhase()) {
            case PREPARATION -> phaseComponent = MiniMessage.miniMessage()
                    .deserialize("<gradient:#FF7518:#FFD580>%s</gradient>".formatted(textToSmall(WarManager.getFormattedPhase(war.getPhase()))));
            case COMBAT -> phaseComponent = text(textToSmall(WarManager.getFormattedPhase(war.getPhase())), TextColor.color(0xFC1C1C));
            case ENDED -> phaseComponent = text(textToSmall(WarManager.getFormattedPhase(war.getPhase())), NamedTextColor.GRAY);
            default -> phaseComponent = text(textToSmall(WarManager.getFormattedPhase(war.getPhase())), NamedTextColor.WHITE);
        }

        lines.add(text("  • ", NamedTextColor.DARK_GRAY)
                .append(text(textToSmall("phase:"), NamedTextColor.GRAY))
                .appendSpace()
                .append(phaseComponent)
        );

        Chunk chunk = enemyCity.getMascot().getChunk();
        World world = chunk.getWorld();
        int x = (chunk.getX() << 4) + 8;
        int z = (chunk.getZ() << 4) + 8;
        int y = world.getHighestBlockYAt(x, z);

        LivingEntity mascot = city.getMascot() != null ? (LivingEntity) city.getMascot().getEntity() : null;
        LivingEntity enemyMascot = enemyCity.getMascot() != null ? (LivingEntity) enemyCity.getMascot().getEntity() : null;

        Location mascotLocation = enemyMascot != null ? enemyMascot.getLocation() : new Location(world, x, y, z);
        String direction = DirectionUtils.getDirectionArrow(player, mascotLocation);
        double distance = mascotLocation.distance(player.getLocation());
        int rounded = (int) Math.round(distance);

        lines.add(text("  • ", NamedTextColor.DARK_GRAY)
                .append(text(textToSmall("distance:"), NamedTextColor.GRAY))
                .appendSpace()
                .append(text(direction, TextColor.color(0xFFE206)))
                .appendSpace()
                .append(text("(%s)".formatted(rounded), TextColor.color(0xFFFE35)))
        );


        switch (war.getPhase()) {
            case PREPARATION -> lines.add(text("  • ", NamedTextColor.DARK_GRAY)
                    .append(text(textToSmall("début dans:"), NamedTextColor.GRAY))
                    .appendSpace()
                    .append(text(DateUtils.convertSecondToTime(war.getPreparationTimeRemaining()), TextColor.color(0xF52727)))
            );

            case COMBAT -> {
                if (mascot != null)
                    lines.add(text("  • ", NamedTextColor.DARK_GRAY)
                            .append(text(textToSmall("mascotte:"), NamedTextColor.GRAY))
                            .appendSpace()
                            .append(getColoredHealth(mascot))
                    );

                if (enemyMascot != null)
                    lines.add(text("  • ", NamedTextColor.DARK_GRAY)
                            .append(text(textToSmall("ennemi:"), NamedTextColor.GRAY))
                            .appendSpace()
                            .append(getColoredHealth(enemyMascot))
                    );

                lines.add(text("  • ", NamedTextColor.DARK_GRAY)
                        .append(text(textToSmall("fin dans:"), NamedTextColor.GRAY))
                        .appendSpace()
                        .append(text(DateUtils.convertSecondToTime(war.getCombatTimeRemaining()), TextColor.color(0xF52727)))
                );
            }

            case ENDED -> lines.add(text("  • ", NamedTextColor.DARK_GRAY)
                    .append(text(textToSmall("état:"), NamedTextColor.GRAY))
                    .appendSpace()
                    .append(text(textToSmall("terminée"), NamedTextColor.GRAY))
            );
        }

        lines.add(empty());
        lines.add(getFooter());

        board.updateLines(lines);
    }

    @Override
    public boolean shouldDisplay(Player player) {
        if (!player.getWorld().getName().equalsIgnoreCase("world")) return false;
        City city = CityManager.getPlayerCity(player.getUniqueId());
        return city != null && city.isInWar();
    }

    @Override
    public int priority() {
        return 100;
    }

    @Override
    protected int updateInterval() {
        return 1;
    }

    private Component getColoredHealth(LivingEntity entity) {
        if (entity.isDead())
            return text(textToSmall("%s MORTE".formatted(FontImageWrapper.replaceFontImages(":dead1:"))), TextColor.color(0xFF3246));

        double health = entity.getHealth();
        double maxHealth = entity.getAttribute(Attribute.MAX_HEALTH).getValue();
        double ratio = health / maxHealth;

        int fullLifeColor = 0x14FF59; // Vert
        int noLifeColor = 0xFF3D1F;   // Rouge

        double lerpRatio = 1.0 - ratio;
        int interpolatedColor = MathUtils.lerpColor(fullLifeColor, noLifeColor, lerpRatio);
        TextColor color = TextColor.color(interpolatedColor);

        return text("%d/%d %s".formatted((int) health, (int) maxHealth, FontImageWrapper.replaceFontImages(":heart:")), color);
    }
}
