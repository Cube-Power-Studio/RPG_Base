package rpg.rpg_base.GeneralEvents;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.yaml.snakeyaml.Yaml;
import rpg.rpg_base.StatManager.EnduranceManager;
import rpg.rpg_base.StatManager.SkillPointHandler;
import rpg.rpg_base.data.PlayerData;
import rpg.rpg_base.data.PlayerDataLoad;

import java.io.File;
import java.io.IOException;

public class Events implements Listener {
    @EventHandler
    private void onJoin(PlayerJoinEvent event){
        PlayerData data = PlayerDataLoad.getPlayerData(event.getPlayer());
        File f = new File(PlayerDataLoad.getFolderPath(event.getPlayer()) + "/stats.yml");

        if(f.exists()){
            FileConfiguration cfg = YamlConfiguration.loadConfiguration(f);
            data.setLevel(cfg.getInt("stats.level"));
            data.setEnduranceLevel(cfg.getInt("stats.endurancelevel"));
            data.setSP(cfg.getInt("stats.sp"));
            EnduranceManager.Endurance_Lvl = data.getEnduranceLevel();
            SkillPointHandler.SkillPoints = data.getSp();
            SkillPointHandler.level = data.getLevel();
        }else{
            data.setLevel(1);
            data.setSP(1);
            data.setEnduranceLevel(0);
            EnduranceManager.Endurance_Lvl = data.getEnduranceLevel();
            SkillPointHandler.SkillPoints = data.getSp();
            SkillPointHandler.level = data.getLevel();
            try {
                YamlConfiguration.loadConfiguration(f).save(f);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        PlayerDataLoad.setPlayerData(event.getPlayer(), data);

    }

    @EventHandler
    private void onLeave(PlayerQuitEvent event) {
        File f = new File(PlayerDataLoad.getFolderPath(event.getPlayer()) + "/stats.yml");
        FileConfiguration cfg = YamlConfiguration.loadConfiguration(f);
        cfg.set("stats.level", SkillPointHandler.level);
        cfg.set("stats.endurancelevel", EnduranceManager.Endurance_Lvl);
        cfg.set("stats.sp", SkillPointHandler.SkillPoints);

        try {
            // Save the changes made to the cfg object
            cfg.save(f);
            System.out.println("Saved player data for: " + event.getPlayer().getName());
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Failed to save player data for: " + event.getPlayer().getName());
        }

        PlayerDataLoad.setPlayerData(event.getPlayer(), null);
    }

}
