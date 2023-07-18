package me.yjb.yjbcannon.block36;

import me.yjb.yjbcannon.YJBCannon;
import me.yjb.yjbcannon.struct.YJBCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Material;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class Block36 extends YJBCommand implements Listener
{
    private final YJBCannon core;

    public Block36(YJBCannon core) { this.core = core; }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] strings)
    {
        if (command.getName().equalsIgnoreCase("block36") && sender instanceof Player)
        {
            Player p = (Player) sender;

            if (p.hasPermission("yjbcannon.block36.use") && core.getPlotPerm(p))
            {
                giveBlock36(p);
                p.sendMessage(core.getPrefix() + core.getConfig().getString("lang.block36-receive"));
                super.onCommand(sender, command, "Block 36", strings);
            }
            else
            {
                p.sendMessage(core.getPrefix() + core.getNoPerms());
            }
        }
        return true;
    }

    private void giveBlock36(Player p)
    {
        ItemStack piston = new ItemStack(Material.PISTON_BASE, 1);

        ItemMeta meta = piston.getItemMeta();
        meta.setDisplayName(core.getAccentColor() + "Block 36");
        List<String> lore = new ArrayList<>();
        String point = core.getAccentColor() + "» ";
        lore.add(point + ChatColor.WHITE + "Placeable block 36.");
        meta.setLore(lore);

        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        piston.setItemMeta(meta);
        piston.addUnsafeEnchantment(Enchantment.DURABILITY, 1);

        p.setItemInHand(piston);
    }

    @EventHandler
    public void onPlaceBlock36(BlockPlaceEvent e)
    {
        Player p = e.getPlayer();
        if (!p.hasPermission("yjbcannon.block36.use")) return;

        ItemStack inHand = p.getItemInHand();
        if (inHand.getItemMeta().getDisplayName() == null) return;

        if (Material.PISTON_BASE == inHand.getType() && inHand.getItemMeta().getDisplayName().equals(core.getAccentColor() + "Block 36"))
        {
            e.getBlock().setType(Material.PISTON_MOVING_PIECE);
        }
    }
}
