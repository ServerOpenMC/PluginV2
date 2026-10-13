package fr.openmc.api.omcplayer.sub;

import fr.openmc.api.omcplayer.feature.OMCOfflinePlayerFeat;
import fr.openmc.core.OMCRegistry;
import fr.openmc.core.features.bits.BitsManager;
import fr.openmc.core.features.bits.models.BitsPlayer;
import org.bukkit.OfflinePlayer;

public class OMCPlayerBits extends OMCOfflinePlayerFeat {
    private final BitsManager bitsManager = OMCRegistry.FEATURES.BITS.get();

    public OMCPlayerBits(OfflinePlayer player) {
        super(player);
    }

    public double getBits() {
        return bitsManager.getBits(getUniqueId());
    }

    public String getFormattedBits() {
        return bitsManager.getFormattedBits(getUniqueId());
    }

    public void addBits(double amount) {
        bitsManager.addBits(getUniqueId(), amount);
    }

    public boolean withdrawBits(double amount) {
        return bitsManager.withdrawBits(getUniqueId(), amount);
    }

    public void setBits(double amount) {
        bitsManager.setBits(getUniqueId(), amount);
    }

    public BitsPlayer getBitsPlayer() {
        return bitsManager.getBitsPlayer(getUniqueId());
    }
}
