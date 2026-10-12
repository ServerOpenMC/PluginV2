package fr.openmc.core.features.dream.mecanism.singularity;

import fr.openmc.core.lifecycle.interfaces.db.DBTable;
import fr.openmc.core.lifecycle.interfaces.db.HasDatabase;
import fr.openmc.core.registry.features.Feature;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class SingularityManager extends Feature implements HasDatabase {
    public final HashMap<UUID, SingularityContents> singularityContents = new HashMap<>();
    private final DBTable<SingularityContents, String> singularityContentsTable = DBTable.of(SingularityContents.class);

    @Override
    public void onEnable() {
        this.loadAllSingularityContentsData();
    }

    @Override
    public List<DBTable<?, ?>> getTables() {
        return List.of(singularityContentsTable);
    }

    @Override
    public void onDisable() {
        singularityContentsTable.saveMap(singularityContents);
    }

    private void loadAllSingularityContentsData() {
        singularityContents.clear();
        singularityContents.putAll(singularityContentsTable.loadMap(SingularityContents::getPlayerUUID));
    }

    public void addSingularityContents(Player player, ItemStack[] items) {
        if (singularityContents.containsKey(player.getUniqueId())) return;

        singularityContents.put(player.getUniqueId(), new SingularityContents(player.getUniqueId(), items));
    }

    public SingularityContents getSingularityContents(Player player) {
        if (!singularityContents.containsKey(player.getUniqueId())) return null;

        return singularityContents.get(player.getUniqueId());
    }
}
