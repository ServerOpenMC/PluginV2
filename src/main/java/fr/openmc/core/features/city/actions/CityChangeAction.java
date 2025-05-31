package fr.openmc.core.features.city.actions;

import fr.openmc.api.cooldown.DynamicCooldownManager;
import fr.openmc.api.menulib.default_menu.ConfirmMenu;
import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.CityType;
import fr.openmc.core.features.city.conditions.CityTypeConditions;
import fr.openmc.core.features.city.mascots.Mascot;
import fr.openmc.core.features.city.mascots.MascotUtils;
import fr.openmc.core.features.city.mascots.MascotsLevels;
import fr.openmc.core.utils.DateUtils;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class CityChangeAction {
    private static final long COOLDOWN_CHANGE_TYPE = 2 * 24 * 60 * 60 * 1000L; // 2 jours

    public static void beginChangeCity(Player player, CityType typeChange) {
        City city = CityManager.getPlayerCity(player.getUniqueId());

        if (!CityTypeConditions.canCityChangeType(city, player)) return;
        String cityTypeActuel;
        String cityTypeAfter;
        cityTypeActuel = city.getType() == CityType.WAR ? "§cen guerre§7" : "§aen paix§7";
        cityTypeAfter = city.getType() == CityType.WAR ? "§aen paix§7" : "§cen guerre§7";

        List<Component> confirmLore = new ArrayList<>();
        confirmLore.add(Component.text("§cEs-tu sûr de vouloir changer le type de ta §dville §7?"));
        confirmLore.add(Component.text("§7Vous allez passez d'une §dville " + cityTypeActuel + " à une §dville " + cityTypeAfter));
        if (typeChange == CityType.WAR) {
            confirmLore.add(Component.text(""));
            confirmLore.add(Component.text("§c⚠ Vous pourrez être exposé à des guerres contre des personnes à tout moment ! "));
        }
        confirmLore.add(Component.text(""));
        confirmLore.add(Component.text("§c⚠ Ta Mascotte §4§lperdera 1 niveau !"));

        ConfirmMenu menu = new ConfirmMenu(
                player,
                () -> {
                    finishChange(player);
                    player.closeInventory();
                },
                player::closeInventory,
                confirmLore,
                List.of(
                        Component.text("§7Ne pas changer le §dType de Ville")
                )
        );
        menu.open();
    }

    public static void finishChange(Player sender) {
        City city = CityManager.getPlayerCity(sender.getUniqueId());

        if (!CityTypeConditions.canCityChangeType(city, sender)) {
            MessagesManager.sendMessage(sender, MessagesManager.Message.NOPERMISSION.getMessage(), Prefix.CITY, MessageType.ERROR, false);
            return;
        }

        if (city == null) return;

        Mascot mascot = city.getMascot();

        if (MascotUtils.mascotsContains(city.getUUID())) {
            if (!mascot.isAlive()) {
                MessagesManager.sendMessage(sender, Component.text("Vous devez soigner votre mascotte avant"), Prefix.CITY, MessageType.ERROR, false);
                return;
            }
        }

        if (!DynamicCooldownManager.isReady(city.getUUID(), "city:type")) {
            MessagesManager.sendMessage(sender, Component.text("Vous devez attendre " + DateUtils.convertMillisToTime(DynamicCooldownManager.getRemaining(city.getUUID(), "city:type")) + " secondes pour changer de type de ville"), Prefix.CITY, MessageType.ERROR, false);
            return;
        }

        city.changeType();
        DynamicCooldownManager.use(city.getUUID(), "city:type", COOLDOWN_CHANGE_TYPE);

        if (mascot != null) {
            LivingEntity mob = MascotUtils.loadMascot(mascot);
            MascotsLevels mascotsLevels = MascotsLevels.valueOf("level" + mascot.getLevel());

            double lastHealth = mascotsLevels.getHealth();
            int newLevel = Integer.parseInt(String.valueOf(mascotsLevels).replaceAll("[^0-9]", "")) - 1;
            if (newLevel < 1) {
                newLevel = 1;
            }
            MascotUtils.setMascotLevel(city.getUUID(), newLevel);
            mascotsLevels = MascotsLevels.valueOf("level" + mascot.getLevel());

            try {
                int maxHealth = mascotsLevels.getHealth();
                mob.setMaxHealth(maxHealth);
                if (mob.getHealth() >= lastHealth) {
                    mob.setHealth(maxHealth);
                }
                double currentHealth = mob.getHealth();
                mob.setCustomName("§l" + city.getName() + " §c" + currentHealth + "/" + maxHealth + "❤");
            } catch (Exception exception) {
                exception.printStackTrace();
            }

            String cityTypeActuel;
            String cityTypeAfter;
            cityTypeActuel = city.getType() == CityType.WAR ? "§aen paix§7" : "§cen guerre§7";
            cityTypeAfter = city.getType() == CityType.WAR ? "§cen guerre§7" : "§aen paix§7";

            MessagesManager.sendMessage(sender, Component.text("Vous avez changé le type de votre ville de " + cityTypeActuel + " à " + cityTypeAfter), Prefix.CITY, MessageType.SUCCESS, false);

        }

        MessagesManager.sendMessage(sender, Component.text("Vous avez bien changé le §5type §fde votre §dville"), Prefix.CITY, MessageType.SUCCESS, false);
    }
}