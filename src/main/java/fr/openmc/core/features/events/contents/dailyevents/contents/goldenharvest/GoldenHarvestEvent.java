package fr.openmc.core.features.events.contents.dailyevents.contents.goldenharvest;

import fr.openmc.core.OMCRegistry;
import fr.openmc.core.bootstrap.features.Feature;
import fr.openmc.core.bootstrap.features.types.HasFeature;
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
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class GoldenHarvestEvent extends DailyEvent
        implements HasToast, HasAmbient, HasBroadcast, HasFeature {
    @Override
    public String getEventId() {
        return "golden_harvest";
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
        return () -> {};
    }

    @Override
    public Runnable onEnd() {
        return () -> {};
    }

    @Override
    public Component getName() {
        return TranslationManager.translation("feature.dailyevents.goldenharvest.name");
    }

    @Override
    public List<Component> getDescription() {
        return TranslationManager.translationLore("feature.dailyevents.goldenharvest.lore");
    }

    @Override
    public ItemStack getIcon() {
        return new ItemStack(Material.GOLDEN_HOE);
    }

    @Override
    public CustomToastData getStartToastData() {
        return new CustomToastData(
                this.getIcon(),
                TranslationManager.translation("feature.dailyevents.goldenharvest.toast.start"),
                AdvancementType.CHALLENGE
        );
    }

    @Override
    public CustomToastData getEndToastData() {
        return new CustomToastData(
                this.getIcon(),
                TranslationManager.translation("feature.dailyevents.goldenharvest.toast.end"),
                AdvancementType.GOAL
        );
    }

    @Override
    public CustomAmbient getAmbient() {
        return OMCRegistry.CUSTOM_AMBIENTS.GOLDEN;
    }

    @Override
    public Component getStartBroadcast() {
        return TranslationManager.translation("feature.dailyevents.goldenharvest.broadcast.start");
    }

    @Override
    public Component getEndBroadcast() {
        return TranslationManager.translation("feature.dailyevents.goldenharvest.broadcast.end");
    }

    @Override
    public Feature getFeature() {
        return new GoldenHarvestManager();
    }
}
