package net.nyhm.protonet;

import com.google.protobuf.Message;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * This encoder writes the Message identifier followed by the serialized message.
 */
final class MessageEncoder extends MessageToByteEncoder<Message>
{
    private final MessageRegistrar mRegistrar;

    MessageEncoder(MessageRegistrar registrar)
    {
        mRegistrar = registrar;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, Message msg, ByteBuf out)
    {
        out.writeInt(mRegistrar.geID(msg)); // prepend message identifier
        out.writeBytes(msg.toByteArray()); // serialize object
    }
}
