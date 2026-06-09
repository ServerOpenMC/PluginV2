package fr.openmc.core.features.events.contents.dailyevents.contents.bloodynight;

import fr.openmc.core.features.events.contents.dailyevents.models.dailyevent.DailyEvent;
import fr.openmc.core.features.events.contents.dailyevents.models.dailyevent.HasToast;
import fr.openmc.core.utils.nms.toast.CustomToastData;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.Component;
import net.minecraft.advancements.AdvancementType;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class BloodyNightEvent extends DailyEvent implements HasToast {
    @Override
    public String getEventId() {
        return "bloody_night";
    }

    @Override
    public String getWorldEvent() {
        return "world";
    }

    @Override
    public int getDuration() {
        return 20;
    }

    @Override
    public Runnable onStart() {
        return () -> {
            System.out.println("BLOODY NIGHT START");
        };
    }

    @Override
    public Runnable onEnd() {
        return () -> {
            System.out.println("BLOODY NIGHT END");
        };
    }

    @Override
    public Component getName() {
        return TranslationManager.translation("feature.dailyevents.bloodynight.name");
    }

    @Override
    public List<Component> getDescription() {
        return TranslationManager.translationLore("feature.dailyevents.bloodynight.lore");
    }

    @Override
    public ItemStack getIcon() {
        return new ItemStack(Material.REDSTONE);
    }

    @Override
    public CustomToastData getStartToastData() {
        return new CustomToastData(
                this.getIcon(),
                "feature.dailyevents.toast.bloodynight.start",
                AdvancementType.CHALLENGE
        );
    }

    @Override
    public CustomToastData getEndToastData() {
        return new CustomToastData(
                this.getIcon(),
                "feature.dailyevents.toast.bloodynight.end",
                AdvancementType.GOAL
        );
    }
}
