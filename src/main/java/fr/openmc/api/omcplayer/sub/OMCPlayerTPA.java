package fr.openmc.api.omcplayer.sub;

import fr.openmc.api.omcplayer.OMCPlayer;
import fr.openmc.api.omcplayer.feature.OMCPlayerFeat;
import fr.openmc.core.OMCRegistry;
import fr.openmc.core.features.tpa.TPAManager;
import org.bukkit.entity.Player;

import java.util.List;

public class OMCPlayerTPA extends OMCPlayerFeat {
    private final TPAManager tpaManager = OMCRegistry.FEATURES.TPA.get();

    public OMCPlayerTPA(Player player) {
        super(player);
    }

    public boolean hasPendingRequest() {
        return tpaManager.hasPendingRequest(getPlayer());
    }

    public boolean requesterHasPendingRequest() {
        return tpaManager.requesterHasPendingRequest(getPlayer());
    }

    public boolean hasMultipleRequests() {
        return tpaManager.hasMultipleRequests(getPlayer());
    }

    public void addRequest(OMCPlayer target) {
        tpaManager.addRequest(getPlayer(), target);
    }

    public void expireRequest(OMCPlayer target) {
        tpaManager.expireRequest(getPlayer(), target);
    }

    public List<OMCPlayer> getRequesters() {
        return tpaManager.getRequesters(getPlayer());
    }

    public void removeRequest(OMCPlayer target) {
        tpaManager.removeRequest(getPlayer(), target);
    }

    public OMCPlayer getTargetByRequester() {
        return tpaManager.getTargetByRequester(getPlayer());
    }
}
