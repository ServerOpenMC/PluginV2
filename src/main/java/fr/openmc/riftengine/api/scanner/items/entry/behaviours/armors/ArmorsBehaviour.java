package fr.openmc.riftengine.api.scanner.items.entry.behaviours.armors;

import fr.openmc.core.utils.YmlUtils;
import fr.openmc.riftengine.api.scanner.equipements.EquipementEntry;
import fr.openmc.riftengine.api.scanner.items.entry.ItemInfo;
import fr.openmc.riftengine.api.scanner.items.entry.behaviours.EmptyBehaviour;
import fr.openmc.riftengine.api.scanner.items.entry.behaviours.IABehaviour;

import java.util.List;
import java.util.Map;

public record ArmorsBehaviour(EquipementEntry entry, ArmorType type) implements IABehaviour {
    private static ArmorsBehaviour from(ItemInfo itemInfo, List<EquipementEntry> equipements, Map<?, ?> equipement) {
        String id = YmlUtils.getString(equipement, "id", null);

        EquipementEntry entry = equipements.stream()
                .filter(e -> e.namespacedId().equals(id))
                .findFirst()
                .orElse(null);

        ArmorType type = ArmorType.from(itemInfo.material());

        return new ArmorsBehaviour(entry, type);
    }

    public static IABehaviour detectArmorBehaviour(ItemInfo info, Map<?, ?> data, List<EquipementEntry> equipements) {
        Map<?, ?> equipment = YmlUtils.getMap(data, "equipment");

        if (equipment == null) return new EmptyBehaviour();

        return ArmorsBehaviour.from(info, equipements, equipment);
    }
}
