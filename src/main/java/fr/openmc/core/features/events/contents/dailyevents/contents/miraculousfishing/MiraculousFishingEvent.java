package fr.openmc.core.features.events.contents.dailyevents.contents.miraculousfishing;

import fr.openmc.api.menulib.Menu;
import fr.openmc.core.OMCRegistry;
import fr.openmc.core.bootstrap.features.types.HasListeners;
import fr.openmc.core.features.events.contents.dailyevents.contents.miraculousfishing.listeners.EatKebabFermentedListener;
import fr.openmc.core.features.events.contents.dailyevents.contents.miraculousfishing.listeners.PlayerFishListener;
import fr.openmc.core.features.events.contents.dailyevents.contents.miraculousfishing.listeners.PlayerNotPickUpListener;
import fr.openmc.core.features.events.contents.dailyevents.contents.miraculousfishing.menu.MiraculousFishingMenu;
import fr.openmc.core.features.events.contents.dailyevents.models.dailyevent.DailyEvent;
import fr.openmc.core.features.events.contents.dailyevents.models.dailyevent.HasAmbient;
import fr.openmc.core.features.events.contents.dailyevents.models.dailyevent.HasBroadcast;
import fr.openmc.core.features.events.contents.dailyevents.models.dailyevent.HasToast;
import fr.openmc.core.features.events.models.HasMenu;
import fr.openmc.core.registry.ambient.CustomAmbient;
import fr.openmc.core.utils.nms.toast.CustomToastData;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minecraft.advancements.AdvancementType;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Set;

public class MiraculousFishingEvent extends DailyEvent
        implements HasToast, HasAmbient, HasBroadcast, HasListeners, HasMenu {
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
            World world = Bukkit.getWorld(getWorldEvent());

            if (world == null) return;

            world.setWeatherDuration(getDuration() * 20 * 20);
        };
    }

    @Override
    public Runnable onEnd() {
        return () -> {};
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
        return TranslationManager.translation("feature.dailyevents.miraculousfishing.broadcast.start",
                Component.text(FishingAttributeManager.FISHING_SPEED_MODIFIER * 100 + "%", NamedTextColor.AQUA));
    }

    @Override
    public Component getEndBroadcast() {
        return TranslationManager.translation("feature.dailyevents.miraculousfishing.broadcast.end");
    }

    @Override
    public Set<Listener> getListeners() {
        return Set.of(
                new PlayerFishListener(),
                new PlayerNotPickUpListener(),
                new EatKebabFermentedListener()
        );
    }

    @Override
    public Menu getInfoMenu(Player player) {
        return new MiraculousFishingMenu(player);
    }
}
