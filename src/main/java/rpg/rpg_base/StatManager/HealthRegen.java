package rpg.rpg_base.StatManager;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class HealthRegen extends BukkitRunnable {
    private final Player player;
    private volatile boolean isRunning = false;

    public HealthRegen(Player player) {
        this.player = player;
    }

    @Override
    public void run() {
        isRunning = true;

        // Add health regeneration logic
        double currentHealth = HealthManager.getPlayerHealth(player);
        double regenAmount = HealthManager.getPlayerHealthRegen(player);
        double maxHealth = HealthManager.getPlayerMaxHealth(player);

        if (currentHealth + regenAmount > maxHealth) {
            HealthManager.addPlayerHealth(player, (int) (maxHealth - currentHealth));
        } else {
            HealthManager.addPlayerHealth(player, (int) regenAmount);
        }

        // Check if the player's health is at max
        if (HealthManager.getPlayerHealth(player) >= maxHealth) {
            isRunning = false;
            cancel();  // Cancel the task and it will be removed from the scheduler automatically
        }
    }

    public boolean isRunning() {
        return isRunning;
    }
}
