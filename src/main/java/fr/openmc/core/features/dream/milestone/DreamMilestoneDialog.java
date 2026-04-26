package fr.openmc.core.features.dream.milestone;
import fr.openmc.api.input.dialog.ButtonType;
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

public class DreamMilestoneDialog {
	
	public static final Set<Player> dreamMilestoneDialogPlayer = new HashSet<>();
	
	public static void send(Player player, DreamSteps step, List<String> dialogs, int messageStep) {
		List<DialogBody> body = new ArrayList<>();
		
		for (int i = 0; i < messageStep; i++) {
			String d = dialogs.get(i);
			body.add(DialogBody.plainMessage(Component.text(d), 500));
		}
		
		ButtonType btn = (dialogs.size() <= messageStep) ? ButtonType.FINISH : ButtonType.NEXT;
		
		Dialog dialog = Dialog.create(builder -> builder.empty()
				.base(DialogBase.builder(Component.text(step.getQuest().getName()))
						.body(body)
						.canCloseWithEscape(true)
						.build()
				)
				.type(DialogType.notice(
						ActionButton.builder(Component.text(btn.getLabel()))
								.action(DialogAction.customClick((response, audience) -> {
									player.closeInventory();
									if (dialogs.size() <= messageStep) {
										removeMilestoneDialogPlayer(player);
										Consumer<Player> runnable = step.getQuest().getAfterDialog();
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
	
	public static void addMilestoneDialogPlayer(Player player) {
		dreamMilestoneDialogPlayer.add(player);
	}
	
	public static void removeMilestoneDialogPlayer(Player player) {
			dreamMilestoneDialogPlayer.remove(player);
		}
	
	public static boolean isPlayerInMilestoneDialog(Player player) {
			return dreamMilestoneDialogPlayer.contains(player);
		}
}
