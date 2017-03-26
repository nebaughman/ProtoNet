package net.nyhm.protonet;

import com.google.protobuf.Message;
import io.netty.channel.Channel;

/**
 * A peer represents a connected peer. This class may be overridden to provide concrete peer types
 * (such as client ans server peers).
 */
public class Peer
{
    private final Channel mChannel;

    /**
     * A peer is created with a network {@link Channel} (by a {@link PeerFactory}).
     */
    public Peer(Channel channel)
    {
        mChannel = channel;
    }

    /**
     * This peer's channel
     */
    public Channel getChannel()
    {
        return mChannel;
    }

    /**
     * Write a message to the underlying channel.
     */
    public void send(Message msg)
    {
        mChannel.writeAndFlush(msg);
    }
}
