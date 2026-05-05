package fr.openmc.core.features.dream.mecanism.cold;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.dream.models.registry.items.DreamEquipableItem;
import fr.openmc.core.features.dream.registries.DreamItemRegistry;
import fr.openmc.core.utils.bukkit.ParticleUtils;
import fr.openmc.core.utils.bukkit.PlayerUtils;
import fr.openmc.core.utils.text.messages.MessageType;
import fr.openmc.core.utils.text.messages.MessagesManager;
import fr.openmc.core.utils.text.messages.Prefix;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.Campfire;
import org.bukkit.damage.DamageSource;
import org.bukkit.damage.DamageType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;

public class ColdManager {

    private static final NamespacedKey COLD_SPEED_KEY = new NamespacedKey(OMCPlugin.getInstance(), "cold_speed_modifier");
    private static final NamespacedKey COLD_MINING_SPEED_KEY = new NamespacedKey(OMCPlugin.getInstance(), "cold_mining_speed_modifier");

    public static void init() {
        OMCPlugin.registerEvents(
                new ColdListener()
        );
    }

    public static int calculateColdResistance(Player player) {
        int sommeColdResistance = 0;

        List<ItemStack> armorContents = Arrays.stream(player.getEquipment().getArmorContents()).toList();
        if (armorContents.isEmpty()) return 0;

        for (ItemStack item : armorContents) {
            if (item == null || item.getType() == Material.AIR) continue;
            if (DreamItemRegistry.getByItemStack(item) instanceof DreamEquipableItem dreamEquipableItem) {
                Integer coldResistance = dreamEquipableItem.getColdResistance();

                if (coldResistance != null) {
                    sommeColdResistance += coldResistance;
                }
            }
        }

        return sommeColdResistance;
    }

    public static int getColdLevel(int cold) {
        if (cold >= 100) return 5;
        if (cold >= 85)  return 4;
        if (cold >= 75)  return 3;
        if (cold >= 50)  return 2;
        if (cold >= 25)  return 1;
        return 0;
    }

    public static void sendColdLevelMessage(Player player, int level) {
        Component message = switch (level) {
            case 1 -> Component.text("*Vous ressentez le froid vous ralentir...*", NamedTextColor.DARK_GRAY, TextDecoration.ITALIC);
            case 2 -> Component.text("*Le froid se disperse dans tout votre corps*", NamedTextColor.DARK_GRAY, TextDecoration.ITALIC);
            case 3 -> Component.text("*Vous tremblez de froid*", NamedTextColor.DARK_GRAY, TextDecoration.ITALIC);
            case 4 -> Component.text("*Vous êtes sur le point de mourir de froid*", NamedTextColor.DARK_GRAY, TextDecoration.ITALIC);
            default -> null;
        };

        if (message != null) {
            MessagesManager.sendMessage(player, message, Prefix.DREAM, MessageType.INFO, false);
        }
    }

    public static void applyColdEffects(Player player, int cold) {
        int freezeTicks = (int) Math.min(140, (cold / 85.0) * 140);
        PlayerUtils.showFreezeEffect(player, freezeTicks);

        int level = getColdLevel(cold);

        removeColdModifier(player);

        switch (level) {
            case 1 -> {
                applySpeedModifier(player, -0.2);
                applyMiningSpeedModifier(player, -0.3);
            }
            case 2 -> {
                applySpeedModifier(player, -0.4);
                applyMiningSpeedModifier(player, -0.5);
            }
            case 3 -> {
                applySpeedModifier(player, -0.7);
                applyMiningSpeedModifier(player, -0.8);
            }
            case 4 -> {
                applySpeedModifier(player, -0.8);
                applyMiningSpeedModifier(player, -0.9);
                player.damage(0.25, DamageSource.builder(DamageType.FREEZE).build());
            }
            case 5 -> player.setHealth(0);
        }

        if (level > 0) {
            ParticleUtils.sendParticlePacket(player, Particle.SPIT, player.getLocation().add(0, 1, 0), 10, 0.3, 0.5, 0.3, 0.01, null);
        }
    }

    private static void applySpeedModifier(Player player, double reductionPercent) {
        AttributeInstance speed = player.getAttribute(Attribute.MOVEMENT_SPEED);
        if (speed == null) return;

        AttributeModifier modifier = new AttributeModifier(COLD_SPEED_KEY, reductionPercent, AttributeModifier.Operation.ADD_SCALAR);

        speed.addModifier(modifier);
    }

    private static void applyMiningSpeedModifier(Player player, double reductionPercent) {
        AttributeInstance miningSpeed = player.getAttribute(Attribute.BLOCK_BREAK_SPEED);
        if (miningSpeed == null) return;

        AttributeModifier modifier = new AttributeModifier(COLD_MINING_SPEED_KEY, reductionPercent, AttributeModifier.Operation.ADD_SCALAR);

        miningSpeed.addModifier(modifier);
    }

    private static void removeColdModifier(Player player) {
        AttributeInstance speed = player.getAttribute(Attribute.MOVEMENT_SPEED);
        AttributeInstance miningSpeed = player.getAttribute(Attribute.BLOCK_BREAK_SPEED);
        if (speed == null || miningSpeed == null) return;

        speed.getModifiers().stream()
                .filter(modifier -> modifier.getKey().equals(COLD_SPEED_KEY))
                .findFirst()
                .ifPresent(speed::removeModifier);
        miningSpeed.getModifiers().stream()
                .filter(modifier -> modifier.getKey().equals(COLD_MINING_SPEED_KEY))
                .findFirst()
                .ifPresent(miningSpeed::removeModifier);
    }

    public static boolean isNearHeatSource(Player player) {
        Location loc = player.getLocation();
        for (int x = -5; x <= 5; x++) {
            for (int y = -2; y <= 2; y++) {
                for (int z = -5; z <= 5; z++) {
                    Block block = loc.clone().add(x, y, z).getBlock();
                    if (block.getType() == Material.CAMPFIRE && block.getBlockData() instanceof Campfire campfire && campfire.isLit())
                        return true;
                }
            }
        }
        return false;
    }
}
