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

public class TNTFill extends YJBCommand
{
    private YJBCannon core;
    private ItemStack[] tnt;

    public TNTFill(YJBCannon core)
    {
        this.core = core;

        this.tnt = new ItemStack[9];
        ItemStack tntStack = new ItemStack(Material.TNT, 64);
        Arrays.fill(this.tnt, tntStack);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        if (command.getName().equalsIgnoreCase("tntfill") && sender instanceof Player)
        {
            Player subject = (Player) sender;

            if (subject.hasPermission("yjbcannon.tntfill.use") && core.getPlotPerm(subject))
            {
                int fillCount = fillDispensers(subject);
                core.setNDispensersFilled(subject.getUniqueId(), fillCount);
                if (fillCount == -1)
                {
                    subject.sendMessage(core.getPrefix() + ChatColor.RED + core.getConfig()
                            .getConfigurationSection("lang").getString("tntfill-not-found"));
                }
                else
                {
                    String message = PlaceholderAPI.setPlaceholders(subject,
                            core.getConfig().getConfigurationSection("lang")
                                    .getString("tntfill-fill"));

                    subject.sendMessage(core.getPrefix() + message);
                }

                super.onCommand(sender, command, "TNT Fill", args);
            }
            else
            {
                subject.sendMessage(core.getPrefix() + core.getNoPerms());
            }
        }
        return true;
    }

    private int fillDispensers(Player p)
    {
        Location l = p.getLocation();
        World w = p.getWorld();
        int x = l.getBlockX();
        int y = l.getBlockY();
        int z = l.getBlockZ();

        int r = this.core.getConfig().getConfigurationSection("tntfill").getInt("fill-radius");

        boolean foundDispensers = false;
        int fillCount = 0;

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

                        if (!Arrays.equals(inv.getContents(), this.tnt))
                        {
                            fillCount++;
                            inv.setContents(this.tnt);
                            dispenser.update(true);
                        }

                        foundDispensers = true;
                    }
                }
            }
        }
        if (!foundDispensers) return -1;
        return fillCount;
    }
}