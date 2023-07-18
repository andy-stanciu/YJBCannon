package me.yjb.yjbcannon.chunkloader;

import me.clip.placeholderapi.PlaceholderAPI;
import me.yjb.yjbcannon.YJBCannon;
import me.yjb.yjbcannon.struct.YJBCommand;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ListChunkLoader extends YJBCommand implements Listener
{
    private final YJBCannon core;
    private final ChunkLoader host;
    private final String title;

    private FileConfiguration cache;
    private Material chunkLoaderType;

    public ListChunkLoader(YJBCannon core, ChunkLoader host)
    {
        this.core = core;
        this.host = host;
        this.title = core.getAccentColor() + ChatColor.BOLD.toString() + "Your Chunk Loaders";
        this.chunkLoaderType = host.getChunkLoaderType();
        this.cache = ChunkLoaderCache.get();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        if (command.getName().equals("chunkloaders") && sender instanceof Player)
        {
            Player p = (Player) sender;

            if (p.hasPermission("yjbcannon.chunkloader.use"))
            {
                this.cache = ChunkLoaderCache.get();
                if (this.cache.getConfigurationSection(p.getUniqueId().toString()) != null)
                {
                    displayList(p);
                }
                else
                {
                    p.sendMessage(core.getPrefix() + ChatColor.RED +
                            core.getConfig().getConfigurationSection("lang")
                                    .getString("chunkloader-not-placed"));
                }

                super.onCommand(sender, command, "List Chunk Loaders", args);
            }
            else
            {
                p.sendMessage(core.getPrefix() + core.getNoPerms());
            }
        }
        return true;
    }

    private void displayList(Player p)
    {
        Inventory list = Bukkit.createInventory(p, 18, this.title);

        ItemStack info = new ItemStack(Material.BOOK, 1);
        ItemMeta infoMeta = info.getItemMeta();
        infoMeta.setDisplayName(core.getThemeColor() + ChatColor.BOLD.toString() + "Information");
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GRAY + "Chunk Loaders:");
        lore.add(core.getThemeColor() + PlaceholderAPI.setPlaceholders(p, "%yjbcannon_chunkloader_count%") +
                ChatColor.GRAY + "/16");
        infoMeta.setLore(lore);
        info.setItemMeta(infoMeta);

        ItemStack clear = new ItemStack(Material.BARRIER, 1);
        ItemMeta clearMeta = clear.getItemMeta();
        clearMeta.setDisplayName(ChatColor.DARK_RED + ChatColor.BOLD.toString() + "Clear");
        List<String> clearLore = new ArrayList<>();
        clearLore.add(ChatColor.GRAY + "Clears your chunk loaders.");
        clearMeta.setLore(clearLore);
        clear.setItemMeta(clearMeta);

        list.setItem(0, info);
        list.setItem(9, clear);

        list = addChunkLoaders(p.getUniqueId(), list);
        p.openInventory(list);
    }

    private Inventory addChunkLoaders(UUID uuid, Inventory list)
    {
        List<String> locations = this.cache.getConfigurationSection(uuid.toString())
                .getStringList("locations");

        if (locations.size() > 8)
        {
            List<String> locations1 = locations.subList(0, 8);
            List<String> locations2 = locations.subList(8, Math.min(locations.size(), 16));

            for (int i = 0; i < locations1.size(); i++)
            {
                ItemStack beacon = new ItemStack(Material.BEACON, 1);
                ItemMeta meta = beacon.getItemMeta();
                meta.setDisplayName(core.getAccentColor() + "Chunk Loader " + (i + 1));
                List<String> beaconLore = new ArrayList<>();
                String[] coords = locations.get(i).split(",");
                beaconLore.add(ChatColor.GRAY + "X: " + core.getThemeColor() + coords[0]);
                beaconLore.add(ChatColor.GRAY + "Y: " + core.getThemeColor() + coords[1]);
                beaconLore.add(ChatColor.GRAY + "Z: " + core.getThemeColor() + coords[2]);
                meta.setLore(beaconLore);
                beacon.setItemMeta(meta);

                list.setItem(i + 1, beacon);
            }

            for (int i = 0; i < locations2.size(); i++)
            {
                ItemStack beacon = new ItemStack(Material.BEACON, 1);
                ItemMeta meta = beacon.getItemMeta();
                meta.setDisplayName(core.getAccentColor() + "Chunk Loader " + (i + 9));
                List<String> beaconLore = new ArrayList<>();
                String[] coords = locations.get(i + 8).split(",");
                beaconLore.add(ChatColor.GRAY + "X: " + core.getThemeColor() + coords[0]);
                beaconLore.add(ChatColor.GRAY + "Y: " + core.getThemeColor() + coords[1]);
                beaconLore.add(ChatColor.GRAY + "Z: " + core.getThemeColor() + coords[2]);
                meta.setLore(beaconLore);
                beacon.setItemMeta(meta);

                list.setItem(i + 10, beacon);
            }

            return list;
        }

        for (int i = 0; i < locations.size(); i++)
        {
            ItemStack beacon = new ItemStack(Material.BEACON, 1);
            ItemMeta meta = beacon.getItemMeta();
            meta.setDisplayName(core.getAccentColor() + "Chunk Loader " + (i + 1));
            List<String> beaconLore = new ArrayList<>();
            String[] coords = locations.get(i).split(",");
            beaconLore.add(ChatColor.GRAY + "X: " + core.getThemeColor() + coords[0]);
            beaconLore.add(ChatColor.GRAY + "Y: " + core.getThemeColor() + coords[1]);
            beaconLore.add(ChatColor.GRAY + "Z: " + core.getThemeColor() + coords[2]);
            meta.setLore(beaconLore);
            beacon.setItemMeta(meta);

            list.setItem(i + 1, beacon);
        }
        return list;
    }

    @EventHandler
    public void clickEvent(InventoryClickEvent e)
    {
        Inventory inv = e.getClickedInventory();
        String title = null;


        if (inv != null)
        {
            title = inv.getTitle();
        }

        if (title != null)
        {
            if (title.equals(this.title))
            {
                e.setCancelled(true);

                try
                {
                    if (Material.BARRIER == e.getCurrentItem().getType())
                    {
                        Player p = (Player) e.getWhoClicked();
                        Bukkit.dispatchCommand(p, "chunkloaderclear");
                        displayList(p);
                    }
                } catch (Exception ignored) {}
            }
        }
    }
}