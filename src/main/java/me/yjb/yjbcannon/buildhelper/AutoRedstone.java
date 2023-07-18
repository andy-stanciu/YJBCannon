package me.yjb.yjbcannon.buildhelper;

import me.yjb.yjbcannon.YJBCannon;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.material.Step;

public class AutoRedstone implements Listener
{
    private YJBCannon core;

    public AutoRedstone(YJBCannon core) { this.core = core; }

    @EventHandler
    public void onPlaceSlabOrGlowstone(BlockPlaceEvent e)
    {
        if (this.core.getPlayersWithBuildHelper().contains(e.getPlayer().getUniqueId()))
        {
            Block b = e.getBlock();
            World w = b.getWorld();

            if (Material.STEP == b.getType())
            {
                BlockState state = b.getState();
                Step s = (Step) state.getData();
                s.setInverted(true);
                state.update(true);

                Block above = w.getBlockAt(b.getX(), b.getY() + 1, b.getZ());
                if (Material.AIR == above.getType()) above.setType(Material.REDSTONE_WIRE);
            }
            else if (Material.GLOWSTONE == b.getType())
            {
                Block above = w.getBlockAt(b.getX(), b.getY() + 1, b.getZ());
                if (Material.AIR == above.getType()) above.setType(Material.REDSTONE_WIRE);
            }
        }
    }
}
