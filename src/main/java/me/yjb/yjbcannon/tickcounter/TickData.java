package me.yjb.yjbcannon.tickcounter;

public class TickData
{
    private double totalTicks;
    private double addedTicks;
    private boolean hasPriority;

    public TickData(double totalTicks, double addedTicks, boolean hasPriority)
    {
        this.totalTicks = totalTicks;
        this.addedTicks = addedTicks;
        this.hasPriority = hasPriority;
    }

    public double getAddedTicks()
    {
        return addedTicks;
    }

    public double getTotalTicks()
    {
        return totalTicks;
    }

    public boolean getPriority()
    {
        return hasPriority;
    }

    public void setPriority(boolean hasPriority)
    {
        this.hasPriority = hasPriority;
    }

    public void setAddedTicks(double addedTicks)
    {
        this.addedTicks = addedTicks;
    }

    public void setTotalTicks(double totalTicks)
    {
        this.totalTicks = totalTicks;
    }
}
