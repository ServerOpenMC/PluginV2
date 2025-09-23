package fr.openmc.core.features.cosmetics.commands;

import fr.openmc.core.features.cosmetics.CosmeticManager;
import fr.openmc.core.features.cosmetics.CosmeticPointManager;
import fr.openmc.core.features.cosmetics.bodycostmetics.BodyCosmetic;
import fr.openmc.core.features.cosmetics.menu.CosmeticMenu;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.*;
import revxrsal.commands.bukkit.annotation.CommandPermission;

import java.util.UUID;

@Command({"cosmetic"})
@CommandPermission("omc.commands.cosmetics")
public class CosmeticCommand {
    @DefaultFor("~")
    public static void mainCommand(Player player) {
        new CosmeticMenu(player).open();
    }

    @Subcommand({"select"})
    @CommandPermission("omc.commands.cosmetics.select")
    @Description("Sélectionne une cosmétique")
    @AutoComplete("@ownedCosmetics")
    void selectCommand(Player player, String cosmeticName) {
        BodyCosmetic cosmetic = CosmeticManager.getCosmeticByName(cosmeticName);
        UUID playerUUID = player.getUniqueId();
        if (cosmetic != null && CosmeticManager.ownsCosmetic(playerUUID, cosmetic)) {
            CosmeticManager.getActivatedCosmetics().put(playerUUID, cosmetic);
            CosmeticManager.getBodyCosmeticsManager().spawnPlayerBodyCosmetic(((CraftPlayer) player).getHandle(), cosmetic);
            MessagesManager.sendMessage(player, Component.text("Vous avez sélectionné la cosmétique " + cosmetic.getName() + "."), Prefix.COSMETICS, MessageType.SUCCESS, false);
        } else {
            MessagesManager.sendMessage(player, Component.text("Cette cosmétique n'existe pas ou vous ne la possédez pas.", NamedTextColor.RED), Prefix.COSMETICS, MessageType.ERROR, false);
        }
    }

    @Subcommand({"buy"})
    @CommandPermission("omc.commands.cosmetics.buy")
    @Description("Achète une cosmétique")
    @AutoComplete("@cosmetics")
    void buyCommand(Player player, String cosmeticName) {
        BodyCosmetic cosmetic = CosmeticManager.getCosmeticByName(cosmeticName);
        UUID playerUUID = player.getUniqueId();
        if (cosmetic != null && !CosmeticManager.ownsCosmetic(playerUUID, cosmetic)) {
            if (CosmeticPointManager.hasEnoughCosmeticPoint(cosmetic.getPrice(), playerUUID)) {
                CosmeticPointManager.removeCosmeticPoint(cosmetic.getPrice(), playerUUID);
                CosmeticManager.addOwnedCosmetic(playerUUID, cosmetic);
                MessagesManager.sendMessage(player, Component.text("Vous avez acheté la cosmétique " + cosmetic.getName() + " pour " + cosmetic.getPrice() + " Points Cosmétique."), Prefix.COSMETICS, MessageType.SUCCESS, false);
            } else {
                MessagesManager.sendMessage(player, Component.text("Vous n'avez pas assez d'argent pour acheter cette cosmétique (" + CosmeticPointManager.getCosmeticPoint(playerUUID) + "/" + cosmetic.getPrice() + " Points Cosmétique).", NamedTextColor.RED), Prefix.COSMETICS, MessageType.ERROR, false);
            }
        } else {
            MessagesManager.sendMessage(player, Component.text("Cette cosmétique n'existe pas ou vous ne pouvez pas l'acheter.", NamedTextColor.RED), Prefix.COSMETICS, MessageType.ERROR, false);
        }
    }
}
