package net.nyhm.protonet.example.client;

import net.nyhm.protonet.Peer;
import net.nyhm.protonet.ProtocolHandler;
import net.nyhm.protonet.example.proto.LoginProto.RegisterResponse;
import net.nyhm.protonet.util.Log;

/**
 * Login response protocol message handler.
 */
final class RegisterHandler extends ProtocolHandler<RegisterResponse>
{
    RegisterHandler()
    {
        super(RegisterResponse.class);
    }

    @Override
    protected void handle(Peer peer, RegisterResponse msg)
    {
        Log.info("Register response: " + msg.getMessage());
    }
}
