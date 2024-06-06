package rpg.rpg_base.StatManager;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

public class HealthRegen extends BukkitRunnable {
    private Player player;
    private volatile boolean isRunning = false;
    private volatile boolean isScheduled = false;

    public HealthRegen(Player player) {
        player = player;
    }

    @Override
    public void run() {
        isRunning = true;
        if (HealthManager.getPlayerHealth( player) + HealthManager.getPlayerHealthRegen(player) > HealthManager.getPlayerMaxHealth(player)) {
            HealthManager.addPlayerHealth( player, HealthManager.getPlayerMaxHealth(player) - HealthManager.getPlayerHealth(player));
        } else {
            HealthManager.addPlayerHealth( player, HealthManager.getPlayerHealthRegen(player));
        }
        if (HealthManager.getPlayerHealth( player) >= HealthManager.getPlayerMaxHealth(player)) {
            isRunning = false;
            if (isScheduled) {
                this.cancel();
            }
        }
    }

    @Override
    public synchronized void cancel() throws IllegalStateException {
        if (isScheduled) {
            isRunning = false;
            super.cancel();
        }
    }

    public boolean isRunning() {
        return isRunning;
    }

    public void setScheduled(boolean scheduled) {
        isScheduled = scheduled;
    }
}