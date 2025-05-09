package fr.openmc.api.signgui.exception;

// Ce code est basé sur le fichier SignGUIException.java du dépôt SignGUI
// (https://github.com/Rapha149/SignGUI). Licence originale : MIT.
/**
 * An exception thrown when an error occurs while using this api.
 */
public class SignGUIException extends RuntimeException {

    /**
     * {@inheritDoc}
     */
    public SignGUIException() {
    }

    /**
     * {@inheritDoc}
     */
    public SignGUIException(String message) {
        super(message);
    }

    /**
     * {@inheritDoc}
     */
    public SignGUIException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * {@inheritDoc}
     */
    public SignGUIException(Throwable cause) {
        super(cause);
    }

    /**
     * {@inheritDoc}
     */
    public SignGUIException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
