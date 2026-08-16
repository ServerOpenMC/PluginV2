package fr.openmc.core.bootstrap.features;

public class DisableFeatureException extends RuntimeException {
    public DisableFeatureException(String message) {
        super(message);
    }
    public DisableFeatureException(String message, Throwable cause) {
        super(message, cause);
    }
}
