package fr.openmc.core.features.milestones.menus;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.dream.DreamManager;
import fr.openmc.core.features.dream.milestone.DreamSteps;
import fr.openmc.core.utils.dialog.ButtonType;
import io.papermc.paper.dialog.Dialog;
import io.papermc.paper.registry.data.dialog.ActionButton;
import io.papermc.paper.registry.data.dialog.DialogBase;
import io.papermc.paper.registry.data.dialog.action.DialogAction;
import io.papermc.paper.registry.data.dialog.body.DialogBody;
import io.papermc.paper.registry.data.dialog.type.DialogType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickCallback;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class MilestoneDialog {
	
	public static void send(Player player, Enum step, List<String> dialogs) {
		List<DialogBody> body = new ArrayList<>();
		
		for (int i = 0; i < 1; i++) {
			String d = dialogs.get(i);
			body.add(DialogBody.plainMessage(Component.text(d), 500));
		}
		
		ButtonType btn = (dialogs.size() == 1) ? ButtonType.FINISH : ButtonType.NEXT;
		
		Dialog dialog = Dialog.create(builder -> builder.empty()
				.base(DialogBase.builder(Component.text((step.ordinal() + 1) + "/" + DreamSteps.values().length))
						.body(body)
						.canCloseWithEscape(true)
						.build()
				)
				.type(DialogType.notice(
						ActionButton.builder(Component.text(btn.getLabel()))
								.action(DialogAction.customClick((response, audience) -> {
									player.closeInventory();
									if (dialogs.size() == 1) return;
									send(player, step, dialogs, 2);
								}, ClickCallback.Options.builder().build()))
								.build()
				))
		);
		Bukkit.getServer().getScheduler().runTaskLater(OMCPlugin.getInstance(), () -> {
			player.showDialog(dialog);
			DreamManager.addMilestoneDialogPlayer(player);
		}, 20);
	}
	
	private static void send(Player player, Enum step, List<String> dialogs, int messageStep) {
		List<DialogBody> body = new ArrayList<>();
		
		for (int i = 0; i < messageStep; i++) {
			String d = dialogs.get(i);
			body.add(DialogBody.plainMessage(Component.text(d), 500));
		}
		
		ButtonType btn = (dialogs.size() <= messageStep) ? ButtonType.FINISH : ButtonType.NEXT;
		
		Dialog dialog = Dialog.create(builder -> builder.empty()
				.base(DialogBase.builder(Component.text((step.ordinal() + 1) + "/" + DreamSteps.values().length))
						.body(body)
						.canCloseWithEscape(true)
						.build()
				)
				.type(DialogType.notice(
						ActionButton.builder(Component.text(btn.getLabel()))
								.action(DialogAction.customClick((response, audience) -> {
									player.closeInventory();
									if (dialogs.size() <= messageStep) {
										DreamManager.removeMilestoneDialogPlayer(player);
										return;
									}
									send(player, step, dialogs, messageStep + 1);
								}, ClickCallback.Options.builder().build()))
								.build()
				))
		);
		player.showDialog(dialog);
	}
}
