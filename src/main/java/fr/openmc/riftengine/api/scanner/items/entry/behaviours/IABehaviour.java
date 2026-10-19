package fr.openmc.riftengine.api.scanner.items.entry.behaviours;

import fr.openmc.riftengine.api.scanner.items.entry.ItemInfo;
import fr.openmc.riftengine.api.scanner.items.entry.behaviours.block.BlocksBehaviour;

import java.util.Map;

public interface IABehaviour {
    static IABehaviour from(ItemInfo info, Map<?, ?> data) throws Exception {
        if (data == null) return new EmptyBehaviour();

        Object resourceObj = data.get("behaviours");
        Object specificPropertiesObj = data.get("specific_properties");

        if (resourceObj == null && specificPropertiesObj == null) return new EmptyBehaviour();

        Map<?, ?> behaviours;

        if (resourceObj instanceof Map<?, ?> b) {
            behaviours = b;
        } else if (specificPropertiesObj instanceof Map<?, ?> b) {
            behaviours = b;
        } else return new EmptyBehaviour();


        IABehaviour behaviour = new EmptyBehaviour();

        if (behaviours.get("block") instanceof Map<?, ?> block) {
            behaviour = BlocksBehaviour.from(info, block);
        }

        return behaviour;
    }

    default boolean isItem() {
        return !this.isBlock();
    }

    default boolean isBlock() {
        return this instanceof BlocksBehaviour;
    }
}
