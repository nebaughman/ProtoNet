package net.nyhm.protonet;

import io.netty.channel.Channel;

/**
 * A peer factory creates a (concrete) {@link Peer} per network {@link Channel} (which represent
 * connected peers).
 */
public interface PeerFactory<T extends Peer>
{
    T createPeer(Channel channel);
}
