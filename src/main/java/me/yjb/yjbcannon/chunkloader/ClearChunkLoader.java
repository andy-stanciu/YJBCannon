package me.yjb.yjbcannon.chunkloader;

import me.clip.placeholderapi.PlaceholderAPI;
import me.yjb.yjbcannon.YJBCannon;
import me.yjb.yjbcannon.struct.YJBCommand;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public class ClearChunkLoader extends YJBCommand
{
    private final YJBCannon core;
    private FileConfiguration cache;

    public ClearChunkLoader(YJBCannon core)
    {
        this.core = core;
        this.cache = ChunkLoaderCache.get();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        if (command.getName().equals("chunkloaderclear") && sender instanceof Player)
        {
            Player p = (Player) sender;

            if (p.hasPermission("yjbcannon.chunkloader.clear"))
            {
                this.cache = ChunkLoaderCache.get();
                UUID uuid = p.getUniqueId();

                if (this.cache.getConfigurationSection(uuid.toString()) != null)
                {
                    String message = PlaceholderAPI.setPlaceholders(p, core.getConfig()
                            .getConfigurationSection("lang").getString("chunkloader-clear"));
                    p.sendMessage(core.getPrefix() + message);

                    List<String> locations = this.cache.getConfigurationSection(uuid.toString())
                            .getStringList("locations");
                    World world = Bukkit.getServer().getWorld(this.cache
                            .getConfigurationSection(uuid.toString()).getString("world"));

                    for (String s : locations)
                    {
                        String[] coords = s.split(",");
                        int x = Integer.parseInt(coords[0]);
                        int y = Integer.parseInt(coords[1]);
                        int z = Integer.parseInt(coords[2]);

                        world.getBlockAt(x, y, z).setType(Material.AIR);
                    }

                    this.cache.createSection(uuid.toString());
                    ChunkLoaderCache.save();

                    //removes all of their loaded chunks
                    this.core.removeLoadedChunk(uuid);
                }
                else
                {
                    p.sendMessage(core.getPrefix() + ChatColor.RED +
                            core.getConfig().getConfigurationSection("lang")
                                    .getString("chunkloader-not-placed"));
                }

                super.onCommand(sender, command, "Clear Chunk Loaders", args);
            }
            else
            {
                p.sendMessage(core.getPrefix() + core.getNoPerms());
            }
        }
        return true;
    }
}
