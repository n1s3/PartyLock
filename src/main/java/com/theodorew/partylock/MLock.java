package com.theodorew.partylock;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.UUID;

public class MLock extends Lock implements CommandExecutor {

    private final Main pl;

    public MLock(Main pl) {
        super(pl);
        this.pl = getMain();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(sender instanceof Player) {
            Player player = (Player) sender;
            if (!pl.getMultiLockEnabled().contains(player.getUniqueId())) {
                //Enable mlock
                ArrayList<UUID> tmp = pl.getMultiLockEnabled();
                tmp.add(player.getUniqueId());
                pl.setMultiLockEnabled(tmp);
                player.sendMessage(pl.getPrefix() + ChatColor.DARK_GREEN + ChatColor.BOLD + "Enabled!");
            } else {
                player.sendMessage(pl.getPrefix() + ChatColor.DARK_RED + "already enabled.");
                player.sendMessage(pl.getPrefix() + ChatColor.DARK_RED + "right click any block to finish and exit.");
            }
        }
        return true;
    }
}
