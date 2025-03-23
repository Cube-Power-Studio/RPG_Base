package rpg.rpg_base.Data;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import rpg.rpg_base.CustomizedClasses.ItemHandler.ItemManager;
import rpg.rpg_base.CustomizedClasses.PlayerHandler.CPlayer;
import rpg.rpg_base.RPG_Base;


public class UpdatePlayerData extends BukkitRunnable {

    @Override
    public void run() {
        Plugin rpgPlugin = Bukkit.getPluginManager().getPlugin("RPG_Base");

        // Check if the plugin is found
        if (rpgPlugin instanceof RPG_Base) {

            // Iterate over online players and update their stats
            for (Player player : Bukkit.getOnlinePlayers()) {

                PlayerDataManager.savePlayerData(player);
                CPlayer.getPlayerByUUID(player.getUniqueId()).updateStats();


            }
        } else {
            // Print a warning if the plugin is not found
            Bukkit.getLogger().warning("RPG_Base plugin not found!");
        }

    }
}
