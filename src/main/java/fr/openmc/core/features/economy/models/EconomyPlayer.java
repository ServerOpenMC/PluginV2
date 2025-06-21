package fr.openmc.core.features.economy.models;

import java.util.UUID;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import lombok.Getter;

@Getter
@DatabaseTable(tableName = "balances")
public class EconomyPlayer {
    @DatabaseField(id = true)
    private UUID player;
    @DatabaseField(canBeNull = false, defaultValue = "0")
    private double balance;

    EconomyPlayer() {
        // necessary for OrmLite
    }

    public EconomyPlayer(UUID player) {
        this.player = player;
        this.balance = 0;
    }

    public void deposit(double amount) {
        balance += amount;
    }

    public boolean withdraw(double amount) {
        if (amount <= balance) {
            balance -= amount;
            return true;
        }
        return false;
    }
}
