package me.yjb.yjbcannon.watercannon;

import me.yjb.yjbcannon.YJBCannon;
import me.yjb.yjbcannon.struct.YJBCommand;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.material.Dispenser;
import org.bukkit.material.Stairs;

/**
 * For QPs, after the block is a dispenser, write a method to check on the left or right of it to see if a dispenser is next to it
 * If the block directly to the left or right of it is not a dispenser, then it checks in 2 locations if there is any kind of block that
 * would prevent water from flowing out (in front of where the water will be dropped and diagonal left/right depending on which side is open)
 * If the side is indeed open, then it will not drop water
 */

public class WaterCannon extends YJBCommand
{
    private YJBCannon core;

    public WaterCannon(YJBCannon core) { this.core = core; }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        if (command.getName().equalsIgnoreCase("water") && sender instanceof Player)
        {
            Player p = (Player) sender;

            if (p.hasPermission("yjbcannon.water.use") && core.getPlotPerm(p))
            {
                Location l = p.getLocation();
                World w = l.getWorld();
                int x = l.getBlockX();
                int y = l.getBlockY();
                int z = l.getBlockZ();
                int r = core.getConfig().getInt("water.water-radius");

                for (int i = -r; i < r; i++)
                {
                    for (int j = -r; j < r; j++)
                    {
                        for (int k = -r; k < r; k++)
                        {
                            Block b = w.getBlockAt(x + i, y + k, z + j);
                            if (Material.DISPENSER == b.getType())
                            {
                                k += waterDispenserGroup(b.getLocation(), getDispenserDirection(b));
                            }
                            else if (b.getState().getData() instanceof Stairs)
                            {
                                Block blockAbove = w.getBlockAt(x + i, y + k + 1, z + j);
                                if (Material.AIR == blockAbove.getType()) blockAbove.setType(Material.WATER);
                            }
                        }
                    }
                }
                p.sendMessage(core.getPrefix() + core.getConfig().getString("lang.water-watered-cannon"));

                super.onCommand(sender, command, "Water", args);
            }
            else
            {
                p.sendMessage(core.getPrefix() + core.getNoPerms());
            }
        }
        return true;
    }
    
    private int waterDispenserGroup(Location l, BlockFace directionGroup)
    {
        World w = l.getWorld();
        int x = l.getBlockX();
        int y = l.getBlockY();
        int z = l.getBlockZ();

        int delta = 0;

        if (BlockFace.UP == directionGroup)
        {
            Block blockAbove = w.getBlockAt(x, y + 1, z);
            if (Material.AIR == blockAbove.getType()) blockAbove.setType(Material.WATER);
            return delta;
        }
        else if (BlockFace.DOWN == directionGroup)
        {
            Block blockAbove = w.getBlockAt(x, y - 1, z);
            if (Material.AIR == blockAbove.getType()) blockAbove.setType(Material.WATER);
            return delta;
        }

        int[] vec = getDispenseVector(directionGroup);

        if (Material.AIR != w.getBlockAt(x + vec[0], y, z + vec[1]).getType()) return delta;

        int allowableDelta = delta;
        boolean isWaterAllowed = isBlockInFront(w, x, y, z, vec) && areSidesSafe(w, x, y + delta, z, vec) || isBlockBelowDispenser(w, x, y, z, vec);

        while (isDispenserAboveIdentical(w.getBlockAt(x, y + delta + 1, z), directionGroup, vec))
        {
            delta++;
            if (isBlockInFront(w, x, y + delta, z, vec) && areSidesSafe(w, x, y + delta, z, vec))
            {
                allowableDelta = delta;
                isWaterAllowed = true;
            }
        }

        if (isWaterAllowed) w.getBlockAt(x + vec[0], y + allowableDelta, z + vec[1]).setType(Material.WATER);

        return allowableDelta;
    }

    private boolean areSidesSafe(World w, int x, int y, int z, int[] vec)
    {
        int[] leftVector = getLeftVector(vec);
        int[] rightVector = getRightVector(vec);

        Material left = w.getBlockAt(x + leftVector[0], y, z + leftVector[1]).getType();
        Material right = w.getBlockAt(x + rightVector[0], y, z + rightVector[1]).getType();

        Material diagLeft = w.getBlockAt(x + leftVector[0] + vec[0], y, z + leftVector[1] + vec[1]).getType();
        Material diagRight = w.getBlockAt(x + rightVector[0] + vec[0], y, z + rightVector[1] + vec[1]).getType();

        return !(Material.AIR == left && Material.AIR == diagLeft || Material.AIR == right && Material.AIR == diagRight);
    }

    private boolean isBlockBelowDispenser(World w, int x, int y, int z, int[] vec)
    {
        Material materialBelow = w.getBlockAt(x + vec[0], y - 1, z + vec[1]).getType();
        return Material.AIR != materialBelow && Material.WATER != materialBelow;
    }

    private boolean isBlockInFront(World w, int x, int y, int z, int[] vec)
    {
        return Material.AIR != w.getBlockAt(x + vec[0] * 2, y, z + vec[1] * 2).getType();
    }

    private boolean isDispenserAboveIdentical(Block dispenserAbove, BlockFace groupFace, int[] vec)
    {
        if (Material.DISPENSER == dispenserAbove.getType())
        {
            BlockFace aboveFace = getDispenserDirection(dispenserAbove);

            if (aboveFace == groupFace)
            {
                Material adjacentType = dispenserAbove.getLocation().clone().add(vec[0], 0, vec[1]).getBlock().getType();
                return (Material.AIR == adjacentType || Material.WATER == adjacentType);
            }
        }
        if (Material.AIR != dispenserAbove.getType() && Material.REDSTONE_WIRE != dispenserAbove.getType())
        {
            Material adjacentType = dispenserAbove.getLocation().clone().add(vec[0], 0, vec[1]).getBlock().getType();
            return (Material.AIR == adjacentType || Material.WATER == adjacentType);
        }
        return false;
    }

    private int[] getRightVector(int[] vec)
    {
        if (vec[0] == 0) // N or S facing
        {
            return new int[] { vec[1], vec[0] };
        }
        return new int[] { vec[1], -vec[0] };
    }

    private int[] getLeftVector(int[] vec)
    {
        if (vec[0] == 0) // N or S facing
        {
            return new int[] { -vec[1], vec[0] };
        }
        return new int[] { vec[1], vec[0] };
    }

    private int[] getDispenseVector(BlockFace face)
    {
        switch(face)
        {
            case NORTH:
                return new int[] {0, -1};
            case SOUTH:
                return new int[] {0, 1};
            case EAST:
                return new int[] {1, 0};
            case WEST:
                return new int[] {-1, 0};
        }
        return null;
    }

    private BlockFace getDispenserDirection(Block dispenser)
    {
        Dispenser disp = (Dispenser) dispenser.getState().getData();

        return disp.getFacing();
    }
}