package me.yjb.yjbcannon.nodetickcounter;

import me.yjb.yjbcannon.YJBCannon;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class NodeTickCountEvent implements Listener
{
    private final YJBCannon core;
    private SignGUI signGUI;
    private final String inventoryTitle;

    public String input;

    public NodeTickCountEvent(YJBCannon core)
    {
        this.core = core;
        this.inventoryTitle = core.getAccentColor() + ChatColor.BOLD.toString() + "Your Nodes";
    }

    @EventHandler
    public void onNodeCountEvent(PlayerInteractEvent e)
    {
        Player p = e.getPlayer();
        ItemStack itemInHand = p.getItemInHand();

        if (itemInHand != null)
        {
            if (Material.STICK == itemInHand.getType())
            {
                e.setCancelled(true);
                if (Action.LEFT_CLICK_AIR == e.getAction() || Action.LEFT_CLICK_BLOCK == e.getAction())
                {
                    openNodeInventory(p);
                }
                else if (Action.RIGHT_CLICK_BLOCK == e.getAction())
                {
                    Block b = e.getClickedBlock();
                    if (Material.REDSTONE_WIRE == b.getType() || Material.DIODE_BLOCK_OFF == b.getType())
                    {
                        if (p.isSneaking())
                        {
                            setNodeArrayHead(p, b.getLocation());
                        }
                        else
                        {
                            Location currentLoc = b.getLocation();
                            NodeArray nodeArray = this.core.getNodeArray(p.getUniqueId());

                            if (nodeArray != null)
                            {
                                Node currentNode = null;

                                for (Node node : nodeArray.getNodes())
                                {
                                    if (currentLoc.equals(node.getLocation()))
                                    {
                                        currentNode = node;
                                        break;
                                    }
                                }

                                if (currentNode == null)
                                {
                                    addNode(p, currentLoc);
                                }
                                else
                                {
                                    removeNode(p, currentNode);
                                }
                            }
                            else
                            {
                                addNode(p, currentLoc);
                            }
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void clickEvent(InventoryClickEvent e)
    {
        Inventory inv = e.getClickedInventory();
        String title = null;


        if (inv != null)
        {
            title = inv.getTitle();
        }

        if (title != null)
        {
            if (title.equals(this.inventoryTitle))
            {
                e.setCancelled(true);

                if (Material.BARRIER == e.getCurrentItem().getType())
                {
                    Player p = (Player) e.getWhoClicked();
                    core.removeNodeArray(p.getUniqueId());
                    p.closeInventory();
                    p.sendMessage(core.getPrefix() + core.getConfig().getString("lang.nodetickcounter-cleared-nodes"));
                }
                else if (Material.STAINED_GLASS_PANE == e.getCurrentItem().getType())
                {
                    Player p = (Player) e.getWhoClicked();
                    String name = inv.getItem(e.getSlot() - 9).getItemMeta().getDisplayName().replace(core.getAccentColor().toString(), "");
                    NodeArray nodeArray = core.getNodeArray(p.getUniqueId());
                    nodeArray.removeNodeByName(name);
                    openNodeInventory(p);
                }
            }
        }
    }

    private void setNodeArrayHead(Player p, Location head)
    {
        NodeArray nodeArray = this.core.getNodeArray(p.getUniqueId());

        if (nodeArray != null)
        {
            nodeArray.setHead(head);
        }
        else
        {
            nodeArray = new NodeArray(head);
        }

        core.setNodeArray(p.getUniqueId(), nodeArray);

        p.sendMessage(core.getPrefix() + core.getConfig().getString("lang.nodetickcounter-set-origin"));
    }

    private void addNode(Player p, Location node)
    {
        if (this.signGUI == null)
        {
            this.signGUI = new SignGUI(this.core);
        }

        this.signGUI.open(p, new String[] {"Enter Node Name:", "", "", ""}, new SignGUI.SignGUIListener()
        {
            @Override
            public void onSignDone(Player player, String[] lines)
            {
                String name = "";
                for (String s : lines)
                {
                    s = s.replace("\"", "");
                    name += s.trim() + " ";
                }
                name = name.replace("Enter Node Name:", "");
                name = name.trim();

                NodeArray nodeArray = core.getNodeArray(p.getUniqueId());

                if (nodeArray == null)
                {
                    nodeArray = new NodeArray(null);
                }
                nodeArray.addNode(new Node(node, name));

                core.setNodeArray(p.getUniqueId(), nodeArray);

                String message = core.getConfig().getString("lang.nodetickcounter-add-node");
                message = message.replace("<node-name>", core.getThemeColor().toString() + core.getNodeArray(p.getUniqueId()).getNodeByLocation(node).getName() + ChatColor.WHITE);
                p.sendMessage(core.getPrefix() + message);
            }
        });
    }

    private void removeNode(Player p, Node node)
    {
        NodeArray nodeArray = core.getNodeArray(p.getUniqueId());
        nodeArray.removeNode(node);

        String message = core.getConfig().getString("lang.nodetickcounter-remove-node");
        message = message.replace("<node-name>", core.getThemeColor().toString() + node.getName() + ChatColor.WHITE);
        p.sendMessage(core.getPrefix() + message);
    }

    private void openNodeInventory(Player p)
    {
        NodeArray nodeArray = core.getNodeArray(p.getUniqueId());

        if (nodeArray != null)
        {
            ArrayList<Node> nodes = nodeArray.getNodes();
            Location head = nodeArray.getHead();

            Inventory nodeInventory = Bukkit.createInventory(p, getInventorySize(nodes.size()), this.inventoryTitle);

            ItemStack origin = new ItemStack(Material.STONE_BUTTON, 1);
            ItemMeta originMeta = origin.getItemMeta();
            originMeta.setDisplayName(core.getThemeColor() + ChatColor.BOLD.toString() + "Origin");
            List<String> originLore = new ArrayList<>();

            if (head != null)
            {
                originLore.add(ChatColor.GRAY + "X: " + core.getThemeColor() + head.getBlockX());
                originLore.add(ChatColor.GRAY + "Y: " + core.getThemeColor() + head.getBlockY());
                originLore.add(ChatColor.GRAY + "Z: " + core.getThemeColor() + head.getBlockZ());
            }
            else
            {
                originLore.add(ChatColor.GRAY + "Not set");
            }

            originMeta.setLore(originLore);
            origin.setItemMeta(originMeta);

            ItemStack clear = new ItemStack(Material.BARRIER, 1);
            ItemMeta clearMeta = clear.getItemMeta();
            clearMeta.setDisplayName(ChatColor.DARK_RED + ChatColor.BOLD.toString() + "Clear");
            List<String> clearLore = new ArrayList<>();
            clearLore.add(ChatColor.GRAY + "Clears your nodes and the origin.");
            clearMeta.setLore(clearLore);
            clear.setItemMeta(clearMeta);

            nodeInventory.setItem(0, origin);
            nodeInventory.setItem(9, clear);

            nodeInventory = addNodes(nodeInventory, nodeArray);
            p.openInventory(nodeInventory);
        }
        else
        {
            p.sendMessage(core.getPrefix() + ChatColor.RED + core.getConfig().getString("lang.nodetickcounter-no-nodes"));
        }
    }

    private Inventory addNodes(Inventory inv, NodeArray nodeArray)
    {
        ArrayList<Node> nodes = nodeArray.getNodes();

        for (int i = 0; i < Math.min(inv.getSize() / 2 - 1, nodes.size()); i++)
        {
            Node nodeObj = nodes.get(i);
            ItemStack node = new ItemStack(Material.REDSTONE, 1);
            ItemMeta nodeMeta = node.getItemMeta();
            nodeMeta.setDisplayName(core.getAccentColor() + nodeObj.getName());
            List<String> nodeLore = new ArrayList<>();

            long headActivated = nodeArray.getHeadObject().getActivated();
            long nodeActivated = nodeObj.getActivated();
            if (headActivated != -1 && nodeActivated != -1)
            {
                double gt = nodeActivated - headActivated;
                if (gt >= 0)
                {
                    nodeLore.add(ChatColor.GRAY + "Ticked after: " + core.getThemeColor() + (int) gt + ChatColor.GRAY + " GT (" +
                            core.getThemeColor() + (gt / 2) + ChatColor.GRAY + " RT, " + core.getThemeColor() + (gt / 20) + ChatColor.GRAY + " S)");
                }
            }

            nodeLore.add(ChatColor.GRAY + "X: " + core.getThemeColor() + nodeObj.getLocation().getBlockX());
            nodeLore.add(ChatColor.GRAY + "Y: " + core.getThemeColor() + nodeObj.getLocation().getBlockY());
            nodeLore.add(ChatColor.GRAY + "Z: " + core.getThemeColor() + nodeObj.getLocation().getBlockZ());
            nodeMeta.setLore(nodeLore);
            nodeMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            node.setItemMeta(nodeMeta);
            node.addUnsafeEnchantment(Enchantment.DURABILITY, 1);

            inv.setItem(i + 1 + 9 * ((i + 1) / 9), node);

            ItemStack remove = new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte)14);
            ItemMeta removeMeta = remove.getItemMeta();
            removeMeta.setDisplayName(ChatColor.DARK_RED + "Remove " + nodeObj.getName());
            List<String> removeLore = new ArrayList<>();
            removeLore.add(ChatColor.GRAY + "Removes this node.");
            removeMeta.setLore(removeLore);
            removeMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            remove.setItemMeta(removeMeta);
            remove.addUnsafeEnchantment(Enchantment.DURABILITY, 1);

            inv.setItem(i + 10 + 9 * ((i + 1) / 9), remove);
        }

        return inv;
    }

    private int getInventorySize(int nodeCount)
    {
        if (nodeCount <= 8)
        {
            return 18;
        }
        else if (nodeCount <= 17)
        {
            return 36;
        }
        else if (nodeCount <= 26)
        {
            return 54;
        }
        return 54;
    }
}