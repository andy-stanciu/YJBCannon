package me.yjb.yjbcannon.multidispenser;

import org.bukkit.Location;

public class MultiData
{
    private Location location;
    private int amount;
    private int priority;
    private int fuse;
    private long delay;
    private long renderedTick;
    private boolean isTNT;
    private SandType sandType;

    public MultiData()
    {
        this.location = null;
        this.amount = 1;
        this.priority = 0;
        this.fuse = 80;
        this.delay = 1;
        this.renderedTick = -1;
        this.isTNT = true;
        this.sandType = SandType.SAND;
    }

    public SandType getSandType()
    {
        return sandType;
    }

    public boolean isTNT()
    {
        return isTNT;
    }

    public int getAmount()
    {
        return amount;
    }

    public long getDelay()
    {
        return delay;
    }

    public long getRenderedTick()
    {
        return renderedTick;
    }

    public int getPriority()
    {
        return priority;
    }

    public Location getLocation()
    {
        return location;
    }

    public int getFuse()
    {
        return fuse;
    }

    public void setAmount(int amount)
    {
        this.amount = amount;
    }

    public void setDelay(long delay)
    {
        this.delay = delay;
    }

    public void setFuse(int fuse)
    {
        this.fuse = fuse;
    }

    public void setLocation(Location location)
    {
        this.location = location;
    }

    public void setPriority(int priority)
    {
        this.priority = priority;
    }

    public void setRenderedTick(long renderedTick)
    {
        this.renderedTick = renderedTick;
    }

    public void setTNT(boolean TNT)
    {
        isTNT = TNT;
    }

    public void setSandType(SandType sandType)
    {
        this.sandType = sandType;
    }

    @Override
    public String toString()
    {
        return "Loc:" + this.location + ",Del:" + this.delay + ",Pr:" + this.priority + ",N:" + this.amount;
    }
}
