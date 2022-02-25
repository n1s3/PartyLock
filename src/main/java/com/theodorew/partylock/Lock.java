package com.theodorew.partylock;

import com.alessiodp.parties.api.Parties;
import com.alessiodp.parties.api.interfaces.PartiesAPI;
import com.alessiodp.parties.api.interfaces.PartyPlayer;
import de.tr7zw.nbtapi.NBTTileEntity;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.block.Container;
import org.bukkit.block.Lockable;
import org.bukkit.block.data.BlockData;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class Lock implements CommandExecutor {

    private final Main pl;
    private final PartiesAPI api;

    public Lock(Main pl) {
        this.pl = pl;
        this.api = pl.getAPI();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender instanceof Player) {
            //Player sent cmd
            Player player = (Player) sender;
            if (api.isPlayerInParty(player.getUniqueId())) {
                Block block = player.getTargetBlock(5);
                if (pl.validContainer(block)) {
                    lockBlock(block, player);
                    player.sendMessage(ChatColor.DARK_GREEN + "Locked successfully!");
                }
            } else {
                player.sendMessage(ChatColor.RED + "You need to be in a party to lock containers...");
            }
        } else {
            //Console sent cmd
            pl.print(ChatColor.RED + "Console commands not supported at this time.");
        }
        return true;
    }

    private void lockBlock(Block block, Player player) {
        NBTTileEntity tent = new NBTTileEntity(block.getState());
        //tent.setString("Lock", "test");
        tent.setString("Lock", api.getPartyPlayer(player.getUniqueId()).getPartyName());
    }

    private UUID getPartyID(Player player){
        PartyPlayer partyPlayer = api.getPartyPlayer(player.getUniqueId());
        if (partyPlayer.getPartyId() == null) {
            UUID partyKey = UUID.randomUUID();
            partyPlayer.setPartyId(partyKey);
            return partyKey;
        }
        return partyPlayer.getPartyId();
    }
}