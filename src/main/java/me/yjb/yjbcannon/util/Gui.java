package me.yjb.yjbcannon.util;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

public abstract class Gui
{
    protected static ChatColor themeColor;
    protected static ChatColor accentColor;
    protected static FileConfiguration config;

    public static void setThemeColor(ChatColor newColor) { themeColor = newColor; }
    public static void setAccentColor(ChatColor newColor) { accentColor = newColor; }
    public static void setConfig(FileConfiguration _config) { config = _config; }

    protected static ItemStack[] instantiate(int size)
    {
        ItemStack[] gui = new ItemStack[size];
        ItemStack filler = getFillerItem(15);

        Arrays.fill(gui, filler);

        return gui;
    }

    protected static ItemStack getFillerItem(int color)
    {
        ItemStack item = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short)color);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(" ");
        item.setItemMeta(meta);

        return item;
    }
}
