package me.yjb.yjbcannon;

import me.yjb.yjbcannon.struct.YJBCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class YJBCannonReload extends YJBCommand
{
    private final YJBCannon core;

    public YJBCannonReload (YJBCannon core) { this.core = core; }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        if (command.getName().equalsIgnoreCase("yjbcannonreload") && sender instanceof Player)
        {
            Player subject = (Player) sender;

            if (subject.hasPermission("yjbcannon.reload"))
            {
                core.reloadConfig();
                core.refreshConfigValues();
                subject.sendMessage(core.getPrefix() + "The config was reloaded! If some changes did not register, reload the plugin via Plugin Manager.");
                super.onCommand(sender, command, "Reload", args);
            }
            else
            {
                subject.sendMessage(core.getPrefix() + core.getNoPerms());
            }
        }
        return true;
    }
}