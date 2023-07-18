package me.yjb.yjbcannon.voidblock;

import me.clip.placeholderapi.PlaceholderAPI;
import me.yjb.yjbcannon.YJBCannon;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.scheduler.BukkitTask;

import java.util.UUID;

public class VoidBlock implements Listener
{
    private YJBCannon core;
    private BukkitTask entityRemover;
    private int voidLimit;
    private Material voidBlock;

    public VoidBlock(YJBCannon core)
    {
        this.core = core;
        this.voidLimit = core.getConfig().getInt("voidblock.void-block-limit");
        this.voidBlock = Material.getMaterial(core.getConfig().getString("voidblock.void-block"));
    }

    public Material getVoidBlock() { return this.voidBlock; }

    @EventHandler
    public void onPlaceVoidBlock(BlockPlaceEvent e)
    {
        Player p = e.getPlayer();

        if (p.hasPermission("yjbcannon.voidblock.use") && core.getPlotPerm(p))
        {
            Block b = e.getBlock();
            

            if (this.voidBlock == b.getType())
            {
                UUID uuid = p.getUniqueId();

                int blockCount = this.core.getPlayerVoidBlocks().getOrDefault(uuid, 0);

                if (blockCount < this.voidLimit)
                {
                    initVoidBlock(uuid, blockCount, b, p);
                }
                else if (p.hasPermission("yjbcannon.voidblock.bypass"))
                {
                    initVoidBlock(uuid, blockCount, b, p);
                }
                else
                {
                    //Send message saying they have reached max blocks
                    String message = PlaceholderAPI.setPlaceholders(p, core.getConfig().getString("lang.voidblock-reached-limit"));
                    p.sendMessage(core.getPrefix() + ChatColor.RED + message);

                    e.setCancelled(true);
                }
            }
        }
    }

    private void initVoidBlock(UUID uuid, int blockCount, Block b, Player p)
    {
        Snowball center = (Snowball) b.getWorld().spawnEntity(b.getLocation().clone().add(0.5, 0.5, 0.5), EntityType.SNOWBALL);
        this.core.addVoidBlock(b.getLocation(), uuid, blockCount, center);

        String message = PlaceholderAPI.setPlaceholders(p, core.getConfig().getString("lang.voidblock-place"));
        p.sendMessage(core.getPrefix() + message);

        if (this.entityRemover == null)
        {
            this.entityRemover = new EntityRemover(this.core, this).runTaskTimer(this.core, 1L, 1L);
        }
        if (this.core.getVoidBlocks().isEmpty())
        {
            this.entityRemover.cancel();
        }
    }
}
