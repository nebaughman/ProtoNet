package net.nyhm.protonet;

import com.google.protobuf.Message;

/**
 * A handler processes protocol messages (of a certain type).
 */
public abstract class ProtocolHandler<T extends Message>
{
    private final Class<T> mMessageType;

    protected ProtocolHandler(Class<T> messagType)
    {
        mMessageType = messagType;
    }

    final Class<T> getMessageType()
    {
        return mMessageType;
    }

    protected abstract void handle(Peer peer, T msg);
}
