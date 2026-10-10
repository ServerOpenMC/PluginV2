package fr.openmc.api.omcplayer.sub;

import fr.openmc.api.cooldown.Cooldown;
import fr.openmc.api.cooldown.DynamicCooldownManager;
import fr.openmc.core.OMCRegistry;
import fr.openmc.core.features.settings.PlayerSettings;
import fr.openmc.core.features.settings.PlayerSettingsManager;
import fr.openmc.core.features.settings.SettingType;
import fr.openmc.core.utils.text.DateUtils;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;

public class OMCPlayerCooldown extends OMCPlayerFeat {
    private static final DynamicCooldownManager DYNAMIC_COOLDOWN_MANAGER = OMCRegistry.FEATURES.DYNAMIC_COOLDOWN.get();

    public OMCPlayerCooldown(OfflinePlayer player) {
        super(player);
    }

    public Map<String, Cooldown> getCooldowns() {
        return DYNAMIC_COOLDOWN_MANAGER.getCooldowns(getUniqueId());
    }

    public boolean isReady(String group) {
        return DYNAMIC_COOLDOWN_MANAGER.isReady(getUniqueId(), group);
    }

    public void use(String group, long duration) {
        DYNAMIC_COOLDOWN_MANAGER.use(getUniqueId(), group, duration);
    }

    public long getRemaining(String group) {
        return DYNAMIC_COOLDOWN_MANAGER.getRemaining(getUniqueId(), group);
    }

    public String getRemainingFormatted(String group) {
        return DateUtils.convertMillisToTime(DYNAMIC_COOLDOWN_MANAGER.getRemaining(getUniqueId(), group));
    }

    public void clear(String group, boolean callEvent) {
        DYNAMIC_COOLDOWN_MANAGER.clear(getUniqueId(), group, callEvent);
    }

    public void reduceCooldown(String group, long reductionMillis) {
        DYNAMIC_COOLDOWN_MANAGER.reduceCooldown(getPlayer(), getUniqueId(), group, reductionMillis);
    }
}

