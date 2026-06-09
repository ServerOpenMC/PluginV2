package fr.openmc.core.features.events.contents.dailyevents.models.dailyevent;

import fr.openmc.core.utils.nms.toast.CustomToastData;
import org.bukkit.entity.Player;

public interface HasToast {
    CustomToastData getStartToastData();
    CustomToastData getEndToastData();

    default void runStartToast(Player player) {

    }
}
