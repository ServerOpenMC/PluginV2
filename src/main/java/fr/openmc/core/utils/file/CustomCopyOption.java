package fr.openmc.core.utils.file;

import java.nio.file.CopyOption;

public enum CustomCopyOption implements CopyOption {
    /**
     * Copy and paste a file if it not exists.
     */
    SKIP_EXISTING,
}

