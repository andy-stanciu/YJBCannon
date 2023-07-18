package me.yjb.yjbcannon.nodetickcounter;

import org.bukkit.Location;

public class Head
{
    private Location location;
    private long activated;

    public Head(Location location)
    {
        this.location = location;
        this.activated = -1;
    }

    public long getActivated() { return this.activated; }
    public Location getLocation() { return this.location; }

    public void setActivated(long activated) { this.activated = activated; }
    public void setLocation(Location location) { this.location = location; }
}
