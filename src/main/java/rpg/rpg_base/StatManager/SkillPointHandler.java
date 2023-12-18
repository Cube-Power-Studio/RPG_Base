package rpg.rpg_base.StatManager;

import org.bukkit.entity.Player;
import rpg.rpg_base.GuiHandlers.GUIManager;
import rpg.rpg_base.RPG_Base;

public class SkillPointHandler {
    public static int SkillPoints;
    public static int LevelMax;
    public static int skillPointsPerLevel;
    public static int level;

    private static RPG_Base rpg_base;
    private static int previousLevel = 0;


    private static GUIManager guiManager;
    private static EnduranceManager enduranceManager;
    public SkillPointHandler (GUIManager guiManager, RPG_Base rpg_base, EnduranceManager enduranceManager){
        SkillPointHandler.rpg_base = rpg_base;
        SkillPointHandler.guiManager = guiManager;
        SkillPointHandler.enduranceManager = enduranceManager;
    }

    public static void UpdateSkillPoints(Player player){
        LevelMax = rpg_base.getConfig().getConfigurationSection("Levels").getInt("LevelMax");
        skillPointsPerLevel = rpg_base.getConfig().getConfigurationSection("Levels").getInt("SkillPointsPerLevel");
    }
    public static void UpdateLevel(){
        updateSkillPointsOnLevelChange();
    }

    public static synchronized void updateSkillPointsOnLevelChange() {
        // Your existing logic remains unchanged
        if (level > previousLevel) {
            // Player leveled up
            SkillPoints += (level - previousLevel) * skillPointsPerLevel;
        } else if (level < previousLevel) {
            // Player lost levels (this is probably not normal, handle as needed)
            SkillPoints -= (previousLevel - level) * skillPointsPerLevel;
        }

        // Update the previous level for the next call
        previousLevel = level;
    }


}
