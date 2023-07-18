package me.yjb.yjbcannon.nodetickcounter;

import java.util.UUID;

public class TrackedNodeArray
{
    private NodeArray nodeArray;
    private UUID uuid;

    public TrackedNodeArray(NodeArray nodeArray, UUID uuid)
    {
        this.nodeArray = nodeArray;
        this.uuid = uuid;
    }

    public NodeArray getNodeArray()
    {
        return nodeArray;
    }

    public UUID getUuid()
    {
        return uuid;
    }
}
