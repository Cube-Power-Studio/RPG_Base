package rpg.rpg_base.GeneralEvents;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.player.*;
import rpg.rpg_base.CustomMobs.MobLevelManager;
import rpg.rpg_base.CustomMobs.MobManager;
import rpg.rpg_base.GUIs.CraftingGui;
import rpg.rpg_base.GUIs.SkillGui;
import rpg.rpg_base.GuiHandlers.GUIManager;
import rpg.rpg_base.RPG_Base;
import rpg.rpg_base.StatManager.*;
import rpg.rpg_base.data.PlayerDataManager;
import java.io.File;
import java.io.IOException;

import static rpg.rpg_base.StatManager.EnduranceManager.HP_per_lvl;
import static rpg.rpg_base.StatManager.EnduranceManager.getEndurance_lvl;

public class Events implements Listener {
    private final RPG_Base plugin;
    private final GUIManager guiManager;

    public Events(RPG_Base plugin, GUIManager guiManager) {
        this.plugin = plugin;
        this.guiManager = guiManager;
    }

    @EventHandler
    private void onJoin(PlayerJoinEvent event){
//        DamageManager.setPlayerBaseDamage(event.getPlayer().getUniqueId(), StrengthManager.getStrength_dmg(event.getPlayer()));
        File f = new File(PlayerDataManager.getFolderPath(event.getPlayer()) + "/stats.yml");
        if(f.exists()){
            FileConfiguration cfg = YamlConfiguration.loadConfiguration(f);
            EnduranceManager.setEndurance_lvl(event.getPlayer(), cfg.getInt("stats.endurancelevel"));
            StrengthManager.setStrength_lvl(event.getPlayer(), cfg.getInt("stats.strengthlevel"));
            LevelManager.setPlayerCurrentSkillPoints(event.getPlayer(), cfg.getInt("stats.sp"));
            LevelManager.setPlayerLevel(event.getPlayer(), cfg.getInt("stats.level") );
            LevelManager.setPlayerSpentSkillPoints(event.getPlayer(), cfg.getInt("stats.spentsp"));
        }else{
            EnduranceManager.setEndurance_lvl(event.getPlayer(), 0);
            StrengthManager.setStrength_lvl(event.getPlayer(), 0);
            LevelManager.setPlayerLevel(event.getPlayer(), 1 );
            try {
                YamlConfiguration.loadConfiguration(f).save(f);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        HealthManager.setPlayerHealth(event.getPlayer(), HealthManager.getPlayerMaxHealth(event.getPlayer()));
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
        HealthManager.healthRegenTasks.remove(event.getPlayer().getUniqueId());
    }
    @EventHandler
    private void onDamage(EntityDamageByEntityEvent e){
        HealthManager.distributeDamage(e);
        Entity entity = e.getEntity();
        if (entity.getType().isAlive()) {
            entity.setCustomNameVisible(true);
            entity.setCustomName(null);
            entity.setCustomName(ChatColor.GOLD + "[" + MobLevelManager.getEntityLevel(entity.getUniqueId()) + "Lvl] - " + ChatColor.RESET + MobManager.mobNames.get(entity.getUniqueId()) + " " + ChatColor.RED + HealthManager.getEntityHealth(entity.getUniqueId()) + "/" + HealthManager.getEntityMaxHealth((entity.getUniqueId())));
        }
    }

    @EventHandler
    private void onSpawn(PlayerRespawnEvent event){
        HealthManager.setPlayerHealth(event.getPlayer(), HealthManager.getPlayerMaxHealth(event.getPlayer()));
    }

    @EventHandler
    private void onItemDamage(PlayerItemDamageEvent e){
        e.setCancelled(true);
    }

    @EventHandler
    private void onBlockClick(PlayerInteractEvent event){
        if(event.getClickedBlock() != null && event.getClickedBlock().getType().equals(Material.CRAFTING_TABLE)){
            event.setCancelled(true);
            this.guiManager.openGui(new CraftingGui(plugin), event.getPlayer());
        }
    }
}
