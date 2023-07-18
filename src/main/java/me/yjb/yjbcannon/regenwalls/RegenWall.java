package me.yjb.yjbcannon.regenwalls;

import me.clip.placeholderapi.PlaceholderAPI;
import me.yjb.yjbcannon.YJBCannon;
import me.yjb.yjbcannon.struct.YJBCommand;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;

public class RegenWall extends YJBCommand implements Listener
{
    private final YJBCannon core;
    private Material regenBlock;
    private BukkitTask wallRegenerator;

    public RegenWall(YJBCannon core)
    {
        this.core = core;
        this.regenBlock = Material.getMaterial(core.getConfig().getString("regenwall.type"));
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args)
    {
        if (command.getName().equalsIgnoreCase("regenwall") && sender instanceof Player)
        {
            Player p = (Player) sender;

            if (p.hasPermission("yjbcannon.regenwall.use") && core.getPlotPerm(p))
            {
                if (args.length == 0) p.sendMessage(core.getPrefix() + ChatColor.RED + "Usage: /regenwall <obsidian/cobble/sand>");
                else
                {
                    switch (args[0].toLowerCase())
                    {
                        case "obby":
                        case "obsidian":
                            giveRegenWall(p, "Obsidian");
                            break;
                        case "cobble":
                        case "cobblestone":
                            giveRegenWall(p, "Cobblestone");
                            break;
                        case "sand":
                            giveRegenWall(p, "Sand");
                            break;
                        default:
                            p.sendMessage(core.getPrefix() + ChatColor.RED + "Usage: /regenwall <obsidian/cobble/sand>");
                            break;
                    }
                }

                super.onCommand(sender, command, "Regen Wall", args);
            }
            else
            {
                p.sendMessage(core.getPrefix() + core.getNoPerms());
            }
        }
        return true;
    }

    @EventHandler
    public void onRegenWallPlace(BlockPlaceEvent e)
    {
        Player p = e.getPlayer();
        if (!p.hasPermission("yjbcannon.regenwall.use")) return;

        ItemStack inHand = p.getItemInHand();
        if (inHand.getItemMeta().getDisplayName() == null) return;

        String displayName = inHand.getItemMeta().getDisplayName();
        if (inHand.getType() == this.regenBlock && displayName.contains("Regen Wall"))
        {
            if (core.getPlayerRegens().getOrDefault(p.getUniqueId(), 0) >= core.getConfig().getInt("regenwall.regen-wall-limit"))
            {
                String message = PlaceholderAPI.setPlaceholders(p, core.getConfig().getString("lang.regenwall-reached-limit"));
                p.sendMessage(core.getPrefix() + ChatColor.RED + message);

                e.setCancelled(true);

                return;
            }

            String typeStr = displayName.split(":")[1].replace(core.getThemeColor().toString(), "").toLowerCase().trim();

            RegenWallType type = RegenWallType.OBSIDIAN;
            if (typeStr.equals("cobblestone")) type = RegenWallType.COBBLESTONE;
            else if (typeStr.equals("sand")) type = RegenWallType.SAND;

            this.core.getRegenLocations().add(new RegenLocation(e.getBlock().getLocation(), p.getUniqueId(), type));
            this.core.getPlayerRegens().put(p.getUniqueId(), this.core.getPlayerRegens().getOrDefault(p.getUniqueId(), 0) + 1);

            if (this.wallRegenerator == null)
            {
                double speed = this.core.getConfig().getDouble("regenwall.regen-speed");
                long delay = (long) (speed * 20);

                this.wallRegenerator = new WallRegenerator(this.core).runTaskTimer(this.core, delay, delay);
            }
            if (this.core.getRegenLocations().isEmpty())
            {
                this.wallRegenerator.cancel();
            }
        }
    }

    private void giveRegenWall(Player p, String type)
    {
        ItemStack regenWall = new ItemStack(this.regenBlock, 1);

        ItemMeta meta = regenWall.getItemMeta();
        meta.setDisplayName(core.getAccentColor() + "Regen Wall: " + core.getThemeColor() + type);
        List<String> lore = new ArrayList<>();
        String point = core.getAccentColor() + "» ";
        lore.add(point + ChatColor.WHITE + "Creates a wall that regenerates every " + core.getThemeColor() + core.getConfig().getDouble("regenwall.regen-speed") + ChatColor.WHITE + " seconds.");
        meta.setLore(lore);

        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        regenWall.setItemMeta(meta);
        regenWall.addUnsafeEnchantment(Enchantment.DURABILITY, 1);

        p.setItemInHand(regenWall);
        p.sendMessage(core.getPrefix() + core.getConfig().getString("lang.regenwall-receive"));
    }
}
