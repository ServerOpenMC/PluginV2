package fr.openmc.core.features.adminshop;

import org.bukkit.Material;

public record ShopCategory(String id, String name, Material material, int position) {}