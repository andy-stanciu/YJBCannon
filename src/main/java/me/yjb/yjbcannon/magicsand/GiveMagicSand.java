package me.yjb.yjbcannon.magicsand;

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

public class GiveMagicSand extends YJBCommand
{
    private YJBCannon core;

    public GiveMagicSand(YJBCannon core)
    {
        this.core = core;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        if (command.getName().equalsIgnoreCase("magicsand") && sender instanceof Player)
        {
            Player subject = (Player) sender;

            if (subject.hasPermission("yjbcannon.magicsand.use") && core.getPlotPerm(subject))
            {
                Material material = Material.SANDSTONE;
                short damage = -1;

                if (args.length != 0)
                {
                    switch(args[0])
                    {
                        case "red":
                        case "redsand":
                            material = Material.RED_SANDSTONE;
                            break;
                        case "gravel":
                            material = Material.STONE;
                            damage = 5;
                            break;
                        case "anvil":
                            material = Material.IRON_BLOCK;
                    }
                }

                ItemStack magicSand;
                if (damage == -1) magicSand = new ItemStack(material, 1);
                else magicSand = new ItemStack(material, 1, damage);

                ItemMeta meta = magicSand.getItemMeta();
                meta.setDisplayName(core.getAccentColor() + "Magic Sand");
                List<String> lore = new ArrayList<>();
                String point = core.getAccentColor() + "» ";
                lore.add(point + ChatColor.WHITE + "Spawns sand underneath itself!");
                lore.add(point + ChatColor.WHITE + "Use /ms red for" + ChatColor.RED + " red sand"
                        + ChatColor.WHITE + ", /ms gravel for" + ChatColor.GRAY + " gravel" + ChatColor.WHITE +
                        ", and /ms anvil for" + ChatColor.DARK_GRAY + " anvils" + ChatColor.WHITE + "!");
                lore.add(point + ChatColor.WHITE + "Place them or World Edit them in and use " +
                        core.getThemeColor() + "/refill");
                lore.add(point + ChatColor.WHITE + "Clear them with" + core.getThemeColor() + " /msclear" +
                        ChatColor.WHITE + " or" + core.getThemeColor() + " /msc");
                meta.setLore(lore);

                meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                magicSand.setItemMeta(meta);
                magicSand.addUnsafeEnchantment(Enchantment.DURABILITY, 1);

                subject.setItemInHand(magicSand);
                subject.sendMessage(core.getPrefix() + core.getConfig()
                        .getConfigurationSection("lang").getString("magicsand-give"));

                super.onCommand(sender, command, "Give Magic Sand", args);
            }
            else
            {
                subject.sendMessage(core.getPrefix() + core.getNoPerms());
            }
        }
        return true;
    }
}
