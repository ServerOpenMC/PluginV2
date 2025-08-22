package fr.openmc.core.disabled.corporation.data;

import java.util.UUID;

public record TransactionData(double value, String nature, String place, UUID sender) {
}
