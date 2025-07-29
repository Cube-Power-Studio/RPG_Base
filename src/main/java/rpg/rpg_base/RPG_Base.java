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
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import rpg.rpg_base.Crafting.RecipeLoader;
import rpg.rpg_base.CustomizedClasses.Entities.MobClasses.MobFlags;
import rpg.rpg_base.CustomizedClasses.Entities.MobClasses.MobFlagsHandler;
import rpg.rpg_base.CustomizedClasses.Entities.MobClasses.MobManager;
import rpg.rpg_base.CustomizedClasses.Entities.MobClasses.spawning.SpawnManager;
import rpg.rpg_base.CustomizedClasses.EntityHandler.CEntity;
import rpg.rpg_base.CustomizedClasses.MiningHandler.MiningFlagHandler;
import rpg.rpg_base.CustomizedClasses.MiningHandler.MiningFlags;
import rpg.rpg_base.CustomizedClasses.MiningHandler.MiningManager;
import rpg.rpg_base.CustomizedClasses.PlayerHandler.PlayerListeners;
import rpg.rpg_base.CustomizedClasses.PlayerHandler.SkillSystem.SkillRegistry;
import rpg.rpg_base.CustomizedClasses.items.ItemManager;
import rpg.rpg_base.Data.DataBaseManager;
import rpg.rpg_base.Data.PlayerDataManager;
import rpg.rpg_base.Data.SavePlayerData;
import rpg.rpg_base.Data.UpdatePlayerData;
import rpg.rpg_base.GUIs.player.menu.PlayerInventoryButtons;
import rpg.rpg_base.GUIs.player.menu.PlayerMenuItem;
import rpg.rpg_base.GeneralEvents.Events;
import rpg.rpg_base.GuiHandlers.GUIListener;
import rpg.rpg_base.GuiHandlers.GUIManager;
import rpg.rpg_base.Placeholders.CustomItemCount;
import rpg.rpg_base.QuestModule.conditions.CustomItemCountFactory;
import rpg.rpg_base.QuestModule.events.*;
import rpg.rpg_base.QuestModule.objectives.CollectCustomItemsObjectiveFactory;
import rpg.rpg_base.QuestModule.objectives.KillCustomMobsObjectiveFactory;
import rpg.rpg_base.Shops.ShopsManager;
import rpg.rpg_base.Utils.Util;

import java.io.File;


public final class RPG_Base extends JavaPlugin {
    private File config;
    private FileConfiguration configData;
    private final Util util = new Util();
    public GUIManager guiManager;

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

        SessionManager sessionManager = worldGuard.getPlatform().getSessionManager();

        sessionManager.registerHandler(MobFlagsHandler.FACTORY(), null);
        sessionManager.registerHandler(MiningFlagHandler.FACTORY(), null);


        BetonQuest betonQuest = BetonQuest.getInstance();

        BetonQuestLoggerFactory loggerFactory = betonQuest.getLoggerFactory();
        PrimaryServerThreadData data = new PrimaryServerThreadData(getServer(), getServer().getScheduler(), betonQuest);

        betonQuest.getQuestRegistries().objective().register("custommobkill", new KillCustomMobsObjectiveFactory());
        betonQuest.getQuestRegistries().objective().register("customitemcollect", new CollectCustomItemsObjectiveFactory());

        betonQuest.getQuestRegistries().condition().registerCombined("hascustomitem", new CustomItemCountFactory(data));

        betonQuest.getQuestRegistries().event().register("givemoney", new GiveMoneyFactory(loggerFactory));
        betonQuest.getQuestRegistries().event().register("givexp", new GiveXPFactory(loggerFactory));
        betonQuest.getQuestRegistries().event().register("removecustomitem", new RemoveItemsFactory(loggerFactory));
        betonQuest.getQuestRegistries().event().register("givecustomitem", new GiveItemsFactory(loggerFactory));
        betonQuest.getQuestRegistries().event().register("shopopen", new ShopOpenFactory(loggerFactory));

        guiManager = new GUIManager();

        getLogger().info("Registering listeners...");

        Bukkit.getPluginManager().registerEvents(new GUIListener(guiManager), this);
        Bukkit.getPluginManager().registerEvents(new Events(this, guiManager), this);
        Bukkit.getPluginManager().registerEvents(new MiningManager(this),this);
        Bukkit.getPluginManager().registerEvents(new PlayerMenuItem(guiManager), this);
        Bukkit.getPluginManager().registerEvents(new PlayerInventoryButtons(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerListeners(), this);
        Bukkit.getPluginManager().registerEvents(new MobManager(), this);

        getLogger().info("Listeners registered successfully.");

        ShopsManager shopsManager = new ShopsManager(guiManager);

        new UpdatePlayerData().runTaskTimer(this, 0, 2);
        new SavePlayerData().runTaskTimer(this, 0,6000);

        updateConfig();

        try {
            getLogger().info("RPG_Base Plugin Enabled successfully!");
        }catch(Exception e){
            getLogger().info("RPG_Base Plugin caught an error!!! Check for updates or contact technician!!!" + e);
        }
    }

    public void loadRecipes(){
        File recipeFolder = new File(getDataFolder() + File.separator + "recipes");
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

        SkillRegistry.registerAllSkills();
    }
    public void updateConfig(){
        ItemManager.loadItems(DataBaseManager.getItems());

        ShopsManager.loadShops();

        rpg.rpg_base.CustomizedClasses.Entities.MobClasses.MobManager.loadMobs();
        SpawnManager.loadNodes(DataBaseManager.getSpawningNodes());
        SpawnManager.spawnMobs();

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
            PlayerDataManager.savePlayerData(player);
        }

        for(CEntity entity : CEntity.customEntities.values()){
            if(entity.getEntity() != null) {
                entity.getEntity().remove();
            }
        }

        DataBaseManager.disconnectFromDB();

        try {
            getLogger().info(ChatColor.GREEN + "RPG_Base Plugin disabled successfully!");
        }catch(Exception e){
            getLogger().info(ChatColor.RED + "RPG_Base Plugin caught an error!!! Check for updates or contact technician!!!" + e);
        }
    }
}
