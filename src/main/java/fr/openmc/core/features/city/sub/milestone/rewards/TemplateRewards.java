package fr.openmc.core.features.city.sub.milestone.rewards;

import fr.openmc.core.features.city.sub.milestone.CityRewards;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;

import java.util.Arrays;

/**
 * Cette classe implémente l'interface CityRewards et
 * fournit une implémentation simple qui retourne un message.
 */
public class TemplateRewards implements CityRewards {

    // Clé de traduction et arguments pour le message de récompense.
    private final String messageKey;
    private final ComponentLike[] args;

    /**
     * Constructeur qui initialise le message de la récompense.
     *
     * @param messageKey la clé de traduction
     * @param args       les arguments de traduction
     */
    public TemplateRewards(String messageKey, ComponentLike... args) {
        this.messageKey = messageKey;
        this.args = Arrays.copyOf(args, args.length);
    }

    /**
     * Retourne le composant message associé à cette récompense.
     *
     * @return le composant message
     */
    @Override
    public Component getName() {
        return TranslationManager.translation(messageKey, args);
    }
}
