package me.yjb.yjbcannon.tickcounter;

import me.clip.placeholderapi.PlaceholderAPI;
import me.yjb.yjbcannon.YJBCannon;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Diode;

import java.util.HashMap;
import java.util.UUID;

public class CountEvent implements Listener
{
    private final YJBCannon core;

    private HashMap<UUID, TickData> data;

    public CountEvent(YJBCannon core)
    {
        this.core = core;
        this.data = core.getTickData();
    }

    @EventHandler
    public void onCount(PlayerInteractEvent e)
    {
        Player subject = e.getPlayer();
        ItemStack itemInHand = subject.getItemInHand();

        if (itemInHand != null)
        {
            if (Material.BLAZE_ROD == itemInHand.getType())
            {
                if (e.getAction() == Action.LEFT_CLICK_AIR)
                {
                    resetCounter(subject);
                }
                else if (e.getAction() == Action.LEFT_CLICK_BLOCK)
                {
                    resetCounter(subject);
                    e.setCancelled(true);
                }
                else
                {
                    Block target = e.getClickedBlock();
                    e.setCancelled(true);

                    if (target != null)
                    {
                        Material type = target.getType();

                        switch (type)
                        {
                            case DIODE_BLOCK_OFF:
                            case DIODE_BLOCK_ON:
                                Diode repeater = (Diode) target.getState().getData();
                                updateTickData(subject, 2 * repeater.getDelay(), false);
                                break;
                            case REDSTONE_COMPARATOR_OFF:
                            case REDSTONE_COMPARATOR_ON:
                                updateTickData(subject, 2, true);
                                break;
                            case REDSTONE_TORCH_OFF:
                            case REDSTONE_TORCH_ON:
                            case PISTON_BASE:
                                updateTickData(subject, 2, false);
                                break;
                            case PISTON_STICKY_BASE:
                                updateTickData(subject, 2.5, false);
                                break;
                        }
                    }
                }
            }
        }
    }

    private void updateTickData(Player p, double delay, boolean hasPriority)
    {
        UUID uuid = p.getUniqueId();
        TickData data = this.data.getOrDefault(uuid, null);

        if (p.isSneaking())
        {
            delay = -delay;
            if (hasPriority) hasPriority = false;
        }

        if (data != null)
        {
            data.setTotalTicks(Math.max(data.getTotalTicks() + delay, 0));
            data.setAddedTicks(delay);
            data.setPriority(hasPriority);
        }
        else
        {
            this.data.put(uuid, new TickData(Math.max(delay, 0), delay, hasPriority));
        }

        String message = PlaceholderAPI.setPlaceholders(p, core.getConfig()
                .getConfigurationSection("lang").getString("tickcounter-increment"));

        p.sendMessage(core.getPrefix() + message);
    }

    private void resetCounter(Player subject)
    {
        this.data.put(subject.getUniqueId(), null);
        subject.sendMessage(core.getPrefix() + "You have reset the tick counter!");
    }
}