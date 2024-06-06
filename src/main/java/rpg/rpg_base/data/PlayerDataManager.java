package rpg.rpg_base.data;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import rpg.rpg_base.StatManager.LevelManager;

import java.io.File;
import java.io.IOException;

import static rpg.rpg_base.StatManager.EnduranceManager.getEndurance_lvl;
import static rpg.rpg_base.StatManager.StrengthManager.getStrength_lvl;


public class PlayerDataManager {

    public static String getFolderPath(Player p) {
        String folderPath = "plugins/RPG_Base/player/" + p.getUniqueId();
        File folder = new File(folderPath);

        if (!folder.exists()) {
            folder.mkdirs();
        }

        return folderPath;
    }
    public static void savePlayerData(Player p){
        File f = new File(PlayerDataManager.getFolderPath(p) + "/stats.yml");
        FileConfiguration cfg = YamlConfiguration.loadConfiguration(f);
        cfg.set("stats.level", LevelManager.getPlayerLevel(p));
        cfg.set("stats.endurancelevel", getEndurance_lvl(p));
        cfg.set("stats.strengthlevel", getStrength_lvl(p));
        cfg.set("stats.sp", LevelManager.getPlayerCurrentSkillPoints(p));
        cfg.set("stats.spentsp", LevelManager.getPlayerSpentSkillPoints(p));

        try {
            // Save the changes made to the cfg object
            cfg.save(f);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Failed to save player data for: " + p.getName());
        }
    }
}