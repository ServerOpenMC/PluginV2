package fr.openmc.api.entity.player.sub;

import fr.openmc.core.OMCRegistry;
import fr.openmc.core.features.economy.EconomyManager;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class OMCPlayerEconomy extends OMCPlayerFeat {
    private final EconomyManager economyManager = OMCRegistry.FEATURES.ECONOMY.get();

    public OMCPlayerEconomy(Player player) {
        super(player);
    }

    /**
     * Recupere la balance du joueur
     *
     * @return la balance du joueur
     */
    public double getBalance() {
        return economyManager.getBalance(getUniqueId());
    }

    /**
     * Recupere la balance du joueur formatee avec le symbole de la monnaie
     *
     * @return la balance du joueur formatee
     */
    public String getFormattedBalance() {
        return economyManager.getFormattedBalance(getUniqueId());
    }

    public void addBalance(double amount) {
        economyManager.addBalance(getUniqueId(), amount);
    }

    public void addBalance(double amount, @Nullable String reason) {
        economyManager.addBalance(getUniqueId(), amount, reason);
    }

    public boolean withdrawBalance(double amount) {
        return economyManager.withdrawBalance(getUniqueId(), amount);
    }

    public boolean withdrawBalance(double amount, @Nullable String reason) {
        return economyManager.withdrawBalance(getUniqueId(), amount, reason);
    }

    public void setBalance(double amount) {
        economyManager.setBalance(getUniqueId(), amount);
    }

    public boolean pay(UUID targetUUID, double amount, @Nullable String reason) {
        return economyManager.transferBalance(getUniqueId(), targetUUID, amount, reason);
    }
}
