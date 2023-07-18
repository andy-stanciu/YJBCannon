package me.yjb.yjbcannon.stackremover;

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

public class StackRemover extends YJBCommand
{
    private final YJBCannon core;

    public StackRemover(YJBCannon core)
    {
        this.core = core;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        if (command.getName().equalsIgnoreCase("bone") && sender instanceof Player)
        {
            Player subject = (Player) sender;

            if (subject.hasPermission("yjbcannon.stackremover.use"))
            {
                subject.setItemInHand(getStackRemover());
                subject.sendMessage(core.getPrefix() + core.getConfig().getString("lang.stackremover-receive"));
                super.onCommand(sender, command, "Stack Remover", args);
            }
            else
            {
                subject.sendMessage(core.getPrefix() + core.getNoPerms());
            }
        }
        return true;
    }

    private ItemStack getStackRemover()
    {
        ItemStack stackRemover = new ItemStack(Material.BONE, 1);

        ItemMeta meta = stackRemover.getItemMeta();
        meta.setDisplayName(core.getAccentColor() + "Stack Remover");
        List<String> lore = new ArrayList<>();
        String point = core.getAccentColor() + "» ";
        lore.add(point + ChatColor.WHITE + "Left click to remove " + ChatColor.YELLOW + "sand" + ChatColor.WHITE + ", " + ChatColor.RED + "red sand"
                + ChatColor.WHITE + ", or " + ChatColor.DARK_GRAY + "gravel" + ChatColor.WHITE + "!");
        meta.setLore(lore);

        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        stackRemover.setItemMeta(meta);
        stackRemover.addUnsafeEnchantment(Enchantment.DURABILITY, 1);

        return stackRemover;
    }
}