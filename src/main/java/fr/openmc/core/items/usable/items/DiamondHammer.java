package fr.openmc.core.items.usable.items;

import fr.openmc.core.items.usable.AbstractHammer;
import org.bukkit.Material;

public class DiamondHammer extends AbstractHammer {

    public DiamondHammer() {
        super(
                "omc_items:diamond_hammer",
                Material.DIAMOND_PICKAXE,
                1,
                1
        );
    }

}
