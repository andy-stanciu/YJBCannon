package me.yjb.yjbcannon.wire;

import me.yjb.yjbcannon.util.Gui;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class WireGui extends Gui
{
    private static ItemStack[] gui;

    public static ItemStack[] get() { return gui; }

    public static void init()
    {
        gui = instantiate(36);

        gui[27] = getCancelItem();
        gui[35] = getConfirmItem();

        gui[11] = getBlockInfo();
        gui[13] = getTransparentBlockInfo();
        gui[15] = getWiringInfo();
    }

    private static ItemStack getBlockInfo()
    {
        ItemStack item = new ItemStack(Material.PAPER, 1);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(accentColor + ChatColor.BOLD.toString() + "Block");
        List<String> lore = new ArrayList<>();
        String point = accentColor + "» ";
        lore.add(point + ChatColor.WHITE + "Click below to change type.");
        meta.setLore(lore);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        item.setItemMeta(meta);
        item.addUnsafeEnchantment(Enchantment.DURABILITY, 1);

        return item;
    }

    private static ItemStack getTransparentBlockInfo()
    {
        ItemStack item = new ItemStack(Material.PAPER, 1);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(accentColor + ChatColor.BOLD.toString() + "Transparent Block");
        List<String> lore = new ArrayList<>();
        String point = accentColor + "» ";
        lore.add(point + ChatColor.WHITE + "Click below to change type.");
        meta.setLore(lore);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        item.setItemMeta(meta);
        item.addUnsafeEnchantment(Enchantment.DURABILITY, 1);

        return item;
    }

    private static ItemStack getWiringInfo()
    {
        ItemStack item = new ItemStack(Material.PAPER, 1);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(accentColor + ChatColor.BOLD.toString() + "Wiring");
        List<String> lore = new ArrayList<>();
        String point = accentColor + "» ";
        lore.add(point + ChatColor.WHITE + "Click below to change type.");
        meta.setLore(lore);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        item.setItemMeta(meta);
        item.addUnsafeEnchantment(Enchantment.DURABILITY, 1);

        return item;
    }

    private static ItemStack getConfirmItem()
    {
        ItemStack item = new ItemStack(Material.EMERALD_BLOCK, 1);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(themeColor + ChatColor.BOLD.toString() + "Confirm");
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        item.setItemMeta(meta);
        item.addUnsafeEnchantment(Enchantment.DURABILITY, 1);

        return item;
    }

    private static ItemStack getCancelItem()
    {
        ItemStack item = new ItemStack(Material.REDSTONE_BLOCK, 1);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.DARK_RED + ChatColor.BOLD.toString() + "Cancel");
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        item.setItemMeta(meta);
        item.addUnsafeEnchantment(Enchantment.DURABILITY, 1);

        return item;
    }
}
