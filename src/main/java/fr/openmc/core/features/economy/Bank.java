package fr.openmc.core.features.economy;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import lombok.Getter;

@Getter
@DatabaseTable(tableName = "bank")
public class Bank {

    @DatabaseField(id = true)
    private String uuid;

    @DatabaseField(canBeNull = false, defaultValue = "0")
    private double balance;

    Bank() {
        // necessary for OrmLite
    }

    Bank(String uuid) {
        this.uuid = uuid;
        this.balance = 0;
    }

    public void deposit(double amount) {
        balance += amount;
    }

    public void withdraw(double amount) {
        balance -= amount;
        assert balance >= 0;
    }
}
