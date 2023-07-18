package me.yjb.yjbcannon.magicsand;

import me.clip.placeholderapi.PlaceholderAPI;
import me.yjb.yjbcannon.YJBCannon;
import me.yjb.yjbcannon.struct.YJBCommand;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.UUID;

public class RegisterMagicBlock extends YJBCommand
{
    private final YJBCannon core;
    private final MagicSand host;

    private ArrayList<TrackedLocation> magicBlocks;
    private Material magicBlock;

    public RegisterMagicBlock(YJBCannon core, MagicSand host)
    {
        this.core = core;
        this.host = host;
        this.magicBlock = Material.getMaterial(core.getConfig().getConfigurationSection("magicsand")
                .getString("magic-block"));
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        if (command.getName().equalsIgnoreCase("refill") && sender instanceof Player)
        {
            Player subject = (Player) sender;

            if (subject.hasPermission("yjbcannon.magicsand.register") && core.getPlotPerm(subject))
            {
                int blockCount = registerMagicBlocks(subject);

                if (blockCount == -1)
                {
                    subject.sendMessage(core.getPrefix() + ChatColor.RED +
                            core.getConfig().getConfigurationSection("lang")
                                    .getString("magicsand-found-no-blocks"));
                }
                else
                {
                    if (blockCount == 0 && !subject.hasPermission("yjbcannon.magicsand.bypass"))
                    {
                        initSandSpawner();

                        String message = PlaceholderAPI.setPlaceholders(subject,
                                core.getConfig().getConfigurationSection("lang")
                                .getString("magicsand-reached-limit"));
                        subject.sendMessage(core.getPrefix() + ChatColor.RED + message);
                    }
                    else
                    {
                        core.setNBlocksRegistered(subject.getUniqueId(), blockCount);
                        initSandSpawner();

                        String message = PlaceholderAPI.setPlaceholders(subject,
                                core.getConfig().getConfigurationSection("lang")
                                        .getString("magicsand-registered-blocks"));
                        subject.sendMessage(core.getPrefix() + message);
                    }
                }

                super.onCommand(sender, command, "Refill Magic Sand", args);
            }
            else
            {
                subject.sendMessage(core.getPrefix() + core.getNoPerms());
            }
        }
        return true;
    }

    @SuppressWarnings("deprecation")
    private int registerMagicBlocks(Player p)
    {
        this.magicBlocks = this.host.getMagicBlocks();

        int nAdded = 0;
        boolean foundBlocks = false;

        UUID uuid = p.getUniqueId();
        Location l = p.getLocation();
        World w = p.getWorld();
        int x = l.getBlockX();
        int y = l.getBlockY();
        int z = l.getBlockZ();
        int r = core.getConfig().getConfigurationSection("magicsand").getInt("register-radius");

        for (int i = -r; i < r; i++)
        {
            for (int j = -r; j < r; j++)
            {
                for (int k = -r; k < r; k++)
                {
                    if (this.host.getPlayerBlocks().getOrDefault(uuid, 0) < this.host.getMagicLimit())
                    {
                        Block b = w.getBlockAt(x + i, y + j, z + k);
                        if (this.magicBlock == b.getType() || Material.SANDSTONE == b.getType() || Material.RED_SANDSTONE == b.getType() || Material.IRON_BLOCK == b.getType()
                                || (Material.STONE == b.getType() && b.getData() == (byte)5))
                        {
                            boolean foundLoc = false;
                            Location loc = b.getLocation();

                            for (TrackedLocation tl : this.magicBlocks)
                            {
                                if (tl.getLocation().equals(loc))
                                {
                                    foundLoc = true;
                                    break;
                                }
                            }

                            if (!foundLoc)
                            {
                                char type = this.host.getType(b);
                                if (type == 'u') type = this.host.checkForType(loc);

                                this.host.getMagicBlocks().add(new TrackedLocation(loc, uuid, type));
                                this.host.incrementPlayerBlocks(uuid);
                                b.setType(this.magicBlock);
                                nAdded++;
                                foundBlocks = true;
                            }
                        }
                    }
                    else if (p.hasPermission("yjbcannon.magicsand.bypass"))
                    {
                        Block b = w.getBlockAt(x + i, y + j, z + k);
                        if (this.magicBlock == b.getType() || Material.SANDSTONE == b.getType() || Material.RED_SANDSTONE == b.getType() || Material.IRON_BLOCK == b.getType()
                                || (Material.STONE == b.getType() && b.getData() == (byte)5))
                        {
                            boolean foundLoc = false;
                            Location loc = b.getLocation();

                            for (TrackedLocation tl : this.magicBlocks)
                            {
                                if (tl.getLocation().equals(loc))
                                {
                                    foundLoc = true;
                                    break;
                                }
                            }

                            if (!foundLoc)
                            {
                                char type = this.host.getType(b);
                                if (type == 'u') type = this.host.checkForType(loc);

                                this.host.getMagicBlocks().add(new TrackedLocation(loc, uuid, type));
                                this.host.incrementPlayerBlocks(uuid);
                                b.setType(this.magicBlock);
                                nAdded++;
                                foundBlocks = true;
                            }
                        }
                    }
                    else
                    {
                        foundBlocks = true; //not really true, but makes it work
                        break;
                    }
                }
            }
        }
        if (!foundBlocks) return -1;
        return nAdded;
    }

    private void initSandSpawner()
    {
        BukkitTask sandSpawner = this.host.getSandSpawner();

        if (sandSpawner == null)
        {
            double speed = this.core.getConfig().getConfigurationSection("magicsand")
                    .getDouble("speed");
            long delay = (long) (speed * 20);

            sandSpawner = new SandSpawner(this.host).runTaskTimer(this.core, delay, delay);
        }
        if (this.magicBlocks.isEmpty())
        {
            sandSpawner.cancel();
        }
    }
}