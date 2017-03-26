package net.nyhm.protonet;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;

/**
 * Use an instance of this class to initialize the netty framework. This class configures socket
 * channels with the message decoding framework.
 */
public final class NetInitializer extends ChannelInitializer<SocketChannel>
{
    // TODO: determine (experimentally) a reasonable upper bound for protocol messages; if there are
    // special-case outliers, consider alternatives such as chunking into multiple messages.
    //
    private static final int MAX_MSG_SIZE = 1048576; // 1 megabyte

    private final PeerFactory mFactory;
    private final MessageRegistrar mRegistrar;
    private final Dispatcher mDispatcher;

    public NetInitializer(NetworkSpec spec)
    {
        mFactory = spec.getPeerFactory();
        mRegistrar = spec.getRegistrar();
        mDispatcher = spec.getDispatcher();
    }

    @Override
    public void initChannel(SocketChannel ch) throws Exception
    {
        ChannelPipeline p = ch.pipeline();

        // decoders
        p.addLast("frameDecoder", new LengthFieldBasedFrameDecoder(MAX_MSG_SIZE, 0, 4, 0, 4));
        p.addLast("messageDecoder", new MessageDecoder(mRegistrar));
        p.addLast("messageHandler", new MessageHandler(mFactory.createPeer(ch), mDispatcher));

        // encoders
        p.addLast("frameEncoder", new LengthFieldPrepender(4));
        p.addLast("messageEncoder", new MessageEncoder(mRegistrar));
    }
}
