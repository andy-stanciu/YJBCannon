package me.yjb.yjbcannon.nodetickcounter;

import java.util.concurrent.atomic.AtomicLong;

public class Timer implements Runnable
{
    private final AtomicLong gameticks;

    public Timer()
    {
        this.gameticks = new AtomicLong();
    }

    @Override
    public synchronized void run()
    {
        this.gameticks.getAndIncrement();
    }

    public synchronized long getGameticks()
    {
        return this.gameticks.get();
    }
}
