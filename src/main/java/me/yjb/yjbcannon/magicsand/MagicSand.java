package me.yjb.yjbcannon.magicsand;

import me.clip.placeholderapi.PlaceholderAPI;
import me.yjb.yjbcannon.YJBCannon;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class MagicSand implements Listener
{
    private final YJBCannon core;

    private BukkitTask sandSpawner;
    private ArrayList<TrackedLocation> magicBlocks;
    private HashMap<UUID, Integer> playerBlocks;
    private Material magicBlock;
    private int magicLimit;
    private boolean setSandInsteadOfSpawning;

    public MagicSand(YJBCannon core)
    {
        this.core = core;
        this.magicBlocks = core.getMagicBlocks();
        this.playerBlocks = core.getPlayerBlocks();
        this.magicLimit = core.getConfig().getConfigurationSection("magicsand").getInt("magic-block-limit");
        this.magicBlock = Material.getMaterial(core.getConfig().getConfigurationSection("magicsand")
                .getString("magic-block"));
        this.setSandInsteadOfSpawning = core.getConfig().getBoolean("magicsand.set-blocks-to-sand-instead-of-spawning-sand");
    }

    public ArrayList<TrackedLocation> getMagicBlocks() { return this.magicBlocks; }
    public HashMap<UUID, Integer> getPlayerBlocks() { return this.playerBlocks; }
    public boolean isSetSandInsteadOfSpawning() { return this.setSandInsteadOfSpawning; }
    public void removeMagicBlock(TrackedLocation tl) { this.magicBlocks.remove(tl); }
    public void incrementPlayerBlocks(UUID uuid) { this.playerBlocks.put(uuid, this.playerBlocks.getOrDefault(uuid, 0) + 1); }
    public void decrementPlayerBlocks(UUID uuid) { this.playerBlocks.put(uuid, this.playerBlocks.getOrDefault(uuid, 0) - 1); }
    public Material getMagicBlock() { return this.magicBlock; }
    public int getMagicLimit() { return this.magicLimit; }
    public BukkitTask getSandSpawner() { return this.sandSpawner; }

    @EventHandler
    @SuppressWarnings("deprecation")
    public void onPlaceMagicBlock(BlockPlaceEvent e)
    {
        Player p = e.getPlayer();

        if (p.hasPermission("yjbcannon.magicsand.use") && core.getPlotPerm(p))
        {
            Block b = e.getBlock();

            if (this.magicBlock == b.getType() || Material.SANDSTONE == b.getType() || Material.RED_SANDSTONE == b.getType() || Material.IRON_BLOCK == b.getType()
                || (Material.STONE == b.getType() && b.getData() == (byte)5))
            {
                if (e.getItemInHand().getItemMeta() == null) return;
                if (e.getItemInHand().getItemMeta().getDisplayName() == null) return;
                if (e.getItemInHand().getItemMeta().getDisplayName().contains("Magic Sand"))
                {
                    UUID uuid = p.getUniqueId();

                    char type = getType(b);

                    int blockCount = this.playerBlocks.getOrDefault(uuid, 0);
                    b.setType(this.magicBlock);

                    if (blockCount < this.magicLimit)
                    {
                        initMagicSand(uuid, blockCount, b, type);
                    }
                    else if (p.hasPermission("yjbcannon.magicsand.bypass"))
                    {
                        initMagicSand(uuid, blockCount, b, type);
                    }
                    else
                    {
                        //Send message saying they have reached max blocks
                        String message = PlaceholderAPI.setPlaceholders(p, core.getConfig().getConfigurationSection("lang")
                                .getString("magicsand-reached-limit"));
                        p.sendMessage(core.getPrefix() + ChatColor.RED + message);

                        e.setCancelled(true);
                    }
                }
            }
        }
    }

    private void initMagicSand(UUID uuid, int blockCount, Block b, char type)
    {
        this.playerBlocks.put(uuid, blockCount + 1);

        if (type == 'u') type = checkForType(b.getLocation());
        this.magicBlocks.add(new TrackedLocation(b.getLocation(), uuid, type));

        if (this.sandSpawner == null)
        {
            double speed = this.core.getConfig().getConfigurationSection("magicsand")
                    .getDouble("speed");
            long delay = (long) (speed * 20);

            this.sandSpawner = new SandSpawner(this).runTaskTimer(this.core, delay, delay);
        }
        if (this.magicBlocks.isEmpty())
        {
            this.sandSpawner.cancel();
        }
    }

    public char getType(Block b)
    {
        char type = 'u';
        switch(b.getType())
        {
            case SANDSTONE:
                type = 'n';
                break;
            case RED_SANDSTONE:
                type = 'r';
                break;
            case STONE:
                type = 'g';
                break;
            case IRON_BLOCK:
                type = 'a';
                break;
        }
        return type;
    }

    @SuppressWarnings("deprecation")
    public char checkForType(Location l)
    {
        Block target = null;
        World w = l.getWorld();
        int x = l.getBlockX();
        int z = l.getBlockZ();

        for (int y = l.getBlockY() - 1; y > 0; y--)
        {
            target = w.getBlockAt(x, y, z);
            if (Material.AIR != target.getType())
            {
                if (Material.SAND == target.getType())
                {
                    if (target.getData() == (byte)1) return 'r';
                    else return 'n';
                }
                else if (Material.GRAVEL == target.getType())
                {
                    return 'g';
                }
                else if (Material.ANVIL == target.getType())
                {
                    return 'a';
                }
                return 'n';
            }
        }
        return 'n';
    }
}