package fr.openmc.core.features.milestones.menus;

import fr.openmc.api.menulib.Menu;
import fr.openmc.api.menulib.utils.InventorySize;
import fr.openmc.api.menulib.utils.ItemBuilder;
import fr.openmc.core.features.milestones.Milestone;
import fr.openmc.core.features.milestones.MilestoneQuest;
import fr.openmc.core.features.milestones.MilestonesManager;
import net.kyori.adventure.text.Component;
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
    public @NotNull String getName() {
        return "Menu des milestones - " + milestone.getName();
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
    public @NotNull Map<Integer, ItemBuilder> getContent() {
        Map<Integer, ItemBuilder> content = new HashMap<>();
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
            quest.getDescription(player.getUniqueId()).forEach(line -> stepLore.add(Component.text(line)));


            int slot = snake.nodes.get(i);
            content.put(slot, new ItemBuilder(this, quest.getIcon(), meta -> {
                meta.displayName(Component.text(
                        (completed ? "§a" : active ? "§e" : "§7") + quest.getName()
                ));
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

            content.put(a, new ItemBuilder(this, linkMat).hideTooltip(true));
        }

        content.put(45, new ItemBuilder(this, Material.ARROW, true));

        if (offset > 0) {
            content.put(48, new ItemBuilder(this, Material.ARROW,
                    meta -> meta.displayName(Component.text("§e◀ Page précédente")))
                    .setOnClick(c -> {
                        offset = Math.max(0, offset - MAX_VISIBLE_NODES);
                        open();
                    }));
        }

        if (offset + visible < steps.size()) {
            content.put(50, new ItemBuilder(this, Material.ARROW,
                    meta -> meta.displayName(Component.text("§ePage suivante ▶")))
                    .setOnClick(c -> {
                        offset = Math.min(steps.size() - 1, offset + MAX_VISIBLE_NODES);
                        open();
                    }));
        }

        content.put(53, new ItemBuilder(this, Material.BARRIER,
                meta -> meta.displayName(Component.text("§cFermer"))).setCloseButton());

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