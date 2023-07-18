package me.yjb.yjbcannon.magicsand;

import org.bukkit.Location;

import java.util.UUID;

public class TrackedLocation
{
    private Location location;
    private UUID uuid;
    private char type;

    public TrackedLocation(Location location, UUID uuid, char type)
    {
        this.location = location;
        this.uuid = uuid;
        this.type = type;
    }

    public Location getLocation() { return this.location; }
    public UUID getUuid() { return this.uuid; }
    public char getType() { return this.type; }
}
