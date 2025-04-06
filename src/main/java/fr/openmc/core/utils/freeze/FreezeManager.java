package fr.openmc.core.utils.freeze;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class FreezeManager {
	
	public static final List<Player> FROZEN_PLAYERS = new ArrayList<>();
	
	public FreezeManager() {
		Bukkit.getServer().getPluginManager().registerEvents(new FreezeListener(), OMCPlugin.getInstance());
	}
	
	public static void switchFreeze(Player player, Player target) {
		if (target == null) {
			MessagesManager.sendMessage(player, Component.text("§4Joueur introuvable"), Prefix.OPENMC, MessageType.ERROR, false);
		} else {
			String freezed = "";

			if (FROZEN_PLAYERS.contains(target)) {
				FROZEN_PLAYERS.remove(target);
				freezed = "freeze";
			} else {
				FROZEN_PLAYERS.add(target);
				target.sendTitle("§4Vous êtes freeze", "§5Si vous vous déconnectez, vous serez banni");
				freezed = "unfreeze";
			}

			MessagesManager.sendMessage(player, Component.text("§2Vous avez " + freezed + "§6" + target.getName()), Prefix.OPENMC, MessageType.SUCCESS, false);
			MessagesManager.sendMessage(target, Component.text("§4Vous avez été " + freezed), Prefix.OPENMC, MessageType.WARNING, true);
		}
	}
}
