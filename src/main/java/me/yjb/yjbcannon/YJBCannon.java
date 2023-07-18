package me.yjb.yjbcannon;

import me.clip.placeholderapi.PlaceholderAPI;
import me.yjb.yjbcannon.block36.Block36;
import me.yjb.yjbcannon.buildhelper.AutoRedstone;
import me.yjb.yjbcannon.buildhelper.ToggleBuildHelper;
import me.yjb.yjbcannon.calculator.Calculator;
import me.yjb.yjbcannon.chunkloader.*;
import me.yjb.yjbcannon.entityclear.EntityClear;
import me.yjb.yjbcannon.entityride.EntityRide;
import me.yjb.yjbcannon.magicsand.*;
import me.yjb.yjbcannon.multidispenser.MultiDispenser;
import me.yjb.yjbcannon.multidispenser.MultiGui;
import me.yjb.yjbcannon.network.WebClient;
import me.yjb.yjbcannon.nodetickcounter.*;
import me.yjb.yjbcannon.plotcorner.PlotCorner;
import me.yjb.yjbcannon.protectionblocks.DisableDispenserDrops;
import me.yjb.yjbcannon.regenwalls.RegenLocation;
import me.yjb.yjbcannon.regenwalls.RegenWall;
import me.yjb.yjbcannon.remotefire.PowerEvent;
import me.yjb.yjbcannon.remotefire.RemoteObject;
import me.yjb.yjbcannon.stackremover.RemoveEvent;
import me.yjb.yjbcannon.stackremover.StackRemover;
import me.yjb.yjbcannon.tickcounter.CountEvent;
import me.yjb.yjbcannon.tickcounter.TickCounter;
import me.yjb.yjbcannon.tickcounter.TickData;
import me.yjb.yjbcannon.tntfill.TNTClear;
import me.yjb.yjbcannon.tntfill.TNTFill;
import me.yjb.yjbcannon.util.Features;
import me.yjb.yjbcannon.util.Gui;
import me.yjb.yjbcannon.voidblock.ClearVoidBlock;
import me.yjb.yjbcannon.voidblock.GiveVoidBlock;
import me.yjb.yjbcannon.voidblock.VoidBlock;
import me.yjb.yjbcannon.voidblock.VoidLocation;
import me.yjb.yjbcannon.wallgenerator.WallGenerator;
import me.yjb.yjbcannon.protectionblocks.ProtectEvent;
import me.yjb.yjbcannon.util.Upload;
import me.yjb.yjbcannon.watercannon.WaterCannon;
import me.yjb.yjbcannon.waterprotect.WaterFlowEvent;
import me.yjb.yjbcannon.wire.Wire;
import me.yjb.yjbcannon.wire.WireGui;
import org.bukkit.*;
import org.bukkit.command.CommandExecutor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.Listener;
import org.bukkit.material.MaterialData;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.PrintStream;
import java.net.URI;
import java.net.URLClassLoader;
import java.util.*;

public final class YJBCannon extends JavaPlugin
{
    private final String version = "2.3.3";
    private final String product = "0";
    private final String server = "20.102.121.128:20500";
    private final long TIMEOUT = 5000;
    private String clientIP = null;
    private String updatedVersion;
    private String clientName = null;

    private volatile boolean valid = false;
    private volatile boolean checkedForUpdates = false;
    private volatile boolean isOutdated = false;
    private volatile boolean isUpdateDownloaded = false;

    private final ChatColor themeColor = ChatColor.valueOf(getConfig().getString("lang.theme-color"));
    private final ChatColor accentColor = ChatColor.valueOf(getConfig().getString("lang.accent-color"));
    private final String prefix = ChatColor.DARK_GRAY + "[" + accentColor + ChatColor.BOLD.toString() +
            getConfig().getConfigurationSection("lang").getString("chat-prefix1") +
            themeColor + ChatColor.BOLD.toString() + getConfig().getConfigurationSection("lang")
            .getString("chat-prefix2") + ChatColor.DARK_GRAY + "] " + ChatColor.WHITE;
    private final String line = ChatColor.DARK_GRAY + ChatColor.STRIKETHROUGH.toString() +
            "--------------------------------------------------";
    private final String noPerms = ChatColor.RED + getConfig().getConfigurationSection("lang")
            .getString("no-perms");
    private final String plotPerm = getConfig().getString("plot-access");

    private HashMap<UUID, TickData> tickData = new HashMap<>();
    private HashMap<UUID, Integer[]> wallData = new HashMap<>();
    private HashMap<UUID, Integer[]> startingPositions = new HashMap<>();
    private HashMap<UUID, Material[][][]> cache = new HashMap<>();
    private HashMap<UUID, MaterialData[][][]> metadata = new HashMap<>();
    private HashMap<UUID, Long> wallCooldown = new HashMap<>();
    private ArrayList<TrackedLocation> magicBlocks = new ArrayList<>();
    private HashMap<UUID, Integer> playerBlocks = new HashMap<>();
    private HashMap<UUID, Location> buttonLocations = new HashMap<>();
    private HashMap<UUID, Location> leverLocations = new HashMap<>();
    private HashMap<UUID, Integer> nBlocksRegistered = new HashMap<>();
    private HashMap<UUID, Boolean> isLeverPowered = new HashMap<>();
    private HashMap<UUID, Integer> nDispensersFilled = new HashMap<>();
    private HashMap<UUID, Integer> nDispensersCleared = new HashMap<>();
    private HashMap<UUID, Integer> nMagicBlocksRemoved = new HashMap<>();
    private ArrayList<ChunkLocation> loadedChunks = new ArrayList<>();
    private ArrayList<UUID> playersWithBuildHelper = new ArrayList<>();
    private ArrayList<VoidLocation> voidBlocks = new ArrayList<>();
    private HashMap<UUID, Integer> playerVoidBlocks = new HashMap<>();
    private HashMap<UUID, Integer> nVoidBlocksRemoved = new HashMap<>();
    private HashMap<UUID, NodeArray> nodeArrays = new HashMap<>();
    private ArrayList<TrackedNodeArray> trackedNodeArrays = new ArrayList<>();
    private ArrayList<RegenLocation> regenLocations = new ArrayList<>();
    private HashMap<UUID, Integer> playerRegens = new HashMap<>();

    private CountEvent countEvent = new CountEvent(this);
    private RemoveEvent removeEvent = new RemoveEvent(this);
    private MagicSand magicSand = new MagicSand(this);
    private PowerEvent powerEvent = new PowerEvent(this);
    private ProtectEvent protectEvent = new ProtectEvent(this);
    private WaterFlowEvent waterFlowEvent = new WaterFlowEvent(this);
    private ChunkLoader chunkLoader = new ChunkLoader(this);
    private ChunkLoaderUpdater chunkLoaderUpdater = new ChunkLoaderUpdater(this, this.chunkLoader);
    private ChunkUnloadEvent chunkUnloadEvent = new ChunkUnloadEvent(this, this.chunkLoader);
    private DisableDispenserDrops disableDispenserDrops = new DisableDispenserDrops(this);
    private AutoRedstone autoRedstone = new AutoRedstone(this);
    private VoidBlock voidBlock = new VoidBlock(this);
    private NodeTickCountEvent nodeTickCountEvent = new NodeTickCountEvent(this);
    private RedstonePowerEvent redstonePowerEvent = new RedstonePowerEvent(this);
    private Block36 block36 = new Block36(this);
    private RegenWall regenWall = new RegenWall(this);
    private EntityRide entityRide = new EntityRide(this);

    private TickCounter tickCounter = new TickCounter(this);
    private StackRemover stackRemover = new StackRemover(this);
    private WallGenerator wallGenerator = new WallGenerator(this);
    private GiveMagicSand giveMagicSand = new GiveMagicSand(this);
    private RegisterMagicBlock registerMagicBlock = new RegisterMagicBlock(this, this.magicSand);
    private ClearMagicBlocks clearMagicBlocks = new ClearMagicBlocks(this);
    private RemoteObject remoteObject = new RemoteObject(this);
    private YJBCannonReload reload = new YJBCannonReload(this);
    private Features features = new Features(this);
    private Upload upload = new Upload(this);
    private TNTFill tntFill = new TNTFill(this);
    private TNTClear tntClear = new TNTClear(this);
    private Calculator calculator = new Calculator(this);
    private ClearChunkLoader clearChunkLoader = new ClearChunkLoader(this);
    private GiveChunkLoader giveChunkLoader = new GiveChunkLoader(this, this.chunkLoader);
    private ListChunkLoader listChunkLoader = new ListChunkLoader(this, this.chunkLoader);
    private ToggleBuildHelper toggleBuildHelper = new ToggleBuildHelper(this);
    private GiveVoidBlock giveVoidBlock = new GiveVoidBlock(this);
    private ClearVoidBlock clearVoidBlock = new ClearVoidBlock(this);
    private WaterCannon waterCannon = new WaterCannon(this);
    private NodeTickCounter nodeTickCounter = new NodeTickCounter(this);
    private Wire wire = new Wire(this);
    private MultiDispenser multiDispenser = new MultiDispenser(this);
    private PlotCorner plotCorner = new PlotCorner(this);
    private EntityClear entityClear = new EntityClear(this);

    public HashMap<UUID, TickData> getTickData() { return this.tickData; }
    public HashMap<UUID, Integer[]> getWallData() { return this.wallData; }
    public HashMap<UUID, Integer[]> getStartingPositions() { return this.startingPositions; }
    public HashMap<UUID, Material[][][]> getCache() { return this.cache; }
    public HashMap<UUID, MaterialData[][][]> getMetadata() { return this.metadata; }
    public HashMap<UUID, Long> getWallCooldown() { return this.wallCooldown; }
    public ArrayList<TrackedLocation> getMagicBlocks() { return this.magicBlocks; }
    public ArrayList<ChunkLocation> getLoadedChunks() { return this.loadedChunks; }
    public HashMap<UUID, Integer> getPlayerBlocks() { return this.playerBlocks; }
    public HashMap<UUID, Location> getButtonLocations() { return this.buttonLocations; }
    public HashMap<UUID, Location> getLeverLocations() { return this.leverLocations; }
    public FileConfiguration getChunkLoaderCache() { return this.chunkLoader.getCache(); }
    public ArrayList<UUID> getPlayersWithBuildHelper() { return this.playersWithBuildHelper; }
    public ArrayList<VoidLocation> getVoidBlocks() { return this.voidBlocks; }
    public HashMap<UUID, Integer> getPlayerVoidBlocks() { return this.playerVoidBlocks; }
    public HashMap<UUID, Integer> getPlayerRegens() { return this.playerRegens; }
    public ArrayList<RegenLocation> getRegenLocations() { return this.regenLocations; }
    public void addLoadedChunk(ChunkLocation cl) { this.loadedChunks.add(cl); }
    public void removeLoadedChunk(ChunkLocation cl) { this.loadedChunks.remove(cl); }
    public void removeLoadedChunk(UUID uuid) { this.loadedChunks.removeIf(cl -> cl.getUuid().equals(uuid)); }
    public int getNBlocksRegistered(UUID uuid) { return this.nBlocksRegistered.getOrDefault(uuid, 0); }
    public void setNBlocksRegistered(UUID uuid, int n) { this.nBlocksRegistered.put(uuid, n); }
    public void addButtonLocation(UUID uuid, Location location) { this.buttonLocations.put(uuid, location); }
    public void addLeverLocation(UUID uuid, Location location) { this.leverLocations.put(uuid, location); }
    public void removeButtonLocation(UUID uuid) { this.buttonLocations.remove(uuid); }
    public void removeLeverLocation(UUID uuid) { this.leverLocations.remove(uuid); }
    public boolean getLeverPowered(UUID uuid) { return this.isLeverPowered.getOrDefault(uuid, false); }
    public void setLeverPowered(UUID uuid, boolean isPowered) { this.isLeverPowered.put(uuid, isPowered); }
    public int getNDispensersFilled(UUID uuid) { return this.nDispensersFilled.getOrDefault(uuid, 0); }
    public int getNDispensersCleared(UUID uuid) { return this.nDispensersCleared.getOrDefault(uuid, 0); }
    public void setNDispensersFilled(UUID uuid, int n) { this.nDispensersFilled.put(uuid, n); }
    public void setNDispensersCleared(UUID uuid, int n) { this.nDispensersCleared.put(uuid, n); }
    public int getNMagicBlocksRemoved(UUID uuid) { return this.nMagicBlocksRemoved.getOrDefault(uuid, 0); }
    public void setNMagicBlocksRemoved(UUID uuid, int n) { this.nMagicBlocksRemoved.put(uuid, n); }
    public void addPlayerWithBuildHelper(UUID uuid) { this.playersWithBuildHelper.add(uuid); }
    public void removePlayerWithBuildHelper(UUID uuid) { this.playersWithBuildHelper.remove(uuid); }
    public int getNVoidBlocksRegistered(UUID uuid) { return this.playerVoidBlocks.getOrDefault(uuid, 0); }
    public int getNVoidBlocksRemoved(UUID uuid) { return this.nVoidBlocksRemoved.getOrDefault(uuid, 0); }
    public void setNVoidBlocksRemoved(UUID uuid, int n) { this.nVoidBlocksRemoved.put(uuid, n); }
    public NodeArray getNodeArray(UUID uuid) { return this.nodeArrays.getOrDefault(uuid, null); }

    public String getVersion() { return this.version; }
    public ChatColor getThemeColor() { return this.themeColor; }
    public ChatColor getAccentColor() { return this.accentColor; }
    public String getPrefix() { return this.prefix; }
    public String getLine() { return this.line; }
    public String getNoPerms() { return this.noPerms; }
    public void setValid(boolean valid) { this.valid = valid; }
    public void setClientIP(String clientIP) { this.clientIP = clientIP; }
    public void setOutdated(boolean isUpdated) { this.isOutdated = isUpdated; }
    public void setUpdateDownloaded(boolean isUpdateDownloaded) { this.isUpdateDownloaded = isUpdateDownloaded; }
    public void setUpdatedVersion(String updatedVersion) { this.updatedVersion = updatedVersion; }
    public void setCheckedForUpdates(boolean checkedForUpdates) { this.checkedForUpdates = checkedForUpdates; }
    public String getUpdatedVersion() { return this.updatedVersion; }
    public void setClientName(String clientName) { this.clientName = clientName; }

    public boolean getPlotPerm(Player p)
    {
        if (p.hasPermission("yjbcannon.admin")) return true;
        return (PlaceholderAPI.setPlaceholders(p, this.plotPerm).equalsIgnoreCase("yes"));
    }

    public UUID removeLoadedChunk(int x, int z)
    {
        int index = -1;
        for (int i = 0; i < this.loadedChunks.size(); i++)
        {
            if (this.loadedChunks.get(i).getX() == x && this.loadedChunks.get(i).getZ() == z)
            {
                index = i;
                break;
            }
        }

        if (index != -1) return this.loadedChunks.remove(index).getUuid();
        return null;
    }

    public void addVoidBlock(Location loc, UUID uuid, int blockCount, Snowball center)
    {
        this.voidBlocks.add(new VoidLocation(loc, uuid, center));
        this.playerVoidBlocks.put(uuid, blockCount + 1);
    }

    public void removeVoidBlock(VoidLocation vl)
    {
        this.voidBlocks.remove(vl);
        this.playerVoidBlocks.put(vl.getUuid(), this.playerVoidBlocks.getOrDefault(vl.getUuid(), 0) - 1);
    }

    public void setNodeArray(UUID uuid, NodeArray nodeArray)
    {
        this.trackedNodeArrays.removeIf(trackedNodeArray -> trackedNodeArray.getUuid().equals(uuid));
        this.trackedNodeArrays.add(new TrackedNodeArray(nodeArray, uuid));
        this.nodeArrays.put(uuid, nodeArray);
    }

    public void removeNodeArray(UUID uuid)
    {
        this.trackedNodeArrays.removeIf(trackedNodeArray -> trackedNodeArray.getUuid().equals(uuid));
        this.nodeArrays.remove(uuid);
    }

    public Object[] getNodeInformationByLocation(Location location)
    {
        for (TrackedNodeArray trackedNodeArray : this.trackedNodeArrays)
        {
            NodeArray nodeArray = trackedNodeArray.getNodeArray();
            Node node = nodeArray.getNodeByLocation(location);
            if (node != null) return new Object[] {node, trackedNodeArray};

            Head head = nodeArray.getHeadObject();
            if (location.equals(head.getLocation())) return new Object[] {head, trackedNodeArray};
        }
        return null;
    }

    @Override
    public void onDisable() {

        if (getConfig().getBoolean("auto-update")) autoUpdate();
    }

    @Override
    public void onEnable()
    {
        this.valid = false;

        getConfig().options().copyDefaults();
        saveDefaultConfig();

        isIPRegistered();

        System.out.println("[YJBCannon] Hello " + this.clientName + ", YJBCannon is starting up!");

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null
            && Bukkit.getPluginManager().getPlugin("ProtocolLib") != null
            && Bukkit.getPluginManager().getPlugin("FastAsyncWorldEdit") != null
            && Bukkit.getPluginManager().getPlugin("WorldEdit") != null
            && Bukkit.getPluginManager().getPlugin("PlotSquared") != null)
        {
            refreshConfigValues();

            new YJBCannonExpansion(this).register();

            ChunkLoaderCache.setup();

            initGui();

            registerEvent("enable-tick-counter", this.countEvent);
            registerEvent("enable-stack-remover", this.removeEvent);
            registerEvent("enable-magic-sand", this.magicSand);
            registerEvent("enable-remote-fire", this.powerEvent);
            registerEvent("enable-water-protect", this.waterFlowEvent);
            registerEvent("enable-chunk-loader", this.chunkLoader);
            registerEvent("enable-chunk-loader", this.chunkLoaderUpdater);
            registerEvent("enable-chunk-loader", this.chunkUnloadEvent);
            registerEvent("enable-chunk-loader", this.listChunkLoader);
            registerEvent("enable-build-helper", this.autoRedstone);
            registerEvent("enable-void-block", this.voidBlock);
            registerEvent("enable-node-tick-counter", this.nodeTickCountEvent);
            registerEvent("enable-node-tick-counter", this.redstonePowerEvent);
            registerEvent("disable-dispenser-drops", this.disableDispenserDrops);
            registerEvent("enable-block-36", this.block36);
            registerEvent("enable-regen-wall", this.regenWall);
            registerEvent("enable-wire", this.wire);
            registerEvent("enable-multi-dispenser", this.multiDispenser);
            registerEvent("enable-entity-ride", this.entityRide);
            Bukkit.getPluginManager().registerEvents(this.protectEvent, this);

            registerCommand("enable-tick-counter", "tickcounter", this.tickCounter);
            registerCommand("enable-stack-remover", "bone", this.stackRemover);
            registerCommand("enable-wall-generator", "walls", this.wallGenerator);
            registerCommand("enable-wall-generator", "undowalls", this.wallGenerator);
            registerCommand("enable-magic-sand", "magicsand", this.giveMagicSand);
            registerCommand("enable-magic-sand", "refill", this.registerMagicBlock);
            registerCommand("enable-magic-sand", "magicclear", this.clearMagicBlocks);
            registerCommand("enable-remote-fire", "fire", this.remoteObject);
            registerCommand("enable-remote-fire", "lever", this.remoteObject);
            registerCommand("enable-tntfill", "tntfill", this.tntFill);
            registerCommand("enable-tntfill", "tntclear", this.tntClear);
            registerCommand("enable-calculator", "calc", this.calculator);
            registerCommand("enable-chunk-loader", "chunkloader", this.giveChunkLoader);
            registerCommand("enable-chunk-loader", "chunkloaderclear", this.clearChunkLoader);
            registerCommand("enable-chunk-loader", "chunkloaders", this.listChunkLoader);
            registerCommand("enable-build-helper", "buildhelper", this.toggleBuildHelper);
            registerCommand("enable-void-block", "voidblock", this.giveVoidBlock);
            registerCommand("enable-void-block", "voidclear", this.clearVoidBlock);
            registerCommand("enable-water-cannon", "water", this.waterCannon);
            registerCommand("enable-node-tick-counter", "nodetickcounter", this.nodeTickCounter);
            registerCommand("enable-block-36", "block36", this.block36);
            registerCommand("enable-regen-wall", "regenwall", this.regenWall);
            registerCommand("enable-wire", "wire", this.wire);
            registerCommand("enable-multi-dispenser", "multi", this.multiDispenser);
            registerCommand("enable-plot-corner", "corner", this.plotCorner);
            registerCommand("enable-clear-entities", "clearentities", this.entityClear);
            getCommand("yjbcannonreload").setExecutor(this.reload);
            getCommand("features").setExecutor(this.features);
            getCommand("upload").setExecutor(this.upload);

            System.out.println("[YJBCannon] YJBCannon by yJb has started up.");
        }
        else
        {
            System.out.println("[YJBCannon] You are missing one of the following required dependencies, disabling plugin: PlaceholderAPI, ProtocolLib, WorldEdit, FastASyncWorldEdit, PlotSquared");
            Bukkit.getPluginManager().disablePlugin(this);
        }
    }

    private void initGui()
    {
        Gui.setAccentColor(this.accentColor);
        Gui.setThemeColor(this.themeColor);
        Gui.setConfig(getConfig());

        WireGui.init();
        MultiGui.init();
    }

    private void registerEvent(String path, Listener listener)
    {
        if (getConfig().getBoolean("toggles." + path)) Bukkit.getPluginManager().registerEvents(listener, this);
    }

    private void registerCommand(String path, String name, CommandExecutor commandExecutor)
    {
        if (getConfig().getBoolean("toggles." + path)) getCommand(name).setExecutor(commandExecutor);
    }

    public void refreshConfigValues()
    {
        checkAndSet("auto-update", true);
        checkAndSet("toggles.enable-protection-blocks", true);
        checkAndSet("toggles.enable-unbreakable-blocks", true);
        checkAndSet("toggles.disable-dispenser-drops", true);
        checkAndSet("toggles.enable-tick-counter", true);
        checkAndSet("toggles.enable-magic-sand", true);
        checkAndSet("toggles.enable-remote-fire", true);
        checkAndSet("toggles.enable-water-protect", true);
        checkAndSet("toggles.enable-chunk-loader", true);
        checkAndSet("toggles.enable-build-helper", true);
        checkAndSet("toggles.enable-void-block", true);
        checkAndSet("toggles.enable-node-tick-counter", true);
        checkAndSet("toggles.enable-wall-generator", true);
        checkAndSet("toggles.enable-calculator", true);
        checkAndSet("toggles.enable-water-cannon", true);
        checkAndSet("toggles.enable-tntfill", true);
        checkAndSet("toggles.enable-block-36", true);
        checkAndSet("toggles.enable-regen-wall", true);
        checkAndSet("toggles.enable-wire", true);
        checkAndSet("toggles.enable-multi-dispenser", true);
        checkAndSet("toggles.enable-plot-corner", true);
        checkAndSet("toggles.enable-entity-ride", true);
        checkAndSet("toggles.enable-clear-entities", true);
        checkAndSet("chunkloader.chunk-loader", "BEACON");
        checkAndSet("chunkloader.chunk-loader-limit", 16);
        checkAndSet("chunkloader.chunk-loader-radius", 2);
        checkAndSet("magicsand.magic-block", "SEA_LANTERN");
        checkAndSet("magicsand.magic-block-limit", 128);
        checkAndSet("magicsand.register-radius", 32);
        checkAndSet("magicsand.set-blocks-to-sand-instead-of-spawning-sand", false);
        checkAndSet("magicsand.speed", 0.05);
        checkAndSet("voidblock.void-block", "COAL_BLOCK");
        checkAndSet("voidblock.void-block-limit", 8);
        checkAndSet("water.water-radius", 64);
        checkAndSet("protectionblocks.prot-block", "EMERALD_BLOCK");
        checkAndSet("protectionblocks.unbreakable-block", "DIAMOND_BLOCK");
        checkAndSet("tntfill.fill-radius", 64);
        checkAndSet("tntclear.clear-radius", 64);
        checkAndSet("regenwall.type", "GOLD_BLOCK");
        checkAndSet("regenwall.regen-speed", 1.0);
        checkAndSet("regenwall.regen-wall-limit", 16);
        checkAndSet("wire.max-width", 64);
        checkAndSet("wire.max-height", 64);
        checkAndSet("wire.check-for-border", true);
        checkAndSet("multidispenser.max-entities-dispensed", 256);
        checkAndSet("multidispenser.max-delay", 320);
        checkAndSet("multidispenser.max-fuse", 80);
        checkAndSet("clearentities.clear-radius", 64);
        checkAndSet("wallgenerator.max-wall-count", 100);
        checkAndSet("wallgenerator.max-wall-width", 25);
        checkAndSet("wallgenerator.cooldown", 30);
        checkAndSet("wallgenerator.check-for-border", true);
        checkAndSet("wallgenerator.allowed-materials", new String[] {"OBSIDIAN", "COBBLESTONE", "SAND", "STONE", "DIRT", "GLOWSTONE", "GLASS"});
        checkAndSet("waterprotect.unbreakable", new String[] {"REDSTONE_COMPARATOR", "REDSTONE_COMPARATOR_OFF", "REDSTONE_COMPARATOR_ON", "REDSTONE_TORCH_OFF",
                "REDSTONE_TORCH_ON", "REDSTONE_WIRE", "DIODE", "DIODE_BLOCK_OFF", "DIODE_BLOCK_ON", "REDSTONE", "STONE_BUTTON", "LEVER", "CARPET"});
        checkAndSet("lang.theme-color", "GREEN");
        checkAndSet("lang.accent-color", "DARK_GREEN");
        checkAndSet("lang.chat-prefix1", "YJB");
        checkAndSet("lang.chat-prefix2", "Cannon");
        checkAndSet("lang.no-perms", "You do not have sufficient permissions.");
        checkAndSet("lang.upload-message", "&a&lWebsite: &f&ohttps://athion.net/fawe/");
        checkAndSet("lang.features-list", new String[] {"&2-&aTick Counter &8 | &f&o/tc", "&2-&aNode Tick Counter &8 | &f&o/ntc", "&2-&aStack Remover &8 | &f&o/bone",
                "&2-&aMagic Sand &8 | &f&o/ms, clear with /magicclear (/mc), refill with /refill", "&2-&aChunk Loader &8 | &f&o/cl, info with /chunkloaders, clear with /clclear",
                "&2-&aWall Generator &8 | &f&o/walls <amount> <width> <material>", "&2-&aBuild Helper &8 | &f&o/bh, assists with slab and glowstone placement",
                "&2-&aVoid Block &8 | &f&o/vb, clear with /vbclear (/vbc)", "&2-&aWater &8 | &f&o/water, waters your cannon", "&2-&aRemote Button &8 | &f&o/fire", "&2-&aRemote Lever &8 | &f&o/lever",
                "&2-&aTNT Fill &8 | &f&o/tntfill (/tf)", "&2-&aPlaceable Block 36 &8 | &f&o/block36", "&2-&aAuto Wire Dispensers &8 | &f&o/wire", "&2-&aMulti Dispensers &8 | &f&o/multi",
                "&2-&aSelf Regenerating Walls | &f&o/regenwall", "&2-&aClear Entities | &f&o/ce", "&2-&aEntity Ride | &f&osaddle", "&2-&aPlot Corner | &f&o/corner <+/-> <+/->",
                "&2-&aSchematic Upload &8 | &f&o/upload", "&2-&aProtection Blocks &8 | &f&oemerald block", "&2-&aUnbreakable Blocks &8 | &f&odiamond block", "&2-&aCalculator &8 | &f&o/calc"});
        checkAndSet("lang.tickcounter-receive", "You have been given a tick counter!");
        checkAndSet("lang.tickcounter-increment", "&a(%yjbcannon_tickcounter_delay%) &fGT: &a%yjbcannon_tickcounter_gt%&f, RT: &a%yjbcannon_tickcounter_rt%&f, S: &a%yjbcannon_tickcounter_seconds%&f, PR: &a%yjbcannon_tickcounter_priority%&f");
        checkAndSet("lang.nodetickcounter-receive", "You have been given a node tick counter!");
        checkAndSet("lang.nodetickcounter-set-origin", "You have set your origin node!");
        checkAndSet("lang.nodetickcounter-add-node", "You have added the node <node-name>!");
        checkAndSet("lang.nodetickcounter-remove-node", "You have removed the node <node-name>!");
        checkAndSet("lang.nodetickcounter-no-nodes", "You have not added any nodes!");
        checkAndSet("lang.nodetickcounter-cleared-nodes", "You have cleared all of your nodes!");
        checkAndSet("lang.nodetickcounter-node-powered", "The node <node-name> was ticked off after <gt> GT (<rt> RT, <sec> S)!");
        checkAndSet("lang.stackeremover-receive", "You have been given a stack remover!");
        checkAndSet("lang.stackremover-remove", "The stack has been removed!");
        checkAndSet("lang.stackremover-heal", "The wall has been healed!");
        checkAndSet("lang.wallgenerator-generate", "Generated &a%yjbcannon_wallgenerator_count% %yjbcannon_wallgenerator_width%&f-wide &a%yjbcannon_wallgenerator_type% &fwall%yjbcannon_wallgenerator_plural%! Undo with &f&o/undowalls");
        checkAndSet("lang.wallgenerator-usage", "Usage: /walls <amount> <width> <material>");
        checkAndSet("lang.wallgenerator-undo", "Successfully undid your most recent wall generation!");
        checkAndSet("lang.wallgenerator-wall-limit", "You are not permitted to generate more than 100 walls.");
        checkAndSet("lang.wallgenerator-too-close-to-border", "Unable to generate walls, you are too close to your plot border.");
        checkAndSet("lang.wallgenerator-width-limit", "You are not permitted to generate walls that are more than 25 wide.");
        checkAndSet("lang.wallgenerator-on-cooldown", "You are on cooldown to generate walls for the next %yjbcannon_wallgenerator_cooldown% seconds.");
        checkAndSet("lang.magicsand-reached-limit", "You have been given a magic sand block!");
        checkAndSet("lang.magicsand-registered-blocks", "You have registered &a%yjbcannon_magicsand_blocks_registered% &fmagic blocks!");
        checkAndSet("lang.magicsand-found-no-blocks", "Did not find any magic blocks nearby.");
        checkAndSet("lang.magicsand-removed-blocks", "Removed &a%yjbcannon_magicsand_blocks_removed% &fmagic blocks!");
        checkAndSet("lang.remotefire-button-fire", "Fired!");
        checkAndSet("lang.remotefire-button-removed", "Your button was recently removed.");
        checkAndSet("lang.remotefire-button-notfound", "You have not recently pressed a button.");
        checkAndSet("lang.remotefire-lever-flick", "Flicked %yjbcannon_remotefire_lever_state%&f!");
        checkAndSet("lang.remotefire-lever-removed", "Your lever was recently removed.");
        checkAndSet("lang.remotefire-lever-notfound", "You have not recently flicked a lever.");
        checkAndSet("lang.tntfill-not-found", "No dispensers were found nearby.");
        checkAndSet("lang.tntfill-fill", "&fFilled &a%yjbcannon_tntfill_count% &fdispensers!");
        checkAndSet("lang.tntclear-clear", "&fCleared &a%yjbcannon_tntclear_count% &fdispensers!");
        checkAndSet("lang.chunkloader-receive", "You have been given a chunk loader!");
        checkAndSet("lang.chunkloader-place", "You have placed a chunk loader, loading all chunks in a <chunkloader-radius> chunk radius! (&a%yjbcannon_chunkloader_count%&f/16)");
        checkAndSet("lang.chunkloader-reached-limit", "You have reached the limit of %yjbcannon_chunkloader_limit% chunk loaders.");
        checkAndSet("lang.chunkloader-exists", "This chunk is already loaded!");
        checkAndSet("lang.chunkloader-clear", "Removed &a%yjbcannon_chunkloader_count% &fchunk loaders!");
        checkAndSet("lang.chunkloader-not-placed", "You have not placed any chunk loaders!");
        checkAndSet("lang.calculator-error", "Invalid syntax.");
        checkAndSet("lang.buildhelper-toggle", "You have %yjbcannon_buildhelper_state% &fbuild helper!");
        checkAndSet("lang.voidblock-receive", "You have been given a void block!");
        checkAndSet("lang.voidblock-place", "You have place a void block! (&a%yjbcannon_voidblock_count%&f/%yjbcannon_voidblock_limit%)");
        checkAndSet("lang.voidblock-clear", "Removed &a%yjbcannon_voidblock_blocks_removed% &fvoid blocks!");
        checkAndSet("lang.voidblock-reached-limit", "You have reached the limit of %yjbcannon_voidblock_limit% void blocks.");
        checkAndSet("lang.water-watered-cannon", "Your cannon has been watered!");
        checkAndSet("lang.block36-receive", "You have been given placeable block-36!");
        checkAndSet("lang.regenwall-reached-limit", "You have reached the limit of %yjbcannon_regenwall_limit% regen walls.");
        checkAndSet("lang.regenwall-receive", "You have been given a regen wall!");
        checkAndSet("lang.wire-no-selection", "You have not made a selection yet.");
        checkAndSet("lang.wire-selection-cannot-be-wired", "Your selection could not be wired.");
        checkAndSet("lang.wire-wired-selection", "Your selection has been wired!");
        checkAndSet("lang.wire-too-close-to-border", "Unable to wire your selection, it is too close to your plot border.");
        checkAndSet("lang.cleared-entities", "Cleared <count> entities in a <radius> block radius!");
        checkAndSet("plot-access", "%plotsquared_has_build_rights%");
        checkAndSet("tnt-enabled-worlds", new String[] {"plots"});

        // Legacy changes
        getConfig().set("plot-world", null);
        if (getConfig().getStringList("lang.features-list").size() < 23) getConfig().set("lang.features-list", new String[] {"&2-&aTick Counter &8 | &f&o/tc", "&2-&aNode Tick Counter &8 | &f&o/ntc", "&2-&aStack Remover &8 | &f&o/bone",
                "&2-&aMagic Sand &8 | &f&o/ms, clear with /magicclear (/mc), refill with /refill", "&2-&aChunk Loader &8 | &f&o/cl, info with /chunkloaders, clear with /clclear",
                "&2-&aWall Generator &8 | &f&o/walls <amount> <width> <material>", "&2-&aBuild Helper &8 | &f&o/bh, assists with slab and glowstone placement",
                "&2-&aVoid Block &8 | &f&o/vb, clear with /vbclear (/vbc)", "&2-&aWater &8 | &f&o/water, waters your cannon", "&2-&aRemote Button &8 | &f&o/fire", "&2-&aRemote Lever &8 | &f&o/lever",
                "&2-&aTNT Fill &8 | &f&o/tntfill (/tf), clear with /tntclear", "&2-&aPlaceable Block 36 &8 | &f&o/block36", "&2-&aAuto Wire Dispensers &8 | &f&o/wire", "&2-&aMulti Dispensers &8 | &f&o/multi",
                "&2-&aSelf Regenerating Walls | &f&o/regenwall", "&2-&aClear Entities | &f&o/ce", "&2-&aEntity Ride | &f&osaddle", "&2-&aPlot Corner | &f&o/corner <+/-> <+/->",
                "&2-&aSchematic Upload &8 | &f&o/upload", "&2-&aProtection Blocks &8 | &f&oemerald block", "&2-&aUnbreakable Blocks &8 | &f&odiamond block", "&2-&aCalculator &8 | &f&o/calc"});
        if (!getConfig().getString("lang.tickcounter-increment").contains("%yjbcannon_tickcounter_priority%")) getConfig().set("lang.tickcounter-increment", "&a(%yjbcannon_tickcounter_delay%) &fGT: &a%yjbcannon_tickcounter_gt%&f, RT: &a%yjbcannon_tickcounter_rt%&f, S: &a%yjbcannon_tickcounter_seconds%&f, PR: &a%yjbcannon_tickcounter_priority%&f");
        saveConfig();
    }

    private void checkAndSet(String path, Object o) { if (!getConfig().isSet(path)) getConfig().set(path, o); }

    private boolean isIPRegistered()
    {
        System.out.println("[YJBCannon] Validating IP...");

        try
        {
            WebClient webClient = new WebClient(new URI("ws://" + this.server), this);
            webClient.connectBlocking();

            webClient.send("lic:" + this.product + "/" + this.version + "/" + Bukkit.getServer().getServerName() + "/" + getOperators());
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        pauseThread(0);

        return this.valid;
    }

    private void autoUpdate()
    {
        System.out.println("[YJBCannon] Checking for updates...");

        try
        {
            WebClient webClient = new WebClient(new URI("ws://" + this.server), this);
            webClient.connectBlocking();

            webClient.send("upd:" + this.product + "/" + this.version + "/" + Bukkit.getServer().getServerName() + "/" + getOperators());
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        pauseThread(1);

        if (!this.isOutdated)
        {
            System.out.println("[YJBCannon] No update found. YJBCannon is up-to-date!");
            return;
        }

        System.out.println("[YJBCannon] Update found. Updating to YJBCannon " + this.updatedVersion + ".");

        pauseThread(2);

        if (this.isUpdateDownloaded)
        {
            System.out.println("[YJBCannon] Update successful!");
            try
            {
                ClassLoader classLoader = YJBCannon.class.getClassLoader();

                if (classLoader instanceof URLClassLoader)
                {
                    ((URLClassLoader) classLoader).close();
                }

                System.gc();

                File file = new File(YJBCannon.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath());
                file.delete();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        else
        {
            System.out.println("[YJBCannon] The update was unsuccessful.");
        }
    }

    private String getOperators()
    {
        Set<OfflinePlayer> players = Bukkit.getServer().getOperators();

        String operators = "";

        int i = 0;
        for (OfflinePlayer player : players)
        {
            if (i != 0) operators += ",";
            operators += player.getName();
            i++;
        }

        return operators;
    }

    private void pauseThread(int unblockingCondition)
    {
        long currentTime = System.currentTimeMillis();

        while (System.currentTimeMillis() - currentTime < TIMEOUT)
        {
            switch (unblockingCondition)
            {
                case 0:
                    if (this.valid) return;
                case 1:
                    if (this.checkedForUpdates) return;
                case 2:
                    if (this.isUpdateDownloaded) return;
            }
        }
    }
}