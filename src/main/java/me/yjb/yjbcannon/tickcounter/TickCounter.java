package me.yjb.yjbcannon.tickcounter;

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

public class TickCounter extends YJBCommand
{
    private final YJBCannon core;

    public TickCounter(YJBCannon core)
    {
        this.core = core;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        if (command.getName().equalsIgnoreCase("tickcounter") && sender instanceof Player)
        {
            Player subject = (Player) sender;

            if (subject.hasPermission("yjbcannon.tickcounter.use"))
            {
                subject.setItemInHand(getTickCounter());
                subject.sendMessage(core.getPrefix() +
                        core.getConfig().getConfigurationSection("lang").getString("tickcounter-receive"));

                super.onCommand(sender, command, "Tick Counter", args);
            }
            else
            {
                subject.sendMessage(core.getPrefix() + core.getNoPerms());
            }
        }
        return true;
    }

    private ItemStack getTickCounter()
    {
        ItemStack tickCounter = new ItemStack(Material.BLAZE_ROD, 1);

        ItemMeta meta = tickCounter.getItemMeta();
        meta.setDisplayName(core.getAccentColor() + "Tick Counter");
        List<String> lore = new ArrayList<>();
        String point = core.getAccentColor() + "» ";
        lore.add(point + ChatColor.WHITE + "Right click a repeater to count its delay!");
        lore.add(point + ChatColor.WHITE + "Left click to reset.");
        meta.setLore(lore);

        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        tickCounter.setItemMeta(meta);
        tickCounter.addUnsafeEnchantment(Enchantment.DURABILITY, 1);

        return tickCounter;
    }
}