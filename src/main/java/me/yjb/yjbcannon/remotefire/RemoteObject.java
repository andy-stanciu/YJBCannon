package me.yjb.yjbcannon.remotefire;

import me.clip.placeholderapi.PlaceholderAPI;
import me.yjb.yjbcannon.YJBCannon;
import me.yjb.yjbcannon.struct.YJBCommand;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.material.Button;
import org.bukkit.material.Lever;

import java.util.HashMap;
import java.util.UUID;

public class RemoteObject extends YJBCommand
{
    private final YJBCannon core;

    private HashMap<UUID, Location> buttonLocations;
    private HashMap<UUID, Location> leverLocations;

    public RemoteObject(YJBCannon core)
    {
        this.core = core;
        this.buttonLocations = core.getButtonLocations();
        this.leverLocations = core.getLeverLocations();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        if (command.getName().equalsIgnoreCase("fire") && sender instanceof Player)
        {
            Player subject = (Player) sender;

            if (subject.hasPermission("yjbcannon.remotefire.button") && core.getPlotPerm(subject))
            {
                UUID uuid = subject.getUniqueId();
                Location l = this.buttonLocations.getOrDefault(uuid, null);

                if (l != null)
                {
                    Block b= l.getBlock();
                    if (Material.STONE_BUTTON == b.getType())
                    {
                        BlockState state = b.getState();
                        Button button = (Button) state.getData();

                        button.setPowered(true);
                        state.setData(button);
                        state.update(true, true);

                        Block nearby = b.getRelative(button.getAttachedFace());
                        Material mat = nearby.getType();

                        nearby.setType(Material.PRISMARINE, true);
                        nearby.setType(mat, true);

                        Bukkit.getServer().getScheduler().runTaskLater(this.core, new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                button.setPowered(false);
                                state.setData(button);
                                state.update(true, true);

                                nearby.setType(Material.PRISMARINE, true);
                                nearby.setType(mat, true);
                            }
                        }, 20L);

                        subject.sendMessage(core.getPrefix() + core.getConfig().getConfigurationSection("lang")
                                .getString("remotefire-button-fire"));
                    }
                    else
                    {
                        this.core.removeButtonLocation(uuid);
                        subject.sendMessage(core.getPrefix() + ChatColor.RED +
                                core.getConfig().getConfigurationSection("lang")
                                .getString("remotefire-button-removed"));
                    }
                }
                else
                {
                    subject.sendMessage(core.getPrefix() + ChatColor.RED +
                            core.getConfig().getConfigurationSection("lang")
                            .getString("remotefire-button-notfound"));
                }

                super.onCommand(sender, command, "Fire", args);
            }
            else
            {
                subject.sendMessage(core.getPrefix() + core.getNoPerms());
            }
        }
        else if (command.getName().equalsIgnoreCase("lever") && sender instanceof Player)
        {
            Player subject = (Player) sender;

            if (subject.hasPermission("yjbcannon.remotefire.lever") && core.getPlotPerm(subject))
            {
                UUID uuid = subject.getUniqueId();
                Location l = this.leverLocations.getOrDefault(uuid, null);

                if (l != null)
                {
                    Block b= l.getBlock();
                    if (Material.LEVER == b.getType())
                    {
                        BlockState state = b.getState();
                        Lever lever = (Lever) state.getData();

                        core.setLeverPowered(uuid, !lever.isPowered());
                        lever.setPowered(!lever.isPowered());
                        state.setData(lever);
                        state.update(true, true);

                        Block nearby = b.getRelative(lever.getAttachedFace());
                        Material mat = nearby.getType();

                        nearby.setType(Material.PRISMARINE, true);
                        nearby.setType(mat, true);

                        String message = PlaceholderAPI.setPlaceholders(subject,
                                core.getConfig().getConfigurationSection("lang")
                                        .getString("remotefire-lever-flick"));
                        subject.sendMessage(core.getPrefix() + message);
                    }
                    else
                    {
                        this.core.removeLeverLocation(uuid);
                        subject.sendMessage(core.getPrefix() + ChatColor.RED +
                                core.getConfig().getConfigurationSection("lang")
                                .getString("remotefire-lever-removed"));
                    }
                }
                else
                {
                    subject.sendMessage(core.getPrefix() + ChatColor.RED +
                            core.getConfig().getConfigurationSection("lang")
                            .getString("remotefire-lever-notfound"));
                }

                super.onCommand(sender, command, "Lever", args);
            }
            else
            {
                subject.sendMessage(core.getPrefix() + core.getNoPerms());
            }
        }
        return true;
    }
}
