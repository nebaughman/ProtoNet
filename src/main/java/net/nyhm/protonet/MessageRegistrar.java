package net.nyhm.protonet;

import com.google.protobuf.Message;
import net.nyhm.protonet.util.Log;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * All protocol message classes must be catalogued here. This class assists the {@link MessageDecoder}
 * in parsing incoming bytes into Message objects (to be dispatched).
 */
public final class MessageRegistrar
{
    private final Map<Integer,MessageParser> mParsers = new HashMap<>();

    /*
    static
    {
        register(LoginProto.LoginRequest.class);
        register(LoginProto.LoginResponse.class);
        register(LoginProto.RegisterRequest.class);
        register(LoginProto.RegisterResponse.class);
    }
    */

    public MessageRegistrar()
    {
    }

    /**
     * Register a concrete Message type, which prepares a {@link MessageParser} for this type.
     */
    public <T extends Message> void register(Class<T> type)
    {
        try
        {
            mParsers.put(getID(type), new MessageParser<T>(type));
        }
        catch (NoSuchMethodException e)
        {
            Log.warn("Bad parser", e); // TODO: fail & halt
        }
    }

    /**
     * This method produces a unique ID value for a message class.
     */
    private <T extends Message> int getID(Class<T> type)
    {
        return type.getName().hashCode();
        // TODO: use better ID: not bound to class name, no collisions, language independent
    }

    /**
     * This method produces a unique ID value for a Message.
     */
    int geID(Message msg)
    {
        return getID(msg.getClass());
    }

    /**
     * This method parses a Message from its (protobuf) byte-packed format.
     *
     * @return the parsed Message object, null if unable to parse
     */
    Message parse(int id, byte[] bytes)
    {
        MessageParser parser = mParsers.get(id);
        if (parser == null) return null;
        try
        {
            return parser.parse(bytes);
        }
        catch (Exception e)
        {
            Log.warn("Bad message class", e);
            return null;
        }
    }

    /**
     * Helper to capture the Class and parser Method from a concrete Message class.
     */
    private static final class MessageParser<T extends Message>
    {
        private final Class<T> mClass;
        private final Method mParser;

        /**
         * Message classes must contain a static <tt>parseFrom</tt> method that takes an array of bytes.
         * Protobuf-generated Message classes have this method signature.
         */
        MessageParser(Class<T> type) throws NoSuchMethodException
        {
            mClass = type;
            mParser = type.getMethod("parseFrom", byte[].class);
        }

        /**
         * Parse a Message instance from protobuf bytes. The bytes must contain a message
         * matching this parser's class.
         */
        Message parse(byte[] bytes) throws InvocationTargetException, IllegalAccessException
        {
            return (Message)mParser.invoke(mClass, bytes); // invoke static method
        }
    }
}
