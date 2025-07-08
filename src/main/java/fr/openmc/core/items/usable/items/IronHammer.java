package fr.openmc.core.items.usable.items;

import fr.openmc.core.items.usable.AbstractHammer;
import org.bukkit.Material;

public class IronHammer extends AbstractHammer {

    public IronHammer() {
        super(
                "omc_items:iron_hammer",
                Material.IRON_PICKAXE,
                1,
                0
        );
    }

}
