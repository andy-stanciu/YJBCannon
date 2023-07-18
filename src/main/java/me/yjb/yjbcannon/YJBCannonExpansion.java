package me.yjb.yjbcannon;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.List;

public class YJBCannonExpansion extends PlaceholderExpansion
{
    private YJBCannon core;

    public YJBCannonExpansion(YJBCannon core) { this.core = core; }

    @Override
    public boolean persist() { return true; }

    @Override
    public boolean canRegister() { return true; }

    @Override
    public String getAuthor() { return "yJb"; }

    @Override
    public String getIdentifier() { return "yjbcannon"; }

    @Override
    public String getVersion() { return core.getDescription().getVersion(); }

    @Override
    public String onPlaceholderRequest(Player player, String identifier)
    {
        if (identifier.equals("tickcounter_rt"))
        {
            return "" + ((double)(int)core.getTickData().getOrDefault(player.getUniqueId(), null).getTotalTicks() / 2);
        }
        if (identifier.equals("tickcounter_gt"))
        {
            return "" + (int)core.getTickData().getOrDefault(player.getUniqueId(), null).getTotalTicks();
        }
        if (identifier.equals("tickcounter_seconds"))
        {
            return "" + ((double)(int)(core.getTickData().getOrDefault(player.getUniqueId(), null).getTotalTicks())) / 20;
        }
        if (identifier.equals("tickcounter_delay"))
        {
            int delay = (int)core.getTickData().getOrDefault(player.getUniqueId(), null).getAddedTicks();
            return "" + ((delay > 0) ? "+" : "") + delay;
        }
        if (identifier.equals("tickcounter_priority"))
        {
            return "" + ((core.getTickData().getOrDefault(player.getUniqueId(), null).getPriority()) ? 1 : 0);
        }
        if (identifier.equals("wallgenerator_count"))
        {
            return "" + core.getWallData().getOrDefault(player.getUniqueId(), null)[0];
        }
        if (identifier.equals("wallgenerator_width"))
        {
            return "" + core.getWallData().getOrDefault(player.getUniqueId(), null)[1];
        }
        if (identifier.equals("wallgenerator_type"))
        {
            Integer[] data = core.getWallData().getOrDefault(player.getUniqueId(), null);
            if (data != null)
            {
                return Material.values()[data[2]].name().toLowerCase();
            }
        }
        if (identifier.equals("wallgenerator_plural"))
        {
            Integer[] data = core.getWallData().getOrDefault(player.getUniqueId(), null);
            if (data != null)
            {
                return (data[0] == 1) ? "" : "s";
            }
        }
        if (identifier.equals("wallgenerator_cooldown"))
        {
            return "" + (((core.getWallCooldown().getOrDefault(player.getUniqueId(), null) / 1000) +
                    core.getConfig().getConfigurationSection("wallgenerator").getInt("cooldown")) -
                    (System.currentTimeMillis() / 1000));
        }
        if (identifier.equals("magicsand_limit"))
        {
            return "" + core.getConfig().getInt("magicsand.magic-block-limit");
        }
        if (identifier.equals("magicsand_blocks_registered"))
        {
            return "" + core.getNBlocksRegistered(player.getUniqueId());
        }
        if (identifier.equals("magicsand_blocks_removed"))
        {
            return "" + core.getNMagicBlocksRemoved(player.getUniqueId());
        }
        if (identifier.equals("remotefire_lever_state"))
        {
            return core.getLeverPowered(player.getUniqueId()) ? "&aon" : "&coff";
        }
        if (identifier.equals("remotefire_button_location"))
        {
            Location l = core.getButtonLocations().getOrDefault(player.getUniqueId(), null);
            String ret;

            if (l != null)
            {
                ret = "" + l.getBlockX() + ", " + l.getBlockY() + ", " + l.getBlockZ();
            }
            else
            {
                ret = "None";
            }
            return ret;
        }
        if (identifier.equals("remotefire_lever_location"))
        {
            Location l = core.getLeverLocations().getOrDefault(player.getUniqueId(), null);
            String ret;

            if (l != null)
            {
                ret = "" + l.getBlockX() + ", " + l.getBlockY() + ", " + l.getBlockZ();
            }
            else
            {
                ret = "None";
            }
            return ret;
        }
        if (identifier.equals("tntfill_count"))
        {
            return "" + core.getNDispensersFilled(player.getUniqueId());
        }
        if (identifier.equals("tntclear_count"))
        {
            return "" + core.getNDispensersCleared(player.getUniqueId());
        }
        if (identifier.equals("chunkloader_count"))
        {
            return "" + core.getChunkLoaderCache().getConfigurationSection(player.getUniqueId().toString())
                    .getStringList("locations").size();
        }
        if (identifier.equals("chunkloader_limit"))
        {
            return "" + core.getConfig().getConfigurationSection("chunkloader")
                    .getInt("chunk-loader-limit");
        }
        if (identifier.equals("buildhelper_state"))
        {
            return core.getPlayersWithBuildHelper().contains(player.getUniqueId()) ? "&aenabled" : "&cdisabled";
        }
        if (identifier.equals("voidblock_limit"))
        {
            return "" + core.getConfig().getInt("voidblock.void-block-limit");
        }
        if (identifier.equals("voidblock_count"))
        {
            return "" + core.getNVoidBlocksRegistered(player.getUniqueId());
        }
        if (identifier.equals("voidblock_blocks_removed"))
        {
            return "" + core.getNVoidBlocksRemoved(player.getUniqueId());
        }
        if (identifier.equals("regenwall_limit"))
        {
            return "" + core.getConfig().getInt("regenwall.regen-wall-limit");
        }
        return getIdentifier() + "_" + identifier;
    }
}
