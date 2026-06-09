package fr.openmc.core.features.events.contents.dailyevents.contents.goldenharvest;

import fr.openmc.core.features.events.contents.dailyevents.models.dailyevent.DailyEvent;
import fr.openmc.core.features.events.contents.dailyevents.models.dailyevent.HasToast;
import fr.openmc.core.utils.nms.toast.CustomToastData;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.Component;
import net.minecraft.advancements.AdvancementType;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class GoldenHarvestEvent extends DailyEvent implements HasToast {
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
                "feature.dailyevents.toast.goldenharvest.start",
                AdvancementType.CHALLENGE
        );
    }

    @Override
    public CustomToastData getEndToastData() {
        return new CustomToastData(
                this.getIcon(),
                "feature.dailyevents.toast.goldenharvest.end",
                AdvancementType.GOAL
        );
    }
}
