package me.yjb.yjbcannon.remotefire;

import me.yjb.yjbcannon.YJBCannon;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.UUID;

public class PowerEvent implements Listener
{
    private final YJBCannon core;

    public PowerEvent(YJBCannon core)
    {
        this.core = core;
    }

    @EventHandler
    public void onPower(PlayerInteractEvent e)
    {
        Player p = e.getPlayer();
        Block b = e.getClickedBlock();

        if (core.getPlotPerm(p))
        {
            if (e.getAction() == Action.RIGHT_CLICK_BLOCK)
            {
                UUID uuid = p.getUniqueId();
                Location l = b.getLocation();

                if (Material.STONE_BUTTON == b.getType())
                {
                    this.core.addButtonLocation(uuid, b.getLocation());
                }
                else if (Material.LEVER == b.getType())
                {
                    this.core.addLeverLocation(uuid, b.getLocation());
                }
            }
        }
    }
}
