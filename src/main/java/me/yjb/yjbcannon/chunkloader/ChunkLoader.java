package me.yjb.yjbcannon.chunkloader;

import me.clip.placeholderapi.PlaceholderAPI;
import me.yjb.yjbcannon.YJBCannon;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ChunkLoader implements Listener
{
    private final YJBCannon core;
    private FileConfiguration cache;
    private Material chunkLoaderType;

    public ChunkLoader(YJBCannon core)
    {
        this.core = core;
        this.cache = ChunkLoaderCache.get();
        this.chunkLoaderType = Material.getMaterial(core.getConfig()
                .getConfigurationSection("chunkloader").getString("chunk-loader"));
    }

    public FileConfiguration getCache()
    {
        if (this.cache == null) this.cache = ChunkLoaderCache.get();
        return this.cache;
    }

    public Material getChunkLoaderType() { return this.chunkLoaderType; }

    @EventHandler
    public void onChunkLoaderPlace(BlockPlaceEvent e)
    {
        if (this.chunkLoaderType == e.getBlock().getType())
        {
            Player p = e.getPlayer();

            if (p.hasPermission("yjbcannon.chunkloader.use") && core.getPlotPerm(p))
            {
                this.cache = ChunkLoaderCache.get();
                String uuid = p.getUniqueId().toString();

                if (this.cache.getConfigurationSection(uuid) == null) this.cache.createSection(uuid);

                ConfigurationSection section = this.cache.getConfigurationSection(uuid);

                setOrDefault(section, "name", p.getName());
                setOrDefault(section, "world", p.getWorld().getName());

                //needed in case they world-edited away the chunk loaders or got rid of them without breaking them
                filterLocations(section, section.getStringList("locations"),
                        section.getStringList("chunks"), p.getWorld());

                ArrayList<String> chunks = (ArrayList<String>) section.getStringList("chunks");

                if (chunks.size() < core.getConfig().getConfigurationSection("chunkloader")
                        .getInt("chunk-loader-limit") || p.hasPermission("yjbcannon.chunkloader.bypass"))
                {
                    Location l = e.getBlock().getLocation();
                    Chunk c = l.getChunk();
                    int x = c.getX();
                    int z = c.getZ();

                    if (!isChunkLoaded(x, z))
                    {
                        ArrayList<String> locations = (ArrayList<String>) section.getStringList("locations");

                        chunks.add("" + x + "," + z);
                        locations.add("" + l.getBlockX() + "," + l.getBlockY() + "," + l.getBlockZ());

                        setOrDefault(section, "chunks", chunks);
                        setOrDefault(section, "locations", locations);
                        ChunkLoaderCache.save();

                        //add the chunk to the loaded chunks
                        core.addLoadedChunk(new ChunkLocation(e.getBlock().getChunk().getX(),
                                e.getBlock().getChunk().getZ(), p.getUniqueId()));

                        String message = PlaceholderAPI.setPlaceholders(p, core.getConfig()
                                .getConfigurationSection("lang").getString("chunkloader-place"));
                        message = message.replace("<chunkloader-radius>", core.getThemeColor().toString() +
                                core.getConfig().getInt("chunkloader.chunk-loader-radius") + ChatColor.WHITE);
                        p.sendMessage(core.getPrefix() + message);
                    }
                    else
                    {
                        e.setCancelled(true);
                        p.sendMessage(core.getPrefix() + ChatColor.RED + core.getConfig()
                                .getConfigurationSection("lang").getString("chunkloader-exists"));
                        ChunkLoaderCache.save();
                    }
                }
                else
                {
                    e.setCancelled(true);
                    String message = PlaceholderAPI.setPlaceholders(p, core.getConfig()
                            .getConfigurationSection("lang").getString("chunkloader-reached-limit"));
                    p.sendMessage(core.getPrefix() + ChatColor.RED + message);
                    ChunkLoaderCache.save();
                }

            }
        }
    }

    @EventHandler
    public void onChunkLoaderBreak(BlockBreakEvent e)
    {
        if (this.chunkLoaderType == e.getBlock().getType())
        {
            Player p = e.getPlayer();
            Location l = e.getBlock().getLocation();
            Chunk c = e.getBlock().getChunk();

            if (p.hasPermission("yjbcannon.chunkloader.use") && core.getPlotPerm(p))
            {
                int x = c.getX();
                int z = c.getZ();
                UUID uuid = core.removeLoadedChunk(x, z); //removes the loaded chunk

                if (uuid != null)
                {
                    this.cache = ChunkLoaderCache.get();
                    ConfigurationSection section = this.cache.getConfigurationSection(uuid.toString());

                    ArrayList<String> chunks = (ArrayList<String>) section.getStringList("chunks");
                    ArrayList<String> locations = (ArrayList<String>) section.getStringList("locations");

                    chunks.remove("" + x + "," + z);
                    locations.remove("" + l.getBlockX() + "," + l.getBlockY() + "," + l.getBlockZ());

                    setOrDefault(section, "chunks", chunks);
                    setOrDefault(section, "locations", locations);
                    ChunkLoaderCache.save();
                }
            }
        }
    }

    private void setOrDefault(ConfigurationSection section, String path, Object value)
    {
        if (section.get(path) == null)
        {
            section.addDefault(path, value);
        }
        else
        {
            section.set(path, value);
        }
    }

    private boolean isChunkLoaded(int x, int z)
    {
        int radius = core.getConfig().getInt("chunkloader.chunk-loader-radius");

        for (ChunkLocation cl : core.getLoadedChunks())
        {
            if (Math.abs(cl.getX() - x) <= radius && Math.abs(cl.getZ() - z) <= radius)
            {
                return true;
            }
        }
        return false;
    }

    public void filterLocations(ConfigurationSection section, List<String> locations, List<String> chunks, World w)
    {
        ArrayList<String> nullLocations = new ArrayList<>();
        String[] strings;
        int x;
        int y;
        int z;
        int chunkX;
        int chunkZ;
        Block b;

        for (String s : locations)
        {
            strings = s.split(",");
            x = Integer.parseInt(strings[0]);
            y = Integer.parseInt(strings[1]);
            z = Integer.parseInt(strings[2]);

            b = w.getBlockAt(x, y, z);
            chunkX = b.getChunk().getX();
            chunkZ = b.getChunk().getZ();

            if (Material.BEACON != b.getType())
            {
                nullLocations.add(s);
                chunks.remove("" + chunkX + "," + chunkZ);
                core.removeLoadedChunk(chunkX, chunkZ);
            }
        }
        locations.removeAll(nullLocations);

        setOrDefault(section, "chunks", chunks);
        setOrDefault(section, "locations", locations);
        ChunkLoaderCache.save();
    }
}