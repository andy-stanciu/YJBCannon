package me.yjb.yjbcannon.tntfill;

import me.clip.placeholderapi.PlaceholderAPI;
import me.yjb.yjbcannon.YJBCannon;
import me.yjb.yjbcannon.struct.YJBCommand;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Dispenser;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

public class TNTClear extends YJBCommand
{
    private YJBCannon core;
    private ItemStack[] empty;

    public TNTClear(YJBCannon core)
    {
        this.core = core;

        this.empty = new ItemStack[9];
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        if (command.getName().equalsIgnoreCase("tntclear") && sender instanceof Player)
        {
            Player subject = (Player) sender;

            if (subject.hasPermission("yjbcannon.tntclear.use") && core.getPlotPerm(subject))
            {
                int clearCount = clearDispensers(subject);
                core.setNDispensersCleared(subject.getUniqueId(), clearCount);
                if (clearCount == -1)
                {
                    subject.sendMessage(core.getPrefix() + ChatColor.RED + core.getConfig()
                            .getConfigurationSection("lang").getString("tntfill-not-found"));
                }
                else
                {
                    String message = PlaceholderAPI.setPlaceholders(subject,
                            core.getConfig().getConfigurationSection("lang")
                                    .getString("tntclear-clear"));

                    subject.sendMessage(core.getPrefix() + message);
                }

                super.onCommand(sender, command, "TNT Clear", args);
            }
            else
            {
                subject.sendMessage(core.getPrefix() + core.getNoPerms());
            }
        }
        return true;
    }

    private int clearDispensers(Player p)
    {
        Location l = p.getLocation();
        World w = p.getWorld();
        int x = l.getBlockX();
        int y = l.getBlockY();
        int z = l.getBlockZ();

        int r = this.core.getConfig().getConfigurationSection("tntclear").getInt("clear-radius");

        boolean foundDispensers = false;
        int clearCount = 0;

        for (int i = -r; i < r; i++)
        {
            for (int j = -r; j < r; j++)
            {
                for (int k = -r; k < r; k++)
                {
                    Block b = w.getBlockAt(x + i, y + j, z + k);
                    if (Material.DISPENSER == b.getType())
                    {
                        Dispenser dispenser = (Dispenser) b.getState();
                        Inventory inv = dispenser.getInventory();

                        if (!Arrays.equals(inv.getContents(), this.empty))
                        {
                            clearCount++;
                            inv.clear();
                            dispenser.update(true);
                        }

                        foundDispensers = true;
                    }
                }
            }
        }
        if (!foundDispensers) return -1;
        return clearCount;
    }
}