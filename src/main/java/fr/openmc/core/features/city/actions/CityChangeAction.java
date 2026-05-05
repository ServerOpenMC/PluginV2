package fr.openmc.core.features.city.actions;

import fr.openmc.api.cooldown.DynamicCooldownManager;
import fr.openmc.api.menulib.template.ConfirmMenu;
import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.CityType;
import fr.openmc.core.features.city.conditions.CityTypeConditions;
import fr.openmc.core.features.city.sub.mascots.MascotsManager;
import fr.openmc.core.features.city.sub.mascots.models.Mascot;
import fr.openmc.core.features.city.sub.mascots.models.MascotsLevels;
import fr.openmc.core.features.city.sub.milestone.rewards.FeaturesRewards;
import fr.openmc.core.utils.text.DateUtils;
import fr.openmc.core.utils.text.messages.MessageType;
import fr.openmc.core.utils.text.messages.MessagesManager;
import fr.openmc.core.utils.text.messages.Prefix;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class CityChangeAction {
    private static final long COOLDOWN_CHANGE_TYPE = 2 * 24 * 60 * 60 * 1000L; // 2 jours
    private static final NamespacedKey MAX_HEALTH_KEY = NamespacedKey.fromString("openmc:mascot_max_health");

    public static void beginChangeCity(Player player, CityType typeChange) {
        City city = CityManager.getPlayerCity(player.getUniqueId());

        if (!CityTypeConditions.canCityChangeType(city, player, typeChange)) return;

        if (typeChange.equals(CityType.WAR) && !FeaturesRewards.hasUnlockFeature(city, FeaturesRewards.Feature.TYPE_WAR)) {
	        MessagesManager.sendMessage(player, TranslationManager.translation("messages.city.havent_unlocked_feature",
                            Component.text(FeaturesRewards.getFeatureUnlockLevel(FeaturesRewards.Feature.TYPE_WAR))),
                    Prefix.CITY, MessageType.ERROR, false);
            return;
        }

        Component inPeace = TranslationManager.translation("feature.city.type.in_peace").color(NamedTextColor.GREEN);
        Component inWar = TranslationManager.translation("feature.city.type.in_war").color(NamedTextColor.RED);
        Component cityTypeActuel = city.getType() == CityType.WAR ? inWar : inPeace;
        Component cityTypeAfter = city.getType() == CityType.WAR ? inPeace : inWar;

        List<Component> confirmLore = new ArrayList<>();
        confirmLore.add(TranslationManager.translation("feature.city.type.confirm_change"));
        confirmLore.add(TranslationManager.translation("feature.city.type.change_type_to_type", cityTypeActuel, cityTypeAfter));
        if (typeChange == CityType.WAR) {
            confirmLore.add(Component.empty());
	        confirmLore.add(TranslationManager.translation("feature.city.type.warning_war"));
        }
        confirmLore.add(Component.empty());
	    confirmLore.add(TranslationManager.translation("feature.city.type.mascot_losing_level"));

        ConfirmMenu menu = new ConfirmMenu(
                player,
                () -> {
                    finishChange(player);
                    player.closeInventory();
                },
                player::closeInventory,
                confirmLore,
                List.of(
		                TranslationManager.translation("feature.city.type.not_change_type")
                )
        );
        menu.open();
    }

    public static void finishChange(Player sender) {
        City city = CityManager.getPlayerCity(sender.getUniqueId());

        if (!CityTypeConditions.canCityChangeType(city, sender, city.getType() == CityType.WAR ? CityType.PEACE : CityType.WAR)) {
            MessagesManager.sendMessage(sender, TranslationManager.translation("messages.global.cannot_do_this"), Prefix.CITY, MessageType.ERROR, false);
            return;
        }

        if (city == null) return;

        Mascot mascot = city.getMascot();

        if (mascot == null) {
	        MessagesManager.sendMessage(sender, TranslationManager.translation("feature.city.type.mascot_not_exist_change_type"),
                    Prefix.CITY, MessageType.ERROR, false);
            return;
        }

        if (!mascot.isAlive()) {
	        MessagesManager.sendMessage(sender, TranslationManager.translation("feature.city.type.mascot_must_by_alive_change_type"),
                    Prefix.CITY, MessageType.ERROR, false);
            return;
        }

        if (!DynamicCooldownManager.isReady(city.getUniqueId(), "city:type")) {
	        MessagesManager.sendMessage(sender, TranslationManager.translation("feature.city.type.must_wait_before_change_type",
                    Component.text(DateUtils.convertMillisToTime(DynamicCooldownManager.getRemaining(city.getUniqueId(), "city:type")))),
                    Prefix.CITY, MessageType.ERROR, false);
            return;
        }

        city.changeType();
        DynamicCooldownManager.use(city.getUniqueId(), "city:type", COOLDOWN_CHANGE_TYPE);

        LivingEntity mob = (LivingEntity) mascot.getEntity();
        MascotsLevels mascotsLevels = MascotsLevels.valueOf("level" + mascot.getLevel());

        double lastHealth = mascotsLevels.getHealth();
        int newLevel = Integer.parseInt(String.valueOf(mascotsLevels).replaceAll("[^0-9]", "")) - 1;
        if (newLevel < 1) {
            newLevel = 1;
        }

        mascot.setLevel(newLevel);
        mascotsLevels = MascotsLevels.valueOf("level" + mascot.getLevel());

        try {
            double maxHealth = mascotsLevels.getHealth();
            mob.getAttribute(Attribute.MAX_HEALTH).removeModifier(MAX_HEALTH_KEY);
            mob.getAttribute(Attribute.MAX_HEALTH).addModifier(new AttributeModifier(MAX_HEALTH_KEY, maxHealth, AttributeModifier.Operation.ADD_NUMBER));
            if (mob.getHealth() >= lastHealth) {
                mob.setHealth(maxHealth);
            }

            mob.customName(MascotsManager.getAliveMascotName(
                    city.getName(),
                    mob.getHealth(),
                    maxHealth
            ));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        Component inPeace = TranslationManager.translation("feature.city.type.in_peace").color(NamedTextColor.GREEN);
        Component inWar = TranslationManager.translation("feature.city.type.in_war").color(NamedTextColor.RED);
        Component cityTypeActuel = city.getType() == CityType.WAR ? inPeace : inWar;
        Component cityTypeAfter = city.getType() == CityType.WAR ? inWar : inPeace;

        MessagesManager.sendMessage(sender, TranslationManager.translation("feature.city.type.change_type_success",
                        cityTypeActuel, cityTypeAfter),
                Prefix.CITY, MessageType.SUCCESS, false);
    }
}