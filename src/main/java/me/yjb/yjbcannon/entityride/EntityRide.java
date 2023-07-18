package me.yjb.yjbcannon.entityride;

import me.yjb.yjbcannon.YJBCannon;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;

public class EntityRide implements Listener
{
    private final YJBCannon core;

    public EntityRide(YJBCannon core) { this.core = core; }

    @EventHandler
    public void onRide(PlayerInteractEntityEvent e)
    {
        Player p = e.getPlayer();
        if (!p.hasPermission("yjbcannon.entityride.use")) return;

        ItemStack item = e.getPlayer().getItemInHand();
        if (item == null) return;

        if (item.getType() != Material.SADDLE) return;

        Entity entity = e.getRightClicked();
        if (entity.getType() != EntityType.PRIMED_TNT && entity.getType() != EntityType.FALLING_BLOCK) return;

        entity.setPassenger(p);
    }
}
