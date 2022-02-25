package com.theodorew.partylock;

import com.alessiodp.parties.api.interfaces.PartiesAPI;
import de.tr7zw.nbtapi.NBTTileEntity;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

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

    //Lock container with party name
    private void lockBlock(Block block, Player player) {
        NBTTileEntity tent = new NBTTileEntity(block.getState());
        tent.setString("Lock", api.getPartyPlayer(player.getUniqueId()).getPartyName());
    }

    public Main getMain() { return pl; }

}