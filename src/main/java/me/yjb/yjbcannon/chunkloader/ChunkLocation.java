package me.yjb.yjbcannon.chunkloader;

import java.util.UUID;

public class ChunkLocation
{
    private final int x;
    private final int z;
    private final UUID uuid;

    public ChunkLocation(int x, int z, UUID uuid)
    {
        this.x = x;
        this.z = z;
        this.uuid = uuid;
    }

    public int getX() { return this.x; }
    public int getZ() { return this.z; }
    public UUID getUuid() { return this.uuid; }

    @Override
    public boolean equals(Object object)
    {
        if (object instanceof ChunkLocation)
        {
            ChunkLocation other = (ChunkLocation) object;
            return this.x == other.x && this.z == other.z;
        }
        return false;
    }
}
