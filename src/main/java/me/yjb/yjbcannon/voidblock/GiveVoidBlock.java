package me.yjb.yjbcannon.voidblock;

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

public class GiveVoidBlock extends YJBCommand
{
    private YJBCannon core;
    private Material voidBlock;

    public GiveVoidBlock(YJBCannon core)
    {
        this.core = core;
        this.voidBlock = Material.getMaterial(core.getConfig().getString("voidblock.void-block"));
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        if (command.getName().equalsIgnoreCase("voidblock") && sender instanceof Player)
        {
            Player subject = (Player) sender;

            if (subject.hasPermission("yjbcannon.voidblock.use") && core.getPlotPerm(subject))
            {
                ItemStack vb = new ItemStack(this.voidBlock, 1);

                ItemMeta meta = vb.getItemMeta();
                meta.setDisplayName(core.getAccentColor() + "Void Block");
                List<String> lore = new ArrayList<>();
                String point = core.getAccentColor() + "» ";
                lore.add(point + ChatColor.WHITE + "Deletes any colliding TNT or sand!");
                lore.add(point + ChatColor.WHITE + "Clear them with" + core.getThemeColor() + " /vbclear" +
                        ChatColor.WHITE + " or" + core.getThemeColor() + " /vbc");
                meta.setLore(lore);

                meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                vb.setItemMeta(meta);
                vb.addUnsafeEnchantment(Enchantment.DURABILITY, 1);

                subject.setItemInHand(vb);
                subject.sendMessage(core.getPrefix() + core.getConfig().getString("lang.voidblock-receive"));

                super.onCommand(sender, command, "Give Void Block", args);
            }
            else
            {
                subject.sendMessage(core.getPrefix() + core.getNoPerms());
            }
        }
        return true;
    }
}
