package fr.openmc.core.features.adminshop;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;

public record ShopCategory(String id, Component name, Material material, int position) {}