package tech.vhcid.vhcwarps.managers;

import org.jetbrains.annotations.Nullable;
import tech.vhcid.vhcwarps.Vhcwarps;
import tech.vhcid.vhcwarps.objects.Warp;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class WarpManager {
    private final Map<String, Warp> warps = new HashMap<>();
    private final Vhcwarps plugin;
    public WarpManager (Vhcwarps plugin){
        this.plugin = plugin;
    }

    @Nullable
    public Warp getWarpByName(String name){
        return this.warps.get(name);
    }

    public List<String> getWarpListByName() {
        return new ArrayList<>(this.warps.keySet());
    }

    public void createWarp(String warpName, Location location){
        this.warps.put(warpName, new Warp(warpName, location, "vhcwarps.access." + warpName));
    }
    public void deleteWarpByName(String warpName){
        this.warps.remove(warpName);
    }
    public void clearWarp(){
        this.warps.clear();
    }
    public void serializeLocation(Warp warp){
        FileConfiguration config = plugin.getConfig();
        Location location = warp.getLocation().clone();

        String world = location.getWorld().getName();
        double x = location.getX();
        double y = location.getY();
        double z = location.getZ();
        double yaw = location.getYaw();
        double pitch = location.getPitch();

        config.set("warps." + warp.getName() + ".world", world);
        config.set("warps." + warp.getName() + ".x", x);
        config.set("warps." + warp.getName() + ".y", y);
        config.set("warps." + warp.getName() + ".z", z);
        config.set("warps." + warp.getName() + ".yaw", yaw);
        config.set("warps." + warp.getName() + ".pitch", pitch);

        plugin.saveConfig();
    }
    public void saveWarps(){
        for(String warpName : this.warps.keySet()){
            this.serializeLocation(this.warps.get(warpName));
        }
    }
    public void loadWarps(){
        FileConfiguration config = plugin.getConfig();
        if(!config.isConfigurationSection("warps")) return;

        System.out.println("[VHC Warps] Meload Semua warps yang tersimpan..");
        for(String warpName : config.getConfigurationSection("warps").getKeys(false)){

            World world = Bukkit.getWorld(config.getString("warps." + warpName + ".world"));
            double x = config.getDouble("warps." + warpName + ".x");
            double y = config.getDouble("warps." + warpName + ".y");
            double z = config.getDouble("warps." + warpName + ".z");
            float yaw = (float) config.getDouble("warps." + warpName + ".yaw");
            float pitch = (float) config.getDouble("warps." + warpName + ".pitch");

            Location location = new Location(world, x, y, z, yaw, pitch);

            String permission = "vhcwarps" + warpName;

            this.warps.put(warpName, new Warp(warpName, location, permission));
            System.out.println("[VHC Warps] Load Warp" + warpName);

        }
        config.set("warps", null);
        plugin.saveConfig();

        System.out.println("[VHC Warps] Berhasil Meload Semua Warps!");
    }
}
