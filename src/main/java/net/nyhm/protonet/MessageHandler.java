package net.nyhm.protonet;

import com.google.protobuf.Message;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import net.nyhm.protonet.util.Log;

/**
 * This class integrates the {@link Peer} and {@link Dispatcher} system to the
 * underlying (netty) network framework.
 */
final class MessageHandler extends SimpleChannelInboundHandler<Message>
{
    private final Peer mPeer;
    private final Dispatcher mDispatcher;

    MessageHandler(Peer peer, Dispatcher dispatcher)
    {
        mPeer = peer;
        mDispatcher = dispatcher;
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx)
        throws Exception
    {
        ctx.flush();
    }

    // TODO: Rename method in netty 5.0
    //
    //protected void messageReceived(ChannelHandlerContext ctx, Object msg)
    //
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Message msg)
    {
        mDispatcher.dispatch(mPeer, msg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
        throws Exception
    {
        Log.warn("Exception handling request", cause);
        ctx.close();
    }
}
