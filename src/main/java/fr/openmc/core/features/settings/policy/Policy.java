package fr.openmc.core.features.settings.policy;

public interface Policy {
    /**
     * Returns the display name of the policy.
     *
     * @return the display name
     */
    String getDisplayName();

    /**
     * Returns the description of the policy.
     *
     * @return the description
     */
    String getDescription();
}
