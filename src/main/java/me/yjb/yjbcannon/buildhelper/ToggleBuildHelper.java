package me.yjb.yjbcannon.buildhelper;

import me.clip.placeholderapi.PlaceholderAPI;
import me.yjb.yjbcannon.YJBCannon;
import me.yjb.yjbcannon.struct.YJBCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class ToggleBuildHelper extends YJBCommand
{
    private YJBCannon core;

    public ToggleBuildHelper(YJBCannon core) { this.core = core; }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        if (command.getName().equalsIgnoreCase("buildhelper") && sender instanceof Player)
        {
            Player p = (Player) sender;

            if (p.hasPermission("yjbcannon.buildhelper.use") && core.getPlotPerm(p))
            {
                UUID uuid = p.getUniqueId();
                if (core.getPlayersWithBuildHelper().contains(uuid))
                {
                    core.removePlayerWithBuildHelper(uuid);
                    sendMessage(p);
                }
                else
                {
                    core.addPlayerWithBuildHelper(uuid);
                    sendMessage(p);
                }
                super.onCommand(sender, command, "Build Helper", args);
            }
            else
            {
                p.sendMessage(core.getPrefix() + core.getNoPerms());
            }
        }
        return true;
    }

    private void sendMessage(Player p)
    {
        String message = PlaceholderAPI.setPlaceholders(p, core.getConfig().getString("lang.buildhelper-toggle"));
        p.sendMessage(core.getPrefix() + message);
    }
}
