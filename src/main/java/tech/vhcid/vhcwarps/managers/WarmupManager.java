package tech.vhcid.vhcwarps.managers;

import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

public class WarmupManager {

    private final Set<UUID> player = new HashSet<>();

    public boolean isPlayerOnWarmup(UUID uuid){
        return this.player.contains(uuid);
    }

    public void addPlayerToWarmup(UUID uuid) {
        this.player.add(uuid);
    }
    public void removePlayerFromWarmup(UUID uuid) {
        this.player.remove(uuid);
    }

}
