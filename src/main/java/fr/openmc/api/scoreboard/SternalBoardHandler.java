package fr.openmc.api.scoreboard;

import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.minecraft.network.chat.numbers.BlankFormat;
import net.minecraft.network.chat.numbers.FixedFormat;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.*;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.scores.DisplaySlot;
import net.minecraft.world.scores.Team;
import net.minecraft.world.scores.criteria.ObjectiveCriteria;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

/*
 * This file is part of SternalBoard, licensed under the MIT License.
 *
 * Copyright (c) 2019-2025 Ismael Hanbel
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

/**
 * Lightweight packet-based FastBoard-API fork for Bukkit plugins.
 * It can be safely used asynchronously as everything is at packet level.
 * readapted for OpenMC
 * <p>
 * @author ShieldCommunity, axeno
 * @version 2.3.0
 */
public abstract class SternalBoardHandler<T> {

    private static final Map<Class<?>, Field[]> PACKETS = new HashMap<>(8);
    protected static final String[] COLOR_CODES = Arrays.stream(ChatColor.values())
            .map(Object::toString)
            .toArray(String[]::new);

    private static final MethodHandle SEND_PACKET;
    private static final SternalReflection.PacketConstructor PACKET_SB_OBJ;
    private static final SternalReflection.PacketConstructor PACKET_SB_DISPLAY_OBJ;
    private static final SternalReflection.PacketConstructor PACKET_SB_TEAM;
    private static final SternalReflection.PacketConstructor PACKET_SB_SERIALIZABLE_TEAM;
    private static final MethodHandle PACKET_SB_SET_SCORE;
    private static final MethodHandle PACKET_SB_RESET_SCORE;
    private static final MethodHandle FIXED_NUMBER_FORMAT;

    static {
        try {
            MethodHandles.Lookup lookup = MethodHandles.lookup();

            SEND_PACKET = lookup.findVirtual(ServerGamePacketListenerImpl.class, "send",
                    MethodType.methodType(void.class, Packet.class));

            PACKET_SB_OBJ = SternalReflection.findPacketConstructor(ClientboundSetObjectivePacket.class, lookup);
            PACKET_SB_DISPLAY_OBJ = SternalReflection.findPacketConstructor(ClientboundSetDisplayObjectivePacket.class, lookup);
            PACKET_SB_TEAM = SternalReflection.findPacketConstructor(ClientboundSetPlayerTeamPacket.class, lookup);

            Class<?> sbTeamClass = SternalReflection.innerClass(ClientboundSetPlayerTeamPacket.class,
                    innerClass -> !innerClass.isEnum());
            PACKET_SB_SERIALIZABLE_TEAM = sbTeamClass != null ?
                    SternalReflection.findPacketConstructor(sbTeamClass, lookup) : null;

            PACKET_SB_SET_SCORE = lookup.findConstructor(ClientboundSetScorePacket.class,
                    MethodType.methodType(void.class, String.class, String.class, int.class,
                            Optional.class, Optional.class));

            PACKET_SB_RESET_SCORE = lookup.findConstructor(ClientboundResetScorePacket.class,
                    MethodType.methodType(void.class, String.class, String.class));

            FIXED_NUMBER_FORMAT = lookup.findConstructor(FixedFormat.class,
                    MethodType.methodType(void.class, net.minecraft.network.chat.Component.class));

            for (Class<?> clazz : Arrays.asList(ClientboundSetObjectivePacket.class,
                    ClientboundSetDisplayObjectivePacket.class, ClientboundSetScorePacket.class,
                    ClientboundSetPlayerTeamPacket.class, sbTeamClass)) {
                if (clazz == null) continue;

                Field[] fields = Arrays.stream(clazz.getDeclaredFields())
                        .filter(field -> !Modifier.isStatic(field.getModifiers()))
                        .toArray(Field[]::new);
                for (Field field : fields) {
                    field.setAccessible(true);
                }
                PACKETS.put(clazz, fields);
            }
        } catch (Throwable t) {
            throw new ExceptionInInitializerError(t);
        }
    }

    @Getter
    private final Player player;
    @Getter
    private final String id;

    private final List<T> lines = new ArrayList<>();
    private final List<T> scores = new ArrayList<>();
    @Getter
    private T title = emptyLine();
    @Getter
    private boolean deleted = false;

    protected SternalBoardHandler(Player player) {
        this.player = Objects.requireNonNull(player, "player");
        this.id = "sb-%s".formatted(Integer.toHexString(ThreadLocalRandom.current().nextInt()));

        try {
            sendObjectivePacket(ObjectiveMode.CREATE);
            sendDisplayObjectivePacket();
        } catch (Throwable t) {
            throw new RuntimeException("Unable to create scoreboard", t);
        }
    }

    public void updateTitle(T title) {
        if (this.title.equals(Objects.requireNonNull(title, "title"))) {
            return;
        }

        this.title = title;

        try {
            sendObjectivePacket(ObjectiveMode.UPDATE);
        } catch (Throwable t) {
            throw new RuntimeException("Unable to update scoreboard title", t);
        }
    }

    public List<T> getLines() {
        return new ArrayList<>(this.lines);
    }

    public T getLine(int line) {
        checkLineNumber(line, true, false);
        return this.lines.get(line);
    }

    public Optional<T> getScore(int line) {
        checkLineNumber(line, true, false);
        return Optional.ofNullable(this.scores.get(line));
    }

    public synchronized void updateLine(int line, T text) {
        updateLine(line, text, null);
    }

    public synchronized void updateLine(int line, T text, T scoreText) {
        checkLineNumber(line, false, false);

        try {
            if (line < size()) {
                this.lines.set(line, text);
                this.scores.set(line, scoreText);

                sendLineChange(getScoreByLine(line));
                sendScorePacket(getScoreByLine(line), ScoreboardAction.CHANGE);

                return;
            }

            List<T> newLines = new ArrayList<>(this.lines);
            List<T> newScores = new ArrayList<>(this.scores);

            if (line > size()) {
                for (int i = size(); i < line; i++) {
                    newLines.add(emptyLine());
                    newScores.add(null);
                }
            }

            newLines.add(text);
            newScores.add(scoreText);

            updateLines(newLines, newScores);
        } catch (Throwable t) {
            throw new RuntimeException("Unable to update scoreboard lines", t);
        }
    }

    public synchronized void removeLine(int line) {
        checkLineNumber(line, false, false);

        if (line >= size()) {
            return;
        }

        List<T> newLines = new ArrayList<>(this.lines);
        List<T> newScores = new ArrayList<>(this.scores);
        newLines.remove(line);
        newScores.remove(line);
        updateLines(newLines, newScores);
    }

    public void updateLines(T... lines) {
        updateLines(Arrays.asList(lines));
    }

    public synchronized void updateLines(Collection<T> lines) {
        updateLines(lines, null);
    }

    public synchronized void updateLines(Collection<T> lines, Collection<T> scores) {
        Objects.requireNonNull(lines, "lines");
        checkLineNumber(lines.size(), false, true);

        if (scores != null && scores.size() != lines.size()) {
            throw new IllegalArgumentException("The size of the scores must match the size of the board");
        }

        List<T> oldLines = new ArrayList<>(this.lines);
        this.lines.clear();
        this.lines.addAll(lines);

        List<T> oldScores = new ArrayList<>(this.scores);
        this.scores.clear();
        this.scores.addAll(scores != null ? scores : Collections.nCopies(lines.size(), null));

        int linesSize = this.lines.size();

        try {
            if (oldLines.size() != linesSize) {
                List<T> oldLinesCopy = new ArrayList<>(oldLines);

                if (oldLines.size() > linesSize) {
                    for (int i = oldLinesCopy.size(); i > linesSize; i--) {
                        sendTeamPacket(i - 1, TeamMode.REMOVE);
                        sendScorePacket(i - 1, ScoreboardAction.REMOVE);
                        oldLines.removeFirst();
                    }
                } else {
                    for (int i = oldLinesCopy.size(); i < linesSize; i++) {
                        sendScorePacket(i, ScoreboardAction.CHANGE);
                        sendTeamPacket(i, TeamMode.CREATE, null, null);
                    }
                }
            }

            for (int i = 0; i < linesSize; i++) {
                if (!Objects.equals(getLineByScore(oldLines, i), getLineByScore(i))) {
                    sendLineChange(i);
                }
                if (!Objects.equals(getLineByScore(oldScores, i), getLineByScore(this.scores, i))) {
                    sendScorePacket(i, ScoreboardAction.CHANGE);
                }
            }
        } catch (Throwable t) {
            throw new RuntimeException("Unable to update scoreboard lines", t);
        }
    }

    public synchronized void updateScore(int line, T text) {
        checkLineNumber(line, true, false);
        this.scores.set(line, text);

        try {
            sendScorePacket(getScoreByLine(line), ScoreboardAction.CHANGE);
        } catch (Throwable e) {
            throw new RuntimeException("Unable to update line score", e);
        }
    }

    public synchronized void removeScore(int line) {
        updateScore(line, null);
    }

    public synchronized void updateScores(Collection<T> texts) {
        Objects.requireNonNull(texts, "texts");

        if (this.scores.size() != this.lines.size()) {
            throw new IllegalArgumentException("The size of the scores must match the size of the board");
        }

        List<T> newScores = new ArrayList<>(texts);
        for (int i = 0; i < this.scores.size(); i++) {
            if (Objects.equals(this.scores.get(i), newScores.get(i))) {
                continue;
            }

            this.scores.set(i, newScores.get(i));

            try {
                sendScorePacket(getScoreByLine(i), ScoreboardAction.CHANGE);
            } catch (Throwable e) {
                throw new RuntimeException("Unable to update scores", e);
            }
        }
    }

    public int size() {
        return this.lines.size();
    }

    public void delete() {
        try {
            for (int i = 0; i < this.lines.size(); i++) {
                sendTeamPacket(i, TeamMode.REMOVE);
            }

            sendObjectivePacket(ObjectiveMode.REMOVE);
        } catch (Throwable t) {
            throw new RuntimeException("Unable to delete scoreboard", t);
        }

        this.deleted = true;
    }

    protected abstract void sendLineChange(int score) throws Throwable;

    protected abstract Object toMinecraftComponent(T value) throws Throwable;

    protected abstract T emptyLine();

    private void checkLineNumber(int line, boolean checkInRange, boolean checkMax) {
        if (line < 0) {
            throw new IllegalArgumentException("Line number must be positive");
        }

        if (checkInRange && line >= this.lines.size()) {
            throw new IllegalArgumentException("Line number must be under " + this.lines.size());
        }

        if (checkMax && line >= COLOR_CODES.length - 1) {
            throw new IllegalArgumentException("Line number is too high: " + line);
        }
    }

    protected int getScoreByLine(int line) {
        return this.lines.size() - line - 1;
    }

    protected T getLineByScore(int score) {
        return getLineByScore(this.lines, score);
    }

    protected T getLineByScore(List<T> lines, int score) {
        return score < lines.size() ? lines.get(lines.size() - score - 1) : null;
    }

    protected void sendObjectivePacket(ObjectiveMode mode) throws Throwable {
        Object packet = PACKET_SB_OBJ.invoke();

        setField(packet, String.class, this.id);
        setField(packet, int.class, mode.ordinal());

        if (mode != ObjectiveMode.REMOVE) {
            setComponentField(packet, this.title, 1);
            setField(packet, Optional.class, Optional.empty());
            setField(packet, ObjectiveCriteria.RenderType.class, ObjectiveCriteria.RenderType.INTEGER);
        }

        sendPacket(packet);
    }

    protected void sendDisplayObjectivePacket() throws Throwable {
        Object packet = PACKET_SB_DISPLAY_OBJ.invoke();

        setField(packet, DisplaySlot.class, DisplaySlot.SIDEBAR);
        setField(packet, String.class, this.id);

        sendPacket(packet);
    }

    protected void sendScorePacket(int score, ScoreboardAction action) throws Throwable {
        String objName = COLOR_CODES[score];

        if (action == ScoreboardAction.REMOVE) {
            sendPacket(PACKET_SB_RESET_SCORE.invoke(objName, this.id));
            return;
        }

        T scoreFormat = getLineByScore(this.scores, score);
        Object format = scoreFormat != null
                ? FIXED_NUMBER_FORMAT.invoke(toMinecraftComponent(scoreFormat))
                : BlankFormat.INSTANCE;

        Object scorePacket = PACKET_SB_SET_SCORE.invoke(objName, this.id, score,
                Optional.empty(), Optional.of(format));

        sendPacket(scorePacket);
    }

    protected void sendTeamPacket(int score, TeamMode mode) throws Throwable {
        sendTeamPacket(score, mode, null, null);
    }

    protected void sendTeamPacket(int score, TeamMode mode, T prefix, T suffix) throws Throwable {
        if (mode == TeamMode.ADD_PLAYERS || mode == TeamMode.REMOVE_PLAYERS) {
            throw new UnsupportedOperationException();
        }

        Object packet = PACKET_SB_TEAM.invoke();

        setField(packet, String.class, this.id + ':' + score);
        setField(packet, int.class, mode.ordinal(), 0);

        if (mode == TeamMode.REMOVE) {
            sendPacket(packet);
            return;
        }

        Object team = PACKET_SB_SERIALIZABLE_TEAM.invoke();

        // Set component fields
        setComponentField(team, null, 0);           // displayName
        setComponentField(team, prefix, 1);          // prefix
        setComponentField(team, suffix, 2);          // suffix

        // Set String fields (nametagVisibility and collisionRule)
        setField(team, String.class, "always", 0);
        setField(team, String.class, "always", 1);

        // Set enum fields - only set each once (not twice)
        setField(team, Team.Visibility.class, Team.Visibility.ALWAYS, 0);
        setField(team, Team.CollisionRule.class, Team.CollisionRule.ALWAYS, 0);

        // Try to set NamedTextColor if it exists
        try {
            setField(team, net.minecraft.ChatFormatting.class, net.minecraft.ChatFormatting.WHITE, 0);
        } catch (Exception ignored) {
            // Field might not exist or be different type
        }

        // Try to set int flags if they exist
        try {
            setField(team, int.class, 0x00, 0);
        } catch (Exception ignored) {
            // Field might not exist
        }

        setField(packet, Optional.class, Optional.of(team));

        if (mode == TeamMode.CREATE) {
            setField(packet, Collection.class, Collections.singletonList(COLOR_CODES[score]));
        }

        sendPacket(packet);
    }

    private void sendPacket(Object packet) throws Throwable {
        if (this.deleted) {
            throw new IllegalStateException("This SternalBoard is deleted");
        }

        if (this.player.isOnline()) {
            CraftPlayer craftPlayer = (CraftPlayer) this.player;
            ServerPlayer entityPlayer = craftPlayer.getHandle();
            SEND_PACKET.invoke(entityPlayer.connection, packet);
        }
    }

    private void setField(Object object, Class<?> fieldType, Object value) throws ReflectiveOperationException {
        setField(object, fieldType, value, 0);
    }

    private void setField(Object packet, Class<?> fieldType, Object value, int count) throws ReflectiveOperationException {
        int i = 0;
        for (Field field : PACKETS.get(packet.getClass())) {
            if (field.getType() == fieldType && count == i++) {
                field.set(packet, value);
            }
        }
    }

    private void setComponentField(Object packet, T value, int count) throws Throwable {
        int i = 0;
        for (Field field : PACKETS.get(packet.getClass())) {
            if ((field.getType() == String.class || field.getType() == net.minecraft.network.chat.Component.class) && count == i++) {
                field.set(packet, toMinecraftComponent(value));
            }
        }
    }

    public enum ObjectiveMode {
        CREATE, REMOVE, UPDATE
    }

    public enum TeamMode {
        CREATE, REMOVE, UPDATE, ADD_PLAYERS, REMOVE_PLAYERS
    }

    public enum ScoreboardAction {
        CHANGE, REMOVE
    }
}