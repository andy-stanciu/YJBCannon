package me.yjb.yjbcannon.util;

import me.clip.placeholderapi.PlaceholderAPI;
import me.yjb.yjbcannon.YJBCannon;
import me.yjb.yjbcannon.struct.YJBCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.List;

public class Features extends YJBCommand
{
    private YJBCannon core;
    private ConfigurationSection section;

    public Features(YJBCannon core)
    {
        this.core = core;
        this.section = core.getConfig().getConfigurationSection("lang");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        if (command.getName().equalsIgnoreCase("features") && sender instanceof Player)
        {
            Player subject = (Player) sender;

            if (subject.hasPermission("yjbcannon.features"))
            {
                StringBuilder message = new StringBuilder();
                List<String> features = this.section.getStringList("features-list");
                ConfigurationSection section = this.core.getConfig().getConfigurationSection("lang");

                message.append(this.core.getLine());
                message.append("\n" + core.getThemeColor() + ChatColor.BOLD.toString() + section.getString("chat-prefix1") +
                        " " + ChatColor.WHITE + ChatColor.BOLD.toString() + section.getString("chat-prefix2") + " " +
                        ChatColor.WHITE + core.getVersion() + " " + core.getThemeColor() + ChatColor.ITALIC.toString() + "by yJb");

                for (String s : features)
                {
                    message.append("\n").append(PlaceholderAPI.setPlaceholders(subject, s));
                }
                message.append("\n" + this.core.getLine());

                subject.sendMessage(message.toString());

                super.onCommand(sender, command, "Features", args);
            }
            else
            {
                subject.sendMessage(core.getPrefix() + core.getNoPerms());
            }
        }
        return true;
    }
}
