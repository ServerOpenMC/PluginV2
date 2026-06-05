package fr.openmc.core.utils.nms;

import net.minecraft.advancements.*;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundUpdateAdvancementsPacket;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import org.bukkit.Material;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class ToastUtils {
    private static final Identifier TOAST_IDENTIFIER = Identifier.parse("omc:custom_toast");
    private static final AdvancementRequirements ADV_REQUIREMENTS = AdvancementRequirements.allOf(Set.of("c"));

    /**
     * Affiche un Toast (= pop up lorsqu'on obtient un succes) Customisable
     * @param player Le joueur ciblé
     * @param item l'item utilisé
     * @param translationKey la clé de translation du texte
     * @param type le type du succes
     */
    public static void sendCustomToast(Player player, org.bukkit.inventory.ItemStack item, String translationKey, AdvancementType type) {
        Advancement adv = new Advancement(
                Optional.empty(),
                Optional.of(new DisplayInfo(
                        ItemStackTemplate.fromNonEmptyStack(ItemStack.fromBukkitCopy(item)),
                        Component.translatable(translationKey),
                        Component.empty(),
                        Optional.empty(),
                        type,
                        true,
                        false,
                        true
                )),
                AdvancementRewards.EMPTY,
                Map.of(),
                ADV_REQUIREMENTS,
                false
        );

        AdvancementHolder holder = new AdvancementHolder(TOAST_IDENTIFIER, adv);

        AdvancementProgress progress = new AdvancementProgress();
        progress.update(ADV_REQUIREMENTS);
        progress.grantProgress("c");

        ServerPlayer nmsPlayer = ((CraftPlayer) player).getHandle();
        nmsPlayer.connection.send(new ClientboundUpdateAdvancementsPacket(
                false,
                Set.of(holder),
                Set.of(),
                Map.of(TOAST_IDENTIFIER, progress),
                true
        ));

        nmsPlayer.connection.send(new ClientboundUpdateAdvancementsPacket(
                false,
                Set.of(),
                Set.of(TOAST_IDENTIFIER),
                Map.of(),
                false
        ));
    }

    /**
     * Affiche un Toast (= pop up lorsqu'on obtient un succes) Customisable
     * @param player Le joueur ciblé
     * @param material le material utilisé
     * @param translationKey la clé de translation du texte
     * @param type le type du succes
     */
    public static void sendCustomToast(Player player, Material material, String translationKey, AdvancementType type) {
        sendCustomToast(player, new org.bukkit.inventory.ItemStack(material), translationKey, type);
    }
}
