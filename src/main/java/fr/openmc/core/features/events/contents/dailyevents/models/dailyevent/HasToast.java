package fr.openmc.core.features.events.contents.dailyevents.models.dailyevent;

import fr.openmc.core.utils.nms.toast.CustomToastData;

/**
 * Wrapper sous forme d'interface afin d'implementer les toasts dans les événnements.
 */
public interface HasToast {
    CustomToastData getStartToastData();
    CustomToastData getEndToastData();
}
