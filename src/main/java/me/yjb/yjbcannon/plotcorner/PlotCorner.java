package me.yjb.yjbcannon.plotcorner;

import com.intellectualcrafters.plot.api.PlotAPI;
import com.intellectualcrafters.plot.object.Plot;
import me.yjb.yjbcannon.YJBCannon;
import me.yjb.yjbcannon.struct.YJBCommand;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class PlotCorner extends YJBCommand
{
    private final YJBCannon core;
    private final PlotAPI plotAPI;

    public PlotCorner(YJBCannon core)
    {
        this.core = core;
        this.plotAPI = new PlotAPI();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args)
    {
        if (command.getName().equalsIgnoreCase("corner") && sender instanceof Player)
        {
            Player p = (Player) sender;

            if (p.hasPermission("yjbcannon.corner.use") && core.getPlotPerm(p))
            {
                ArrayList<Plot> playerPlots = new ArrayList<>(this.plotAPI.getPlayerPlots(p));

                if (playerPlots.isEmpty())
                {
                    p.sendMessage(core.getPrefix() + ChatColor.RED + "You have not claimed a plot yet.");
                    return true;
                }

                if (args.length < 2)
                {
                    sendUsage(p);
                    return true;
                }

                String arg1 = args[0];
                String arg2 = args[1];

                if ((!args[0].equals("-") && !args[0].equals("+")) || (!args[1].equals("-") && !args[1].equals("+")))
                {
                    sendUsage(p);
                    return true;
                }

                int index = 0;

                int offsetX = 4;
                int offsetZ = 4;

                if (arg1.equals("-") && arg2.equals("+"))
                {
                    index = 1;
                    offsetX = 4;
                    offsetZ = -3;
                }
                else if (arg1.equals("+") && arg2.equals("+"))
                {
                    index = 2;
                    offsetX = -2;
                    offsetZ = -2;
                }
                else if (arg1.equals("+") && arg2.equals("-"))
                {
                    index = 3;
                    offsetX = -3;
                    offsetZ = 4;
                }

                Plot plot = playerPlots.get(0);
                p.teleport(toBukkitLocation(plot.getAllCorners().get(index)).add(offsetX, 1, offsetZ));

                super.onCommand(sender, command, "Corner", args);
            }
            else
            {
                p.sendMessage(core.getPrefix() + core.getNoPerms());
            }
        }
        return true;
    }

    private void sendUsage(Player p)
    {
        p.sendMessage(core.getPrefix() + ChatColor.RED + "Usage: /corner <+/-> <+/->");
    }

    private Location toBukkitLocation(com.intellectualcrafters.plot.object.Location location)
    {
        return new Location(Bukkit.getServer().getWorld(location.getWorld()), location.getX(), location.getY(), location.getZ());
    }
}
