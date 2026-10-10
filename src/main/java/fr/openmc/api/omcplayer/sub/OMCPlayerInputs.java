package fr.openmc.api.omcplayer.sub;

import fr.openmc.api.input.ChatInput;
import fr.openmc.api.input.dialog.DialogInput;
import fr.openmc.api.input.location.ItemInteraction;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.function.Consumer;
import java.util.function.Function;

public class OMCPlayerInputs extends OMCPlayerFeat {
    public OMCPlayerInputs(Player player) {
        super(player);
    }

    /**
     * Envoie une demande d'input de string au joueur sous forme de dialogue
     * @param lore le texte qui sera affiché dans le dialogue
     * @param maxLength la longueur maximale de la réponse
     * @param callback la fonction qui sera appelée lorsque le joueur aura répondu
     */
    public void sendStringDialogInput(Component lore, int maxLength, Consumer<String> callback) {
        DialogInput.send(getPlayer(), lore, maxLength, callback);
    }

    /**
     * Envoie une demande d'input de string au joueur via un chat
     * @param startMessage le message qui sera affiché dans le chat
     * @param callback la fonction qui sera appelée lorsque le joueur aura répondu
     */
    public void sendStringChatInput(Component startMessage, Consumer<String> callback) {
        ChatInput.sendInput(getPlayer(), startMessage, callback);
    }


    /**
     * Envoie une demande d'input de float au joueur sous forme de dialogue
     * @param lore le texte qui sera affiché dans le dialogue
     * @param minSize la valeur minimale de la réponse
     * @param maxSize la valeur maximale de la réponse
     * @param initial la valeur initiale de la réponse
     * @param callback la fonction qui sera appelée lorsque le joueur aura répondu
     */
    public void sendFloatDialogInput(Component lore, float minSize, float maxSize, float initial, Consumer<Float> callback) {
        DialogInput.sendFloat(getPlayer(), lore, minSize, maxSize, initial, callback);
    }


    public void sendLocationInput(ItemStack item,
                                  String chronometerGroup,
                                  int chronometerTime,
                                  String startMessage,
                                  Component endMessage,
                                  Function<Location, Boolean> result,
                                  Runnable onFail) {
        ItemInteraction.runLocationInteraction(getPlayer(), item, chronometerGroup, chronometerTime, startMessage, endMessage, result, onFail);
    }
}

