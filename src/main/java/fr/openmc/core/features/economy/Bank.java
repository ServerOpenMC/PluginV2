package fr.openmc.core.features.economy;

import java.util.UUID;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.sk89q.worldedit.entity.Player;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@DatabaseTable(tableName = "bank")
class Bank {

    @DatabaseField(id = true)
    private String uuid;

    @DatabaseField(canBeNull = false, defaultValue = "0")
    private double balance;

    Bank() {
        // necessary for OrmLite
    }

    Bank(String uuid, double balance) {
        this.uuid = uuid;
        this.balance = balance;
    }
}
