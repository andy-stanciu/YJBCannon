package me.yjb.yjbcannon.waterprotect;

import me.yjb.yjbcannon.YJBCannon;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFromToEvent;

import java.util.List;

public class WaterFlowEvent implements Listener
{
    private final YJBCannon core;

    public WaterFlowEvent(YJBCannon core) { this.core = core; }

    @EventHandler
    public void onWaterFlow(BlockFromToEvent e)
    {
        List<String> unbreakable = this.core.getConfig().getConfigurationSection("waterprotect")
                .getStringList("unbreakable");

        String block = e.getToBlock().getType().toString();
        e.setCancelled(unbreakable.contains(block));
    }
}