package fr.openmc.core.features.city.models.city.namespaces;

import fr.openmc.core.features.city.sub.notation.models.CityNotation;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface CityNotations {
    boolean isTop10Notation();

    @Nullable CityNotation getNotationOfWeek(String weekStr);

    List<CityNotation> getAvailableNotation();

    void setNotationOfWeek(String weekStr, double architecturalNote, double coherenceNote, String description);

}
