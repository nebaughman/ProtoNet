package net.nyhm.protonet;

import com.google.protobuf.Message;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * A dispatcher is provided for network initialization (via a {@link NetInitializer}).
 * The network will send incoming messages through the dispatcher to registered {@link ProtocolHandler}s.
 */
public class Dispatcher
{
    /**
     * Monitors receive notification of any and all messages
     */
    private final List<Consumer<Message>> mMonitors = new ArrayList<>();

    /**
     * Handlers receive messages of a particular type (class)
     */
    private final Map<Class,List<ProtocolHandler>> mHandlers = new HashMap<>();

    /**
     * Handlers registered to receive just the next message of a particular type (class)
     */
    private final Map<Class,List<ProtocolHandler>> mOnce = new HashMap<>();

    public Dispatcher()
    {
    }

    /**
     * Register a {@link ProtocolHandler} to receive notification of a certain message type (by exact class).
     * More than one handler may be registered for each message type.
     * The handler will be informed of all future such messages,
     * until {@link #unregister(ProtocolHandler) unregistered}.
     */
    public final void register(ProtocolHandler handler)
    {
        register(mHandlers, handler.getMessageType(), handler);
    }

    /**
     * The handler will no longer receive notifications of the specified type.
     */
    public final void unregister(ProtocolHandler handler)
    {
        unregister(mHandlers, handler.getMessageType(), handler);
        unregister(mOnce, handler.getMessageType(), handler);
    }

    /**
     * Like {@link #register(ProtocolHandler)}, but the handler will be notified of at most one
     * (the next to be received) such message, and is then effectively unregistered. More than
     * one handler may be registered for a certain message type, and they will each be notified
     * (only once) for the next receipt of such a message. A handler may
     * {@link #unregister(Map, Class, ProtocolHandler)}} before being notified.
     */
    public void registerOnce(ProtocolHandler handler)
    {
        register(mOnce, handler.getMessageType(), handler);
    }

    /**
     * Internal helper method to add the handler to a listener list. Notice that parametric
     * type safety is no longer guarded here.
     */
    private void register(Map<Class,List<ProtocolHandler>> list, Class type, ProtocolHandler handler)
    {
        List<ProtocolHandler> handlers = mHandlers.get(type);
        if (handlers == null)
        {
            handlers = new ArrayList<>();
            mHandlers.put(type, handlers);
        }
        if (handlers.contains(handler))
        {
            throw new RuntimeException("Redundant handler instance: " + type.getName() +
                " " + handler.getClass().getName()); // TODO: proper exceptions (or just warn here)
        }
        handlers.add(handler);
    }

    /**
     * Internal helper method to remove the handler from a listener list.
     */
    private void unregister(Map<Class,List<ProtocolHandler>> list, Class type, ProtocolHandler handler)
    {
        List<ProtocolHandler> handlers = list.get(type);
        if (handlers != null && handlers.remove(handler) && handlers.isEmpty()) list.remove(type);
    }

    /**
     * A <i>monitor</i> receives notification of all messages. More than one monitor may be registered.
     */
    public final void monitor(Consumer<Message> monitor)
    {
        mMonitors.add(monitor);
    }

    /**
     * The given <i>monitor</i> will no longer receive message notifications.
     */
    public final void unmonitor(Consumer<Message> monitor)
    {
        mMonitors.remove(monitor);
    }

    // TODO: Upon dispatch to each handler, wrap handle() call in protective exception catching.
    //
    /**
     * This method dispatches a message (from the specified peer). This method may be overridden,
     * but it is expected that this super method will be called to perform the dispatch.
     */
    protected void dispatch(Peer peer, Message msg)
    {
        mMonitors.forEach(m -> m.accept(msg));
        Class type = msg.getClass();
        if (mHandlers.containsKey(type)) mHandlers.get(type).forEach(h -> h.handle(peer, msg));
        if (mOnce.containsKey(type)) mOnce.get(type).forEach(h -> h.handle(peer, msg));
        mOnce.remove(type); // any listeners have now been notified
    }
}
