package me.yjb.yjbcannon.util;

import me.clip.placeholderapi.PlaceholderAPI;
import me.yjb.yjbcannon.YJBCannon;
import me.yjb.yjbcannon.struct.YJBCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Upload extends YJBCommand
{
    private YJBCannon core;

    public Upload(YJBCannon core) { this.core = core; }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        if (command.getName().equalsIgnoreCase("upload") && sender instanceof Player)
        {
            Player subject = (Player) sender;

            if (subject.hasPermission("yjbcannon.upload"))
            {
                StringBuilder sb = new StringBuilder();

                sb.append(this.core.getLine());
                sb.append("\n" + PlaceholderAPI.setPlaceholders(subject,
                        this.core.getConfig().getConfigurationSection("lang")
                                .getString("upload-message")));
                sb.append("\n" + this.core.getLine());

                subject.sendMessage(sb.toString());

                super.onCommand(sender, command, "Upload", args);
            }
            else
            {
                subject.sendMessage(core.getPrefix() + core.getNoPerms());
            }
        }
        return true;
    }
}
