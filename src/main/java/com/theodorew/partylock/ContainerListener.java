package com.theodorew.partylock;

import de.tr7zw.nbtapi.NBTCompound;
import de.tr7zw.nbtapi.NBTTileEntity;
import org.bukkit.block.Block;
import org.bukkit.block.Container;
import org.bukkit.block.Lockable;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.BlockInventoryHolder;

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
    public void onContainerOpen(PlayerInteractEvent e) {
        if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (isLocked(e.getClickedBlock())) {
                NBTTileEntity tent = new NBTTileEntity(e.getClickedBlock().getState());
                NBTCompound comp = tent.getPersistentDataContainer();
                if (isActiveParty(tent.getString("Lock"))) {
                    if (checkKey(e.getPlayer(), e.getClickedBlock())) {
                        tent.setString("Lock", "");
                        comp.setBoolean("wasLocked", true);
                    }
                } else {
                    //Unlock if party disbanded
                    tent.setString("Lock", "");
                    comp.setBoolean("wasLocked", false);
                }
            }
        }
    }

    @EventHandler
    //Prevent breaking a locked container
    public void onBlockBreak(BlockBreakEvent e) {
        if (e.getBlock() instanceof Container) {
            NBTTileEntity tent = new NBTTileEntity(e.getBlock().getState());
            if (isActiveParty(tent.getString("Lock"))) { //Allow container to be broken if party disbanded
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
}
