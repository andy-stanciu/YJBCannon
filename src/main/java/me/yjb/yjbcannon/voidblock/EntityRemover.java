package me.yjb.yjbcannon.voidblock;

import me.yjb.yjbcannon.YJBCannon;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;

public class EntityRemover extends BukkitRunnable
{
    private YJBCannon core;
    private VoidBlock host;
    private Material voidBlock;
    private ArrayList<VoidLocation> voidBlocks;
    private ArrayList<VoidLocation> toRemove;

    public EntityRemover(YJBCannon core, VoidBlock host)
    {
        this.core = core;
        this.host = host;
        this.voidBlock = host.getVoidBlock();
        this.voidBlocks = core.getVoidBlocks();
        this.toRemove = new ArrayList<>();
    }

    @Override
    public void run()
    {
        this.voidBlocks = this.core.getVoidBlocks();
        this.toRemove.clear();

        for (VoidLocation vl : this.voidBlocks)
        {
            Location l = vl.getLocation();
            World w = l.getWorld();

            if (this.voidBlock == l.getBlock().getType())
            {
                for (Entity e : vl.getCenter().getNearbyEntities(0.75, 1.0, 0.75))
                {
                    if (EntityType.PRIMED_TNT == e.getType() || EntityType.FALLING_BLOCK == e.getType()) e.remove();
                }
            }
            else
            {
                vl.getCenter().remove();
                this.toRemove.add(vl);
            }
        }

        for (VoidLocation vl : this.toRemove)
        {
            this.core.removeVoidBlock(vl);
        }
    }
}
