package fr.openmc.core.hooks.itemsadder.placeholders;

/**
 * Définit un placeholder ItemsAdder personnalisé.
 */
public interface IAPlaceholder {
    /**
     * @return le nom unique du placeholder (ex: "dream_rarity").
     */
    String name();

    /**
     * @param argument l'argument brut fourni dans le placeholder.
     * @return la valeur qui doit être remplacer par le placeholder
     */
    String resolve(String argument);
}
