package rpg.rpg_base.StatManager;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import rpg.rpg_base.RPG_Base;

import java.util.HashMap;
import java.util.Map;

public class LevelManager {
    public static int LevelMax;
    public static int skillPointsPerLevel;
    public static double xpForNextLevelScale = 1.2;

    private static RPG_Base plugin;

    private static final Map<Player, Integer> totalXp = new HashMap<>();
    private static final Map<Player, Integer> xp = new HashMap<>();
    private static final Map<Player, Integer> xpToNextLevel = new HashMap<>();
    private static final Map<Player, Integer> previousLevels = new HashMap<>();
    private static final Map<Player, Integer> totalSkillPoints = new HashMap<>();
    private static final Map<Player, Integer> spentSkillPoints = new HashMap<>();
    private static final Map<Player, Integer> currentSkillPoints = new HashMap<>();
    private static final Map<Player, Integer> currentLevels = new HashMap<>();



    public LevelManager(RPG_Base plugin){
        LevelManager.plugin = plugin;
    }


    public static void UpdateLevelRules() {
        LevelMax = plugin.getConfig().getConfigurationSection("Levels").getInt("LevelMax");
        skillPointsPerLevel = plugin.getConfig().getConfigurationSection("Levels").getInt("SkillPointsPerLevel");
    }

    public static void updatePlayerLevel(Player player) {
        if(!xp.containsKey(player)){
            xp.put(player, 0);
            xpToNextLevel.put(player, 10);
        }
        if(xp.get(player) >= xpToNextLevel.get(player)){
            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
            addPlayer_lvl(player, 1);
            xpToNextLevel.put(player, Math.toIntExact(Math.round(xpToNextLevel.get(player) * xpForNextLevelScale)));
        }


        updateSkillPoints(player);
    }

    public static synchronized void updateSkillPoints(Player player) {
        int previousLevel = getPlayerPreviousLevel(player);
        if(totalSkillPoints.get(player)==null) {
            totalSkillPoints.put(player, getPlayerTotalSkillPoints(player));
        }
        if (getPlayerLevel(player) > previousLevel) {
            // Player leveled up
            int gainedLevels = getPlayerLevel(player)- previousLevel;
            int gainedSkillPoints = gainedLevels * skillPointsPerLevel;

            // Update player-specific skill points
            totalSkillPoints.put(player, totalSkillPoints.get(player) + gainedSkillPoints);

        } else if (getPlayerLevel(player) < previousLevel) {
            // Player lost levels (this is probably not normal, handle as needed)
            int lostLevels = previousLevel - getPlayerLevel(player);
            int lostSkillPoints = lostLevels * skillPointsPerLevel;

            // Update player-specific skill points

            totalSkillPoints.put(player, totalSkillPoints.get(player) - lostSkillPoints);

        }
        if(spentSkillPoints.get(player) != null) {
            currentSkillPoints.put(player, totalSkillPoints.get(player) - spentSkillPoints.get(player));
        }else{
            currentSkillPoints.put(player, totalSkillPoints.get(player));
        }
        // Update the previous level for the next call
        setPlayerPreviousLevel(player);
    }

    //LEVELS
    public static int getPlayerPreviousLevel(Player player) {
        return previousLevels.getOrDefault(player, 0);
    }
    public static void setPlayerLevel(Player player, int level){
        currentLevels.put(player, level);
    }
    public static void addPlayer_lvl(Player player, int lvlsAdded){
        currentLevels.put(player, getPlayerLevel(player) + lvlsAdded);
    }
    public static void remPlayer_lvl(Player player, int lvlsRemoved){
        currentLevels.put(player, getPlayerLevel(player) - lvlsRemoved);
    }
    public static int getPlayerLevel(Player player){
        return currentLevels.getOrDefault(player, 1);
    }
    public static void setPlayerPreviousLevel(Player player) {
        previousLevels.put(player, getPlayerLevel(player));
    }


    //EXPERIENCE
    public static int getXP(Player player){
        return xp.get(player);
    }
    public static void setXp(Player player, int Xp){
        xp.put(player, Xp);
    }
    public static void addXp(Player player, int Xp){
        xp.put(player, getXP(player) + Xp);
    }
    public static void remXp(Player player, int Xp){
        xp.put(player, getXP(player) - Xp);
    }
    public static void setTotalXp(Player player, int Xp){
        totalXp.put(player, Xp);
    }
    public static int getXpToNextLvl(Player player){
        return xpToNextLevel.get(player);
    }


    //SKILL POINTS
    public static int getPlayerTotalSkillPoints(Player player) {
        return totalSkillPoints.getOrDefault(player, skillPointsPerLevel);
    }
    public static void setPlayerSpentSkillPoints(Player player, int SpentSkillPoints) {
        spentSkillPoints.put(player, SpentSkillPoints);
    }
    public static int getPlayerSpentSkillPoints(Player player){
        return spentSkillPoints.getOrDefault(player, 0);
    }
    public static void setPlayerCurrentSkillPoints(Player player, int CurrentSkillPoints) {
        currentSkillPoints.put(player, CurrentSkillPoints);
    }
    public static int getPlayerCurrentSkillPoints(Player player){
        return currentSkillPoints.getOrDefault(player, 2);
    }

}
