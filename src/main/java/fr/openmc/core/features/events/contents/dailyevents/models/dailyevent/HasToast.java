package fr.openmc.core.features.events.contents.dailyevents.models.dailyevent;

import fr.openmc.core.utils.nms.toast.CustomToastData;

public interface HasToast {
    CustomToastData getStartToastData();
    CustomToastData getEndToastData();
}
