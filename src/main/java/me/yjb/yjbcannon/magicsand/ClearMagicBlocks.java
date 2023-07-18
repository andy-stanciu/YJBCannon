package me.yjb.yjbcannon.magicsand;

import me.clip.placeholderapi.PlaceholderAPI;
import me.yjb.yjbcannon.YJBCannon;
import me.yjb.yjbcannon.struct.YJBCommand;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.UUID;

public class ClearMagicBlocks extends YJBCommand
{
    private final YJBCannon core;
    private ArrayList<TrackedLocation> magicBlocks;

    public ClearMagicBlocks(YJBCannon core)
    {
        this.core = core;
        this.magicBlocks = core.getMagicBlocks();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        if (command.getName().equalsIgnoreCase("magicclear") && sender instanceof Player)
        {
            Player p = (Player) sender;
            if (p.hasPermission("yjbcannon.magicsand.clear"))
            {
                UUID uuid = p.getUniqueId();
                this.magicBlocks = core.getMagicBlocks();

                int removedCount = 0;

                for (TrackedLocation tl : this.magicBlocks)
                {
                    if (tl.getUuid().equals(uuid))
                    {
                        removeMagicBlock(tl);
                        removeSandBelow(tl.getLocation());
                        removedCount++;
                    }
                }

                this.core.setNMagicBlocksRemoved(uuid, removedCount);
                String message = PlaceholderAPI.setPlaceholders(p, core.getConfig()
                        .getConfigurationSection("lang").getString("magicsand-removed-blocks"));
                p.sendMessage(core.getPrefix() + message);

                super.onCommand(sender, command, "Clear Magic Sand", args);
            }
            else
            {
                p.sendMessage(core.getPrefix() + core.getNoPerms());
            }
        }
        return true;
    }

    private void removeSandBelow(Location l)
    {
        for (int y = l.getBlockY() - 1; y > 0; y--)
        {
            Block b = l.getWorld().getBlockAt(l.getBlockX(), y, l.getBlockZ());
            if (Material.SAND == b.getType() || Material.GRAVEL == b.getType() || Material.ANVIL == b.getType())
            {
                b.setType(Material.AIR);
            }
            else
            {
                break;
            }
        }
    }

    @SuppressWarnings("deprecation")
    private void removeMagicBlock(TrackedLocation tl)
    {
        Block b = tl.getLocation().getBlock();

        Material replacement = Material.SANDSTONE;
        int data = -1;

        switch(tl.getType())
        {
            case 'r':
                replacement = Material.RED_SANDSTONE;
                break;
            case 'g':
                replacement = Material.STONE;
                data = 5;
                break;
            case 'a':
                replacement = Material.IRON_BLOCK;
                break;
        }

        b.setType(replacement);
        if (data == 5) b.setData((byte)data);
    }
}
