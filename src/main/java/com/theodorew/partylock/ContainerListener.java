package com.theodorew.partylock;

import com.alessiodp.parties.api.interfaces.PartiesAPI;
import com.alessiodp.parties.api.interfaces.PartyPlayer;
import de.tr7zw.nbtapi.NBTCompound;
import de.tr7zw.nbtapi.NBTTileEntity;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
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
    private final PartiesAPI api;

    public ContainerListener(Main pl) {
        this.pl = pl;
        this.api = pl.getAPI();
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
                    tent.setString("Lock", api.getPartyPlayer(e.getPlayer().getUniqueId()).getPartyName());
                    comp.setBoolean("wasLocked", false);
                }
            }
        }
    }

    @EventHandler
    //Unlock chest when valid player opens
    public void onPlayerInteract(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        Block block = e.getClickedBlock();
        switch (e.getAction()) {
            case RIGHT_CLICK_BLOCK:
                cancelMultiLock(player);
                if (isLocked(block)) {
                    NBTTileEntity tent = new NBTTileEntity(block.getState());
                    NBTCompound comp = tent.getPersistentDataContainer();
                    if (isActiveParty(tent.getString("Lock"))) {
                        if (checkKey(player, block)) {
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
                if (pl.validContainer(block)) {
                    if (pl.getMultiLockEnabled().contains(player.getUniqueId())) {
                        String blockName =  ChatColor.GOLD + "" + ChatColor.ITALIC + block.getType();
                        NBTTileEntity tent = new NBTTileEntity(block.getState());
                        if (!checkKey(player, block)) {
                            tent.setString("Lock", api.getPartyPlayer(player.getUniqueId()).getPartyName());
                            player.sendMessage(pl.multiLockPrefix(blockName + ChatColor.DARK_GREEN + ChatColor.BOLD + " locked!"));
                        } else {
                            player.sendMessage(pl.multiLockPrefix(blockName + ChatColor.DARK_RED + ChatColor.BOLD + " already locked!"));
                        }
                    }
                }
                break;
        }
    }

    @EventHandler
    //Prevent breaking a locked container if player isn't within the owning party
    public void onBlockBreak(BlockBreakEvent e) {
        Block block = e.getBlock();
        Player player = e.getPlayer();
        //Disable block breaking when multi lock mode is enabled.
        if (pl.getMultiLockEnabled().contains(player.getUniqueId())) { e.setCancelled(true); return; }
        if (isLocked(block)) {
            if (!checkKey(player, block)) {
                if(isActiveParty(player)) {
                    e.setCancelled(true);
                }
            }
        }
    }

    private boolean checkKey(Player player, Block block) {
        NBTTileEntity tent = new NBTTileEntity(block.getState());
        return tent.getString("Lock").equals(api.getPartyPlayer(player.getUniqueId()).getPartyName());
    }

    private boolean isLocked(Block block) {
        if (pl.validContainer(block)) {
            Lockable lockable = (Lockable) block.getState();
            return lockable.isLocked();
        }
        return false;
    }

    private boolean isActiveParty(String party) {
        return api.getParty(party) != null;
    }

    private boolean isActiveParty(Player player) {
        PartyPlayer partyPlayer = api.getPartyPlayer(player.getUniqueId());
        return api.getParty(partyPlayer.getPartyName()) != null;
    }

    private void cancelMultiLock(Player player) {
        ArrayList<UUID> tmp = pl.getMultiLockEnabled();
        if (!tmp.contains(player.getUniqueId())) { return; }
        tmp.remove(player.getUniqueId());
        pl.setMultiLockEnabled(tmp);
        player.sendMessage(pl.multiLockPrefix(ChatColor.GRAY + "" + ChatColor.BOLD + "Disabled!"));
    }
}
