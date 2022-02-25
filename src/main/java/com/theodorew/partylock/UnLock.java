package com.theodorew.partylock;

import com.alessiodp.parties.api.interfaces.PartiesAPI;
import de.tr7zw.nbtapi.NBTTileEntity;

import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.block.Lockable;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class UnLock implements CommandExecutor {

    private final Main pl;
    private final PartiesAPI api;

    public UnLock(Main pl) {
        this.pl = pl;
        this.api = pl.getAPI();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (api.isPlayerInParty(player.getUniqueId())) {
                Block block = player.getTargetBlock(5);
                if (pl.validContainer(block)) {
                    if (unLock(block)) {
                        player.sendMessage(ChatColor.DARK_GREEN + "Unlocked successfully!");
                    } else {
                        player.sendMessage(ChatColor.DARK_RED + "This container isn't locked, so it can't be unlocked.");
                    }
                }
            }
        }
        return true;
    }

    private boolean unLock(Block block) {
        Lockable lockable = (Lockable) block.getState();
        if (lockable.isLocked()) {
            NBTTileEntity tent = new NBTTileEntity(block.getState());
            tent.setString("Lock", "");
            return true;
        }
        return false;
    }
}
