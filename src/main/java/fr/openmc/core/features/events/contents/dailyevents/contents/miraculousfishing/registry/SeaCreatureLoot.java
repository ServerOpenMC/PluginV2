package fr.openmc.core.features.events.contents.dailyevents.contents.miraculousfishing.registry;

import fr.openmc.core.registry.loottable.loots.CustomLoot;
import fr.openmc.core.registry.loottable.loots.RepresentedItem;
import fr.openmc.core.registry.mobs.CustomMobEntry;
import fr.openmc.core.utils.EnumUtils;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.Set;

@Getter
public class SeaCreatureLoot implements CustomLoot, RepresentedItem {
    @Setter
    private double chance;
    private final CustomMobEntry seaCreatureMob;

    public SeaCreatureLoot(CustomMobEntry seaCreatureMob, double chance) {
        this.chance = chance;
        this.seaCreatureMob = seaCreatureMob;
    }

    @Override
    public Component getDisplayText() {
        return Component.text(seaCreatureMob.getMob().getName(), NamedTextColor.AQUA)
                .decoration(TextDecoration.ITALIC, false);
    }

    @Override
    public ItemStack getRepresentativeItem() {
        return ItemStack.of(EnumUtils.match(seaCreatureMob.getMobSnapshot().getEntityType().name() + "_SPAWN_EGG", Material.class, Material.BARRIER));
    }

    @Override
    public Set<CustomLoot> run(Player receiver) {
        // déjà lancé dans MiraculousFishingManager.simulateLaunchLoot
        return Collections.singleton(this);
    }
}