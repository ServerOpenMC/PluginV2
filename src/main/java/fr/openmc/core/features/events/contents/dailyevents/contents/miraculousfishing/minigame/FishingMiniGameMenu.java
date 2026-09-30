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
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

public class FishingMiniGameMenu extends Menu {
    private static final int TRACK_START = 10;
    private static final int TRACK_END = 16;
    private static final int GOOD_ZONE_START = 12;
    private static final int GOOD_ZONE_END = 14;
    private static final int PERFECT_SLOT = 13;
    private static final int ACTION_SLOT = 22;
    private static final long GAME_DURATION_TICKS = 100L;
    private static final long UPDATE_PERIOD_TICKS = 2L;

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
        return TranslationManager.translation("feature.dailyevents.miraculousfishing.minigame.name");
    }

    @Override
    public String getTexture() {
        return null;
    }

    @Override
    public @NotNull InventorySize getInventorySize() {
        return InventorySize.NORMAL;
    }

    @Override
    public void onInventoryClick(InventoryClickEvent event) {
        event.setCancelled(true);

        if (event.getClickedInventory() == null
                || event.getClickedInventory() != event.getView().getTopInventory()) {
            return;
        }

        if (event.getSlot() != ACTION_SLOT || finished) {
            return;
        }

        finish(resolveResult());
    }

    @Override
    public @NotNull Map<Integer, ItemMenuBuilder> getContent() {
        Map<Integer, ItemMenuBuilder> content = new HashMap<>();

        for (int slot = 0; slot < getInventorySize().getSize(); slot++) {
            content.put(slot, new ItemMenuBuilder(this, Material.BLACK_STAINED_GLASS_PANE,
                    itemMeta -> itemMeta.displayName(Component.text(" "))).hideTooltip(true));
        }

        for (int slot = TRACK_START; slot <= TRACK_END; slot++) {
            Material material;
            if (slot == PERFECT_SLOT) {
                material = Material.GOLD_BLOCK;
            } else if (slot >= GOOD_ZONE_START && slot <= GOOD_ZONE_END) {
                material = Material.LIME_STAINED_GLASS_PANE;
            } else {
                material = Material.GRAY_STAINED_GLASS_PANE;
            }

            String translationKey = slot == PERFECT_SLOT
                    ? "feature.dailyevents.miraculousfishing.minigame.perfect_zone"
                    : "feature.dailyevents.miraculousfishing.minigame.good_zone";

            content.put(slot, new ItemMenuBuilder(this, material,
                    itemMeta -> itemMeta.displayName(TranslationManager.translation(translationKey))));
        }

        content.put(cursorSlot, new ItemMenuBuilder(this, Material.LIGHT_BLUE_STAINED_GLASS_PANE,
                itemMeta -> itemMeta.displayName(
                        TranslationManager.translation("feature.dailyevents.miraculousfishing.minigame.cursor")
                )));

        content.put(ACTION_SLOT, new ItemMenuBuilder(this, Material.FISHING_ROD,
                itemMeta -> {
                    itemMeta.displayName(
                            TranslationManager.translation("feature.dailyevents.miraculousfishing.minigame.action")
                    );
                    itemMeta.lore(TranslationManager.translationLore(
                            "feature.dailyevents.miraculousfishing.minigame.action_lore"
                    ));
                    itemMeta.setEnchantmentGlintOverride(true);
                }));

        content.put(4, new ItemMenuBuilder(this, Material.CLOCK,
                itemMeta -> {
                    itemMeta.displayName(
                            TranslationManager.translation("feature.dailyevents.miraculousfishing.minigame.instruction")
                    );
                    itemMeta.lore(TranslationManager.translationLore(
                            "feature.dailyevents.miraculousfishing.minigame.instruction_lore"
                    ));
                }));

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
        return List.of();
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

                    if (cursorSlot == TRACK_END || cursorSlot == TRACK_START) {
                        direction *= -1;
                    }

                    cursorSlot += direction;
                    update();
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

        if (animationTask != null) {
            animationTask.cancel();
        }

        if (timeoutTask != null) {
            timeoutTask.cancel();
        }
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

    private void finish(FishingMiniGameResult result) {
        if (finished) {
            return;
        }

        finished = true;

        if (animationTask != null) {
            animationTask.cancel();
        }

        if (timeoutTask != null) {
            timeoutTask.cancel();
        }

        FishingMiniGameManager.remove(getOwner());

        if (!getOwner().isOnline()) {
            return;
        }

        getOwner().playSound(
                getOwner().getLocation(),
                result == FishingMiniGameResult.MISS
                        ? Sound.BLOCK_NOTE_BLOCK_BASS
                        : Sound.ENTITY_PLAYER_LEVELUP,
                1F,
                result == FishingMiniGameResult.PERFECT ? 1.4F : 1F
        );
        getOwner().closeInventory();
        callback.accept(result);
    }
}
