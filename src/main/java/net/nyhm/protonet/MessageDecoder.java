package net.nyhm.protonet;

import com.google.protobuf.Message;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * This decoder consumes a message identifier and parses the subsequent message.
 * This decoder assumes that the input buffer will contain a fully parsable message
 * (preceded by its unique identifier). That is, parsing will fail if the input buffer
 * has not yet been filled with an entire message from the network. To avoid this,
 * use a LengthFieldBasedFrameDecoder before this decoder to fill the buffer with a
 * full frame of bytes.
 */
final class MessageDecoder extends ByteToMessageDecoder
{
    private final MessageRegistrar mRegistrar;

    MessageDecoder(MessageRegistrar registrar)
    {
        mRegistrar = registrar;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out)
    {
        if (in.readableBytes() < 1) return; // LengthFieldBasedFrameDecoder will pass full buffer
        int id = in.readInt(); // TODO: Sanity/security check to guard against unexpectedly large frames
        byte[] bytes = new byte[in.readableBytes()];
        in.readBytes(bytes);
        Message msg = mRegistrar.parse(id, bytes);
        if (msg != null) out.add(msg);
    }
}
