package fr.openmc.api.omcplayer.sub;

import fr.openmc.api.omcplayer.feature.OMCOfflinePlayerFeat;
import fr.openmc.core.OMCRegistry;
import fr.openmc.core.features.settings.PlayerSettings;
import fr.openmc.core.features.settings.PlayerSettingsManager;
import fr.openmc.core.features.settings.SettingType;
import org.bukkit.OfflinePlayer;

import java.util.UUID;

public class OMCPlayerSettings extends OMCOfflinePlayerFeat {
    private static final PlayerSettingsManager PLAYER_SETTINGS = OMCRegistry.FEATURES.PLAYER_SETTINGS.get();

    public OMCPlayerSettings(OfflinePlayer player) {
        super(player);
    }

    public PlayerSettings getSettings() {
        return PLAYER_SETTINGS.getPlayerSettings(getUniqueId());
    }

    public boolean canReceiveFriendRequest(UUID senderUUID) {
        return this.getSettings().canPerformAction(SettingType.FRIEND_REQUESTS_POLICY, senderUUID);
    }

    public boolean canReceiveLetter(UUID senderUUID) {
        return this.getSettings().canPerformAction(SettingType.MAILBOX_RECEIVE_POLICY, senderUUID);
    }

    public boolean shouldPlayMascotSound() {
        return this.getSettings().getSetting(SettingType.MASCOT_PLAY_SOUND_POLICY);
    }

    public boolean shouldPlayNotificationSound() {
        return this.getSettings().getSetting(SettingType.NOTIFICATIONS_SOUND);
    }

    public boolean shouldPlayJoinAnimation() {
        return this.getSettings().getSetting(SettingType.JOIN_ANIMATION);
    }

    public boolean shouldShowTeleportFade() {
        return this.getSettings().getSetting(SettingType.TELEPORT_TITLE_FADE);
    }

    public boolean canReceiveCityInvite(UUID receiverUUID) {
        return this.getSettings().canPerformAction(SettingType.CITY_JOIN_REQUESTS_POLICY, receiverUUID);
    }

    public boolean canReceivePrivateMessage(UUID senderUUID) {
        return this.getSettings().canPerformAction(SettingType.PRIVATE_MESSAGE_POLICY, senderUUID);
    }
}
