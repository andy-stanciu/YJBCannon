package me.yjb.yjbcannon.stackremover;

import me.yjb.yjbcannon.YJBCannon;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class RemoveEvent implements Listener
{
    private final YJBCannon core;

    public RemoveEvent(YJBCannon core) { this.core = core; }

    @EventHandler
    @SuppressWarnings("deprecation")
    public void onRemove(PlayerInteractEvent e)
    {
        Player subject = e.getPlayer();
        ItemStack itemInHand = subject.getItemInHand();

        if (!subject.hasPermission("yjbcannon.stackremover.use")) return;
        if (!core.getPlotPerm(subject)) return;

        if (e.getAction() == Action.LEFT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_AIR) return;

        if (itemInHand != null)
        {
            if (Material.BONE == itemInHand.getType())
            {
                Block target = e.getClickedBlock();
                e.setCancelled(true);

                if (e.getAction() == Action.LEFT_CLICK_BLOCK)
                {
                    if (Material.SAND == target.getType() || Material.GRAVEL == target.getType() || Material.ANVIL == target.getType())
                    {
                        Location l = target.getLocation();

                        for (int i = 1; i <= 256; i++)
                        {
                            Block block = subject.getWorld().getBlockAt(l.getBlockX(), i, l.getBlockZ());

                            if (Material.SAND == block.getType() || Material.GRAVEL == block.getType() || Material.ANVIL == block.getType())
                            {
                                block.setType(Material.AIR);
                            }
                        }

                        subject.sendMessage(core.getPrefix() + core.getConfig().getString("lang.stackremover-remove"));
                    }
                }
                else if (e.getAction() == Action.RIGHT_CLICK_BLOCK)
                {
                    Material type = target.getType();
                    byte data = target.getData();
                    Location l = target.getLocation();

                    for (int i = 1; i <= 254; i++)
                    {
                        Block block = subject.getWorld().getBlockAt(l.getBlockX(), i, l.getBlockZ());

                        if (block.getType() == Material.AIR || block.getType() == Material.WATER || block.getType() == Material.STATIONARY_WATER)
                        {
                            block.setType(type);
                            block.setData(data);
                        }
                    }

                    subject.sendMessage(core.getPrefix() + core.getConfig().getString("lang.stackremover-heal"));
                }
            }
        }
    }
}
