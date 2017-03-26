package net.nyhm.protonet;

public final class NetworkSpec
{
    private final PeerFactory mPeerFactory;
    private final MessageRegistrar mRegistrar;
    private final Dispatcher mDispatcher;

    public NetworkSpec(PeerFactory peerFactory, MessageRegistrar registrar, Dispatcher dispatcher)
    {
        mPeerFactory = peerFactory;
        mRegistrar = registrar;
        mDispatcher = dispatcher;
    }

    public PeerFactory getPeerFactory()
    {
        return mPeerFactory;
    }

    public MessageRegistrar getRegistrar()
    {
        return mRegistrar;
    }

    public Dispatcher getDispatcher()
    {
        return mDispatcher;
    }
}
