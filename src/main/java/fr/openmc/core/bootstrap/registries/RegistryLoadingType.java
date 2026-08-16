package fr.openmc.core.bootstrap.registries;

/**
 * Les Conditions de Chargement :
 * - BOOTSTRAP, registre a charger avant le chargement du monde et des datapacks
 * - RUNTIME, registre a charger pendant le chargement du serveur
 * - AFTER_IA, registre à charger après l'initialisation complete d'ItemsAdder {@link dev.lone.itemsadder.api.Events.ItemsAdderLoadDataEvent}
 * - NOT_LOADED_UNIT_TEST, registre non chargé pendant les tests unitaires
 */
public enum RegistryLoadingType {
    BOOTSTRAP,
    RUNTIME,
    AFTER_IA,
    NOT_LOADED_UNIT_TEST
}
