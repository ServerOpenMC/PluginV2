package fr.openmc.core.features.events.contents.christmas.mecanism.dailyevent;

import fr.openmc.api.menulib.Menu;
import fr.openmc.core.OMCRegistry;
import fr.openmc.core.bootstrap.features.Feature;
import fr.openmc.core.bootstrap.features.types.HasFeature;
import fr.openmc.core.features.events.contents.dailyevents.contents.miraculousfishing.MiraculousFishingManager;
import fr.openmc.core.features.events.contents.dailyevents.contents.miraculousfishing.menu.MiraculousFishingMenu;
import fr.openmc.core.features.events.contents.dailyevents.models.dailyevent.*;
import fr.openmc.core.features.events.models.HasMenu;
import fr.openmc.core.registry.ambient.CustomAmbient;
import fr.openmc.core.utils.nms.toast.CustomToastData;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minecraft.advancements.AdvancementType;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class BlizzardEvent extends DailyEvent
        implements HasToast, HasAmbient, HasBroadcast, HasMenu, HasFeature, HasBossBar {
    @Override
    public String getEventId() {
        return "blizzard";
    }

    @Override
    public String getWorldEvent() {
        return "world";
    }

    @Override
    public int getDuration() {
        return 60;
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
        return TranslationManager.translation("feature.dailyevents.blizzard.name");
    }

    @Override
    public List<Component> getDescription() {
        return TranslationManager.translationLore("feature.dailyevents.blizzard.lore");
    }

    @Override
    public ItemStack getIcon() {
        return new ItemStack(Material.PACKED_ICE);
    }

    @Override
    public NamedTextColor getMainColor() {
        return NamedTextColor.AQUA;
    }

    @Override
    public CustomToastData getStartToastData() {
        return new CustomToastData(
                this.getIcon(),
                TranslationManager.translation("feature.dailyevents.blizzard.toast.start"),
                AdvancementType.CHALLENGE
        );
    }

    @Override
    public CustomToastData getEndToastData() {
        return new CustomToastData(
                this.getIcon(),
                TranslationManager.translation("feature.dailyevents.blizzard.toast.end"),
                AdvancementType.GOAL
        );
    }

    @Override
    public CustomAmbient getAmbient() {
        return OMCRegistry.CUSTOM_AMBIENTS.BLIZZARD;
    }

    @Override
    public Component getStartBroadcast() {
        return TranslationManager.translation("feature.dailyevents.blizzard.broadcast.start");
    }

    @Override
    public Component getEndBroadcast() {
        return TranslationManager.translation("feature.dailyevents.blizzard.broadcast.end");
    }

    @Override
    public Menu getInfoMenu(Player player) {
        // todo: info menu
        return new MiraculousFishingMenu(player);
    }

    @Override
    public Feature getFeature() {
        return new MiraculousFishingManager();
    }

    @Override
    public BossBar.Color getBossBarColor() {
        return BossBar.Color.WHITE;
    }

    @Override
    public BossBar.Overlay getBossBarOverlay() {
        return BossBar.Overlay.NOTCHED_12;
    }
}
