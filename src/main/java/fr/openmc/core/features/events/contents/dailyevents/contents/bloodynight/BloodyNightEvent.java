package fr.openmc.core.features.events.contents.dailyevents.contents.bloodynight;

import fr.openmc.api.menulib.Menu;
import fr.openmc.core.OMCRegistry;
import fr.openmc.core.bootstrap.features.types.HasListeners;
import fr.openmc.core.features.events.contents.dailyevents.contents.bloodynight.listeners.MonsterSpawnLIstener;
import fr.openmc.core.features.events.contents.dailyevents.contents.bloodynight.listeners.PlayerKillMonsterListener;
import fr.openmc.core.features.events.contents.dailyevents.contents.bloodynight.menu.BloodyNightMenu;
import fr.openmc.core.features.events.contents.dailyevents.models.dailyevent.DailyEvent;
import fr.openmc.core.features.events.contents.dailyevents.models.dailyevent.HasAmbient;
import fr.openmc.core.features.events.contents.dailyevents.models.dailyevent.HasBroadcast;
import fr.openmc.core.features.events.contents.dailyevents.models.dailyevent.HasToast;
import fr.openmc.core.features.events.models.HasMenu;
import fr.openmc.core.registry.ambient.CustomAmbient;
import fr.openmc.core.utils.nms.toast.CustomToastData;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.Component;
import net.minecraft.advancements.AdvancementType;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Set;

public class BloodyNightEvent extends DailyEvent
        implements HasToast, HasAmbient, HasBroadcast, HasListeners, HasMenu {
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
        return () -> BloodyNightManager.start(this);
    }

    @Override
    public Runnable onEnd() {
        return () -> BloodyNightManager.stop(this);
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
                TranslationManager.translation("feature.dailyevents.bloodynight.toast.start"),
                AdvancementType.CHALLENGE
        );
    }

    @Override
    public CustomToastData getEndToastData() {
        return new CustomToastData(
                this.getIcon(),
                TranslationManager.translation("feature.dailyevents.bloodynight.toast.end"),
                AdvancementType.GOAL
        );
    }

    @Override
    public CustomAmbient getAmbient() {
        return OMCRegistry.CUSTOM_AMBIENTS.BLOODY;
    }

    @Override
    public Component getStartBroadcast() {
        return TranslationManager.translation("feature.dailyevents.bloodynight.broadcast.start");
    }

    @Override
    public Component getEndBroadcast() {
        return TranslationManager.translation("feature.dailyevents.bloodynight.broadcast.end");
    }

    @Override
    public Set<Listener> getListeners() {
        return Set.of(
                new PlayerKillMonsterListener(),
                new MonsterSpawnLIstener()
        );
    }

    @Override
    public Menu getInfoMenu(Player player) {
        return new BloodyNightMenu(player);
    }
}
