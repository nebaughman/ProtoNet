package net.nyhm.protonet.example.client;

import io.netty.channel.Channel;
import net.nyhm.protonet.*;
import net.nyhm.protonet.example.proto.LoginProto;
import net.nyhm.protonet.util.Log;

/**
 * Example client, which connects to an ExampleServer, registers & logs in.
 */
public final class ExampleClient
{
    private static final String HOST = "localhost";
    private static final int PORT = 12345;

    private final ProtonetClient mClient;

    private ExampleClient(ProtonetClient client)
    {
        mClient = client;
    }

    void connect() throws Exception
    {
        mClient.connect(); // TODO: handle exceptions, fail gracefully
    }

    void close()
    {
        mClient.close();
    }

    /**
     * Performs login
     */
    public void login(String email, String pass)
    {
        mClient.send(LoginProto.LoginRequest.newBuilder()
            .setEmail(email)
            .setPass(pass)
            .build());
    }

    /**
     * Register (and log in).
     */
    public void register(String email, String pass, String name)
    {
        mClient.send(LoginProto.RegisterRequest.newBuilder()
            .setEmail(email)
            .setPass(pass)
            .setName(name)
            .build());
    }

    public static void main(String[] args) throws Exception
    {
        Dispatcher dispatcher = new Dispatcher(); // FxDispatcher needs JavaFX application initialization
        dispatcher.register(new LoginHandler());
        dispatcher.register(new RegisterHandler());

        MessageRegistrar messages = new MessageRegistrar();
        messages.register(LoginProto.LoginRequest.class);
        messages.register(LoginProto.LoginResponse.class);
        messages.register(LoginProto.RegisterRequest.class);
        messages.register(LoginProto.RegisterResponse.class);

        NetworkSpec spec = new NetworkSpec(
            ServerPeer::new,
            messages,
            dispatcher
        );

        ExampleClient client = new ExampleClient(
            new ProtonetClient(spec, HOST, PORT));

        client.connect();

        Log.info("Registering...");
        client.register("a@example.com", "a-pass", "a-name");
        client.login("a@example.com", "a-pass"); // success
        client.login("a@example.com", "xxxxxx"); // failure

        // TODO: disconnect needs to be sophisticated enough to await server responses
        Thread.sleep(2000);
        client.close();
    }

    /**
     * A server peer. (This concrete Peer could have other methods for the client to communicate with the server.)
     */
    private static final class ServerPeer extends Peer
    {
        ServerPeer(Channel channel)
        {
            super(channel);
        }
    }
}
