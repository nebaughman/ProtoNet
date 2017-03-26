package net.nyhm.protonet;

import com.google.protobuf.Message;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import net.nyhm.protonet.util.Log;

/**
 * A (sample) protonet client implementation, which encapsulates the Netty socket handling.
 */
public final class ProtonetClient
{
    private final NetworkSpec mNetworkSpec;
    private final String mServerHost;
    private final int mServerPort;

    private EventLoopGroup mWorker = null;
    private Channel mChannel = null;

    //private ChannelFuture mFuture = null;
    //
    // TODO: watch future for out-of-band channel closure; shutdown worker as a side-effect of channel closure
    // (even if proper close method is not called, worker is still shut down gracefully)

    public ProtonetClient(NetworkSpec spec, String serverHost, int serverPort)
    {
        mNetworkSpec = spec;
        mServerHost = serverHost;
        mServerPort = serverPort;
    }

    /**
     * Connect and perform handshake.
     */
    public void connect() throws Exception
    {
        if (mChannel != null) return; // ignore

        mWorker = new NioEventLoopGroup();

        Bootstrap b = new Bootstrap();
        b.group(mWorker);
        b.channel(NioSocketChannel.class);
        b.option(ChannelOption.SO_KEEPALIVE, true);
        b.handler(new NetInitializer(mNetworkSpec));

        try
        {
            ChannelFuture future = b.connect(mServerHost, mServerPort).sync();
            mChannel = future.channel();
            Log.info("Connected to " + mServerHost + ":" + mServerPort);
        }
        catch (InterruptedException e)
        {
            Log.warn("Client connection interrupted", e);
            mWorker.shutdownGracefully();
            throw new Exception("Unable to connect to server"); // TODO: proper exception
        }
    }

    /**
     * Send an arbitrary message to the server
     */
    public void send(Message msg)
    {
        mChannel.writeAndFlush(msg);
    }

    /**
     * Close the connection to the server.
     */
    public void close()
    {
        try
        {
            if (mChannel != null)
            {
                Log.info("Closing connection");
                mChannel.close().sync(); // wait for close
            }
        }
        catch (InterruptedException e)
        {
            Log.warn("Interrupted closing connection", e);
        }
        finally
        {
            if (mWorker != null) mWorker.shutdownGracefully();
        }
    }
}
