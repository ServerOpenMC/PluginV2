package fr.openmc.core.features.quests.command;

import fr.openmc.core.features.quests.menus.QuestsMenu;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.DefaultFor;
import revxrsal.commands.annotation.Description;

@Command({"quest"})
@Description("Commande pour les quêtes")
public class QuestCommand {

    @DefaultFor({"~"})
    @Description("Ouvre le menu des quêtes")
    public void onQuest(Player player) {
        (new QuestsMenu(player)).open();
    }
}
