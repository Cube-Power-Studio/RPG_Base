package rpg.rpg_base.IslandManager;

import java.util.*;

import org.bukkit.block.Biome;
import org.bukkit.entity.EntityType;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;
import org.bukkit.potion.PotionEffectType;

public class Settings {
    public enum GameType {
        RPGBASE
    }
    public final static String PERMPREFIX = "rpg.";
    // The island command
    public final static String ISLANDCOMMAND = "island";
    // The challenge command
    public final static String SPAWNCOMMAND = "spawn";
    // Admin command

    public static int resetWait;
    public static int resetLimit;
    public static int maxTeamSize;
    public static String worldName;
    public static int monsterSpawnLimit;
    public static int animalSpawnLimit;
    public static int waterAnimalSpawnLimit;
    /**
     * Default world protection settings
     */
    public static final Map<Island.SettingsFlag, Boolean> defaultWorldSettings = new HashMap<>();

    /**
     * Default island protection settings
     */
    public static final Map<Island.SettingsFlag, Boolean> defaultIslandSettings = new HashMap<>();
    /**
     * Default spawn protection settings
     */
    public static final Map<Island.SettingsFlag, Boolean> defaultSpawnSettings = new HashMap<>();
    /**
     * Visitors settings to show in the GUI
     */
    public static final Map<Island.SettingsFlag, Boolean> visitorSettings = new HashMap<>();
    // Flymode
    public static int flyTimeOutside;

    // Temporary Permissions
    public static List<String> temporaryPermissions;

    // System settings
    public static boolean allowChestDamage;
    public static boolean allowCreeperDamage;
    public static boolean allowCreeperGriefing;
    public static boolean allowEndermanGriefing;
    public static boolean allowPistonPush;
    public static boolean allowTNTDamage;
    public static boolean allowVisitorKeepInvOnDeath;
    public static boolean restrictWither;

    public static ItemStack[] chestItems;
    public static int islandDistance;
    public static int islandXOffset;
    public static int islandZOffset;
    public static int seaHeight;
    public static int islandProtectionRange;
    public static int abandonedIslandLevel;
    public static Double startingMoney;
    public static List<PotionEffectType> acidDamageType = new ArrayList<>();
    public static boolean resetMoney;
    public static boolean damageOps;
    public static boolean endermanDeathDrop;
    public static boolean onlyLeaderCanCoop;
    public static boolean coopIsRequest;

    // Invincible visitor
    public static boolean invincibleVisitors;
    public static HashSet<EntityDamageEvent.DamageCause> visitorDamagePrevention;

    // public static boolean ultraSafeBoats;
    public static boolean logInRemoveMobs;
    public static boolean islandRemoveMobs;
    public static int islandHeight;

    // Levels
    public static HashMap<MaterialData, Integer> blockLimits;
    public static HashMap<MaterialData, Integer> blockValues;

    // Challenge completion broadcast
    public static boolean broadcastMessages;
    // Nether world
    public static boolean clearInventory;
    // Use control panel for /island
    public static boolean useControlPanel;
    // Prevent /island when falling
    public static boolean allowTeleportWhenFalling;
    // Biomes
    public static double biomeCost;
    public static Biome defaultBiome;

    // Island reset commands
    public static List<String> resetCommands = new ArrayList<>();
    // Mob limits
    public static int breedingLimit;

    // Console shows teamchat messages
    public static boolean logTeamChat;

    // Animal Damage
    public static boolean damageChickens;

    // Use Economy
    public static boolean useEconomy;

    // Wait between being invited to same team island
    public static int inviteWait;

    // Use physics when pasting schematic blocks
    public static boolean usePhysics;

    // Use old display (chat instead of GUI) for Island top ten
    public static boolean displayIslandTopTenInChat;

    // Disable offline redstone
    public static boolean disableOfflineRedstone;

    // Falling blocked commands
    public static List<String> fallingCommandBlockList;
    public static List<String> leaveCommands;
    public static long resetConfirmWait;
    public static boolean resetEnderChest;
    public static EntityType islandCompanion;
    public static boolean updateCheck;
    public static List<String> companionNames;
    public static long islandStartX;
    public static long islandStartZ;
    public static int maxHomes;
    public static boolean immediateTeleport;
    public static boolean makeIslandIfNone;
    public static boolean setTeamName;
    public static boolean useSchematicPanel;
    public static boolean chooseIslandRandomly;
    public static double underWaterMultiplier;
    public static String teamSuffix;
    public static int levelCost;
    public static boolean respawnOnIsland;
    public static int maxTeamSizeVIP;
    public static int maxTeamSizeVIP2;
    public static boolean teamChat;
    public static List<String> startCommands;
    public static boolean useWarpPanel;
    public static List<EntityType> mobWhiteList = new ArrayList<EntityType>();
    public static List<String> visitorCommandBlockList;
    public static boolean muteDeathMessages;
    public static int maxIslands;
    public static HashMap<String,Integer> limitedBlocks;
    public static long pvpRestartCooldown;
    public static long backupDuration;
    public static List<String> freeLevels = new ArrayList<String>();
    public static int cleanRate;
    public static boolean allowPushing;
    public static boolean recoverSuperFlat;
    protected static boolean levelLogging;
    public static boolean persistantCoops;
    //public static boolean allowSpawnCreeperPain;
    public static List<String> teamStartCommands;
    public static int minNameLength;
    public static int maxNameLength;
    public static int deathpenalty;
    public static boolean sumTeamDeaths;
    public static int maxDeaths;
    public static boolean islandResetDeathReset;
    public static boolean teamJoinDeathReset;
    public static List<String> allowedFakePlayers;
    //public static boolean allowSpawnVillagerTrading;
    public static String chatLevelPrefix;
    public static String chatIslandPlayer;
    public static boolean allowObsidianScooping;
    public static boolean allowFireExtinguish;
    //public static boolean allowSpawnFireExtinguish;
    public static boolean allowMobDamageToItemFrames;
    public static boolean kickedKeepInv;
    public static boolean hackSkeletonSpawners;
    public static HashMap<EntityType, Integer> entityLimits;
    public static boolean helmetProtection;
    public static boolean fullArmorProtection;
    public static String defaultLanguage;
    public static boolean showInActionBar;
    public static boolean leaversLoseReset;
    public static int maxPurge;
    public static boolean allowTNTPushing;
    public static boolean silenceCommandFeedback;
    public static long inviteTimeout;
    public static boolean warpHeads;
    public static boolean saveEntities;
    public static boolean coopsCanCreateWarps;
    public static boolean deleteProtectedOnly;
}