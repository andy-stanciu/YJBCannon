package me.yjb.yjbcannon.voidblock;

import me.clip.placeholderapi.PlaceholderAPI;
import me.yjb.yjbcannon.YJBCannon;
import me.yjb.yjbcannon.struct.YJBCommand;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class ClearVoidBlock extends YJBCommand
{
    private YJBCannon core;

    public ClearVoidBlock(YJBCannon core) { this.core = core; }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        if (command.getName().equalsIgnoreCase("voidclear") && sender instanceof Player)
        {
            Player p = (Player) sender;
            if (p.hasPermission("yjbcannon.voidblock.clear"))
            {
                UUID uuid = p.getUniqueId();

                int removedCount = 0;

                for (VoidLocation vl : this.core.getVoidBlocks())
                {
                    if (vl.getUuid().equals(uuid))
                    {
                        vl.getLocation().getBlock().setType(Material.AIR);
                        removedCount++;
                    }
                }

                this.core.setNVoidBlocksRemoved(uuid, removedCount);
                String message = PlaceholderAPI.setPlaceholders(p, this.core.getConfig().getString("lang.voidblock-clear"));
                p.sendMessage(core.getPrefix() + message);

                super.onCommand(sender, command, "Clear Void Blocks", args);
            }
            else
            {
                p.sendMessage(core.getPrefix() + core.getNoPerms());
            }
        }
        return true;
    }
}
