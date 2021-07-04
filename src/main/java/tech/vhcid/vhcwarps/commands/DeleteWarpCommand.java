package tech.vhcid.vhcwarps.commands;

import tech.vhcid.vhcwarps.Vhcwarps;
import tech.vhcid.vhcwarps.managers.WarpManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
public class DeleteWarpCommand implements CommandExecutor, TabCompleter {

    private final Vhcwarps plugin;

    public DeleteWarpCommand(Vhcwarps plugin) {
        this.plugin = plugin;
    }


    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {

        if (!(sender instanceof Player)) return true;
        WarpManager warpManager = plugin.getWarpManager();

        if (args.length < 1) {
            sender.sendMessage(this.color("&cGunakan: /delWarp <warpName>"));
            return true;
        }

        Player player = (Player) sender;
        String warpName = args[0];

        if (warpManager.getWarpByName(warpName) == null) {
            sender.sendMessage(this.color("&cTidak Ada Warp Dengan Nama Tersebut"));
            return true;
        }

        warpManager.deleteWarpByName(warpName);

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
            player.sendTitle(this.color("&c&lWarps Dihapus!"), this.color("&7Kamu Telah Menghapus Warp Dengan Nama &f&n" + warpName), 15, 50, 15);
        });

        return false;
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {

        WarpManager warpManager = plugin.getWarpManager();

        if (args.length == 1) {
            return warpManager.getWarpListByName();
        }

        return null;
    }

    private String color(String s) {
        return ChatColor.translateAlternateColorCodes('&', s);
    }
}