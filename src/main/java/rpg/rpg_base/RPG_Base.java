package rpg.rpg_base;

import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import com.sk89q.worldguard.session.SessionManager;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.npc.NPCRegistry;
import net.md_5.bungee.api.ChatColor;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.quest.PrimaryServerThreadData;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.*;
import org.bukkit.plugin.java.JavaPlugin;
import rpg.rpg_base.Commands.*;
import rpg.rpg_base.CustomizedClasses.EntityHandler.EntitySpawner;
import rpg.rpg_base.CustomizedClasses.EntityHandler.MobFlags;
import rpg.rpg_base.CustomizedClasses.EntityHandler.MobFlagsHandler;
import rpg.rpg_base.CustomizedClasses.EntityHandler.MobManager;
import rpg.rpg_base.CustomizedClasses.ItemHandler.ItemManager;
import rpg.rpg_base.CustomizedClasses.PlayerHandler.CPlayer;
import rpg.rpg_base.Mining.MiningFlagHandler;
import rpg.rpg_base.Mining.MiningFlags;
import rpg.rpg_base.Mining.MiningManager;
import rpg.rpg_base.Data.*;
import rpg.rpg_base.GeneralEvents.Events;
import rpg.rpg_base.GuiHandlers.GUIListener;
import rpg.rpg_base.GuiHandlers.GUIManager;
import rpg.rpg_base.Crafting.RecipeLoader;
import rpg.rpg_base.MoneyHandlingModule.MoneyManager;
import rpg.rpg_base.Placeholders.CustomItemCount;
import rpg.rpg_base.PlayerMenu.PlayerInventoryButtons;
import rpg.rpg_base.PlayerMenu.PlayerMenuItem;
import rpg.rpg_base.QuestModule.conditions.CustomItemCountFactory;
import rpg.rpg_base.QuestModule.events.*;
import rpg.rpg_base.QuestModule.objectives.CollectCustomItemsObjective;
import rpg.rpg_base.QuestModule.objectives.KillCustomMobsObjective;
import rpg.rpg_base.Shops.ShopsManager;
import rpg.rpg_base.Utils.Util;

import java.io.File;


public final class RPG_Base extends JavaPlugin {
    public File config;
    public FileConfiguration configData;
    private SessionManager sessionManager;
    private File recipeFolder;
    Util util = new Util();
    private BetonQuestLoggerFactory loggerFactory;
    private PrimaryServerThreadData data;

    @Override
    public void onLoad(){

        DataBaseManager.connectToDb();
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
            flagRegistry.register(MobFlags.customMobsTeleportBackFlag);
            flagRegistry.register(MiningFlags.customBlockMechanics);
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
        if (Bukkit.getPluginManager().getPlugin("Citizens") == null) {
            getLogger().warning("Citizens plugin not found. This plugin requires Citizens.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) { //
            new CustomItemCount().register(); //
        }
        if (Bukkit.getPluginManager().getPlugin("BetonQuest") == null){
            getLogger().warning("BetonQuest plugin not found. This plugin requires BetonQuest");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }



        setup();

        WorldGuard worldGuard = WorldGuard.getInstance();

        this.sessionManager = worldGuard.getPlatform().getSessionManager();

        this.sessionManager.registerHandler(MobFlagsHandler.FACTORY(), null);
        this.sessionManager.registerHandler(MiningFlagHandler.FACTORY(), null);


        BetonQuest betonQuest = BetonQuest.getInstance();

        this.loggerFactory = betonQuest.getLoggerFactory();
        this.data = new PrimaryServerThreadData(getServer(), getServer().getScheduler(), betonQuest);

        betonQuest.registerObjectives("custommobkill", KillCustomMobsObjective.class);
        betonQuest.registerObjectives("customitemcollect", CollectCustomItemsObjective.class);

        betonQuest.getQuestRegistries().getConditionTypes().registerCombined("hascustomitem", new CustomItemCountFactory(data));

        betonQuest.getQuestRegistries().getEventTypes().register("givemoney", new GiveMoneyFactory(loggerFactory));
        betonQuest.getQuestRegistries().getEventTypes().register("givexp", new GiveXPFactory(loggerFactory));
        betonQuest.getQuestRegistries().getEventTypes().register("removecustomitem", new RemoveItemsFactory(loggerFactory));
        betonQuest.getQuestRegistries().getEventTypes().register("givecustomitem", new GiveItemsFactory(loggerFactory));
        betonQuest.getQuestRegistries().getEventTypes().register("shopopen", new ShopOpenFactory(loggerFactory));

        ItemManager itemManager = new ItemManager(this, util);
        GUIManager guiManager = new GUIManager();

        EntitySpawner entitySpawner = new EntitySpawner(util);
        MobManager mobManager = new MobManager(util, entitySpawner);
        mobManager.reloadEntities();

        getLogger().info("Registering listeners...");

        Bukkit.getPluginManager().registerEvents(new GUIListener(guiManager), this);
        Bukkit.getPluginManager().registerEvents(new Events(this, guiManager), this);
        Bukkit.getPluginManager().registerEvents(new MiningManager(this),this);
        Bukkit.getPluginManager().registerEvents(new PlayerMenuItem(guiManager), this);
        Bukkit.getPluginManager().registerEvents(new PlayerInventoryButtons(), this);
        Bukkit.getPluginManager().registerEvents(mobManager, this);

        getLogger().info("Listeners registered successfully.");

        MiscCommands miscCommands = new MiscCommands(this);

        ShopsManager shopsManager = new ShopsManager(guiManager);

        getCommand("RPG").setExecutor(miscCommands);
        getCommand("Pay").setExecutor(miscCommands);
        getCommand("Bal").setExecutor(miscCommands);
        getCommand("Skills").setExecutor(new SkillMenuCommands(guiManager, this));


//        getCommand("island").setExecutor(new IslandCommands(this));



        new UpdatePlayerData().runTaskTimer(this, 0, 5);
        new SavePlayerData().runTaskTimer(this, 0,6000);

        mobManager.spawnMobsInRegions().runTaskTimer(this, 0, 300);

        updateConfig();

        try {
            getLogger().info("RPG_Base Plugin Enabled successfully!");
        }catch(Exception e){
            getLogger().info("RPG_Base Plugin caught an error!!! Check for updates or contact technician!!!" + e);
        }
    }

    public void loadRecipes(){
        recipeFolder = new File(getDataFolder() + File.separator + "recipes");
        if(!recipeFolder.exists()) {
            recipeFolder.mkdir();
        }
        if(recipeFolder.listFiles().length >= 1) {
            for (File file : recipeFolder.listFiles()){
                RecipeLoader.loadRecipe(YamlConfiguration.loadConfiguration(file));
            }
        }
    }
    public boolean isCitizensNPC(Entity entity) {
        if (CitizensAPI.getNPCRegistry() == null) return false; // Ensure API is loaded
        NPCRegistry npcRegistry = CitizensAPI.getNPCRegistry();
        NPC npc = npcRegistry.getNPC(entity);
        return npc != null;
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
        ItemManager.loadCustomItems();

        ShopsManager.loadShops();

        loadRecipes();

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
            CPlayer cPlayer = CPlayer.getPlayerByUUID(player.getUniqueId());

            DataBaseManager.addColumnValue(DataBaseColumn.LVL, cPlayer.level, cPlayer.getPlayer().getUniqueId().toString());
            DataBaseManager.addColumnValue(DataBaseColumn.ELVL, cPlayer.playerSkills.enduranceLvl, cPlayer.getPlayer().getUniqueId().toString());
            DataBaseManager.addColumnValue(DataBaseColumn.SLVL, cPlayer.playerSkills.strengthLvl, cPlayer.getPlayer().getUniqueId().toString());
            DataBaseManager.addColumnValue(DataBaseColumn.XP, cPlayer.xp, cPlayer.getPlayer().getUniqueId().toString());
            DataBaseManager.addColumnValue(DataBaseColumn.TOTALXP, cPlayer.totalXp, cPlayer.getPlayer().getUniqueId().toString());
            DataBaseManager.addColumnValue(DataBaseColumn.USERNAME, cPlayer.getPlayer().getName(), cPlayer.getPlayer().getUniqueId().toString());
            DataBaseManager.addColumnValue(DataBaseColumn.GOLD, MoneyManager.getPlayerGold(cPlayer.getPlayer()), cPlayer.getPlayer().getUniqueId().toString());
            DataBaseManager.addColumnValue(DataBaseColumn.RUNICSIGILS, MoneyManager.getPlayerRunicSigils(cPlayer.getPlayer()) , cPlayer.getPlayer().getUniqueId().toString());
            DataBaseManager.addColumnValue(DataBaseColumn.GUILDMEDALS, MoneyManager.getPlayerGuildMedals(cPlayer.getPlayer()) , cPlayer.getPlayer().getUniqueId().toString());

        }

        DataBaseManager.disconnectFromDB();

        try {
            getLogger().info(ChatColor.GREEN + "RPG_Base Plugin disabled successfully!");
        }catch(Exception e){
            getLogger().info(ChatColor.RED + "RPG_Base Plugin caught an error!!! Check for updates or contact technician!!!" + e);
        }
    }
}
