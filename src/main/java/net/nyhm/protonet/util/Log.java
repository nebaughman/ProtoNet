package net.nyhm.protonet.util;

import java.util.logging.Logger;

/**
 * Simple logging facade, which can be adapted to an underlying logging service.
 */
public final class Log
{
    public enum Level { INFO, WARN, FAIL }

    private static final Logger LOGGER = Logger.getGlobal();

    private Log() {}

    private static java.util.logging.Level getLevel(Level level)
    {
        switch (level)
        {
            case FAIL: return java.util.logging.Level.SEVERE;
            case WARN: return java.util.logging.Level.WARNING;
            default: return java.util.logging.Level.INFO;
        }
    }

    public static void log(Level level, String msg, Throwable cause)
    {
        LOGGER.log(getLevel(level), msg, cause);
    }

    public static void info(String msg)
    {
        log(Level.INFO, msg, null);
    }

    public static void warn(String msg)
    {
        warn(msg, null);
    }

    public static void warn(String msg, Throwable cause)
    {
        log(Level.WARN, msg, cause);
    }

    public static void fail(String msg, Throwable cause)
    {
        log(Level.FAIL, msg, cause);
    }
}
