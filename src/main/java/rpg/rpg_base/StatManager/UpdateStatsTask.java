package rpg.rpg_base.StatManager;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import rpg.rpg_base.GUIs.SkillGui;
import rpg.rpg_base.RPG_Base;
import rpg.rpg_base.data.PlayerData;
import rpg.rpg_base.data.PlayerDataLoad;

public class UpdateStatsTask extends BukkitRunnable {
    private final RPG_Base plugin;
    private final SkillGui skillGui;

    public UpdateStatsTask(RPG_Base plugin, SkillGui  skillGui) {
        this.plugin = plugin;
        this.skillGui = skillGui;
    }

    @Override
    public void run() {
        Plugin rpgPlugin = Bukkit.getPluginManager().getPlugin("RPG_Base");

        // Check if the plugin is found
        if (rpgPlugin instanceof RPG_Base) {
            RPG_Base rpgBase = (RPG_Base) rpgPlugin;

            // Iterate over online players and update their stats
            for (Player player : Bukkit.getOnlinePlayers()) {
                rpgBase.updateStats(player);
                SkillPointHandler.UpdateLevel();

                if (player.getOpenInventory().getTitle().equals("Skills")) {
                    skillGui.UpdateGui(player);

                }
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
