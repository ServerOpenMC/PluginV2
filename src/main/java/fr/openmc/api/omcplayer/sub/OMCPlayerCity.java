package fr.openmc.api.omcplayer.sub;

import fr.openmc.api.omcplayer.feature.OMCOfflinePlayerFeat;
import fr.openmc.core.OMCRegistry;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.models.city.City;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.Nullable;


public class OMCPlayerCity extends OMCOfflinePlayerFeat {
    private final CityManager cityManager;

    public OMCPlayerCity(OfflinePlayer player) {
        super(player);
        this.cityManager = OMCRegistry.FEATURES.CITY.get();
    }

    @Nullable
    public City getCity() {
        return cityManager.getCity(getUniqueId());
    }

    public boolean hasCity() {
        return getCity() != null;
    }
}
