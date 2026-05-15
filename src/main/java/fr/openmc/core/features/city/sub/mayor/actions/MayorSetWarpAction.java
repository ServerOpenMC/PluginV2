package fr.openmc.core.features.city.sub.mayor.actions;

import fr.openmc.api.cooldown.DynamicCooldownManager;
import fr.openmc.api.input.location.ItemInteraction;
import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.CityPermission;
import fr.openmc.core.features.city.sub.mayor.models.CityLaw;
import fr.openmc.core.features.city.sub.mayor.models.Mayor;
import fr.openmc.core.registry.items.CustomItemRegistry;
import fr.openmc.core.utils.text.messages.MessageType;
import fr.openmc.core.utils.text.messages.MessagesManager;
import fr.openmc.core.utils.text.messages.Prefix;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

import static fr.openmc.core.features.city.sub.mayor.menu.MayorLawMenu.COOLDOWN_TIME_WARP;

public class MayorSetWarpAction {
    public static void setWarp(Player player) {
        City city = CityManager.getPlayerCity(player.getUniqueId());

        if (city == null) return;

        Mayor mayor = city.getMayor();

        if ((mayor == null || !player.getUniqueId().equals(city.getMayor().getMayorUUID())) && !city.getPlayerWithPermission(CityPermission.OWNER).equals(player.getUniqueId())) {
            MessagesManager.sendMessage(player, TranslationManager.translation("feature.city.mayor.warp.error.not_mayor"), Prefix.MAYOR, MessageType.ERROR, false);
            return;
        }

        if (!DynamicCooldownManager.isReady(city.getUniqueId(), "mayor:law-move-warp")) {
            return;
        }
        CityLaw law = city.getLaw();

        ItemInteraction.runLocationInteraction(
                player,
                getWarpWand(),
                "mayor:wait-set-warp",
                300,
                TranslationManager.translation("feature.city.mayor.warp.interaction.remaining", Component.text("300s").color(NamedTextColor.GRAY)),
                TranslationManager.translation("feature.city.mayor.warp.interaction.timeout"),
                locationClick -> {
                    if (locationClick == null) return true;
                    Chunk chunk = locationClick.getChunk();

                    if (!city.hasChunk(chunk.getX(), chunk.getZ())) {
                        MessagesManager.sendMessage(player, TranslationManager.translation("feature.city.mayor.warp.error.outside_city"), Prefix.CITY, MessageType.ERROR, false);
                        return false;
                    }

                    DynamicCooldownManager.use(city.getUniqueId(), "mayor:law-move-warp", COOLDOWN_TIME_WARP);
                    law.setWarp(locationClick);
                    MessagesManager.sendMessage(player, TranslationManager.translation(
                            "feature.city.mayor.warp.success",
                            Component.text(locationClick.x()).color(NamedTextColor.GOLD),
                            Component.text(locationClick.y()).color(NamedTextColor.GOLD),
                            Component.text(locationClick.z()).color(NamedTextColor.GOLD)
                    ), Prefix.CITY, MessageType.SUCCESS, false);
                    return true;
                },
                null
        );
    }

    public static ItemStack getWarpWand() {
        List<Component> loreItemInterraction = List.of(
                TranslationManager.translation("feature.city.mayor.warp.wand.lore")
        );
        ItemStack item = CustomItemRegistry.getByName("omc_items:warp_stick").getBest();
        ItemMeta itemMeta = item.getItemMeta();

        itemMeta.displayName(TranslationManager.translation("feature.city.mayor.warp.wand.name"));
        itemMeta.lore(loreItemInterraction);
        item.setItemMeta(itemMeta);
        return item;
    }
}
