package fr.openmc.core.features.events.contents.dailyevents.contents.miraculousfishing.registry;

import fr.openmc.core.registry.items.CustomItem;
import fr.openmc.core.registry.loottable.loots.CustomLoot;
import fr.openmc.core.registry.loottable.loots.RepresentedItem;
import fr.openmc.core.registry.loottable.loots.menu.LootsInfoMenu;
import fr.openmc.core.registry.mobs.CustomMobEntry;
import fr.openmc.core.utils.EnumUtils;
import fr.openmc.core.utils.text.messages.TranslationManager;
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
    private final ItemStack item;
    private boolean throwCreature = true;

    public SeaCreatureLoot(CustomMobEntry seaCreatureMob, double chance) {
        this(seaCreatureMob, chance, true);
    }

    public SeaCreatureLoot(CustomMobEntry seaCreatureMob, double chance, boolean throwCreature) {
        this.chance = chance;
        this.seaCreatureMob = seaCreatureMob;
        this.item = ItemStack.of(EnumUtils.match(seaCreatureMob.getMobSnapshot().getEntityType().name() + "_SPAWN_EGG", Material.class, Material.BARRIER));
        this.throwCreature = throwCreature;
    }

    public SeaCreatureLoot(CustomMobEntry seaCreatureMob, ItemStack item, double chance) {
        this(seaCreatureMob, item, chance, true);
    }

    public SeaCreatureLoot(CustomMobEntry seaCreatureMob, ItemStack item, double chance, boolean throwCreature) {
        this.chance = chance;
        this.seaCreatureMob = seaCreatureMob;
        this.item = item;
        this.throwCreature = throwCreature;
    }

    public SeaCreatureLoot(CustomMobEntry seaCreatureMob, Material item, double chance) {
        this(seaCreatureMob, ItemStack.of(item), chance);
    }

    public SeaCreatureLoot(CustomMobEntry seaCreatureMob, CustomItem item, double chance) {
        this(seaCreatureMob, item.getBest(), chance);
    }

    public SeaCreatureLoot(CustomMobEntry seaCreatureMob, CustomItem item, double chance, boolean throwCreature) {
        this(seaCreatureMob, item.getBest(), chance, throwCreature);
    }

    @Override
    public Component getDisplayText() {
        return Component.text(seaCreatureMob.getMob().getName(), NamedTextColor.AQUA)
                .decoration(TextDecoration.ITALIC, false);
    }

    @Override
    public ItemStack getRepresentativeItem() {
        return item;
    }

    @Override
    public Set<CustomLoot> run(Player receiver) {
        // déjà lancé dans MiraculousFishingManager.simulateLaunchLoot
        return Collections.singleton(this);
    }

    public void showLoot(Player player) {
        new LootsInfoMenu(
                player,
                TranslationManager.translation("feature.dailyevents.miraculousfishing.menu.loot_info.sea_creature"),
                seaCreatureMob.getMob().getLoots()
        ).open();
    }
}