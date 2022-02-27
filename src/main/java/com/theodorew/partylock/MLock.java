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
            if (pl.getAPI().isPlayerInParty(player.getUniqueId())) {
                if (!pl.getMultiLockEnabled().contains(player.getUniqueId())) {
                    ArrayList<UUID> tmp = pl.getMultiLockEnabled();
                    tmp.add(player.getUniqueId());
                    pl.setMultiLockEnabled(tmp);
                    player.sendMessage(pl.multiLockPrefix(ChatColor.DARK_GREEN + "" + ChatColor.BOLD + "Enabled!"));
                } else {
                    player.sendMessage(pl.multiLockPrefix(ChatColor.DARK_RED + "Disabled!"));
                }
             } else {
                player.sendMessage(pl.multiLockPrefix(ChatColor.RED + "You need to be in a party to lock containers"));
            }
        }
        return true;
    }
}
