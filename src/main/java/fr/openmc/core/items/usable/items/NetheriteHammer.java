package fr.openmc.core.items.usable.items;

import fr.openmc.core.items.usable.AbstractHammer;
import org.bukkit.Material;

public class NetheriteHammer extends AbstractHammer {

    public NetheriteHammer() {
        super(
                "omc_items:netherite_hammer",
                Material.NETHERITE_PICKAXE,
                1,
                2
        );
    }

}
