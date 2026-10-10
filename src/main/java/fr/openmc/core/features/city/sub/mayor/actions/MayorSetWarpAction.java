package fr.openmc.core.features.city.sub.mayor.actions;

import fr.openmc.api.cooldown.DynamicCooldownManager;
import fr.openmc.api.omcplayer.OMCPlayer;
import fr.openmc.core.OMCRegistry;
import fr.openmc.core.features.city.models.CityPermission;
import fr.openmc.core.features.city.models.city.City;
import fr.openmc.core.features.city.sub.mayor.models.CityLaw;
import fr.openmc.core.features.city.sub.mayor.models.Mayor;
import fr.openmc.core.utils.text.messages.Prefix;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Chunk;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

import static fr.openmc.core.features.city.sub.mayor.menu.MayorLawMenu.COOLDOWN_TIME_WARP;

public class MayorSetWarpAction {
    private final static DynamicCooldownManager dynamicCooldownManager = OMCRegistry.FEATURES.DYNAMIC_COOLDOWN.get();

    public static void setWarp(OMCPlayer player) {
        City city = player.city().getCity();

        if (city == null) return;

        Mayor mayor = city.getMayor();

        if ((mayor == null
                || !player.getUniqueId().equals(city.getMayor().getMayorUUID()))
                && !city.getPlayerWithPermission(CityPermission.OWNER).equals(player.getUniqueId())) {
            player.message().sendError(TranslationManager.translation("feature.city.mayor.warp.error.not_mayor"), Prefix.MAYOR, false);
            return;
        }

        if (!dynamicCooldownManager.isReady(city.getUniqueId(), "mayor:law-move-warp")) return;

        CityLaw law = city.getLaw();

        player.inputs().sendLocationInput(
                getWarpWand(),
                "mayor:wait-set-warp",
                300,
                "feature.city.mayor.warp.interaction.remaining",
                TranslationManager.translation("feature.city.mayor.warp.interaction.timeout"),
                locationClick -> {
                    if (locationClick == null) return true;
                    Chunk chunk = locationClick.getChunk();

                    if (!city.hasChunk(chunk.getX(), chunk.getZ())) {
                        player.message().sendError(TranslationManager.translation("feature.city.mayor.warp.error.outside_city"), Prefix.CITY, false);
                        return false;
                    }

                    dynamicCooldownManager.use(city.getUniqueId(), "mayor:law-move-warp", COOLDOWN_TIME_WARP);
                    law.setWarp(locationClick);
                    player.message().sendSuccess(TranslationManager.translation(
                            "feature.city.mayor.warp.success",
                            Component.text(locationClick.x()).color(NamedTextColor.GOLD),
                            Component.text(locationClick.y()).color(NamedTextColor.GOLD),
                            Component.text(locationClick.z()).color(NamedTextColor.GOLD)
                    ), Prefix.CITY, false);
                    return true;
                },
                null
        );
    }

    public static ItemStack getWarpWand() {
        List<Component> loreItemInterraction = List.of(
                TranslationManager.translation("feature.city.mayor.warp.wand.lore")
        );
        ItemStack item = OMCRegistry.CUSTOM_ITEMS.WARP_STICK.getBest();
        ItemMeta itemMeta = item.getItemMeta();

        itemMeta.displayName(TranslationManager.translation("feature.city.mayor.warp.wand.name"));
        itemMeta.lore(loreItemInterraction);
        item.setItemMeta(itemMeta);
        return item;
    }
}
