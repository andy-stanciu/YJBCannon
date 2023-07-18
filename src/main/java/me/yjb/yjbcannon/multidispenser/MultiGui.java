package me.yjb.yjbcannon.multidispenser;

import me.yjb.yjbcannon.util.Gui;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class MultiGui extends Gui
{
    private static ItemStack[] gui;

    public static ItemStack[] get() { return gui; }

    public static void init()
    {
        gui = instantiate(54);

        gui[1] = getTNTItem();
        gui[3] = getSandItem();
        gui[5] = getRedSandItem();
        gui[7] = getGravelItem();

        gui[45] = getCancelItem();
        gui[53] = getConfirmItem();

        gui[13] = getAmountInfo();
        gui[22] = getDelayInfo();
        gui[31] = getPriorityInfo();
        gui[40] = getFuseInfo();

        for (int i = 0; i < 4; i++)
        {
            gui[10 + i * 9] = getModifyItem(-50, 14);
            gui[11 + i * 9] = getModifyItem(-10, 14);
            gui[12 + i * 9] = getModifyItem(-1, 14);
            gui[14 + i * 9] = getModifyItem(1, 5);
            gui[15 + i * 9] = getModifyItem(10, 5);
            gui[16 + i * 9] = getModifyItem(50, 5);
        }
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

    private static ItemStack getTNTItem()
    {
        ItemStack item = new ItemStack(Material.TNT, 1);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(accentColor + ChatColor.BOLD.toString() + "TNT");
        List<String> lore = new ArrayList<>();
        String point = accentColor + "» ";
        lore.add(point + ChatColor.WHITE + "Click to select type.");
        meta.setLore(lore);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        item.setItemMeta(meta);
        item.addUnsafeEnchantment(Enchantment.DURABILITY, 1);

        return item;
    }

    private static ItemStack getSandItem()
    {
        ItemStack item = new ItemStack(Material.SAND, 1);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(themeColor + "Sand");
        List<String> lore = new ArrayList<>();
        String point = accentColor + "» ";
        lore.add(point + ChatColor.WHITE + "Click to select type.");
        meta.setLore(lore);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        item.setItemMeta(meta);

        return item;
    }

    private static ItemStack getRedSandItem()
    {
        ItemStack item = new ItemStack(Material.SAND, 1, (short)1);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(themeColor + "Red Sand");
        List<String> lore = new ArrayList<>();
        String point = accentColor + "» ";
        lore.add(point + ChatColor.WHITE + "Click to select type.");
        meta.setLore(lore);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        item.setItemMeta(meta);

        return item;
    }

    private static ItemStack getGravelItem()
    {
        ItemStack item = new ItemStack(Material.GRAVEL, 1);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(themeColor + "Gravel");
        List<String> lore = new ArrayList<>();
        String point = accentColor + "» ";
        lore.add(point + ChatColor.WHITE + "Click to select type.");
        meta.setLore(lore);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        item.setItemMeta(meta);

        return item;
    }

    private static ItemStack getModifyItem(int number, int color)
    {
        ItemStack item = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short)color);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName((number < 0 ? ChatColor.RED.toString() : ChatColor.GREEN.toString()) + (number < 0 ? number : "+" + number));
        item.setItemMeta(meta);

        return item;
    }

    private static ItemStack getAmountInfo()
    {
        ItemStack item = new ItemStack(Material.DISPENSER, 1);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(accentColor + ChatColor.BOLD.toString() + "Amount: " + ChatColor.WHITE + "1");
        List<String> lore = new ArrayList<>();
        String point = accentColor + "» ";
        lore.add(point + ChatColor.WHITE + "The number of entities to be dispensed.");
        meta.setLore(lore);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        item.setItemMeta(meta);
        item.addUnsafeEnchantment(Enchantment.DURABILITY, 1);

        return item;
    }

    private static ItemStack getDelayInfo()
    {
        ItemStack item = new ItemStack(Material.DIODE, 1);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(accentColor + ChatColor.BOLD.toString() + "Delay: " + ChatColor.WHITE + "1");
        List<String> lore = new ArrayList<>();
        String point = accentColor + "» ";
        lore.add(point + ChatColor.WHITE + "The delay (GT) before an entity is dispensed.");
        lore.add(point + ChatColor.DARK_GRAY + "Note that the minimum is 1 GT, not 0 GT. Therefore,");
        lore.add(point + ChatColor.DARK_GRAY + "a multi dispenser will always tick off 1 GT after a");
        lore.add(point + ChatColor.DARK_GRAY + "normal dispenser on the same ticks.");
        meta.setLore(lore);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        item.setItemMeta(meta);
        item.addUnsafeEnchantment(Enchantment.DURABILITY, 1);

        return item;
    }

    private static ItemStack getPriorityInfo()
    {
        ItemStack item = new ItemStack(Material.REDSTONE_COMPARATOR, 1);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(accentColor + ChatColor.BOLD.toString() + "Priority: " + ChatColor.WHITE + "0");
        List<String> lore = new ArrayList<>();
        String point = accentColor + "» ";
        lore.add(point + ChatColor.WHITE + "The rendering priority of the entity to be dispensed.");
        lore.add(point + ChatColor.DARK_GRAY + "A priority of 0 will render before a priority of 1.");
        meta.setLore(lore);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        item.setItemMeta(meta);
        item.addUnsafeEnchantment(Enchantment.DURABILITY, 1);

        return item;
    }

    private static ItemStack getFuseInfo()
    {
        ItemStack item = new ItemStack(Material.TNT, 1);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(accentColor + ChatColor.BOLD.toString() + "Fuse: " + ChatColor.WHITE + "80");
        List<String> lore = new ArrayList<>();
        String point = accentColor + "» ";
        lore.add(point + ChatColor.WHITE + "The fuse (GT) of the TNT to be dispensed.");
        lore.add(point + ChatColor.DARK_GRAY + "Fuse only affects TNT.");
        meta.setLore(lore);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        item.setItemMeta(meta);
        item.addUnsafeEnchantment(Enchantment.DURABILITY, 1);

        return item;
    }
}