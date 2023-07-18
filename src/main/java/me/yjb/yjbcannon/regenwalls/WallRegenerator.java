package me.yjb.yjbcannon.regenwalls;

import me.yjb.yjbcannon.YJBCannon;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;

public class WallRegenerator extends BukkitRunnable
{
    private final YJBCannon core;
    private ArrayList<RegenLocation> toRemove;
    private Material regenBlock;

    public WallRegenerator(YJBCannon core)
    {
        this.core = core;
        this.toRemove = new ArrayList<>();
        this.regenBlock = Material.getMaterial(core.getConfig().getString("regenwall.type"));
    }

    @Override
    public void run()
    {
        this.toRemove.clear();

        for (RegenLocation rl : this.core.getRegenLocations())
        {
            Location l = rl.getLocation();
            World w = l.getWorld();

            if (this.regenBlock == l.getBlock().getType())
            {
                Location locBelow = l.clone().subtract(0, 1, 0);

                for (int y = locBelow.getBlockY(); y > 0; y--)
                {
                    Block b = locBelow.getWorld().getBlockAt(l.getBlockX(), y, l.getBlockZ());
                    if (RegenWallType.OBSIDIAN == rl.getType()) b.setType(Material.OBSIDIAN);
                    else if (RegenWallType.COBBLESTONE == rl.getType()) b.setType(Material.COBBLESTONE);
                    else b.setType(Material.SAND);
                }
            }
            else
            {
                this.toRemove.add(rl);
            }
        }

        for (RegenLocation rl : this.toRemove)
        {
            this.core.getRegenLocations().remove(rl);
            this.core.getPlayerRegens().put(rl.getUuid(), this.core.getPlayerRegens().getOrDefault(rl.getUuid(), 0) - 1);
        }
    }
}
