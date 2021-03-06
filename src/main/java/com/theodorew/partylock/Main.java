package com.theodorew.partylock;

import com.alessiodp.parties.api.Parties;
import com.alessiodp.parties.api.interfaces.PartiesAPI;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.UUID;

public final class Main extends JavaPlugin {

    private PartiesAPI api;
    private ArrayList<UUID> multiLockEnabled = new ArrayList<UUID>();
    private static final String multi_lock_prefix = ChatColor.GRAY + "" + ChatColor.BOLD + "[" + ChatColor.DARK_AQUA + ChatColor.BOLD + "MULTI LOCK" + ChatColor.GRAY + ChatColor.BOLD + "] " + ChatColor.RESET;
    private static final String lock_prefix = ChatColor.GRAY + "" + ChatColor.BOLD + "[" + ChatColor.DARK_AQUA + ChatColor.BOLD + "LOCK" + ChatColor.GRAY + ChatColor.BOLD + "] " + ChatColor.RESET;

    //TODO
    //Double chest locking

    @Override
    public void onEnable() {
        if (getServer().getPluginManager().getPlugin("Parties") != null) {
            if (getServer().getPluginManager().getPlugin("Parties").isEnabled()) {
                print(ChatColor.DARK_GREEN + "Hooked into Parties.");
                api = Parties.getApi();
            } else {
                print(ChatColor.RED + "Missing Parties, this is a required dependency.");
            }
        }
        if (getServer().getPluginManager().getPlugin("NBTAPI") != null) {
            if (getServer().getPluginManager().getPlugin("NBTAPI").isEnabled()) {
                print(ChatColor.DARK_GREEN + "Hooked into NBTAPI");
            } else {
                print(ChatColor.RED + "Missing NBTAPI, this is a required dependency.");
            }
        }
        this.getCommand("lock").setExecutor(new Lock(this));
        this.getCommand("unlock").setExecutor(new UnLock(this));
        this.getCommand("mlock").setExecutor(new MLock(this));
        getServer().getPluginManager().registerEvents(new ContainerListener(this), this);
    }

    public boolean validContainer(Block block) {
        switch (block.getType()) {
            case DISPENSER:
            case DROPPER:
            case CHEST:
            case BARREL:
                return true;
        }
        return false;
    }

    public String lockPrefix(String string) { return lock_prefix + string; }

    public String multiLockPrefix(String string) { return multi_lock_prefix + string; }

    public void print(String msg){
        getLogger().info(msg);
    }

    @Override
    public void onDisable() {
        print(ChatColor.GOLD + "Goodbye!");
    }

    public PartiesAPI getAPI() {
        return this.api;
    }

    public ArrayList<UUID> getMultiLockEnabled() { return this.multiLockEnabled; }

    public void setMultiLockEnabled(ArrayList<UUID> multiLockEnabled) { this.multiLockEnabled = multiLockEnabled; }
}
