package fr.openmc.core.features.homes;

import fr.openmc.core.OMCRegistry;
import fr.openmc.core.features.homes.command.*;
import fr.openmc.core.features.homes.models.Home;
import fr.openmc.core.features.homes.models.HomeLimit;
import fr.openmc.core.lifecycle.interfaces.HasCommands;
import fr.openmc.core.lifecycle.interfaces.HasRegistries;
import fr.openmc.core.lifecycle.interfaces.db.DBTable;
import fr.openmc.core.lifecycle.interfaces.db.HasDatabase;
import fr.openmc.core.lifecycle.registries.LifecycleRegistry;
import fr.openmc.core.lifecycle.registries.SubRegistry;
import fr.openmc.core.registry.features.Feature;
import fr.openmc.core.registry.features.annotations.Credit;
import lombok.Getter;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.function.Supplier;

@Credit(developers = {"Axeno"}, graphist = {"Gexary"})
@Getter
public class HomesManager extends Feature implements HasDatabase, HasCommands, HasRegistries {
    @Getter
    private final List<Home> homes = new ArrayList<>();
    private final List<HomeLimit> homeLimits = new ArrayList<>();

    @Override
    public void onEnable() {
        loadHomeLimit();
        loadHomes();
    }

    @Override
    public Set<Object> getCommands() {
        return Set.of(
                new SetHomeCommand(),
                new RenameHomeCommand(),
                new DelHomeCommand(),
                new RelocateHomeCommand(),
                new TpHomeCommand(),
                new HomeWorldCommand(),
                new UpgradeHomeCommand()
        );
    }

    @Override
    public void onDisable() {
        saveHomes();
        saveHomeLimit();
    }

    @Override
    public List<Supplier<LifecycleRegistry>> getRegistries() {
        return List.of(
                () -> SubRegistry.boot(new HomeFeaturesRegistry(),
                        r -> OMCRegistry.HOME_FEATURES = r)
        );
    }

    // DB methods

    private final DBTable<Home, UUID> homesTable = DBTable.of(Home.class);
    private final DBTable<HomeLimit, UUID> limitsTable = DBTable.of(HomeLimit.class);

    @Override
    public List<DBTable<?, ?>> getTables() {
        return List.of(homesTable, limitsTable);
    }

    private void loadHomeLimit() {
        homeLimits.clear();
        homeLimits.addAll(limitsTable.loadList());

        for (HomeLimit homeLimit : homeLimits) {
            if (homeLimit.getLimit() == 0) homeLimit.setLimit(HomeLimits.LIMIT_0.getLimit());
        }
    }

    private void saveHomeLimit() {
        limitsTable.clearTable();
        limitsTable.saveAll(homeLimits);
        homeLimits.clear();
    }

    private void loadHomes() {
        homes.clear();
        homes.addAll(homesTable.loadList());
    }

    private void saveHomes() {
        homesTable.clearTable();
        homesTable.saveAll(homes);
        homes.clear();
    }

    public boolean addHome(Home home) {
        return homes.add(home);
    }

    public boolean removeHome(Home home) {
        return homes.remove(home);
    }

    public void renameHome(Home home, String newName) {
        home.setName(newName);
    }

    public void relocateHome(Home home, Location location) {
        home.setLocation(location);
    }

    public void addHomeLimit(HomeLimit homeLimit) {
        homeLimits.add(homeLimit);
    }
}
