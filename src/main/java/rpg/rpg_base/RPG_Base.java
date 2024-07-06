package rpg.rpg_base;

import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.session.SessionManager;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import rpg.rpg_base.Commands.*;
import rpg.rpg_base.CustomItemsManager.ItemHandlers;
import rpg.rpg_base.CustomMining.MiningFlagHandler;
import rpg.rpg_base.CustomMining.MiningFlags;
import rpg.rpg_base.CustomMining.MiningManager;
import rpg.rpg_base.CustomMobs.*;
import rpg.rpg_base.GeneralEvents.ChatListener;
import rpg.rpg_base.GeneralEvents.Events;
import rpg.rpg_base.GuiHandlers.GUIListener;
import rpg.rpg_base.GuiHandlers.GUIManager;
import rpg.rpg_base.IslandManager.*;
import rpg.rpg_base.IslandManager.events.IslandDeleteEvent;
import rpg.rpg_base.IslandManager.events.IslandPreDeleteEvent;
import rpg.rpg_base.StatManager.*;
import rpg.rpg_base.data.PlayerCashe;
import rpg.rpg_base.data.SavePlayerData;
import rpg.rpg_base.data.UpdatePlayerData;
import rpg.rpg_base.data.PlayerDataManager;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


public final class RPG_Base extends JavaPlugin {
    public File config;
    public FileConfiguration configData;
    private SessionManager sessionManager;
    public static World islandWorld;
    private PlayerCashe players;
    private IslandGrid grid;
    private ChatListener chatListener;
    private File playersFolder;
    private boolean newIsland = false;
    private Messages messages;
    private Map<String,RPGlocale> availableLocales = new HashMap<>();

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
        setup();

        WorldGuard worldGuard = WorldGuard.getInstance();

        this.sessionManager = worldGuard.getPlatform().getSessionManager();

        this.sessionManager.registerHandler(MobFlagsHandler.FACTORY(), null);
        this.sessionManager.registerHandler(MiningFlagHandler.FACTORY(), null);

        ItemHandlers itemHandlers = new ItemHandlers(this);
        GUIManager guiManager = new GUIManager();
        Events events = new Events(this, guiManager);
        GUIListener guiListener = new GUIListener(guiManager);
        MobManager mobManager = new MobManager(this);
        MiningManager miningManager = new MiningManager(this);

        Bukkit.getPluginManager().registerEvents(guiListener, this);
        Bukkit.getPluginManager().registerEvents(new EnduranceManager(this), this);
        Bukkit.getPluginManager().registerEvents(events, this);
        Bukkit.getPluginManager().registerEvents(mobManager, this);
        Bukkit.getPluginManager().registerEvents(miningManager, this);

        StrengthManager strengthManager = new StrengthManager(this);
        HealthManager healthManager = new HealthManager(this);
        EnduranceManager enduranceManager = new EnduranceManager(this);
        LevelManager skillPointHandler = new LevelManager(this);

        itemHandlers.loadCustomItems();

        getCommand("RPG").setExecutor(new MiscCommands(this, itemHandlers));
        getCommand("Skills").setExecutor(new SkillMenuCommands(guiManager, this, enduranceManager));
//        getCommand("island").setExecutor(new IslandCommands(this));

        setBasicConfigs();

        new UpdatePlayerData().runTaskTimer(this, 0, 5);
        new SavePlayerData().runTaskTimer(this, 0,6000);
        new StatUpdates(this).runTaskTimer(this,0,5);
        new MobSpawningTask(this).run();

        playersFolder = new File(getDataFolder() + File.separator + "player");
        if (!playersFolder.exists()) {
            playersFolder.mkdir();
        }

        try {
            getLogger().info("RPG_Base Plugin Enabled successfully!");
        }catch(Exception e){
            getLogger().info("RPG_Base Plugin catched an error!!! Check for updates or contact technician!!!" + e);
        }
    }

    public void setBasicConfigs() {
        LevelManager.UpdateLevelRules();
        EnduranceManager.updateEnduranceRules();
        StrengthManager.updateStrengthRules();
    }

    public RPGlocale myLocale(UUID player) {
        String locale = players.getLocale(player);
        if (locale.isEmpty() || !availableLocales.containsKey(locale)) {
            return availableLocales.get(Settings.defaultLanguage);
        }
        return availableLocales.get(locale);
    }
    private void setup() {
        config = new File(this.getDataFolder(), "config.yml");

        if (!config.exists()) {
            config.getParentFile().mkdirs();
            this.saveDefaultConfig();
        }

        configData = YamlConfiguration.loadConfiguration(config);
        if(Bukkit.getWorld(configData.getString("IslandWorld"))!=null){
            islandWorld = Bukkit.getWorld(configData.getString("IslandWorld"));
        }
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

    public void deletePlayerIsland(final UUID player, boolean removeBlocks) {
        // Removes the island
        //getLogger().info("DEBUG: deleting player island");
        CoopPlay.getInstance().clearAllIslandCoops(player);
        Island island = grid.getIsland(player);
        if (island != null) {
            getServer().getPluginManager().callEvent(new IslandPreDeleteEvent(player, island));
            if (removeBlocks) {
                grid.removePlayersFromIsland(island, player);
                new DeleteIslandChunk(this, island);
                //new DeleteIslandByBlock(this, island);
            } else {
                island.setLocked(false);
                grid.setIslandOwner(island, null);
            }
            getServer().getPluginManager().callEvent(new IslandDeleteEvent(player, island.getCenter()));
        } else {
            getLogger().severe("Could not delete player: " + player.toString() + " island!");
            getServer().getPluginManager().callEvent(new IslandDeleteEvent(player, null));
        }
        players.zeroPlayerData(player);
    }


    /**
     * @return the grid
     */
    public IslandGrid getGrid() {
        /*
	if (grid == null) {
	    grid = new GridManager(this);
	}*/
        return grid;
    }

    @SuppressWarnings("deprecation")
    public void resetPlayer(Player player) {
        // getLogger().info("DEBUG: clear inventory = " +
        // Settings.clearInventory);
        if (Settings.clearInventory
                && (player.getWorld().getName().equalsIgnoreCase(Settings.worldName) || player.getWorld().getName()
                .equalsIgnoreCase(Settings.worldName + "_nether"))) {
            // Clear their inventory and equipment and set them as survival
            player.getInventory().clear(); // Javadocs are wrong - this does not
            // clear armor slots! So...
            player.getInventory().setArmorContents(null);
            player.getInventory().setHelmet(null);
            player.getInventory().setChestplate(null);
            player.getInventory().setLeggings(null);
            player.getInventory().setBoots(null);
            player.getEquipment().clear();
        }
        if (!player.isOp()) {
            player.setGameMode(GameMode.SURVIVAL);
        }
        // Clear the starter island
        players.clearStartIslandRating(player.getUniqueId());
        // Save the player
        players.save(player.getUniqueId());
        // Update the inventory
        player.updateInventory();
        if (Settings.resetEnderChest) {
            // Clear any Enderchest contents
            final ItemStack[] items = new ItemStack[player.getEnderChest().getContents().length];
            player.getEnderChest().setContents(items);
        }
        // Clear any potion effects
        for (PotionEffect effect : player.getActivePotionEffects())
            player.removePotionEffect(effect.getType());
    }
    /**
     * @return the players
     */
    public PlayerCashe getPlayers() {
        if (players == null) {
            players = new PlayerCashe(this);
        }
        return players;
    }

    public ChatListener getChatListener(){
        return chatListener;
    }
    public Messages getMessages() {
        return messages;
    }
    public void setNewIsland(boolean newIsland) {
        this.newIsland = newIsland;
    }
    /**
     * @return the playersFolder
     */
    public File getPlayersFolder() {
        return playersFolder;
    }

    /**
     * @return the newIsland
     */
    public boolean isNewIsland() {
        return newIsland;
    }

    public static World getIslandWorld(){
        return islandWorld;
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
