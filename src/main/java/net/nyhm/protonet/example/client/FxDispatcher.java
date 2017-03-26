package net.nyhm.protonet.example.client;

import com.google.protobuf.Message;
import javafx.application.Platform;
import net.nyhm.protonet.ProtonetClient;
import net.nyhm.protonet.Dispatcher;
import net.nyhm.protonet.Peer;

/**
 * A {@link Dispatcher} that posts messages to the JavaFX Application Thread, for use by a {@link ProtonetClient}.
 */
public class FxDispatcher extends Dispatcher
{
    @Override
    protected void dispatch(Peer peer, Message msg)
    {
        Platform.runLater(() -> super.dispatch(peer, msg));
    }
}
