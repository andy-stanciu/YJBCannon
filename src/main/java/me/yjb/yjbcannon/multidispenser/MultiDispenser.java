package me.yjb.yjbcannon.multidispenser;

import me.yjb.yjbcannon.YJBCannon;
import me.yjb.yjbcannon.struct.YJBCommand;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Dispenser;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class MultiDispenser extends YJBCommand implements Listener
{
    private final YJBCannon core;

    private MultiExecutor multiExecutor;

    private ItemStack[] multiGui;

    public MultiDispenser(YJBCannon core) { this.core = core; }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args)
    {
        if (command.getName().equalsIgnoreCase("multi") && sender instanceof Player)
        {
            Player p = (Player) sender;

            if (p.hasPermission("yjbcannon.multi.use") && core.getPlotPerm(p))
            {
                if (this.multiGui == null) this.multiGui = MultiGui.get();
                Inventory inv = Bukkit.createInventory(p, 54, core.getThemeColor() + "Multi Dispensers");
                inv.setContents(this.multiGui);

                p.openInventory(inv);

                super.onCommand(sender, command, "Multi Dispenser", args);
            }
            else
            {
                p.sendMessage(core.getPrefix() + core.getNoPerms());
            }
        }
        return true;
    }

    @EventHandler
    public void onInventoryMove(InventoryClickEvent e)
    {
        Inventory inv = e.getInventory();
        String name = inv.getName();
        if (name == null) return;

        if (name.contains("Multi Dispensers"))
        {
            e.setCancelled(true);

            int slot = e.getSlot();
            HumanEntity player = e.getWhoClicked();

            switch (slot)
            {
                case 1:
                case 3:
                case 5:
                case 7:
                    clearSelections(inv, 1, 7);
                    ItemStack item = inv.getItem(slot);
                    ItemMeta itemMeta = item.getItemMeta();
                    itemMeta.setDisplayName(core.getAccentColor() + ChatColor.BOLD.toString() + itemMeta.getDisplayName().replace(core.getThemeColor().toString(), ""));
                    item.setItemMeta(itemMeta);
                    item.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
                    break;
                case 10:
                case 11:
                case 12:
                case 14:
                case 15:
                case 16:
                    updateInfoItem(inv, Integer.parseInt(inv.getItem(slot).getItemMeta().getDisplayName().replace(ChatColor.GREEN.toString(), "").replace(ChatColor.RED.toString(), "")),
                            13, 1, core.getConfig().getInt("multidispenser.max-entities-dispensed"),
                            core.getAccentColor() + ChatColor.BOLD.toString() + "Amount: " + ChatColor.WHITE);
                    break;
                case 19:
                case 20:
                case 21:
                case 23:
                case 24:
                case 25:
                    updateInfoItem(inv, Integer.parseInt(inv.getItem(slot).getItemMeta().getDisplayName().replace(ChatColor.GREEN.toString(), "").replace(ChatColor.RED.toString(), "")),
                            22, 1, core.getConfig().getInt("multidispenser.max-delay"),
                            core.getAccentColor() + ChatColor.BOLD.toString() + "Delay: " + ChatColor.WHITE);
                    break;
                case 28:
                case 29:
                case 30:
                case 32:
                case 33:
                case 34:
                    updateInfoItem(inv, Integer.parseInt(inv.getItem(slot).getItemMeta().getDisplayName().replace(ChatColor.GREEN.toString(), "").replace(ChatColor.RED.toString(), "")),
                            31, 0, 1,
                            core.getAccentColor() + ChatColor.BOLD.toString() + "Priority: " + ChatColor.WHITE);
                    break;
                case 37:
                case 38:
                case 39:
                case 41:
                case 42:
                case 43:
                    updateInfoItem(inv, Integer.parseInt(inv.getItem(slot).getItemMeta().getDisplayName().replace(ChatColor.GREEN.toString(), "").replace(ChatColor.RED.toString(), "")),
                            40, 0, core.getConfig().getInt("multidispenser.max-fuse"),
                            core.getAccentColor() + ChatColor.BOLD.toString() + "Fuse: " + ChatColor.WHITE);
                    break;
                case 45:
                    player.closeInventory();
                    break;
                case 53:
                    int amount = getValueFromInfoItem(inv.getItem(13).getItemMeta());
                    int delay = getValueFromInfoItem(inv.getItem(22).getItemMeta());
                    int priority = getValueFromInfoItem(inv.getItem(31).getItemMeta());
                    int fuse = getValueFromInfoItem(inv.getItem(40).getItemMeta());

                    String type = getMultiType(inv);

                    player.getInventory().addItem(getMultiItem(amount, delay, priority, fuse, type));
                    break;
            }
        }
    }

    private String getMultiType(Inventory inv)
    {
        if (inv.getItem(1).containsEnchantment(Enchantment.DURABILITY)) return "TNT";
        if (inv.getItem(3).containsEnchantment(Enchantment.DURABILITY)) return "Sand";
        if (inv.getItem(5).containsEnchantment(Enchantment.DURABILITY)) return "Red Sand";
        return "Gravel";
    }

    private void clearSelections(Inventory inv, int start, int finish)
    {
        for (int i = start; i <= finish; i++)
        {
            ItemStack item = inv.getItem(i);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(meta.getDisplayName().replace(core.getAccentColor() + ChatColor.BOLD.toString(), core.getThemeColor().toString()));
            item.setItemMeta(meta);
            item.removeEnchantment(Enchantment.DURABILITY);
        }
    }

    @EventHandler
    public void onDispense(BlockDispenseEvent e)
    {
        Block b = e.getBlock();
        BlockState state = b.getState();
        if (!(state instanceof Dispenser)) return;

        Dispenser dispenser = (Dispenser) state;
        Inventory inv = dispenser.getInventory();

        if (inv.getName() == null) return;
        if (inv.getName().equals("container.dispenser")) return;

        e.setCancelled(true);

        String[] params = inv.getName().split(",");
        MultiData multiData = new MultiData();

        for (String param : params)
        {
            String[] data = param.split(":");
            parseDispenserParam(data, multiData);
        }

        World world = b.getWorld();
        Location location = b.getLocation();

        Vector spawnVector = getSpawnVector(getDispenserDirection(b));

        double vectorX = spawnVector.getX();
        double vectorY = spawnVector.getY();
        double vectorZ = spawnVector.getZ();

        double x = location.getX();
        double y = location.getY();
        double z = location.getZ();

        if (this.multiExecutor == null)
        {
            this.multiExecutor = new MultiExecutor(this.core);
            Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(this.core, this.multiExecutor, 1L, 1L);
        }

        multiData.setLocation(new Location(world, x + vectorX + 0.5, y + vectorY, z + vectorZ + 0.5));
        multiData.setRenderedTick(this.multiExecutor.currentTick);
        this.multiExecutor.queue(multiData);
    }

    private ItemStack getMultiItem(int amount, int delay, int priority, int fuse, String type)
    {
        ItemStack item = new ItemStack(Material.DISPENSER, 1);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("m:" + amount + "," + "d:" + delay + "," + "p:" + priority + "," + "f:" + fuse + "," + type.replace(" ", "").toLowerCase());
        List<String> lore = new ArrayList<>();
        String point = core.getAccentColor() + "» ";
        lore.add(core.getAccentColor() + ChatColor.BOLD.toString() + "Multi Dispenser: " + ChatColor.WHITE + type);
        lore.add(point + ChatColor.WHITE + "Amount: " + core.getThemeColor() + amount);
        lore.add(point + ChatColor.WHITE + "Delay: " + core.getThemeColor() + delay + " GT");
        lore.add(point + ChatColor.WHITE + "Priority: " + core.getThemeColor() + priority);
        lore.add(point + ChatColor.WHITE + "Fuse: " + core.getThemeColor() + fuse + " GT");
        meta.setLore(lore);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        item.setItemMeta(meta);
        item.addUnsafeEnchantment(Enchantment.DURABILITY, 1);

        return item;
    }

    private void updateInfoItem(Inventory inv, int modifier, int updateSlot, int clampMin, int clampMax, String newName)
    {
        ItemStack mainItem = inv.getItem(updateSlot);
        ItemMeta mainItemMeta = mainItem.getItemMeta();
        int value = getValueFromInfoItem(mainItemMeta);

        value = clampToBounds(value + modifier, clampMin, clampMax);

        mainItemMeta.setDisplayName(newName + value);
        mainItem.setItemMeta(mainItemMeta);
    }

    private int getValueFromInfoItem(ItemMeta itemMeta)
    {
        return Integer.parseInt(itemMeta.getDisplayName().split(":")[1].trim().replace(ChatColor.WHITE.toString(), ""));
    }

    private void parseDispenserParam(String[] param, MultiData multiData)
    {
        if (param.length == 1)
        {
            switch (param[0].toLowerCase().trim())
            {
                case "sand":
                case "s":
                    multiData.setTNT(false);
                    break;
                case "red sand":
                case "redsand":
                case "red":
                case "r":
                    multiData.setTNT(false);
                    multiData.setSandType(SandType.RED_SAND);
                    break;
                case "gravel":
                case "g":
                    multiData.setTNT(false);
                    multiData.setSandType(SandType.GRAVEL);
                    break;
            }
        }

        if (param.length < 2) return;

        mapValueKeyToData(param[0], param[1], multiData);
    }

    private void mapValueKeyToData(String key, String value, MultiData multiData)
    {
        switch (key.toLowerCase().trim())
        {
            case "m":
            case "multi":
            case "amount":
            case "a":
                try
                {
                    int amount = Integer.parseInt(value);
                    multiData.setAmount(clampToBounds(amount, 1, core.getConfig().getInt("multidispenser.max-entities-dispensed")));
                }
                catch (NumberFormatException ignored) {}
                break;
            case "d":
            case "delay":
            case "wait":
            case "w":
                try
                {
                    int delay = Integer.parseInt(value);
                    multiData.setDelay(clampToBounds(delay, 1, core.getConfig().getInt("multidispenser.max-delay")));
                }
                catch (NumberFormatException ignored) {}
                break;
            case "p":
            case "priority":
            case "pr":
                try
                {
                    int priority = Integer.parseInt(value);
                    multiData.setPriority(clampToBounds(priority, 0, 1));
                }
                catch (NumberFormatException ignored) {}
                break;
            case "f":
            case "fuse":
            case "time":
            case "t":
                try
                {
                    int fuse = Integer.parseInt(value);
                    multiData.setFuse(clampToBounds(fuse, 0, core.getConfig().getInt("multidispenser.max-fuse")));
                }
                catch (NumberFormatException ignored) {}
                break;
        }
    }

    private int clampToBounds(int value, int lower, int upper)
    {
        if (value >= lower && value <= upper) return value;
        if (value < lower) return lower;
        return upper;
    }

    private Vector getSpawnVector(BlockFace face)
    {
        switch (face)
        {
            case NORTH:
                return new Vector(0, 0, -1);
            case SOUTH:
                return new Vector(0, 0, 1);
            case EAST:
                return new Vector(1, 0, 0);
            case WEST:
                return new Vector(-1, 0, 0);
            case UP:
                return new Vector(0, 1, 0);
            case DOWN:
                return new Vector(0, -1, 0);
        }
        return new Vector(0, 0, 0);
    }

    private BlockFace getDispenserDirection(Block dispenser)
    {
        org.bukkit.material.Dispenser disp = (org.bukkit.material.Dispenser) dispenser.getState().getData();

        return disp.getFacing();
    }
}