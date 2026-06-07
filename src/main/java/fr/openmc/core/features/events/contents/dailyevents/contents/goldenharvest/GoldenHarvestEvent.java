package fr.openmc.core.features.events.contents.dailyevents.contents.goldenharvest;

import fr.openmc.core.features.events.contents.dailyevents.models.DailyEvent;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class GoldenHarvestEvent extends DailyEvent {
    @Override
    public String getEventId() {
        return "golden_harvest";
    }

    @Override
    public int getDuration() {
        return 40;
    }

    @Override
    public Runnable onStart() {
        return () -> {
            System.out.println("GOLDEN HARVEST START");
        };
    }

    @Override
    public Runnable onEnd() {
        return () -> {
            System.out.println("GOLDEN HARVEST END");
        };
    }

    @Override
    public Component getName() {
        return TranslationManager.translation("feature.dailyevents.goldenharvest.name");
    }

    @Override
    public List<Component> getDescription() {
        return List.of();
    }

    @Override
    public ItemStack getIcon() {
        return new ItemStack(Material.GOLDEN_HOE);
    }
}
