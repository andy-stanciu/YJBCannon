package me.yjb.yjbcannon.protectionblocks;

import me.yjb.yjbcannon.YJBCannon;
import org.bukkit.Material;
import org.bukkit.block.Dispenser;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class DisableDispenserDrops implements Listener
{
    private final YJBCannon core;

    public DisableDispenserDrops(YJBCannon core) { this.core = core; }

    @EventHandler
    public void onBreakDispenser(BlockBreakEvent e)
    {
        if (Material.DISPENSER == e.getBlock().getType() && core.getPlotPerm(e.getPlayer()))
        {
            Dispenser dispenser = (Dispenser) e.getBlock().getState();
            dispenser.getInventory().clear();
            dispenser.update(true);
        }
    }
}
