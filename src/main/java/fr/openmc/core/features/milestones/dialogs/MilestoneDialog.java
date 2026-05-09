package fr.openmc.core.features.milestones.dialogs;
import fr.openmc.api.input.dialog.ButtonType;
import fr.openmc.core.features.milestones.MilestoneStep;
import io.papermc.paper.dialog.Dialog;
import io.papermc.paper.registry.data.dialog.ActionButton;
import io.papermc.paper.registry.data.dialog.DialogBase;
import io.papermc.paper.registry.data.dialog.action.DialogAction;
import io.papermc.paper.registry.data.dialog.body.DialogBody;
import io.papermc.paper.registry.data.dialog.type.DialogType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickCallback;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

/**
 * Systeme de dialogue narratif
 */
@SuppressWarnings("UnstableApiUsage")
public class MilestoneDialog {
	public static final Set<Player> isInMilestoneDialog = new HashSet<>();

	/**
	 * Envoie le dialogue de narration
	 * @param player le Joueur
	 * @param step l'enum de l'étape du milestone
	 * @param dialogs le texte du dialogue
	 */
	public static void send(Player player, Enum<? extends MilestoneStep> step, List<String> dialogs) {
		send(player, step, dialogs, 1);
	}

	/**
	 * Envoie le dialogue de narration
	 * @param player le Joueur
	 * @param step l'enum de l'étape du milestone
	 * @param dialogs le texte du dialogue
	 * @param messageStep l'incrément pour les étapes du dialogues
	 */
	public static void send(Player player, Enum<? extends MilestoneStep> step, List<String> dialogs, int messageStep) {
		List<DialogBody> body = new ArrayList<>();

		for (int i = 0; i < messageStep; i++) {
			String d = dialogs.get(i);
			body.add(DialogBody.plainMessage(Component.text(d), 500));
		}
		
		ButtonType btn = (dialogs.size() <= messageStep) ? ButtonType.FINISH : ButtonType.NEXT;
		
		Dialog dialog = Dialog.create(builder -> builder.empty()
				.base(DialogBase.builder(Component.text(((MilestoneStep) step).getQuest().getName()))
						.body(body)
						.canCloseWithEscape(true)
						.build()
				)
				.type(DialogType.notice(
						ActionButton.builder(Component.text(btn.getLabel()))
								.action(DialogAction.customClick((response, audience) -> {
									player.closeInventory();
									if (dialogs.size() <= messageStep) {
										removeToMilestoneDialog(player);
										Consumer<Player> runnable = ((MilestoneStep) step).getQuest().getActionsAfterDialog();
										if (runnable != null) runnable.accept(player);
										return;
									}
									send(player, step, dialogs, messageStep + 1);
								}, ClickCallback.Options.builder().build()))
								.build()
				))
		);
		player.showDialog(dialog);
	}

	/**
	 * Ajoute le joueur comme marqué "dans un dialogue"
	 * @param player le joueur
	 */
	public static void addToMilestoneDialog(Player player) {
		isInMilestoneDialog.add(player);
	}

	/**
	 * Retire le joueur comme marqué "dans un dialogue"
	 * @param player le joueur
	 */
	public static void removeToMilestoneDialog(Player player) {
		isInMilestoneDialog.remove(player);
	}

	/**
	 * Determine si le joueur est dans un milestone dialogue ou non
	 * @param player le joueur
	 * @return boolean indiquant si le joueur est dans un dialogue ou non
	 */
	public static boolean isInMilestoneDialog(Player player) {
		return isInMilestoneDialog.contains(player);
	}
}
