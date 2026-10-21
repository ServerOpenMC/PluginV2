package fr.openmc.riftengine.api.scanner.items.entry.behaviours.armors;

import fr.openmc.core.utils.YmlUtils;
import fr.openmc.riftengine.api.scanner.equipements.EquipementEntry;
import fr.openmc.riftengine.api.scanner.items.entry.behaviours.EmptyBehaviour;
import fr.openmc.riftengine.api.scanner.items.entry.behaviours.IABehaviour;

import java.util.List;
import java.util.Map;

public record ArmorsBehaviour(EquipementEntry entry) implements IABehaviour {
    private static ArmorsBehaviour from(List<EquipementEntry> equipements, Map<?, ?> equipement) {
        String id = YmlUtils.getString(equipement, "id", null);

        EquipementEntry entry = equipements.stream()
                .filter(e -> e.namespacedId().equals(id))
                .findFirst()
                .orElse(null);

        return new ArmorsBehaviour(entry);
    }

    public static IABehaviour detectArmorBehaviour(Map<?, ?> data, List<EquipementEntry> equipements) {
        Object equipement = data.get("equipement");

        if (equipement == null) return new EmptyBehaviour();

        Map<?, ?> equipementMap = (Map<?, ?>) equipement;

        return ArmorsBehaviour.from(equipements, equipementMap);
    }
}
