package me.yjb.yjbcannon.regenwalls;

import org.bukkit.Location;

import java.util.UUID;

public class RegenLocation
{
    private Location location;
    private UUID uuid;
    private RegenWallType type;

    public RegenLocation(Location location, UUID uuid, RegenWallType type)
    {
        this.location = location;
        this.uuid = uuid;
        this.type = type;
    }

    public Location getLocation() { return this.location; }
    public UUID getUuid() { return this.uuid; }
    public RegenWallType getType() { return this.type; }
}
