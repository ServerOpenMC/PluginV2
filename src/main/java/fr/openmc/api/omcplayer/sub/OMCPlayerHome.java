package fr.openmc.api.omcplayer.sub;

import fr.openmc.core.OMCRegistry;
import fr.openmc.core.features.homes.HomeLimits;
import fr.openmc.core.features.homes.HomesManager;
import fr.openmc.core.features.homes.models.Home;
import fr.openmc.core.features.homes.models.HomeLimit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;

import java.util.List;

public class OMCPlayerHome extends OMCPlayerFeat {
    private final HomesManager homesManager = OMCRegistry.FEATURES.HOMES.get();

    public OMCPlayerHome(OfflinePlayer player) {
        super(player);
    }

    public List<Home> getHomes() {
        return homesManager.getHomes()
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
        return homesManager.addHome(home);
    }

    public boolean removeHome(Home home) {
        return homesManager.removeHome(home);
    }

    public void renameHome(Home home, String newName) {
        homesManager.renameHome(home, newName);
    }

    public void relocateHome(Home home, Location newLoc) {
        homesManager.relocateHome(home, newLoc);
    }

    public HomeLimits getHomeLimit() {
        HomeLimit homeLimit = homesManager.getHomeLimits().stream()
                .filter(hl -> hl.getPlayerUUID().equals(getUniqueId()))
                .findFirst()
                .orElse(null);

        if (homeLimit == null) {
            homeLimit = new HomeLimit(getUniqueId(), HomeLimits.LIMIT_0);
            homesManager.addHomeLimit(homeLimit);
        }

        return homeLimit.getHomeLimit();
    }

    public void updateHomeLimit() {
        HomeLimit homeLimit = homesManager.getHomeLimits().stream()
                .filter(hl -> hl.getPlayerUUID().equals(getUniqueId()))
                .findFirst()
                .orElse(null);
        if (homeLimit == null) {
            homesManager.addHomeLimit(new HomeLimit(getUniqueId(), HomeLimits.LIMIT_0));
        } else {
            int currentLimitIndex = homeLimit.getHomeLimit().ordinal();
            HomeLimits newLimit = HomeLimits.values()[currentLimitIndex + 1];
            homeLimit.setLimit(newLimit.getLimit());
        }
    }
}
