package net.nyhm.protonet;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import net.nyhm.protonet.util.Log;
import net.nyhm.protonet.util.NamedThreadFactory;

/**
 * A (sample) protonet server implementation, which encapsulates the Netty socket handling.
 */
public final class ProtonetServer
{
    private static final int BOSS_THREADS = 1;
    private static final int WORK_THREADS = 1;

    private final NetworkSpec mSpec;

    private final int mPort;

    private Channel mChannel = null;

    public ProtonetServer(NetworkSpec spec, int port)
    {
        mSpec = spec;
        mPort = port;
    }

    public void start() throws Exception
    {
        EventLoopGroup bossGroup = new NioEventLoopGroup(
            BOSS_THREADS, new NamedThreadFactory("net-boss"));

        EventLoopGroup workerGroup = new NioEventLoopGroup(
            WORK_THREADS, new NamedThreadFactory("net-worker"));

        try
        {
            ServerBootstrap b = new ServerBootstrap();

            b.option(ChannelOption.SO_KEEPALIVE, true);
            b.option(ChannelOption.TCP_NODELAY, true);

            b.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new NetInitializer(mSpec));

            Log.info("Listening on port " + mPort);
            mChannel = b.bind(mPort).sync().channel();
            mChannel.closeFuture().sync();
        }
        finally
        {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
            Log.info("Server stopped");
        }
    }

    public void stop()
    {
        if (mChannel != null)
        {
            mChannel.close();
        }
    }
}
