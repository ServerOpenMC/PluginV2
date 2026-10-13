package fr.openmc.core.features.city.models.city.namespaces;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public interface CityChest {

    @Nullable UUID getChestWatcher();

    void setChestWatcher(UUID chestWatcher);

    ItemStack[] getChestContent(int page);

    void saveChestContent(int page, ItemStack[] content);

    @NotNull Integer getChestPages();
}
