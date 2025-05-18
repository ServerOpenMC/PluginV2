package fr.openmc.core.features.corporation.models;

import java.util.UUID;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import lombok.Getter;

@DatabaseTable(tableName = "company_merchants")
public class CompanyMerchant {
    @DatabaseField(id = true)
    @Getter
    private UUID player;
    @DatabaseField
    private UUID company;
    @DatabaseField(canBeNull = false, defaultValue = "0", columnName = "money_won")
    @Getter
    private double moneyWon;

    CompanyMerchant() {
        // required for ORMLite
    }

    public CompanyMerchant(UUID player, UUID company, double moneyWon) {
        this.player = player;
        this.company = company;
        this.moneyWon = moneyWon;
    }
}
