package net.nyhm.protonet.util;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A ThreadFactory that names its threads (with optional count and priority).
 */
public class NamedThreadFactory implements ThreadFactory
{
    private final AtomicInteger mNext = new AtomicInteger(0);

    private final String mName;
    private final int mPriority;
    private final boolean mCount;

    /**
     * Create threads all of the given name.
     */
    public NamedThreadFactory(String name)
    {
        this(name, false, Thread.NORM_PRIORITY);
    }

    /**
     * Create threads with the given name, optionally appending an incrementing count.
     */
    public NamedThreadFactory(String name, boolean count)
    {
        this(name, count, Thread.NORM_PRIORITY);
    }

    /**
     * Create threads with the specified priority and with the given name,
     * optionally appending an incrementing count.
     */
    public NamedThreadFactory(String name, boolean count, int priority)
    {
        mName = name;
        mPriority = priority;
        mCount = count;
    }

    /**
     * This method produces a new thread.
     */
    @Override
    public Thread newThread(Runnable r)
    {
        Thread t = new Thread(r, nextName());
        t.setPriority(mPriority);
        return t;
    }

    /**
     * Produce a name for the next thread to be created. Incrementing the count is thread safe (atomic).
     * Notice however that newThread is not synchronized, so the ordering of threads as they are created
     * cannot be guaranteed.
     */
    private String nextName()
    {
        return mCount ? mName + "-" + mNext.getAndIncrement() : mName;
    }
}
