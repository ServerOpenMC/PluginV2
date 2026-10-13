package fr.openmc.core.features.city.sub.mayor.perks.event;

import fr.openmc.api.cooldown.CooldownEndEvent;
import fr.openmc.api.cooldown.DynamicCooldownManager;
import fr.openmc.core.OMCRegistry;
import fr.openmc.core.features.city.models.city.City;
import fr.openmc.core.features.city.sub.mayor.managers.MayorManager;
import fr.openmc.core.features.city.sub.mayor.models.MayorPhase;
import fr.openmc.core.features.city.sub.mayor.perks.PerkUtils;
import fr.openmc.core.features.city.sub.mayor.perks.Perks;
import fr.openmc.core.utils.bukkit.MaterialUtils;
import fr.openmc.core.utils.text.DateUtils;
import fr.openmc.core.utils.text.messages.MessageType;
import fr.openmc.core.utils.text.messages.MessagesManager;
import fr.openmc.core.utils.text.messages.Prefix;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.UUID;

public class AgriculturalEssorPerk implements Listener {

    private final DynamicCooldownManager dynamicCooldownManager = OMCRegistry.FEATURES.DYNAMIC_COOLDOWN.get();
    private final MayorManager mayorManager;

    public AgriculturalEssorPerk(MayorManager mayorManager) {
        this.mayorManager = mayorManager;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (!mayorManager.getMayorPhase().equals(MayorPhase.MAYOR_ELECTED)) return;

        Player player = event.getPlayer();

        City city = City.ofPlayer(player);

        if (city == null) return;

        if (!PerkUtils.hasPerk(city.getMayor(), Perks.AGRICULTURAL_ESSOR.getId())) return;

        if (!dynamicCooldownManager.isReady(city.getUniqueId(), "city:agricultural_essor")) {
            MessagesManager.sendMessage(player, TranslationManager.translation(
                    "feature.city.mayor.perk.event.agricultural.start",
                    Component.text(DateUtils.convertMillisToTime(dynamicCooldownManager.getRemaining(city.getUniqueId(), "city:agricultural_essor"))).color(NamedTextColor.RED)
            ), Prefix.MAYOR, MessageType.INFO, false);
        }
    }

    @EventHandler
    void onTimeEnd(CooldownEndEvent e) {
        if (!mayorManager.getMayorPhase().equals(MayorPhase.MAYOR_ELECTED)) return;

        String cooldownGroup = e.getGroup();
        if (!cooldownGroup.equals("city:agricultural_essor")) return;

        City city = City.of(e.getCooldownUUID());

        if (city == null) return;

        if (!PerkUtils.hasPerk(city.getMayor(), Perks.AGRICULTURAL_ESSOR.getId())) return;

        for (UUID memberUUID : city.getMembers()) {
            Player player = Bukkit.getPlayer(memberUUID);

            if (player == null || !player.isOnline()) continue;

            MessagesManager.sendMessage(player, TranslationManager.translation("feature.city.mayor.perk.event.agricultural.end"), Prefix.MAYOR, MessageType.INFO, false);
        }
    }

    @EventHandler
    public void onCropBreak(BlockBreakEvent event) {
        if (!mayorManager.getMayorPhase().equals(MayorPhase.MAYOR_ELECTED)) return;

        Player player = event.getPlayer();
        City city = City.ofPlayer(player);

        if (city == null) return;

        if (!PerkUtils.hasPerk(city.getMayor(), Perks.AGRICULTURAL_ESSOR.getId())) return;

        if (dynamicCooldownManager.isReady(city.getUniqueId(), "city:agricultural_essor")) return;

        Block block = event.getBlock();

        if (!MaterialUtils.isCrop(block.getType())) return;

        event.setDropItems(false);

        Collection<ItemStack> drops = block.getDrops(player.getInventory().getItemInMainHand());

        if (!drops.isEmpty()) {
            for (ItemStack drop : drops) {
                block.getWorld().dropItemNaturally(block.getLocation(), drop);
                block.getWorld().dropItemNaturally(block.getLocation(), drop.clone());
            }
        }
    }
}
