package rpg.rpg_base.GeneralEvents;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import rpg.rpg_base.StatManager.EnduranceManager;
import rpg.rpg_base.StatManager.HealthManager;
import rpg.rpg_base.StatManager.LevelManager;
import rpg.rpg_base.data.PlayerDataManager;
import java.io.File;
import java.io.IOException;

import static rpg.rpg_base.StatManager.EnduranceManager.HP_per_lvl;
import static rpg.rpg_base.StatManager.EnduranceManager.getEndurance_lvl;

public class Events implements Listener {

    @EventHandler
    private void onJoin(PlayerJoinEvent event){
        File f = new File(PlayerDataManager.getFolderPath(event.getPlayer()) + "/stats.yml");
        if(f.exists()){
            FileConfiguration cfg = YamlConfiguration.loadConfiguration(f);
            EnduranceManager.setEndurance_lvl(event.getPlayer(), cfg.getInt("stats.endurancelevel"));
            LevelManager.setPlayerCurrentSkillPoints(event.getPlayer(), cfg.getInt("stats.sp"));
            LevelManager.setPlayerLevel(event.getPlayer(), cfg.getInt("stats.level") );
            LevelManager.setPlayerSpentSkillPoints(event.getPlayer(), cfg.getInt("stats.spentsp"));
        }else{

            EnduranceManager.setEndurance_lvl(event.getPlayer(), 0);
            LevelManager.setPlayerLevel(event.getPlayer(), 1 );
            try {
                YamlConfiguration.loadConfiguration(f).save(f);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        for(int i = 0; i<getEndurance_lvl(event.getPlayer()); i ++) {
            HealthManager.addPlayerMaxHealth(event.getPlayer(), HP_per_lvl);
        }
        EnduranceManager.EnduranceStats(event.getPlayer());
    }

    @EventHandler
    private void onLeave(PlayerQuitEvent event) {
        File f = new File(PlayerDataManager.getFolderPath(event.getPlayer()) + "/stats.yml");
        FileConfiguration cfg = YamlConfiguration.loadConfiguration(f);
        cfg.set("stats.level", LevelManager.getPlayerLevel(event.getPlayer()));
        cfg.set("stats.endurancelevel", getEndurance_lvl(event.getPlayer()));
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
    @EventHandler
    private void onDamage(EntityDamageEvent event){
        HealthManager.distributeDamage(event);
    }
    @EventHandler
    private void onSpawn(PlayerRespawnEvent event){
        HealthManager.setPlayerHealth(event.getPlayer(), HealthManager.getPlayerMaxHealth(event.getPlayer()));
    }
    @EventHandler
    private void onEntitySpawn(EntitySpawnEvent event){
        if(event.getEntity().getType().isAlive()) {
            HealthManager.setEntityHealth(event.getEntity().getUniqueId(), HealthManager.getEntityMaxHealth(event.getEntity().getUniqueId()));
        }
    }
}
