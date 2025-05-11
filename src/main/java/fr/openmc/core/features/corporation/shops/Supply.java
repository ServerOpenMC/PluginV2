package fr.openmc.core.features.corporation.shops;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
public class Supply {

    private final UUID supplier;
    private final UUID itemId;
    private final UUID supplierUUID;
    @Setter
    private int amount;

    public Supply(UUID supplier, UUID itemId, int amount) {
        this.supplier = supplier;
        this.itemId = itemId;
        this.amount = amount;
        this.supplierUUID = UUID.randomUUID();
    }

    public Supply(UUID supplier, UUID itemId, int amount, UUID supplierUUID) {
        this.supplier = supplier;
        this.itemId = itemId;
        this.amount = amount;
        this.supplierUUID = supplierUUID;
    }
}
