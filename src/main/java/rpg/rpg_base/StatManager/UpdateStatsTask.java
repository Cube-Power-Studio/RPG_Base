package rpg.rpg_base.StatManager;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import rpg.rpg_base.RPG_Base;

public class UpdateStatsTask extends BukkitRunnable {

    @Override
    public void run() {
        Plugin rpgPlugin = Bukkit.getPluginManager().getPlugin("RPG_Base");

        // Check if the plugin is found
        if (rpgPlugin instanceof RPG_Base) {

            // Iterate over online players and update their stats
            for (Player player : Bukkit.getOnlinePlayers()) {
                LevelManager.UpdateLevel(player);
                EnduranceManager.EnduranceStats(player);

            }
        } else {
            // Print a warning if the plugin is not found
            Bukkit.getLogger().warning("RPG_Base plugin not found!");
        }
        if(EnduranceManager.Endurance_Lvl<0){
            EnduranceManager.Endurance_Lvl = 0;
        }

    }
}
