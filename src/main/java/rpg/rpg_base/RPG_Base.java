package rpg.rpg_base;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import rpg.rpg_base.Commands.EnduranceCommands;
import rpg.rpg_base.Commands.LevelCommands;
import rpg.rpg_base.Commands.SkillMenuCommands;
import rpg.rpg_base.GUIs.SkillGui;
import rpg.rpg_base.GeneralEvents.Events;
import rpg.rpg_base.GuiHandlers.GUIListener;
import rpg.rpg_base.GuiHandlers.GUIManager;
import rpg.rpg_base.StatManager.LevelManager;
import rpg.rpg_base.StatManager.UpdateStatsTask;
import rpg.rpg_base.StatManager.EnduranceManager;
import rpg.rpg_base.data.PlayerDataLoad;

import java.io.File;
import java.io.IOException;


public final class RPG_Base extends JavaPlugin {
    public File config;
    public FileConfiguration configData;
    @Override
    public void onEnable() {
        setup();

        GUIManager guiManager = new GUIManager();
        Events events = new Events();
        GUIListener guiListener = new GUIListener(guiManager);

        Bukkit.getPluginManager().registerEvents(guiListener, this);
        Bukkit.getPluginManager().registerEvents(new EnduranceManager(this), this);
        Bukkit.getPluginManager().registerEvents(events, this);

        EnduranceManager enduranceManager = new EnduranceManager(this);
        LevelManager skillPointHandler = new LevelManager(this);
        SkillGui skillGui = new SkillGui(this,enduranceManager, guiManager);

        getCommand("EnduranceLVLADD").setExecutor(new EnduranceCommands(this, enduranceManager));
        getCommand("EnduranceLVLREM").setExecutor(new EnduranceCommands(this, enduranceManager));
        getCommand("Skills").setExecutor(new SkillMenuCommands(guiManager, this, enduranceManager));
        getCommand("LevelAdd").setExecutor(new LevelCommands(this, skillPointHandler));

        new UpdateStatsTask().runTaskTimer(this, 0L, 0L);

        setBasicConfigs();

        try {
            getLogger().info("RPG_Base Plugin Enabled successfully!");
        }catch(Exception e){
            getLogger().info("RPG_Base Plugin catched an error!!! Check for updates or contact technician!!!" + e);
        }
    }
    public void setBasicConfigs() {
        LevelManager.UpdateLevelRules();
    }
    private void setup() {
        config = new File(this.getDataFolder(), "config.yml");

        if (!config.exists()) {
            config.getParentFile().mkdirs();
            this.saveDefaultConfig();
        }

        configData = YamlConfiguration.loadConfiguration(config);

        try {
            double configVersion = getConfig().getDouble("version");

            double pluginVersion = 1.1;
            if (configVersion != pluginVersion) {
                // Perform any necessary update logic here
                this.saveDefaultConfig();
                configData = YamlConfiguration.loadConfiguration(config);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public FileConfiguration getConfig() {
        return this.configData;
    }




    @Override
    public void onDisable() {
        for(Player player : Bukkit.getOnlinePlayers()) {

            File f = new File(PlayerDataLoad.getFolderPath(player) + "/stats.yml");
            FileConfiguration cfg = YamlConfiguration.loadConfiguration(f);

            cfg.set("stats.level", LevelManager.getPlayerLevel(player));
            cfg.set("stats.endurancelevel", EnduranceManager.Endurance_Lvl);
            cfg.set("stats.sp", LevelManager.getPlayerCurrentSkillPoints(player));
            cfg.set("stats.spentsp", LevelManager.getPlayerSpentSkillPoints(player));
            try {
                // Save the changes made to the cfg object
                cfg.save(f);
                System.out.println("Saved player data for: " + player.getName());
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Failed to save player data for: " + player.getName());
            }

        }

        try {
            getLogger().info("RPG_Base Plugin disabled successfully!");
        }catch(Exception e){
            getLogger().info("RPG_Base Plugin catched an error!!! Check for updates or contact technician!!!" + e);
        }
    }
}
