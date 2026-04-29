package fr.openmc.core.features.dream.mecanism.altar;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.dream.models.registry.items.DreamItem;
import fr.openmc.core.features.dream.registries.DreamItemRegistry;
import fr.openmc.core.utils.ItemUtils;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import net.kyori.adventure.text.Component;
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
            MessagesManager.sendMessage(player, Component.text("Cet objet ne peut pas être utilisé dans l'§5Altar."), Prefix.DREAM, MessageType.ERROR, false);
            return;
        }

        AltarRecipes recipe = AltarRecipes.match(dreamItem);
        if (recipe == null) {
            MessagesManager.sendMessage(player, Component.text("§cAucune recette ne correspond à cet objet."), Prefix.DREAM, MessageType.ERROR, false);
            return;
        }

        ItemUtils.setTag(item, "altar_bound", altarLoc.toString());

        ItemDisplay display = altarLoc.getWorld().spawn(altarLoc.clone().add(0.5, 2, 0.5), ItemDisplay.class, ent -> {
            ent.setItemStack(item.asOne());
            ent.setGlowing(true);
        });

        floatingItems.put(altarLoc, display);
        boundPlayers.put(altarLoc, player.getUniqueId());

        MessagesManager.sendMessage(player, Component.text("§aVotre objet est lié à l'§5Altar"), Prefix.DREAM, MessageType.ERROR, false);
    }

    public static void unbind(Location altarLoc) {
        boundPlayers.remove(altarLoc);
        ItemDisplay display = floatingItems.remove(altarLoc);
        if (display != null) display.remove();
    }

    public static void tryRitual(Player player, Location altarLoc) {
        if (!boundPlayers.containsKey(altarLoc)) return;
        if (!boundPlayers.get(altarLoc).equals(player.getUniqueId())) {
            MessagesManager.sendMessage(player, Component.text("Cet §5Altar §fappartient déjà à un autre joueur"), Prefix.DREAM, MessageType.ERROR, false);
            return;
        }

        ItemStack hand = player.getInventory().getItemInMainHand();
        if (ItemUtils.getTag(hand, "altar_bound") == null) {
            MessagesManager.sendMessage(player, Component.text("Vous devez tenir l’objet lié à l'§5Altar §fdans votre main"), Prefix.DREAM, MessageType.ERROR, false);
            return;
        }

        DreamItem input = DreamItemRegistry.getByItemStack(hand);
        AltarRecipes recipe = AltarRecipes.match(input);

        if (recipe == null) return;

        DreamItem soulOrb = DreamItemRegistry.getByName("omc_dream:soul");

        if (soulOrb == null) {
            MessagesManager.sendMessage(player, Component.text("Erreur : omc_dream:soul pas trouvé"), Prefix.DREAM, MessageType.ERROR, false);
            return;
        }

        int required = recipe.getSoulsRequired();

        if (!ItemUtils.hasEnoughItems(player, soulOrb.getBest(), required)) {
            MessagesManager.sendMessage(player, Component.text("Vous n’avez pas assez d’§5Âmes nécessaires ! §f(§5" + required + " Ames §fnéccessaire)"), Prefix.DREAM, MessageType.ERROR, false);
            return;
        }

        ItemUtils.removeItemsFromInventory(player, soulOrb.getBest(), required);
        ItemUtils.removeItemsFromInventory(player, hand, 1);

        player.getInventory().addItem(recipe.getOutput().getBest());

        Bukkit.getScheduler().runTask(OMCPlugin.getInstance(), () ->
                Bukkit.getServer().getPluginManager().callEvent(new AltarCraftingEvent(player, recipe.getOutput()))
        );

        unbind(altarLoc);
        MessagesManager.sendMessage(player, Component.text("§5Rituel accompli !"), Prefix.DREAM, MessageType.SUCCESS, false);
        player.playSound(player.getLocation(), Sound.ENTITY_LIGHTNING_BOLT_IMPACT, 2.0f, 1.0f);
    }
}