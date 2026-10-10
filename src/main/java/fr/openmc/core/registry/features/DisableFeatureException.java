package fr.openmc.core.registry.features;

public class DisableFeatureException extends RuntimeException {
    public DisableFeatureException(String message) {
        super(message);
    }
    public DisableFeatureException(String message, Throwable cause) {
        super(message, cause);
    }
}
