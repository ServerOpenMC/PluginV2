package fr.openmc.api.entity.player.sub;

import fr.openmc.core.features.economy.EconomyManager;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class OMCPlayerEconomy extends OMCPlayerFeat {
    public OMCPlayerEconomy(Player player) {
        super(player);
    }

    /**
     * Recupere la balance du joueur
     *
     * @return la balance du joueur
     */
    public double getBalance() {
        return EconomyManager.getBalance(getUniqueId());
    }

    /**
     * Recupere la balance du joueur formatee avec le symbole de la monnaie
     *
     * @return la balance du joueur formatee
     */
    public String getFormattedBalance() {
        return EconomyManager.getFormattedBalance(getUniqueId());
    }

    public void addBalance(double amount) {
        EconomyManager.addBalance(getUniqueId(), amount);
    }

    public void addBalance(double amount, @Nullable String reason) {
        EconomyManager.addBalance(getUniqueId(), amount, reason);
    }

    public boolean withdrawBalance(double amount) {
        return EconomyManager.withdrawBalance(getUniqueId(), amount);
    }

    public boolean withdrawBalance(double amount, @Nullable String reason) {
        return EconomyManager.withdrawBalance(getUniqueId(), amount, reason);
    }

    public void setBalance(double amount) {
        EconomyManager.setBalance(getUniqueId(), amount);
    }

    public boolean pay(UUID targetUUID, double amount, @Nullable String reason) {
        return EconomyManager.transferBalance(getUniqueId(), targetUUID, amount, reason);
    }
}
