package net.nyhm.protonet.example.server;

import io.netty.channel.Channel;
import net.nyhm.protonet.ProtonetServer;
import net.nyhm.protonet.Dispatcher;
import net.nyhm.protonet.MessageRegistrar;
import net.nyhm.protonet.NetworkSpec;
import net.nyhm.protonet.Peer;
import net.nyhm.protonet.example.proto.LoginProto;

/**
 * Example driver for the server framework
 */
public final class ExampleServer
{
    private static final int PORT = 12345;

    private ExampleServer()
    {
    }

    public static void main(String[] args) throws Exception
    {
        AccountModel accounts = new AccountModel();

        Dispatcher dispatcher = new Dispatcher();
        dispatcher.register(new LoginHandler(accounts));
        dispatcher.register(new RegisterHandler(accounts));

        MessageRegistrar messages = new MessageRegistrar();
        messages.register(LoginProto.LoginRequest.class);
        messages.register(LoginProto.LoginResponse.class);
        messages.register(LoginProto.RegisterRequest.class);
        messages.register(LoginProto.RegisterResponse.class);

        NetworkSpec spec = new NetworkSpec(
            ClientPeer::new,
            messages,
            dispatcher
        );

        new ProtonetServer(spec, PORT).start();
        //
        // TODO: Shutdown/terminate/crash handling
    }

    /**
     * A client peer. (This concrete Peer could have other methods for the server to communicate with the client.)
     */
    private static final class ClientPeer extends Peer
    {
        ClientPeer(Channel channel)
        {
            super(channel);
        }
    }
}
