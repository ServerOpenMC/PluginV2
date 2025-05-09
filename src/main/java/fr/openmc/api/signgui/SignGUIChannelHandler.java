package fr.openmc.api.signgui;

import io.netty.handler.codec.MessageToMessageDecoder;

// Ce code est basé sur le fichier SignGUIChannelHandler.java du dépôt SignGUI
// (https://github.com/Rapha149/SignGUI). Licence originale : MIT.
public abstract class SignGUIChannelHandler<I> extends MessageToMessageDecoder<I> {

    public abstract Object getBlockPosition();

    public abstract void close();
}
