package me.yjb.yjbcannon.protectionblocks;

import me.yjb.yjbcannon.YJBCannon;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Dispenser;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;

public class ProtectEvent implements Listener
{
    private Material protBlock;
    private YJBCannon core;
    private Material unbreakableBlock;
    private Material regenBlock;

    public ProtectEvent(YJBCannon core)
    {
        this.core = core;
        this.protBlock = Material.getMaterial(core.getConfig()
                .getConfigurationSection("protectionblocks").getString("prot-block"));
        this.unbreakableBlock = Material.getMaterial(core.getConfig()
                .getConfigurationSection("protectionblocks").getString("unbreakable-block"));
        this.regenBlock = Material.getMaterial(core.getConfig().getString("regenwall.type"));
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onTNTExplode(EntityExplodeEvent e)
    {
        World world = e.getLocation().getWorld();

        boolean isCancelled = !core.getConfig().getStringList("tnt-enabled-worlds").contains(world.getName());
        e.setCancelled(isCancelled);

        if (this.core.getConfig().getBoolean("toggles.enable-unbreakable-blocks")
                && !this.core.getConfig().getBoolean("toggles.enable-protection-blocks")
                && !isCancelled)
        {
            for (int i = 0; i < e.blockList().size(); i++)
            {
                Block b = e.blockList().get(i);
                Location l = b.getLocation();
                int x = l.getBlockX();
                int z = l.getBlockZ();

                if (this.unbreakableBlock == b.getType() || this.regenBlock == b.getType())
                {
                    e.blockList().remove(i);
                    i--;
                }
            }
        }

        if (this.core.getConfig().getBoolean("toggles.enable-protection-blocks") && !isCancelled)
        {
            for (int i = 0; i < e.blockList().size(); i++)
            {
                Block b = e.blockList().get(i);
                Location l = b.getLocation();
                int x = l.getBlockX();
                int z = l.getBlockZ();
                boolean isAllowed = true;

                if (this.protBlock == b.getType() || this.unbreakableBlock == b.getType() || this.regenBlock == b.getType())
                {
                    e.blockList().remove(i);
                    i--;
                    isAllowed = false;
                }
                else
                {
                    for (int j = 0; j < 256; j++)
                    {
                        if (this.protBlock == world.getBlockAt(x, j, z).getType())
                        {
                            e.blockList().remove(i);
                            i--;
                            isAllowed = false;
                            break;
                        }
                    }
                }

                if (isAllowed && Material.DISPENSER == b.getType())
                {
                    Dispenser dispenser = (Dispenser) b.getState();
                    dispenser.getInventory().clear();
                    dispenser.update(true);
                }
            }
        }
    }
}