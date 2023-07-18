package me.yjb.yjbcannon.wallgenerator;

import me.clip.placeholderapi.PlaceholderAPI;
import me.yjb.yjbcannon.YJBCannon;
import me.yjb.yjbcannon.struct.YJBCommand;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.material.MaterialData;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class WallGenerator extends YJBCommand
{
    private final YJBCannon core;
    private final int WORLD_HEIGHT = 256;

    private HashMap<UUID, Integer[]> wallData;
    private HashMap<UUID, Integer[]> startingPositions;
    private HashMap<UUID, Material[][][]> cache;
    private HashMap<UUID, MaterialData[][][]> metadata;
    private HashMap<UUID, Long> cooldown;

    public WallGenerator(YJBCannon core)
    {
        this.core = core;
        this.wallData = core.getWallData();
        this.startingPositions = core.getStartingPositions();
        this.cache = core.getCache();
        this.metadata = core.getMetadata();
        this.cooldown = core.getWallCooldown();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        if (command.getName().equalsIgnoreCase("walls") && sender instanceof Player)
        {
            Player subject = (Player) sender;

            if (subject.hasPermission("yjbcannon.walls.use") && core.getPlotPerm(subject))
            {
                getPlayerInput(subject, args);
                super.onCommand(sender, command, "Walls", args);
            }
            else
            {
                subject.sendMessage(core.getPrefix() + core.getNoPerms());
            }
        }
        else if (command.getName().equalsIgnoreCase("undowalls") && sender instanceof Player)
        {
            Player subject = (Player) sender;

            if (subject.hasPermission("yjbcannon.walls.undowalls") && core.getPlotPerm(subject))
            {
                World w = subject.getWorld();

                Material[][][] restoration = this.cache.getOrDefault(subject.getUniqueId(), null);
                MaterialData[][][] states = this.metadata.getOrDefault(subject.getUniqueId(), null);
                Integer[] startingPos = this.startingPositions.getOrDefault(subject.getUniqueId(), null);

                //Restore blocks using cache and correct states
                for (int x = 0; x < restoration.length; x++)
                {
                    for (int y = 0; y < restoration[x].length; y++)
                    {
                        for (int z = 0; z < restoration[x][y].length; z++)
                        {
                            Block target;
                            if (startingPos[3] == 1) //facing positive
                            {
                                target = w.getBlockAt(startingPos[0] + x,
                                        startingPos[1] + y, startingPos[2] + z);
                            }
                            else //facing negative
                            {
                                if (startingPos[4] == 0) //facing z
                                {
                                    target = w.getBlockAt(startingPos[0] - x,
                                            startingPos[1] + y, startingPos[2] + z);
                                }
                                else //facing x
                                {
                                    target = w.getBlockAt(startingPos[0] + x,
                                            startingPos[1] + y, startingPos[2] - z);
                                }
                            }
                            target.setType(restoration[x][y][z]);
                            target.getState().setData(states[x][y][z]);
                            target.getState().update();
                        }
                    }
                }
                subject.sendMessage(core.getPrefix() + core.getConfig().getConfigurationSection("lang")
                        .getString("wallgenerator-undo"));

                super.onCommand(sender, command, "Undo Walls", args);
            }
            else
            {
                subject.sendMessage(core.getPrefix() + core.getNoPerms());
            }
        }
        return true;
    }

    private void getPlayerInput(Player subject, String[] args)
    {
        if (args.length == 0)
        {
            subject.sendMessage(core.getPrefix() + ChatColor.RED + core.getConfig().getString("lang.wallgenerator-usage"));
        }
        else
        {
            int nWalls = 1;
            int width = 1;
            Material type = Material.COBBLESTONE;

            try
            {
                nWalls = Integer.parseInt(args[0]);
            }
            catch (NumberFormatException e)
            {
                nWalls = -1;
            }

            if (args.length >= 2)
            {
                try
                {
                    width = Integer.parseInt(args[1]);
                }
                catch (NumberFormatException e)
                {
                    width = -1;
                }
            }

            if (args.length >= 3)
            {
                String s = args[2];
                List<String> allowedMaterials = core.getConfig().getStringList("wallgenerator.allowed-materials");
                for (String material : allowedMaterials)
                {
                    if (material.equalsIgnoreCase(s))
                    {
                        type = Material.valueOf(material);
                        break;
                    }
                }
            }

            if (nWalls <= 0 || width <= 0)
            {
                subject.sendMessage(core.getPrefix() + ChatColor.RED + core.getConfig()
                        .getConfigurationSection("lang").getString("wallgenerator-usage"));
            }
            else if (nWalls > core.getConfig().getConfigurationSection("wallgenerator")
                    .getInt("max-wall-count") && !subject
                    .hasPermission("yjbcannon.walls.bypass.size"))
            {
                subject.sendMessage(core.getPrefix() + ChatColor.RED +
                        core.getConfig().getConfigurationSection("lang")
                        .getString("wallgenerator-wall-limit"));
            }
            else if (width > core.getConfig().getConfigurationSection("wallgenerator")
                    .getInt("max-wall-width") && !subject
                    .hasPermission("yjbcannon.walls.bypass.size"))
            {
                subject.sendMessage(core.getPrefix() + ChatColor.RED +
                        core.getConfig().getConfigurationSection("lang")
                        .getString("wallgenerator-width-limit"));
            }
            else
            {
                if (!subject.hasPermission("yjbcannon.walls.bypass.cooldown"))
                {
                    if (cooldown.containsKey(subject.getUniqueId()))
                    {
                        long secondsLeft = ((cooldown.get(subject.getUniqueId()) / 1000) +
                                core.getConfig().getConfigurationSection("wallgenerator").getInt("cooldown")) -
                                (System.currentTimeMillis() / 1000);
                        if (secondsLeft > 0)
                        {
                            String message = PlaceholderAPI.setPlaceholders(subject,
                                    core.getConfig().getConfigurationSection("lang")
                                            .getString("wallgenerator-on-cooldown"));
                            subject.sendMessage(core.getPrefix() + ChatColor.RED + message);
                        }
                        else
                        {
                            cooldown.put(subject.getUniqueId(), System.currentTimeMillis());
                            doWallGeneration(nWalls, width, type, subject);
                        }
                    }
                    else
                    {
                        cooldown.put(subject.getUniqueId(), System.currentTimeMillis());
                        doWallGeneration(nWalls, width, type, subject);
                    }
                }
                else
                {
                    doWallGeneration(nWalls, width, type, subject);
                }
            }
        }
    }

    private void doWallGeneration(int nWalls, int width, Material type, Player subject)
    {
        if (generateWalls(nWalls, width, type, subject))
        {
            String message = PlaceholderAPI.setPlaceholders(subject,
                    core.getConfig().getConfigurationSection("lang")
                            .getString("wallgenerator-generate"));

            subject.sendMessage(core.getPrefix() + message);
        }
        else
        {
            cooldown.remove(subject.getUniqueId());
            subject.sendMessage(core.getPrefix() + ChatColor.RED + core.getConfig()
                    .getConfigurationSection("lang").getString("wallgenerator-too-close-to-border"));
        }
    }

    private boolean generateWalls(int nWalls, int width, Material type, Player subject)
    {
        Integer[] info = this.wallData.getOrDefault(subject.getUniqueId(), null);

        int wallWidth = (width + width % 2) - 1; //Making width odd

        if (info != null)
        {
            info[0] = nWalls;
            info[1] = wallWidth;
            info[2] = type.ordinal();
            this.wallData.put(subject.getUniqueId(), info);
        }
        else
        {
            this.wallData.put(subject.getUniqueId(), new Integer[]{nWalls, width, type.ordinal()});
        }

        Material[][][] area;
        MaterialData[][][] data;
        World w = subject.getWorld();
        Location l = subject.getLocation();

        int x = l.getBlockX();
        int z = l.getBlockZ();

        double dir = l.getYaw();
        boolean isFacingZ = true;
        int wallGap = 2;

        if ((dir >= 45 && dir <= 135) || (dir >= -135 && dir <= -45) || (dir >= 225 && dir <= 315))
        {
            isFacingZ = false;
        }
        if ((dir >= 45 && dir <= 225) || (dir <= -135 && dir >= -225))
        {
            wallGap = -2;
        }

        boolean foundBorder = false;
        if (core.getConfig().getConfigurationSection("wallgenerator").getBoolean("check-for-border"))
        {
            //Checking for plot border
            if (isFacingZ)
            {
                if (wallGap > 0)
                {
                    for (int i = 0; i < nWalls * 2 + 2; i++)
                    {
                        if (Material.BEDROCK == w.getBlockAt(x, 1, z + i).getType())
                        {
                            foundBorder = true;
                            break;
                        }
                    }
                    if (!foundBorder)
                    {
                        for (int i = (-1 * wallWidth) / 2 - 1; i < wallWidth / 2 + 1; i++)
                        {
                            if (Material.BEDROCK == w.getBlockAt(x + i, 1, z).getType())
                            {
                                foundBorder = true;
                                break;
                            }
                        }
                    }
                }
                else
                {
                    for (int i = 0; i < nWalls * 2 + 2; i++)
                    {
                        if (Material.BEDROCK == w.getBlockAt(x, 1, z - i).getType())
                        {
                            foundBorder = true;
                            break;
                        }
                    }
                    if (!foundBorder)
                    {
                        for (int i = (-1 * wallWidth) / 2 - 1; i < wallWidth / 2 + 1; i++)
                        {
                            if (Material.BEDROCK == w.getBlockAt(x + i, 1, z).getType())
                            {
                                foundBorder = true;
                                break;
                            }
                        }
                    }
                }
            }
            else
            {
                if (wallGap > 0)
                {
                    for (int i = 0; i < nWalls * 2 + 2; i++)
                    {
                        if (Material.BEDROCK == w.getBlockAt(x + i, 1, z).getType())
                        {
                            foundBorder = true;
                            break;
                        }
                    }
                    if (!foundBorder)
                    {
                        for (int i = (-1 * wallWidth) / 2 - 1; i < wallWidth / 2 + 1; i++)
                        {
                            if (Material.BEDROCK == w.getBlockAt(x, 1, z + i).getType())
                            {
                                foundBorder = true;
                                break;
                            }
                        }
                    }
                }
                else
                {
                    for (int i = 0; i < nWalls * 2 + 2; i++)
                    {
                        if (Material.BEDROCK == w.getBlockAt(x - i, 1, z).getType())
                        {
                            foundBorder = true;
                            break;
                        }
                    }
                    if (!foundBorder)
                    {
                        for (int i = (-1 * wallWidth) / 2 - 1; i < wallWidth / 2 + 1; i++)
                        {
                            if (Material.BEDROCK == w.getBlockAt(x, 1, z + i).getType())
                            {
                                foundBorder = true;
                                break;
                            }
                        }
                    }
                }
            }
        }

        if (!foundBorder)
        {
            //Initializing the area displaced by the wall generation
            if (isFacingZ)
            {
                area = new Material[wallWidth][WORLD_HEIGHT - 1][nWalls * 2];
                data = new MaterialData[wallWidth][WORLD_HEIGHT - 1][nWalls * 2];
                z += wallGap;
                x -= wallWidth / 2;
                this.startingPositions.put(subject.getUniqueId(), new Integer[]{x, 1, z - wallGap / 2, wallGap/2, 1});
            }
            else
            {
                area = new Material[nWalls * 2][WORLD_HEIGHT - 1][wallWidth];
                data = new MaterialData[nWalls * 2][WORLD_HEIGHT - 1][wallWidth];
                x += wallGap;
                z -= wallWidth / 2;
                this.startingPositions.put(subject.getUniqueId(), new Integer[]{x - wallGap / 2, 1, z, wallGap / 2, 0});
            }

            for (int i = 0; i < nWalls; i++)
            {
                for (int j = 0; j < wallWidth; j++)
                {
                    for (int y = 1; y < WORLD_HEIGHT - 1; y++)
                    {
                        generateBlock(x, y, z, i, j, w, type, isFacingZ, wallGap, area, data);
                    }

                    generateBlock(x, WORLD_HEIGHT - 1, z, i, j, w, Material.WATER, isFacingZ, wallGap, area, data);

                    if (isFacingZ)
                    {
                        x++;
                    }
                    else
                    {
                        z++;
                    }
                }

                if (isFacingZ)
                {
                    x -= wallWidth;
                    z += wallGap;
                }
                else
                {
                    z -= wallWidth;
                    x += wallGap;
                }
            }
            this.cache.put(subject.getUniqueId(), area);
            this.metadata.put(subject.getUniqueId(), data);

            return true;
        }
        else
        {
            return false;
        }
    }

    private void generateBlock(int x, int y, int z, int wallIndex,
                               int widthIndex, World w, Material type,
                               boolean isFacingZ, int wallGap,
                               Material[][][] area, MaterialData[][][] data)
    {
        Block block = w.getBlockAt(x, y, z);
        Material blockType = block.getType();
        MaterialData blockData = block.getState().getData();
        block.setType(type);

        Block gap;
        Material gapType;
        MaterialData gapData;

        if (isFacingZ)
        {
            gap = w.getBlockAt(x, y, z - wallGap / 2);
            gapType = gap.getType();
            gapData = gap.getState().getData();
            area[widthIndex][y - 1][wallIndex * 2 + 1] = blockType;
            area[widthIndex][y - 1][wallIndex * 2] = gapType;
            data[widthIndex][y - 1][wallIndex * 2 + 1] = blockData;
            data[widthIndex][y - 1][wallIndex * 2] = gapData;
        }
        else
        {
            gap = w.getBlockAt(x - wallGap / 2, y, z);
            gapType = gap.getType();
            gapData = gap.getState().getData();
            area[wallIndex * 2 + 1][y - 1][widthIndex] = blockType;
            area[wallIndex * 2][y - 1][widthIndex] = gapType;
            data[wallIndex * 2 + 1][y - 1][widthIndex] = blockData;
            data[wallIndex * 2][y - 1][widthIndex] = gapData;
        }

        gap.setType(Material.AIR);
    }
}