package me.yjb.yjbcannon.nodetickcounter;

import me.yjb.yjbcannon.YJBCannon;
import me.yjb.yjbcannon.struct.YJBCommand;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class NodeTickCounter extends YJBCommand
{
    private final YJBCannon core;

    public NodeTickCounter(YJBCannon core)
    {
        this.core = core;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        if (command.getName().equalsIgnoreCase("nodetickcounter") && sender instanceof Player)
        {
            Player p = (Player) sender;

            if (p.hasPermission("yjbcannon.nodetickcounter.use"))
            {
                p.setItemInHand(getNodeTickCounter());
                p.sendMessage(core.getPrefix() + core.getConfig().getString("lang.nodetickcounter-receive"));
                super.onCommand(sender, command, "Node Tick Counter", args);
            }
            else
            {
                p.sendMessage(core.getPrefix() + core.getNoPerms());
            }
        }
        return true;
    }

    private ItemStack getNodeTickCounter()
    {
        ItemStack nodeTickCounter = new ItemStack(Material.STICK, 1);

        ItemMeta meta = nodeTickCounter.getItemMeta();
        meta.setDisplayName(core.getAccentColor() + "Node Tick Counter");
        List<String> lore = new ArrayList<>();
        String point = core.getAccentColor() + "» ";
        lore.add(point + ChatColor.WHITE + "Right click redstone or a repeater to add/remove nodes!");
        lore.add(point + ChatColor.WHITE + "Sneak while right clicking to set the origin.");
        lore.add(point + ChatColor.WHITE + "Left click to view your nodes.");
        lore.add(point + ChatColor.DARK_GRAY + "Disclaimer: this will not be accurate if your cannon is on auto.");
        meta.setLore(lore);

        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        nodeTickCounter.setItemMeta(meta);
        nodeTickCounter.addUnsafeEnchantment(Enchantment.DURABILITY, 1);

        return nodeTickCounter;
    }
}
