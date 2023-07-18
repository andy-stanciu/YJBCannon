package me.yjb.yjbcannon.magicsand;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FallingBlock;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;

public class SandSpawner extends BukkitRunnable
{
    private final Vector vector = new Vector(0, -1, 0);

    private MagicSand host;
    private Material magicBlock;
    private ArrayList<TrackedLocation> magicBlocks;
    private ArrayList<TrackedLocation> toRemove;

    public SandSpawner(MagicSand host)
    {
        this.host = host;
        this.magicBlock = host.getMagicBlock();
        this.magicBlocks = this.host.getMagicBlocks();
        this.toRemove = new ArrayList<>();
    }

    @Override
    @SuppressWarnings("deprecation")
    public void run()
    {
        this.magicBlocks = this.host.getMagicBlocks();
        this.toRemove.clear();

        for (TrackedLocation tl : this.magicBlocks)
        {
            Location l = tl.getLocation();
            World w = l.getWorld();

            if (this.magicBlock == l.getBlock().getType())
            {
                Block blockBelow = l.getWorld().getBlockAt(l.getBlockX(), l.getBlockY() - 1, l.getBlockZ());
                Location locBelow = blockBelow.getLocation();

                if (Material.AIR == blockBelow.getType())
                {
                    char type = tl.getType();

                    if (this.host.isSetSandInsteadOfSpawning())
                    {
                        for (int y = locBelow.getBlockY(); y > 0; y--)
                        {
                            Block b = locBelow.getWorld().getBlockAt(l.getBlockX(), y, l.getBlockZ());
                            if (Material.AIR == b.getType())
                            {
                                if (type == 'n') b.setType(Material.SAND);
                                else if (type == 'g') b.setType(Material.GRAVEL);
                                else if (type == 'a') b.setType(Material.ANVIL);
                                else
                                {
                                    b.setType(Material.SAND);
                                    b.setData((byte)1);
                                }
                            }
                            else
                            {
                                break;
                            }
                        }
                    }
                    else
                    {
                        FallingBlock fallingBlock = null;

                        if (type == 'n') fallingBlock = w.spawnFallingBlock(locBelow, Material.SAND, (byte)0);
                        else if (type == 'g') fallingBlock = w.spawnFallingBlock(locBelow, Material.GRAVEL, (byte)0);
                        else if (type == 'a') fallingBlock = w.spawnFallingBlock(locBelow, Material.ANVIL, (byte)0);
                        else
                        {
                            fallingBlock = w.spawnFallingBlock(locBelow, Material.SAND, (byte)1);
                        }

                        fallingBlock.setVelocity(this.vector);
                    }
                }
            }
            else
            {
                this.toRemove.add(tl);
            }
        }

        for (TrackedLocation tl : this.toRemove)
        {
            this.host.removeMagicBlock(tl);
            this.host.decrementPlayerBlocks(tl.getUuid());
        }
    }
}