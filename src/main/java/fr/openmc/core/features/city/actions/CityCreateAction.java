package fr.openmc.core.features.city.actions;

import fr.openmc.api.cooldown.DynamicCooldownManager;
import fr.openmc.api.omcplayer.OMCPlayer;
import fr.openmc.core.OMCRegistry;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.conditions.CityCreateConditions;
import fr.openmc.core.features.city.models.CityType;
import fr.openmc.core.features.city.models.city.City;
import fr.openmc.core.features.city.sub.mascots.MascotsManager;
import fr.openmc.core.features.city.sub.mayor.managers.MayorManager;
import fr.openmc.core.features.city.sub.view.CityClaimViewManager;
import fr.openmc.core.utils.bukkit.ItemUtils;
import fr.openmc.core.utils.text.messages.MessageType;
import fr.openmc.core.utils.text.messages.Prefix;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CityCreateAction {
    private static final CityManager CITY_MANAGER = OMCRegistry.FEATURES.CITY.get();
    private static final MayorManager MAYOR_MANAGER = OMCRegistry.CITY_FEATURES.MAYOR;
    private static final MascotsManager MASCOTS_MANAGER = OMCRegistry.CITY_FEATURES.MASCOTS;
    private static final CityClaimViewManager CLAIM_VIEW_MANAGER = OMCRegistry.CITY_FEATURES.CLAIM_VIEW;
    private static final DynamicCooldownManager DYNAMIC_COOLDOWN_MANAGER = OMCRegistry.FEATURES.DYNAMIC_COOLDOWN.get();

    public static final int FREE_CLAIMS = 9;
    public static final long IMMUNITY_COOLDOWN = 7 * 24 * 60 * 60 * 1000L;

    private static final Map<UUID, String> pendingCities = new HashMap<>();

    public static void beginCreateCity(OMCPlayer player, String cityName) {
        if (cityName == null) return;
        if (!CityCreateConditions.canCityCreate(player, cityName)) return;

        pendingCities.put(player.getUniqueId(), cityName);

        if (!ItemUtils.takeAywenite(player, CityCreateConditions.AYWENITE_CREATE)) return;
        if (!player.economy().withdrawBalance(CityCreateConditions.MONEY_CREATE)) return;

        player.inputs().sendLocationInput(
                getMascotStick(),
                "mascot:stick",
                300,
                "feature.city.mascot.mascot_wand.claim_mascot_wand",
                TranslationManager.translation("feature.city.mascot.mascot_wand.create_cancelled"),
                location -> {
                    if (!isValidLocation(player, location)) return false;

                    return finalizeCreation(player, location);
                },
                () -> {
                    pendingCities.remove(player.getUniqueId());
                    ItemUtils.giveItem(player, OMCRegistry.CUSTOM_ITEMS.AYWENITE.getBest(), CityCreateConditions.AYWENITE_CREATE);
                    player.economy().addBalance(CityCreateConditions.MONEY_CREATE, "Remboursement création ville annulée");
                }
        );
    }

    private static ItemStack getMascotStick() {
        ItemStack stick = OMCRegistry.CUSTOM_ITEMS.MASCOT_STICK.getBest();

        ItemMeta meta = stick.getItemMeta();
        if (meta != null) {
            meta.displayName(TranslationManager.translation("feature.city.mascot.mascot").decorate(TextDecoration.BOLD));
            meta.lore(TranslationManager.translationLore("feature.city.mascot.mascot_wand.lore"));
            stick.setItemMeta(meta);
        }
        return stick;
    }

    private static boolean isValidLocation(OMCPlayer player, Location location) {
        if (location == null || location.getWorld() == null) return false;
        if (!"world".equals(location.getWorld().getName())) {
	       player.message().send(TranslationManager.translation("feature.city.mascot.mascot_wand.can_only_place_mascot"),
                   Prefix.CITY, MessageType.ERROR, false);
           return false;
        }
        if (location.clone().add(0, 1, 0).getBlock().getType().isSolid()) {
	       player.message().send(TranslationManager.translation("feature.city.mascot.mascot_wand.hasnot_block_above"),
                   Prefix.CITY, MessageType.ERROR, false);
            return false;
        }
        return true;
    }

    public static boolean finalizeCreation(OMCPlayer player, Location mascotLocation) {
        Chunk chunk = mascotLocation.getChunk();

        if (OMCRegistry.HOOKS.WORLD_GUARD.doesChunkContainWGRegion(chunk)) {
            player.message().send(TranslationManager.translation("feature.city.claim.is_in_region"), Prefix.CITY, MessageType.ERROR, false);
            return false;
        }

        if (CITY_MANAGER.isChunkClaimedInRadius(chunk, 1)) {
            player.message().send(TranslationManager.translation("feature.city.claim.already_claim_in_adjacent"),
                    Prefix.CITY, MessageType.ERROR, false);
            return false;
        }

        UUID cityUUID = UUID.randomUUID();

        String pendingCityName = pendingCities.remove(player.getUniqueId());
        if (pendingCityName == null) return false;

        City city = new City(cityUUID, pendingCityName, player, CityType.PEACE, chunk);

        // Lois
        MAYOR_MANAGER.createCityLaws(city, false, null);

        // Mascotte
        player.getWorld().getBlockAt(mascotLocation).setType(Material.AIR);
        MASCOTS_MANAGER.createMascot(city, cityUUID, pendingCityName, player.getWorld(), mascotLocation);

        // Feedback
	    player.message().send(TranslationManager.translation("feature.city.create.success", Component.text(pendingCityName)).color(NamedTextColor.GREEN), Prefix.CITY, MessageType.SUCCESS, true);
        player.message().send(
                TranslationManager.translation("feature.city.create.free_claim",
                        Component.text(FREE_CLAIMS).color(NamedTextColor.GOLD)),
                Prefix.CITY, MessageType.INFO, false);

        player.cooldown().use("city:big", 60000);
        DYNAMIC_COOLDOWN_MANAGER.use(cityUUID, "city:immunity", IMMUNITY_COOLDOWN);

        CLAIM_VIEW_MANAGER.updateAllViews();
        return true;
    }
}