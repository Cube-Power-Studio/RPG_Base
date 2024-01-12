package rpg.rpg_base.data;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import rpg.rpg_base.RPG_Base;

public class SavePlayerData extends BukkitRunnable {
    @Override
    public void run() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            PlayerDataManager.savePlayerData(player);
        }
        RPG_Base.getInstance().getLogger().info("Player Data Saved!");
    }
}
