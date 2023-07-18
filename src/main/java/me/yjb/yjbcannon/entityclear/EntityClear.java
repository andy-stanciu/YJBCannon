package me.yjb.yjbcannon.entityclear;

import me.yjb.yjbcannon.YJBCannon;
import me.yjb.yjbcannon.struct.YJBCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.List;

public class EntityClear extends YJBCommand
{
    private final YJBCannon core;

    public EntityClear(YJBCannon core) { this.core =core; }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args)
    {
        if (command.getName().equalsIgnoreCase("clearentities") && sender instanceof Player)
        {
            Player p = (Player) sender;

            if (p.hasPermission("yjbcannon.clearentities.use") && core.getPlotPerm(p))
            {
                int radius = core.getConfig().getInt("clearentities.clear-radius");

                List<Entity> nearby = p.getNearbyEntities(radius, radius, radius);

                int removedCount = 0;
                for (Entity e : nearby)
                {
                    if (EntityType.PLAYER != e.getType())
                    {
                        e.remove();
                        removedCount++;
                    }
                }

                p.sendMessage(core.getPrefix() + core.getConfig().getString("lang.cleared-entities").replace("<count>",
                        core.getThemeColor().toString() + removedCount + ChatColor.WHITE.toString()).replace("<radius>",
                        core.getThemeColor().toString() + radius + ChatColor.WHITE));

                super.onCommand(sender, command, "Clear Entities", args);
            }
            else
            {
                p.sendMessage(core.getPrefix() + core.getNoPerms());
            }
        }
        return true;
    }
}
