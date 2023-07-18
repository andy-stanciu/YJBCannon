package me.yjb.yjbcannon.nodetickcounter;

import me.yjb.yjbcannon.YJBCannon;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.scheduler.BukkitScheduler;

public class RedstonePowerEvent implements Listener
{
    private final YJBCannon core;
    private final long ORIGIN_QUICKPULSE = 20;
    private final long TIMEOUT = 160;

    private BukkitScheduler scheduler;
    private Timer timer;

    public RedstonePowerEvent(YJBCannon core) { this.core = core; }

    @EventHandler
    public synchronized void onRedstonePower(BlockRedstoneEvent e)
    {
        if (e.getOldCurrent() == 0)
        {
            if (this.timer == null)
            {
                this.scheduler = Bukkit.getServer().getScheduler();
                this.timer = new Timer();
                this.scheduler.scheduleSyncRepeatingTask(this.core, this.timer, 1L, 1L);
            }

            long current = this.timer.getGameticks();

            Object[] nodeInformation = this.core.getNodeInformationByLocation(e.getBlock().getLocation());
            if (nodeInformation != null)
            {
                if (nodeInformation[0] instanceof Node)
                {
                    Node node = (Node) nodeInformation[0];
                    TrackedNodeArray trackedNodeArray = (TrackedNodeArray) nodeInformation[1];

                    long headActivated = trackedNodeArray.getNodeArray().getHeadObject().getActivated();
                    if (headActivated == -1 || current - headActivated > TIMEOUT) return;

                    node.setActivated(current);

                    Player p = Bukkit.getServer().getPlayer(trackedNodeArray.getUuid());
                    if (p != null)
                    {
                        double gt = current - headActivated;
                        String message = core.getConfig().getString("lang.nodetickcounter-node-powered");
                        message = message.replace("<node-name>", core.getThemeColor().toString() + node.getName() + ChatColor.WHITE);
                        message = message.replace("<gt>", core.getThemeColor().toString() + (int) gt + ChatColor.WHITE);
                        message = message.replace("<rt>", core.getThemeColor().toString() + (gt / 2) + ChatColor.WHITE);
                        message = message.replace("<sec>", core.getThemeColor().toString() + (gt / 20) + ChatColor.WHITE);
                        p.sendMessage(core.getPrefix() + message);
                    }
                }
                else
                {
                    Head head = (Head) nodeInformation[0];

                    if (current - head.getActivated() > ORIGIN_QUICKPULSE) head.setActivated(current);
                }
            }
        }
    }
}
