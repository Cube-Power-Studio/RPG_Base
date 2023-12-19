package rpg.rpg_base.GeneralEvents;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import rpg.rpg_base.StatManager.EnduranceManager;
import rpg.rpg_base.StatManager.LevelManager;
import rpg.rpg_base.data.PlayerDataLoad;

import java.io.File;
import java.io.IOException;

public class Events implements Listener {
    @EventHandler
    private void onJoin(PlayerJoinEvent event){
        File f = new File(PlayerDataLoad.getFolderPath(event.getPlayer()) + "/stats.yml");

        if(f.exists()){
            FileConfiguration cfg = YamlConfiguration.loadConfiguration(f);
            EnduranceManager.Endurance_Lvl = cfg.getInt("stats.endurancelevel");
            LevelManager.setPlayerCurrentSkillPoints(event.getPlayer(), cfg.getInt("stats.sp"));
            LevelManager.setPlayerLevel(event.getPlayer(), cfg.getInt("stats.level") );
            LevelManager.setPlayerSpentSkillPoints(event.getPlayer(), cfg.getInt("stats.spentsp"));
        }else{

            EnduranceManager.Endurance_Lvl = 0;
            LevelManager.setPlayerLevel(event.getPlayer(), 1 );
            try {
                YamlConfiguration.loadConfiguration(f).save(f);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    @EventHandler
    private void onLeave(PlayerQuitEvent event) {
        File f = new File(PlayerDataLoad.getFolderPath(event.getPlayer()) + "/stats.yml");
        FileConfiguration cfg = YamlConfiguration.loadConfiguration(f);
        cfg.set("stats.level", LevelManager.getPlayerLevel(event.getPlayer()));
        cfg.set("stats.endurancelevel", EnduranceManager.Endurance_Lvl);
        cfg.set("stats.sp", LevelManager.getPlayerCurrentSkillPoints(event.getPlayer()));
        cfg.set("stats.spentsp", LevelManager.getPlayerSpentSkillPoints(event.getPlayer()));

        try {
            // Save the changes made to the cfg object
            cfg.save(f);
            System.out.println("Saved player data for: " + event.getPlayer().getName());
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Failed to save player data for: " + event.getPlayer().getName());
        }

    }

}
