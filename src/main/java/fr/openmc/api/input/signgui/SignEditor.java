package fr.openmc.api.input.signgui;

import io.netty.channel.ChannelPipeline;
import org.bukkit.Location;

// Ce code est basé sur le fichier SignEditor.java du dépôt SignGUI
// (https://github.com/Rapha149/SignGUI). Licence originale : MIT.
public record SignEditor(Object sign, Location location, Object blockPosition, ChannelPipeline pipeline) {

}
