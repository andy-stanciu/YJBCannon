package me.yjb.yjbcannon.nodetickcounter;

import org.bukkit.Location;

public class Node
{
    private Location location;
    private String name;
    private long activated;

    public Node(Location location, String name)
    {
        this.location = location;
        this.name = name;
        this.activated = -1;
    }

    public String getName() { return this.name; }
    public Location getLocation() { return this.location; }
    public long getActivated() { return this.activated; }

    public void setActivated(long activated) { this.activated = activated; }
}
