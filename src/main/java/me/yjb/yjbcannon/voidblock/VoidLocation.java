package me.yjb.yjbcannon.voidblock;

import org.bukkit.Location;
import org.bukkit.entity.Snowball;

import java.util.UUID;

public class VoidLocation
{
    private Location location;
    private UUID uuid;
    private Snowball center;

    public VoidLocation(Location location, UUID uuid, Snowball center)
    {
        this.location = location;
        this.uuid = uuid;
        this.center = center;
    }

    public Location getLocation() { return this.location; }
    public UUID getUuid() { return this.uuid; }
    public Snowball getCenter() { return this.center; }
}
