package me.yjb.yjbcannon.wire;

import com.sk89q.worldedit.*;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.world.World;
import me.yjb.yjbcannon.YJBCannon;
import me.yjb.yjbcannon.struct.YJBCommand;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.*;

import java.util.HashMap;
import java.util.UUID;

public class Wire extends YJBCommand implements Listener
{
    private final YJBCannon core;

    private final HashMap<UUID, WireData> wireData = new HashMap<>();

    private ItemStack[] wireGui;

    private final ItemStack[] blocks = new ItemStack[] { new ItemStack(Material.OBSIDIAN), new ItemStack(Material.STONE), new ItemStack(Material.COBBLESTONE), new ItemStack(Material.QUARTZ_BLOCK) };
    private final ItemStack[] transparentBlocks = new ItemStack[] { new ItemStack(Material.STEP), new ItemStack(Material.GLOWSTONE) };
    private final ItemStack[] wiring = new ItemStack[] { new ItemStack(Material.DIODE), new ItemStack(Material.REDSTONE), new ItemStack(Material.REDSTONE_COMPARATOR) };

    public Wire(YJBCannon core) { this.core = core; }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String str, String[] strings)
    {
        if (command.getName().equalsIgnoreCase("wire") && sender instanceof Player)
        {
            Player p = (Player) sender;

            if (p.hasPermission("yjbcannon.wire.use") && core.getPlotPerm(p))
            {
                Region selection = getSelection(p);
                if (selection == null)
                {
                    p.sendMessage(core.getPrefix() + ChatColor.RED + core.getConfig().getString("lang.wire-no-selection"));
                    return true;
                }

                boolean canWireSelection = true;
                BlockFace groupDirection = null;
                org.bukkit.World world = p.getWorld();

                if (selection.getWidth() > 1 && selection.getLength() > 1)
                {
                    p.sendMessage(core.getPrefix() + ChatColor.RED + core.getConfig().getString("lang.wire-selection-cannot-be-wired") + " Did you make sure that it's width does not exceed 1?");
                    return true;
                }

                if ((selection.getWidth() > core.getConfig().getInt("wire.max-width") || selection.getLength() > core.getConfig().getInt("wire.max-width") ||
                        selection.getHeight() > core.getConfig().getInt("wire.max-height")) && !p.hasPermission("yjbcannon.admin"))
                {
                    p.sendMessage(core.getPrefix() + ChatColor.RED + core.getConfig().getString("lang.wire-selection-cannot-be-wired") + " Is your selection too big?");
                    return true;
                }

                for (BlockVector point : selection)
                {
                    Block b = world.getBlockAt(point.getBlockX(), point.getBlockY(), point.getBlockZ());

                    if (Material.DISPENSER != b.getType())
                    {
                        canWireSelection = false;
                        break;
                    }

                    BlockFace dir = getDispenserDirection(b);
                    if (groupDirection == null) groupDirection = dir;

                    if (groupDirection != dir)
                    {
                        canWireSelection = false;
                        break;
                    }
                }

                if (!canWireSelection || groupDirection == null)
                {
                    p.sendMessage(core.getPrefix() + ChatColor.RED + core.getConfig().getString("lang.wire-selection-cannot-be-wired") + " Does it consist of only dispensers facing the same direction?");
                    return true;
                }

                org.bukkit.util.Vector placementVector = getPlacementVector(groupDirection);
                if (placementVector == null)
                {
                    p.sendMessage(core.getPrefix() + ChatColor.RED + core.getConfig().getString("lang.wire-selection-cannot-be-wired") + " Are any dispensers facing upwards or downwards?");
                    return true;
                }

                this.wireData.put(p.getUniqueId(), new WireData(selection, world, placementVector, groupDirection));

                if (this.wireGui == null) this.wireGui = WireGui.get();
                Inventory inv = Bukkit.createInventory(p, 36, core.getThemeColor() + "Wire");
                inv.setContents(this.wireGui);

                inv.setItem(20, this.blocks[0]);
                inv.setItem(22, this.transparentBlocks[0]);
                inv.setItem(24, this.wiring[0]);

                p.openInventory(inv);

                super.onCommand(sender, command, "Wire", strings);
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

        if (name.contains("Wire"))
        {
            e.setCancelled(true);

            int slot = e.getSlot();
            HumanEntity player = e.getWhoClicked();

            switch (slot)
            {
                case 20:
                    inv.setItem(slot, this.blocks[(getItemIndex(inv.getItem(slot), this.blocks) + 1) % this.blocks.length]);
                    break;
                case 22:
                    inv.setItem(slot, this.transparentBlocks[(getItemIndex(inv.getItem(slot), this.transparentBlocks) + 1) % this.transparentBlocks.length]);
                    break;
                case 24:
                    inv.setItem(slot, this.wiring[(getItemIndex(inv.getItem(slot), this.wiring) + 1) % this.wiring.length]);
                    break;
                case 27:
                    player.closeInventory();
                    break;
                case 35:
                    WireData wireData = this.wireData.get(player.getUniqueId());
                    player.closeInventory();
                    wire((Player)player, wireData, inv.getItem(20).getType(), inv.getItem(22).getType(), mapWiring(inv.getItem(24).getType()));
                    break;
            }
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e)
    {
        Inventory inv = e.getInventory();
        String name = inv.getName();
        if (name == null) return;

        if (name.contains("Wire")) this.wireData.remove(e.getPlayer().getUniqueId());
    }

    private void wire(Player p, WireData wireData, Material block, Material transparentBlock, Material wiring)
    {
        if (wireData == null) return;

        Region selection = wireData.getSelection();
        org.bukkit.World world = wireData.getWorld();
        org.bukkit.util.Vector placementVector = wireData.getPlacementVector();
        BlockFace groupDirection = wireData.getGroupDirection();

        int minX = selection.getMinimumPoint().getBlockX();
        int minY = selection.getMinimumPoint().getBlockY();
        int minZ = selection.getMinimumPoint().getBlockZ();

        int maxX = selection.getMaximumPoint().getBlockX();
        int maxZ = selection.getMaximumPoint().getBlockZ();

        final org.bukkit.util.Vector modificationVector = getModificationVector(minX, maxX, minZ, maxZ);

        int width = modificationVector.getBlockZ() == 1 ? selection.getLength() : selection.getWidth();
        int height = selection.getHeight();

        Block out1 = world.getBlockAt(minX + placementVector.getBlockX(), minY, minZ + placementVector.getBlockZ());
        Block out2 = world.getBlockAt(minX + placementVector.getBlockX() * 2, minY, minZ + placementVector.getBlockZ() * 2);

        if ((out1.getType() == Material.BEDROCK || out2.getType() == Material.BEDROCK) && core.getConfig().getBoolean("wire.check-for-border"))
        {
            p.sendMessage(core.getPrefix() + ChatColor.RED + core.getConfig().getString("lang.wire-too-close-to-border"));
            return;
        }

        for (int row = 0; row < width; row++)
        {
            for (int col = 0; col < height; col++)
            {
                Block b = world.getBlockAt(
                        minX + placementVector.getBlockX() + modificationVector.getBlockX() * row,
                        minY + col + placementVector.getBlockY(),
                        minZ + placementVector.getBlockZ() + modificationVector.getBlockZ() * row);

                if (row % 2 == 0)
                {
                    createWiring(p, b, world, groupDirection, placementVector, block, transparentBlock, wiring, col % 2 == 1, col, height);
                }
                else
                {
                    createWiring(p, b, world, groupDirection, placementVector, block, transparentBlock, wiring, col % 2 == 0, col, height);
                }
            }
        }

        p.sendMessage(core.getPrefix() + core.getConfig().getString("lang.wire-wired-selection"));
    }

    private Material mapWiring(Material gui)
    {
        switch(gui)
        {
            case DIODE:
                return Material.DIODE_BLOCK_OFF;
            case REDSTONE_COMPARATOR:
                return Material.REDSTONE_COMPARATOR_OFF;
            case REDSTONE:
                return Material.REDSTONE_WIRE;
        }
        return Material.DIODE_BLOCK_OFF;
    }

    private int getItemIndex(ItemStack item, ItemStack[] arr)
    {
        for (int i = 0; i < arr.length; i++)
        {
            if (item.getType() == arr[i].getType()) return i;
        }
        return -1;
    }

    private void createWiring(Player p, Block b, org.bukkit.World world, BlockFace groupDirection, org.bukkit.util.Vector placementVector,
                              Material block, Material transparentBlock, Material wiring, boolean isBlock, int col, int height)
    {
        // Create platform
        if (col == 0)
        {
            Location l = b.getLocation();
            int x = l.getBlockX();
            int y = l.getBlockY();
            int z = l.getBlockZ();

            if (l.getBlockY() > 1)
            {
                world.getBlockAt(x, y - 1, z).setType(block);
                world.getBlockAt(x + placementVector.getBlockX(), y - 1, z + placementVector.getBlockZ()).setType(block);

                Block under = world.getBlockAt(x - placementVector.getBlockX(), y - 1, z - placementVector.getBlockZ());
                if (Material.DISPENSER != under.getType()) under.setType(block);
            }
        }

        if (isBlock)
        {
            // Only need the wiring platform if the col < height - 1
            if (col < height - 1)
            {
                // Setting to block
                b.setType(block);

                // Setting transparent block only if col > 0, otherwise block
                Block wireBlock = world.getBlockAt(b.getLocation().add(placementVector));
                if (col > 0)
                {
                    wireBlock.setType(transparentBlock);
                    if (Material.STEP == transparentBlock)
                    {
                        BlockState state = wireBlock.getState();
                        Step s = (Step) state.getData();
                        s.setInverted(true);
                        state.update(true);
                    }
                }
                else
                {
                    wireBlock.setType(block);
                }
            }

            // col == height - 1 (at the top)
            else
            {
                // Setting type to block if the wiring is redstone
                if (Material.REDSTONE_WIRE == wiring) b.setType(block);
            }
        }
        else
        {
            // Setting wiring
            b.setType(wiring);
            if (Material.DIODE_BLOCK_OFF == wiring)
            {
                BlockState state = b.getState();
                Diode repeater = (Diode) state.getData();
                repeater.setFacingDirection(groupDirection);
                state.update(true);
            }
            else if (Material.REDSTONE_COMPARATOR_OFF == wiring)
            {
                BlockState state = b.getState();
                setComparatorDirection(state, groupDirection);
            }

            // Setting redstone
            world.getBlockAt(b.getLocation().add(placementVector)).setType(Material.REDSTONE_WIRE);
        }
    }

    @SuppressWarnings("deprecation")
    private void setComparatorDirection(BlockState state, BlockFace groupDirection)
    {
        int data = state.getRawData() & 0xC;

        switch (groupDirection)
        {
            case EAST:
                data |= 0x1;
                break;
            case SOUTH:
                data |= 0x2;
                break;
            case WEST:
                data |= 0x3;
                break;
            case NORTH:
            default:
                data |= 0x0;
        }

        state.setRawData((byte)data);
        state.update(true);
    }

    private org.bukkit.util.Vector getPlacementVector(BlockFace face)
    {
        switch(face)
        {
            case NORTH:
                return new org.bukkit.util.Vector(0, 0, 1);
            case SOUTH:
                return new org.bukkit.util.Vector(0, 0, -1);
            case EAST:
                return new org.bukkit.util.Vector(-1, 0, 0);
            case WEST:
                return new org.bukkit.util.Vector(1, 0, 0);
        }
        return null;
    }

    private org.bukkit.util.Vector getModificationVector(int minX, int maxX, int minZ, int maxZ)
    {
        // No modification necessary, 1x1 pillar
        if (minX == maxX && minZ == maxZ) return new org.bukkit.util.Vector(0, 0, 0);

        // Z is variable
        if (minX == maxX) return new org.bukkit.util.Vector(0, 0, 1);

        // X is variable
        if (minZ == maxZ) return new org.bukkit.util.Vector(1, 0, 0);

        throw new IllegalArgumentException("Encountered invalid dimensions when calculating modification vector");
    }

    private BlockFace getDispenserDirection(Block dispenser)
    {
        Dispenser disp = (Dispenser) dispenser.getState().getData();

        return disp.getFacing();
    }

    private Region getSelection(Player p)
    {
        Region region = null;

        LocalSession localSession = WorldEdit.getInstance().getSessionManager().findByName(p.getName());
        if (localSession == null) return region;

        World selectionWorld = localSession.getSelectionWorld();

        try
        {
            if (selectionWorld != null) region = localSession.getSelection(selectionWorld);
        }
        catch (Exception ignored) {}

        return region;
    }
}
