package me.yjb.yjbcannon.wire;

import com.sk89q.worldedit.regions.Region;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.util.Vector;

public class WireData
{
    private Region selection;
    private World world;
    private Vector placementVector;
    private BlockFace groupDirection;

    public WireData(Region selection, World world, Vector placementVector, BlockFace groupDirection)
    {
        this.selection = selection;
        this.world = world;
        this.placementVector = placementVector;
        this.groupDirection = groupDirection;
    }

    public BlockFace getGroupDirection()
    {
        return groupDirection;
    }

    public Region getSelection()
    {
        return selection;
    }

    public Vector getPlacementVector()
    {
        return placementVector;
    }

    public World getWorld()
    {
        return world;
    }
}
