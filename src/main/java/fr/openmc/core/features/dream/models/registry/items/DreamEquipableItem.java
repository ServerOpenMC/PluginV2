package fr.openmc.core.features.dream.models.registry.items;

public abstract class DreamEquipableItem extends DreamItem {

    /**
     * Creates a new DreamEquipableItem with the specified name.
     *
     * @param name The namespaced ID of the item, e.g., "omc_dream:dream_helmet".
     */
    protected DreamEquipableItem(String name) {
        super(name);
    }

    public abstract long getAdditionalMaxTime();

    public abstract Integer getColdResistance();
}
