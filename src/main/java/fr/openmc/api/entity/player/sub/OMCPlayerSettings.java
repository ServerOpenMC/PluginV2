package fr.openmc.api.entity.player.sub;

import fr.openmc.core.features.settings.PlayerSettings;
import fr.openmc.core.features.settings.PlayerSettingsManager;
import fr.openmc.core.features.settings.SettingType;
import org.bukkit.entity.Player;

import java.util.UUID;

public class OMCPlayerSettings extends OMCPlayerFeat {
    public OMCPlayerSettings(Player player) {
        super(player);
    }

    public PlayerSettings getSettings() {
        return PlayerSettingsManager.getPlayerSettings(getUniqueId());
    }

    public boolean canReceiveFriendRequest(UUID senderUUID) {
        return this.getSettings().canPerformAction(SettingType.FRIEND_REQUESTS_POLICY, senderUUID);
    }

    public boolean shouldPlayNotificationSound() {
        return this.getSettings().getSetting(SettingType.NOTIFICATIONS_SOUND);
    }

    public boolean canReceiveCityInvite(UUID receiverUUID) {
        return this.getSettings().canPerformAction(SettingType.CITY_JOIN_REQUESTS_POLICY, receiverUUID);
    }

    public boolean canReceivePrivateMessage(UUID senderUUID) {
        return this.getSettings().canPerformAction(SettingType.PRIVATE_MESSAGE_POLICY, senderUUID);
    }
}
