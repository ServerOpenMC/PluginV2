package fr.openmc.core.features.corporation.models;

import java.util.UUID;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "merchant_data")
public class MerchantData {
    @DatabaseField(id = true)
    private UUID id;
    @DatabaseField
    private byte[] content;

    MerchantData() {
        // required for ORMLite
    }
}
