package fr.openmc.api.entity.player.sub;

import fr.openmc.core.features.homes.HomeLimits;
import fr.openmc.core.features.homes.HomesManager;
import fr.openmc.core.features.homes.models.Home;
import fr.openmc.core.features.homes.models.HomeLimit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;

import java.util.List;

public class OMCPlayerHome extends OMCPlayerFeat {
    public OMCPlayerHome(OfflinePlayer player) {
        super(player);
    }

    public List<Home> getHomes() {
        return HomesManager.homes
                .stream()
                .filter(home -> home.getOwner().equals(getUniqueId()))
                .toList();
    }

    public List<String> getHomesNames() {
        return getHomes()
                .stream()
                .map(Home::getName)
                .toList();
    }

    public boolean setHome(Home home) {
        return HomesManager.homes.add(home);
    }

    public boolean removeHome(Home home) {
        return HomesManager.homes.remove(home);
    }

    public void renameHome(Home home, String newName) {
        home.setName(newName);
    }

    public void relocateHome(Home home, Location newLoc) {
        HomesManager.homes.remove(home);
    }

    public HomeLimits getHomeLimit() {
        HomeLimit homeLimit = HomesManager.homeLimits.stream()
                .filter(hl -> hl.getPlayerUUID().equals(getUniqueId()))
                .findFirst()
                .orElse(null);

        if (homeLimit == null) {
            homeLimit = new HomeLimit(getUniqueId(), HomeLimits.LIMIT_0);
            HomesManager.homeLimits.add(homeLimit);
        }

        return homeLimit.getHomeLimit();
    }

    public void updateHomeLimit() {
        HomeLimit homeLimit = HomesManager.homeLimits.stream()
                .filter(hl -> hl.getPlayerUUID().equals(getUniqueId()))
                .findFirst()
                .orElse(null);
        if (homeLimit == null) {
            HomesManager.homeLimits.add(new HomeLimit(getUniqueId(), HomeLimits.LIMIT_0));
        } else {
            int currentLimitIndex = homeLimit.getHomeLimit().ordinal();
            HomeLimits newLimit = HomeLimits.values()[currentLimitIndex + 1];
            homeLimit.setLimit(newLimit.getLimit());
        }
    }
}
