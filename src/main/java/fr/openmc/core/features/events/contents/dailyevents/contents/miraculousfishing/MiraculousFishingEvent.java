package fr.openmc.core.features.events.contents.dailyevents.contents.miraculousfishing;

import fr.openmc.core.OMCRegistry;
import fr.openmc.core.bootstrap.features.types.HasListeners;
import fr.openmc.core.features.events.contents.dailyevents.contents.miraculousfishing.listeners.PlayerFishListener;
import fr.openmc.core.features.events.contents.dailyevents.contents.miraculousfishing.listeners.PlayerNotPickUpListener;
import fr.openmc.core.features.events.contents.dailyevents.models.dailyevent.DailyEvent;
import fr.openmc.core.features.events.contents.dailyevents.models.dailyevent.HasAmbient;
import fr.openmc.core.features.events.contents.dailyevents.models.dailyevent.HasBroadcast;
import fr.openmc.core.features.events.contents.dailyevents.models.dailyevent.HasToast;
import fr.openmc.core.registry.ambient.CustomAmbient;
import fr.openmc.core.utils.nms.toast.CustomToastData;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.Component;
import net.minecraft.advancements.AdvancementType;
import org.bukkit.Material;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Set;

public class MiraculousFishingEvent extends DailyEvent implements HasToast, HasAmbient, HasBroadcast, HasListeners {
    @Override
    public String getEventId() {
        return "miraculous_fishing";
    }

    @Override
    public String getWorldEvent() {
        return "world";
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

    @Override
    public CustomToastData getStartToastData() {
        return new CustomToastData(
                this.getIcon(),
                TranslationManager.translation("feature.dailyevents.miraculousfishing.toast.start"),
                AdvancementType.CHALLENGE
        );
    }

    @Override
    public CustomToastData getEndToastData() {
        return new CustomToastData(
                this.getIcon(),
                TranslationManager.translation("feature.dailyevents.miraculousfishing.toast.end"),
                AdvancementType.GOAL
        );
    }

    @Override
    public CustomAmbient getAmbient() {
        return OMCRegistry.CUSTOM_AMBIENTS.BLESSED;
    }

    @Override
    public Component getStartBroadcast() {
        return TranslationManager.translation("feature.dailyevents.miraculousfishing.broadcast.start");
    }

    @Override
    public Component getEndBroadcast() {
        return TranslationManager.translation("feature.dailyevents.miraculousfishing.broadcast.end");
    }

    @Override
    public Set<Listener> getListeners() {
        return Set.of(
                new PlayerFishListener(),
                new PlayerNotPickUpListener()
        );
    }
}
