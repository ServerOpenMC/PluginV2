package fr.openmc.core.features.city.sub.milestone.rewards;

import fr.openmc.core.features.city.sub.milestone.CityRewards;
import net.kyori.adventure.text.Component;

/**
 * Cette classe implémente l'interface CityRewards et
 * fournit une implémentation simple qui retourne un message.
 */
public class TemplateRewards implements CityRewards {

    // Composant de message affiché pour la récompense.
    private final Component message;

    /**
     * Constructeur qui initialise le message de la récompense.
     *
     * @param message le composant message à afficher
     */
    public TemplateRewards(Component message) {
        this.message = message;
    }

    /**
     * Retourne le composant message associé à cette récompense.
     *
     * @return le composant message
     */
    @Override
    public Component getName() {
        return message;
    }
}
