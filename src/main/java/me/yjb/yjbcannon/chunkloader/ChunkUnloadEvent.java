package me.yjb.yjbcannon.chunkloader;

import me.yjb.yjbcannon.YJBCannon;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class ChunkUnloadEvent implements Listener
{
    private final YJBCannon core;
    private final ChunkLoader host;
    private Material chunkLoaderType;

    public ChunkUnloadEvent(YJBCannon core, ChunkLoader host)
    {
        this.core = core;
        this.host = host;
        this.chunkLoaderType = host.getChunkLoaderType();
    }

    @EventHandler
    public void onChunkUnload(org.bukkit.event.world.ChunkUnloadEvent e)
    {
        boolean foundChunk = false;
        int x = e.getChunk().getX();
        int z = e.getChunk().getZ();
        int radius = core.getConfig().getInt("chunkloader.chunk-loader-radius");

        for (ChunkLocation cl : this.core.getLoadedChunks())
        {
            if (Math.abs(cl.getX() - x) <= radius && Math.abs(cl.getZ() - z) <= radius)
            {
                foundChunk = true;
                break;
            }
        }

        e.setCancelled(foundChunk);
    }
}
