package rpg.rpg_base.data;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import rpg.rpg_base.GUIs.ActionBar;
import rpg.rpg_base.RPG_Base;
import rpg.rpg_base.StatManager.EnduranceManager;
import rpg.rpg_base.StatManager.HealthManager;
import rpg.rpg_base.StatManager.LevelManager;


public class UpdatePlayerData extends BukkitRunnable {

    @Override
    public void run() {
        Plugin rpgPlugin = Bukkit.getPluginManager().getPlugin("RPG_Base");

        // Check if the plugin is found
        if (rpgPlugin instanceof RPG_Base) {

            // Iterate over online players and update their stats
            for (Player player : Bukkit.getOnlinePlayers()) {
                LevelManager.UpdateLevel(player);
                ActionBar.statisticBar();
                PlayerDataManager.savePlayerData(player);

                if(HealthManager.getPlayerHealth(player) <= 0 ){
                    HealthManager.setPlayerHealth(player, 0);
                }
                if(EnduranceManager.getEndurance_lvl(player)<0){
                    EnduranceManager.setEndurance_lvl(player, 0);
                }
            }
        } else {
            // Print a warning if the plugin is not found
            Bukkit.getLogger().warning("RPG_Base plugin not found!");
        }

    }
}
