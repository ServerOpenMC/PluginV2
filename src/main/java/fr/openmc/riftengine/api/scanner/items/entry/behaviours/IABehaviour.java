package fr.openmc.riftengine.api.scanner.items.entry.behaviours;

import fr.openmc.riftengine.api.scanner.equipements.EquipementEntry;
import fr.openmc.riftengine.api.scanner.items.entry.ItemInfo;
import fr.openmc.riftengine.api.scanner.items.entry.behaviours.armors.ArmorsBehaviour;
import fr.openmc.riftengine.api.scanner.items.entry.behaviours.block.BlocksBehaviour;

import java.util.List;
import java.util.Map;

public interface IABehaviour {
    static IABehaviour from(ItemInfo info, Map<?, ?> data, List<EquipementEntry> equipements) throws Exception {
        if (data == null) return new EmptyBehaviour();
        Object resourceObj = data.get("behaviours");

        IABehaviour behaviour = BlocksBehaviour.detectBlockBehaviour(info, data, resourceObj);

        if (behaviour instanceof EmptyBehaviour) {
            return ArmorsBehaviour.detectArmorBehaviour(info, data, equipements);
        }

        return behaviour;
    }

    default boolean isItem() {
        return !this.isBlock();
    }

    default boolean isArmor() {
        return this instanceof ArmorsBehaviour;
    }

    default boolean isBlock() {
        return this instanceof BlocksBehaviour;
    }
}
