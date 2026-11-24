package fr.openmc.core.features.events.contents.christmas.contents.mobs;

import fr.openmc.core.features.events.contents.christmas.mecanism.gift.models.GiftType;
import fr.openmc.core.registry.mobs.CustomMob;
import org.bukkit.Location;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Transformation;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class GiftMob extends CustomMob<ItemDisplay> {
    private final ItemStack toDisplay;

    private static final Transformation TRANSFORMATION = new Transformation(
            new Vector3f(0f, 0f, 0f),
            new Quaternionf(),
            new Vector3f(1f, 1f, 1f),
            new Quaternionf()
    );

    public GiftMob(String id, GiftType giftType) {
        super(id,
                giftType.getTranslatableName(),
                ItemDisplay.class,
                1,
                1
        );
        this.toDisplay = giftType.getGiftItem().getBest();
    }

    @Override
    public ItemDisplay spawn(Location spawnLocation) {
        Location center = spawnLocation.clone()
                .add(0.5, 0.75, 0.5);
        center.setYaw(0f);
        center.setPitch(0f);

        ItemDisplay gift = center.getWorld().spawn(
                center,
                ItemDisplay.class,
                CreatureSpawnEvent.SpawnReason.CUSTOM
        );

        registerAsCustomMob(gift);

        gift.setItemStack(toDisplay);
        gift.setItemDisplayTransform(ItemDisplay.ItemDisplayTransform.NONE);
        gift.setTransformation(TRANSFORMATION);

        return gift;
    }
}

