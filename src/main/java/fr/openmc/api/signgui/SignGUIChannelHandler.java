package fr.openmc.api.signgui;

import io.netty.handler.codec.MessageToMessageDecoder;

public abstract class SignGUIChannelHandler<I> extends MessageToMessageDecoder<I> {

    public abstract Object getBlockPosition();

    public abstract void close();
}
