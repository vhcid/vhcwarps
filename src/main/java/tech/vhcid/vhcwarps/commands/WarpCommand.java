package tech.vhcid.vhcwarps.commands;

import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tech.vhcid.vhcwarps.Vhcwarps;
import tech.vhcid.vhcwarps.managers.WarpManager;
import tech.vhcid.vhcwarps.managers.WarmupManager;
import tech.vhcid.vhcwarps.objects.Warp;
import java.util.ArrayList;
import java.util.List;

public class WarpCommand implements CommandExecutor, TabCompleter {

    private final Vhcwarps plugin;
    public WarpCommand(Vhcwarps plugin){
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {

        WarpManager warpManager = plugin.getWarpManager();
        WarmupManager warmupManager = plugin.getWarmupManager();

        if(args.length == 0){
            sender.sendMessage(this.color("&cGunakan: /warp <warpName> [<player>]"));
            return true;
        }

        if(args.length == 1){
            if(!(sender instanceof Player)) return true;

            String warpName = args[0];

            Warp warp = warpManager.getWarpByName(warpName);
            if(warp == null){
                sender.sendMessage(this.color("&cTidak Ada Warp Dengan Nama Itu!"));
                return true;
            }

            Player player = (Player) sender;

            if(warmupManager.isPlayerOnWarmup(player.getUniqueId())){
                player.sendMessage(this.color("&cKamu Sudah Terteleportasi... Tunggu Sebentar!"));
                return true;
            }

            warmupManager.addPlayerToWarmup(player.getUniqueId());

            new BukkitRunnable(){
                int counter = 6;
                @Override
                public void run(){

                    if(!warmupManager.isPlayerOnWarmup(player.getUniqueId())){
                        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                            player.sendMessage(color("&cJangan Bergerak! Teleportasimu akan di batalkan!"));
                            player.sendTitle("", "", 0, 20, 0);
                            player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_LAND, 1, 1);
                        });
                        this.cancel();
                        return;
                    }

                    if(counter == 1){
                        player.teleport(warp.getLocation());
                        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                            player.playSound(warp.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
                            warmupManager.removePlayerFromWarmup(player.getUniqueId());
                            player.sendTitle("", "", 0, 20, 0);
                        });
                        this.cancel();
                        return;
                    }

                    Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                        player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
                        player.sendTitle(color("&e&lMeneleportasikan.."), color("&7Kamu Akan Di Teleportasi Dalam &f&n" + counter), 0, 30, 0);
                    });

                    counter--;

                }

            }.runTaskTimer(plugin, 0L, 20L);
            return true;
        }

        if(args.length == 2){
            if(!(sender.hasPermission("vhcwarps.admin"))){
                sender.sendMessage(this.color("&cYou don't have enough permission!"));
                return true;
            }

            String warpName = args[0];

            Warp warp = warpManager.getWarpByName(warpName);
            if(warp == null){
                sender.sendMessage(this.color("&cThere is no warp with that name!"));
                return true;
            }

            Player player = Bukkit.getPlayer(args[1]);
            if(player == null){
                sender.sendMessage(this.color("&cThere is no players online with that name!"));
                return true;
            }

            player.teleport(warp.getLocation());
            return true;

        }

        return false;
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {

        WarpManager warpManager = plugin.getWarpManager();

        if(args.length == 1){
            return warpManager.getWarpListByName();
        }

        if(args.length == 2){
            List<String> suggestions = new ArrayList<>();
            for(Player online : Bukkit.getOnlinePlayers()){
                suggestions.add(online.getName());
            }
            return suggestions;
        }

        return null;
    }

    private String color(String s){
        return ChatColor.translateAlternateColorCodes('&', s);
    }

}
