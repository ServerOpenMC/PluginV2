package fr.openmc.core.features.dream.mecanism.altar;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.dream.events.AltarBindEvent;
import fr.openmc.core.features.dream.events.AltarCraftingEvent;
import fr.openmc.core.features.dream.mecanism.altar.tasks.AltarCheckTask;
import fr.openmc.core.features.dream.mecanism.altar.tasks.AltarParticlesTask;
import fr.openmc.core.features.dream.models.registry.items.DreamItem;
import fr.openmc.core.features.dream.registries.DreamItemRegistry;
import fr.openmc.core.utils.bukkit.ItemUtils;
import fr.openmc.core.utils.text.messages.MessageType;
import fr.openmc.core.utils.text.messages.MessagesManager;
import fr.openmc.core.utils.text.messages.Prefix;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AltarManager {

    public static final Map<Location, UUID> boundPlayers = new HashMap<>();
    public static final Map<Location, ItemDisplay> floatingItems = new HashMap<>();

    public static void init() {
        new AltarCheckTask().runTaskTimer(OMCPlugin.getInstance(), 0L, 40L);
        new AltarParticlesTask().runTaskTimer(OMCPlugin.getInstance(), 0L, 2L);
        OMCPlugin.registerEvents(new AltarListener());
    }

    public static boolean hasItem(Location loc) {
        return boundPlayers.containsKey(loc);
    }

    public static void bindItem(Player player, Location altarLoc, ItemStack item) {
        DreamItem dreamItem = DreamItemRegistry.getByItemStack(item);
        if (dreamItem == null) {
            MessagesManager.sendMessage(player, TranslationManager.translation("feature.dream.altar.message.unusable_item"), Prefix.DREAM, MessageType.ERROR, false);
            return;
        }

        AltarRecipes recipe = AltarRecipes.match(dreamItem);
        if (recipe == null) {
            MessagesManager.sendMessage(player, TranslationManager.translation("feature.dream.altar.message.no_recipe"), Prefix.DREAM, MessageType.ERROR, false);
            return;
        }

        ItemUtils.setTag(item, "altar_bound", altarLoc.toString());

        ItemDisplay display = altarLoc.getWorld().spawn(altarLoc.clone().add(0.5, 2, 0.5), ItemDisplay.class, ent -> {
            ent.setItemStack(item.asOne());
            ent.setGlowing(true);
        });

        floatingItems.put(altarLoc, display);
        boundPlayers.put(altarLoc, player.getUniqueId());

		Bukkit.getPluginManager().callEvent(new AltarBindEvent(player, dreamItem, recipe, altarLoc));
        MessagesManager.sendMessage(player, TranslationManager.translation("feature.dream.altar.message.bound"), Prefix.DREAM, MessageType.ERROR, false);
    }

    public static void unbind(Location altarLoc) {
        boundPlayers.remove(altarLoc);
        ItemDisplay display = floatingItems.remove(altarLoc);
        if (display != null) display.remove();
    }

    public static void tryRitual(Player player, Location altarLoc) {
        if (!boundPlayers.containsKey(altarLoc)) return;
        if (!boundPlayers.get(altarLoc).equals(player.getUniqueId())) {
            MessagesManager.sendMessage(player, TranslationManager.translation("feature.dream.altar.message.already_bound"), Prefix.DREAM, MessageType.ERROR, false);
            return;
        }

        ItemStack hand = player.getInventory().getItemInMainHand();
        if (ItemUtils.getTag(hand, "altar_bound") == null) {
            MessagesManager.sendMessage(player, TranslationManager.translation("feature.dream.altar.message.must_hold_bound_item"), Prefix.DREAM, MessageType.ERROR, false);
            return;
        }

        DreamItem input = DreamItemRegistry.getByItemStack(hand);
        AltarRecipes recipe = AltarRecipes.match(input);

        if (recipe == null) return;

        DreamItem soulOrb = DreamItemRegistry.SOUL;

        if (soulOrb == null) {
            MessagesManager.sendMessage(player, TranslationManager.translation("feature.dream.altar.message.soul_not_found"), Prefix.DREAM, MessageType.ERROR, false);
            return;
        }

        int required = recipe.getSoulsRequired();

        if (!ItemUtils.hasEnoughItems(player, soulOrb.getBest(), required)) {
            MessagesManager.sendMessage(player, TranslationManager.translation(
                    "feature.dream.altar.message.not_enough_souls",
                    Component.text(required).color(NamedTextColor.DARK_PURPLE)
            ), Prefix.DREAM, MessageType.ERROR, false);
            return;
        }

        ItemUtils.removeItemsFromPlayerInventory(player, soulOrb.getBest(), required);
        ItemUtils.removeItemsFromPlayerInventory(player, hand, 1);

        player.getInventory().addItem(recipe.getOutput().getBest());

        Bukkit.getScheduler().runTask(OMCPlugin.getInstance(), () ->
                Bukkit.getServer().getPluginManager().callEvent(new AltarCraftingEvent(player, recipe, recipe.getOutput()))
        );

        unbind(altarLoc);
        MessagesManager.sendMessage(player, TranslationManager.translation("feature.dream.altar.message.ritual_complete"), Prefix.DREAM, MessageType.SUCCESS, false);
        player.playSound(player.getLocation(), Sound.ENTITY_LIGHTNING_BOLT_IMPACT, 2.0f, 1.0f);
    }
}
