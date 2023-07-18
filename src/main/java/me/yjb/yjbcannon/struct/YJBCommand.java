package me.yjb.yjbcannon.struct;

import me.yjb.yjbcannon.YJBCannon;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class YJBCommand implements CommandExecutor
{
    private static String title = "New Command Usage";
    private static String footer = "YJB Development Command Logger by yJb";
    private static String COMMAND_WEBHOOK_URL = "https://discord.com/api/webhooks/972562066374279228/9Nm1C6H4qfkAFuuiX4Ae-cah3Yjz1GQBCXhji_Qv_40F-AC1ehHN4vR4HOVSKhAUVZ75";

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args)
    {
        return true;
    }
}
