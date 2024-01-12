package rpg.rpg_base;

import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.session.SessionManager;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import rpg.rpg_base.Commands.*;
import rpg.rpg_base.CustomItemsManager.ItemHandlers;
import rpg.rpg_base.CustomMining.MiningFlags;
import rpg.rpg_base.CustomMobs.MobFlags;
import rpg.rpg_base.CustomMobs.MobFlagsHandler;
import rpg.rpg_base.CustomMobs.MobManager;
import rpg.rpg_base.GeneralEvents.Events;
import rpg.rpg_base.GuiHandlers.GUIListener;
import rpg.rpg_base.GuiHandlers.GUIManager;
import rpg.rpg_base.StatManager.DamageManager;
import rpg.rpg_base.StatManager.HealthManager;
import rpg.rpg_base.StatManager.LevelManager;
import rpg.rpg_base.data.SavePlayerData;
import rpg.rpg_base.data.UpdatePlayerData;
import rpg.rpg_base.StatManager.EnduranceManager;
import rpg.rpg_base.data.PlayerDataManager;

import java.io.File;
import java.io.IOException;


public final class RPG_Base extends JavaPlugin {
    public File config;
    public FileConfiguration configData;
    private RegionContainer regionContainer;
    private SessionManager sessionManager;
    @Override
    public void onLoad(){

        WorldGuardPlugin worldGuardPlugin = (WorldGuardPlugin) this.getServer().getPluginManager().getPlugin("WorldGuard");


        if (worldGuardPlugin == null) {
            getLogger().severe("WorldGuard plugin not found. RPG_Base plugin disabled.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        WorldGuard worldGuard = WorldGuard.getInstance();

        try{
            FlagRegistry flagRegistry = worldGuard.getFlagRegistry();
            flagRegistry.register(MobFlags.customMobsFlag);
            flagRegistry.register(MiningFlags.customOreFlag);
            flagRegistry.register(MiningFlags.oreMiningFlag);
            flagRegistry.register(MiningFlags.customBlockMining);
        }catch (Exception e)
        {
            this.getServer().getPluginManager().disablePlugin(this);

            throw new RuntimeException(e instanceof IllegalStateException ?
                    "WorldGuard prevented flag registration. Did you reload the plugin? This is not supported!" :
                    "Flag registration failed!", e);
        }
    }
    @Override
    public void onEnable() {
        setup();

        WorldGuardPlugin worldGuardPlugin = (WorldGuardPlugin) this.getServer().getPluginManager().getPlugin("WorldGuard");

        WorldGuard worldGuard = WorldGuard.getInstance();

        this.regionContainer = worldGuard.getPlatform().getRegionContainer();
        this.sessionManager = worldGuard.getPlatform().getSessionManager();

        this.sessionManager.registerHandler(MobFlagsHandler.FACTORY(), null);


        DamageManager damageManager = new DamageManager();
        GUIManager guiManager = new GUIManager();
        Events events = new Events();
        GUIListener guiListener = new GUIListener(guiManager);
        MobManager mobManager = new MobManager();

        Bukkit.getPluginManager().registerEvents(damageManager, this);
        Bukkit.getPluginManager().registerEvents(guiListener, this);
        Bukkit.getPluginManager().registerEvents(new EnduranceManager(this), this);
        Bukkit.getPluginManager().registerEvents(events, this);
        Bukkit.getPluginManager().registerEvents(mobManager, this);

        EnduranceManager enduranceManager = new EnduranceManager(this);
        LevelManager skillPointHandler = new LevelManager(this);
        ItemHandlers itemHandlers = new ItemHandlers(this);
        HealthManager healthManager = new HealthManager(this);

        itemHandlers.loadCustomItems();

        getCommand("GiveItems").setExecutor(new GiveCommand(this, itemHandlers));
        getCommand("RPG").setExecutor(new MiscCommands(this));
        getCommand("EnduranceLVLADD").setExecutor(new EnduranceCommands(this, enduranceManager));
        getCommand("EnduranceLVLREM").setExecutor(new EnduranceCommands(this, enduranceManager));
        getCommand("Skills").setExecutor(new SkillMenuCommands(guiManager, this, enduranceManager));
        getCommand("LevelAdd").setExecutor(new LevelCommands(this, skillPointHandler));

        new UpdatePlayerData().runTaskTimer(this, 0L, 0L);
        new SavePlayerData().runTaskTimer(this, 0,6000);
        setBasicConfigs();

        try {
            getLogger().info("RPG_Base Plugin Enabled successfully!");
        }catch(Exception e){
            getLogger().info("RPG_Base Plugin catched an error!!! Check for updates or contact technician!!!" + e);
        }
    }
    public void setBasicConfigs() {
        LevelManager.UpdateLevelRules();
        EnduranceManager.updateEnduranceRules();
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
    public void updateConfig(){

        ItemHandlers itemHandlers = new ItemHandlers(this);

        itemHandlers.loadCustomItems();

        setBasicConfigs();
        config = new File(this.getDataFolder(), "config.yml");
        configData = YamlConfiguration.loadConfiguration(config);
    }
    public FileConfiguration getConfig() {
        return this.configData;
    }
    public static RPG_Base getInstance(){
        return getPlugin(RPG_Base.class);
    }




    @Override
    public void onDisable() {
        for(Player player : Bukkit.getOnlinePlayers()) {

            File f = new File(PlayerDataManager.getFolderPath(player) + "/stats.yml");
            FileConfiguration cfg = YamlConfiguration.loadConfiguration(f);

            cfg.set("stats.level", LevelManager.getPlayerLevel(player));
            cfg.set("stats.endurancelevel", EnduranceManager.getEndurance_lvl(player));
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
