package fr.openmc.core.features.events.contents.dailyevents.contents.miraculousfishing.minigame;

import fr.openmc.api.menulib.Menu;
import fr.openmc.api.menulib.utils.InventorySize;
import fr.openmc.api.menulib.utils.ItemMenuBuilder;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

public class FishingMiniGameMenu extends Menu {
    private static final int TRACK_START = 10;
    private static final int TRACK_END = 18;
    private static final int GOOD_ZONE_START = 13;
    private static final int GOOD_ZONE_END = 15;
    private static final int PERFECT_SLOT = 14;
    private static final int ACTION_SLOT = 40;
    private static final int INFO_SLOT = 4;
    private static final long GAME_DURATION_TICKS = 100L;
    private static final long UPDATE_PERIOD_TICKS = 2L;
    private static final List<Integer> NON_TAKABLE_SLOTS = List.of();

    private final Consumer<FishingMiniGameResult> callback;

    private int cursorSlot = TRACK_START;
    private int direction = 1;
    private boolean started;
    private boolean finished;
    private BukkitTask animationTask;
    private BukkitTask timeoutTask;

    public FishingMiniGameMenu(Player owner, Consumer<FishingMiniGameResult> callback) {
        super(owner);
        this.callback = Objects.requireNonNull(callback, "callback");
    }

    @Override
    public @NotNull Component getName() {
        return TranslationManager.translation(
                "feature.dailyevents.miraculousfishing.minigame.name"
        );
    }

    @Override
    public String getTexture() {
        return null;
    }

    @Override
    public @NotNull InventorySize getInventorySize() {
        return InventorySize.LARGER;
    }

    @Override
    public void onInventoryClick(InventoryClickEvent event) {
        event.setCancelled(true);

        if (finished
                || event.getClickedInventory() == null
                || event.getClickedInventory() != event.getView().getTopInventory()
                || event.getSlot() != ACTION_SLOT) {
            return;
        }

        finish(resolveResult());
    }

    @Override
    public @NotNull Map<Integer, ItemMenuBuilder> getContent() {
        Map<Integer, ItemMenuBuilder> content = new HashMap<>();

        fillBackground(content);
        addTrack(content);
        addCursor(content);
        addInfo(content);
        addAction(content);

        return content;
    }

    @Override
    public void onClose(InventoryCloseEvent event) {
        if (!finished) {
            finish(FishingMiniGameResult.MISS);
        }
    }

    @Override
    public List<Integer> getTakableSlot() {
        return NON_TAKABLE_SLOTS;
    }

    void start() {
        if (started || finished) {
            return;
        }

        started = true;

        animationTask = Bukkit.getScheduler().runTaskTimer(
                OMCPlugin.getInstance(),
                task -> {
                    if (finished) {
                        task.cancel();
                        return;
                    }

                    if (!getOwner().isOnline()) {
                        finish(FishingMiniGameResult.MISS);
                        return;
                    }

                    renderCursor();
                },
                UPDATE_PERIOD_TICKS,
                UPDATE_PERIOD_TICKS
        );

        timeoutTask = Bukkit.getScheduler().runTaskLater(
                OMCPlugin.getInstance(),
                () -> finish(FishingMiniGameResult.MISS),
                GAME_DURATION_TICKS
        );
    }

    void abort() {
        if (finished) {
            return;
        }

        finished = true;
        cancelTasks();
    }

    private void renderCursor() {
        int previousCursorSlot = cursorSlot;

        moveCursor();

        Inventory inventory = getOwner().getOpenInventory().getTopInventory();
        if (inventory.getHolder() != this) {
            finish(FishingMiniGameResult.MISS);
            return;
        }

        inventory.setItem(previousCursorSlot, createTrackItem(previousCursorSlot));
        inventory.setItem(cursorSlot, createCursorItem());
        getOwner().updateInventory();
    }

    private void moveCursor() {
        if (cursorSlot == TRACK_END || cursorSlot == TRACK_START) {
            direction *= -1;
        }

        cursorSlot += direction;
    }

    private FishingMiniGameResult resolveResult() {
        if (cursorSlot == PERFECT_SLOT) {
            return FishingMiniGameResult.PERFECT;
        }

        if (cursorSlot >= GOOD_ZONE_START && cursorSlot <= GOOD_ZONE_END) {
            return FishingMiniGameResult.GOOD;
        }

        return FishingMiniGameResult.MISS;
    }

    private void fillBackground(Map<Integer, ItemMenuBuilder> content) {
        int size = getInventorySize().getSize();

        for (int slot = 0; slot < size; slot++) {
            Material material = slot < 9 || slot >= size - 9
                    ? Material.PRISMARINE_BRICKS
                    : Material.BLUE_STAINED_GLASS_PANE;

            content.put(slot, pane(material));
        }

        for (int slot = 9; slot < size - 9; slot++) {
            if (slot / 9 == 1 || slot / 9 == 3) {
                content.put(slot, pane(Material.LIGHT_BLUE_STAINED_GLASS_PANE));
            }
        }

        content.put(0, icon(Material.COD, "feature.dailyevents.miraculousfishing.minigame.fish"));
        content.put(8, icon(Material.COD, "feature.dailyevents.miraculousfishing.minigame.fish"));
        content.put(size - 9, icon(Material.COD, "feature.dailyevents.miraculousfishing.minigame.fish"));
        content.put(size - 1, icon(Material.COD, "feature.dailyevents.miraculousfishing.minigame.fish"));
    }

    private void addTrack(Map<Integer, ItemMenuBuilder> content) {
        for (int slot = TRACK_START; slot <= TRACK_END; slot++) {
            content.put(slot, createTrackItem(slot));
        }
    }

    private ItemMenuBuilder createTrackItem(int slot) {
        Material material;
        String translationKey;

        if (slot == PERFECT_SLOT) {
            material = Material.SEA_LANTERN;
            translationKey = "feature.dailyevents.miraculousfishing.minigame.perfect_zone";
        } else if (slot >= GOOD_ZONE_START && slot <= GOOD_ZONE_END) {
            material = Material.EMERALD_BLOCK;
            translationKey = "feature.dailyevents.miraculousfishing.minigame.good_zone";
        } else {
            material = Material.GRAY_CONCRETE;
            translationKey = "feature.dailyevents.miraculousfishing.minigame.miss_zone";
        }

        return icon(material, translationKey);
    }

    private void addCursor(Map<Integer, ItemMenuBuilder> content) {
        content.put(cursorSlot, createCursorItem());
    }

    private ItemMenuBuilder createCursorItem() {
        return new ItemMenuBuilder(this, Material.AMETHYST_SHARD,
                itemMeta -> {
                    itemMeta.displayName(
                            TranslationManager.translation(
                                    "feature.dailyevents.miraculousfishing.minigame.cursor"
                            )
                    );
                    itemMeta.setEnchantmentGlintOverride(true);
                });
    }

    private void addInfo(Map<Integer, ItemMenuBuilder> content) {
        content.put(INFO_SLOT, new ItemMenuBuilder(this, Material.HEART_OF_THE_SEA,
                itemMeta -> {
                    itemMeta.displayName(TranslationManager.translation(
                            "feature.dailyevents.miraculousfishing.minigame.instruction"
                    ));
                    itemMeta.lore(TranslationManager.translationLore(
                            "feature.dailyevents.miraculousfishing.minigame.instruction_lore"
                    ));
                    itemMeta.setEnchantmentGlintOverride(true);
                }));
    }

    private void addAction(Map<Integer, ItemMenuBuilder> content) {
        content.put(ACTION_SLOT, new ItemMenuBuilder(this, Material.FISHING_ROD,
                itemMeta -> {
                    itemMeta.displayName(TranslationManager.translation(
                            "feature.dailyevents.miraculousfishing.minigame.action"
                    ));
                    itemMeta.lore(TranslationManager.translationLore(
                            "feature.dailyevents.miraculousfishing.minigame.action_lore"
                    ));
                    itemMeta.setEnchantmentGlintOverride(true);
                }));
    }

    private ItemMenuBuilder pane(Material material) {
        return new ItemMenuBuilder(this, material,
                itemMeta -> itemMeta.displayName(Component.text(" "))).hideTooltip(true);
    }

    private ItemMenuBuilder icon(Material material, String translationKey) {
        return new ItemMenuBuilder(this, material,
                itemMeta -> itemMeta.displayName(
                        TranslationManager.translation(translationKey)
                ));
    }

    private void finish(FishingMiniGameResult result) {
        if (finished) {
            return;
        }

        finished = true;
        cancelTasks();
        FishingMiniGameManager.remove(getOwner());

        if (!getOwner().isOnline()) {
            return;
        }

        Sound sound;
        float pitch;

        switch (result) {
            case PERFECT -> {
                sound = Sound.ENTITY_PLAYER_LEVELUP;
                pitch = 1.5F;
            }
            case GOOD -> {
                sound = Sound.ENTITY_PLAYER_LEVELUP;
                pitch = 1.15F;
            }
            default -> {
                sound = Sound.BLOCK_NOTE_BLOCK_BASS;
                pitch = 0.8F;
            }
        }

        getOwner().playSound(getOwner().getLocation(), sound, 1F, pitch);
        getOwner().closeInventory();
        callback.accept(result);
    }

    private void cancelTasks() {
        if (animationTask != null) {
            animationTask.cancel();
            animationTask = null;
        }

        if (timeoutTask != null) {
            timeoutTask.cancel();
            timeoutTask = null;
        }
    }

}
