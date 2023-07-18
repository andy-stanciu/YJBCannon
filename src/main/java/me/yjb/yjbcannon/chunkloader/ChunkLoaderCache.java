package me.yjb.yjbcannon.chunkloader;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class ChunkLoaderCache
{
    private static File file;
    private static FileConfiguration cache;

    public static void setup()
    {
        file = new File(Bukkit.getServer().getPluginManager().getPlugin("YJBCannon").getDataFolder(), "chunkloader-cache.yml");

        if (!file.exists())
        {
            try
            {
                file.createNewFile();
            }
            catch (IOException e)
            {
                System.out.println("YJBCannon failed to create file chunkloader-cache.yml");
            }
        }
        cache = YamlConfiguration.loadConfiguration(file);
    }

    public static FileConfiguration get() { return cache; }

    public static void save()
    {
        try
        {
            cache.save(file);
        }
        catch (IOException e)
        {
            System.out.println("YJBCannon failed to save chunkloader-cache.yml");
        }
    }

    public static void reload() { cache = YamlConfiguration.loadConfiguration(file); }
}
