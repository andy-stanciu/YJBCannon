package me.yjb.yjbcannon.chunkloader;

import me.yjb.yjbcannon.YJBCannon;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.ArrayList;
import java.util.UUID;

public class ChunkLoaderUpdater implements Listener
{
    private final YJBCannon core;
    private final ChunkLoader host;

    public ChunkLoaderUpdater(YJBCannon core, ChunkLoader host)
    {
        this.core = core;
        this.host = host;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e)
    {
        updateChunksLoaded(e.getPlayer().getUniqueId(), e.getPlayer().getWorld(), true);
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent e)
    {
        updateChunksLoaded(e.getPlayer().getUniqueId(), e.getPlayer().getWorld(), false);
    }

    private void updateChunksLoaded(UUID uuid, World w, boolean add)
    {
        ConfigurationSection section = ChunkLoaderCache.get()
                .getConfigurationSection(uuid.toString());

        if (section != null)
        {
            ArrayList<String> chunks = (ArrayList<String>) section.getStringList("chunks");

            for (String s : chunks)
            {
                String[] chunk = s.split(",");
                int x = Integer.parseInt(chunk[0]);
                int z = Integer.parseInt(chunk[1]);

                if (add)
                {
                    this.core.addLoadedChunk(new ChunkLocation(x, z, uuid));
                }
                else
                {
                    this.core.removeLoadedChunk(new ChunkLocation(x, z, uuid));
                    this.host.filterLocations(section, section.getStringList("locations"),
                            section.getStringList("chunks"), w);
                }
            }
        }
    }
}
