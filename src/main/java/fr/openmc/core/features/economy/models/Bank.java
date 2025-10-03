package fr.openmc.core.features.economy.models;

import java.util.UUID;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import lombok.Getter;

@Getter
@DatabaseTable(tableName = "banks")
public class Bank {

    @DatabaseField(id = true, columnName = "player")
    private UUID playerUUID;

    @DatabaseField(canBeNull = false, defaultValue = "0")
    private double balance;

    Bank() {
        // necessary for OrmLite
    }

    public Bank(UUID playerUUID) {
        this.playerUUID = playerUUID;
        this.balance = 0;
    }

    public void deposit(double amount) {
        balance += amount;
        assert balance >= 0;
    }

    public void withdraw(double amount) {
        balance -= amount;
        assert balance >= 0;
    }
}
