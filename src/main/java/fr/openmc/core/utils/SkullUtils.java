package fr.openmc.core.utils;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.ResolvableProfile;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SkullUtils {

    private static final Map<String, ItemStack> CACHE = new HashMap<>();

    /**
     * Creates a player skull item for the specified player UUID.
     *
     * @param playerUUID the UUID of the player whose skull is to be created
     * @return an {@link ItemStack} representing the player's skull
     */
    public static ItemStack getPlayerSkull(UUID playerUUID) {
        ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
        PlayerProfile profile = Bukkit.createProfile(playerUUID);
        skull.setData(DataComponentTypes.PROFILE, ResolvableProfile.resolvableProfile(profile));
        return skull;
    }

    /**
     * Récupère une tête personnalisée (mise en cache)
     *
     * @param base64 texture (minecraft-heads.com)
     * @param name   nom affiché (optionnel)
     * @return ItemStack de tête (copie sûre)
     */
    public static ItemStack getCustomHead(String base64, String name) {
        ItemStack cached = CACHE.get(base64);
        if (cached != null) {
            ItemStack clone = cached.clone();
            if (name != null && !name.isEmpty()) {
                SkullMeta meta = (SkullMeta) clone.getItemMeta();
                if (meta != null) meta.displayName(Component.text(name));
                clone.setItemMeta(meta);
            }
            return clone;
        }

        ItemStack head = new ItemStack(Material.PLAYER_HEAD);

        PlayerProfile profile = Bukkit.createProfile(UUID.randomUUID());
        profile.setProperty(new ProfileProperty("textures", base64));

        head.setData(DataComponentTypes.PROFILE, ResolvableProfile.resolvableProfile(profile));

        ItemMeta meta = head.getItemMeta();
        if (meta == null) return null;
        if (name != null && !name.isEmpty()) meta.displayName(Component.text(name));

        CACHE.put(base64, head.clone());
        return head;
    }
}
