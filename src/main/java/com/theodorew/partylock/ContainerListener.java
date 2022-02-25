package com.theodorew.partylock;

import de.tr7zw.nbtapi.NBTCompound;
import de.tr7zw.nbtapi.NBTTileEntity;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.block.Container;
import org.bukkit.block.Lockable;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.BlockInventoryHolder;

import java.util.ArrayList;
import java.util.UUID;

public class ContainerListener implements Listener {

    private final Main pl;

    public ContainerListener(Main pl) {
        this.pl = pl;
    }

    @EventHandler
    //Re-lock container on close, if it was previously locked
    public void onContainerClose(InventoryCloseEvent e) {
        if (e.getInventory().getHolder() instanceof BlockInventoryHolder) {
            BlockInventoryHolder holder = (BlockInventoryHolder) e.getInventory().getHolder();
            if (pl.validContainer(holder.getBlock())) {
                NBTTileEntity tent = new NBTTileEntity(holder.getBlock().getState());
                NBTCompound comp = tent.getPersistentDataContainer();
                if (comp.getBoolean("wasLocked")) {
                    tent.setString("Lock", pl.getAPI().getPartyPlayer(e.getPlayer().getUniqueId()).getPartyName());
                    comp.setBoolean("wasLocked", false);
                }
            }
        }
    }

    @EventHandler
    //Unlock chest when valid player opens
    public void onPlayerInteract(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        switch (e.getAction()) {
            case RIGHT_CLICK_BLOCK:
                cancelMultiLock(player);
                if (isLocked(e.getClickedBlock())) {
                    NBTTileEntity tent = new NBTTileEntity(e.getClickedBlock().getState());
                    NBTCompound comp = tent.getPersistentDataContainer();
                    if (isActiveParty(tent.getString("Lock"))) {
                        if (checkKey(player, e.getClickedBlock())) {
                            tent.setString("Lock", "");
                            comp.setBoolean("wasLocked", true);
                        }
                    } else {
                        //Unlock if party disbanded
                        tent.setString("Lock", "");
                        comp.setBoolean("wasLocked", false);
                    }
                }
                break;
            case LEFT_CLICK_BLOCK:
                //Lock if block is container and player is in multi lock mode
                if (pl.validContainer(e.getClickedBlock())) {
                    if (pl.getMultiLockEnabled().contains(player.getUniqueId())) {
                        String blockName =  ChatColor.GOLD + "" + ChatColor.ITALIC + e.getClickedBlock().getType();
                        NBTTileEntity tent = new NBTTileEntity(e.getClickedBlock().getState());
                        if (!checkKey(player, e.getClickedBlock())) {
                            tent.setString("Lock", pl.getAPI().getPartyPlayer(player.getUniqueId()).getPartyName());
                            player.sendMessage(pl.getPrefix() + blockName + ChatColor.DARK_GREEN + " is now locked!");
                        } else {
                            player.sendMessage(pl.getPrefix() + blockName + ChatColor.DARK_RED + " is already locked!");
                        }
                    }
                }
                break;
        }
    }

    @EventHandler
    //Prevent breaking a locked container
    public void onBlockBreak(BlockBreakEvent e) {
        if (e.getBlock() instanceof Container) {
            NBTTileEntity tent = new NBTTileEntity(e.getBlock().getState());
            ArrayList<UUID> tmp = pl.getMultiLockEnabled();
            if (isActiveParty(tent.getString("Lock")) || tmp.contains(e.getPlayer().getUniqueId())) { //Deny container to be broken
                e.setCancelled(isLocked(e.getBlock()));
            }
        }
    }

    private boolean isLocked(Block block) {
        if (pl.validContainer(block)) {
            Lockable lockable = (Lockable) block.getState();
            return lockable.isLocked();
        }
        return false;
    }

    private boolean isActiveParty(String otherParty) {
        return pl.getAPI().getParty(otherParty) != null;
    }

    //Check if player party equals container owner party (key container is locked with)
    private boolean checkKey(Player player, Block block) {
        Lockable lockable = (Lockable) block.getState();
        return lockable.getLock().equals(pl.getAPI().getPartyPlayer(player.getUniqueId()).getPartyName());
    }

    private void cancelMultiLock(Player player) {
        ArrayList<UUID> tmp = pl.getMultiLockEnabled();
        if (!tmp.contains(player.getUniqueId())) { return; }
        tmp.remove(player.getUniqueId());
        pl.setMultiLockEnabled(tmp);
        player.sendMessage(pl.getPrefix() + ChatColor.GRAY + " is now disabled");
    }
}
