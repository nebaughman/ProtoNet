package net.nyhm.protonet.example.client;

import net.nyhm.protonet.Peer;
import net.nyhm.protonet.ProtocolHandler;
import net.nyhm.protonet.example.proto.LoginProto.LoginResponse;
import net.nyhm.protonet.util.Log;

/**
 * Login response protocol message handler.
 */
final class LoginHandler extends ProtocolHandler<LoginResponse>
{
    LoginHandler()
    {
        super(LoginResponse.class);
    }

    @Override
    protected void handle(Peer peer, LoginResponse msg)
    {
        Log.info("Login response: " + msg.getMessage());
    }
}

