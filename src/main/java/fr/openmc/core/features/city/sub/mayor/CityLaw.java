package fr.openmc.core.features.city.sub.mayor;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;


@Setter
@Getter
public class CityLaw {
    private boolean pvp;
    private Location warp;

    public CityLaw(boolean pvp, Location warp) {
        this.pvp = pvp;
        this.warp = warp;
    }

}