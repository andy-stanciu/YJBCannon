package me.yjb.yjbcannon.chunkloader;

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

public class GiveChunkLoader extends YJBCommand
{
    private final YJBCannon core;
    private final ChunkLoader host;
    private Material chunkLoaderType;

    public GiveChunkLoader(YJBCannon core, ChunkLoader host)
    {
        this.core = core;
        this.host = host;
        this.chunkLoaderType = host.getChunkLoaderType();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        if (command.getName().equals("chunkloader") && sender instanceof Player)
        {
            Player p = (Player) sender;

            if (p.hasPermission("yjbcannon.chunkloader.use"))
            {
                ItemStack chunkLoader = new ItemStack(this.chunkLoaderType, 1);

                ItemMeta meta = chunkLoader.getItemMeta();
                meta.setDisplayName(core.getAccentColor() + "Chunk Loader");
                List<String> lore = new ArrayList<>();
                String point = core.getAccentColor() + "» ";
                lore.add(point + ChatColor.WHITE + "Loads the chunks in a " + core.getThemeColor() + core.getConfig().getInt("chunkloader.chunk-loader-radius") + ChatColor.WHITE + " chunk radius!");
                lore.add(point + ChatColor.WHITE + "See your chunk loaders with " + core.getThemeColor() + "/cllist");
                lore.add(point + ChatColor.WHITE + "Clear them with" + core.getThemeColor() + " /clc");
                meta.setLore(lore);

                meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                chunkLoader.setItemMeta(meta);
                chunkLoader.addUnsafeEnchantment(Enchantment.DURABILITY, 1);

                p.setItemInHand(chunkLoader);
                p.sendMessage(core.getPrefix() + core.getConfig()
                        .getConfigurationSection("lang").getString("chunkloader-receive"));

                super.onCommand(sender, command, "Give Chunk Loader", args);
            }
            else
            {
                p.sendMessage(core.getPrefix() + core.getNoPerms());
            }
        }
        return true;
    }
}