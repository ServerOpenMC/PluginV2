package fr.openmc.core.features.city.actions;

import fr.openmc.api.cooldown.DynamicCooldownManager;
import fr.openmc.api.input.location.ItemInteraction;
import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.CityType;
import fr.openmc.core.features.city.conditions.CityCreateConditions;
import fr.openmc.core.features.city.sub.mascots.MascotsManager;
import fr.openmc.core.features.city.sub.mayor.ElectionType;
import fr.openmc.core.features.city.sub.mayor.managers.MayorManager;
import fr.openmc.core.features.city.sub.mayor.managers.PerkManager;
import fr.openmc.core.features.city.sub.mayor.perks.Perks;
import fr.openmc.core.features.economy.EconomyManager;
import fr.openmc.core.items.CustomItemRegistry;
import fr.openmc.core.utils.DateUtils;
import fr.openmc.core.utils.ItemUtils;
import fr.openmc.api.hooks.WorldGuardHook;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static fr.openmc.core.features.city.sub.mayor.managers.MayorManager.PHASE_1_DAY;

public class CityCreateAction {

    public static final long IMMUNITY_COOLDOWN = 7 * 24 * 60 * 60 * 1000L;

    private static final Map<UUID, String> pendingCities = new HashMap<>();

    public static void beginCreateCity(Player player, String cityName) {
        if (!CityCreateConditions.canCityCreate(player, cityName)) return;

        pendingCities.put(player.getUniqueId(), cityName);

        if (!ItemUtils.takeAywenite(player, CityCreateConditions.AYWENITE_CREATE)) return;
        if (!EconomyManager.withdrawBalance(player.getUniqueId(), CityCreateConditions.MONEY_CREATE)) return;

        ItemInteraction.runLocationInteraction(
                player,
                getMascotStick(),
                "mascot:stick",
                300,
                "Vous avez reçu un baton pour poser votre mascotte",
                "§cCréation annulée",
                location -> {
                    if (!isValidLocation(player, location)) return false;
                    return finalizeCreation(player, location);
                },
                () -> {
                    pendingCities.remove(player.getUniqueId());
                    ItemUtils.giveAywenite(player, CityCreateConditions.AYWENITE_CREATE);
                    EconomyManager.addBalance(player.getUniqueId(), CityCreateConditions.MONEY_CREATE);
                }
        );
    }

    private static ItemStack getMascotStick() {
        ItemStack stick = CustomItemRegistry.getByName("omc_items:mascot_stick").getBest();
        ItemMeta meta = stick.getItemMeta();
        if (meta != null) {
            meta.displayName(Component.text("§lMascotte"));
            meta.lore(List.of(
                    Component.text("§cVotre mascotte sera posée à l'emplacement du coffre."),
                    Component.text("§cCe baton n'est pas retirable."),
                    Component.text("§cDéconnexion = annulation.")
            ));
            stick.setItemMeta(meta);
        }
        return stick;
    }

    private static boolean isValidLocation(Player player, Location location) {
        if (location == null || location.getWorld() == null) return false;
        if (!"world".equals(location.getWorld().getName())) {
            MessagesManager.sendMessage(player, Component.text("§cCoffre uniquement dans le monde principal"), Prefix.CITY, MessageType.ERROR, false);
            return false;
        }
        if (location.clone().add(0, 1, 0).getBlock().getType().isSolid()) {
            MessagesManager.sendMessage(player, Component.text("§cAucun bloc ne doit être au-dessus du coffre"), Prefix.CITY, MessageType.ERROR, false);
            return false;
        }
        return true;
    }

    public static boolean finalizeCreation(Player player, Location mascotLocation) {
        UUID playerUUID = player.getUniqueId();
        String pendingCityName = pendingCities.remove(playerUUID);
        if (pendingCityName == null) return false;

        String cityUUID = UUID.randomUUID().toString().substring(0, 8);
        Chunk chunk = mascotLocation.getChunk();

        if (WorldGuardHook.doesChunkContainWGRegion(chunk)) {
            MessagesManager.sendMessage(player, Component.text("Ce chunk est dans une région protégée"), Prefix.CITY, MessageType.ERROR, false);
            return false;
        }

        if (CityManager.isChunkClaimedInRadius(chunk, 1)) {
            MessagesManager.sendMessage(player, Component.text("Une des parcelles autour de ce chunk est claim!"), Prefix.CITY, MessageType.ERROR, false);
            return false;
        }

        City city = new City(cityUUID, pendingCityName, player, CityType.PEACE, chunk);

        // Maire
        if (MayorManager.phaseMayor == 1) { // si création pendant le choix des maires
            MayorManager.createMayor(null, null, city, null, null, null, null, ElectionType.OWNER_CHOOSE);
        } else { // si création pendant les réformes actives
            NamedTextColor color = MayorManager.getRandomMayorColor();
            List<Perks> perks = PerkManager.getRandomPerksAll();
            MayorManager.createMayor(player.getName(), player.getUniqueId(), city, perks.getFirst(), perks.get(1), perks.get(2), color, ElectionType.OWNER_CHOOSE);
            MessagesManager.sendMessage(player, Component.text("Vous avez été désigné comme §6Maire de la Ville.\n§8§oVous pourrez choisir vos Réformes dans " + DateUtils.getTimeUntilNextDay(PHASE_1_DAY)), Prefix.MAYOR, MessageType.SUCCESS, true);
        }

        // Lois
        MayorManager.createCityLaws(city, false, null);

        // Mascotte
        player.getWorld().getBlockAt(mascotLocation).setType(Material.AIR);
        MascotsManager.createMascot(city, cityUUID, pendingCityName, player.getWorld(), mascotLocation);

        // Feedback
        MessagesManager.sendMessage(player, Component.text("§aVotre ville a été crée : " + pendingCityName), Prefix.CITY, MessageType.SUCCESS, true);
        MessagesManager.sendMessage(player, Component.text("§7+ §615 chunks gratuits"), Prefix.CITY, MessageType.INFO, false);

        DynamicCooldownManager.use(playerUUID.toString(), "city:big", 60000);
        DynamicCooldownManager.use(cityUUID, "city:immunity", IMMUNITY_COOLDOWN);

        return true;
    }
}