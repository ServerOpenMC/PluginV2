package fr.openmc.core.features.corpse.commnads;

import fr.openmc.core.features.corpse.model.DBCorpse;
import fr.openmc.core.features.corpse.npc.CorpseNPCManager;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Subcommand;
import revxrsal.commands.bukkit.annotation.CommandPermission;

@Command("corpse")
public class CorpseCommand {

    @Subcommand("help")
    void onHelp() {}

    @Subcommand("abort")
    void onAbort() {}

    @Subcommand("setting")
    void onSetting() {}

    @Subcommand("locate")
    @CommandPermission("omc.admins.commands.corpse.locate")
    void onLocate() {}

   //@Subcommand("create")
   //@CommandPermission("omc.admins.commands.corpse.locate")
   //void onCreate() {
   //    DBCorpse
   //    CorpseNPCManager.createNPCS();
   //}

}
