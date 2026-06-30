package fr.openmc.core.features.events.contents.dailyevents.contents.miraculousfishing.contents.mobs;

import fr.openmc.core.registry.loottable.loots.ItemLoot;
import fr.openmc.core.registry.loottable.loots.XpLoot;
import fr.openmc.core.registry.mobs.CustomMob;
import fr.openmc.core.utils.RandomUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Drowned;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class SeaGuard extends CustomMob<Drowned> {
    public SeaGuard(String id) {
        super(id,
                "Gardien des mers",
                Drowned.class,
                30,
                7,
                RandomUtils.randomBetween(0.2, 0.3),
                List.of(
                        new ItemLoot(Material.ROTTEN_FLESH,
                                1, 3, 20),
                        new XpLoot(10, 30, 1)
                )
        );
    }

    @Override
    public Drowned spawn(Location spawnLocation) {
        Drowned drowned = this.getPreBuildMob(spawnLocation);

        Random random = ThreadLocalRandom.current();
        if (random.nextBoolean())
            drowned.getEquipment().setHelmet(ItemStack.of(Material.COPPER_HELMET));
        if (random.nextBoolean())
            drowned.getEquipment().setChestplate(ItemStack.of(Material.COPPER_CHESTPLATE));
        if (random.nextBoolean())
            drowned.getEquipment().setLeggings(ItemStack.of(Material.IRON_LEGGINGS));
        if (random.nextBoolean())
            drowned.getEquipment().setBoots(ItemStack.of(Material.COPPER_BOOTS));
        if (random.nextBoolean())
            drowned.getEquipment().setItemInMainHand(getDrownedTrident());

        drowned.setAggressive(true);
        drowned.setShouldBurnInDay(false);

        return drowned;
    }

    private ItemStack getDrownedTrident() {
        ItemStack trident = new ItemStack(Material.TRIDENT);

        if (ThreadLocalRandom.current().nextBoolean())
            trident.addEnchantment(Enchantment.IMPALING, 3);
        if (ThreadLocalRandom.current().nextBoolean())
            trident.addEnchantment(Enchantment.RIPTIDE, 3);

        return trident;
    }
}
