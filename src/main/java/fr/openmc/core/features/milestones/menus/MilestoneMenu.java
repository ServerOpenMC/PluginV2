package fr.openmc.core.features.milestones.menus;

import fr.openmc.api.menulib.Menu;
import fr.openmc.api.menulib.utils.InventorySize;
import fr.openmc.api.menulib.utils.ItemMenuBuilder;
import fr.openmc.core.features.milestones.MilestonesManager;
import fr.openmc.core.features.milestones.models.Milestone;
import fr.openmc.core.features.milestones.quests.MilestoneQuest;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MilestoneMenu extends Menu {

    private final Milestone milestone;
    private final List<MilestoneQuest> steps;
    private int offset = 0;

    private static final int START_ROW = 0;
    private static final int END_ROW = 4;
    private static final int[] COLS = {0, 2, 4, 6, 8};
    private static final int MAX_VISIBLE_NODES = 2 * COLS.length;

    private static int slotAt(int row, int col) {
        return row * 9 + col;
    }

    private static int rowOf(int slot) {
        return slot / 9;
    }

    private static int colOf(int slot) {
        return slot % 9;
    }

    public MilestoneMenu(Player owner, Milestone milestone) {
        super(owner);
        this.milestone = milestone;
        this.steps = milestone.getSteps();
    }

    @Override
    public @NotNull Component getName() {
        return TranslationManager.translation(
                "feature.milestones.menu.title.milestone",
                Component.text(milestone.getName())
        );
    }

    @Override
    public String getTexture() {
        return null;
    }

    @Override
    public @NotNull InventorySize getInventorySize() {
        return InventorySize.LARGEST;
    }

    @Override
    public void onInventoryClick(InventoryClickEvent click) {

    }

    @Override
    public @NotNull Map<Integer, ItemMenuBuilder> getContent() {
        Map<Integer, ItemMenuBuilder> content = new HashMap<>();
        Player player = getOwner();
        int currentStep = MilestonesManager.getPlayerStep(milestone.getType(), player);

        int remaining = Math.max(0, steps.size() - offset);
        int visible = Math.min(MAX_VISIBLE_NODES, remaining);

        Snake snake = buildSnake(visible);

        for (int i = 0; i < visible; i++) {
            int stepIndex = offset + i;
            MilestoneQuest quest = steps.get(stepIndex);

            boolean completed = stepIndex < currentStep;
            boolean active = stepIndex == currentStep;

            List<Component> stepLore = new ArrayList<>();
            quest.getDescription(player.getUniqueId()).forEach(line -> stepLore.add(
                    LegacyComponentSerializer.legacySection().deserialize(line)
                            .decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE)
            ));


            int slot = snake.nodes.get(i);
            NamedTextColor nameColor = completed ? NamedTextColor.GREEN : active ? NamedTextColor.YELLOW : NamedTextColor.GRAY;
            content.put(slot, new ItemMenuBuilder(this, quest.getIcon(), meta -> {
                meta.displayName(Component.text(quest.getName())
                        .color(nameColor)
                        .decoration(TextDecoration.ITALIC, false));
                meta.lore(stepLore);
                meta.setEnchantmentGlintOverride(completed || active);
            }));
        }

        for (int j = 0; j < snake.links.size(); j++) {
            int a = snake.links.get(j);

            int segmentIndex = -1;
            for (int i = 0; i + 1 < snake.nodes.size(); i++) {
                int n1 = snake.nodes.get(i);
                int n2 = snake.nodes.get(i + 1);

                if ((colOf(n1) == colOf(n2) && colOf(n1) == colOf(a) && rowOf(a) > Math.min(rowOf(n1), rowOf(n2)) && rowOf(a) < Math.max(rowOf(n1), rowOf(n2)))
                        || (rowOf(n1) == rowOf(n2) && rowOf(n1) == rowOf(a) && colOf(a) > Math.min(colOf(n1), colOf(n2)) && colOf(a) < Math.max(colOf(n1), colOf(n2)))) {
                    segmentIndex = i;
                    break;
                }
            }

            boolean alreadyPassed = (segmentIndex != -1 && (offset + segmentIndex + 1) <= currentStep);

            Material linkMat = alreadyPassed
                    ? Material.LIME_STAINED_GLASS_PANE
                    : Material.GRAY_STAINED_GLASS_PANE;

            content.put(a, new ItemMenuBuilder(this, linkMat).hideTooltip(true));
        }

        content.put(45, new ItemMenuBuilder(this, Material.ARROW, true));

        if (offset > 0) {
            content.put(48, new ItemMenuBuilder(this, Material.ARROW,
                    meta -> meta.displayName(TranslationManager.translation("feature.milestones.menu.page.previous")
                            .decoration(TextDecoration.ITALIC, false)))
                    .setOnClick(c -> {
                        offset = Math.max(0, offset - MAX_VISIBLE_NODES);
                        open();
                    }));
        }

        if (offset + visible < steps.size()) {
            content.put(50, new ItemMenuBuilder(this, Material.ARROW,
                    meta -> meta.displayName(TranslationManager.translation("feature.milestones.menu.page.next")
                            .decoration(TextDecoration.ITALIC, false)))
                    .setOnClick(c -> {
                        offset = Math.min(steps.size() - 1, offset + MAX_VISIBLE_NODES);
                        open();
                    }));
        }

        content.put(53, new ItemMenuBuilder(this, Material.BARRIER,
                meta -> meta.displayName(TranslationManager.translation("feature.milestones.menu.close")
                        .decoration(TextDecoration.ITALIC, false))).setCloseButton());

        return content;
    }

    @Override
    public void onClose(InventoryCloseEvent event) {
    }

    @Override
    public List<Integer> getTakableSlot() {
        return List.of();
    }

	// ============================== SNAKE ============================== //
	
    private record Snake(List<Integer> nodes, List<Integer> links) {
    }

    private Snake buildSnake(int count) {
        List<Integer> nodes = new ArrayList<>();
        List<Integer> links = new ArrayList<>();
        int placed = 0;

        for (int colIdx = 0; colIdx < COLS.length && placed < count; colIdx++) {
            int col = COLS[colIdx];
            int nextPrimary = (colIdx + 1 < COLS.length) ? COLS[colIdx + 1] : -1;
            boolean topDown = (colIdx % 2 == 0);

            if (topDown) {
                // haut
                if (placed < count) {
                    nodes.add(slotAt(START_ROW, col));
                    placed++;
                    if (placed < count) {
                        for (int r = START_ROW + 1; r <= END_ROW - 1; r++)
                            links.add(slotAt(r, col));
                    }
                }
                // bas
                if (placed < count) {
                    nodes.add(slotAt(END_ROW, col));
                    placed++;
                    if (placed < count && nextPrimary != -1) {
                        for (int c = col + 1; c < nextPrimary; c++)
                            links.add(slotAt(END_ROW, c));
                    }
                }
            } else {
                // bas
                if (placed < count) {
                    nodes.add(slotAt(END_ROW, col));
                    placed++;
                    if (placed < count) {
                        for (int r = END_ROW - 1; r >= START_ROW + 1; r--)
                            links.add(slotAt(r, col));
                    }
                }

                // haut
                if (placed < count) {
                    nodes.add(slotAt(START_ROW, col));
                    placed++;
                    if (placed < count && nextPrimary != -1) {
                        for (int c = col + 1; c < nextPrimary; c++)
                            links.add(slotAt(START_ROW, c));
                    }
                }
            }
        }
        return new Snake(nodes, links);
    }
}