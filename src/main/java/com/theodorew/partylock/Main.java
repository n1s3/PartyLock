package com.theodorew.partylock;

import com.alessiodp.parties.api.Parties;
import com.alessiodp.parties.api.interfaces.PartiesAPI;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {

    private PartiesAPI api;

    //TODO
    //Double chest locking
    //Multi lock -- lock multiple chests at once with a punch

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

    public void print(String msg){
        getLogger().info(msg);
    }

    public PartiesAPI getAPI() {
        return api;
    }

    @Override
    public void onDisable() {
        print(ChatColor.GOLD + "Goodbye!");
    }
}
