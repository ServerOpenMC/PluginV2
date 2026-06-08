package fr.openmc.core.features.events.contents.dailyevents.contents.miraculousfishing;

import fr.openmc.core.features.events.contents.dailyevents.models.DailyEvent;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class MiraculousFishingEvent extends DailyEvent {
    @Override
    public String getEventId() {
        return "miraculous_fishing";
    }

    @Override
    public int getDuration() {
        return 30;
    }

    @Override
    public Runnable onStart() {
        return () -> {
            System.out.println("MIRACULOUS FISHING START");
        };
    }

    @Override
    public Runnable onEnd() {
        return () -> {
            System.out.println("MIRACULOUS FISHING END");
        };
    }

    @Override
    public Component getName() {
        return TranslationManager.translation("feature.dailyevents.miraculousfishing.name");
    }

    @Override
    public List<Component> getDescription() {
        return TranslationManager.translationLore("feature.dailyevents.miraculousfishing.lore");
    }

    @Override
    public ItemStack getIcon() {
        return new ItemStack(Material.FISHING_ROD);
    }
}
