package me.yjb.yjbcannon.multidispenser;

import me.yjb.yjbcannon.YJBCannon;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.TNTPrimed;

import java.util.ArrayList;
import java.util.Arrays;

public class MultiExecutor implements Runnable
{
    private final YJBCannon core;
    private final ArrayList<MultiData> tasks;
    private final ArrayList<MultiData> toRemove;

    public long currentTick;

    public MultiExecutor(YJBCannon core)
    {
        this.core = core;
        this.tasks = new ArrayList<>();
        this.toRemove = new ArrayList<>();
    }

    @Override
    public void run()
    {
        currentTick++;

        this.toRemove.clear();

        for (MultiData task : this.tasks)
        {
            if (currentTick - task.getRenderedTick() >= task.getDelay())
            {
                Location location = task.getLocation();
                World world = location.getWorld();
                boolean isTNT = task.isTNT();
                int amount = task.getAmount();
                int fuse = task.getFuse();
                SandType sandType = task.getSandType();

                if (isTNT) spawnEntities(world, location, EntityType.PRIMED_TNT, sandType, amount, fuse);
                else spawnEntities(world, location, EntityType.FALLING_BLOCK, sandType, amount, fuse);

                this.toRemove.add(task);
            }
        }

        this.tasks.removeAll(this.toRemove);
    }

    public void queue(MultiData multiData)
    {
        long thisDelay = multiData.getDelay();
        int thisPriority = multiData.getPriority();

        int addIndex = 0;

        for (int i = 0; i < this.tasks.size(); i++)
        {
            MultiData task = this.tasks.get(i);
            long taskDelay = task.getDelay();
            int taskPriority = task.getPriority();

            if (taskDelay < thisDelay)
            {
                if (i == this.tasks.size() - 1)
                {
                    addIndex = -1;
                    break;
                }

                continue;
            }

            if (taskDelay == thisDelay)
            {
                if (thisPriority < taskPriority)
                {
                    addIndex = i;
                    break;
                }

                if (i == this.tasks.size() - 1)
                {
                    addIndex = -1;
                    break;
                }

                int lookAhead = i + 1;

                while (lookAhead < this.tasks.size())
                {
                    MultiData next = this.tasks.get(lookAhead);

                    if (thisDelay < next.getDelay() || thisPriority < next.getPriority()) break;

                    lookAhead++;
                }

                addIndex = (lookAhead + 1 >= this.tasks.size()) ? -1 : lookAhead + 1;
                break;
            }

            addIndex = i;
            break;
        }

        if (addIndex != -1) this.tasks.add(addIndex, multiData);
        else this.tasks.add(multiData);
    }

    @SuppressWarnings("deprecation")
    private void spawnEntities(World world, Location location, EntityType type, SandType sandType, int amount, int fuse)
    {
        if (EntityType.PRIMED_TNT == type)
        {
            for (int i = 0; i < amount; i++)
            {
                Entity entity = world.spawnEntity(location, type);
                TNTPrimed tnt = (TNTPrimed)entity;
                tnt.setFuseTicks(fuse);
            }
        }
        else
        {
            for (int i = 0; i < amount; i++)
            {
                switch (sandType)
                {
                    case SAND:
                        world.spawnFallingBlock(location, Material.SAND, (byte)0);
                        break;
                    case GRAVEL:
                        world.spawnFallingBlock(location, Material.GRAVEL, (byte)0);
                        break;
                    case RED_SAND:
                        world.spawnFallingBlock(location, Material.SAND, (byte)1);
                        break;
                }
            }
        }
    }
}
