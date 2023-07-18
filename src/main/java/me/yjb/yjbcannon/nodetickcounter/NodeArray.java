package me.yjb.yjbcannon.nodetickcounter;

import org.bukkit.Location;

import java.util.ArrayList;

public class NodeArray
{
    private Head head;
    private ArrayList<Node> nodes;

    public NodeArray(Location head)
    {
        this.head = new Head(head);
        this.nodes = new ArrayList<>();
    }

    public void setHead(Location head) { this.head.setLocation(head); }
    public void addNode(Node node) { this.nodes.add(node); }
    public void removeNode(Node node) { this.nodes.remove(node); }
    public Location getHead() { return this.head.getLocation(); }
    public Head getHeadObject() { return this.head; }
    public ArrayList<Node> getNodes() { return this.nodes; }

    public Node getNodeByLocation(Location location)
    {
        for (Node node : this.nodes)
        {
            if (location.equals(node.getLocation())) return node;
        }
        return null;
    }

    public boolean removeNodeByName(String name)
    {
        return this.nodes.removeIf(node -> name.equals(node.getName()));
    }
}