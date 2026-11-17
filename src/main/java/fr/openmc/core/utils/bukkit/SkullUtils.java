package fr.openmc.core.utils.bukkit;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import fr.openmc.api.menulib.Menu;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.utils.cache.CachePlayerProfile;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.ResolvableProfile;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class SkullUtils {

    private static final Map<String, ItemStack> CUSTOM_CACHE = new ConcurrentHashMap<>();

    private static NamespacedKey uuidKey() {
        return new NamespacedKey(OMCPlugin.getInstance(), "pending_head");
    }

    /**
     * Creates a player skull item for the specified player UUID.
     *
     * @param playerUUID the UUID of the player whose skull is to be created
     * @return an {@link ItemStack} representing the player's skull
     */
    public static ItemStack getPlayerSkull(UUID playerUUID) {
        PlayerProfile cached = CachePlayerProfile.getCached(playerUUID);
        if (cached != null) return buildHead(cached);

        CachePlayerProfile.request(playerUUID, null);
        return new ItemStack(Material.PLAYER_HEAD);
    }

    public static ItemStack getPlayerSkull(UUID uuid, Menu menu) {
        PlayerProfile cached = CachePlayerProfile.getCached(uuid);
        if (cached != null) return buildHead(cached);

        ItemStack placeholder = new ItemStack(Material.PLAYER_HEAD);
        placeholder.editMeta(meta -> meta.getPersistentDataContainer()
                .set(uuidKey(), PersistentDataType.STRING, uuid.toString()));

        CachePlayerProfile.request(uuid, profile -> replaceInMenu(menu, uuid, profile));

        return placeholder;
    }

    private static void replaceInMenu(Menu menu, UUID uuid, PlayerProfile profile) {
        Inventory menuInventory = menu.getInventory();

        Player owner = menu.getOwner();
        Inventory top = owner.getOpenInventory().getTopInventory();

        if (!Objects.equals(top.getHolder(), menuInventory.getHolder())) return;

        String id = uuid.toString();
        for (int slot = 0; slot < top.getSize(); slot++) {
            ItemStack current = top.getItem(slot);
            if (current == null
                    || current.getType() != Material.PLAYER_HEAD) continue;

            String tag = current.getPersistentDataContainer().get(uuidKey(), PersistentDataType.STRING);
            if (!id.equals(tag)) continue;

            ItemStack updated = current.clone();
            updated.setData(DataComponentTypes.PROFILE, ResolvableProfile.resolvableProfile(profile));
            top.setItem(slot, updated);
        }
    }

    /**
     * Récupère une tête personnalisée (mise en cache)
     *
     * @param base64 texture (minecraft-heads.com)
     * @param name   nom affiché (optionnel)
     * @return ItemStack de tête (copie sûre)
     */
    public static ItemStack getCustomHead(String base64, Component name) {
        ItemStack head = CUSTOM_CACHE.computeIfAbsent(base64, key -> {
            PlayerProfile profile = Bukkit.createProfile(UUID.randomUUID());
            profile.setProperty(new ProfileProperty("textures", key));
            return buildHead(profile);
        }).clone();

        if (name != null) head.setData(DataComponentTypes.CUSTOM_NAME, name);
        return head;
    }

    private static ItemStack buildHead(PlayerProfile profile) {
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        head.setData(DataComponentTypes.PROFILE, ResolvableProfile.resolvableProfile(profile));
        return head;
    }
}