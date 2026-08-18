package fr.openmc.api.entity.player.sub;

import fr.openmc.core.OMCRegistry;
import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

public class OMCPlayerCity extends OMCPlayerFeat {
    private final CityManager cityManager;

    public OMCPlayerCity(Player player) {
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
