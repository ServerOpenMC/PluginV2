package fr.openmc.api.scoreboard;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.craftbukkit.util.CraftChatMessage;
import org.bukkit.entity.Player;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Array;
import java.util.Objects;

/**
 * SternalBoard implementation using Adventure Components and MiniMessage.
 * <p>Based on SternalBoard <a href="https://github.com/ShieldCommunity/SternalBoard/">GitHub</a> by ShieldCommunity
 * and adapted to OpenMC with Adventure API.
 * </p>
 */
public class SternalBoard extends SternalBoardHandler<Component> {

    private static final MethodHandle MESSAGE_FROM_STRING;
    private static final Object EMPTY_MESSAGE;
    private static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();
    private static final LegacyComponentSerializer LEGACY_SERIALIZER = LegacyComponentSerializer.legacySection();

    static {
        try {
            MethodHandles.Lookup lookup = MethodHandles.lookup();
            MESSAGE_FROM_STRING = lookup.unreflect(CraftChatMessage.class.getMethod("fromString", String.class));
            EMPTY_MESSAGE = Array.get(MESSAGE_FROM_STRING.invoke(""), 0);
        } catch (Throwable t) {
            throw new ExceptionInInitializerError(t);
        }
    }

    /**
     * Creates a new SternalBoard for the given player.
     *
     * @param player the owner of the scoreboard
     */
    public SternalBoard(Player player) {
        super(player);
    }

    /**
     * Update the scoreboard title with a MiniMessage string.
     *
     * @param title the new scoreboard title (MiniMessage format)
     */
    public void updateTitle(String title) {
        updateTitle(MINI_MESSAGE.deserialize(title));
    }

    /**
     * Update the scoreboard title with an Adventure Component.
     *
     * @param title the new scoreboard title
     */
    @Override
    public void updateTitle(Component title) {
        Objects.requireNonNull(title, "title");

        super.updateTitle(title);
    }

    /**
     * Update the scoreboard lines with MiniMessage strings.
     *
     * @param lines the new scoreboard lines (MiniMessage format)
     */
    public void updateLines(String... lines) {
        Objects.requireNonNull(lines, "lines");
        Component[] components = new Component[lines.length];
        for (int i = 0; i < lines.length; i++) {
            components[i] = lines[i] != null ? MINI_MESSAGE.deserialize(lines[i]) : Component.empty();
        }
        updateLines(components);
    }

    /**
     * Update the scoreboard lines with Adventure Components.
     *
     * @param lines the new scoreboard lines
     */
    @Override
    public void updateLines(Component... lines) {
        Objects.requireNonNull(lines, "lines");
        super.updateLines(lines);
    }

    /**
     * Update a single line with a MiniMessage string.
     *
     * @param line the line number
     * @param text the new line text (MiniMessage format)
     */
    public void updateLine(int line, String text) {
        updateLine(line, text != null ? MINI_MESSAGE.deserialize(text) : Component.empty());
    }

    /**
     * Update a single line with a MiniMessage string and score.
     *
     * @param line the line number
     * @param text the new line text (MiniMessage format)
     * @param scoreText the new line's score (MiniMessage format)
     */
    public void updateLine(int line, String text, String scoreText) {
        updateLine(line,
                text != null ? MINI_MESSAGE.deserialize(text) : Component.empty(),
                scoreText != null ? MINI_MESSAGE.deserialize(scoreText) : null
        );
    }

    @Override
    protected void sendLineChange(int score) throws Throwable {
        int maxLength = 1024;
        Component line = getLineByScore(score);

        if (line == null || line.equals(Component.empty())) {
            Component emptyPrefix = Component.text(COLOR_CODES[score], NamedTextColor.WHITE);
            sendTeamPacket(score, TeamMode.UPDATE, emptyPrefix, Component.empty());
            return;
        }

        String legacyLine = LEGACY_SERIALIZER.serialize(line);

        if (legacyLine.length() <= maxLength) {
            sendTeamPacket(score, TeamMode.UPDATE, line, Component.empty());
            return;
        }

        int splitIndex = maxLength;

        if (legacyLine.charAt(splitIndex - 1) == '§') {
            splitIndex--;
        }

        String prefixStr = legacyLine.substring(0, splitIndex);
        String suffixStr = legacyLine.substring(splitIndex);

        String lastColors = getLastColors(prefixStr);
        if (!lastColors.isEmpty() && !suffixStr.startsWith("§")) {
            suffixStr = lastColors + suffixStr;
        }

        if (suffixStr.length() > maxLength) {
            suffixStr = suffixStr.substring(0, maxLength);
        }

        Component prefix = LEGACY_SERIALIZER.deserialize(prefixStr);
        Component suffix = LEGACY_SERIALIZER.deserialize(suffixStr);

        sendTeamPacket(score, TeamMode.UPDATE, prefix, suffix);
    }

    /**
     * Extract the last color codes from a legacy string.
     *
     * @param text the legacy string
     * @return the last color codes
     */
    private String getLastColors(String text) {
        StringBuilder colors = new StringBuilder();
        boolean hasColor = false;

        for (int i = text.length() - 2; i >= 0; i -= 2) {
            if (text.charAt(i) == '§' && i + 1 < text.length()) {
                char code = text.charAt(i + 1);

                // If it's a color code (not a format code), we're done
                if ("0123456789abcdefx".indexOf(Character.toLowerCase(code)) >= 0) {
                    colors.insert(0, "§" + code);
                    hasColor = true;
                    break;
                }

                // It's a format code (bold, italic, etc.)
                if ("klmnor".indexOf(Character.toLowerCase(code)) >= 0) {
                    colors.insert(0, "§" + code);
                }
            }
        }

        // If no color was found, add reset
        if (!hasColor && !colors.isEmpty()) {
            colors.insert(0, "§r");
        }

        return colors.toString();
    }

    @Override
    protected Object toMinecraftComponent(Component component) throws Throwable {
        if (component == null || component.equals(Component.empty())) {
            return EMPTY_MESSAGE;
        }

        String legacy = LEGACY_SERIALIZER.serialize(component);
        return Array.get(MESSAGE_FROM_STRING.invoke(legacy), 0);
    }

    @Override
    protected Component emptyLine() {
        return Component.empty();
    }
}