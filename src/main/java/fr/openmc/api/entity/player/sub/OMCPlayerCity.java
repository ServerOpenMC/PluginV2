package fr.openmc.api.entity.player.sub;

import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

public class OMCPlayerCity extends OMCPlayerFeat {
    public OMCPlayerCity(Player player) {
        super(player);
    }

    @Nullable
    public City getCity() {
        return CityManager.getCity(getUniqueId());
    }

    public boolean hasCity() {
        return getCity() != null;
    }
}
