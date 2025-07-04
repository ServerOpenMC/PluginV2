package fr.openmc.core.features.milestones.menus;

import fr.openmc.api.menulib.Menu;
import fr.openmc.api.menulib.utils.InventorySize;
import fr.openmc.api.menulib.utils.ItemBuilder;
import fr.openmc.core.features.milestones.Milestone;
import fr.openmc.core.features.milestones.MilestonesManager;
import fr.openmc.core.features.quests.objects.Quest;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MilestoneMenu extends Menu {

    private final Milestone milestone;
    private final List<Quest> steps;
    private int offset = 0;

    public MilestoneMenu(Player owner, Milestone milestone) {
        super(owner);
        this.milestone = milestone;
        this.steps = milestone.getSteps();
    }

    @Override
    public @NotNull String getName() {
        return "§7Progression - " + milestone.getName();
    }

    @Override
    public @NotNull InventorySize getInventorySize() {
        return InventorySize.LARGEST;
    }

    @Override
    public void onInventoryClick(InventoryClickEvent click) {

    }

    @Override
    public @NotNull Map<Integer, ItemStack> getContent() {
        Map<Integer, ItemStack> content = new HashMap<>();
        Player player = getOwner();
        int currentStep = MilestonesManager.getPlayerStep(milestone.getType(), player);

        int visibleSteps = 5;
        int baseSlot = 18;

        for (int i = 0; i < visibleSteps; i++) {
            int questIndex = offset + i;
            if (questIndex >= steps.size()) break;

            int slot = baseSlot + (i * 2);
            Quest quest = steps.get(questIndex);
            boolean completed = questIndex < currentStep;
            boolean active = questIndex == currentStep;

            List<Component> stepLore = new ArrayList<>();
            quest.getDescription(player.getUniqueId()).forEach(line -> stepLore.add(Component.text(line)));

            content.put(slot, new ItemBuilder(this, quest.getIcon().getType(), meta -> {
                meta.displayName(Component.text((completed ? "§a" : active ? "§e" : "§7") + quest.getName()));
                meta.lore(stepLore);
                meta.setEnchantmentGlintOverride(completed || active);
            }));

            if (i < visibleSteps - 1 && questIndex + 1 < steps.size()) {
                int linkSlot = slot + 1;
                boolean previousCompleted = questIndex < currentStep;
                Material linkType = previousCompleted ? Material.LIME_STAINED_GLASS_PANE : Material.GRAY_STAINED_GLASS_PANE;

                content.put(linkSlot, new ItemBuilder(this, linkType, meta ->
                        meta.displayName(Component.text(""))));
            }
        }

        if (offset > 0) {
            content.put(28, new ItemBuilder(this, Material.ARROW,
                    meta -> meta.displayName(Component.text("§e◀ Étape précédente")))
                    .setOnClick(click -> {
                        if (offset > 0) {
                            offset--;
                            open();
                        }
                    })
            );
        }

        if (offset + visibleSteps < steps.size()) {
            content.put(34, new ItemBuilder(this, Material.ARROW,
                    meta -> meta.displayName(Component.text("§eÉtape suivante ▶")))
                    .setOnClick(click -> {
                        if (offset + visibleSteps < steps.size()) {
                            offset++;
                            open();
                        }
                    })
            );
        }


        content.put(53, new ItemBuilder(this, Material.BARRIER, meta ->
                meta.displayName(Component.text("§cRetour"))).setBackButton());

        return content;
    }

    @Override
    public void onClose(InventoryCloseEvent event) {
    }

    @Override
    public List<Integer> getTakableSlot() {
        return List.of();
    }
}